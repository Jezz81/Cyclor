package com.lunacy.statistics;

import java.util.ArrayList;

public class BucketDescription {
	private int _bucketId;
	private double _sampleStandardDeviation;
	private double _average;
	private int _numMembers;
	private double _coefficientVariation;
	private char _longShort;
	private int _upCnt;
	private int _downCnt;
	private double _pctUp;
	private double _pctDown;
	ArrayList<Double> _rawData;
	
	public int getBucketId()
	{
		return _bucketId;
	}
	
	public double getSampleStandardDeviation()
	{
		return _sampleStandardDeviation;
	}
	
	public double getAverage()
	{
		return _average;
	}
	
	public int getNumMembers()
	{
		return _numMembers;
	}
	
	public double getCoefficientVariation()
	{
		return _coefficientVariation;
	}
	
	public char getLongShort()
	{
		return _longShort;
	}
	
	public double getPctUp()
	{
		return _pctUp;
	}
	
	public double getPctDown()
	{
		return _pctDown;
	}
	
	public BucketDescription(int bucketId) {
		_bucketId = bucketId;
		_rawData = new ArrayList<Double>();
	}
	
	public void addDatapoint(double dp)
	{
		_rawData.add((Double)dp);
		if(dp>0)
		{
			_upCnt++;
		}else
		{
			_downCnt++;
		}
	}
	
	public void generateStatistics()
	{
		_sampleStandardDeviation = StatisticsHelpers.returnSampleStdev(_rawData);
		_average = StatisticsHelpers.returnAverage(_rawData);
		if(_sampleStandardDeviation>0)
		{
			_coefficientVariation = Math.abs(_average)/_sampleStandardDeviation;
		}
		if(_average>0)
		{
			_longShort = 'L';
		}else
		{
			_longShort = 'S';
		}
		_numMembers = _rawData.size();
		_pctUp = (double)_upCnt/(_upCnt+_downCnt);
		_pctDown = (double)_downCnt/(_upCnt+_downCnt);
		//System.out.println(String.format("avg %3.4f stdev %3.4f bucket ID %d",_average,_sampleStandardDeviation,_bucketId));
	}
	
}
