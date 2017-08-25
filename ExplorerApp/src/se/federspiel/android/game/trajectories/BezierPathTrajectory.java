package se.federspiel.android.game.trajectories;

import se.federspiel.android.game.GameTime;
import se.federspiel.android.game.geometry.Point;
import se.federspiel.android.game.geometry.Vector2;
import se.federspiel.android.game.interfaces.IGameContext;
import se.federspiel.android.game.interfaces.ITrajectoryControlledSprite;
import se.federspiel.android.game.utils.AMath;
import se.federspiel.android.game.utils.AMath.BezierFactors;
import se.federspiel.android.game.utils.DownCounter;

public class BezierPathTrajectory extends AbstractPathTrajectory
{
	private DownCounter mDownCounter = new DownCounter(2);
	
	private BezierFactors[] mFactors = null;
	private int mFactorIndex = -1;
	
	private float mAbsoluteSpeed = 0;
	
	private int mRepetitions = 1;
	
	private BezierFactors mCurrentFactors = new BezierFactors();
	private Point mEndPoint = null;
	private Point mStartPoint = null;
	
	private float mSnapRadius = 0;
	
    public BezierPathTrajectory(IGameContext gameContext, ITrajectoryControlledSprite sprite)
    {
    	super(gameContext, sprite);
    }

	public void definePath(Direction direction, int repetitions, Point[] path, float scale)
	{
		assert (path.length >= 2);

		Point[] ctrlPoints = AMath.bezierInterpolateControlPoints(path, scale);
		
		super.definePath(direction, repetitions, ctrlPoints);
		
		evaluateControlPoints();
	}
	
	public void definePath(Direction direction, int repetitions, Point[] path)
	{
		assert ((path.length >= 4) && (((path.length - 4) % 3) == 0));
		
		super.definePath(direction, repetitions, path);
		
		evaluateControlPoints();
	}
	
	@Override
    public void setInitialSpeed(Vector2 speed)
    {
		mAbsoluteSpeed = speed.getMagnitude();
		
		mSnapRadius = mAbsoluteSpeed * GameTime.FPS_TIME_S;
		
    	super.setInitialSpeed(speed);
    	
    	evaluateControlPoints();
    }
	
	@Override
	public void setup()
	{
		super.setup();
		
		mFactorIndex = -1; 
		mRepetitions = mInitialRepetitions;
		
    	super.setInitialPosition(selectPoint());
	}
	
    @Override
    public boolean update(GameTime gameTime)
    {
    	boolean updated = false;
    	
    	if (mFactorIndex != -1)
    	{
        	mPreUpdatePosition.set(mPosition);
        	
    		mPosition.move(mCurrentFactors.mDfx, mCurrentFactors.mDfy);
    		
    		mCurrentFactors.takeAStep();
    		
    		if (AMath.insideCircle(mEndPoint, mSnapRadius, mPosition))
    		{
    			if (selectPoint() != null)
    			{
    				BezierPathTrajectory.this.setPosition(mStartPoint);
    			}
    		}
        	
    		updateControlledObjectsPosition(mPreUpdatePosition, mPosition);
        	
    		updated = true;
    	}
    	
    	return updated;
    }
    
    private void evaluateControlPoints()
    {
    	if (mDownCounter.countDown())
    	{
    		if ((mPathPositions.length >= 4) && (((mPathPositions.length - 4) % 3) == 0))
    		{
	    		int size = 1 + (mPathPositions.length - 4) / 3;
	    		
    	    	switch (mDirection)
    	    	{
    	    		case FORWARD :
						mFactors = new BezierFactors[size];
				
						for (int i = 1; i < mPathPositions.length; i += 3)
						{
							 mFactors[i / 3] = calculateFactors(i - 1, 1, new BezierFactors());
						}
						break;
					
    	    		case BACKWARDS :
						mFactors = new BezierFactors[size];
				
						for (int i = 1; i < mPathPositions.length; i += 3)
						{
							 mFactors[i / 3] = calculateFactors(mPathPositions.length - i, -1, new BezierFactors());
						}
						break;
						
    	    		case BACK_AND_FORTH :
						mFactors = new BezierFactors[size * 2];
				
						for (int i = 1; i < mPathPositions.length; i += 3)
						{
							 mFactors[i / 3] = calculateFactors(i - 1, 1, new BezierFactors());
						}
						
						for (int i = 1; i < mPathPositions.length; i += 3)
						{
							 mFactors[(i + mPathPositions.length) / 3] = calculateFactors(mPathPositions.length - i, -1, new BezierFactors());
						}
						break;
    	    	}
    		}
    	}
    }
	
    private BezierFactors calculateFactors(int startIndex, int inc, BezierFactors factors)
    {
    	Point point1 = mPathPositions[startIndex];
    	
    	int offset = inc;
		Point point2 = mPathPositions[startIndex + offset];
		
		offset += inc;
		Point point3 = mPathPositions[startIndex + offset];
		
		offset += inc;
		Point point4 = mPathPositions[startIndex + offset];

	    AMath.bezierCalculateFactors(point1, point2, point3, point4, 30, factors);
		
		int steps = (int) ((factors.pathLength() * GameTime.FPS) / mAbsoluteSpeed); 
		
		AMath.bezierCalculateFactors(point1, point2, point3, point4, steps, factors);
	    
		return factors;
    }
    
	private Point selectPoint()
	{
		Point selectedPoint = null;
		
		selectNextPoint();
    	
		if (mFactorIndex == -1) 
		{
			if (mRepetitions > 0)
			{
				mRepetitions--;
				
				if (mRepetitions >= 0)
				{
					mFactorIndex = 0;
				}
			}
		}

    	if (mFactorIndex >= 0)
    	{
			mCurrentFactors.set(mFactors[mFactorIndex]);
			
			mStartPoint = mCurrentFactors.mFirstPoint;
			mEndPoint = mCurrentFactors.mLastPoint;
			
			selectedPoint = mStartPoint;
    	}
    	
		return selectedPoint;
    }
    
	private void selectNextPoint()
    {
		if (mFactorIndex != -1)
		{
			mFactorIndex++;

			if (mFactorIndex >= mFactors.length)
			{
				mFactorIndex = -1;
			}
		}
    }
}
