package se.federspiel.android.sensor;

import android.content.Context;
import android.hardware.Sensor;

public class RelativeHumiditySensor extends AbstractSensor
{
	private static final String UNIT = "[%]";
	
	public RelativeHumiditySensor(Sensor sensor, Context context)
	{
		super(sensor, context);
	}

	public RelativeHumiditySensor(RelativeHumiditySensor sensor)
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
		return new String[] { "Humidity" };
	}

	@Override
	public int getValueCount()
	{
		return 1;
	}
}
