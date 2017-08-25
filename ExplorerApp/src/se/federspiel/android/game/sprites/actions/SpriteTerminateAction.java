package se.federspiel.android.game.sprites.actions;

import se.federspiel.android.game.GameTime;
import se.federspiel.android.game.interfaces.ICollisionSprite;
import se.federspiel.android.game.interfaces.IGameContext;
import se.federspiel.android.game.interfaces.ISprite;
import se.federspiel.android.game.interfaces.ISpriteCollisionObject;
import se.federspiel.android.game.interfaces.ISpriteCollisionObject.ICollisionContext;

public class SpriteTerminateAction extends AbstractSpriteAction
{
    private IGameContext mGameContext = null;
    private boolean mTerminate = false;

    private ITerminateSpriteListener mTerminateSpriteListener = null;
    
	public SpriteTerminateAction(IGameContext gameContext)
	{
		mGameContext = gameContext;	
	}
	
	@Override
	public void onSpriteCollision(ICollisionSprite sprite, ICollisionContext context,
			ISpriteCollisionObject collidingObject)
	{
		mTerminate = true;
		
        super.onSpriteCollision(sprite, context, collidingObject);
	}
	
	@Override
	public void update(ISprite sprite, GameTime gameTime)
	{
		if (mTerminate)
		{
			mGameContext.getCollisionManager().remove(sprite.getCollisionObject());
			mGameContext.getGameEngine().removeComponent(sprite);
			
			if (mTerminateSpriteListener != null)
			{
				mTerminateSpriteListener.onSpriteTermination(sprite);
			}
			
			mTerminate = false;
		}
	}

	public void setTerminationListener(ITerminateSpriteListener listener)
	{
		assert mTerminateSpriteListener == null;
		
		mTerminateSpriteListener = listener;
	}
	
	public interface ITerminateSpriteListener
	{
		public void onSpriteTermination(ISprite sprite);
	}
}
