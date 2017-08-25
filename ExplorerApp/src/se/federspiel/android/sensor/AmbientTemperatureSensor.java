package se.federspiel.android.sensor;

import android.content.Context;
import android.hardware.Sensor;

public class AmbientTemperatureSensor extends AbstractSensor
{
	private static final String UNIT = "[C]";
	
	public AmbientTemperatureSensor(Sensor sensor, Context context)
	{
		super(sensor, context);
	}

	public AmbientTemperatureSensor(AmbientTemperatureSensor sensor)
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
		return new String[] { "Temp" };
	}

	@Override
	public int getValueCount()
	{
		return 1;
	}
}
