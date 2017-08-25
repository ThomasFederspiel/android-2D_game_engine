package se.federspiel.android.game.trajectories;

import java.util.ArrayList;

import se.federspiel.android.game.GameTime;
import se.federspiel.android.game.geometry.Point;
import se.federspiel.android.game.geometry.Vector2;
import se.federspiel.android.game.interfaces.IGameContext;
import se.federspiel.android.game.interfaces.ITrajectoryControlledSprite;
import se.federspiel.android.game.trajectories.limits.OutOfBoundsEvent;

public class CompositeTrajectory extends AbstractTrajectory
{
	private Point mOldUpdatePosition = Point.Zero.clone();
    private Vector2 mOldUpdateSpeed = Vector2.Zero.clone();
	
	private ArrayList<AbstractTrajectory> mServeFirstTrajectories = new ArrayList<AbstractTrajectory>();
	private ArrayList<AbstractTrajectory> mServeAllTrajectories = new ArrayList<AbstractTrajectory>();
	
	public CompositeTrajectory(IGameContext gameContext, ITrajectoryControlledSprite sprite)
    {
    	super(gameContext, sprite);
    }

    public void addServeAllTrajectory(AbstractTrajectory trajectory)
	{
		trajectory.changeContext(this);
		
		mServeAllTrajectories.add(trajectory);
	}

    public void addServeFirstTrajectory(AbstractTrajectory trajectory)
	{
		trajectory.changeContext(this);
		
		mServeFirstTrajectories.add(trajectory);
	}

    @Override
	public void setup()
	{
		super.setup();
		
		for (AbstractTrajectory trajectories : mServeFirstTrajectories)
		{
			trajectories.setup();
		}
		
		for (AbstractTrajectory trajectories : mServeAllTrajectories)
		{
			trajectories.setup();
		}
	}

	@Override
	public void teardown()
	{
		for (AbstractTrajectory trajectories : mServeFirstTrajectories)
		{
			trajectories.teardown();
		}
		
		for (AbstractTrajectory trajectories : mServeAllTrajectories)
		{
			trajectories.teardown();
		}
		
		super.teardown();
	}

	@Override
	public boolean update(GameTime gameTime)
	{
		boolean updated = false;
		
		mOldUpdatePosition.set(mPosition);
    	mOldUpdateSpeed.set(mSpeed);

    	for (int i = 0; i < mServeFirstTrajectories.size(); i++)
    	{
    		AbstractTrajectory trajectories = mServeFirstTrajectories.get(i);
    		
			if (trajectories.update(gameTime))
			{
				updated = true;
				
				break;
			}
		}

    	if (!updated)
    	{
	    	for (int i = 0; i < mServeAllTrajectories.size(); i++)
	    	{
	    		AbstractTrajectory trajectories = mServeAllTrajectories.get(i);
	    		
				if (trajectories.update(gameTime))
				{
					updated = true;
				}
			}
    	}
    	
		if (updated)
		{
			mPreUpdatePosition.set(mOldUpdatePosition);
//	;+		mPreUpdateSpeed.set(mOldUpdateSpeed);
		}
		
		return updated;
	}
	
	@Override
    protected void onOutOfBoundsY(OutOfBoundsEvent event)
    {
		setZeroSpeed();
		
		event.adjust = true;
    }
    
	@Override
    protected void onOutOfBoundsX(OutOfBoundsEvent event)
    {
		setZeroSpeed();
		
		event.adjust = true;
    }

	private void setZeroSpeed()
	{
		setSpeed(0, 0);
	}
}
