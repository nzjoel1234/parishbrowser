package com.joel.parishbrowser;

import java.util.HashMap;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.joel.parishbrowser.contentprovider.ParishContentProvider;
import com.joel.parishbrowser.contentprovider.RefreshStateContentProvider;
import com.joel.parishbrowser.database.ParishTable;
import com.joel.parishbrowser.database.RefreshStateTable;
import com.joel.parishbrowser.processor.ParishLoaderHelper;

public class MainActivity extends Activity implements OnInfoWindowClickListener, LoaderManager.LoaderCallbacks<Cursor> {

   private final static String PREFERENCE_LONGITUDE = "PREFERENCE_LONGITUDE";
   private final static String PREFERENCE_LATITUDE = "PREFERENCE_LATITUDE";
   private final static String PREFERENCE_ZOOM = "PREFERENCE_ZOOM";
   private final static String PREFERENCE_TILT = "PREFERENCE_TILT";
   private final static String PREFERENCE_BEARING = "PREFERENCE_BEARING";
   
   MapFragment mMapFragment;
   private GoogleMap mMap;
   private CameraPosition savedCameraPosition;
   
   private ParishLoaderHelper helper = new ParishLoaderHelper(this, null);

   private Uri mParishTableRefreshStateUri = ContentUris.withAppendedId(RefreshStateContentProvider.CONTENT_URI, RefreshStateTable.Tables.TABLE_ID_PARISH);

   private final static int LOADER_REFRESH_STATE = 0;
   private final static int LOADER_PARISHES = 1;

