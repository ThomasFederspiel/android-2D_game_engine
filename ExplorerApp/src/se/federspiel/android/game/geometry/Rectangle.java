package se.federspiel.android.game.geometry;

import se.federspiel.android.game.interfaces.IBounds;
import android.graphics.Rect;

public class Rectangle implements IBounds
{
	public static final Rectangle Zero = new Rectangle(0, 0, 0, 0);

	protected Point mTopLeft = Point.Zero.clone();

	protected Point mCenter = Point.Zero.clone();
	protected Point mOffset = Point.Zero.clone();

	protected Dimensions mDimensions = Dimensions.Zero.clone();

	private Rect mAndroidRect = new Rect();
	
	public Rectangle(Rectangle rectangle)
	{
		this(rectangle.mTopLeft, rectangle.mDimensions);
	}
	
	public Rectangle(Point topLeft, Dimensions dims)
	{
		mTopLeft.set(topLeft);

		mDimensions.set(dims);
		
		calculateOffsetAndCenter();
	}
	
	public Rectangle(float left, float top, int width, int height)
	{
		mTopLeft.set(left, top);
		
		mDimensions.setDimensions(width, height);
		
		calculateOffsetAndCenter();
	}
	
	@Override
	public float getLeft()
	{
		return mTopLeft.X;
	}
	
	@Override
	public float getTop()
	{
		return mTopLeft.Y;
	}

	@Override
	public float getRight()
	{
		return mTopLeft.X + mDimensions.getWidth();
	}

	@Override
	public float getBottom()
	{
		return mTopLeft.Y + mDimensions.getHeight();
	}

	@Override
	public int getWidth()
	{
		return mDimensions.getWidth();
	}

	@Override
	public int getHeight()
	{
		return mDimensions.getHeight();
	}

	@Override
    public Point getCenter()
    {
    	return mCenter;
    }

	@Override
    public Point getPosition()
    {
    	return mTopLeft;
    }

	@Override
    public void setPosition(float x, float y)
    {
		mTopLeft.set(x, y);
		
		mCenter.X = mTopLeft.X + mOffset.X;
		mCenter.Y = mTopLeft.Y + mOffset.Y;
    }
    
	@Override
    public void setPosition(Point position)
    {
		setPosition(position.X, position.Y);
    }
    
	@Override
	public void copy(Rectangle rectangle, float x, float y)
	{
		rectangle.set(this);
		rectangle.setPosition(x, y);
	}
	
	@Override
    public void setDimensions(Dimensions dims)
    {
		setDimensions(dims.getWidth(), dims.getHeight());
    }
	
    public void setDimensions(int width, int height)
    {
    	mDimensions.setDimensions(width, height);
    	
		calculateOffset();		
    }
    
	@Override
    public Dimensions getDimensions()
    {
    	return mDimensions;
    }
    
	@Override
	public boolean contains(float x, float y)
	{
        return ((x >= mTopLeft.X) && (x <= getRight()) && (y >= mTopLeft.Y) && (y <= getBottom()));
	}
	
	@Override
	public boolean intersects(Rectangle box)
	{
		return ((mTopLeft.X < box.getRight()) && (box.getLeft() < getRight())
	                && (mTopLeft.Y < box.getBottom()) && (box.getTop() < getBottom()));
	}
	
	public Rectangle set(Rectangle rect)
	{
		mTopLeft.set(rect.mTopLeft);
		
		mDimensions.set(rect.mDimensions);
		
		return this;
	}
	
	public void union(IBounds bounds)
	{
		if (mTopLeft.X > bounds.getLeft())
		{
			mTopLeft.X = bounds.getLeft();
		}

		if (mTopLeft.Y > bounds.getTop())
		{
			mTopLeft.Y = bounds.getTop();
		}
		
		if (getRight() < bounds.getRight())
		{
			mDimensions.setWidth(mDimensions.getWidth() + (int) (bounds.getRight() - getRight()));
		}
		
		if (getBottom() < bounds.getBottom())
		{
			mDimensions.setHeight(mDimensions.getHeight() + (int) (bounds.getBottom() - getBottom()));
		}
	}
	
	public void union(float x, float y)
	{
		if (mTopLeft.X > x)
		{
			mDimensions.setWidth(mDimensions.getWidth() + (int) (mTopLeft.X - x));
			
			mTopLeft.X = x;
		}

		if (mTopLeft.Y > y)
		{
			mDimensions.setHeight(mDimensions.getHeight() + (int) (mTopLeft.Y - y));
			
			mTopLeft.Y = y;
		}
		
		if (getRight() <= x)
		{
			mDimensions.setWidth(mDimensions.getWidth() + (int) (x - getRight()) + 1);
		}
		
		if (getBottom() <= y)
		{
			mDimensions.setHeight(mDimensions.getHeight() + (int) (y - getBottom()) + 1);
		}
	}

	public Rectangle clone()
	{
		return new Rectangle(this);
	}
	
	public Rect getAndroidRect()
	{
		mAndroidRect.set((int) mTopLeft.X, (int) mTopLeft.Y, (int) getRight(), (int) getBottom());
		
		return mAndroidRect;
	}
	
	public String toString()
	{
		return "(x = " + mTopLeft.X + ", y = " + mTopLeft.Y + ", width = " 
			+ mDimensions.getWidth() + ", height = " + mDimensions.getHeight() + ")";
	}
	
	private void calculateOffset()
	{
		mOffset.X = mDimensions.getWidth() / 2;
		mOffset.Y = mDimensions.getHeight() / 2;
	}
	
	private void calculateOffsetAndCenter()
	{
		calculateOffset();
		
		mCenter.X = mTopLeft.X + mOffset.X;
		mCenter.Y = mTopLeft.Y + mOffset.Y;
	}
}
