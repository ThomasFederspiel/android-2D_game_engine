package se.federspiel.android.game;

import java.util.ArrayList;

import se.federspiel.android.game.collision.CollisionEvaluatorLibrary.FirstFoundCollisionEvaluator;
import se.federspiel.android.game.collision.CollisionSelectorLibrary.ListCollisionSelector;
import se.federspiel.android.game.collision.CollisionSet;
import se.federspiel.android.game.collision.bounds.CollisionBoundingBox;
import se.federspiel.android.game.collision.bounds.CollisionBoundingCircle;
import se.federspiel.android.game.collision.bounds.CollisionBoundingLine;
import se.federspiel.android.game.geometry.Point;
import se.federspiel.android.game.geometry.Ray;
import se.federspiel.android.game.geometry.Rectangle;
import se.federspiel.android.game.geometry.Vector2;
import se.federspiel.android.game.interfaces.IBounds;
import se.federspiel.android.game.interfaces.ICollisionBound;
import se.federspiel.android.game.interfaces.ICollisionManager;
import se.federspiel.android.game.interfaces.ICollisionManager.ICollisionLibrary;
import se.federspiel.android.game.interfaces.ISpriteCollisionObject;
import se.federspiel.android.game.interfaces.ISpriteCollisionObject.ICollisionContext;
import se.federspiel.android.game.utils.AMath;
import se.federspiel.android.util.ALog;

import com.example.explorerapp.AInstrumentation;

public class CollisionManager implements ICollisionManager, ICollisionLibrary
{
    private static final int VORONOI_RECT_INSIDE = 0;	// 0000
    private static final int VORONOI_RECT_LEFT = 1;   // 0001
    private static final int VORONOI_RECT_RIGHT = 2;  // 0010
    private static final int VORONOI_RECT_BOTTOM = 4; // 0100
    private static final int VORONOI_RECT_TOP = 8;    // 1000

    private static final float VORONOI_EPS = 0.00005f;
    
	public enum IntersectionEnum
	{
		INTERSECTION_INSIDE,
		INTERSECTION_OUTSIDE,
		INTERSECTION_INTERSECT
	}

	private long mCollisionSetMaskGenerator = 1;
	private boolean mCollisionSetsEnabled = false;
	
    private ArrayList<ISpriteCollisionObject> mSpriteCollisionObjects = new ArrayList<ISpriteCollisionObject>();
    private GameContext mGameContext = null;

    private ICollisionEvaluator mCollisionEvaluator = new FirstFoundCollisionEvaluator();
    private ICollisionSelector mCollisionSelector = new ListCollisionSelector();
    
    private GCManagerContext mGCContext = new GCManagerContext();

    private Statistics mStatisticts = new Statistics();
    
    public CollisionManager(GameContext gameContext)
    {
        mGameContext = gameContext;
    }
 
	@Override
	public CollisionSet createCollisionSet()
	{
		CollisionSet set = new CollisionSet(mCollisionSetMaskGenerator);
		
		mCollisionSetMaskGenerator <<= 1;
		
		return set;
	}

	@Override
	public void enableCollisionSets(boolean enable)
	{
		mCollisionSetsEnabled = enable;
	}
	
	@Override
	public boolean isCollisionSetEnabled()
	{
		return mCollisionSetsEnabled;
	}
	
	@Override
    public void add(ISpriteCollisionObject collisionObject)
    {
        mSpriteCollisionObjects.add(collisionObject);
        
        mCollisionSelector.add(collisionObject);
    }

	@Override
    public void remove(ISpriteCollisionObject collisionObject)
    {
        mSpriteCollisionObjects.remove(collisionObject);
        
        mCollisionSelector.remove(collisionObject);
    }

	@Override
    public void update()
    {
		if (AInstrumentation.TRACK_COLLISION_STATISTICS)
		{
			if (AInstrumentation.LOG_COLLISION_STATISTICS)
			{
				mStatisticts.mTimeToLogCounter--;
				
				if (mStatisticts.mTimeToLogCounter <= 0)
				{
					ALog.debug(this, "Collision Manager Statistics -----");
					ALog.debug(this, "Invokations = " + mStatisticts.mNofManagerInvokations);
					ALog.debug(this, "Total evaluations = " + mStatisticts.mNofCollisionsEvaluated);
					ALog.debug(this, "Average evaluations = " + (mStatisticts.mNofCollisionsEvaluated / mStatisticts.mNofManagerInvokations));
					ALog.debug(this, "Total collisions = " + mStatisticts.mNofActualCollisions);
					ALog.debug(this, "Average collisions (x1000) = " + 1000 * (mStatisticts.mNofActualCollisions / (float) mStatisticts.mNofManagerInvokations));
					
					mStatisticts.mTimeToLogCounter = AInstrumentation.LOG_COLLISION_INTERVAL;
				}
			}
	    	
	    	mStatisticts.mNofManagerInvokations++;
		}
		
		evaluateCollisions();
    }
    
	@Override
	public void setCollisionEvaluator(ICollisionEvaluator evaluator)
	{
		mCollisionEvaluator = evaluator;
	}

	@Override
	public void setCollisionSelector(ICollisionSelector selector)
	{
		mCollisionSelector = selector;
	}
	
	@Override
	public void destroy()
	{
	    mGameContext = null;
	    mCollisionEvaluator = null;
	    mGCContext = null;

	    mCollisionSelector.clear();
	    mCollisionSelector = null;
	    
	    mSpriteCollisionObjects.clear();
	    mSpriteCollisionObjects = null;
	}
	
    private void evaluateCollisions()
    {
//		ALog.debug(this, "mCollisionEvaluator.evaluate() start -----------------");
		
    	mCollisionEvaluator.evaluate(this, mSpriteCollisionObjects, mCollisionSelector);
    	
//		ALog.debug(this, "mCollisionEvaluator.evaluate() end -----------------");
    }

