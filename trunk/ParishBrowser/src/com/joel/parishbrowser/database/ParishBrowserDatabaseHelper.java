package com.joel.parishbrowser.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ParishBrowserDatabaseHelper extends SQLiteOpenHelper
{
   public static String DatabaseName = "com.joel.parishbrowser.database";
   public static int DatabaseVersion = 8;
   
   public ParishBrowserDatabaseHelper(Context context)
   {
      super(context, DatabaseName, null, DatabaseVersion);
   }

   // Method is called during creation of the database
   @Override
   public void onCreate(SQLiteDatabase database)
   {
      ParishTable.onCreate(database);
      RefreshStateTable.onCreate(database);
   }

   // Method is called during an upgrade of the database,
   // e.g. if you increase the database version
   @Override
   public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion)
   {
      ParishTable.onUpgrade(database, oldVersion, newVersion);
      RefreshStateTable.onUpgrade(database, oldVersion, newVersion);
   }

}