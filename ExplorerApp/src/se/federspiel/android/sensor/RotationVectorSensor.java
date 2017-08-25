package se.federspiel.android.sensor;

import android.content.Context;
import android.hardware.Sensor;

public class RotationVectorSensor extends AbstractSensor
{
	private static final String UNIT = "[]";
	
	public RotationVectorSensor(Sensor sensor, Context context)
	{
		super(sensor, context);
	}

	public RotationVectorSensor(RotationVectorSensor sensor)
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
