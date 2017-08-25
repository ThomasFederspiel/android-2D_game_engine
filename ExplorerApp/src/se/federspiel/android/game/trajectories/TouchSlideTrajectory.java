package se.federspiel.android.game.trajectories;

import se.federspiel.android.game.GameTime;
import se.federspiel.android.game.geometry.Point;
import se.federspiel.android.game.interfaces.IBounds;
import se.federspiel.android.game.interfaces.IGameContext;
import se.federspiel.android.game.interfaces.ITrajectoryControlledSprite;
import se.federspiel.android.game.interfaces.IUserInputManager;
import se.federspiel.android.game.interfaces.IUserInputManager.IOnTouchListener;
import se.federspiel.android.game.interfaces.IUserInputManager.TouchEvent;
import se.federspiel.android.game.trajectories.limits.OutOfBoundsEvent;
import se.federspiel.android.game.utils.AMath;

public class TouchSlideTrajectory extends AbstractTrajectory implements IOnTouchListener
{
	public enum TouchLock
	{
		LOCK_NO,
		LOCK_X,
		LOCK_Y,
		LOCK_X_OR_Y
	}
	
	private static final int STOP_TIME_MS = 200;
	private static final int STOP_COUNT_LEVEL = STOP_TIME_MS * GameTime.FPS / 1000;
	
	private Point mOffset = Point.Zero.clone();
	private Point mInitialTouchPoint = Point.Zero.clone();
	private Point mTouchPoint = Point.Zero.clone();

	private TouchLock mInitialLock = TouchLock.LOCK_NO;
	private TouchLock mActiveLock = TouchLock.LOCK_NO;
	
	private boolean mTouchActive = false;
	private boolean mTouchPointIsUpdated = false;
	private boolean mTouchRelease = false;
	private boolean mSlideReleaseEnable = false;
	private int mNoUpdateCounts = STOP_COUNT_LEVEL;
	
    public TouchSlideTrajectory(IGameContext gameContext)
    {
    	super(gameContext);
    }

    public TouchSlideTrajectory(IGameContext gameContext, ITrajectoryControlledSprite sprite)
    {
    	super(gameContext, sprite);
    }

	@Override
	public void setup()
	{
		setAccelerationMode(AccelerationModeEnum.ACCELERATION_ABSOLUTE_MODE);
		
		mGameContext.getUserInputManager().setBoundedOnTouchListener(this, mControllerObject.getBounds(), IUserInputManager.TouchZOrder.SPRITE);
		
		setZeroSpeed();
	}

	@Override
	public void teardown()
	{
		mGameContext.getUserInputManager().removeBoundedOnTouchListener(this);
	}
	
	public void setSlideRelease(boolean enable)
	{
		mSlideReleaseEnable = enable;
	}
	
	public void setLock(TouchLock lock)
	{
		mInitialLock = lock;
	}

	@Override
    public void setPosition(Point position)
    {
		updateOffset(position.X, position.Y);
		
		super.setPosition(position);
    }
	
