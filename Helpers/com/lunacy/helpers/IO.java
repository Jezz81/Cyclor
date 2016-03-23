package com.lunacy.helpers;
import java.awt.image.ConvolveOp;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
//import java.time.format.DateTimeFormatter;
//import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import org.xml.sax.*;
import org.w3c.dom.*;

import com.lunacy.cycles.Cycle;
import com.lunacy.marketdata.MarketDataPoint;
import com.lunacy.marketdata.MarketDataSeries;

public final class IO {

	public IO() {
		// TODO Auto-generated constructor stub
	}

	public Properties getProperties(String fileName)
	{
		Properties retVal = new Properties();
		InputStream inputStream = null;
		
		try
		{
			inputStream = new FileInputStream(fileName);
			retVal.load(inputStream);
		}catch(Exception e)
		{
			System.out.println(e);
		}finally
		{
			try {
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return retVal;
	}
	/*
	public static java.util.Date getExpectedEffectiveDate(String[] args)
	{
		int i=0;
		while(i<args.length)
		{
			if(args[i].trim().equals("-expectedEffectiveDate"))
			{
				//System.out.println(String.format("expected Date %s", args[i+1]));
				DateFormat formatter = new SimpleDateFormat("yyyyMMdd_HH:mm",Locale.ENGLISH);
				try {
					return formatter.parse(args[i+1]);
				} catch (ParseException e)
				{
					e.printStackTrace();
				}
			}
			i++;
		}
		return null;
	}
	*/
	
	public static int TryGetBucketCount(String[] args)
	{
		for(int i=0;i<args.length;i++)
		{
			if(args[i].equals("-numBuckets"))
			{
				return Integer.parseInt(args[i+1]);
			}
		}
		return -1;
	}
	
	public static ArrayList<Cycle> readCycleDataFiles(String cycleDataDir) throws Exception
	{
		ArrayList<File> cycleDataFiles = new ArrayList<File>();
		ArrayList<Cycle> retVal = new ArrayList<Cycle>();
		
		File cycleDir = new File(cycleDataDir);
		
		File[] fList = cycleDir.listFiles();
		
		for(File f:fList)
		{
			if(f.isFile())
			{
				cycleDataFiles.add(f);
			}else
			{
				throw new Exception(String.format("For the edification of the Cycle Data Directory: Subdirectories are not allowed in %s", cycleDataDir));
			}
		}
		
		for(File cycleDataFile:cycleDataFiles)
		{
			Cycle currCycle = getCycleSeries(cycleDataFile.getCanonicalPath());
			if(currCycle!=null)
			{
				retVal.add(currCycle);
			}else{
				throw new Exception(String.format("No data found in %s",cycleDataFile.getCanonicalFile()));
			}
		}
		return retVal;
	}
	
	
	public static ArrayList<MarketDataSeries> readMarketDataFiles(String marketDataDir,String delim) throws Exception
	{
		ArrayList<File> marketDataFiles = new ArrayList<File>();
		ArrayList<MarketDataSeries> retVal = new ArrayList<MarketDataSeries>();
		
		File posDir = new File(marketDataDir);
		
		File[] fList = posDir.listFiles();
		for(File f:fList)
		{
			if(f.isFile())
			{
				marketDataFiles.add(f);
			}else
			{
				throw new Exception(String.format("For the edification of the Market Data Directory: Subdirectories are not allowed in %s", marketDataDir));
			}
		}
		
		if(marketDataFiles.isEmpty())
		{
			throw new Exception(String.format("No files found in %s", marketDataDir));
		}
		
		for(File mktDataF:fList)
		{
			MarketDataSeries currMktData = getMarketDataSeries(mktDataF.getCanonicalPath(), delim);
			retVal.add(currMktData);
		}
		return retVal;
	}
	
	
	
	private static MarketDataSeries getMarketDataSeries(String marketDataFName,String delim) throws Exception
	{
		String marketIdentifier = Helpers.returnMarketIdentifierFromFilename(marketDataFName);
		MarketDataSeries retVal = new MarketDataSeries(marketIdentifier);
		try
		{
			FileReader fr = new FileReader(marketDataFName);
			BufferedReader marketDataBuff = new BufferedReader(fr);
			String headerLine = marketDataBuff.readLine();
			String dataline;
			String[] splitLine=headerLine.split(delim);
			
			int effectiveDateOrd = Helpers.getOrdinal("EFFECTIVE_DATE", splitLine);
			int openOrd = Helpers.getOrdinal("OPEN", splitLine);
			int hiOrd = Helpers.getOrdinal("HIGH", splitLine);
			int loOrd = Helpers.getOrdinal("LOW", splitLine);
			int clsOrd = Helpers.getOrdinal("CLOSE", splitLine);
			boolean readAllFields;
			
			Date effDate;
			double op,hi,lo,cls;
			MarketDataPoint newMktDataPt;
			
			if(effectiveDateOrd<0)
			{
				marketDataBuff.close();
				throw new IOException(String.format("EFFECTIVE_DATE not found in %s",marketDataFName));
			}
			if(clsOrd<0)
			{
				marketDataBuff.close();
				throw new IOException(String.format("CLOSE not found in %s", marketDataFName));
			}
			if((openOrd<0||hiOrd<0||loOrd<0)&&
					(openOrd>=0||hiOrd>=0||loOrd>=0))
			{
				marketDataBuff.close();
				throw new IOException(String.format("If one of OPEN,HIGH,LOW specified and others are not this is an error. All or none must be spec'd in %s",marketDataFName));
			}
			
			if(openOrd>=0&&hiOrd>=0&&loOrd>=0)
			{
				readAllFields = true;
			}else{
				readAllFields=false;
			}
			
			if(!readAllFields){
				while(null!=(dataline=marketDataBuff.readLine()))
				{
					splitLine = dataline.split(delim);
					if(splitLine.length>1){
						effDate = Helpers.getDate(splitLine, effectiveDateOrd);
						cls = Helpers.getDouble(splitLine, clsOrd);
						newMktDataPt = new MarketDataPoint(effDate, cls);
						retVal.AddDataPoint(newMktDataPt);
					}
				}
			}else
			{
				while(null!=(dataline=marketDataBuff.readLine()))
				{
					splitLine = dataline.split(delim);
					if(splitLine.length>4){
						effDate = Helpers.getDate(splitLine, effectiveDateOrd);
						op = Helpers.getDouble(splitLine, openOrd);
						hi = Helpers.getDouble(splitLine, hiOrd);
						lo = Helpers.getDouble(splitLine, loOrd);
						cls = Helpers.getDouble(splitLine, clsOrd);
						newMktDataPt = new MarketDataPoint(effDate,op,hi,lo, cls);
						retVal.AddDataPoint(newMktDataPt);
					}
				}
			}
			marketDataBuff.close();
		}catch(Exception e)
		{
			e.printStackTrace();
			throw e;
		}
		retVal.SortDataPoints();
		return retVal;
	}
	
	private static Cycle getCycleSeries(String fName) throws Exception
	{
		String cycleId = Helpers.returnCycleIdentifierFromFilename(fName);
		Cycle retVal = new Cycle(cycleId);
		
		try{
			FileReader fr = new FileReader(fName);
			BufferedReader br = new BufferedReader(fr);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm");
			Date currDate;
			String line;
			while(null!=(line=br.readLine()))
			{
				if(!Helpers.stringIsNullOrEmpty(line)){
					currDate = sdf.parse(line.trim());
					retVal.AddDateTimeDemarcation(currDate);
				}
			}
			br.close();
		}catch(Exception e){
			e.printStackTrace();
			throw e;
		}
		retVal.sortDateTimeDemarcations();
		return retVal;
	}
}
