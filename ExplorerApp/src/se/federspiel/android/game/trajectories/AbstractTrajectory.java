package se.federspiel.android.game.trajectories;

import se.federspiel.android.game.GameTime;
import se.federspiel.android.game.geometry.Point;
import se.federspiel.android.game.geometry.Ray;
import se.federspiel.android.game.geometry.Vector2;
import se.federspiel.android.game.interfaces.IBounds;
import se.federspiel.android.game.interfaces.IConstraint;
import se.federspiel.android.game.interfaces.IGameContext;
import se.federspiel.android.game.interfaces.ITrajectory;
import se.federspiel.android.game.interfaces.ITrajectoryControlledSprite;
import se.federspiel.android.game.interfaces.ITrajectoryLimits;
import se.federspiel.android.game.trajectories.limits.OutOfBoundsEvent;
import se.federspiel.android.game.trajectories.limits.OutOfBoundsEvent.OutOfBoundsLimit;
import se.federspiel.android.game.utils.AMath;

public abstract class AbstractTrajectory implements ITrajectory
{
	private AccelerationModeEnum mAccelerationMode = AccelerationModeEnum.ACCELERATION_ABSOLUTE_MODE;
    
    private IntegrationMethod mIntegrationMethod = IntegrationMethod.INTEGRATION_METHOD_EULER;
    
    private SpriteTrajectoryConstraint mSpriteTrajectoryConstraint = null;

	protected ITrajectoryControlledSprite mControllerObject = null;
    protected IGameContext mGameContext = null;

    protected Ray mMovementRay = Ray.Zero.clone();
    
	protected Point mPosition = Point.Zero.clone();

	// px/s
	protected Vector2 mSpeed = Vector2.Zero.clone();
	
	// px/s^2, The current acceleration force + base acceleration
	protected Vector2 mAcceleration = Vector2.Zero.clone();

	// px/s^2,
	protected Vector2 mBaseAcceleration = Vector2.Zero.clone();

	protected Vector2 mForce = Vector2.Zero.clone();

	protected Point mPreUpdatePosition = Point.Zero.clone();
    protected Vector2 mPreUpdateSpeed = Vector2.Zero.clone();

	private OutOfBoundsEvent mOutOfBoundsEvent = new OutOfBoundsEvent();

	private Point mTmpPoint = Point.Zero.clone();
	
    public AbstractTrajectory(IGameContext gameContext)
    {
    	assert gameContext != null;
    	
    	mGameContext = gameContext;
    	
    	mSpriteTrajectoryConstraint = new SpriteTrajectoryConstraint(gameContext, this);
    }
    
    public AbstractTrajectory(IGameContext gameContext, ITrajectoryControlledSprite controlledObject)
    {
    	this(gameContext);

    	assert controlledObject != null;
    	
    	mControllerObject = controlledObject;
    }

	@Override
	public void setInitialPosition(Point point)
	{
        setPosition(point);
        mPreUpdatePosition.set(point);
        
		doMethodUpdate();
	}

    @Override
	public void setInitialPositionX(float x)
	{
        setPositionX(x);
        mPreUpdatePosition.X = x;
        
		doMethodUpdate();
	}

	@Override
	public void setInitialPositionY(float y)
	{
        setPositionY(y);
        mPreUpdatePosition.Y = y;
        
		doMethodUpdate();
	}

	/**
	 * px/s
	 */
	@Override
    public void setInitialSpeed(Vector2 speed)
    {
		setInitialSpeed(speed.X, speed.Y);
    }

	/**
	 * px/s
	 */
	@Override
    public void setInitialSpeed(float x, float y)
    {
		mSpeed.set(x, y);
		mPreUpdateSpeed.set(x, y);
        
		doMethodUpdate();
    }
	
	@Override
    public void setInitialAcceleration(Vector2 acceleration)
	{
		setInitialAcceleration(acceleration.X, acceleration.Y);
    }
	
	@Override
    public void setInitialAcceleration(float accX, float accY)
	{
	 	mBaseAcceleration.set(accX, accY);
        mAcceleration.set(mBaseAcceleration);
	 	
		doMethodUpdate();
	}

