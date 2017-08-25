package se.federspiel.android.backgrounds.trajectories;

import se.federspiel.android.game.GameTime;
import se.federspiel.android.game.interfaces.ITrajectoryControlledBackground;

public abstract class AbstractTrajectory 
{
	protected ITrajectoryControlledBackground mTrajectoryControlledBackground = null;
	
	public abstract void updatePosition(GameTime gameTime);
	
	public void setTrajectoryControlledBackground(ITrajectoryControlledBackground background)
	{
		mTrajectoryControlledBackground = background;
	}
	
	public void setup()
	{
	}

	public void teardown()
	{
	}
}
