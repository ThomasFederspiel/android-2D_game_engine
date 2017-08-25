package se.federspiel.android.game;

import java.lang.reflect.Constructor;
import java.util.Hashtable;

import se.federspiel.android.game.interfaces.IBackground;
import se.federspiel.android.game.interfaces.IBackgroundFactory;
import se.federspiel.android.game.interfaces.IGameContext;

public class BackgroundFactory implements IBackgroundFactory
{
    private Hashtable<String, Class<? extends IBackground>> mRegisteredBackgroundes = new Hashtable<String, Class<? extends IBackground>>();
    private GameContext mGameContext = null;

    public BackgroundFactory(GameContext gameContext)
    {
        mGameContext = gameContext;
    }

    public void registerBackground(String id, Class<? extends IBackground> background)
    {
    	mRegisteredBackgroundes.put(id, background);
    }

    public IBackground createBackground(String id)
    {
    	IBackground background = null;

        if (mRegisteredBackgroundes.containsKey(id))
        {
            Class<? extends IBackground> backgroundClass = mRegisteredBackgroundes.get(id);

            try
            {
	            Constructor<? extends IBackground> backgroundConstructor = backgroundClass.getConstructor(new Class[] { IGameContext.class });
	            
	            background = backgroundConstructor.newInstance(mGameContext);
            }
            catch (Exception e)
            {
            	throw new RuntimeException(e);
            }
        }

        return background;
    } 
    
	@Override
	public void destroy()
	{
		mGameContext = null;
		this.mRegisteredBackgroundes.clear();
	}
}
