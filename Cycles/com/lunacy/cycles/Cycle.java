package com.lunacy.cycles;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import com.lunacy.marketdata.MarketDataPoint;

public class Cycle {
	String _cycleIdentifier;
	ArrayList<Date> _dateTimeDemarcation;
	
	public String getCycleIdentifier()
	{
		return _cycleIdentifier;
	}
	
	public Cycle(String cycleIdentifier) {
		_cycleIdentifier = cycleIdentifier.trim().toUpperCase();
		_dateTimeDemarcation = new ArrayList<>();
	}
	
	public void AddDateTimeDemarcation(Date newDemarcation)
	{
		if(newDemarcation==null)
		{
			throw new InvalidParameterException("DateTime demarcation cannot be null");
		}else
		{
			if(_dateTimeDemarcation.contains(newDemarcation))
			{
				throw new InvalidParameterException(String.format("DateTime demarcation of %s has already been added to %s cannot be null",newDemarcation,_cycleIdentifier));
			}else
			{
				_dateTimeDemarcation.add(newDemarcation);
			}
		}
	}
	
	public ArrayList<Date> returnReadOnlyDateDemarcations()
	{
		//TODO: make... readonly;
		return _dateTimeDemarcation;
	}
	
	public void sortDateTimeDemarcations()
	{
		Collections.sort(_dateTimeDemarcation);
	}
}
