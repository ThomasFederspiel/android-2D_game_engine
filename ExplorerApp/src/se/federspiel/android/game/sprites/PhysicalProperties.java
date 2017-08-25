package se.federspiel.android.game.sprites;

public class PhysicalProperties
{
	public float mFriction = 0.0f;
	private float mMass = 0.0f;
	private float mInvMass = 0.0f;
	private float mRestitution = 1.0f;
	
	public float getRestitution()
	{
		return mRestitution;
	}
	
	public float getMass()
	{
		return mMass;
	}

	public float getInvMass()
	{
		return mInvMass;
	}

	public void setMass(float mass)
	{
		mMass = mass < 0 ? 0 : mass;
		
		if (mass > 0)
		{
			mInvMass = 1 / mass;
		}
		else
		{
			mInvMass = 0;
		}
	}
	
	public void setRestitution(float restitution)
	{
		mRestitution = restitution < 0.0f ? 0 : (restitution > 1.0f ? 1.0f : restitution);
	}

	public boolean isMassive()
	{
		return (mMass <= 0.0f);
	}

	public float massRelation(PhysicalProperties properties)
	{
		float sum = mMass + properties.mMass;
		
		// Both massive
		if (sum == 0)
		{
			return 0.5f;
		}
		else
		{
			if (mMass == 0)
			{
				return 1.0f;
			}
			else if (properties.mMass == 0)
			{
				return 0.0f;
			}
			else
			{
				return (mMass / sum);
			}
		}
	}

	
	public boolean hasFriction()
	{
		return (mFriction > 0.0f);
	}
}
