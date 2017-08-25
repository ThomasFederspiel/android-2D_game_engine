package se.federspiel.android.game.geometry;

public class MovementRectangle extends Rectangle
{
	public static final MovementRectangle Zero = new MovementRectangle(0, 0, 0, 0, Float.NaN, Float.NaN, Float.NaN, Float.NaN);

	private Point mTopLeftLimit = Point.Zero.clone();
	private Point mBottomRightLimit = Point.Zero.clone();
	
	public MovementRectangle(MovementRectangle rectangle)
	{
		super(rectangle);
		
		mTopLeftLimit.set(rectangle.mTopLeftLimit);
		mBottomRightLimit.set(rectangle.mBottomRightLimit);
	}
	
	public MovementRectangle(float left, float top, int width, int height,
			float leftLimit, float topLimit, float rightLimit, float bottomLimit)
	{
		super(left, top, width, height);
		
		mTopLeftLimit.set(leftLimit, topLimit);
		mBottomRightLimit.set(rightLimit, bottomLimit);
	}
	
	public void resetLimits()
	{
		mTopLeftLimit.set(Float.NaN, Float.NaN);
		mBottomRightLimit.set(Float.NaN, Float.NaN);
	}

	public void setLimits(float leftLimit, float topLimit, float rightLimit, float bottomLimit)
	{
		mTopLeftLimit.set(leftLimit, topLimit);
		mBottomRightLimit.set(rightLimit, bottomLimit);
	}
	
    public void setPositionLimited(float x, float y)
    {
    	float dx = x - mTopLeft.X;
	    float dy = y - mTopLeft.Y;
    	
	    moveLimited(dx, dy);
    }
    
    public void moveLimited(float dx, float dy)
    {
		mTopLeft.move(dx, dy);
		
		if ((mTopLeftLimit.X != Float.NaN) && (mTopLeft.X < mTopLeftLimit.X))
		{
			mTopLeft.move(mTopLeftLimit.X - mTopLeft.X, 0);
		}
		else 
		{
			float right = mTopLeft.X + mDimensions.getWidth();
			
			if ((mBottomRightLimit.X != Float.NaN) && (right > mBottomRightLimit.X))
			{
				mTopLeft.move(mBottomRightLimit.X - right, 0);
			}
		}

		if ((mTopLeftLimit.Y != Float.NaN) && (mTopLeft.Y < mTopLeftLimit.Y))
		{
			mTopLeft.move(0, mTopLeftLimit.Y - mTopLeft.Y);
		}
		else 
		{
			float bottom = mTopLeft.Y + mDimensions.getHeight();
			
			if ((mBottomRightLimit.Y != Float.NaN) && (bottom > mBottomRightLimit.Y))
			{
				mTopLeft.move(0, mBottomRightLimit.Y - bottom);
			}
		}
    }

	public MovementRectangle set(MovementRectangle rect)
	{
		super.set(rect);
		
		mTopLeftLimit.set(rect.mTopLeftLimit);
		mBottomRightLimit.set(rect.mBottomRightLimit);
		
		return this;
	}
	
	public MovementRectangle clone()
	{
		return new MovementRectangle(this);
	}
}
