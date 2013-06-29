package com.joel.ParishBrowserTest;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;

import org.junit.Test;

import com.joel.parishbrowser.decoders.Parish;
import com.joel.parishbrowser.decoders.ParishListDecoder;

public class ParishListDecoderTests
{
   @Test
   public void ParseParishList() throws IOException
   {
      String rawHtml = Utilities.readFile("test\\com\\joel\\ParishBrowserTest\\ParishList.html");
      
      List<String> parishes = ParishListDecoder.ParseParishList(rawHtml);
      assertSame(81, parishes.size());
      assertTrue(parishes.contains("/?sid=8&do=detail&type=parish&id=3159"));
   }

   @Test
   public void ParseParish() throws IOException
   {
      String rawHtml = Utilities.readFile("test\\com\\joel\\ParishBrowserTest\\ctkParish.html");
      
      Parish parish = ParishListDecoder.ParseParish("some//url123", rawHtml);
      assertSame(123, parish.Id);
      assertTrue("03-358 2611".equals(parish.Phone));
      assertTrue("03-358 4190".equals(parish.Fax));
      assertTrue("6pm".equals(parish.SaturdayMasses));
      assertTrue("9am, 11am".equals(parish.SundayMasses));
      assertTrue("90 Greers Road Burnside Christchurch 8053 New Zealand".equals(parish.PhysicalAddress));
   }
}
