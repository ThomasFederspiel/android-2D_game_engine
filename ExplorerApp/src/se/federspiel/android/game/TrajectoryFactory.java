package se.federspiel.android.game;

import java.lang.reflect.Constructor;
import java.util.Hashtable;

import se.federspiel.android.game.interfaces.IGameContext;
import se.federspiel.android.game.interfaces.ITrajectory;
import se.federspiel.android.game.interfaces.ITrajectoryControlledSprite;
import se.federspiel.android.game.interfaces.ITrajectoryFactory;

public class TrajectoryFactory implements ITrajectoryFactory
{
    private Hashtable<String, Class<? extends ITrajectory>> mRegisteredTrajectories = new Hashtable<String, Class<? extends ITrajectory>>();
    private GameContext mGameContext = null;

    public TrajectoryFactory(GameContext gameContext)
    {
        mGameContext = gameContext;
    }

    public void registerTrajectory(String id, Class<? extends ITrajectory> trajectory)
    {
        mRegisteredTrajectories.put(id, trajectory);
    }

    public ITrajectory createTrajectory(String id, ITrajectoryControlledSprite controlledObject)
    {
        ITrajectory trajectory = null;

        Class<? extends ITrajectory> trajectoryClass = GetTrajectory(id);

        if (trajectoryClass != null)
        {
            try
            {
	            Constructor<? extends ITrajectory> trajectoryConstructor = trajectoryClass.getConstructor(new Class[] { IGameContext.class, ITrajectoryControlledSprite.class });
	            
	            trajectory = trajectoryConstructor.newInstance(mGameContext, controlledObject);
	            
	            controlledObject.setTrajectory(trajectory);
            }
            catch (Exception e)
            {
            	throw new RuntimeException(e);
            }
        }

        return trajectory;
    }

    public ITrajectory createTrajectory(String id)
    {
        ITrajectory trajectory = null;

        Class<? extends ITrajectory> trajectoryClass = GetTrajectory(id);

        if (trajectoryClass != null)
        {
            try
            {
            	Constructor<? extends ITrajectory> trajectoryConstructor = trajectoryClass.getConstructor(new Class[] { IGameContext.class });
				
				trajectory = trajectoryConstructor.newInstance(mGameContext);
            }
            catch (Exception e)
            {
            	throw new RuntimeException(e);
            }
        }

        return trajectory;
    }

    private Class<? extends ITrajectory> GetTrajectory(String id)
    {
        Class<? extends ITrajectory> type = null;

        if (mRegisteredTrajectories.containsKey(id))
        {
            type = mRegisteredTrajectories.get(id);
        }

        return type;
    }
    
	@Override
	public void destroy()
	{
		mGameContext = null;
		mRegisteredTrajectories.clear();
	}
}
