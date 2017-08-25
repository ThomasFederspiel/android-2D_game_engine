package se.federspiel.android.game.collision;

import java.util.ArrayList;

import se.federspiel.android.game.CollisionManager.CollisionContext;
import se.federspiel.android.game.interfaces.ISpriteCollisionObject;
import se.federspiel.android.game.interfaces.ICollisionManager.ICollisionEvaluator;
import se.federspiel.android.game.interfaces.ICollisionManager.ICollisionLibrary;
import se.federspiel.android.game.interfaces.ICollisionManager.ICollisionSelector;
import se.federspiel.android.game.interfaces.ISpriteCollisionObject.ICollisionContext;

public class CollisionEvaluatorLibrary
{
	private static void onSpriteCollision(ISpriteCollisionObject collisionObjectOne,
			ICollisionContext contextOne,
			ISpriteCollisionObject collisionObjectTwo,
			ICollisionContext contextTwo)
	{
		collisionObjectOne.onSpriteCollision(contextOne, collisionObjectTwo);
    	collisionObjectTwo.onSpriteCollision(contextTwo, collisionObjectOne);
	}
	
    private static boolean canCollide(ICollisionLibrary library, ISpriteCollisionObject collisionObjectOne, ISpriteCollisionObject collisionObjectTwo)
    {
    	boolean canCollide = false;
    	
    	if (!collisionObjectOne.isStationary() || !collisionObjectTwo.isStationary())
    	{
    		if (library.isCollisionSetEnabled())
    		{
    			CollisionSet setOne = collisionObjectOne.getCollisionSet();
    			CollisionSet setTwo = collisionObjectTwo.getCollisionSet();

    			assert setOne != null;
    			assert setTwo != null;
    			
    			canCollide = setOne.canCollide(setTwo);
    		}
    		else
    		{
    			canCollide = true;
    		}
    	}
    	
    	return canCollide;
    }
	
	private static void filterCollisionSets(ArrayList<ISpriteCollisionObject> objects, ISpriteCollisionObject collisioner)
	{
		CollisionSet setCollisioner = collisioner.getCollisionSet();
		
		assert setCollisioner != null;
		
		for (int i = (objects.size() - 1); i >= 0; i--)
		{
			CollisionSet set = objects.get(i).getCollisionSet();

			assert set != null;
			
			if (!setCollisioner.canCollide(set))
    		{
				objects.remove(i);
    		}
		}
	}
	
    public static class FirstFoundCollisionEvaluator implements ICollisionEvaluator
    {
		@Override
		public void evaluate(ICollisionLibrary library, ArrayList<ISpriteCollisionObject> objects, 
    			ICollisionSelector selector)
		{
	        for (int i = 0; i < (objects.size() - 1); i++)
	        {
	        	ISpriteCollisionObject collisionObjectOne = objects.get(i);
	    		ICollisionContext contextOne = collisionObjectOne.getCollisionContext();

	            for (int j = i + 1; j < objects.size(); j++)
	            {
	            	ISpriteCollisionObject collisionObjectTwo = objects.get(j);
	        		ICollisionContext contextTwo = collisionObjectTwo.getCollisionContext();

	                // ;+
	                //ALog.debug(this, "collisionObjectOne.isStationary() = " + collisionObjectOne.isStationary());
	        		//ALog.debug(this, "collisionObjectTwo.isStationary() = " + collisionObjectTwo.isStationary());
	                
	                if (canCollide(library, collisionObjectOne, collisionObjectTwo))
	                {
		                if (library.isColliding(collisionObjectOne, collisionObjectTwo, contextOne, contextTwo))
		                {
		            		library.evaluateRigidity(collisionObjectOne, contextOne, collisionObjectTwo, contextTwo);
		            		
//		            		ALog.debug(this, "contextOne = " + contextOne.getCorrectedMovement());
//		            		ALog.debug(this, "contextTwo = " + contextTwo.getCorrectedMovement());
		            		
		                	onSpriteCollision(collisionObjectOne, contextOne, collisionObjectTwo, contextTwo);
		                	
		                	continue;
		                }
	                }
	        	}
	        }
		}
    }
    
    public static class NearestFoundCollisionEvaluator implements ICollisionEvaluator
    {
    	private ICollisionContext mContextOne = new CollisionContext();
    	private ICollisionContext mContextTwo = new CollisionContext();
    	
