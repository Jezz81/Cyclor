package com.lunacy.main;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;

import com.lunacy.cycles.Cycle;
import com.lunacy.helpers.Helpers;
import com.lunacy.helpers.IO;
import com.lunacy.marketdata.MarketDataSeries;
import com.lunacy.statistics.ScaledCycleSeries;

public class Program {
	private final static int DEFAULT_BUCKET_COUNT = 30;
	private final static String delim = ";";
	
	public Program() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		String currentDir = new File(Program.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getParentFile().getPath();
		String marketDataDir =  String.format("%s%sMarketData", currentDir,File.separator);//"/home/l/Lunacy/MarketData";
		String cycleDataDir = String.format("%s%sCycles", currentDir,File.separator);//"/home/l/Lunacy/Cycles";
		String outputDirectory = String.format("%s%sOutput", currentDir,File.separator);//"/home/l/Lunacy/Output";
		new File(outputDirectory).mkdir();
		
		ArrayList<MarketDataSeries> marketSeries = IO.readMarketDataFiles(marketDataDir, delim);
		ArrayList<Cycle> cycleSeries = IO.readCycleDataFiles(cycleDataDir);
		int numBuckets = IO.TryGetBucketCount(args);
		numBuckets=numBuckets< 0?DEFAULT_BUCKET_COUNT:numBuckets;
		for(int i=0;i<marketSeries.size();i++)
		{
			for(int j=0;j<cycleSeries.size();j++)
			{
				ScaledCycleSeries currCycle = new ScaledCycleSeries();
				currCycle.createSeries(marketSeries.get(i), cycleSeries.get(j), numBuckets);
				currCycle.printRawResultsToFile(outputDirectory, delim);
				currCycle.printSummaryResultsToFile(outputDirectory, delim);
			}
		}
	}

}
