package se.federspiel.android.game.sprites.actions;

import se.federspiel.android.game.interfaces.ICollisionSprite;
import se.federspiel.android.game.interfaces.ISpriteCollisionObject;
import se.federspiel.android.game.interfaces.ISpriteCollisionObject.ICollisionContext;

public class SpriteAdjustPositionAction extends AbstractSpriteAction
{
	@Override
	public void onSpriteCollision(ICollisionSprite sprite, ICollisionContext context,
			ISpriteCollisionObject collidingObject)
	{	
        sprite.getTrajectory().setPosition(context.getCorrectedMovement().getEndPosition());
        
        super.onSpriteCollision(sprite, context, collidingObject);
	}
}