	@Override
    public void setAccelerationMode(AccelerationModeEnum mode)
	{
		mAccelerationMode = mode;
	}
    
	@Override
    public void setIntegrationMethod(IntegrationMethod method)
	{
		mIntegrationMethod = method;
		
		doMethodUpdate();
	}
	
	@Override
    public IntegrationMethod getIntegrationMethod()
    {
    	return mIntegrationMethod;
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
	public void setPosition(float x, float y)
	{
		mTmpPoint.set(x, y);
		
		updateControlledObjectsPosition(mPosition, mTmpPoint);
        
		mPosition.set(mTmpPoint);
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
        return mSpeed;
    }

	@Override
	public float getSpeedX()
	{
		return mSpeed.X;
	}

	@Override
	public float getSpeedY()
	{
		return mSpeed.Y;
	}

	@Override
    public void setSpeed(Vector2 speed)
    {
		mSpeed.set(speed);
    }

	@Override
	public void setSpeed(float x, float y)
	{
		mSpeed.set(x, y);
	}

	@Override
	public void setSpeedX(float x)
	{
		mSpeed.set(x, mSpeed.Y);
	}

	@Override
	public void setSpeedY(float y)
	{
		mSpeed.set(mSpeed.X, y);
	}

	@Override
    public Vector2 getAcceleration()
	{
		return mAcceleration;
	}

	@Override
    public void setAcceleration(Vector2 acceleration)
	{
		setAcceleration(acceleration.X, acceleration.Y);
	}

	@Override
    public void setAcceleration(float x, float y)
	{
		mBaseAcceleration.set(x, y);
		mAcceleration.set(mBaseAcceleration);
	}
    
	@Override
    public void setAccelerationX(float x)
	{
		mBaseAcceleration.X = x;
		mAcceleration.set(mBaseAcceleration);
	}

	@Override
    public void setAccelerationY(float y)
	{
		mBaseAcceleration.Y = y;
		mAcceleration.set(mBaseAcceleration);
	}

	@Override
    public Ray getMovementRay()
    {
    	return mMovementRay.set(mPreUpdatePosition, 
    			AMath.subtract(mPosition, mPreUpdatePosition, mMovementRay.getVector())); 
    }

	@Override
    public Vector2 getMovementSpeed()
    {
		return mPreUpdateSpeed; 
    }

    @Override
	public void addForce(Vector2 force)
	{
    	mForce.add(force);
	}

	@Override
	public boolean isStationary()
	{
		return ((Math.abs(mPreUpdateSpeed.X) < AMath.EPS) 
				&& (Math.abs(mPreUpdateSpeed.Y) < AMath.EPS));
	}
	
	@Override
	public boolean isMoving()
	{
		return !isStationary();
	}

	@Override
    public ITrajectoryLimits getPositionLimits()
    {
		return mSpriteTrajectoryConstraint.getPositionLimits();
    }
	
	@Override
    public void setPositionLimits(ITrajectoryLimits limits)
    {
		mSpriteTrajectoryConstraint.setPositionLimits(limits);
    }

	@Override
	public void setup()
	{
		mSpriteTrajectoryConstraint.setup();
	}

	@Override
	public void teardown()
	{
		mSpriteTrajectoryConstraint.teardown();
	}

    protected void reverseX()
    {
    	mSpeed.X = -mSpeed.X;
    }
    
    protected void reverseY()
    {
    	mSpeed.Y = -mSpeed.Y;
    }

    protected void onOutOfBoundsX(OutOfBoundsEvent event)
    {
    }
    
    protected void onOutOfBoundsY(OutOfBoundsEvent event)
    {
    }

    protected boolean updatePosition(GameTime gameTime)
    {
    	switch (mIntegrationMethod)
    	{
	    	case INTEGRATION_METHOD_EULER :
	            return eulerIntegration(gameTime);
	    		
	    	case INTEGRATION_METHOD_SYMPLECTIC_EULER :
	            return symplecticEulerIntegration(gameTime);
	    		
	    	case INTEGRATION_METHOD_POSITION_VERLET :
	            return positionVerletIntegration(gameTime);
    	}
    	
    	return false;
	}

    /**
     * x(i+1) = x(i) + v(i) * dt(i)
	 * v(i+1) = v(i) + a(i) * dt(i)
     */
    private boolean eulerIntegration(GameTime gameTime)
    {
    	boolean posUpdated = false;

    	Point position = mPosition;
    	Vector2 speed = mSpeed;
    	Vector2 acceleration = mAcceleration;

    	mPreUpdatePosition.set(mPosition);
    	mPreUpdateSpeed.set(mSpeed);
    	
    	float timeFactor = gameTime.getElapsedTime() / 1E9F; 
    	
    	if (speed.X != 0)
    	{
		    position.X +=
		        speed.X * timeFactor;
		    
		    posUpdated = true;
    	}
    	
    	if (acceleration.X != 0)
    	{
		    speed.X += getAccelerationSign(speed.X) * acceleration.X * timeFactor;
    	}
    	
    	if (mForce.X != 0)
    	{
			acceleration.X = mForce.X * mControllerObject.getPhysicalProperties().getInvMass() + mBaseAcceleration.X;

    		mForce.X = 0;
    	}
	    
    	if (speed.Y != 0)
    	{
		    position.Y +=
		        speed.Y * timeFactor;

		    posUpdated = true;
    	}

    	if (acceleration.Y != 0)
    	{
		    speed.Y += getAccelerationSign(speed.Y) * acceleration.Y * timeFactor;
    	}
    	
    	if (mForce.Y != 0)
    	{
			acceleration.Y = mForce.Y * mControllerObject.getPhysicalProperties().getInvMass() + mBaseAcceleration.Y;
    		
    		mForce.Y = 0;
    	}
    	
    	if (posUpdated)
    	{
    		updateControlledObjectsPosition(mPreUpdatePosition, position);
    	}

    	return posUpdated;
	}
    
    /**
	 *	v(i+1) = v(i) + a(i) * dt(i)
	 *	x(i+1) = x(i) + v(i+1) * dt(i)
     */
    private boolean symplecticEulerIntegration(GameTime gameTime)
    {
    	boolean posUpdated = false;

    	Point position = mPosition;
    	Vector2 speed = mSpeed;
    	Vector2 acceleration = mAcceleration;

    	mPreUpdatePosition.set(mPosition);
    	mPreUpdateSpeed.set(mSpeed);
    	
    	float timeFactor = gameTime.getElapsedTime() / 1E9F; 
    	
    	if (mForce.X != 0)
    	{
			acceleration.X = mForce.X * mControllerObject.getPhysicalProperties().getInvMass() + mBaseAcceleration.X;
    		
    		mForce.X = 0;
    	}

    	if (acceleration.X != 0)
    	{
		    speed.X += getAccelerationSign(speed.X) * acceleration.X * timeFactor;
    	}
	    
    	if (speed.X != 0)
    	{
		    position.X += speed.X * timeFactor;
		    
		    posUpdated = true;
    	}
    	
    	if (mForce.Y != 0)
    	{
			acceleration.Y = mForce.Y * mControllerObject.getPhysicalProperties().getInvMass() + mBaseAcceleration.Y;
    		
    		mForce.Y = 0;
    	}
    	
    	if (acceleration.Y != 0)
    	{
		    speed.Y += getAccelerationSign(speed.Y) * acceleration.Y * timeFactor;
    	}
    	
    	if (speed.Y != 0)
    	{
		    position.Y += speed.Y * timeFactor;

		    posUpdated = true;
    	}

    	if (posUpdated)
    	{
    		updateControlledObjectsPosition(mPreUpdatePosition, position);
    	}

    	return posUpdated;
	}

    /**
	 * x(i+1) = x(i) + (x(i) - x(i-1)) + a(i) * dt(i) * dt(i)
     */
    private boolean positionVerletIntegration(GameTime gameTime)
    {
    	Point position = mPosition;
    	Vector2 speed = mSpeed;
    	Vector2 acceleration = mAcceleration;

    	mTmpPoint.set(mPreUpdatePosition);
    	
    	mPreUpdatePosition.set(mPosition);
    	mPreUpdateSpeed.set(mSpeed);

    	float timeFactor = gameTime.getElapsedTime() / 1E9F; 
    	
    	float timeFactorSqr = timeFactor * timeFactor;
    	
    	if (mForce.X != 0)
    	{
			acceleration.X = mForce.X * mControllerObject.getPhysicalProperties().getInvMass() + mBaseAcceleration.X;
    		
    		mForce.X = 0;
    	}

    	float deltaX = (mPosition.X - mTmpPoint.X) + acceleration.X * timeFactorSqr;
    	
	    position.X = position.X + deltaX;
		speed.X = deltaX / timeFactor;
	    
    	if (mForce.Y != 0)
    	{
			acceleration.Y = mForce.Y * mControllerObject.getPhysicalProperties().getInvMass() + mBaseAcceleration.Y;
    		
    		mForce.Y = 0;
    	}

    	float deltaY = (mPosition.Y - mTmpPoint.Y) + acceleration.Y * timeFactorSqr;
    	
	    position.Y = position.Y + deltaY;
		speed.Y = deltaY / timeFactor;

		updateControlledObjectsPosition(mPreUpdatePosition, position);
		
    	return true;
	}

    private int getAccelerationSign(float speed)
    {
    	int sign = 1;
    	
    	switch (mAccelerationMode)
    	{
    		case ACCELERATION_ABSOLUTE_MODE :
    			if (speed != 0)
    			{
    				sign = (int) Math.signum(speed);
    			}
    			break;
    			
    		case ACCELERATION_DIRECTIONAL_MODE :
    			sign = 1;
    			break;
    	}
    	
    	return sign;
    }
    
	protected void changeContext(AbstractTrajectory trajectory)
	{
		mControllerObject = trajectory.mControllerObject;
		
	    mMovementRay = trajectory.mMovementRay;
	    
	    mPosition = trajectory.mPosition;
	    mSpeed = trajectory.mSpeed;
	    mAcceleration = trajectory.mAcceleration;
	    mBaseAcceleration = trajectory.mBaseAcceleration;

	    mPreUpdatePosition = trajectory.mPreUpdatePosition;
	    mPreUpdateSpeed = trajectory.mPreUpdateSpeed;
	}

	void updateControlledObjectsPosition(Point oldPosition, Point newPosition)
	{
		mControllerObject.onPositionChanged(oldPosition, newPosition);
	}

    boolean checkPositionLimits(Point position, ITrajectoryLimits positionLimits)
    {
    	boolean changed = false;

    	assert positionLimits != null;
    	
    	IBounds bounds = mControllerObject.getBounds();
    	
    	Point boundsPosition = bounds.getPosition();

    	float xLimitCorr = positionLimits.limitX(bounds);
    	
        if (xLimitCorr != 0)
        {
        	mOutOfBoundsEvent.setup(xLimitCorr, position.X);
        	
        	if (xLimitCorr < 0)
        	{
            	mOutOfBoundsEvent.limitReached = OutOfBoundsLimit.BOUNDS_LIMIT_RIGHT;
        	}
        	else
        	{
            	mOutOfBoundsEvent.limitReached = OutOfBoundsLimit.BOUNDS_LIMIT_LEFT;
        	}
        	
        	notifyOnOutOfBoundsX(mOutOfBoundsEvent);
        	
        	if (mOutOfBoundsEvent.adjust)
        	{
        		position.X += mOutOfBoundsEvent.distance;
        		
        		changed = true;
        	}
        }
         
    	float yLimitCorr = positionLimits.limitY(bounds);

        if (yLimitCorr != 0)
        {
        	mOutOfBoundsEvent.setup(yLimitCorr, position.Y);
        	
        	if (yLimitCorr < 0)
        	{
            	mOutOfBoundsEvent.limitReached = OutOfBoundsLimit.BOUNDS_LIMIT_BOTTOM;
        	}
        	else
        	{
            	mOutOfBoundsEvent.limitReached = OutOfBoundsLimit.BOUNDS_LIMIT_TOP;
        	}
        	
        	notifyOnOutOfBoundsY(mOutOfBoundsEvent);
        	
        	if (mOutOfBoundsEvent.adjust)
        	{
        		position.Y += mOutOfBoundsEvent.distance;
        		
        		changed = true;
        	}
        }
        
        return changed;
    }
    
	private void notifyOnOutOfBoundsX(OutOfBoundsEvent event)
    {
		if (!mControllerObject.onOutOfBounds(event))
		{
			onOutOfBoundsX(event);
		}
    }
    
    private void notifyOnOutOfBoundsY(OutOfBoundsEvent event)
    {
		if (!mControllerObject.onOutOfBounds(event))
		{
			onOutOfBoundsY(event);
		}
    }

    private void doMethodUpdate()
    {
    	switch (mIntegrationMethod)
    	{
	    	case INTEGRATION_METHOD_EULER :
	    		break;
	    		
	    	case INTEGRATION_METHOD_SYMPLECTIC_EULER :
	    		break;
	    		
	    	case INTEGRATION_METHOD_POSITION_VERLET :
	    		calculatePositionVertletInitialPosition(mPosition, mPreUpdatePosition);
	    		break;
    	}
    }
    
    /**
     * x(i+1) = x(i) + v(i) * dt + 0.5 * a(i) * dt(i) * dt(i)  
     * 
     * gives
     * 
     * x(i) = x(i+1) - v(i) * dt - 0.5 * a(i) * dt(i) * dt(i)   
     */
    private Point calculatePositionVertletInitialPosition(Point startPosition, Point result)
    {
    	float frameTimeSqr = GameTime.FPS_TIME_S * GameTime.FPS_TIME_S;
    	
    	result.set(startPosition.X - mSpeed.X * GameTime.FPS_TIME_S - mAcceleration.X * frameTimeSqr,
    			startPosition.Y - mSpeed.Y * GameTime.FPS_TIME_S - mAcceleration.Y * frameTimeSqr);
    			
    	return result;
    }

    public static class SpriteTrajectoryConstraint implements IConstraint
    {
        private IGameContext mGameContext = null;
    	private ITrajectoryLimits mTrajectoryLimit = null;
    	private AbstractTrajectory mTrajectory = null;

    	private boolean mIsRegistered = false;
    	
    	public SpriteTrajectoryConstraint(IGameContext gameContext, AbstractTrajectory trajectory)
    	{
    		mGameContext = gameContext;
    		mTrajectory = trajectory;
    	}
    	
        public ITrajectoryLimits getPositionLimits()
        {
    		return mTrajectoryLimit;
        }
    	
        public void setPositionLimits(ITrajectoryLimits limits)
        {
        	mTrajectoryLimit = limits;
        	
        	if (mIsRegistered)
        	{
            	if (mTrajectoryLimit == null)
            	{
            		mGameContext.getConstraintsManager().remove(this);
            		mIsRegistered = false;
            	}
        	}
        	else
        	{
            	if (mTrajectoryLimit != null)
            	{
            		mGameContext.getConstraintsManager().add(this);
            		mIsRegistered = true;
            	}
        	}
        }

        public void setup()
        {
        	if ((mTrajectoryLimit != null) && !mIsRegistered)
        	{
        		mGameContext.getConstraintsManager().add(this);
        		mIsRegistered = true;
        	}
        }
        
        public void teardown()
        {
        	if (mIsRegistered)
        	{
        		mGameContext.getConstraintsManager().remove(this);
        		mIsRegistered = false;
        	}
        }
        
    	@Override
    	public void update()
    	{
    		if (mTrajectory.checkPositionLimits(mTrajectory.mPosition, mTrajectoryLimit))
    		{
    			mTrajectory.updateControlledObjectsPosition(mTrajectory.mPreUpdatePosition, mTrajectory.mPosition);
    		}
    	}
    }
}
