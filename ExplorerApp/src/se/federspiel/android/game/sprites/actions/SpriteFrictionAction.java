package se.federspiel.android.game.sprites.actions;

import se.federspiel.android.game.geometry.Vector2;
import se.federspiel.android.game.interfaces.ICollisionSprite;
import se.federspiel.android.game.interfaces.ISpriteCollisionObject;
import se.federspiel.android.game.interfaces.ISpriteCollisionObject.ICollisionContext;

public class SpriteFrictionAction extends AbstractSpriteAction
{
	private Vector2 mSpriteSpeed = Vector2.Zero.clone();
	private Vector2 mColliderSpeed = Vector2.Zero.clone();
	private Vector2 mNormal = Vector2.Zero.clone();
	
	@Override
	public void onSpriteCollision(ICollisionSprite sprite, ICollisionContext context,
			ISpriteCollisionObject collidingObject)
	{	
		if (collidingObject.getPhysicalProperties().hasFriction())
		{
			float friction = collidingObject.getPhysicalProperties().mFriction;
			
			mSpriteSpeed.set(sprite.getSpeed());
	    	mColliderSpeed.set(collidingObject.getSpeed());
			
	    	mNormal = context.getCollisionNormal().getNormal(mNormal);
	    	
	    	float colliderFrictionSpeed = mNormal.dot(mColliderSpeed);
	    	
	    	mSpriteSpeed.add(mNormal.scale(friction * colliderFrictionSpeed));
	    	
	        sprite.getTrajectory().setSpeed(mSpriteSpeed);
		}
		
        super.onSpriteCollision(sprite, context, collidingObject);
	}
}
