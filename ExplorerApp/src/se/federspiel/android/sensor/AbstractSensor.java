package se.federspiel.android.sensor;

import java.util.ArrayList;

import se.federspiel.android.sensor.filter.ISensorFilter;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public abstract class AbstractSensor implements ISensor, SensorEventListener
{
	private SensorManager mSensorManager = null;
	
	private ArrayList<ISensorChangedEventListener> mListeners = new ArrayList<ISensorChangedEventListener>();

	private ISensorFilter mFilterChain = null;
	
	private Sensor mSensor = null;
	private long mLastTimestamp = 0;
	private float[] mLastValues = null;
	private int mLastAccuracy = 0;

	private boolean mIsListening = false;
	
	public AbstractSensor(Sensor sensor, Context context)
	{
		mSensor = sensor;

		mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
	}

	public AbstractSensor(AbstractSensor sensor)
	{
		mSensor = sensor.mSensor;
		mSensorManager = sensor.mSensorManager;
	}

	@Override
	public void startSampling(int rate)
	{
		if (!mIsListening)
		{
			mSensorManager.registerListener(this, mSensor, rate);
			
			mIsListening = true;
		}
	}

	@Override
	public void stopSampling()
	{
		if (mIsListening)
		{
			mSensorManager.unregisterListener(this);
			
			mIsListening = false;
		}
	}

	@Override
	public void setOnSensorChangedListener(ISensorChangedEventListener listener, int rate)
	{
		if (mListeners.contains(listener))
		{
			throw new RuntimeException("Listener already registered");
		}

		if (mListeners.size() == 0)
		{
			mListeners.add(listener);

			startSampling(rate);
		}
		else
		{
			mListeners.add(listener);
		}
	}

	@Override
	public void removeOnSensorChangedListener(ISensorChangedEventListener listener)
	{
		if (!mListeners.contains(listener))
		{
			throw new RuntimeException("Listener not registered");
		}

		mListeners.remove(listener);
		
		if (mListeners.size() == 0)
		{
			stopSampling();
		}
	}


	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy)
	{
		for (int i = 0; i < mListeners.size(); i++)
		{
			mListeners.get(i).onAccuracyChanged(this, accuracy);
		}
	}

	@Override
	public void onSensorChanged(SensorEvent event) 
	{
		float[] values = event.values;
				
		if (mFilterChain != null)
		{
			values = mFilterChain.apply(values);
		}
		
		setSensorValues(event.timestamp, values, event.accuracy);
		
		for (int i = 0; i < mListeners.size(); i++)
		{
			mListeners.get(i).onSensorChanged(this);
		}
	}

	public void addFilter(ISensorFilter filter)
	{
		if (mFilterChain == null)
		{
			mFilterChain = filter;
		}
		else
		{
			mFilterChain.connect(filter);
		}
	}

	public static AbstractSensor createSensor(Sensor sensor, Context context)
	{
		AbstractSensor abstractSensor = null;

		switch (sensor.getType())
		{
			case Sensor.TYPE_ACCELEROMETER :
				abstractSensor = new AccelerometerSensor(sensor, context);
				break;
				
			case Sensor.TYPE_AMBIENT_TEMPERATURE :
			case Sensor.TYPE_TEMPERATURE :
				abstractSensor = new AmbientTemperatureSensor(sensor, context);
				break;

			case Sensor.TYPE_GRAVITY :
				abstractSensor = new GravitySensor(sensor, context);
				break;

			case Sensor.TYPE_GYROSCOPE :
			case Sensor.TYPE_GYROSCOPE_UNCALIBRATED:
				abstractSensor = new GyroscopeSensor(sensor, context);
				break;

			case Sensor.TYPE_LIGHT :
				abstractSensor = new LightSensor(sensor, context);
				break;

			case Sensor.TYPE_LINEAR_ACCELERATION :
				abstractSensor = new LinearAccelerationSensor(sensor, context);
				break;

			case Sensor.TYPE_MAGNETIC_FIELD :
			case Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED:
				abstractSensor = new MagneticFieldSensor(sensor, context);
				break;

			case Sensor.TYPE_ORIENTATION :
				abstractSensor = new OrientationSensor(sensor, context);
				break;

			case Sensor.TYPE_PRESSURE :
				abstractSensor = new PressureSensor(sensor, context);
				break;

			case Sensor.TYPE_PROXIMITY :
				abstractSensor = new ProximitySensor(sensor, context);
				break;

			case Sensor.TYPE_RELATIVE_HUMIDITY :
				abstractSensor = new RelativeHumiditySensor(sensor, context);
				break;

			case Sensor.TYPE_ROTATION_VECTOR :
			case Sensor.TYPE_GAME_ROTATION_VECTOR :
				abstractSensor = new RotationVectorSensor(sensor, context);
				break;
				
			case Sensor.TYPE_SIGNIFICANT_MOTION :
				// ;+ 
				break;
				
			default :
				// ;+ throw new RuntimeException("Unsupported sensor type " + sensor.getType());
		}
		
		return abstractSensor;
	}
	
	public static AbstractSensor cloneSensor(ISensor sensor)
	{
		AbstractSensor abstractSensor = null;

		switch (sensor.getType())
		{
			case Sensor.TYPE_ACCELEROMETER :
				abstractSensor = new AccelerometerSensor((AccelerometerSensor) sensor);
				break;
				
			case Sensor.TYPE_AMBIENT_TEMPERATURE :
			case Sensor.TYPE_TEMPERATURE :
				abstractSensor = new AmbientTemperatureSensor((AmbientTemperatureSensor) sensor);
				break;

			case Sensor.TYPE_GRAVITY :
				abstractSensor = new GravitySensor((GravitySensor) sensor);
				break;

			case Sensor.TYPE_GYROSCOPE :
			case Sensor.TYPE_GYROSCOPE_UNCALIBRATED:
				abstractSensor = new GyroscopeSensor((GyroscopeSensor) sensor);
				break;

			case Sensor.TYPE_LIGHT :
				abstractSensor = new LightSensor((LightSensor) sensor);
				break;

			case Sensor.TYPE_LINEAR_ACCELERATION :
				abstractSensor = new LinearAccelerationSensor((LinearAccelerationSensor) sensor);
				break;

			case Sensor.TYPE_MAGNETIC_FIELD :
			case Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED:
				abstractSensor = new MagneticFieldSensor((MagneticFieldSensor) sensor);
				break;

			case Sensor.TYPE_ORIENTATION :
				abstractSensor = new OrientationSensor((OrientationSensor) sensor);
				break;

			case Sensor.TYPE_PRESSURE :
				abstractSensor = new PressureSensor((PressureSensor) sensor);
				break;

			case Sensor.TYPE_PROXIMITY :
				abstractSensor = new ProximitySensor((ProximitySensor) sensor);
				break;

			case Sensor.TYPE_RELATIVE_HUMIDITY :
				abstractSensor = new RelativeHumiditySensor((RelativeHumiditySensor) sensor);
				break;

			case Sensor.TYPE_ROTATION_VECTOR :
			case Sensor.TYPE_GAME_ROTATION_VECTOR :
				abstractSensor = new RotationVectorSensor((RotationVectorSensor) sensor);
				break;
				
			case Sensor.TYPE_SIGNIFICANT_MOTION :
				// ;+ 
				break;
				
			default :
				// ;+ throw new RuntimeException("Unsupported sensor type " + sensor.getType());
		}
		
		return abstractSensor;
	}
	
	@Override
	public float getMaximumRange()
	{
		return mSensor.getMaximumRange();
	}

	@Override
	public int getMinDelay()
	{
		return mSensor.getMinDelay();
	}

	@Override
	public String getName()
	{
		return mSensor.getName();
	}

	@Override
	public float getPower()
	{
		return mSensor.getPower();
	}

	@Override
	public float getResolution()
	{
		return mSensor.getResolution();
	}

	@Override
	public int getType()
	{
		return mSensor.getType();
	}

	@Override
	public String getVendor()
	{
		return mSensor.getVendor();
	}

	@Override
	public int getVersion()
	{
		return mSensor.getVersion();
	}

	@Override
	public abstract String getUnitString();

	@Override
	public float[] getLastRawSensorValues()
	{
		return mLastValues;
	}

	@Override
	public int getLastAccuracy()
	{
		return mLastAccuracy;
	}
	
	@Override
	public long getLastTimestamp()
	{
		return mLastTimestamp;
	}
	
	protected void setSensorValues(long timestamp, float[] values, int accuracy)
	{
		mLastTimestamp = timestamp;
		mLastValues = values;
		mLastAccuracy = accuracy;
	}
}