	@Override
    public boolean isColliding(ISpriteCollisionObject collisionObjectOne, ISpriteCollisionObject collisionObjectTwo,
    		ICollisionContext contextOne, ICollisionContext contextTwo)
    {
    	boolean collision = false;

        ICollisionBound boundsOne = collisionObjectOne.getCollisionBounds();
        ICollisionBound boundsTwo = collisionObjectTwo.getCollisionBounds();

		if (AInstrumentation.TRACK_COLLISION_STATISTICS)
		{
	    	mStatisticts.mNofCollisionsEvaluated++;
		}
		
        // ;+
        //ALog.debug(this, "isColliding()");
        
        if (boundsOne instanceof CollisionBoundingBox) 
        {
        	if (boundsTwo instanceof CollisionBoundingBox)
        	{
                // ;+
        		//ALog.debug(this, "isColliding(Rect vs rect)");
                
                collision = areRectanglesCollidingSweep(collisionObjectOne, collisionObjectTwo, (CollisionBoundingBox) boundsOne, (CollisionBoundingBox) boundsTwo,
                		contextOne, contextTwo);
        	}
        	else if (boundsTwo instanceof CollisionBoundingCircle)
        	{
                // ;+
        		//ALog.debug(this, "isColliding(Rect vs circle)");
                
                collision = areCircleRectangleCollidingSweep(collisionObjectTwo, collisionObjectOne, (CollisionBoundingCircle) boundsTwo,
                		(CollisionBoundingBox) boundsOne, contextTwo, contextOne);
        	}
        } 
        else if (boundsOne instanceof CollisionBoundingCircle)
        {
        	if (boundsTwo instanceof CollisionBoundingCircle)
        	{
                // ;+
        		// ALog.debug(this, "isColliding(Circle vs circle)");
                
                collision = areCirclesCollidingSweep(collisionObjectOne, collisionObjectTwo, (CollisionBoundingCircle) boundsOne, (CollisionBoundingCircle) boundsTwo,
                		contextOne, contextTwo);
        	}
            else if (boundsTwo instanceof CollisionBoundingLine)
            {
                // ;+
            	// ALog.debug(this, "isColliding(Circle vs line)");
                
	            collision = areCircleLineCollidingSweep(collisionObjectOne, collisionObjectTwo, (CollisionBoundingCircle) boundsOne, (CollisionBoundingLine) boundsTwo,
	            		contextOne, contextTwo);
            }
        	else if (boundsTwo instanceof CollisionBoundingBox)
        	{
                // ;+
        		//ALog.debug(this, "isColliding(Circle vs Rect)");
                
                collision = areCircleRectangleCollidingSweep(collisionObjectOne, collisionObjectTwo, (CollisionBoundingCircle) boundsOne,
                		(CollisionBoundingBox) boundsTwo, contextOne, contextTwo);
        	}
        }
        else if (boundsOne instanceof CollisionBoundingLine)
        {
            if (boundsTwo instanceof CollisionBoundingLine)
            {
            }
            else if (boundsTwo instanceof CollisionBoundingCircle)
            {
                // ;+
            	// ALog.debug(this, "isColliding(Line vs circle)");
                
	            collision = areCircleLineCollidingSweep(collisionObjectTwo, collisionObjectOne, (CollisionBoundingCircle) boundsTwo, (CollisionBoundingLine) boundsOne,
	            		contextTwo, contextOne);
            }
        }
        
		if (AInstrumentation.TRACK_COLLISION_STATISTICS)
		{
			if (collision)
			{
		    	mStatisticts.mNofActualCollisions++;
			}
		}
		
	    return collision;
	}

	@Override
	public void evaluateRigidity(ISpriteCollisionObject collisionObjectOne,
			ICollisionContext contextOne,
			ISpriteCollisionObject collisionObjectTwo,
			ICollisionContext contextTwo)
	{
		if (collisionObjectOne.isYielding() && !collisionObjectTwo.isYielding())
		{
			Vector2 orgRayVectorTwo = mGCContext.mGCVector1.set(collisionObjectTwo.getLastUpdateMovementRay().getVector());

			orgRayVectorTwo.subtract(contextTwo.getCorrectedMovement().getVector());

			Vector2 normal = mGCContext.mGCVector2.set(contextOne.getCollisionNormal());
			
			normal.scale(normal.dot(orgRayVectorTwo));

			contextOne.getCorrectedMovement().addVector(normal);
			contextTwo.setCorrectedMovement(collisionObjectTwo.getLastUpdateMovementRay());
			
//	    	ALog.debug(this, "rigid, contextOne = " + contextOne.getCorrectedMovement());
//	    	ALog.debug(this, "rigid, contextTwo = " + contextTwo.getCorrectedMovement());
		}
		else if (!collisionObjectOne.isYielding() && collisionObjectTwo.isYielding())
		{
			Vector2 orgRayVetorOne = mGCContext.mGCVector1.set(collisionObjectOne.getLastUpdateMovementRay().getVector());

			orgRayVetorOne.subtract(contextOne.getCorrectedMovement().getVector());

			Vector2 normal = mGCContext.mGCVector2.set(contextTwo.getCollisionNormal());
			
			normal.scale(normal.dot(orgRayVetorOne));

			contextTwo.getCorrectedMovement().addVector(normal);
			contextOne.setCorrectedMovement(collisionObjectOne.getLastUpdateMovementRay());
			
//	    	ALog.debug(this, "rigid, contextOne = " + contextOne.getCorrectedMovement());
//	    	ALog.debug(this, "rigid, contextTwo = " + contextTwo.getCorrectedMovement());
		}
	}
		
