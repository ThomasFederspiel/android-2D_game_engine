package se.federspiel.android.game.constraints;

import se.federspiel.android.game.geometry.Point;
import se.federspiel.android.game.geometry.Vector2;
import se.federspiel.android.game.interfaces.IConstraint;
import se.federspiel.android.game.sprites.AbstractConstraintObject;
import se.federspiel.android.game.utils.AMath;

public class SpringConstraint implements IConstraint
{
    private AbstractConstraintObject mSpringBaseObject = null;
    private AbstractConstraintObject mSpringPullObject = null;
    
    private float mDamping = 0.0f;
    private float mSpringConstant = 0.0f;
    
    // px
    private float mRestingDistance = 0.0f;

    // px
    private float mIncompressibleDistance = 0.0f;
    
    private Vector2 mTmpDistance = Vector2.Zero.clone();
    private Vector2 mTmpVector = Vector2.Zero.clone();
    private Vector2 mTmpForce = Vector2.Zero.clone();
    private Point mTmpPosition = Point.Zero.clone();
    
	public SpringConstraint(AbstractConstraintObject base, AbstractConstraintObject pull)
	{
		assert base != null;
		assert pull != null;
    	
		mSpringBaseObject = base;
		mSpringPullObject = pull;
	}

	public void setSpringFactors(float springConstant, float restingDistance)
	{
		mSpringConstant = springConstant;
		mRestingDistance = restingDistance;
	}
	
	public void setDamping(float damping)
	{
		assert damping >= 0;
		
		mDamping = damping;
	}

	public void setIncompressible(float incompressibleDistance)
	{
		assert incompressibleDistance > 0;
		
		mIncompressibleDistance = incompressibleDistance;
	}

	// F = -k(|x|-d)(x/|x|) - bv
	
	@Override
	public void update()
	{
		AMath.subtract(mSpringPullObject.getMassCenter(),
				mSpringBaseObject.getMassCenter(), mTmpDistance);
		
		float distance = mTmpDistance.getMagnitude();

		float damping = mDamping;

		boolean isIncompressible = false;
		
		if ((mIncompressibleDistance > 0) && (mIncompressibleDistance >= distance))
		{
			damping = 0;
			
			isIncompressible = true;
		}
		
		mTmpDistance.normalize();

		float speed = 0;

		if (damping > 0)
		{
			mTmpVector.set(mSpringPullObject.getSpeed());
			mTmpVector.subtract(mSpringBaseObject.getSpeed());
	
			speed = mTmpDistance.dot(mTmpVector);
		}
		
		float f = -mSpringConstant * (distance - mRestingDistance) - (damping * speed);
		
		mTmpForce.set(mTmpDistance);
		mTmpForce.scale(f);
		
		mSpringPullObject.addForce(mTmpForce);

		mTmpForce.reverse();
		
		mSpringBaseObject.addForce(mTmpForce);
		
		if (isIncompressible)
		{
			float correction = mIncompressibleDistance - distance;
			
			float correctionPull = correction 
					* (1 - mSpringPullObject.getPhysicalProperties().massRelation(mSpringBaseObject.getPhysicalProperties()));

			mTmpVector.set(mTmpDistance);
			mTmpVector.scale(correctionPull);
			
			mTmpPosition.set(mSpringPullObject.getPosition());
			mTmpPosition.move(mTmpVector.X, mTmpVector.Y);
			mSpringPullObject.setPosition(mTmpPosition);
			
			float speedAlongString = mTmpDistance.dot(mSpringPullObject.getSpeed());
			
			if (speedAlongString < 0)
			{
				mTmpVector.set(mTmpDistance);
				mTmpVector.scale(speedAlongString);
	
				mSpringPullObject.getSpeed().subtract(mTmpVector);
			}
			
			mTmpVector.set(mTmpDistance);
			mTmpVector.scale(-(correction - correctionPull));
			
			mTmpPosition.set(mSpringBaseObject.getPosition());
			mTmpPosition.move(mTmpVector.X, mTmpVector.Y);
			mSpringBaseObject.setPosition(mTmpPosition);
			
			speedAlongString = mTmpDistance.dot(mSpringBaseObject.getSpeed());
			
			if (speedAlongString > 0)
			{
				mTmpVector.set(mTmpDistance);
				mTmpVector.scale(speedAlongString);

				mSpringBaseObject.getSpeed().subtract(mTmpVector);
			}
		}
	}
}
