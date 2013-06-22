package com.joel.parishbrowser.database;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

public class ParishTable
{
   public static final String TABLE_NAME = "Parish";
   public static final String COLUMN_ID = "id";
   public static final String COLUMN_NAME = "name";
   public static final String COLUMN_PHYSICAL_ADDRESS = "physicalAddress";
   public static final String COLUMN_POSTAL_ADDRESS = "postalAddress";
   public static final String COLUMN_PHONE = "phone";
   public static final String COLUMN_FAX = "fax";
   public static final String COLUMN_SATURDAY_MASSES = "saturdayMasses";
   public static final String COLUMN_SUNDAY_MASSES = "sundayMasses";
   public static final String COLUMN_WEEKDAY_MASSES = "weekdayMasses";
   public static final String COLUMN_OTHER_MASSES = "otherMasses";
   public static final String COLUMN_RECONCILIATION = "reconciliation";
   public static final String COLUMN_LONGITUDE = "longitude";
   public static final String COLUMN_LATITUDE = "latitude";
   public static final String COLUMN_ROW_STATE = "rowState";

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
         + COLUMN_NAME + " TEXT,"
         + COLUMN_PHYSICAL_ADDRESS + " TEXT,"
         + COLUMN_POSTAL_ADDRESS + " TEXT,"
         + COLUMN_PHONE + " TEXT,"
         + COLUMN_FAX + " TEXT,"
         + COLUMN_SATURDAY_MASSES + " TEXT,"
         + COLUMN_SUNDAY_MASSES + " TEXT,"
         + COLUMN_WEEKDAY_MASSES + " TEXT,"
         + COLUMN_OTHER_MASSES + " TEXT,"
         + COLUMN_RECONCILIATION + " TEXT,"
         + COLUMN_LONGITUDE + " REAL,"
         + COLUMN_LATITUDE + " REAL,"
         + COLUMN_ROW_STATE + " INTEGER"
         + ");";

   public static void onCreate(SQLiteDatabase database)
   {
      database.execSQL(DATABASE_CREATE);
   }

   public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion)
   {
      Log.w(ParishTable.class.getName(),
            "Upgrading " + TABLE_NAME + " table from version "
                  + oldVersion + " to " + newVersion
                  + ", which will destroy all old data");

      database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
      onCreate(database);
   }
}
