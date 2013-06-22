package com.joel.parishbrowser;

import android.app.Activity;
import android.app.ActionBar;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.joel.parishbrowser.contentprovider.ParishContentProvider;
import com.joel.parishbrowser.database.ParishTable;

public class ParishActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor>
{
   public static class Extras
   {
      public static String ParishId = "parishId";
   }
   
   private final static int LOADER_PARISH = 0; 
   private Uri parishUri;
   
   @Override
   public void onCreate(Bundle savedInstanceState)
   {  
      super.onCreate(savedInstanceState);
      
      setContentView(R.layout.activity_parish);
      
      Intent intent = getIntent();
      int parishId = intent.getIntExtra(Extras.ParishId, -1);
      
      parishUri = ContentUris.withAppendedId(ParishContentProvider.CONTENT_URI, parishId);
      
      getLoaderManager().initLoader(LOADER_PARISH, null, this);
   }
   
   private void underline(TextView textView)
   {
      SpannableString content = new SpannableString(textView.getText());
      content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
      textView.setText(content);
   }
   
   private void setupTextView(Cursor cursor, String columnName, int valueViewId, int labelViewId, OnClickListener listener)
   {
      TextView valueTextView = (TextView) findViewById(valueViewId);
      TextView labelTextView = (TextView) findViewById(labelViewId);
      
      int columnId = cursor.getColumnIndex(columnName);
      String value = cursor.getString(columnId);
      
      if (value == null || value.isEmpty())
      {
         valueTextView.setVisibility(View.GONE);
         labelTextView.setVisibility(View.GONE);
         return;
      }

      valueTextView.setVisibility(View.VISIBLE);
      labelTextView.setVisibility(View.VISIBLE);
         
      valueTextView.setText(value);

      if (listener != null)
      {
         underline(valueTextView);
         valueTextView.setOnClickListener(listener);
      }
   }
   
   private void loadViewFromResult(Cursor result)
   {
      if (!result.moveToFirst())
      {
         return;
      }

      OnClickListener addressOnClickListener = new View.OnClickListener()
      {
         public void onClick(View v)
         {
            TextView text = (TextView)v;
            String uri = "geo:0,0?q=" + text.getText();
            startActivity(new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri)));
         }
      };
      
      OnClickListener telOnClickListener = new View.OnClickListener()
      {
         public void onClick(View v)
         {
            TextView text = (TextView)v;
            String uri = "tel:" + text.getText();
            startActivity(new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri)));
         }
      };

      int nameColumn = result.getColumnIndex(ParishTable.COLUMN_NAME);
      ActionBar actionBar = getActionBar();
      actionBar.setSubtitle(result.getString(nameColumn));
      
      setupTextView(result, 
            ParishTable.COLUMN_PHYSICAL_ADDRESS, 
            R.id.parish_physicalAddress, 
            R.id.parish_physicalAddressLabel, 
            addressOnClickListener);
      
      setupTextView(result, 
            ParishTable.COLUMN_POSTAL_ADDRESS, 
            R.id.parish_postalAddress, 
            R.id.parish_postalAddressLabel, 
            addressOnClickListener);
      
      setupTextView(result, 
            ParishTable.COLUMN_PHONE, 
            R.id.parish_phone, 
            R.id.parish_phoneLabel, 
            telOnClickListener);
      
      setupTextView(result, 
            ParishTable.COLUMN_FAX, 
            R.id.parish_fax, 
            R.id.parish_faxLabel, 
            telOnClickListener);
      
      setupTextView(result, 
            ParishTable.COLUMN_SATURDAY_MASSES, 
            R.id.parish_saturdayMasses, 
            R.id.parish_saturdayMassesLabel, 
            null);
      
      setupTextView(result, 
            ParishTable.COLUMN_SUNDAY_MASSES, 
            R.id.parish_sundayMasses, 
            R.id.parish_sundayMassesLabel, 
            null);
      
      setupTextView(result, 
            ParishTable.COLUMN_WEEKDAY_MASSES, 
            R.id.parish_weekdayMasses, 
            R.id.parish_weekdayMassesLabel, 
            null);
      
      setupTextView(result, 
            ParishTable.COLUMN_OTHER_MASSES, 
            R.id.parish_otherMasses, 
            R.id.parish_otherMassesLabel, 
            null);
      
      setupTextView(result, 
            ParishTable.COLUMN_RECONCILIATION, 
            R.id.parish_reconciliation, 
            R.id.parish_reconciliationLabel, 
            null);
            
      
   }

   public Loader<Cursor> onCreateLoader(int id, Bundle data)
   {
      switch (id)
      {
      case LOADER_PARISH:
         return new CursorLoader(this, parishUri,
               new String[]
               {
                  ParishTable.COLUMN_NAME,
                  ParishTable.COLUMN_PHYSICAL_ADDRESS,
                  ParishTable.COLUMN_POSTAL_ADDRESS,
                  ParishTable.COLUMN_PHONE,
                  ParishTable.COLUMN_FAX,
                  ParishTable.COLUMN_SATURDAY_MASSES,
                  ParishTable.COLUMN_SUNDAY_MASSES,
                  ParishTable.COLUMN_WEEKDAY_MASSES,
                  ParishTable.COLUMN_OTHER_MASSES,
                  ParishTable.COLUMN_RECONCILIATION
               },
               null, null, null);
      }

      return null;
   }

   public void onLoadFinished(Loader<Cursor> loader, Cursor cursor)
   {
      switch (loader.getId())
      {
      case LOADER_PARISH:
         loadViewFromResult(cursor);
         break;
      }
   }

   public void onLoaderReset(Loader<Cursor> loader)
   {
   }
}





