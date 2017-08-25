package se.federspiel.android.game.sprites.actions;

import se.federspiel.android.game.geometry.Vector2;
import se.federspiel.android.game.interfaces.ICollisionSprite;
import se.federspiel.android.game.interfaces.ISpriteCollisionObject;
import se.federspiel.android.game.interfaces.ISpriteCollisionObject.ICollisionContext;
import se.federspiel.android.game.utils.AMath;

public class SpriteBounceAction extends AbstractSpriteAction
{
	private Vector2 mSpeedV12 = Vector2.Zero.clone();
	
	public SpriteBounceAction()
	{
	}
	
	//	v1' = v1 - f / m1 * n;
	//	f = (1 + e) * v12 * n / ( 1 / m1 + 1 / m2) * n * n
	
	// m1 = massive
	// v1' = v1;
	// v2' = v2 - f / m2 * n
	// f = (1 + e) * v21 * n / (1 / m2) * n * n
	
	
	@Override
	public void onSpriteCollision(ICollisionSprite sprite, ICollisionContext context,
			ISpriteCollisionObject collidingObject)
	{
		Vector2 myNormal = context.getCollisionNormal();
		
    	Vector2 mySpeed = sprite.getTrajectory().getSpeed();
    	
		Vector2 collisionerSpeed = collidingObject.getSpeed();
		
		if (sprite.getCollisionObject().getPhysicalProperties().isMassive()
				&& collidingObject.getPhysicalProperties().isMassive())
		{
			mySpeed.set(0, 0);
		}
		else if (sprite.getCollisionObject().getPhysicalProperties().isMassive())
		{
			// Keep speed
		}
		else if (collidingObject.getPhysicalProperties().isMassive() || collidingObject.isStationary())
		{
			// m2 = massive
			// v1' = v1 - f / m1 * n
			// f = (1 + e) * v12 * n / (1 / m1) * n * n
			
			mSpeedV12.set(mySpeed);
			mSpeedV12.subtract(collisionerSpeed);
			
			float normalDot = AMath.dot(myNormal, myNormal);
			float invMassSum = sprite.getCollisionObject().getPhysicalProperties().getInvMass();

			float f = ((1 + sprite.getCollisionObject().getPhysicalProperties().getRestitution())
				* AMath.dot(mSpeedV12, myNormal)) / (invMassSum * normalDot);
			
			mySpeed.subtract(myNormal.scale(f * sprite.getCollisionObject().getPhysicalProperties().getInvMass()));
		}
		else
		{
			// Formula :
			//	v1' = v1 - f / m1 * n;
			//	f = (1 + e) * v12 * n / (1 / m1 + 1 / m2) * n * n

			mSpeedV12.set(mySpeed);
			mSpeedV12.subtract(collisionerSpeed);
			
			float normalDot = AMath.dot(myNormal, myNormal);
			float invMassSum = collidingObject.getPhysicalProperties().getInvMass() 
				+ sprite.getCollisionObject().getPhysicalProperties().getInvMass();

			float f = ((1 + sprite.getCollisionObject().getPhysicalProperties().getRestitution())
				* AMath.dot(mSpeedV12, myNormal)) / (invMassSum * normalDot);
			
			mySpeed.subtract(myNormal.scale(f * sprite.getCollisionObject().getPhysicalProperties().getInvMass()));
		}
		
    	sprite.getTrajectory().setSpeed(mySpeed);

        sprite.getTrajectory().setPosition(context.getCorrectedMovement().getEndPosition());
        
        super.onSpriteCollision(sprite, context, collidingObject);
	}
}
