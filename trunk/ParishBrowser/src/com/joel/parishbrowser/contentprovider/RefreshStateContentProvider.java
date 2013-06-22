package com.joel.parishbrowser.contentprovider;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

import com.joel.parishbrowser.database.ParishBrowserDatabaseHelper;
import com.joel.parishbrowser.database.RefreshStateTable;
import com.patriot.restprocessor.contentprovider.TableContentProviderBase;

public class RefreshStateContentProvider extends TableContentProviderBase
{
   public static final String AUTHORITY = "com.joel.parishbrowser.refreshstates";

   private static final String BASE_PATH = "refreshstates";

   public static final Uri CONTENT_URI = Uri.parse(
           "content://" + AUTHORITY + "/" + BASE_PATH);

   public static final String CONTENT_TYPE =
           ContentResolver.CURSOR_DIR_BASE_TYPE
           + "/vnd.homein.refreshstates";

   public static final String CONTENT_ITEM_TYPE =
           ContentResolver.CURSOR_ITEM_BASE_TYPE
           + "/vnd.homein.refreshstate";

   @Override
   public boolean onCreate()
   {
       super.initialise(
               new ParishBrowserDatabaseHelper(getContext()),
               AUTHORITY,
               BASE_PATH,
               CONTENT_URI,
               RefreshStateTable.TABLE_NAME,
               RefreshStateTable.COLUMN_ID,
               CONTENT_ITEM_TYPE,
               CONTENT_TYPE);

       return true;
   }

   @Override
   protected String[] getAvailableColumns()
   {
       return new String[] {
               BaseColumns._ID,
               RefreshStateTable.COLUMN_ID,
               RefreshStateTable.COLUMN_ROW_STATE };
   }
}
