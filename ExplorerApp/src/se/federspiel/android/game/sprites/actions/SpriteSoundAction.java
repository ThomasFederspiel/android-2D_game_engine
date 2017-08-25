package se.federspiel.android.game.sprites.actions;

import se.federspiel.android.game.interfaces.ICollisionSprite;
import se.federspiel.android.game.interfaces.IGameContext;
import se.federspiel.android.game.interfaces.ISpriteCollisionObject;
import se.federspiel.android.game.interfaces.ISpriteCollisionObject.ICollisionContext;


public class SpriteSoundAction extends AbstractSpriteAction
{
    private IGameContext mGameContext = null;
    private int mResourceId = -1;
    private boolean mVibrate = false;
    
	public SpriteSoundAction(int resourceId, boolean vibrate, IGameContext gameContext)
	{
		mResourceId = resourceId;
		mVibrate = vibrate;	
		mGameContext = gameContext;	
	}
	
	@Override
	public void onSpriteCollision(ICollisionSprite sprite, ICollisionContext context,
			ISpriteCollisionObject collidingObject)
	{
    	mGameContext.getSoundManager().playSound(mResourceId, false);
    	
    	if (mGameContext.getServiceManager().hasVibrator() && mVibrate)
    	{
    		mGameContext.getServiceManager().getVibrator().vibrate(100);
    	}
    	
        super.onSpriteCollision(sprite, context, collidingObject);
	}
}
