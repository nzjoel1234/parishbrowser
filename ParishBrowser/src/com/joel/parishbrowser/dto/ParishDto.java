package com.joel.parishbrowser.dto;

import com.joel.parishbrowser.database.ParishTable;
import com.joel.parishbrowser.decoders.Parish;

import android.content.ContentValues;

public class ParishDto
{
   private Parish mParish;
   private double mLongitude;
   private double mLatitude;

   public ParishDto(Parish parish, double longitude, double latitude)
   {
      mParish = parish;
      mLongitude = longitude;
      mLatitude = latitude;
   }

   public ContentValues toContentValues()
   {
      ContentValues values = new ContentValues();

      values.put(ParishTable.COLUMN_ID, mParish.Id);
      values.put(ParishTable.COLUMN_NAME, mParish.Name);
      values.put(ParishTable.COLUMN_PHYSICAL_ADDRESS, mParish.PhysicalAddress);
      values.put(ParishTable.COLUMN_POSTAL_ADDRESS, mParish.PostalAddress);
      values.put(ParishTable.COLUMN_PHONE, mParish.Phone);
      values.put(ParishTable.COLUMN_FAX, mParish.Fax);
      values.put(ParishTable.COLUMN_SATURDAY_MASSES, mParish.SaturdayMasses);
      values.put(ParishTable.COLUMN_SUNDAY_MASSES, mParish.SundayMasses);
      values.put(ParishTable.COLUMN_WEEKDAY_MASSES, mParish.WeekdayMasses);
      values.put(ParishTable.COLUMN_OTHER_MASSES, mParish.OtherMasses);
      values.put(ParishTable.COLUMN_RECONCILIATION, mParish.Reconciliation);
      values.put(ParishTable.COLUMN_LONGITUDE, mLongitude);
      values.put(ParishTable.COLUMN_LATITUDE, mLatitude);

      return values;
   }
}
