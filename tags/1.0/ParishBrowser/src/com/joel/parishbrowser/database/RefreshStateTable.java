package com.joel.parishbrowser.database;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

public class RefreshStateTable
{
   public static final String TABLE_NAME = "RefreshState";
   public static final String COLUMN_ID = "id";
   public static final String COLUMN_ROW_STATE = "rowState";

   public static class Tables
   {
      public static final int TABLE_ID_PARISH = 0;
   }

   public static class RowStates
   {
      public static final int STEADY_STATE = 0;
      public static final int UPDATING = 1;
   }

   // Database creation SQL statement
   private static final String DATABASE_CREATE = "CREATE TABLE "
         + TABLE_NAME
         + " ("
         + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
         + COLUMN_ID + " INTEGER,"
         + COLUMN_ROW_STATE + " INTEGER"
         + ");";
   
   private static final String INSERT_ROWS = "INSERT INTO "
         + TABLE_NAME
         + " ("
         + COLUMN_ID + ", "
         + COLUMN_ROW_STATE
         + ") "
         + "VALUES ("
         + Integer.toString(Tables.TABLE_ID_PARISH) + ", "
         + Integer.toString(RowStates.STEADY_STATE)
         + ");";

   public static void onCreate(SQLiteDatabase database)
   {
      database.beginTransaction();
      
      try
      {
          database.execSQL(DATABASE_CREATE);
          database.execSQL(INSERT_ROWS);
         
          database.setTransactionSuccessful();
       }
       finally
       {
          database.endTransaction();
       }
   }

   public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion)
   {
      database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
      onCreate(database);
   }
}
