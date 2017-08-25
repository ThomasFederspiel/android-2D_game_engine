package se.federspiel.android.sensor;

import android.content.Context;
import android.hardware.Sensor;

public class ProximitySensor extends AbstractSensor
{
	private static final String UNIT = "[cm]";
	
	public ProximitySensor(Sensor sensor, Context context)
	{
		super(sensor, context);
	}

	public ProximitySensor(ProximitySensor sensor)
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
		return new String[] { "Distance" };
	}

	@Override
	public int getValueCount()
	{
		return 1;
	}
}