    private boolean areRectanglesCollidingSweep(ISpriteCollisionObject collisionObjectOne, ISpriteCollisionObject collisionObjectTwo,
    		CollisionBoundingBox collisionBoundOne, CollisionBoundingBox collisionBoundTwo,
    		ICollisionContext contextOne, ICollisionContext contextTwo)
    {
    	boolean collision = false;

		Ray movementRayOne = mGCContext.mGCRay1.set(collisionObjectOne.getLastUpdateMovementRay());
		Ray movementRayTwo = mGCContext.mGCRay2.set(collisionObjectTwo.getLastUpdateMovementRay());

//		ALog.debug(this, "movementRayOne = " + movementRayOne);
//		ALog.debug(this, "movementRayTwo = " + movementRayTwo);
		
		movementRayOne.subtractVector(movementRayTwo.getVector());

		CollisionBoundingBox boundTwo = mGCContext.mGCBoundingBox1.set(collisionBoundTwo);

		boundTwo.setPosition(movementRayTwo.getStartPosition());
		
		Rectangle rectangle = AMath.minkowskiSumTopLeft(boundTwo.getBoundingBox(), collisionBoundOne.getBoundingBox(),
				mGCContext.mGCRectangle1);

		Point intersectPosition = mGCContext.mGCPoint1;

		Vector2 contactNormalOne = mGCContext.mGCVector1;
		
		IntersectionEnum status = cohenSutherlandRectangleClip(rectangle, movementRayOne, intersectPosition, contactNormalOne);
    
		if (status == IntersectionEnum.INTERSECTION_INTERSECT)
		{
//			ALog.debug(this, "Intersect status = " + status);
//			ALog.debug(this, "intersectPosition = " + intersectPosition.toString());
//			ALog.debug(this, "contactNormalOne = " + contactNormalOne.toString());
//
//			ALog.debug(this, "movementRayOne = " + collisionObjectOne.getLastUpdateMovementRay());
//			ALog.debug(this, "movementRayTwo = " + collisionObjectTwo.getLastUpdateMovementRay());
//			ALog.debug(this, "boundTwo.getBoundingBox(), = " + boundTwo.getBoundingBox());
//			ALog.debug(this, "collisionBoundOne.getBoundingBox() = " + collisionBoundOne.getBoundingBox());
//			ALog.debug(this, "Minkowski rect = " + rectangle.toString());
			
			float factor = AMath.distance(movementRayOne.getStartPosition(), intersectPosition) / movementRayOne.getMagnitude();
			
			movementRayOne = mGCContext.mGCRay1.set(collisionObjectOne.getLastUpdateMovementRay());
			
	    	movementRayOne.scale(factor);
	    	movementRayTwo.scale(factor);
	    	
	    	Vector2 contactNormalTwo = mGCContext.mGCVector2.set(contactNormalOne);
	    			
	    	contactNormalTwo.reverse();
	    	
	    	contextOne.setCollisionNormal(contactNormalOne);
	    	contextOne.setCorrectedMovement(movementRayOne);
	    	
	    	contextTwo.setCollisionNormal(contactNormalTwo);
	    	contextTwo.setCorrectedMovement(movementRayTwo);
	    	
	    	collision = true;
		}
		else if (status == IntersectionEnum.INTERSECTION_INSIDE)
		{
//			ALog.debug(this, "Intersect status = " + status);

			if (!movementRayOne.getVector().isZero())
			{
				Ray backoutRayOne = mGCContext.mGCRay3.set(movementRayOne);
				
//				ALog.debug(this, "before backoutRayOne = " + backoutRayOne);
//				ALog.debug(this, "before backoutRayOne.getEndPosition() = " + backoutRayOne.getEndPosition());

				float backoutDistance = 2 * Math.max(rectangle.getWidth(), rectangle.getHeight());
			   
//				ALog.debug(this, "backoutDistance = " + backoutDistance);
//				ALog.debug(this, "backoutRayOne.getVector() = " + backoutRayOne.getVector());
//				ALog.debug(this, "backoutRayOne.getVector().getMagnitude() = " + backoutRayOne.getVector().getMagnitude());
			
				backoutRayOne.moveStartPosition(backoutDistance);
			
//				ALog.debug(this, "backoutRayOne.getVector().getMagnitude() = " + backoutRayOne.getVector().getMagnitude());
//				ALog.debug(this, "backoutRayOne.getVector() = " + backoutRayOne.getVector());
			
//				ALog.debug(this, "after backoutRayOne = " + backoutRayOne);
			
//				ALog.debug(this, "movementRayOne = " + collisionObjectOne.getLastUpdateMovementRay());
//				ALog.debug(this, "movementRayTwo = " + collisionObjectTwo.getLastUpdateMovementRay());
//				ALog.debug(this, "boundTwo.getBoundingBox(), = " + boundTwo.getBoundingBox());
//				ALog.debug(this, "collisionBoundOne.getBoundingBox() = " + collisionBoundOne.getBoundingBox());
//				ALog.debug(this, "Minkowski rect = " + rectangle.toString());
				
				status = cohenSutherlandRectangleClip(rectangle, backoutRayOne, intersectPosition, contactNormalOne);
				
				if (status == IntersectionEnum.INTERSECTION_INTERSECT)
				{
//					ALog.debug(this, "Intersect status = " + status);
//					ALog.debug(this, "intersectPosition = " + intersectPosition.toString());
//					ALog.debug(this, "contactNormalOne = " + contactNormalOne.toString());
					
					float factor = -AMath.distance(movementRayOne.getStartPosition(), intersectPosition) / movementRayOne.getMagnitude();
					
					movementRayOne = mGCContext.mGCRay1.set(collisionObjectOne.getLastUpdateMovementRay());
					
//					ALog.debug(this, "before movementRayOne = " + movementRayOne);
//					ALog.debug(this, "before movementRayTwo = " + movementRayTwo);
				
			    	movementRayOne.scale(factor);
			    	movementRayTwo.scale(factor);
		    	
//			    	ALog.debug(this, "after movementRayOne = " + movementRayOne);
//			    	ALog.debug(this, "after movementRayTwo = " + movementRayTwo);
				
			    	Vector2 contactNormalTwo = mGCContext.mGCVector2.set(contactNormalOne);
			    			
			    	contactNormalTwo.reverse();
			    	
			    	contextOne.setCollisionNormal(contactNormalOne);
			    	contextOne.setCorrectedMovement(movementRayOne);
			    	
			    	contextTwo.setCollisionNormal(contactNormalTwo);
			    	contextTwo.setCorrectedMovement(movementRayTwo);
			    	
			    	collision = true;
				}
				else
				{
					if (status == IntersectionEnum.INTERSECTION_INSIDE)
					{
						ALog.debug(this, "INTERSECTION_INSIDE - 2");
					}
					else
					{
						ALog.debug(this, "INTERSECTION_OUTSIDE - 2");
					}
				}
			}
			else
			{
				ALog.debug(this, "INTERSECTION_INSIDE - No movement");
			}
		}
		else
		{
			// OUTSIDE, no collision
		}
		
	    return collision;
    }
    
