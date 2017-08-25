package se.federspiel.android.game.sprites.actions;

import se.federspiel.android.game.geometry.Point;
import se.federspiel.android.game.geometry.Vector2;
import se.federspiel.android.game.interfaces.ICollisionSprite;
import se.federspiel.android.game.interfaces.ISpriteCollisionObject;
import se.federspiel.android.game.interfaces.ISpriteCollisionObject.ICollisionContext;
import se.federspiel.android.game.utils.AMath;

public class SpriteStickAction extends AbstractSpriteAction
{
	public enum StickDirection
	{
		ALL_DIRECTIONS,
		NORMAL_DIRECTION
	}

	private StickDirection mStickDirection = StickDirection.ALL_DIRECTIONS;

	private Vector2 mNormal = Vector2.Zero.clone();
	private Vector2 mSpeed = Vector2.Zero.clone();
	private Vector2 mOrgMovement = Vector2.Zero.clone();

	private Point mGCOnSpriteCollisionPoint = Point.Zero.clone();
	
	public void setStickDirection(StickDirection direction)
	{
		mStickDirection = direction;
	}
	
	@Override
	public void onSpriteCollision(ICollisionSprite sprite, ICollisionContext context,
			ISpriteCollisionObject collidingObject)
	{	

        switch (mStickDirection)
        {
	        case ALL_DIRECTIONS :
	    		sprite.getTrajectory().setPosition(context.getCorrectedMovement().getEndPosition());
	    		
	            sprite.getTrajectory().setSpeed(0, 0);
	        	break;
	        	
	        case NORMAL_DIRECTION :
	        	
	        	// Eliminate speed in normal direction
	        	mNormal.set(context.getCollisionNormal());
	        	
	        	mSpeed.set(sprite.getTrajectory().getSpeed());

	        	float nSpeed = mSpeed.dot(mNormal);
	        	
	        	mSpeed.subtract(mNormal.scale(nSpeed));
	        	
	            sprite.getTrajectory().setSpeed(mSpeed);

	            // Eliminate movement parallel with normal
	            mOrgMovement.set(sprite.getCollisionObject().getLastUpdateMovementRay().getVector());

	            mOrgMovement.subtract(context.getCorrectedMovement().getVector());

	        	mNormal.set(context.getCollisionNormal());
	            
	        	float nDelta = mOrgMovement.dot(mNormal);
	            
	            mOrgMovement.set(sprite.getCollisionObject().getLastUpdateMovementRay().getVector());
	            
	            mOrgMovement.subtract(mNormal.scale(nDelta));
	            
	    		AMath.add(sprite.getCollisionObject().getLastUpdateMovementRay().getStartPosition(), mOrgMovement, mGCOnSpriteCollisionPoint);
	    		
	    		sprite.getTrajectory().setPosition(mGCOnSpriteCollisionPoint);
	        	
	            break;
        }
        
        super.onSpriteCollision(sprite, context, collidingObject);
	}
}
