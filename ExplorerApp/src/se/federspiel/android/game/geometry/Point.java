package se.federspiel.android.game.geometry;

import se.federspiel.android.game.utils.AMath;

public class Point
{
	public static final Point Zero = new Point(0);
	
	public float X = 0;
	public float Y = 0;
	
	public Point(Point point)
	{
		this(point.X, point.Y);
	}
	
	public Point(float v)
	{
		this(v, v);
	}
	
	public Point(float x, float y)
	{
		X = x;
		Y = y;
	}

	public void set(Point point)
	{
		X = point.X;
		Y = point.Y;
	}
	
	public void set(float x, float y)
	{
		X = x;
		Y = y;
	}
	
	public void move(float dx, float dy)
	{
		X += dx;
		Y += dy;
	}

	public Point clone()
	{
		return new Point(this);
	}
	
	public void addToThis(Point point)
	{
		AMath.addToFirst(this, point);
	}

	public void subtractFromThis(Point point)
	{
		AMath.subtractFromFirst(this, point);
	}
	
	public boolean isSame(Point point)
	{
		return ((Math.abs(X - point.X) < AMath.EPS) 
				&& (Math.abs(Y - point.Y) < AMath.EPS));
	}

	public String toString()
	{
		return "(x = " + X + ", y = " + Y + ")";
	}
}
