package com.joel.parishbrowser.decoders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ParishListDecoder
{
   public static List<String> ParseParishList(String html)
   {
      Document htmlDoc = Jsoup.parse(html);
      
      Element churchList = htmlDoc.getElementById("church_list");
      Elements churches = churchList.getElementsByTag("a");
      
      List<String> parishes = new ArrayList<String>();
      
      for (Element church : churches)
      {
         String link = church.attr("href");
         
         if (link == null || link.trim().length() == 0)
         {
            continue;
         }
         
         parishes.add(link);
      }
      
      return parishes;
   }

   final static Pattern lastIntPattern = Pattern.compile("[^0-9]+([0-9]+)$");
   
   public static Parish ParseParish(String url, String html)
   {
      try
      {
         Document htmlDoc = Jsoup.parse(html);

         Element featuresElement = htmlDoc.getElementById("features");
         
         HashMap<String, String> featuresData = GetDataFromTables(featuresElement);
         if (featuresData == null)
         {
            return null;
         }
         
         Matcher matcher = lastIntPattern.matcher(url);
         if (!matcher.find())
         {
            return null;
         }

         Parish parish = new Parish();
         parish.Id = Integer.parseInt(matcher.group(1));
         parish.Name = featuresElement.getElementsByTag("h2").first().text();
         parish.PhysicalAddress = featuresData.get("Physical Address:");
         parish.PostalAddress = featuresData.get("Postal Address:");
         parish.Phone = featuresData.get("Phone:");
         parish.Fax = featuresData.get("Fax:");
         parish.SaturdayMasses = featuresData.get("Saturday Masses:");
         parish.SundayMasses = featuresData.get("Sunday Masses:");
         parish.OtherMasses = featuresData.get("Other Masses:");
         parish.WeekdayMasses = featuresData.get("Weekday Masses:");
         parish.Reconciliation = featuresData.get("Reconciliation:");
         
         return parish;
      }
      catch (NullPointerException e)
      {
         return null;
      }
   }
   
   private static HashMap<String, String> GetDataFromTables(Element tablesDiv)
   {
      HashMap<String, String> data = new HashMap<String, String>();

      for (Element table : tablesDiv.getElementsByTag("table"))
      {
         Elements elementCollection = table.getElementsByTag("p");
         Element[] elements = elementCollection.toArray(new Element[elementCollection.size()]);
         
         if (elements.length != 2)
            continue;
         
         data.put(elements[0].text(), elements[1].text());
      }
      
      return data;
   }
}












