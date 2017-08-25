package se.federspiel.android.sensor;

import android.content.Context;
import android.hardware.Sensor;

public class PressureSensor extends AbstractSensor
{
	private static final String UNIT = "[mBar]";
	
	public PressureSensor(Sensor sensor, Context context)
	{
		super(sensor, context);
	}

	public PressureSensor(PressureSensor sensor)
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
		return new String[] { "Pressure" };
	}

	@Override
	public int getValueCount()
	{
		return 1;
	}
}
