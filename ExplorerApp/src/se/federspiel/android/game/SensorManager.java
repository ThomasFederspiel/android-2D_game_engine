package se.federspiel.android.game;

import android.content.Context;
import android.hardware.Sensor;
import se.federspiel.android.game.interfaces.ISensorManager;
import se.federspiel.android.sensor.AbstractSensor;
import se.federspiel.android.sensor.ISensor;

public class SensorManager implements ISensorManager
{
	private Context mContext = null;
	
	public SensorManager(Context context)
	{
		mContext = context;
	}

	@Override
	public ISensor getSensor(int type)
	{
		android.hardware.SensorManager manager = 
				(android.hardware.SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
        
		Sensor sensor = manager.getDefaultSensor(type);
		
		return AbstractSensor.createSensor(sensor, mContext);
	}
}
