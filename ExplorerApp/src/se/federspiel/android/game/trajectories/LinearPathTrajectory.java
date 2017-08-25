package se.federspiel.android.game.trajectories;

import se.federspiel.android.game.GameTime;
import se.federspiel.android.game.geometry.Point;
import se.federspiel.android.game.geometry.Vector2;
import se.federspiel.android.game.interfaces.IGameContext;
import se.federspiel.android.game.interfaces.ITrajectoryControlledSprite;
import se.federspiel.android.game.utils.AMath;

public class LinearPathTrajectory extends AbstractPathTrajectory
{
	private boolean mInitialSpeedSet = false;
	
	private float mAbsoluteSpeed = 0;
	
	private Point mCurrentPoint = null;
	private Point mNextPoint = null;
	
	private int mCurrentIndex = -1;
	private int mNextIndex = -1;
	
	private int mRepetitions = 1;
	
	private float mSnapRadius = 0;

	private Vector2 mGCNewSpeedVector = Vector2.Zero.clone();
	
    public LinearPathTrajectory(IGameContext gameContext, ITrajectoryControlledSprite sprite)
    {
    	super(gameContext, sprite);
    }

	@Override
    public void setInitialSpeed(Vector2 speed)
    {
		mAbsoluteSpeed = speed.getMagnitude();

		mSnapRadius = mAbsoluteSpeed * GameTime.FPS_TIME_S;
    }

	@Override
	public void definePath(Direction direction, int repetitions, Point[] path)
	{
		super.definePath(direction, repetitions, path);
	}
	
	@Override
	public void setup()
	{
		super.setup();
		
    	mCurrentPoint = null;
    	mNextPoint = null;
    	
    	mCurrentIndex = -1;
    	mNextIndex = -1;
    	
		mInitialSpeedSet = false;
		
		mRepetitions = mInitialRepetitions;
		
    	super.setInitialPosition(selectPoint());
	}
	
    @Override
    public boolean update(GameTime gameTime)
    {
    	boolean updated = super.updatePosition(gameTime);
    	
    	if (mNextPoint != null)
    	{
    		if (AMath.insideCircle(mNextPoint, mSnapRadius, mPosition))
    		{
    			if (selectPoint() != null)
    			{
    				setPosition(mCurrentPoint);
    			}
    			else
    			{
    				setSpeed(0, 0);
    			}
    		}
    	}
    	
    	return updated;
    }

    @SuppressWarnings("incomplete-switch")
	private Point selectPoint()
    {
    	switch (mDirection)
    	{
    		case FORWARD :
    			
    			selectNextPoint(Direction.FORWARD);
    	    	
    			if ((mCurrentIndex == -1) && (mRepetitions > 0))
    			{
    				mRepetitions--;
    				
    				if (mRepetitions >= 0)
    				{
        				mCurrentIndex = 0;
        				mNextIndex = mCurrentIndex + 1;
    				}
    			}
    			break;
    			
    		case BACKWARDS :
    			
    	    	selectNextPoint(Direction.BACKWARDS);
    	    	
    			if ((mCurrentIndex == -1) && (mRepetitions > 0))
    			{
    				mRepetitions--;
    				
    				if (mRepetitions >= 0)
    				{
        				mCurrentIndex = mPathPositions.length - 1;
        				mNextIndex = mCurrentIndex - 1;
    				}
    			}
    			
    			break;
    			
    		case BACK_AND_FORTH :

				selectNextPoint(mBackAndForthDirection);
				
    			switch (mBackAndForthDirection)
    			{
	        		case FORWARD :
	        			
	        			if ((mCurrentIndex == -1) && (mRepetitions > 0))
	        			{
	        				mRepetitions--;
	        				
	        				if (mRepetitions >= 0)
	        				{
	            				mCurrentIndex = mPathPositions.length - 1;
	            				mNextIndex = mCurrentIndex - 1;
	            				
	            				mBackAndForthDirection = Direction.BACKWARDS;	        				
	            			}
	        			}
	        			break;
	        			
	        		case BACKWARDS :
	        			
	        			if ((mCurrentIndex == -1) && (mRepetitions > 0))
	        			{
	        				mRepetitions--;
	        				
	        				if (mRepetitions >= 0)
	        				{
	            				mCurrentIndex = 0;
	            				mNextIndex = mCurrentIndex + 1;
	            				
	            				mBackAndForthDirection = Direction.FORWARD;	        				
	        				}
	        			}
	        			
	        			break;
    			}
    			
    			break;
    	}

		if (mCurrentIndex >= 0)
		{
			mCurrentPoint = mPathPositions[mCurrentIndex];
			mNextPoint = mPathPositions[mNextIndex];
			
			updateSpeed();
		}
		else
		{
			mCurrentPoint = null;
			mNextPoint = null;
		}
		
		return mCurrentPoint;
    }
    
    @SuppressWarnings("incomplete-switch")
	private void selectNextPoint(Direction direction)
    {
    	switch (direction)
    	{
    		case FORWARD :

    			if (mNextIndex != -1)
    			{
					mCurrentIndex = mNextIndex;
					mNextIndex++;

					if (mNextIndex >= mPathPositions.length)
					{
						mCurrentIndex = -1;
						mNextIndex = -1;
					}
    			}
    			break;
    			
    		case BACKWARDS :
    			
    			if (mNextIndex != -1)
    			{
    				mCurrentIndex = mNextIndex;
    				mNextIndex--;
    				
    				if (mNextIndex <= 0)
    				{
        				mCurrentIndex = -1;
        				mNextIndex = -1;
    				}
    			}
    			
    			break;
    	}
    }
    
    private void updateSpeed()
    {
    	Vector2 newSpeed = null;
    	
		if (mCurrentPoint != null)
		{
//			newSpeed = mNextPoint.subtract(mCurrentPoint, mGCNewSpeedVector);
			newSpeed = AMath.subtract(mNextPoint, mCurrentPoint, mGCNewSpeedVector);
			
			newSpeed.normalize().scale(mAbsoluteSpeed);

			if (!mInitialSpeedSet)
			{
				super.setInitialSpeed(newSpeed);
				
				mInitialSpeedSet = true;
			}
			
			super.setSpeed(newSpeed);
    	}
    }
}
