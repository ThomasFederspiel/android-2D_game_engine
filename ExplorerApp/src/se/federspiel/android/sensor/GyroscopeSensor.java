package se.federspiel.android.sensor;

import android.content.Context;
import android.hardware.Sensor;

public class GyroscopeSensor extends AbstractSensor
{
	private static final String UNIT = "[m/s^2]";
	
	public GyroscopeSensor(Sensor sensor, Context context)
	{
		super(sensor, context);
	}

	public GyroscopeSensor(GyroscopeSensor sensor)
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
		return new String[] { "X-axis", "Y-axis", "Z-axis" };
	}

	@Override
	public int getValueCount()
	{
		return 3;
	}
}
