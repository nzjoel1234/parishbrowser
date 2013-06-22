package com.joel.parishbrowser.processor;

import com.patriot.restprocessor.service.IServiceProvider;
import com.patriot.restprocessor.service.ProcessorService;

public class ParishBrowserProcessor extends ProcessorService
{
   public static class Providers
   {
      public static final int LOADER_PROVIDER = 1;
   }

   @Override
   protected IServiceProvider GetProvider(int providerId)
   {
      switch(providerId)
      {
         case Providers.LOADER_PROVIDER:
            return new ParishLoaderProvider(this);
      }

      return null;
   }

}