    private boolean areCircleRectangleCollidingSweep(ISpriteCollisionObject collisionObjectCircle, ISpriteCollisionObject collisionObjectRect, 
    		CollisionBoundingCircle collisionBoundCircle, CollisionBoundingBox collisionBoundRect, 
    		ICollisionContext contextCircle, ICollisionContext contextRect)
    {
    	boolean collision = false;

		Ray movementRayCircle = mGCContext.mGCRay1.set(collisionObjectCircle.getLastUpdateMovementRay());
		Ray movementRayRect = mGCContext.mGCRay2.set(collisionObjectRect.getLastUpdateMovementRay());
		
//		ALog.debug(this, "1st: movementRayCircle = " + collisionObjectCircle.getLastUpdateMovementRay());
//		ALog.debug(this, "1st: movementRayBox = " + collisionObjectRect.getLastUpdateMovementRay());
		
		movementRayCircle.subtractVector(movementRayRect.getVector());

		CollisionBoundingBox boundRect = mGCContext.mGCBoundingBox1.set(collisionBoundRect);
		
		boundRect.setPosition(movementRayRect.getStartPosition());
		
		Rectangle rectangle = AMath.minkowskiSum(boundRect.getBoundingBox(), collisionBoundCircle.getBoundingCircle(),
				mGCContext.mGCRectangle1);

		// ALog.debug(this, "Minkowski rect = " + rectangle.toString());
		
		Point intersectPosition = mGCContext.mGCPoint1;

		Vector2 contactNormalCircle = mGCContext.mGCVector1;
		
		IntersectionEnum status = cohenSutherlandRectangleClip(rectangle, movementRayCircle, intersectPosition, contactNormalCircle);
    
		if (status == IntersectionEnum.INTERSECTION_INTERSECT)
		{
//			ALog.debug(this, "Intersect status = " + status);
//			ALog.debug(this, "intersectPosition = " + intersectPosition.toString());
//			ALog.debug(this, "contactNormalCircle = " + contactNormalCircle.toString());
//			
//			ALog.debug(this, "movementRayCircle = " + collisionObjectCircle.getLastUpdateMovementRay());
//			ALog.debug(this, "movementRayBox = " + collisionObjectRect.getLastUpdateMovementRay());
			
			float factor = AMath.distance(movementRayCircle.getStartPosition(), intersectPosition) / movementRayCircle.getMagnitude();
			
//			ALog.debug(this, "factor = " + factor);
			
			movementRayCircle = mGCContext.mGCRay1.set(collisionObjectCircle.getLastUpdateMovementRay());
			
	    	movementRayCircle.scale(factor);
	    	movementRayRect.scale(factor);
	    	
	    	Vector2 contactNormalRect = mGCContext.mGCVector2.set(contactNormalCircle);
	    			
	    	contactNormalRect.reverse();
	    	
//	    	ALog.debug(this, "after movementRayCircle = " + movementRayCircle);
//	    	ALog.debug(this, "after movementRayRect = " + movementRayRect);
	    	
	    	contextCircle.setCollisionNormal(contactNormalCircle);
	    	contextCircle.setCorrectedMovement(movementRayCircle);
	    	
	    	contextRect.setCollisionNormal(contactNormalRect);
	    	contextRect.setCorrectedMovement(movementRayRect);
	    	
	    	collision = true;
		}
		else if (status == IntersectionEnum.INTERSECTION_INSIDE)
		{
//			ALog.debug(this, "Intersect status = " + status);
//			
//			ALog.debug(this, "movementRayCirle = " + collisionObjectCircle.getLastUpdateMovementRay());
//			ALog.debug(this, "movementRayBox = " + collisionObjectRect.getLastUpdateMovementRay());
//			ALog.debug(this, "movementRaySum = " + movementRayCircle);
			
			if (!movementRayCircle.getVector().isZero())
			{
				Ray backoutRayCircle = mGCContext.mGCRay3.set(movementRayCircle);
				
				float backoutDistance = 2 * Math.max(rectangle.getWidth(), rectangle.getHeight());
				
//				ALog.debug(this, "backoutDistance = " + backoutDistance);
				
				backoutRayCircle.moveStartPosition(backoutDistance);
			
//				ALog.debug(this, "backoutRayCircle = " + backoutRayCircle);
				
				status = cohenSutherlandRectangleClip(rectangle, backoutRayCircle, intersectPosition, contactNormalCircle);
			
				if (status == IntersectionEnum.INTERSECTION_INTERSECT)
				{
//					ALog.debug(this, "Intersect status = " + status);
//					ALog.debug(this, "intersectPosition = " + intersectPosition.toString());
//					ALog.debug(this, "contactNormalCircle = " + contactNormalCircle.toString());
				
					float factor = -AMath.distance(movementRayCircle.getStartPosition(), intersectPosition) / movementRayCircle.getMagnitude();
				
					movementRayCircle = mGCContext.mGCRay1.set(collisionObjectCircle.getLastUpdateMovementRay());
				
//					ALog.debug(this, "before movementRayCircle = " + movementRayCircle);
//					ALog.debug(this, "before movementRayRect = " + movementRayRect);
				
			    	movementRayCircle.scale(factor);
			    	movementRayRect.scale(factor);
		    	
//			    	ALog.debug(this, "after movementRayCircle = " + movementRayCircle);
//			    	ALog.debug(this, "after movementRayRect = " + movementRayRect);
				
			    	Vector2 contactNormalRect = mGCContext.mGCVector2.set(contactNormalCircle);
			    			
			    	contactNormalRect.reverse();
			    	
			    	contextCircle.setCollisionNormal(contactNormalCircle);
			    	contextCircle.setCorrectedMovement(movementRayCircle);
			    	
			    	contextRect.setCollisionNormal(contactNormalRect);
			    	contextRect.setCorrectedMovement(movementRayRect);
			    	
			    	collision = true;
				}
				else
				{
					if (status == IntersectionEnum.INTERSECTION_INSIDE)
					{
						ALog.debug(this, "INTERSECTION_INSIDE - 2");
					}
					else
					{
						ALog.debug(this, "INTERSECTION_OUTSIDE - 2");
					}
				}
			}
			else
			{
				ALog.debug(this, "INTERSECTION_INSIDE - No movement");
			}
		}
		else
		{
			// OUTSIDE, no collision
		}
		
	    return collision;
    }