		@Override
		public void evaluate(ICollisionLibrary library, ArrayList<ISpriteCollisionObject> objects, 
    			ICollisionSelector selector)
		{
        	ISpriteCollisionObject foundObjectOne = null;
        	ISpriteCollisionObject foundObjectTwo = null;

			float minMagnitude = Float.MAX_VALUE;
			
			for (int i = 0; i < (objects.size() - 1); i++)
	        {
	        	ISpriteCollisionObject collisionObjectOne = objects.get(i);

	            for (int j = i + 1; j < objects.size(); j++)
	            {
	            	ISpriteCollisionObject collisionObjectTwo = objects.get(j);

	                if (canCollide(library, collisionObjectOne, collisionObjectTwo))
	                {
		                if (library.isColliding(collisionObjectOne, collisionObjectTwo, mContextOne, mContextTwo))
		                {
		                	float magnitude = mContextOne.getCorrectedMovement().getMagnitude();
		                	magnitude += mContextTwo.getCorrectedMovement().getMagnitude();
		                	
		                	if (magnitude < minMagnitude)
		                	{
		                		minMagnitude = magnitude;
		                		
		                		foundObjectOne = collisionObjectOne;
		                		foundObjectOne.getCollisionContext().set(mContextOne);
		                		
		                		foundObjectTwo = collisionObjectTwo;
		                		foundObjectTwo.getCollisionContext().set(mContextTwo);
		                	}
		                }
	            	}
	        	}
	        }
			
			if (foundObjectOne != null)
			{
        		library.evaluateRigidity(foundObjectOne, foundObjectOne.getCollisionContext(), foundObjectTwo, foundObjectTwo.getCollisionContext());
        		
            	onSpriteCollision(foundObjectOne, foundObjectOne.getCollisionContext(), foundObjectTwo, foundObjectTwo.getCollisionContext());
			}
		}
		
    }

    public static class NearestFoundCollisionSelectorEvaluator implements ICollisionEvaluator
    {
    	private ICollisionContext mContextOne = new CollisionContext();
    	private ICollisionContext mContextTwo = new CollisionContext();

    	private ArrayList<ISpriteCollisionObject> mSelectedObjects = new ArrayList<ISpriteCollisionObject>();

    	private ArrayList<ISpriteCollisionObject> mAlreadyCheckedObjects = new ArrayList<ISpriteCollisionObject>();
    	
		@Override
		public void evaluate(ICollisionLibrary library, ArrayList<ISpriteCollisionObject> objects, 
    			ICollisionSelector selector)
		{
        	ISpriteCollisionObject foundObjectOne = null;
        	ISpriteCollisionObject foundObjectTwo = null;

			float minMagnitude = Float.MAX_VALUE;
			
			mAlreadyCheckedObjects.clear();
			
			for (int i = 0; i < objects.size(); i++)
	        {
	        	ISpriteCollisionObject collisionObjectOne = objects.get(i);

	        	if (!collisionObjectOne.isStationary())
	        	{
	        		mSelectedObjects.clear();
	        		
	        		mSelectedObjects = selector.select(mSelectedObjects, collisionObjectOne);
	        		
	        		mSelectedObjects.removeAll(mAlreadyCheckedObjects);
	        		
	        		if (library.isCollisionSetEnabled())
	        		{
	        			filterCollisionSets(mSelectedObjects, collisionObjectOne);
	        		}
	        		
		            for (int j = 0; j < mSelectedObjects.size(); j++)
		            {
		            	ISpriteCollisionObject collisionObjectTwo = mSelectedObjects.get(j);

		                if (library.isColliding(collisionObjectOne, collisionObjectTwo, mContextOne, mContextTwo))
		                {
		            		library.evaluateRigidity(collisionObjectOne, mContextOne, collisionObjectTwo, mContextTwo);
		            		
		                	float magnitude = mContextOne.getCorrectedMovement().getMagnitude();
		                	magnitude += mContextTwo.getCorrectedMovement().getMagnitude();

//		                	ALog.debug(this, "corr magnitude = " + magnitude);
		                	
		                	if (magnitude < minMagnitude)
		                	{
		                		minMagnitude = magnitude;
		                		
		                		foundObjectOne = collisionObjectOne;
		                		foundObjectOne.getCollisionContext().set(mContextOne);
		                		
		                		foundObjectTwo = collisionObjectTwo;
		                		foundObjectTwo.getCollisionContext().set(mContextTwo);
		                	}
		                }
	            	}
		        	
		        	mAlreadyCheckedObjects.add(collisionObjectOne);
	        	}
	        }

			if (foundObjectOne != null)
			{
            	onSpriteCollision(foundObjectOne, foundObjectOne.getCollisionContext(), foundObjectTwo, foundObjectTwo.getCollisionContext());
			}
		}
    }
    
