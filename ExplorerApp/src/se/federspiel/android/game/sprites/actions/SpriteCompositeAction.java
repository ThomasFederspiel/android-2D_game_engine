package se.federspiel.android.game.sprites.actions;

import java.util.ArrayList;

import se.federspiel.android.game.GameTime;
import se.federspiel.android.game.interfaces.ICollisionSprite;
import se.federspiel.android.game.interfaces.ISprite;
import se.federspiel.android.game.interfaces.ISpriteAction;
import se.federspiel.android.game.interfaces.ISpriteCollisionObject;
import se.federspiel.android.game.interfaces.ISpriteCollisionObject.ICollisionContext;

public class SpriteCompositeAction extends AbstractSpriteAction
{
	private ArrayList<ISpriteAction> mActions = new ArrayList<ISpriteAction>();
	
	public SpriteCompositeAction()
	{
	}
	
	@Override
	public boolean isYielding(ISprite sprite)
	{
		boolean yielding = false;
		
		for (ISpriteAction actions : mActions)
		{
			if (actions.isYielding(sprite))
			{
				yielding = true;
				
				break;
			}
		}
		
		return yielding;
	}
	
	public void addAction(ISpriteAction action)
	{
		mActions.add(action);
	}
	
	@Override
	public void onSpriteCollision(ICollisionSprite sprite, ICollisionContext context,
			ISpriteCollisionObject collidingObject)
	{
		for (ISpriteAction actions : mActions)
		{
			actions.onSpriteCollision(sprite, context, collidingObject);
		}
		
        super.onSpriteCollision(sprite, context, collidingObject);
	}
	
	@Override
	public void update(ISprite sprite, GameTime gameTime)
	{
		for (ISpriteAction actions : mActions)
		{
			actions.update(sprite, gameTime);
		}
	}
}
