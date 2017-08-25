package se.federspiel.android.game.geometry;

import se.federspiel.android.game.utils.AMath;

public class Ray
{
	public static final Ray Zero = new Ray(Point.Zero.clone(), Vector2.Zero.clone());
	
    private Point mStartPoint = Point.Zero.clone();
    private Point mEndPoint = Point.Zero.clone();
    private Vector2 mVector = Vector2.Zero.clone();
	private Vector2 mNormal = Vector2.Zero.clone();

	private boolean mPendingNormal = false;
	private boolean mPendingEndPosition = false;
	
    public Ray(Point point, Vector2 vector) 
    {
        mStartPoint.set(point);
        mVector.set(vector);
        
        updateEndPosition();
        updateNormal();
    }

    public Point getStartPosition()
    {
        return mStartPoint;
    }

    public void setStartPosition(Point point)
    {
        mStartPoint.set(point);
        
        markPendingEndPosition();
    }
    
    public void moveStartPosition(float distance)
    {
    	mVector.addMagnitude(distance);
		   
    	updateEndPosition();
    	
		AMath.subtract(mEndPoint, mVector, mStartPoint);
		
        markPendingNormal();
    }
    
    public Point getEndPosition()
    {
    	updateEndPosition();
    	
//    	return AMath.add(mStartPoint, mVector);
        return mEndPoint;
    }

    public Vector2 getVector()
    {
        return mVector;
    }

    public void addVector(Vector2 vector)
    {
    	mVector.add(vector);
    	
        markPendingNormal();
        
        markPendingEndPosition();
    }
    
    public void subtractVector(Vector2 vector)
    {
    	mVector.subtract(vector);
    	
        markPendingNormal();
        
        markPendingEndPosition();
    }

    public Ray set(Ray ray)
    {
    	mStartPoint.set(ray.mStartPoint);
    	mVector.set(ray.mVector);

        markPendingNormal();
        
        markPendingEndPosition();
    	
    	return this;
    }
    
    public Ray set(Point point, Vector2 vector)
    {
    	mStartPoint.set(point);
    	mVector.set(vector);
    	
        markPendingNormal();
       
        markPendingEndPosition();
        
    	return this;
    }

    public Ray clone()
    {
    	return new Ray(mStartPoint.clone(), mVector.clone());
    }
    
    public void clampMagnitude(float magnitude)
    {
        mVector.clampMagnitude(magnitude);
        
        markPendingEndPosition();
    }
    
    public void scale(float scale)
    {
        mVector.scale(scale);
        
        markPendingEndPosition();
    }

    public float getMagnitude()
    {
        return mVector.getMagnitude();
    }
    
    public Vector2 getNormal()
    {
    	updateNormal();
    	
        return mNormal;
    }
	
    public String toString()
    {
    	updateEndPosition();
    	
    	return "((x = " + mStartPoint.X + ", y = " + mStartPoint.Y + "), (x = " 
    			+ mEndPoint.X + ", y = " + mEndPoint.Y + "))";
    }
    
    private void markPendingEndPosition()
    {
    	mPendingEndPosition = true;
    }
    
    private void markPendingNormal()
    {
    	mPendingNormal = true;
    }

    private void updateEndPosition()
	{
    	if (mPendingEndPosition)
    	{
			AMath.add(mStartPoint, mVector, mEndPoint);
			
	    	mPendingEndPosition = false;
    	} 
	}
	
	private void updateNormal()
	{
    	if (mPendingNormal)
    	{
			mNormal.X = -mVector.Y;
			mNormal.Y = mVector.X;	
			
			mNormal.normalize();
			
	    	mPendingNormal = false;
    	}
	}
}