    private boolean areCirclesColliding(CollisionBoundingCircle circleOne, CollisionBoundingCircle circleTwo)
    {
    	boolean collision = false;
    	
    	collision = circleOne.intersects(circleTwo);
        
	    return collision;
    }
    
    private boolean areCirclesCollidingSweep(ISpriteCollisionObject collisionObjectOne, ISpriteCollisionObject collisionObjectTwo,
    		CollisionBoundingCircle collisionBoundOne, CollisionBoundingCircle collisionBoundTwo,
    		ICollisionContext contextOne, ICollisionContext contextTwo)
    {
		Ray movementRayOne = mGCContext.mGCRay1.set(collisionObjectOne.getLastUpdateMovementRay());
		Ray movementRayTwo = mGCContext.mGCRay2.set(collisionObjectTwo.getLastUpdateMovementRay());

		movementRayOne.subtractVector(movementRayTwo.getVector());
		
		// Early Escape test: if the length of the movevec is less
		// than distance between the centers of these circles minus 
		// their radii, there's no way they can hit. 
		float circleDistance = AMath.distance(movementRayOne.getStartPosition(), movementRayTwo.getStartPosition());
		int circlesRadiiSum = (collisionBoundOne.getRadius() + collisionBoundTwo.getRadius());
		
		if (movementRayOne.getMagnitude() < (circleDistance - circlesRadiiSum))
		{
		   return false;
		} 
		
		// Normalize the movevec
		Vector2 N = mGCContext.mGCVector1.set(movementRayOne.getVector());

		N.normalize();
    	
		// Find C, the vector from the center of the moving 
		// circle A to the center of B
//		Vector2 C = movementRayTwo.getStartPosition().subtract(movementRayOne.getStartPosition(), mGCContext.mGCVector2);
		Vector2 C = AMath.subtract(movementRayTwo.getStartPosition(), movementRayOne.getStartPosition(), mGCContext.mGCVector2);
    	
    	// D = N . C = ||C|| * cos(angle between N and C)
    	float D = N.dot(C);

    	// Another early escape: Make sure that A is moving 
    	// towards B! If the dot product between the movevec and 
    	// B.center - A.center is less that or equal to 0, 
    	// A isn't isn't moving towards B
    	if (D <= 0)
    	{
    	  return false;
    	} 
    	 
    	// Find the length of the vector C
    	float lengthC = C.getMagnitude();

    	float F = (lengthC * lengthC) - (D * D);
     	
    	// Escape test: if the closest that A will get to B 
    	// is more than the sum of their radii, there's no 
    	// way they are going collide
    	float sumRadiiSquared = circlesRadiiSum * circlesRadiiSum;

    	if (F >= sumRadiiSquared)
    	{
    	  return false;
    	}
     	
    	// We now have F and sumRadii, two sides of a right triangle. 
    	// Use these to find the third side, sqrt(T)
    	float T = sumRadiiSquared - F;
     	
    	// If there is no such right triangle with sides length of 
    	// sumRadii and sqrt(f), T will probably be less than 0. 
    	// Better to check now than perform a square root of a 
    	// negative number. 
    	if (T < 0)
    	{
    	  return false;
    	}
     	
    	// Therefore the distance the circle has to travel along 
    	// movevec is D - sqrt(T)
    	float distance =  D - (float) Math.sqrt(T); 
     	
    	// Get the magnitude of the movement vector
    	float mag = movementRayOne.getMagnitude();

    	// Finally, make sure that the distance A has to move 
    	// to touch B is not greater than the magnitude of the 
    	// movement vector. 
    	if (mag < distance)
    	{
    	  return false;
    	} 
     	
    	// Set the length of the movevec so that the circles will just 
    	// touch
    	float factor = distance / movementRayOne.getMagnitude();
    	
		movementRayOne = mGCContext.mGCRay1.set(collisionObjectOne.getLastUpdateMovementRay());
		
    	movementRayOne.scale(factor);
    	movementRayTwo.scale(factor);
    	
    	Point positionOne = movementRayOne.getEndPosition();
    	Point positionTwo = movementRayTwo.getEndPosition();
    	
    	Vector2 contactNormalTwo = AMath.subtract(positionTwo, positionOne, mGCContext.mGCVector3);
    	
    	contactNormalTwo.normalize();
    	
    	Vector2 contactNormalOne = mGCContext.mGCVector1.set(contactNormalTwo);
    			
    	contactNormalOne.reverse();
    	
    	contextOne.setCollisionNormal(contactNormalOne);
    	contextOne.setCorrectedMovement(movementRayOne);
    	
    	contextTwo.setCollisionNormal(contactNormalTwo);
    	contextTwo.setCorrectedMovement(movementRayTwo);

	    return true;
	}

