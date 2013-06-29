package com.joel.parishbrowser.processor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingQueue;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;

import com.joel.parishbrowser.contentprovider.ParishContentProvider;
import com.joel.parishbrowser.contentprovider.RefreshStateContentProvider;
import com.joel.parishbrowser.database.RefreshStateTable;
import com.joel.parishbrowser.decoders.Parish;
import com.joel.parishbrowser.decoders.ParishListDecoder;
import com.joel.parishbrowser.dto.ParishDto;
import com.joel.parishbrowser.rest.ParishHtmlRetriever;
import com.patriot.restprocessor.service.IServiceProvider;

public class ParishLoaderProvider implements IServiceProvider
{
   private final Context mContext;
   private final Geocoder mGeocoder;

   public ParishLoaderProvider(Context context)
   {
      mContext = context;
      mGeocoder = new Geocoder(mContext);
   }

   /**
    * Identifier for each provided method. Cannot use 0 as Bundle.getInt(key)
    * returns 0 when the key does not exist.
    */
   public static class Methods
   {
      public static final int LOAD_METHOD = 1;
   }

   public boolean RunTask(int methodId, Bundle extras)
   {
      switch (methodId)
      {
      case Methods.LOAD_METHOD:
         return Load();
      }

      return false;
   }

   private void SetParishesRefreshState(ContentResolver contentResolver,
         boolean refreshing)
   {
      Uri parishesRefreshStateUri = ContentUris.withAppendedId(
            RefreshStateContentProvider.CONTENT_URI,
            RefreshStateTable.Tables.TABLE_ID_PARISH);

      int rowState = refreshing ? RefreshStateTable.RowStates.UPDATING
            : RefreshStateTable.RowStates.STEADY_STATE;

      ContentValues values = new ContentValues();
      values.put(RefreshStateTable.COLUMN_ROW_STATE, rowState);

      contentResolver.update(parishesRefreshStateUri, values, null, null);
   }

   public boolean Load()
   {
      ContentResolver contentResolver = mContext.getContentResolver();

      SetParishesRefreshState(contentResolver, true);
      try
      {
         String listPage = new ParishHtmlRetriever().getParishListPage();
         if (listPage == null)
         {
            return false;
         }

         List<String> parishUrls = ParishListDecoder.ParseParishList(listPage);
         if (parishUrls == null || parishUrls.size() == 0)
         {
            return false;
         }

         final ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();

         operations.add(ContentProviderOperation.newDelete(
               ParishContentProvider.CONTENT_URI).build());

         final ArrayList<AsyncLoadParishTask> loadParishTasks = new ArrayList<AsyncLoadParishTask>();
         final LinkedBlockingQueue<AsyncLoadParishTask> loadParishTaskQueue = new LinkedBlockingQueue<AsyncLoadParishTask>();

         final int maxDownloads = 10;
         int downloadCount = 0;
         for (String parishUrl : parishUrls)
         {
            AsyncLoadParishTask task = new AsyncLoadParishTask(parishUrl)
            {
               @Override
               protected void onPostExecute(ContentProviderOperation result)
               {
                  if (!loadParishTaskQueue.isEmpty())
                  {
                     AsyncLoadParishTask task = loadParishTaskQueue.remove();
                     loadParishTasks.add(task);
                     runTask(task);
                  }

                  if (result != null)
                  {
                     operations.add(result);
                  }
               }
            };

            if (downloadCount < maxDownloads)
            {
               downloadCount++;
               loadParishTasks.add(task);
            }
            else
            {
               loadParishTaskQueue.add(task);
            }
         }

         for (AsyncLoadParishTask task : loadParishTasks)
         {
            runTask(task);
         }

         do
         {
            AsyncLoadParishTask task = loadParishTasks.get(0);
            try
            {
               task.get();
            }
            catch (InterruptedException e)
            {
               e.printStackTrace();
            }
            catch (ExecutionException e)
            {
               e.printStackTrace();
            }
            finally
            {
               loadParishTasks.remove(0);
            }
         } while (!loadParishTasks.isEmpty());

         try
         {
            contentResolver.applyBatch(ParishContentProvider.AUTHORITY,
                  operations);
         } catch (RemoteException e)
         {
            e.printStackTrace();
            return false;
         } catch (OperationApplicationException e)
         {
            e.printStackTrace();
            return false;
         }
         return true;
      }
      finally
      {
         SetParishesRefreshState(contentResolver, false);
      }
   }

   private void runTask(AsyncLoadParishTask task)
   {
      // AsyncTasks are by default only run in serial (depending on the
      // android version)
      // see android documentation for AsyncTask.execute()
      task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[]) null);
   }

   public class AsyncLoadParishTask extends
         AsyncTask<Void, Void, ContentProviderOperation>
   {
      private final String mUrl;

      public String getUrl()
      {
         return mUrl;
      }

      public AsyncLoadParishTask(String url)
      {
         mUrl = url;
      }

      @Override
      protected ContentProviderOperation doInBackground(Void... params)
      {
         String parishPage = new ParishHtmlRetriever().getParishPage(mUrl);
         if (parishPage == null)
         {
            return null;
         }

         Parish parish = ParishListDecoder.ParseParish(mUrl, parishPage);
         if (parish == null)
         {
            return null;
         }

         if (parish.PhysicalAddress == null)
         {
            return null;
         }

         Address parishAddress = getAddress(parish.PhysicalAddress);
         if (parishAddress == null)
         {
            return null;
         }

         return ContentProviderOperation
               .newInsert(ParishContentProvider.CONTENT_URI)
                  .withValues(new ParishDto(
                     parish,
                     parishAddress.getLongitude(),
                     parishAddress.getLatitude())
                  .toContentValues())
               .build();
      }

      private Address getAddress(String addressString)
      {
         try
         {
            List<Address> results = mGeocoder.getFromLocationName(
                  addressString, 1);
            if (results.size() == 0)
            {
               return null;
            }
            return results.get(0);
         }
         catch (IOException e)
         {
            e.printStackTrace();
            return null;
         }
      }
   }
}
