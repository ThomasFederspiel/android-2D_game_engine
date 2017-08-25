package se.federspiel.android.game.sprites.actions;

import se.federspiel.android.game.geometry.Vector2;
import se.federspiel.android.game.interfaces.ICollisionSprite;
import se.federspiel.android.game.interfaces.ISprite;
import se.federspiel.android.game.interfaces.ISpriteCollisionObject;
import se.federspiel.android.game.interfaces.ISpriteCollisionObject.ICollisionContext;
import se.federspiel.android.game.utils.AMath;
import se.federspiel.android.util.ALog;

public class SpriteElasticBounceAction extends AbstractSpriteAction
{
	private Vector2 mSpeedV12 = Vector2.Zero.clone();
	private Vector2 mMyNormal = Vector2.Zero.clone();
	private Vector2 mMySpeed = Vector2.Zero.clone();
	
	public SpriteElasticBounceAction()
	{
	}
	
	@Override
	public boolean isYielding(ISprite sprite)
	{
		return !sprite.getCollisionObject().getPhysicalProperties().isMassive();
	}
	
	@Override
	public void onSpriteCollision(ICollisionSprite sprite, ICollisionContext context,
			ISpriteCollisionObject collidingObject)
	{
		mMyNormal.set(context.getCollisionNormal());

		mMySpeed.set(sprite.getTrajectory().getSpeed());
    	
		Vector2 collisionerSpeed = collidingObject.getLastUpdateSpeed();
//		Vector2 collisionerSpeed = collidingObject.getSpeed();
		
		float restitution = Math.min(sprite.getCollisionObject().getPhysicalProperties().getRestitution(),
				collidingObject.getPhysicalProperties().getRestitution());
		
		if (sprite.getCollisionObject().getPhysicalProperties().isMassive()
				&& collidingObject.getPhysicalProperties().isMassive())
		{
			ALog.debug(this, "Massive vs Massive");
			
			// Formula :
			//	v1' = v1 - f  * n;
			//	f = (1 + e) * v12 * n / (2 * n * n)

			mSpeedV12.set(mMySpeed);
			mSpeedV12.subtract(collisionerSpeed);
			
			float normalDot = AMath.dot(mMyNormal, mMyNormal);
			float invMassSum = 2;

			float f = ((1 + restitution) * AMath.dot(mSpeedV12, mMyNormal)) / (invMassSum * normalDot);
			
			mMySpeed.subtract(mMyNormal.scale(f));
		}
		else if (sprite.getCollisionObject().getPhysicalProperties().isMassive())
		{
			ALog.debug(this, "Massive vs weak");
			// Keep speed
		}
		else if (collidingObject.getPhysicalProperties().isMassive()) 
			// ;+ || collidingObject.isStationary())
		{
			if (collidingObject.getPhysicalProperties().isMassive()) 
			{
				ALog.debug(this, "Weak vs Massive");
			}
//	;+		else if (collidingObject.isStationary())
//			{
//				ALog.debug(this, "Weak vs Stationary");
//			}
			
			// m2 = massive
			// v1' = v1 - f / m1 * n
			// f = (1 + e) * v12 * n / (1 / m1) * n * n
			
			mSpeedV12.set(mMySpeed);
			mSpeedV12.subtract(collisionerSpeed);
			
			float normalDot = AMath.dot(mMyNormal, mMyNormal);
			float invMassSum = sprite.getCollisionObject().getPhysicalProperties().getInvMass();

			float f = ((1 + restitution) * AMath.dot(mSpeedV12, mMyNormal)) / (invMassSum * normalDot);
			
			mMySpeed.subtract(mMyNormal.scale(f * sprite.getCollisionObject().getPhysicalProperties().getInvMass()));
		}
		else
		{
			ALog.debug(this, "Mass " + sprite.getCollisionObject().getPhysicalProperties().getMass() 
					+ " vs Mass " + collidingObject.getPhysicalProperties().getMass());

			ALog.debug(this, "Corection(" + Integer.toHexString(sprite.hashCode())+ ") = " 
					+ context.getCorrectedMovement().getVector());

			// Formula :
			//	v1' = v1 - f / m1 * n;
			//	f = (1 + e) * v12 * n / (1 / m1 + 1 / m2) * n * n

			mSpeedV12.set(mMySpeed);
			mSpeedV12.subtract(collisionerSpeed);
			
			float normalDot = AMath.dot(mMyNormal, mMyNormal);
			float invMassSum = collidingObject.getPhysicalProperties().getInvMass() 
				+ sprite.getCollisionObject().getPhysicalProperties().getInvMass();

			float f = ((1 + restitution) * AMath.dot(mSpeedV12, mMyNormal)) / (invMassSum * normalDot);
			
//			ALog.debug(this, "f(" + this + ")" + f);
			
			mMySpeed.subtract(mMyNormal.scale(f * sprite.getCollisionObject().getPhysicalProperties().getInvMass()));
		}
		
    	sprite.getTrajectory().setSpeed(mMySpeed);

        sprite.getTrajectory().setPosition(context.getCorrectedMovement().getEndPosition());
        
        super.onSpriteCollision(sprite, context, collidingObject);
	}

	/* ;+	@Override
	public void onSpriteCollision(ICollisionSprite sprite, ICollisionContext context,
			ISpriteCollisionObject collidingObject)
	{
		Vector2 myNormal = context.getCollisionNormal();
		
    	Vector2 mySpeed = sprite.getTrajectory().getSpeed();
    	
		Vector2 collisionerSpeed = collidingObject.getLastUpdateSpeed();
		
		// Colliding with fixed object, which cause bounce
		if (collidingObject.getPhysicalProperties().isMassive() || collidingObject.isStationary())
		{
			// Formula :
			// m2 = infinite =>
			// V1 = V1 - 2 * a1
			
			float a1 = mySpeed.dot(myNormal);
			
			mySpeed.subtract(myNormal.clone().scale(2 * a1));
		}
		else if (!collidingObject.isYielding())
		{
			float a1 = mySpeed.dot(myNormal);
			float a2 = collisionerSpeed.dot(myNormal);
			
			if (Math.abs(a1) >= Math.abs(a2))
			{
				mySpeed.subtract(myNormal.clone().scale(2 * a1));
			}
			else
			{
				mySpeed.subtract(myNormal.clone().scale(a1-a2));
			}
		} 
		else
		{
			// Colliding with object that will bounce, which causes bounce
			
			// Formula :
			// a1 = V1 (this) projection on N
			// a2 = V2 (collisioner) projection on N
			// m1 = weight (this) 
			// m2 = weight (collisioner) 
			// V1 = V1 - 2 * m2 * (a1 - a2) / (m1 + m2) * N
			
			int m1 = 1;
			int m2 = 1;
			float a1 = mySpeed.dot(myNormal);
			float a2 = collisionerSpeed.dot(myNormal);
			
			mySpeed.subtract(myNormal.clone().scale(2 * m2 * (a1 - a2) / (m1 + m2)));
		}
		
    	sprite.getTrajectory().setSpeed(mySpeed);

        sprite.getTrajectory().setPosition(context.getCorrectedMovement().getEndPosition());
        
        super.onSpriteCollision(sprite, context, collidingObject);
	} */
}
