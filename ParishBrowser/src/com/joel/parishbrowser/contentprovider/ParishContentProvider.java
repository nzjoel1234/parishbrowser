package com.joel.parishbrowser.contentprovider;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

import com.joel.parishbrowser.database.ParishBrowserDatabaseHelper;
import com.joel.parishbrowser.database.ParishTable;
import com.patriot.restprocessor.contentprovider.TableContentProviderBase;

public class ParishContentProvider extends TableContentProviderBase
{
   public static final String AUTHORITY = "com.joel.parishbrowser.parishes";

   private static final String BASE_PATH = "parishes";

   public static final Uri CONTENT_URI = Uri.parse(
           "content://" + AUTHORITY + "/" + BASE_PATH);

   public static final String CONTENT_TYPE =
           ContentResolver.CURSOR_DIR_BASE_TYPE
           + "/vnd.homein.parishes";

   public static final String CONTENT_ITEM_TYPE =
           ContentResolver.CURSOR_ITEM_BASE_TYPE
           + "/vnd.homein.parish";

   @Override
   public boolean onCreate()
   {
       super.initialise(
               new ParishBrowserDatabaseHelper(getContext()),
               AUTHORITY,
               BASE_PATH,
               CONTENT_URI,
               ParishTable.TABLE_NAME,
               ParishTable.COLUMN_ID,
               CONTENT_ITEM_TYPE,
               CONTENT_TYPE);

       return true;
   }

   @Override
   protected String[] getAvailableColumns()
   {
       return new String[] {
               BaseColumns._ID,
               ParishTable.COLUMN_ID,
               ParishTable.COLUMN_NAME,
               ParishTable.COLUMN_PHYSICAL_ADDRESS,
               ParishTable.COLUMN_POSTAL_ADDRESS,
               ParishTable.COLUMN_PHONE,
               ParishTable.COLUMN_FAX,
               ParishTable.COLUMN_SATURDAY_MASSES,
               ParishTable.COLUMN_SUNDAY_MASSES,
               ParishTable.COLUMN_WEEKDAY_MASSES,
               ParishTable.COLUMN_OTHER_MASSES,
               ParishTable.COLUMN_RECONCILIATION,
               ParishTable.COLUMN_LONGITUDE,
               ParishTable.COLUMN_LATITUDE,
               ParishTable.COLUMN_ROW_STATE };
   }
}