   private boolean mRefreshing = false;
   private MenuItem mMenuRefresh;
   private MenuItem mMenuRefreshing;
   
   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);
      
      SharedPreferences prefs = getPreferences(MODE_PRIVATE);
      
      savedCameraPosition = null;
      if (prefs.contains(PREFERENCE_LONGITUDE))
      {
         double longitude = (double)prefs.getFloat(PREFERENCE_LONGITUDE, 0);
         double latitude = (double)prefs.getFloat(PREFERENCE_LATITUDE, 0);
         float zoom = prefs.getFloat(PREFERENCE_ZOOM, 0);
         float tilt = prefs.getFloat(PREFERENCE_TILT, 0);
         float bearing = prefs.getFloat(PREFERENCE_BEARING, 0);

         savedCameraPosition = new CameraPosition(
            new LatLng(latitude, longitude),
            zoom, tilt, bearing);
      }

      setUpMapIfNeeded();

      getLoaderManager().initLoader(LOADER_REFRESH_STATE, null, this);
      getLoaderManager().initLoader(LOADER_PARISHES, null, this);
   }
   
   @Override
   public void onPause() {

      CameraPosition postion = mMap.getCameraPosition();
      
      getPreferences(MODE_PRIVATE)
         .edit()
            .putFloat(PREFERENCE_LONGITUDE, (float) postion.target.longitude)
            .putFloat(PREFERENCE_LATITUDE, (float) postion.target.latitude)
            .putFloat(PREFERENCE_ZOOM, postion.zoom)
            .putFloat(PREFERENCE_TILT, postion.tilt)
            .putFloat(PREFERENCE_BEARING, postion.bearing)
         .commit();
      
      super.onPause();
   }
   
   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
      getMenuInflater().inflate(R.menu.activity_main, menu);

      mMenuRefresh = menu.findItem(R.id.actionbar_item_refresh);
      mMenuRefreshing = menu.findItem(R.id.actionbar_item_refreshing);

      setRefreshActionItemState(mRefreshing);
      return true;
   }
   
   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
      // Handle item selection
      switch (item.getItemId()) {
         case R.menu.activity_main:
            return true;
         case R.id.actionbar_item_refresh:
            helper.Load();
            return true;
         default:
            return super.onOptionsItemSelected(item);
      }
   }
   
   private void setUpMapIfNeeded() {
      // Do a null check to confirm that we have not already instantiated the map.
      
      if (mMapFragment == null)
      {
         mMapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
      }

      if (mMap == null)
      {
         mMap = mMapFragment.getMap();
      }

      // Check if we were successful in obtaining the map.
      if (mMap == null)
      {
         return;
      }
      
      // The Map is verified. It is now safe to manipulate the map.
      mMap.setMyLocationEnabled(true);
      mMap.setOnInfoWindowClickListener(this);
      
      CameraUpdate update;
      
      if (savedCameraPosition != null)
      {
         update = CameraUpdateFactory.newCameraPosition(savedCameraPosition);
      }
      else
      {
         update = CameraUpdateFactory.newLatLng(new LatLng(-43.532247, 172.636401));
      }
      
      MoveCamera(update);
   }
   
   private void MoveCamera(final CameraUpdate update)
   {
      if (update == null)
      {
         return;
      }
      
      MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
      mapFragment.getView().post(new Runnable()
      {
         public void run()
         {
            mMap.moveCamera(update);
         }
      });
   }

   private void setRefreshActionItemState(boolean refreshing)
   {
      mRefreshing = refreshing;

      if (mMenuRefresh != null)
      {
         mMenuRefresh.setVisible(!mRefreshing);
      }
      if (mMenuRefreshing != null)
      {
         mMenuRefreshing.setVisible(mRefreshing);
      }
   }
   
   private HashMap<String, Integer> mParishMarkerLookup;
   
   private void addMarkers(Cursor cursor)
   {
      if (mMap == null)
      {
         return;
      }

      mParishMarkerLookup = new HashMap<String, Integer>();
      mMap.clear();
      
      if (!cursor.moveToFirst())
      {
         Toast.makeText(this, R.string.noParishesLoaded, Toast.LENGTH_LONG).show();
         return;
      }
      
      int idColumn = cursor.getColumnIndex(ParishTable.COLUMN_ID);
      int nameColumn = cursor.getColumnIndex(ParishTable.COLUMN_NAME);
      int longitudeColumn = cursor.getColumnIndex(ParishTable.COLUMN_LONGITUDE);
      int latitudeColumn = cursor.getColumnIndex(ParishTable.COLUMN_LATITUDE);
      
      do
      {
         int id = cursor.getInt(idColumn);
         String name = cursor.getString(nameColumn);
         
         double longitude = cursor.getDouble(longitudeColumn);
         double latitude = cursor.getDouble(latitudeColumn);
         
         LatLng position = new LatLng(latitude, longitude);
         
         String markerId = mMap.addMarker(new MarkerOptions()
            .position(position)
            .title(name))
            .getId();
         
         mParishMarkerLookup.put(markerId, id);
         
      } while(cursor.moveToNext());
   }

   public void onInfoWindowClick(Marker marker)
   {
      Integer id = mParishMarkerLookup.get(marker.getId());
      
      if (id == null)
      {
         return;
      }
      
      Intent intent = new Intent(this, ParishActivity.class);
      intent.putExtra(ParishActivity.Extras.ParishId, id.intValue());
      startActivity(intent);
      return;
   }
   
   private void updateRefreshState(Cursor cursor)
   {
      if (!cursor.moveToFirst())
      {
         return;
      }

      int stateColumn = cursor.getColumnIndex(RefreshStateTable.COLUMN_ROW_STATE);
      int state = cursor.getInt(stateColumn);
      boolean updating = state == RefreshStateTable.RowStates.UPDATING;
      setRefreshActionItemState(updating);
   }
   
   public Loader<Cursor> onCreateLoader(int id, Bundle data)
   {
      switch (id)
      {
      case LOADER_REFRESH_STATE:
         return new CursorLoader(
               this,
               mParishTableRefreshStateUri,
               new String[] { RefreshStateTable.COLUMN_ROW_STATE },
               null, null, null);
         
      case LOADER_PARISHES:
         return new CursorLoader(this, ParishContentProvider.CONTENT_URI,
               new String[]
               {
                  ParishTable.COLUMN_ID,
                  ParishTable.COLUMN_NAME,
                  ParishTable.COLUMN_LONGITUDE,
                  ParishTable.COLUMN_LATITUDE
               },
               null, null, null);
      }

      return null;
   }

   public void onLoadFinished(Loader<Cursor> loader, Cursor cursor)
   {
      switch (loader.getId())
      {
      case LOADER_REFRESH_STATE:
         updateRefreshState(cursor);
         break;
      case LOADER_PARISHES:
         addMarkers(cursor);
         break;
      }
   }

   public void onLoaderReset(Loader<Cursor> loader)
   {
   }
}
