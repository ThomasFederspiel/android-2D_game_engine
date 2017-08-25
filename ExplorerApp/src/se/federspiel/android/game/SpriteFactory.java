package se.federspiel.android.game;

import java.lang.reflect.Constructor;
import java.util.Hashtable;

import se.federspiel.android.game.interfaces.IGameContext;
import se.federspiel.android.game.interfaces.ISprite;
import se.federspiel.android.game.interfaces.ISpriteFactory;

public class SpriteFactory implements ISpriteFactory
{
    private Hashtable<String, Class<? extends ISprite>> mRegisteredSprites = new Hashtable<String, Class<? extends ISprite>>();
    private GameContext mGameContext = null;

    public SpriteFactory(GameContext gameContext)
    {
        mGameContext = gameContext;
    }

    public void registerSprite(String id, Class<? extends ISprite> sprite)
    {
        mRegisteredSprites.put(id, sprite);
    }

    public ISprite createSprite(String id)
    {
        ISprite sprite = null;

        if (mRegisteredSprites.containsKey(id))
        {
            Class<? extends ISprite> spriteClass = mRegisteredSprites.get(id);

            try
            {
	            Constructor<? extends ISprite> spriteConstructor = spriteClass.getConstructor(new Class[] { IGameContext.class });
	            
	            sprite = spriteConstructor.newInstance(mGameContext);
            }
            catch (Exception e)
            {
            	throw new RuntimeException(e);
            }
        }

        return sprite;
    }

	@Override
	public void destroy()
	{
		mGameContext = null;
		mRegisteredSprites.clear();
	}
}
