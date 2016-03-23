package com.lunacy.helpers;

import java.util.ArrayList;
import java.util.Date;
import java.io.File;
import java.security.InvalidParameterException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class Helpers {

	public static int getOrdinal(String searchVal,String[] searchArr)
	{
		int i=0;
		int max = searchArr.length;
		while(i<max)
		{
			if(searchArr[i].trim().toUpperCase().equals(searchVal.trim().toUpperCase()))
			{
				return i;
			}
			i++;
		}
		
		return -1;
	}
	
	public static Date getDate(String[] splitLine,int ord)
	{
		if(ord<splitLine.length&&ord>=0)
		{
			SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd HH:mm",Locale.ENGLISH);
			try {
				return fmt.parse(splitLine[ord].trim());
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}else
		{
			throw new IllegalArgumentException("Invalid ordinal passed...");
		}
		return null;
	}
	
	public static String getString(String[] splitLine,int ord)
	{
		if(ord<splitLine.length&&ord>=0)
		{
			return splitLine[ord].trim();
		}else
		{
			throw new IllegalArgumentException("Invalid ordinal passed...");
		}
	}
	
	public static double getDouble(String[] splitLine,int ord)
	{
		if(ord<splitLine.length&&ord>=0)
		{
			return Double.parseDouble(splitLine[ord]);
		}else
		{
			System.out.print(String.format("Ordinal is %s split linelen is %s", ord,splitLine.length));
			throw new IllegalArgumentException("Invalid ordinal passed...");
		}
	}
	
	public static boolean stringIsNotNullAndNotEmpty(String testVal)
	{
		return testVal!=null&&!testVal.trim().isEmpty();
	}
	
	public static boolean stringIsNullOrEmpty(String testVal)
	{
		if(testVal==null)
		{
			return true;
		}
		
		if(testVal.trim().isEmpty())
		{
			return true;
		}
		
		return false;
	}
	
	public static boolean stringArrayIsNonNullAndNonZeroLen(String[] testArr)
	{
		if(testArr==null)
		{
			return false;
		}
		if(testArr.length==0)
		{
			return false;
		}
		return true;
	}
	
	public static String returnMarketIdentifierFromFilename(String fileName)
	{
		if(stringIsNullOrEmpty(fileName))
		{
			throw new InvalidParameterException(String.format("Filename cannot be empty"));
		}
		File fileInstance = new File(fileName);
		String tmpName = fileInstance.getName();
		String[] splitFName = tmpName.split("\\.");
		return splitFName[0].toUpperCase().trim();
	}
	
	public static String returnCycleIdentifierFromFilename(String fileName)
	{
		if(stringIsNullOrEmpty(fileName))
		{
			throw new InvalidParameterException(String.format("Filename cannot be empty"));
		}
		File fileInstance = new File(fileName);
		String tmpName = fileInstance.getName();
		String[] splitFName = tmpName.split("\\.");
		return splitFName[0].toUpperCase().trim();
	}
}
