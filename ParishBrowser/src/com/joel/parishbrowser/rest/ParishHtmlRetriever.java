package com.joel.parishbrowser.rest;

import android.content.ContentValues;

import com.patriot.restprocessor.HttpRestBase;

public class ParishHtmlRetriever extends HttpRestBase
{
   @Override
   protected String getUri()
   {
      return "http://www.chch.catholic.org.nz/";
   }

   public String getParishListPage()
   {
      ContentValues cv = new ContentValues();
      cv.put("sid", 8);
      
      return executeGet("", cv);
   }

   public String getParishPage(String url)
   {
      return executeGet("", url.replace('/', '\0').replace('?', '\0').trim());
   }
}
