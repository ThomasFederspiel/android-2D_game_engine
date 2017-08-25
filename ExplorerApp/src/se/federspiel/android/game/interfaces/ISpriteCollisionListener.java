package se.federspiel.android.game.interfaces;

import se.federspiel.android.game.interfaces.ISpriteCollisionObject.ICollisionContext;

public interface ISpriteCollisionListener
{
	/**
	 * @param sprite The primary colliding sprite. 
	 * @param context Information about the collision between the sprites in the perspective of the primary sprite
	 * @param collidingObject The secondary sprite colliding with the primary sprite.
	 */
	public void onSpriteCollision(ICollisionSprite sprite, ICollisionContext context, ISpriteCollisionObject collidingObject);
}