    private boolean areCircleLineCollidingSweep(ISpriteCollisionObject collisionObjectCircle, ISpriteCollisionObject collisionObjectLine,
    		CollisionBoundingCircle circle, CollisionBoundingLine line,
    		ICollisionContext contextCircle, ICollisionContext contextLine)
    {
    	boolean collision = false;

//    	ALog.debug(this, "-----------------------------");
    	
		Ray movementRayCircle = mGCContext.mGCRay1.set(collisionObjectCircle.getLastUpdateMovementRay());
		Ray movementRayLine = mGCContext.mGCRay2.set(collisionObjectLine.getLastUpdateMovementRay());
		
		// ALog.debug(this, "movementRayLine.getStartPosition() = " + movementRayLine.getStartPosition());
    	
		movementRayCircle.subtractVector(movementRayLine.getVector());

		// Vector to start of circle movevec
//    	Vector2 startLineCircle = movementRayCircle.getStartPosition().subtract(movementRayLine.getStartPosition(), mGCContext.mGCVector3); 
    	Vector2 startLineCircle = AMath.subtract(movementRayCircle.getStartPosition(), movementRayLine.getStartPosition(), mGCContext.mGCVector3);

		// Vector to end of circle movevec
//    	Vector2 endLineCircle = movementRayCircle.getEndPosition().subtract(movementRayLine.getStartPosition(), mGCContext.mGCVector4); 
    	Vector2 endLineCircle = AMath.subtract(movementRayCircle.getEndPosition(), movementRayLine.getStartPosition(), mGCContext.mGCVector4);

    	Vector2 lineNormal = line.getNormal();

//		ALog.debug(this, "startLineCircle = " + startLineCircle);
//		ALog.debug(this, "endLineCircle = " + endLineCircle);
//		ALog.debug(this, "movementRayLine.start = " + movementRayLine.getStartPosition());
//		ALog.debug(this, "line.dir = " + line.getDirection());
//		ALog.debug(this, "lineNormal = " + lineNormal);
    	
    	// Distance between start of movevec perpendicular to line
    	float startDist = lineNormal.dot(startLineCircle);

    	// Neg distance start of movevec on other side of line
    	if (startDist < 0)
    	{
    		lineNormal = mGCContext.mGCVector1.set(lineNormal);
    		
    		lineNormal.reverse();
    		
        	startDist = -startDist;
    	}
    	
    	// Distance between end of movevec perpendicular to line
    	float endDist = lineNormal.dot(endLineCircle);

//    	ALog.debug(this, "startDist = " + startDist);
//    	ALog.debug(this, "endDist = " + endDist);
    	
    	// Moving towards line and end might have passed not away from it
    	if (startDist > endDist)
    	{
//	    	ALog.debug(this, "startDist > endDist");
	    	
    		// Find how long to move along movevec to reach line 
	    	float t = (circle.getRadius() - startDist) / (endDist - startDist);

	    	// ALog.debug(this, "movementRayCircle.getMagnitude() = " + movementRayCircle.getMagnitude());
//	    	ALog.debug(this, "circle.getRadius() = " + circle.getRadius());
//	    	ALog.debug(this, "t = " + t);
	    	
	    	// Check within start and end of movevec
	    	if ((t >= 0.0) && (t <= 1.0))
	    	{
//		    	ALog.debug(this, "0 < t < 1");
		    	
	    		movementRayCircle = mGCContext.mGCRay1.set(collisionObjectCircle.getLastUpdateMovementRay());

	    		Point closestEndPoint = mGCContext.mGCPoint1;
	    		
		    	movementRayCircle.scale(t); 
		    	movementRayLine.scale(t); 
		    	
	    		// Within line segment
	    		if (withinLineSegment(movementRayLine.getStartPosition(), line.getDirection(), movementRayCircle.getStartPosition(), closestEndPoint))
	    		{
//	    			ALog.debug(this, "Within line segment");
	    			
			    	contextCircle.setCollisionNormal(lineNormal);
			    	contextCircle.setCorrectedMovement(movementRayCircle);
			    	
			    	Vector2 reverseNormal = mGCContext.mGCVector2.set(lineNormal);
			    	
			    	reverseNormal.reverse();
			    	
			    	contextLine.setCollisionNormal(reverseNormal);
			    	contextLine.setCorrectedMovement(movementRayLine);
			    	
			    	collision = true;
	    		}
	    		else
	    		{
//	    			ALog.debug(this, "Not within, Check endpoint collision");
	    			
	    			// Check endpoint collision
	    			movementRayCircle = mGCContext.mGCRay1.set(collisionObjectCircle.getLastUpdateMovementRay());
	    			Ray movementRayPoint = mGCContext.mGCRay3.set(closestEndPoint, collisionObjectLine.getLastUpdateMovementRay().getVector());
	    			
	    		    collision = areCirclePointColliding(movementRayCircle, movementRayPoint, circle, contextCircle, contextLine);
	    	
	    		    if (collision)
	    		    {
	    		    	contextLine.setCorrectedMovement(new Ray(movementRayLine.getStartPosition(), contextLine.getCorrectedMovement().getVector()));
	    		    }
	    		}
		    }
	    	else if (t < 0.0)
	    	{
//		    	ALog.debug(this, "t < 0");
		    	
	    		movementRayCircle = mGCContext.mGCRay1.set(collisionObjectCircle.getLastUpdateMovementRay());
	    		
	    		Point closestEndPoint = mGCContext.mGCPoint1;
	    		
		    	movementRayCircle.scale(t); 
		    	movementRayLine.scale(t); 
		    	
	    		// Within line segment
	    		if (withinLineSegment(movementRayLine.getStartPosition(), line.getDirection(), movementRayCircle.getStartPosition(), closestEndPoint))
	    		{
//	    			ALog.debug(this, "Within line segment");
	    			
			    	contextCircle.setCollisionNormal(lineNormal);
			    	contextCircle.setCorrectedMovement(movementRayCircle);
			    	
			    	Vector2 reverseNormal = mGCContext.mGCVector2.set(lineNormal);
			    	
			    	reverseNormal.reverse();
			    	
			    	contextLine.setCollisionNormal(reverseNormal);
			    	contextLine.setCorrectedMovement(movementRayLine);
			    	
			    	collision = true;
	    		}
	    		else
	    		{
//	    			ALog.debug(this, "Not within line segment, check endpoint");
	    			
	    			// Check endpoint collision
	    			movementRayCircle = mGCContext.mGCRay1.set(collisionObjectCircle.getLastUpdateMovementRay());
	    			Ray movementRayPoint = mGCContext.mGCRay3.set(closestEndPoint, collisionObjectLine.getLastUpdateMovementRay().getVector());
	    			
	    		    collision = areCirclePointColliding(movementRayCircle, movementRayPoint,
	    		    		circle, contextCircle, contextLine);
	    	
	    		    if (collision)
	    		    {
	    		    	contextLine.setCorrectedMovement(new Ray(movementRayLine.getStartPosition(), contextLine.getCorrectedMovement().getVector()));
	    		    }
	    		}
	    	}
    	}
       	else if (startDist <= circle.getRadius())
    	{
//        	ALog.debug(this, "startDist <= circle.getRadius()");
//	    	ALog.debug(this, "circle.getRadius() = " + circle.getRadius());
        	
    		Point closestEndPoint = mGCContext.mGCPoint1;
    		
    		// Not line segment
    		if (!withinLineSegment(movementRayLine.getStartPosition(), line.getDirection(), movementRayCircle.getStartPosition(), closestEndPoint))
    		{
//    			ALog.debug(this, "Not within line segment, check endpoint");
    			
    			// Check endpoint collision
    			movementRayCircle = mGCContext.mGCRay1.set(collisionObjectCircle.getLastUpdateMovementRay());
    			Ray movementRayPoint = mGCContext.mGCRay3.set(closestEndPoint, collisionObjectLine.getLastUpdateMovementRay().getVector());
    			
    		    collision = areCirclePointColliding(movementRayCircle, movementRayPoint,
    		    		circle, contextCircle, contextLine);
    	
    		    if (collision)
    		    {
    		    	contextLine.setCorrectedMovement(new Ray(movementRayLine.getStartPosition(), contextLine.getCorrectedMovement().getVector()));
    		    }
    		}
    	}

//    	if (collision)
//    	{
//    		ALog.debug(this, "COLLISION FOUND");
//    	}
    	
//    	ALog.debug(this, "-----------------------------");
    	
	    return collision;
    }
    
