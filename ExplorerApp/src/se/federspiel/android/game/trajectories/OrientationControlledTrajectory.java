package se.federspiel.android.game.trajectories;

import se.federspiel.android.game.GameTime;
import se.federspiel.android.game.interfaces.IGameContext;
import se.federspiel.android.game.interfaces.ITrajectoryControlledSprite;
import se.federspiel.android.game.trajectories.limits.OutOfBoundsEvent;
import se.federspiel.android.sensor.ISensor;
import se.federspiel.android.sensor.OrientationSensor;
import se.federspiel.android.util.ALog;
import android.hardware.Sensor;

public class OrientationControlledTrajectory extends AbstractTrajectory
{
	public enum OrientationAxis
	{
		X_ONLY,
		Y_ONLY,
		X_AND_Y
	}

	public enum OrientationDirections
	{
		POSITIVE,
		NEGATIVE,
		BOTH
	}

	public enum OrientationMode
	{
		ACCELERATION,
		SPEED
	}

	private static ISensor sSensor = null;

	private int mUseXAxis = 1;
	private int mUseYAxis = 1;

	private OrientationDirections mXDirection = OrientationDirections.BOTH;
	private OrientationDirections mYDirection = OrientationDirections.BOTH;
	
	private OrientationMode mMode = OrientationMode.ACCELERATION;
	
	private int mSpeedGainX = 1;
	private int mSpeedGainY = 1;

	private int mAccelerationGain = 1;
	private float mResistence = 1.0f;
	private float mLimitBounceFactor = 1.0f;
	
	private float mXAccelerationFactor = 1.0f;
	private float mYAccelerationFactor = 1.0f;
	private float mXSpeedFactor = 1.0f;
	private float mYSpeedFactor = 1.0f;
	
    public OrientationControlledTrajectory(IGameContext gameContext)
    {
    	super(gameContext);
    }
    
	public OrientationControlledTrajectory(IGameContext gameContext, ITrajectoryControlledSprite sprite)
    {
    	super(gameContext, sprite);
    }

	public void setLimitBounceFactor(float factor)
	{
		mLimitBounceFactor = factor;
	}
	
	public void setOrientationMode(OrientationMode mode)
	{
		mMode = mode;
		
		updateFactors();
	}
	
	public void setXDirections(OrientationDirections direction)
	{
		mXDirection = direction;
	}
	
	public void setYDirections(OrientationDirections direction)
	{
		mYDirection = direction;
	}

	public void setAxis(OrientationAxis axis)
	{
		switch (axis)
		{
			case X_ONLY :
				mUseXAxis = 1;
				mUseYAxis = 0;
				break;
			
			case Y_ONLY :
				mUseXAxis = 0;
				mUseYAxis = 1;
				break;

			case X_AND_Y :
				mUseXAxis = 1;
				mUseYAxis = 1;
				break;
			
			default:
				assert false;
				break;
		}
		
		updateFactors();
	}
	
	public void setSpeedGain(int gainX, int gainY)
	{
		mSpeedGainX = gainX;
		mSpeedGainY = gainY;

		updateFactors();
	}
	
	public void setAccelerationGain(int gain)
	{
		mAccelerationGain = gain;

		updateFactors();
	}
	
	public void setResistence(int resistence)
	{
		mResistence = (100 - resistence % 100) / 100.0f;
		
		updateFactors();
	}

	@Override
	public void setup()
	{
		setAccelerationMode(AccelerationModeEnum.ACCELERATION_DIRECTIONAL_MODE);

		if (sSensor == null)
		{
			sSensor = mGameContext.getSensorManager().getSensor(Sensor.TYPE_ORIENTATION);
	
			sSensor.startSampling(android.hardware.SensorManager.SENSOR_DELAY_GAME);
		}
		
		setInitialSpeed(0, 0);
	}

	@Override
	public void teardown()
	{
		if (sSensor != null)
		{
			sSensor.stopSampling();
			sSensor = null;
		}
	}
	
