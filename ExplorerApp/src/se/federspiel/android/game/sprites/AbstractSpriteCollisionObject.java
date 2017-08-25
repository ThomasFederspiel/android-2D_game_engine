package se.federspiel.android.game.sprites;

import java.util.ArrayList;

import se.federspiel.android.game.CollisionManager.CollisionContext;
import se.federspiel.android.game.collision.CollisionSet;
import se.federspiel.android.game.geometry.Point;
import se.federspiel.android.game.interfaces.ISpriteCollisionObject;

public abstract class AbstractSpriteCollisionObject extends AbstractConstraintObject implements ISpriteCollisionObject
{
	private CollisionContext mCollisionContext = new CollisionContext();
	
	private ArrayList<ISpritePositionChangedListener> mListeners = new ArrayList<ISpritePositionChangedListener>();
    
	private CollisionSet mCollisionSet = null;
	
	public abstract PhysicalProperties getPhysicalProperties();
	public abstract void setPhysicalProperties(PhysicalProperties properties);

	@Override
	public void setOnSpritePositionChangedListener(ISpritePositionChangedListener listener)
    {
    	assert !mListeners.contains(listener);
    	
    	if (!mListeners.contains(listener))
    	{
    		mListeners.add(listener);
    	}
    }
	
	@Override
	public void removeOnSpritePositionChangedListener(ISpritePositionChangedListener listener)
    {
    	assert mListeners.contains(listener);
    	
    	if (mListeners.contains(listener))
    	{
    		mListeners.remove(listener);
    	}
    }

	@Override
	public ICollisionContext getCollisionContext()
	{
		return mCollisionContext;
	}

	@Override
    public void setCollisionSet(CollisionSet set)
	{
		mCollisionSet = set;
	}
    
	@Override
	public CollisionSet getCollisionSet()
	{
		return mCollisionSet;
	}

    protected void notifySpritePositionChanged(Point oldPosition, Point newPosition)
    {
    	int nofListeners = mListeners.size();
    	
    	if (nofListeners > 0)
    	{
	    	if (!oldPosition.isSame(newPosition))
	    	{
		    	for (int i = 0; i < nofListeners; i++)
		    	{
		    		ISpritePositionChangedListener listener = mListeners.get(i);
		    		
		            listener.onSpritePositionChanged(this, oldPosition, newPosition);
		        }
	    	}
    	}
    }
}
