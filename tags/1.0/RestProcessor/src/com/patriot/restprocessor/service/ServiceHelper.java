package com.patriot.restprocessor.service;

import java.util.Calendar;
import java.util.Random;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

/**
 * The service helpers are a facade for starting a task on the ProcessorService.
 * The purpose of the helpers is to give a simple interface to the upper layers to make asynchronous method calls in the service.
 */
public class ServiceHelper
{
	private final Context mcontext;
	private final int mProviderId;
	private final String mResultAction;
	private final Class<?> mServiceClass;
	private final IProcessorResultReceiver mResultReceiver;

    public ServiceHelper(Context context, Class<?> serviceClass, int providerId, IProcessorResultReceiver resultReceiver)
    {
        mcontext = context;
        mProviderId = providerId;
        mServiceClass = serviceClass;
        mResultReceiver = resultReceiver;
        
        // We need a unique ID to register to receive results from async calls
        Calendar c = Calendar.getInstance();
        Random r = new Random();
        r.setSeed(c.get(Calendar.MILLISECOND));
        int i = r.nextInt();
        
        mResultAction =
            "com.patriot.ServiceHelper." + 
            Integer.toString(i) +
            Integer.toString(c.get(Calendar.MINUTE)) +
            Integer.toString(c.get(Calendar.SECOND));
    }

	/**
	 * Starts the specified methodId with no parameters
	 * @param methodId The method to start
	 */
	public void RunMethod(int methodId)
	{
		RunMethod(methodId, null);
	}

	/**
	 * Starts the specified methodId with the parameters given in Bundle
	 * @param methodId The method to start
	 * @param bundle   The parameters to pass to the method
	 */
	public void RunMethod(int methodId, Bundle bundle)
	{
		Intent service = new Intent(mcontext, mServiceClass);

		service.putExtra(ProcessorService.Extras.PROVIDER_EXTRA, mProviderId);
		service.putExtra(ProcessorService.Extras.METHOD_EXTRA, methodId);
		service.putExtra(ProcessorService.Extras.RESULT_ACTION_EXTRA, mResultAction);

		if (bundle != null)
		{
			service.putExtras(bundle);
		}

		mcontext.startService(service);
	}
    
   public void RegisterReceiver(Context context)
   {
       IntentFilter filter = new IntentFilter(mResultAction);
       context.registerReceiver(mBroadcastReceiver, filter);
   }
    
   public void UnregisterReceiver(Context context)
   {
      try
      {
         context.unregisterReceiver(mBroadcastReceiver);
      }
      catch (IllegalArgumentException e)
      {
        // Don't need to do anything here
        // This exception is thrown when the receiver has not been registered.
      }
   }

    /**
     * Receives intents when the methods we call return.
     * This tells us if the method worked or not.
     */
    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            if (mResultReceiver != null)
            {
                Bundle extras = intent.getExtras();
                boolean result = extras.getBoolean(ProcessorService.Extras.RESULT_EXTRA);
                int method = extras.getInt(ProcessorService.Extras.METHOD_EXTRA);
    
                mResultReceiver.onReceiveProccesorResult(method, extras, result);
            }
        }
    };
}
