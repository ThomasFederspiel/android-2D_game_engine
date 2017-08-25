package se.federspiel.android.sensor;

import se.federspiel.android.sensor.filter.ISensorFilter;

public interface ISensor
{
	public float getMaximumRange();
	public int getMinDelay();
	public String getName();
	public float getPower();
	public float getResolution();
	public int getType();
	public String getVendor();
	public int getVersion();

	public String[] getValueNames();
	public int getValueCount();

	public String getUnitString();
	public float[] getLastRawSensorValues();
	public long getLastTimestamp();
	public int getLastAccuracy();
	
	public void startSampling(int rate);
	public void stopSampling();
	
	public void setOnSensorChangedListener(ISensorChangedEventListener listener, int rate); 
	public void removeOnSensorChangedListener(ISensorChangedEventListener listener); 
	
	public void addFilter(ISensorFilter filter);
}
