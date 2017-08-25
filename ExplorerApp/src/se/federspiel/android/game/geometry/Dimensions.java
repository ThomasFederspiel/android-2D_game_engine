package se.federspiel.android.game.geometry;

import se.federspiel.android.game.utils.AMath;

public class Dimensions
{
	public static final Dimensions Zero = new Dimensions(0, 0);
	
	private int mWidth = 0;
	private int mHeight = 0;
	
	public Dimensions(Dimensions dims)
	{
		this(dims.mWidth, dims.mHeight);
	}
	
	public Dimensions(int width, int height)
	{
		mWidth = width;
		mHeight = height;
	}

	public int getWidth()
	{
		return mWidth;
	}
	
	public int getHeight()
	{
		return mHeight;
	}
	
	public void setDimensions(int width, int height)
	{
		mWidth = width;
		mHeight = height;
	}
	
	public void setWidth(int width)
	{
		mWidth = width;
	}
	
	public void setHeight(int height)
	{
		mHeight = height;
	}
	
	public void set(Dimensions dims)
	{
		mWidth = dims.mWidth;
		mHeight = dims.mHeight;
	}

	public void set(int width, int height)
	{
		mWidth = width;
		mHeight = height;
	}

	public Dimensions clone()
	{
		return new Dimensions(this);
	}
	
	public boolean isSame(Dimensions point)
	{
		return ((Math.abs(mWidth - point.mWidth) < AMath.EPS) 
				&& (Math.abs(mHeight - point.mHeight) < AMath.EPS));
	}
	
	public boolean isSame(int width, int height)
	{
		return ((Math.abs(mWidth - width) < AMath.EPS) 
				&& (Math.abs(mHeight - height) < AMath.EPS));
	}

	public String toString()
	{
		return "(w = " + mWidth + ", h = " + mHeight + ")";
	}
}
