package se.federspiel.android.game.trajectories.limits;

import se.federspiel.android.game.interfaces.IBounds;
import se.federspiel.android.game.interfaces.ITrajectoryLimits;

public class BoundsLimits implements ITrajectoryLimits
{
    private float mXMin = 0;
    private float mXMax = 0;
    private float mYMin = 0;
    private float mYMax = 0;
    
    public BoundsLimits(int xMin, int xMax, int yMin, int yMax)
    {
        mXMin = xMin;
        mXMax = xMax;
        mYMin = yMin;
        mYMax = yMax;
    }

	@Override
	public void moveLimits(float dx, float dy)
	{
        mXMin += dx;
        mXMax += dx;
        
        mYMin += dy;
        mYMax += dy;
	}
	
	@Override
    public float limitX(IBounds bounds)
    {
		float xCorr = mXMax - bounds.getRight();

        if (xCorr > 0)
        {
            xCorr = mXMin - bounds.getLeft();
            
        	if (xCorr < 0)
	        {
        		xCorr = 0;
	        }
        }

        return xCorr;
    }
    
	@Override
    public float limitY(IBounds bounds)
    {
        float yCorr = mYMax - bounds.getBottom();

        if (yCorr > 0)
        {
            yCorr = mYMin - bounds.getTop();
            
        	if (yCorr < 0)
	        {
        		yCorr = 0;
	        }
        }
        
        return yCorr;
    }

	protected void setXLimits(int xMin, int xMax)
	{
        mXMin = xMin;
        mXMax = xMax;
	}

	protected void setYLimits(int yMin, int yMax)
	{
        mYMin = yMin;
        mYMax = yMax;
	}
}
