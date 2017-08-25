package se.federspiel.android.game.trajectories;

import se.federspiel.android.game.GameTime;
import se.federspiel.android.game.geometry.Point;
import se.federspiel.android.game.geometry.Ray;
import se.federspiel.android.game.geometry.Vector2;
import se.federspiel.android.game.interfaces.IGameContext;
import se.federspiel.android.game.interfaces.ITrajectory;
import se.federspiel.android.game.interfaces.ITrajectoryControlledSprite;
import se.federspiel.android.game.interfaces.ITrajectoryLimits;

public class StationaryTrajectory implements ITrajectory
{
	private ITrajectoryControlledSprite mControllerObject = null;
    private IGameContext mGameContext = null;
    
    private Point mPosition = Point.Zero.clone();
    private Vector2 mZero = Vector2.Zero.clone();

    private Ray mMovementRay = Ray.Zero.clone();

	private Point mTmpPoint = Point.Zero.clone();
	
    public StationaryTrajectory(IGameContext gameContext)
    {
    	assert gameContext != null;
    	
    	mGameContext = gameContext;
    }
    
    public StationaryTrajectory(IGameContext gameContext, ITrajectoryControlledSprite controlledObject)
    {
    	assert controlledObject != null;
    	
    	mControllerObject = controlledObject;
    }
    
	@Override
	public void setInitialPosition(Point point)
	{
		setPosition(point);
	}
	
	@Override
	public void setInitialPositionX(float x)
	{
		setPositionX(x);
	}

	@Override
	public void setInitialPositionY(float y)
	{
		setPositionY(y);
	}
	
	@Override
	public void setInitialSpeed(Vector2 vect)
	{
	}

	@Override
	public void setInitialSpeed(float x, float y)
	{
	}

	@Override
	public void setInitialAcceleration(Vector2 vect)
	{
	}

    public void setInitialAcceleration(float accX, float accY)
	{
	}
    
	@Override
	public void setAccelerationMode(AccelerationModeEnum mode)
	{
	}

	@Override
	public Point getPosition()
	{
		return mPosition;
	}

	@Override
	public void setPosition(Point position)
	{
        updateControlledObjectsPosition(mPosition, position);
        
		mPosition.set(position);
	}


	@Override
	public void setPositionX(float x)
	{
		mTmpPoint.set(mPosition);
		mTmpPoint.X = x;
		
		updateControlledObjectsPosition(mPosition, mTmpPoint);
        
		mPosition.set(mTmpPoint);
	}

	@Override
	public void setPositionY(float y)
	{
		mTmpPoint.set(mPosition);
		mTmpPoint.Y = y;
		
		updateControlledObjectsPosition(mPosition, mTmpPoint);
        
		mPosition.set(mTmpPoint);
	}
	
	@Override
	public Vector2 getSpeed()
	{
		return mZero;
	}

	@Override
	public float getSpeedX()
	{
		return 0;
	}

	@Override
	public float getSpeedY()
	{
		return 0;
	}

	@Override
	public void setSpeed(Vector2 vect)
	{
	}

	@Override
	public void setSpeed(float x, float y)
	{
	}

	@Override
	public void setSpeedX(float x)
	{
	}

	@Override
	public void setSpeedY(float y)
	{
	}

	@Override
	public Vector2 getAcceleration()
	{
		return mZero;
	}

	@Override
	public void setAcceleration(Vector2 vect)
	{
	}

	@Override
	public void setAcceleration(float x, float y)
	{
	}
	

	@Override
	public void setAccelerationX(float x)
	{
	}

	@Override
	public void setAccelerationY(float y)
	{
	}
	
	@Override
	public ITrajectoryLimits getPositionLimits()
	{
		return null;
	}
	
	@Override
	public void setPositionLimits(ITrajectoryLimits limits)
	{
	}

	@Override
	public Ray getMovementRay()
	{
		mMovementRay.setStartPosition(mPosition); 
		
		return mMovementRay;
	}

	@Override
    public Vector2 getMovementSpeed()
    {
		return mZero;
    }
	

	@Override
	public void addForce(Vector2 force)
	{
	}
	
	@Override
	public boolean isStationary()
	{
		return true;
	}
	
	@Override
	public boolean isMoving()
	{
		return false;
	}

	@Override
	public boolean update(GameTime gameTime)
	{
		return false;
	}

	@Override
	public void setup()
	{
	}

	@Override
	public void teardown()
	{
	}

	@Override
	public void setIntegrationMethod(IntegrationMethod method) 
	{
	}
	
	@Override
    public IntegrationMethod getIntegrationMethod()
    {
    	return IntegrationMethod.INTEGRATION_METHOD_EULER;
    }
    
	@Override
	public void setPosition(float x, float y)
	{
	}
	
	protected void updateControlledObjectsPosition(Point oldPosition, Point newPosition)
	{
		mControllerObject.onPositionChanged(oldPosition, newPosition);
	}
}
