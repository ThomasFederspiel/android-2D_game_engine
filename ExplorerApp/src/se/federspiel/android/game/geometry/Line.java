package se.federspiel.android.game.geometry;

import se.federspiel.android.game.utils.AMath;


public class Line
{
	private Point mStartPosition = Point.Zero.clone();
	private Vector2 mDirection = Vector2.Zero.clone();
	private Vector2 mNormal = Vector2.Zero.clone();
	
	public Line(float x1, float y1, float x2, float y2)
	{
		mStartPosition.X = x1;
		mStartPosition.Y = y1;

		setDirection(x2 - x1, y2 - y1);
	}
	
	public void setStartPosition(Point position)
	{
		mStartPosition = position;
	}

	public void setStartPosition(float x, float y)
	{
		mStartPosition.X = x;
		mStartPosition.Y = y;
	}

	public void setDirection(float dx, float dy)
	{
		mDirection.X = dx;
		mDirection.Y = dy;
	
		updateNormal();
	}

	public Vector2 getDirection()
	{
		return mDirection;
	}
	
	public Point getStartPosition()
	{
		return mStartPosition;
	}
	
	public Point getEndPosition()
	{
		return new Point(mStartPosition.X + mDirection.X, mStartPosition.Y + mDirection.Y);
	}

	public Point getCenter()
	{
		return new Point(mStartPosition.X + mDirection.X / 2, mStartPosition.Y + mDirection.Y / 2);
	}

	public Vector2 getNormal()
	{
		return mNormal;
	}

	public boolean intersects(Line line)
	{
		return false;
	}
	
	private void updateNormal()
	{
		mNormal = AMath.calculateNormal(mNormal, mDirection);
	}
}