    public static class RepeatNearestFoundCollisionSelectorEvaluator implements ICollisionEvaluator
    {
    	private ICollisionContext mContextOne = new CollisionContext();
    	private ICollisionContext mContextTwo = new CollisionContext();

    	private ArrayList<ISpriteCollisionObject> mSelectedObjects = new ArrayList<ISpriteCollisionObject>();

    	private ArrayList<ISpriteCollisionObject> mAlreadyCheckedObjects = new ArrayList<ISpriteCollisionObject>();
    	
    	private int mCollisionCounter = 0;
    	
		@Override
		public void evaluate(ICollisionLibrary library, ArrayList<ISpriteCollisionObject> objects, 
    			ICollisionSelector selector)
		{
			mAlreadyCheckedObjects.clear();

			for (int i = 0; i < objects.size(); i++)
	        {
	        	ISpriteCollisionObject collisionObjectOne = objects.get(i);

	        	if (!collisionObjectOne.isStationary())
	        	{
	        		do
	        		{
	        			float minMagnitude = Float.MAX_VALUE;
	        			
		            	ISpriteCollisionObject foundObjectOne = null;
		            	ISpriteCollisionObject foundObjectTwo = null;
	
		    			mCollisionCounter = 0;
		    			
		        		mSelectedObjects.clear();
		        		
		        		mSelectedObjects = selector.select(mSelectedObjects, collisionObjectOne);
		        
		        		mSelectedObjects.removeAll(mAlreadyCheckedObjects);
		        		
		        		if (library.isCollisionSetEnabled())
		        		{
		        			filterCollisionSets(mSelectedObjects, collisionObjectOne);
		        		}
		        		
			            for (int j = 0; j < mSelectedObjects.size(); j++)
			            {
			            	ISpriteCollisionObject collisionObjectTwo = mSelectedObjects.get(j);
	
			                if (library.isColliding(collisionObjectOne, collisionObjectTwo, mContextOne, mContextTwo))
			                {
				    			mCollisionCounter++;
				    			
			            		library.evaluateRigidity(collisionObjectOne, mContextOne, collisionObjectTwo, mContextTwo);
			            		
			                	float magnitude = mContextOne.getCorrectedMovement().getMagnitude();
			                	magnitude += mContextTwo.getCorrectedMovement().getMagnitude();
			            
//			                	ALog.debug(this, "mCollisionCounter = " + mCollisionCounter);
//			                	ALog.debug(this, "Collide collisionObjectOne = " + collisionObjectOne);
//			                	ALog.debug(this, "Collide collisionObjectTwo = " + collisionObjectTwo);
//			                	ALog.debug(this, "mContextOne.getCorrectedMovement() = " + mContextOne.getCorrectedMovement());
//			                	ALog.debug(this, "mContextTwo.getCorrectedMovement() = " + mContextTwo.getCorrectedMovement());
//			                	ALog.debug(this, "Magnitude = " + magnitude);
			                	
			                	if (magnitude < minMagnitude)
			                	{
			                		minMagnitude = magnitude;
			                		
			                		foundObjectOne = collisionObjectOne;
			                		foundObjectOne.getCollisionContext().set(mContextOne);
			                		
			                		foundObjectTwo = collisionObjectTwo;
			                		foundObjectTwo.getCollisionContext().set(mContextTwo);
			                	}
			                }
		            	}
			        	
						if (foundObjectOne != null)
						{
				        	mAlreadyCheckedObjects.add(foundObjectTwo);
				        	
			            	onSpriteCollision(foundObjectOne, foundObjectOne.getCollisionContext(), foundObjectTwo, foundObjectTwo.getCollisionContext());
						}
	        		}
	        		while (mCollisionCounter > 0);
	        		
		        	mAlreadyCheckedObjects.add(collisionObjectOne);
	        	}
	        }
		}
    }
}
 