	@Override
    public boolean update(GameTime gameTime)
    {
		float[] rawData = sSensor.getLastRawSensorValues();
		
		if (rawData != null)
		{
			float roll = -rawData[OrientationSensor.ROLL_INDEX];
			float pitch = -rawData[OrientationSensor.PITCH_INDEX];
			
			switch (mXDirection)
			{
				case BOTH :
					break;
					
				case NEGATIVE :
					if (roll > 0)
					{
						roll = 0;
					}
					break;
					
				case POSITIVE :
					if (roll < 0)
					{
						roll = 0;
					}
					break;
			}

			switch (mYDirection)
			{
				case BOTH :
					break;
					
				case NEGATIVE :
					if (pitch > 0)
					{
						pitch = 0;
					}
					break;
					
				case POSITIVE :
					if (pitch < 0)
					{
						pitch = 0;
					}
					break;
			}
			
			switch (mMode)
			{
				case ACCELERATION :
					setAcceleration(roll * mXAccelerationFactor, pitch * mYAccelerationFactor);
					break;
					
				case SPEED :
					setSpeed(roll * mXSpeedFactor, pitch * mYSpeedFactor);
					break;
			}
		}

        return updatePosition(gameTime);
    }

	@Override
    protected void onOutOfBoundsX(OutOfBoundsEvent event)
    {
		switch (event.limitReached)
		{
			case BOUNDS_LIMIT_LEFT :
				if (getSpeedX() < 0)
				{
					ALog.debug(this, "onOutOfBoundsX() - speedX(" + Integer.toHexString(hashCode()) + ") = " + getSpeedX()
						+ ", x = " + event.position);
					
					setSpeedX(-getSpeedX() * mLimitBounceFactor);
				}
				break;
				
			case BOUNDS_LIMIT_RIGHT :
				if (getSpeedX() > 0)
				{
					ALog.debug(this, "onOutOfBoundsX() - speedX(" + Integer.toHexString(hashCode()) + ") = " + getSpeedX()
						+ ", x = " + event.position);
					
					setSpeedX(-getSpeedX() * mLimitBounceFactor);
				}
				break;
				
			case BOUNDS_LIMIT_BOTTOM:
			case BOUNDS_LIMIT_TOP:
			default:
				break;
		}
		
		event.adjust = true;
    }
    
	@Override
    protected void onOutOfBoundsY(OutOfBoundsEvent event)
    {
		switch (event.limitReached)
		{
			case BOUNDS_LIMIT_TOP :
				if (getSpeedY() < 0)
				{
					ALog.debug(this, "onOutOfBoundsY() - speedY(" + Integer.toHexString(hashCode()) + ") = " + getSpeedY()
							+ ", y = " + event.position);
					
					setSpeedY(-getSpeedY() * mLimitBounceFactor);
				}
				break;
				
			case BOUNDS_LIMIT_BOTTOM :
				if (getSpeedY() > 0)
				{
					ALog.debug(this, "onOutOfBoundsY() - speedY(" + Integer.toHexString(hashCode()) + ") = " + getSpeedY()
							+ ", y = " + event.position);
					
					setSpeedY(-getSpeedY() * mLimitBounceFactor);
				}
				break;
				
			case BOUNDS_LIMIT_LEFT :
			case BOUNDS_LIMIT_RIGHT :
			default :
				break;
		}
		
		event.adjust = true;
    }
	
	private void updateFactors()
	{
		switch (mMode)
		{
			case ACCELERATION :
				mXAccelerationFactor = mResistence * mAccelerationGain * mUseXAxis;
				mYAccelerationFactor = mResistence * mAccelerationGain * mUseYAxis;
				break;
				
			case SPEED :
				mXSpeedFactor = mResistence * mSpeedGainX * mUseXAxis;
				mYSpeedFactor = mResistence * mSpeedGainY * mUseYAxis;
				break;
		}
	}
}
