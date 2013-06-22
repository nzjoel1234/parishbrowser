package com.joel.parishbrowser.processor;

import android.content.Context;

import com.patriot.restprocessor.service.IProcessorResultReceiver;

public class ParishLoaderHelper extends ParishBrowserServiceHelper
{   
   public ParishLoaderHelper(Context context, IProcessorResultReceiver resultReceiver)
   {
      super(context, ParishBrowserProcessor.Providers.LOADER_PROVIDER, resultReceiver);
   }
   
   public void Load()
   {
      RunMethod(ParishLoaderProvider.Methods.LOAD_METHOD);
   }
}
