package com.lunacy.statistics;

import java.security.InvalidParameterException;
import java.util.ArrayList;

public final class StatisticsHelpers {

	public StatisticsHelpers() {
		// TODO Auto-generated constructor stub
	}

	public static double returnSampleStdev(ArrayList<Double> series)
	{
		if(series==null){
			return 0;
		}
		if(series.size()<2){
			return 0;
		}
		double total=0;
		double avg = returnAverage(series);
		int N = series.size()-1;
		
		for(int i=series.size()-1;i>=0;i--){
			total+=Math.pow((series.get(i)-avg),2);
		}
		
		return Math.pow(total/N,0.5);
	}
	
	public static double returnAverage(ArrayList<Double> series)
	{
		if(series==null){
			return 0;
		}
		if(series.size()<2){
			return 0;
		}
		double total = 0;
		double N = series.size();
		
		for(int i=series.size()-1;i>=0;i--){
			total+=series.get(i);
		}
		return total/N;
	}
}
