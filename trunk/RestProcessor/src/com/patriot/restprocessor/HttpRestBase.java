package com.patriot.restprocessor;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONStringer;

import android.content.ContentValues;

public abstract class HttpRestBase
{
	protected abstract String getUri();

	protected String executePost(String method)
	{
		return executePost(method, "");
	}
    
    protected String executePost(String method, ContentValues parameters)
    {
        Iterator<String> keyIterator = parameters.keySet().iterator();

        JSONStringer params;
        try
        {
            params = new JSONStringer().object();
                
            while (keyIterator.hasNext())
            {
                String key = keyIterator.next();
                Object value = parameters.get(key);
                params = params.key(key).value(value);
            }
            
            params = params.endObject();
        } 
        catch (JSONException e)
        {
            e.printStackTrace();
            return null;
        }
        
        return executePost(method, params.toString());
    }

	protected String executePost(String method, String urlParameters)
	{
		HttpURLConnection connection = null;
		try
		{
			//Create connection
			URL url = new URL(getUri() + method);
			connection = (HttpURLConnection)url.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type",
					"application/json");

			connection.setRequestProperty("Content-Length", "" +
					Integer.toString(urlParameters.getBytes().length));
			connection.setRequestProperty("Content-Language", "en-US");

			connection.setUseCaches (false);
			connection.setDoInput(true);
			connection.setDoOutput(true);

			//Send request
			DataOutputStream wr = new DataOutputStream (
					connection.getOutputStream ());
			wr.writeBytes (urlParameters);
			wr.flush ();
			wr.close ();

			//Get Response
			InputStream is = connection.getInputStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));
			String line;
			StringBuffer response = new StringBuffer();
			while((line = rd.readLine()) != null)
			{
				response.append(line);
				response.append('\r');
			}
			rd.close();

			return response.toString();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
		finally
		{
			if(connection != null)
			{
				connection.disconnect();
			}
		}
	}

	protected String executeGet(String method)
	{
		return executeGet(method, "");
	}
    
    protected String executeGet(String method, ContentValues parameters)
    {
        Iterator<String> keyIterator = parameters.keySet().iterator();

        StringBuilder paramsBuilder = new StringBuilder();
            
        boolean first = true;
        
        while (keyIterator.hasNext())
        {
            if (first)
            {
                first = false;
            }
            
            String key = keyIterator.next();
            Object value = parameters.get(key);

            paramsBuilder.append(key);
            paramsBuilder.append("=");
            paramsBuilder.append(value);

            if (keyIterator.hasNext())
            {
                paramsBuilder.append("&");
            }
        }
        
        return executeGet(method, paramsBuilder.toString());
    }

	protected String executeGet(String method, String parameters)
	{
		HttpURLConnection connection = null;
		try
		{
			//Create connection
			URL url = new URL(getUri() + method + (parameters.isEmpty() ? "" : "?" + parameters));
			connection = (HttpURLConnection)url.openConnection();

			//Get Response
			InputStream is = connection.getInputStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));
			String line;
			StringBuffer response = new StringBuffer();
			while((line = rd.readLine()) != null)
			{
				response.append(line);
				response.append('\r');
			}
			rd.close();

			return response.toString();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
		finally
		{
			if(connection != null)
			{
				connection.disconnect();
			}
		}
	}
}
