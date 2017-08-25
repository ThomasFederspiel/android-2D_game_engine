package se.federspiel.android.game.trajectories;

import se.federspiel.android.game.GameTime;
import se.federspiel.android.game.geometry.Point;
import se.federspiel.android.game.interfaces.IBounds;
import se.federspiel.android.game.interfaces.IGameContext;
import se.federspiel.android.game.interfaces.ITrajectoryControlledSprite;
import se.federspiel.android.game.interfaces.IUserInputManager.IOnTouchListener;
import se.federspiel.android.game.interfaces.IUserInputManager.TouchEvent;
import se.federspiel.android.game.trajectories.limits.OutOfBoundsEvent;

public class TouchDragTrajectory extends AbstractTrajectory implements IOnTouchListener
{
	private Point mOffset = Point.Zero.clone();
	private Point mTouchPoint = Point.Zero.clone();

	private boolean mContinuousUpdate = false;
	private boolean mIsDraging = false;
	private boolean mTouchPointIsUpdated = false;
	
    public TouchDragTrajectory(IGameContext gameContext)
    {
    	super(gameContext);
    }

    public TouchDragTrajectory(IGameContext gameContext, ITrajectoryControlledSprite sprite)
    {
    	super(gameContext, sprite);
    }

    public void setContinuousUpdate(boolean enable)
    {
    	mContinuousUpdate = enable;
    }
    
	@Override
	public void setup()
	{
		setAccelerationMode(AccelerationModeEnum.ACCELERATION_ABSOLUTE_MODE);
		
		mGameContext.getUserInputManager().setOnTouchListener(this);
		
		setZeroSpeed();
	}

	@Override
	public void teardown()
	{
		mGameContext.getUserInputManager().removeBoundedOnTouchListener(this);
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
		
		if (mTouchPointIsUpdated)
		{
			Point position = mPosition;

	    	mPreUpdatePosition.set(mPosition);

	    	position.X = mTouchPoint.X + mOffset.X;
	    	position.Y = mTouchPoint.Y + mOffset.Y;

        	float timeFactor = gameTime.getElapsedTime() / 1E9F; 
	        	
    		mSpeed.X = (position.X - mPreUpdatePosition.X) / timeFactor;
    		mSpeed.Y = (position.Y - mPreUpdatePosition.Y) / timeFactor;
	    		
    		mPreUpdateSpeed.set(mSpeed);
	    		
    		updateControlledObjectsPosition(mPreUpdatePosition, position);
	    	
    		if (!mContinuousUpdate)
    		{
    			mTouchPointIsUpdated = false;
    		}
    		
    		changed = true;
		}
    	
    	return changed;
    }

	@Override
	public boolean onTouch(TouchEvent event)
	{
		switch (event.action)
		{
			case POINTER_DOWN :
			case POINTER_MOVE :
				
				if (mControllerObject.getBounds().contains(event.x, event.y))
				{
					if (!mIsDraging)
					{
						calculateOffset(event.x, event.y);
						
						mIsDraging = true;
					}
					
					setTouchPosition(event.x, event.y);
				}
				else if (mIsDraging)
				{
					setTouchPosition(event.x, event.y);
				}
						
				break;
				
			case POINTER_UP :
				mIsDraging = false;
				mTouchPointIsUpdated = false;
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
		
		mTouchPointIsUpdated = true;
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
