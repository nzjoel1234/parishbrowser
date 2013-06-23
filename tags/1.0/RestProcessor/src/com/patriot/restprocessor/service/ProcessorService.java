package com.patriot.restprocessor.service;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;

/**
 * This service is for making asynchronous method calls on providers.
 * The fact that this is a service means the method calls
 * will continue to run even when the calling activity is killed.
 */
public abstract class ProcessorService extends Service
{
	private Integer lastStartId;
	private final Context mContext = this;

	/**
	 * The keys to be used for the required actions to start this service.
	 */
	public static class Extras
	{
		/**
		 * The provider which the called method is on.
		 */
		public static final String PROVIDER_EXTRA = "PROVIDER_EXTRA";

		/**
		 * The method to call.
		 */
		public static final String METHOD_EXTRA = "METHOD_EXTRA";

		/**
		 * The action to used for the result intent.
		 */
		public static final String RESULT_ACTION_EXTRA = "RESULT_ACTION_EXTRA";

		/**
		 * The extra used in the result intent to return the result.
		 */
		public static final String RESULT_EXTRA = "RESULT_EXTRA";
		
		/**
		 * The extra used in the result intent to return the error message.
		 */
		public static final String RESULT_ERROR = "RESULT_ERROR";
	}

	private final HashMap<String, AsyncServiceTask> mTasks = new HashMap<String, AsyncServiceTask>();

	protected abstract IServiceProvider GetProvider(int providerId);

	/**
	 * Builds a string identifier for this method call.
	 * The identifier will contain data about:
	 *   What processor was the method called on
	 *   What method was called
	 *   What parameters were passed
	 * This should be enough data to identify a task to detect if a similar task is already running.
	 */
	private String getTaskIdentifier(Bundle extras)
	{
		String[] keys = extras.keySet().toArray(new String[0]);
		java.util.Arrays.sort(keys);
		StringBuilder identifier = new StringBuilder();

		for (int keyIndex = 0; keyIndex < keys.length; keyIndex++)
		{
			String key = keys[keyIndex];

			// The result action may be different for each call.
			if (key.equals(Extras.RESULT_ACTION_EXTRA))
			{
				continue;
			}

			identifier.append("{");
			identifier.append(key);
			identifier.append(":");
			identifier.append(extras.get(key).toString());
			identifier.append("}");
		}

		return identifier.toString();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		// This must be synchronised so that service is not stopped while a new task is being added.
		synchronized (mTasks)
		{
			// stopSelf will be called later and if a new task is being added we do not want to stop the service.
			lastStartId = startId;
			
			if (intent != null)
			{
   			Bundle extras = intent.getExtras();
   
   			String taskIdentifier = getTaskIdentifier(extras);
   
   			// If a similar task is already running then lets use that task.
   			AsyncServiceTask task = mTasks.get(taskIdentifier);
   
   			if (task == null)
   			{
   				task = new AsyncServiceTask(taskIdentifier, extras);
   
   				mTasks.put(taskIdentifier, task);
   
   				// AsyncTasks are by default only run in serial (depending on the android version)
   				// see android documentation for AsyncTask.execute()
   				task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[]) null);
   			}
   
   			// Add this Result Action to the task so that the calling activity can be notified when the task is complete.
   			String resultAction = extras.getString(Extras.RESULT_ACTION_EXTRA);
   			if (resultAction != "")
   			{
   				task.addResultAction(extras.getString(Extras.RESULT_ACTION_EXTRA));
   			}
			}
		}

		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent)
	{
		return null;
	}

	public class AsyncServiceTask extends AsyncTask<Void, Void, Boolean>
	{
		private final Bundle mExtras;
		private final ArrayList<String> mResultActions = new ArrayList<String>();

		private final String mTaskIdentifier;

		/**
		 * Constructor for AsyncServiceTask
		 * 
		 * @param taskIdentifier A string which describes the method being called.
		 * @param extras         The Extras from the Intent which was used to start this method call.
		 */
		public AsyncServiceTask(String taskIdentifier, Bundle extras)
		{
			mTaskIdentifier = taskIdentifier;
			mExtras = extras;
		}

		public void addResultAction(String resultAction)
		{
			if (!mResultActions.contains(resultAction))
			{
				mResultActions.add(resultAction);
			}
		}

		@Override
		protected Boolean doInBackground(Void... params)
		{
			Boolean result = false;
			final int providerId = mExtras.getInt(Extras.PROVIDER_EXTRA);
			final int methodId = mExtras.getInt(Extras.METHOD_EXTRA);

			if (providerId != 0 && methodId != 0)
			{
				final IServiceProvider provider = GetProvider(providerId);

				if (provider != null)
				{
					try
					{
						result = provider.RunTask(methodId, mExtras);
					} catch (Exception e)
					{
						mExtras.putString(Extras.RESULT_ERROR, e.toString());
						result = false;
					}
				}

			}
			return result;
		}

		@Override
		protected void onPostExecute(Boolean result)
		{
			// This must be synchronised so that service is not stopped while a new task is being added.
			synchronized (mTasks)
			{
				// Notify the caller(s) that the method has finished executing
				for (int i = 0; i < mResultActions.size(); i++)
				{
					Intent resultIntent = new Intent(mResultActions.get(i));

					resultIntent.putExtra(Extras.RESULT_EXTRA, result.booleanValue());
					resultIntent.putExtras(mExtras);

					LocalBroadcastManager.getInstance(mContext).sendBroadcast(resultIntent);
				}

				// The task is complete so remove it from the running tasks list
				mTasks.remove(mTaskIdentifier);

				// If there are no other executing methods then stop the service
				if (mTasks.size() < 1)
				{
					stopSelf(lastStartId);
				}
			}
		}
	}
}


























