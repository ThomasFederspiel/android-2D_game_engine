package se.federspiel.android.backgrounds.trajectories;

import java.util.ArrayList;

import se.federspiel.android.game.GameTime;
import se.federspiel.android.game.interfaces.ITrajectoryControlledBackground;

public class CompositeTrajectory extends AbstractTrajectory
{
	private ArrayList<AbstractTrajectory> mTrajectories = new ArrayList<AbstractTrajectory>();
	
	public CompositeTrajectory()
    {
    }

    public void addTrajectory(AbstractTrajectory trajectory)
	{
		mTrajectories.add(trajectory);
		
		trajectory.setTrajectoryControlledBackground(mTrajectoryControlledBackground);
	}

	@Override
	public void setTrajectoryControlledBackground(ITrajectoryControlledBackground background)
	{
		super.setTrajectoryControlledBackground(background);
		
		for (int i = 0; i < mTrajectories.size(); i++)
		{
			mTrajectories.get(i).setTrajectoryControlledBackground(background);
		}
	}
	
	@Override
	public void updatePosition(GameTime gameTime) 
	{
		for (int i = 0; i < mTrajectories.size(); i++)
		{
			mTrajectories.get(i).updatePosition(gameTime);
		}
	}
	
	@Override
	public void setup()
	{
		for (int i = 0; i < mTrajectories.size(); i++)
		{
			mTrajectories.get(i).setup();
		}
	}

	@Override
	public void teardown()
	{
		for (int i = 0; i < mTrajectories.size(); i++)
		{
			mTrajectories.get(i).teardown();
		}
	}
}