	private boolean withinLineSegment(Point lineStartPosition, Vector2 lineDirection, Point pointToVerify, Point closestLineEndPoint)
	{
		boolean within = false;
		
//		Vector2 lineToPoint = pointToVerify.subtract(lineStartPosition, mGCContext.mGCSubCallVector1);
		Vector2 lineToPoint = AMath.subtract(pointToVerify, lineStartPosition, mGCContext.mGCSubCallVector1);
		
		float projection = mGCContext.mGCSubCallVector2.set(lineDirection).normalize().dot(lineToPoint);

//		ALog.debug(this, "withinLineSegment");
		
		// ALog.debug(this, "lineDirectionNormal = " + lineDirection.clone().normalize().toString());
		// ALog.debug(this, "lineDirection = " + lineDirection.toString());
		// ALog.debug(this, "lineStartPosition = " + lineStartPosition.toString());
//		ALog.debug(this, "lineDirection.getMagnitude() = " + lineDirection.getMagnitude());
//		ALog.debug(this, "Projection = " + projection);
		
		if (projection < 0)
		{
			closestLineEndPoint.X = lineStartPosition.X;
			closestLineEndPoint.Y = lineStartPosition.Y;
		}
		else if (projection > lineDirection.getMagnitude())
		{
			AMath.add(lineStartPosition, lineDirection, closestLineEndPoint);
		}
		else
		{
			within = true;
		}
		
		return within;
	}
		
    private boolean areCirclePointColliding(Ray movementRayCircle, Ray movementRayPoint,
    		CollisionBoundingCircle circle,
    		ICollisionContext contextCircle, ICollisionContext contextPoint)
    {
    	boolean collision = false;

//    	ALog.debug(this, "Check point-circle collisiom");
    	
    	Ray movementRayCircleClone = mGCContext.mGCSubCallRay1.set(movementRayCircle);
    	
    	movementRayCircleClone.subtractVector(movementRayPoint.getVector());
    	
//    	Vector2 startCirclePoint = movementRayPoint.getStartPosition().subtract(movementRayCircleClone.getStartPosition(), mGCContext.mGCSubCallVector1);
    	Vector2 startCirclePoint = AMath.subtract(movementRayPoint.getStartPosition(), movementRayCircleClone.getStartPosition(), mGCContext.mGCSubCallVector1);
    	
    	// ALog.debug(this, "movementRayPoint.getStartPosition() = " + movementRayPoint.getStartPosition().toString());
    	// ALog.debug(this, "movementRayCircle.getStartPosition() = " + movementRayCircleClone.getStartPosition().toString());
		
		float closestDistance = Math.abs(movementRayCircleClone.getNormal().dot(startCirclePoint));

		// ALog.debug(this, "closestDistance = " + closestDistance);
		// ALog.debug(this, "circle.getRadius() = " + circle.getRadius());
		
		if (closestDistance <= circle.getRadius())
		{
			Vector2 movementRayCircleNormal = mGCContext.mGCSubCallRay2.set(movementRayCircleClone).getVector().normalize();
	
			float c = (float) Math.sqrt((circle.getRadius() * circle.getRadius()) - (closestDistance * closestDistance));
			
			float m = movementRayCircleNormal.dot(startCirclePoint) - c;
			
			float t = m / movementRayCircle.getMagnitude();
			
			// ALog.debug(this, "c = " + c);
			// ALog.debug(this, "m = " + m);
			// ALog.debug(this, "t = " + t);
			
	    	// Check within start and end of movevec
	    	if ((t >= 0.0) && (t <= 1.0))
	    	{
		    	movementRayCircle.scale(t); 
		    	movementRayPoint.scale(t); 
	
//		    	Vector2 cNormal = movementRayCircle.getEndPosition().subtract(movementRayPoint.getEndPosition(), mGCContext.mGCSubCallVector2).normalize();
		    	Vector2 cNormal = AMath.subtract(movementRayCircle.getEndPosition(), movementRayPoint.getEndPosition(), mGCContext.mGCSubCallVector2).normalize();
		    	
		    	contextCircle.setCollisionNormal(cNormal);
		    	contextCircle.setCorrectedMovement(movementRayCircle);
			
		    	Vector2 pNormal = cNormal.clone();
		    	
		    	pNormal.reverse();
		    	
		    	contextPoint.setCollisionNormal(pNormal);
		    	contextPoint.setCorrectedMovement(movementRayPoint);
	
		    	collision = true;
	    	}
		}
		
    	return  collision;
    }
     
    int voronoiRegionsIncluding(IBounds bounds, Point point)
    {
        int code = VORONOI_RECT_INSIDE;   
 
        if (point.X < bounds.getLeft())
        {
        	// to the left of rectangle
		    code |= VORONOI_RECT_LEFT;
		}
		else if (point.X > bounds.getRight())     
		{
			// to the right of rectangle
		    code |= VORONOI_RECT_RIGHT;
		}
		
		if (point.Y < bounds.getTop())           
		{
			// below the rectangle
		    code |= VORONOI_RECT_TOP;
		}
		else if (point.Y > bounds.getBottom())    
		{
			// above the rectangle
		    code |= VORONOI_RECT_BOTTOM;
		}
		
		return code;
    }
     
