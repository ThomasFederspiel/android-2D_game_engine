package se.federspiel.android.backgrounds.trajectories;

import se.federspiel.android.game.GameTime;
import se.federspiel.android.game.geometry.Point;
import se.federspiel.android.game.geometry.Vector2;

public class ConstantVelocityTrajectory extends AbstractTrajectory
{
	private Point mTmpPosition = Point.Zero.clone();
	
	private Vector2 mSpeed = Vector2.Zero.clone();

	public ConstantVelocityTrajectory()
	{
	}
	
	@Override
	public void updatePosition(GameTime gameTime) 
	{
    	boolean posUpdated = false;

    	mTmpPosition.set(mTrajectoryControlledBackground.getPosition());
    	
    	Vector2 speed = mSpeed;

    	float timeFactor = gameTime.getElapsedTime() / 1E9F; 
    	
    	if (speed.X != 0)
    	{
    		mTmpPosition.X +=
		        speed.X * timeFactor;
		    
		    posUpdated = true;
    	}
    	
    	if (speed.Y != 0)
    	{
    		mTmpPosition.Y +=
		        speed.Y * timeFactor;

		    posUpdated = true;
    	}

    	if (posUpdated)
    	{
    		mTrajectoryControlledBackground.onPositionChanged(mTmpPosition);
    	}
	}
	
    public void setSpeed(Vector2 speed)
    {
		mSpeed.set(speed);
		
		mSpeed.scale(-1);
    }
}
