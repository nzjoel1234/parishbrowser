package com.joel.parishbrowser.processor;

import android.content.Context;

import com.patriot.restprocessor.service.IProcessorResultReceiver;
import com.patriot.restprocessor.service.ServiceHelper;

public abstract class ParishBrowserServiceHelper extends ServiceHelper
{
   public ParishBrowserServiceHelper(Context context, int providerId, IProcessorResultReceiver resultReceiver)
   {
      super(context, ParishBrowserProcessor.class, providerId, resultReceiver);
   }
}