    int voronoiRegionsExcluding(IBounds bounds, Point point)
    {
        int code = VORONOI_RECT_INSIDE;   
 
        if (point.X <= (bounds.getLeft() + VORONOI_EPS))
        {
        	// to the left of rectangle
		    code |= VORONOI_RECT_LEFT;
		}
		else if (point.X >= (bounds.getRight() - VORONOI_EPS))     
		{
			// to the right of rectangle
		    code |= VORONOI_RECT_RIGHT;
		}
		
		if (point.Y <= (bounds.getTop() + VORONOI_EPS))           
		{
			// below the rectangle
		    code |= VORONOI_RECT_TOP;
		}
		else if (point.Y >= (bounds.getBottom() - VORONOI_EPS))    
		{
			// above the rectangle
		    code |= VORONOI_RECT_BOTTOM;
		}
		
		return code;
    }

    // Cohen–Sutherland clipping algorithm clips a line from
    // P0 = (x0, y0) to P1 = (x1, y1) against a rectangle with 
    // diagonal from (xmin, ymin) to (xmax, ymax).
    IntersectionEnum cohenSutherlandRectangleClip(IBounds rect, Ray ray, Point intersectPos, Vector2 normal)
    {
    	IntersectionEnum result = IntersectionEnum.INTERSECTION_INSIDE;
    	
    	Point startPos = ray.getStartPosition();
    	Point endPos = ray.getEndPosition();

    	intersectPos.X = startPos.X;
    	intersectPos.Y = startPos.Y;
    	
		// compute outcodes for P0, P1, and whatever point lies outside the clip rectangle
		int vCodeStart = voronoiRegionsExcluding(rect, intersectPos);
		int vCodeEnd = voronoiRegionsExcluding(rect, endPos);

		boolean finished = false;
		
	    while (!finished) 
        {
			if (vCodeStart == 0) 
			{ 
				result = IntersectionEnum.INTERSECTION_INSIDE;
			    finished = true;
			} 
			else if ((vCodeStart & vCodeEnd) != 0) 
			{ 
				result = IntersectionEnum.INTERSECTION_OUTSIDE;
			    finished = true;
            }
			else 
			{
                // At least one endpoint is outside the clip rectangle; pick it.
                int vCode = vCodeStart;
 
				// Now find the intersection point;
				// use formulas y = y0 + slope * (x - x0), x = x0 + (1 / slope) * (y - y0)
                if ((vCode & VORONOI_RECT_TOP) != 0) 
                {           
                	// point is above the clip rectangle
                	intersectPos.X = intersectPos.X + (endPos.X - intersectPos.X) * (rect.getTop() - intersectPos.Y) / (endPos.Y - intersectPos.Y);
                	intersectPos.Y = rect.getTop();
                	
                	normal.X = 0;
                	normal.Y = -1;
                } 
                else if ((vCode & VORONOI_RECT_BOTTOM) != 0) 
                { 
                	// point is below the clip rectangle
                	intersectPos.X = intersectPos.X + (endPos.X - intersectPos.X) * (rect.getBottom() - intersectPos.Y) / (endPos.Y - intersectPos.Y);
                	intersectPos.Y = rect.getBottom();
                	
                	normal.X = 0;
                	normal.Y = 1;
                } 
                else if ((vCode & VORONOI_RECT_RIGHT) != 0)
                {  
                	// point is to the right of clip rectangle
                	intersectPos.Y = intersectPos.Y + (endPos.Y - intersectPos.Y) * (rect.getRight() - intersectPos.X) / (endPos.X - intersectPos.X);
                    intersectPos.X = rect.getRight();
                    
                	normal.X = 1;
                	normal.Y = 0;
                } 
                else if ((vCode & VORONOI_RECT_LEFT) != 0) 
                {   
                	// point is to the left of clip rectangle
                	intersectPos.Y = intersectPos.Y + (endPos.Y - intersectPos.Y) * (rect.getLeft() - intersectPos.X) / (endPos.X - intersectPos.X);
                    intersectPos.X = rect.getLeft();
                    
                	normal.X = -1;
                	normal.Y = 0;
                }

           		vCodeStart = voronoiRegionsIncluding(rect, intersectPos);
           		
    			if (vCodeStart == 0) 
    			{ 
    				result = IntersectionEnum.INTERSECTION_INTERSECT;
    			    finished = true;
    			}
            }
        }
	    
	    return result;
    }

    public static class CollisionContext implements ICollisionContext
    {
    	private Vector2 mCollisionNormal = Vector2.Zero.clone();
    	private Ray mMovementCorrection = Ray.Zero.clone();
    	
		@Override
		public void setCollisionNormal(Vector2 normal)
		{
			mCollisionNormal.set(normal);
		}
		
		@Override
		public void setCorrectedMovement(Ray ray)
		{
			mMovementCorrection.set(ray);
		}
		
		@Override
		public Vector2 getCollisionNormal()
		{
			return mCollisionNormal;
		}

		@Override
		public Ray getCorrectedMovement()
		{
			return mMovementCorrection;
		}

		@Override
		public void set(ICollisionContext value)
		{
	    	mCollisionNormal.set(value.getCollisionNormal());
	    	mMovementCorrection.set(value.getCorrectedMovement());
		}
    }
    
    private static class GCManagerContext
    {
    	public Ray mGCRay1 = Ray.Zero.clone();
    	public Ray mGCRay2 = Ray.Zero.clone();
    	public Ray mGCRay3 = Ray.Zero.clone();
    	public Point mGCPoint1 = Point.Zero.clone();
    	public Vector2 mGCVector1 = Vector2.Zero.clone();
    	public Vector2 mGCVector2 = Vector2.Zero.clone();
    	public Vector2 mGCVector3 = Vector2.Zero.clone();
    	public Vector2 mGCVector4 = Vector2.Zero.clone();
    	public Rectangle mGCRectangle1 = Rectangle.Zero.clone();
		public CollisionBoundingBox mGCBoundingBox1 = CollisionBoundingBox.Zero.clone();
		
    	public Vector2 mGCSubCallVector1 = Vector2.Zero.clone();
    	public Vector2 mGCSubCallVector2 = Vector2.Zero.clone();
    	public Ray mGCSubCallRay1 = Ray.Zero.clone();
    	public Ray mGCSubCallRay2 = Ray.Zero.clone();
    }
    
    private static class Statistics
    {
    	public long mNofCollisionsEvaluated = 0;
    	public long mNofManagerInvokations = 0;
    	public long mNofActualCollisions = 0;

		public long mTimeToLogCounter = AInstrumentation.LOG_COLLISION_INTERVAL;
    }
}
