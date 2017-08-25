package se.federspiel.android.sensor;

import android.content.Context;
import android.hardware.Sensor;

public class LightSensor extends AbstractSensor
{
	private static final String UNIT = "[lux]";
	
	public LightSensor(Sensor sensor, Context context)
	{
		super(sensor, context);
	}

	public LightSensor(LightSensor sensor)
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
		return new String[] { "Light" };
	}

	@Override
	public int getValueCount()
	{
		return 1;
	}
}
