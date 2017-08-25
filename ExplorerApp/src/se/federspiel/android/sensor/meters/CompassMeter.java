package se.federspiel.android.sensor.meters;

import se.federspiel.android.sensor.MagneticFieldSensor;

public class CompassMeter
{
	private MagneticFieldSensor mSensor = null;
	
	public CompassMeter(MagneticFieldSensor sensor)
	{
		mSensor = sensor;
	}
}
