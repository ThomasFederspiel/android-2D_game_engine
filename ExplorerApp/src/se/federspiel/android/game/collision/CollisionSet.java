package se.federspiel.android.game.collision;

public class CollisionSet
{
	private long mSetMask = 0;
	private long mNoCollisionWithinSet = 0;
	
	private long mJoinedSetMask = 0;
	
	public CollisionSet(long mask)
	{
		mSetMask = mask;
		mNoCollisionWithinSet = 0;
		
		mJoinedSetMask = mSetMask;
	}

	public boolean canCollide(CollisionSet set)
	{
		return (~(mSetMask & set.mSetMask & mNoCollisionWithinSet) 
			& ((mJoinedSetMask & set.mSetMask) | (mSetMask & set.mJoinedSetMask))) != 0;
	}

	public void setCollisionsWithinSet(boolean enable)
	{
		if (enable)
		{
			mNoCollisionWithinSet = 0;
		}
		else
		{
			mNoCollisionWithinSet = mSetMask;
		}
	}
	
	public void joinSet(CollisionSet set)
	{
		mJoinedSetMask |= set.mSetMask;
	}
	
	public void disjoinSet(CollisionSet set)
	{
		mJoinedSetMask &= ~set.mSetMask;
	}
}
