package se.federspiel.android.game.constraints;

import se.federspiel.android.game.geometry.Point;
import se.federspiel.android.game.geometry.Vector2;
import se.federspiel.android.game.interfaces.IConstraint;
import se.federspiel.android.game.sprites.AbstractConstraintObject;
import se.federspiel.android.game.utils.AMath;

public class RigidLinkConstraint implements IConstraint
{
    private AbstractConstraintObject mBaseOne = null;
    private AbstractConstraintObject mBaseTwo = null;

    private float mDistance = 0.0f;
    
    private Vector2 mTmpDistance = Vector2.Zero.clone();
    private Vector2 mTmpVector = Vector2.Zero.clone();
    private Point mTmpPosition = Point.Zero.clone();
    
	public RigidLinkConstraint(AbstractConstraintObject baseOne, AbstractConstraintObject baseTwo)
	{
		assert baseOne != null;
		assert baseTwo!= null;
    	
		mBaseOne = baseOne;
		mBaseTwo = baseTwo;
	}

	public void setLinkDistance(float distance)
	{
		mDistance = distance;
	}
	
	@Override
	public void update()
	{
		AMath.subtract(mBaseTwo.getMassCenter(),
			mBaseOne.getMassCenter(), mTmpDistance);
		
		float distance = mTmpDistance.getMagnitude();

		mTmpDistance.normalize();
		
		float correction = mDistance - distance;
			
		float correctionBaseTwo = correction 
				* (1 - mBaseTwo.getPhysicalProperties().massRelation(mBaseOne.getPhysicalProperties()));

		mTmpVector.set(mTmpDistance);
		mTmpVector.scale(correctionBaseTwo);

		mTmpPosition.set(mBaseTwo.getPosition());
		mTmpPosition.move(mTmpVector.X, mTmpVector.Y);
		mBaseTwo.setPosition(mTmpPosition);
		
		mTmpVector.set(mTmpDistance);
		mTmpVector.scale(-(correction - correctionBaseTwo));
			
		mTmpPosition.set(mBaseOne.getPosition());
		mTmpPosition.move(mTmpVector.X, mTmpVector.Y);
		mBaseOne.setPosition(mTmpPosition);
	}
}