	@Override
    public boolean update(GameTime gameTime)
    {
		boolean changed = false;
		
		if (mTouchActive)
		{
			if (mTouchPointIsUpdated)
			{
				Point position = mPosition;

		    	mPreUpdatePosition.set(mPosition);

				switch (mActiveLock)
				{
					case LOCK_X :
				    	position.X = mTouchPoint.X + mOffset.X;
						break;
						
					case LOCK_Y :
				    	position.Y = mTouchPoint.Y + mOffset.Y;
						break;
						
					default :
				    	position.X = mTouchPoint.X + mOffset.X;
				    	position.Y = mTouchPoint.Y + mOffset.Y;
				    	break;
				}

	        	float timeFactor = gameTime.getElapsedTime() / 1E9F; 
	        	
				switch (mActiveLock)
				{
					case LOCK_X :
			    		mSpeed.X = (position.X - mPreUpdatePosition.X) / timeFactor;
						break;
						
					case LOCK_Y :
			    		mSpeed.Y = (position.Y - mPreUpdatePosition.Y) / timeFactor;
						break;
						
					default :
			    		mSpeed.X = (position.X - mPreUpdatePosition.X) / timeFactor;
			    		mSpeed.Y = (position.Y - mPreUpdatePosition.Y) / timeFactor;
				    	break;
				}
	    		
	    		mPreUpdateSpeed.set(mSpeed);
	    		
	    		updateControlledObjectsPosition(mPreUpdatePosition, position);
	    		
	    		mTouchPointIsUpdated = false;
	    		
				mNoUpdateCounts = STOP_COUNT_LEVEL;
				
	    		changed = true;
			}
			else
			{
				if (mNoUpdateCounts > 0)
				{
					mNoUpdateCounts--;
					
					if (mNoUpdateCounts == 0)
					{
						mPreUpdateSpeed.set(mSpeed);
	
			    		if ((mSpeed.X != 0) || (mSpeed.Y != 0))
			    		{
				    		mSpeed.X = 0;
				    		mSpeed.Y = 0;
				    		
			    			mNoUpdateCounts = 1;
			    		}
					}
				}
				
		    	mPreUpdatePosition.set(mPosition);
	    		updateControlledObjectsPosition(mPreUpdatePosition, mPosition);
	    		
	    		changed = true;
			}
		}
		else if (mTouchRelease)
		{
			if (mSlideReleaseEnable)
			{
	    		mPreUpdateSpeed.set(mSpeed);
	    		
    			mTouchRelease = false;
				
				mNoUpdateCounts = STOP_COUNT_LEVEL;
			}
			else
			{
	    		mPreUpdateSpeed.set(mSpeed);
	
	    		if ((mSpeed.X != 0) || (mSpeed.Y != 0))
	    		{
		    		mSpeed.X = 0;
		    		mSpeed.Y = 0;
	    		}
	    		else
	    		{
	    			mTouchRelease = false;
	    		}
	    		
				mNoUpdateCounts = STOP_COUNT_LEVEL;
			}
			
    		changed = true;
		}
		else if (mSlideReleaseEnable)
		{
			changed = super.updatePosition(gameTime);
		}
		
    	return changed;
   }

	@Override
	public boolean onTouch(TouchEvent event)
	{
		switch (event.action)
		{
			case POINTER_DOWN :
				mTouchActive = true;

				mActiveLock = mInitialLock;

				mInitialTouchPoint.set(event.x, event.y);
				
				calculateOffset(event.x, event.y);
				
				setTouchPosition(event.x, event.y);
				break;
				
			case POINTER_UP :
				mTouchActive = false;
				mTouchRelease = true;
				break;
				
			case POINTER_MOVE :
				setTouchPosition(event.x, event.y);
  				break;
		}
		
		return true;
	}

	@Override
    protected void onOutOfBoundsX(OutOfBoundsEvent event)
    {
		setZeroSpeed();
		
		event.adjust = true;
    }
    
	@Override
    protected void onOutOfBoundsY(OutOfBoundsEvent event)
    {
		setZeroSpeed();
		
		event.adjust = true;
    }
    
	private void setZeroSpeed()
	{
		setSpeed(0, 0);
	}
	
	private void setTouchPosition(float x, float y)
	{
		mTouchPoint.set(x, y);
		
		evaluateLock(mTouchPoint);
	}
	
	private void evaluateLock(Point point)
	{
		switch (mActiveLock)
		{
			case LOCK_X :
			case LOCK_Y :
			case LOCK_NO :
				mTouchPointIsUpdated = true;
				break;
				
			case LOCK_X_OR_Y :
				float xDelta = Math.abs(mInitialTouchPoint.X - point.X);
				float yDelta = Math.abs(mInitialTouchPoint.Y - point.Y);

				if (Math.abs(xDelta - yDelta) > AMath.EPS)
				{
					if (xDelta > yDelta)
					{
						mActiveLock = TouchLock.LOCK_X;
					}
					else
					{
						mActiveLock = TouchLock.LOCK_Y;
					}
				}
				break;
		}
	}
	
	private void calculateOffset(float x, float y)
	{
		IBounds bounds = mControllerObject.getBounds();
		
		Point position = bounds.getPosition();
		
		mOffset.X = position.X - x;
		mOffset.Y = position.Y - y;
	}
	
	private void updateOffset(float x, float y)
	{
		mOffset.X += (x - mPosition.X);
		mOffset.Y += (y - mPosition.Y);
	}
}
