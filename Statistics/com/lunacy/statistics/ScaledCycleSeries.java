package com.lunacy.statistics;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.lunacy.cycles.Cycle;
import com.lunacy.marketdata.MarketDataPoint;
import com.lunacy.marketdata.MarketDataSeries;

public class ScaledCycleSeries {
	ArrayList<SeriesDataPoint> _scaledSeries;
	ArrayList<BucketDescription> _buckets;
	String _seriesName;
	
	public String getSeriesName()
	{
		return _seriesName;
	}
	
	public ScaledCycleSeries() {
		_scaledSeries = new ArrayList<SeriesDataPoint>();
	}

	public void createSeries(MarketDataSeries mktData,Cycle currentCycle,int numBuckets)
	{
		_seriesName = String.format("%s_%s",mktData.getMarketIdentifier(),currentCycle.getCycleIdentifier());
		List<MarketDataPoint> currentMarketDataSeries = mktData.returnReadOnlyMarketDataPoints();
		List<Date> cycleDemarcations = currentCycle.returnReadOnlyDateDemarcations();
		int max = currentMarketDataSeries.size()-2;//only iterate to second last as need T+1 to be available for the next period return
		double minTimeScale=Double.MAX_VALUE;
		for(int i=0;i<max;i++)
		{
			Date previousDemarcation = returnPreviousDemarcation(cycleDemarcations,currentMarketDataSeries.get(i).getDateTimeStamp());
			Date nextDemarcation = returnNextDemarcation(cycleDemarcations, currentMarketDataSeries.get(i).getDateTimeStamp());
			if(previousDemarcation!=null&&nextDemarcation!=null)
			{
				long nextTime = nextDemarcation.getTime();
				long previousTime = previousDemarcation.getTime();
				long currTime = currentMarketDataSeries.get(i).getDateTimeStamp().getTime();
				double pctProgress = (double)(currTime-previousTime)/(nextTime-previousTime);
				double nextPeriodReturn = returnNextPeriodMarketReturn(currentMarketDataSeries.get(i), currentMarketDataSeries.get(i+1));
				SeriesDataPoint newDatapoint = new SeriesDataPoint(pctProgress, nextPeriodReturn);
				if(pctProgress<minTimeScale)
				{
					minTimeScale = pctProgress;
				}
				_scaledSeries.add(newDatapoint);
			}
		}
		
		setSummaryStatistics(numBuckets);
	}
	
	public void printRawResultsToFile(String outputPathName,String delim) throws IOException
	{
		String fName = String.format("%s%s%s.txt", outputPathName,File.separator,_seriesName);
		FileWriter fw = new FileWriter(fName,false);
		BufferedWriter bw = new BufferedWriter(fw);
		
		bw.write(String.format("CYCLE_PROGRESS%sRETURN\n",delim));
		for(int i=0;i<_scaledSeries.size();i++)
		{
			bw.write(String.format("%3.6f%s%3.6f\n", _scaledSeries.get(i).getCycleProgress(),delim,_scaledSeries.get(i).getNextPeriodReturn()));
		}
		bw.flush();
		bw.close();
	}
	
	public void printSummaryResultsToFile(String outputPathName,String delim) throws IOException
	{
		String fName = String.format("%s%s%s_summary.txt", outputPathName,File.separator,_seriesName);
		FileWriter fw = new FileWriter(fName,false);
		BufferedWriter bw = new BufferedWriter(fw);
		
		bw.write(String.format("BUCKET_NUM%sAVG_RETURN%sSAMPLE_STDEV%sCOEFF_VAR%sNUM_MEMBERS%sLONG_OR_SHORT%sPCT_UP%sPCT_DOWN\n",delim,delim,delim,delim,delim,delim,delim));
		for(int i=0;i<_buckets.size();i++)
		{
			bw.write(String.format("%d%s%3.6f%s%3.6f%s%3.6f%s%d%s%c%s%s%s%s\n",
					_buckets.get(i).getBucketId(),
					delim,
					_buckets.get(i).getAverage(),
					delim,
					_buckets.get(i).getSampleStandardDeviation(),
					delim,
					_buckets.get(i).getCoefficientVariation(),
					delim,
					_buckets.get(i).getNumMembers(),
					delim,
					_buckets.get(i).getLongShort(),
					delim,
					_buckets.get(i).getPctUp(),
					delim,
					_buckets.get(i).getPctDown()));
		}
		bw.flush();
		bw.close();
	}
	
	private void setSummaryStatistics(int numBuckets)
	{
		double cutoffIncrement = 1.0/numBuckets;
		_buckets = new ArrayList<BucketDescription>(numBuckets);
		int max = numBuckets;
		for(int i=0;i<max;i++)
		{
			BucketDescription newBucket = new BucketDescription(i);
			_buckets.add(newBucket);
		}
		
		max = _scaledSeries.size();
		for(int i=0;i<max;i++)
		{
			int currIdx = getBucketIdx(_scaledSeries.get(i).getCycleProgress(), cutoffIncrement);
			_buckets.get(currIdx).addDatapoint(_scaledSeries.get(i).getNextPeriodReturn());
		}
		
		max = _buckets.size();
		for(int i=0;i<max;i++)
		{
			_buckets.get(i).generateStatistics();
		}
	}
	
	private int getBucketIdx(double seriesProgression,double cutoffInc)
	{
		double divisor = seriesProgression/cutoffInc;
		return (int) divisor;
	}
	
	private double returnNextPeriodMarketReturn(MarketDataPoint t,MarketDataPoint tPlusOne)
	{
		return tPlusOne.getClose()/t.getClose()-1;
	}
	
	private Date returnPreviousDemarcation(List<Date> cycleDemarcations,Date currentDate)
	{
		if(currentDate.compareTo(cycleDemarcations.get(0))<0)
		{
			return null;
		}else
		{
			int i=0;
			int max = cycleDemarcations.size()-1;
			while(i<max)
			{
				if(currentDate.compareTo(cycleDemarcations.get(i))<=0){
					return cycleDemarcations.get(i-1);
				}
				i++;
			}
			return null;
		}
	}
	
	private Date returnNextDemarcation(List<Date> cycleDemarcations,Date currentDate)
	{
		if(currentDate.compareTo(cycleDemarcations.get(0))<0)
		{
			return null;
		}else
		{
			int max = cycleDemarcations.size()-2;
			int i=max;
			while(i>0)
			{
				if(currentDate.compareTo(cycleDemarcations.get(i))>=0){
					return cycleDemarcations.get(i+1);
				}
				i--;
			}
			return null;
		}
	}
	
	private class SeriesDataPoint{
		private double _cycleProgress;
		private double _nextPeriodReturn;
		
		public double getCycleProgress()
		{
			return _cycleProgress;
		}
		
		public double getNextPeriodReturn()
		{
			return _nextPeriodReturn;
		}
		
		public SeriesDataPoint(double cycleProgress,double nextPeriodReturn)
		{
			_cycleProgress = cycleProgress;
			_nextPeriodReturn = nextPeriodReturn;
		}
	}
}
