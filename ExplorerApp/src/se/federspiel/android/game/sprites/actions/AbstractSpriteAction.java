package se.federspiel.android.game.sprites.actions;

import se.federspiel.android.game.GameTime;
import se.federspiel.android.game.interfaces.ICollisionSprite;
import se.federspiel.android.game.interfaces.ISprite;
import se.federspiel.android.game.interfaces.ISpriteAction;
import se.federspiel.android.game.interfaces.ISpriteCollisionListener;
import se.federspiel.android.game.interfaces.ISpriteCollisionObject;
import se.federspiel.android.game.interfaces.ISpriteCollisionObject.ICollisionContext;

public abstract class AbstractSpriteAction implements ISpriteAction
{
	private ISpriteCollisionListener mSpriteCollisionListener = null;
	
	@Override
	public void setSpriteCollsionListener(ISpriteCollisionListener listener)
	{
		assert mSpriteCollisionListener == null;
		
		mSpriteCollisionListener = listener;
	}

	@Override
	public boolean isYielding(ISprite sprite)
	{
		return true;
	}

	@Override
	public void update(ISprite sprite, GameTime gameTime)
	{
	}
	
	@Override
	public void onSpriteCollision(ICollisionSprite sprite,
			ICollisionContext context, ISpriteCollisionObject collidingObject)
	{
		if (mSpriteCollisionListener != null)
		{
			mSpriteCollisionListener.onSpriteCollision(sprite, context, collidingObject);
		}
	}
}
