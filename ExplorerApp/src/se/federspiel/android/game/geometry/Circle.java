package se.federspiel.android.game.geometry;


public class Circle
{
	private Point mPosition = null;
	private int mRadius = 0;

	public Circle(Point position, int radius)
	{
		mPosition = position;
		mRadius = radius;
	}
	
	public void setPosition(Point position)
	{
		mPosition = position;
	}

	public void setPosition(float x, float y)
	{
		mPosition.X = x;
		mPosition.Y = y;
	}

	public void setRadius(int radius)
	{
		mRadius = radius;
	}

	public int getRadius()
	{
		return mRadius;
	}

	public Point getPosition()
	{
		return mPosition;
	}
	
	public boolean intersects(Circle circle)
	{
		float a = mRadius + circle.mRadius; 
	    double dx = mPosition.X - circle.mPosition.X; 
	    double dy = mPosition.Y - circle.mPosition.Y; 
	    
	    return (a * a >= (dx * dx + dy * dy));		
	}
}
