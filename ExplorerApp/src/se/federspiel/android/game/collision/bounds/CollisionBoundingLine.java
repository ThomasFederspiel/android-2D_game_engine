package se.federspiel.android.game.collision.bounds;

import se.federspiel.android.game.geometry.Line;
import se.federspiel.android.game.geometry.Point;
import se.federspiel.android.game.geometry.Vector2;
import se.federspiel.android.game.interfaces.ICollisionBound;

public class CollisionBoundingLine implements ICollisionBound
{
	public static final CollisionBoundingLine Zero = new CollisionBoundingLine(0, 0, 0, 0);

	private Line mLine = null;

    public CollisionBoundingLine(CollisionBoundingLine bounds)
    {
    	this(bounds.mLine.getStartPosition(), bounds.mLine.getEndPosition());
    }
    
    public CollisionBoundingLine(Point start, Point end)
    {
    	this(start.X, start.Y, end.X, end.Y);
    }
    
    public CollisionBoundingLine(float x1, float y1, float x2, float y2)
    {
        mLine = new Line(x1, y1, x2, y2);
    }

	@Override
    public Point getPosition()
    {
        return mLine.getStartPosition();
    }

	@Override
    public void setPosition(Point position)
    {
        mLine.setStartPosition(position);
    }

	@Override
    public void setPosition(float x, float y)
    {
        mLine.setStartPosition(x, y);
    }

	@Override
	public Point getCenter()
	{
        return mLine.getCenter();
	}
	
	public Vector2 getDirection()
	{
        return mLine.getDirection();
	}
	
	public void setDirection(float dx, float dy)
	{
        mLine.setDirection(dx, dy);
	}
	
	public Vector2 getNormal()
	{
		return mLine.getNormal();
	}
	
	public CollisionBoundingLine clone()
	{
		return new CollisionBoundingLine(this);
	}
	
    public boolean intersects(CollisionBoundingLine line)
    {
    	return mLine.intersects(line.mLine);
    }
}
