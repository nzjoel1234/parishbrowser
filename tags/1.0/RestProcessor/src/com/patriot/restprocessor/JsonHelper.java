package com.patriot.restprocessor;

import java.sql.Date;

public class JsonHelper
{
	public static Date JsonDateToDate(String jsonDate)
	{
		return new Date(JsonDateToMilliseconds(jsonDate));
	}

    public static long JsonDateToMilliseconds(String jsonDate)
    {
        //  "/Date(1321867151710+0100)/"
        int startIndex = jsonDate.indexOf("(") + 1;
        int endIndex = jsonDate.indexOf(")") - 5;
        String milliseconds = jsonDate.substring(startIndex, endIndex);
        return Long.valueOf(milliseconds);
    }

	public static String JsonStringToString(String jsonString)
	{
		if (jsonString == "null")
		{
			return null;
		}
		else
			return jsonString;
	}
}
