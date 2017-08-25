package se.federspiel.android.sensor;

import android.content.Context;
import android.hardware.Sensor;

public class LinearAccelerationSensor extends AbstractSensor
{
	private static final String UNIT = "[m/s^2]";
	
	public LinearAccelerationSensor(Sensor sensor, Context context)
	{
		super(sensor, context);
	}

	public LinearAccelerationSensor(LinearAccelerationSensor sensor)
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
