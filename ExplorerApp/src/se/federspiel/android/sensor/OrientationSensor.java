package se.federspiel.android.sensor;

import android.content.Context;
import android.hardware.Sensor;

public class OrientationSensor extends AbstractSensor
{
	public static final int AZIMUTH_INDEX = 0;
	public static final int PITCH_INDEX = 1;
	public static final int ROLL_INDEX = 2;
	
	private static final String UNIT = "[deg]";
	
	public OrientationSensor(Sensor sensor, Context context)
	{
		super(sensor, context);
	}

	public OrientationSensor(OrientationSensor sensor)
	{
		super(sensor);
	}
	
	@Override
	public String getUnitString()
	{
		return UNIT;
	}

	@Override
	public String[] getValueNames()
	{
		return new String[] { "Azimuth", "Pitch", "Roll" };
	}

	@Override
	public int getValueCount()
	{
		return 3;
	}
}
