package se.federspiel.android.game.collision.bounds;

import se.federspiel.android.game.geometry.Circle;
import se.federspiel.android.game.geometry.Point;
import se.federspiel.android.game.interfaces.ICollisionBound;

public class CollisionBoundingCircle implements ICollisionBound
{
	public static final CollisionBoundingCircle Zero = new CollisionBoundingCircle(0, 0, 0);
	
    private Point mPosition = Point.Zero.clone();
    private int mRadius = 0;

    private Circle mCircle = null;

    public CollisionBoundingCircle(CollisionBoundingCircle circle)
    {
    	this(circle.mPosition, circle.mRadius);
    }

    public CollisionBoundingCircle(Point position, int radius)
    {
    	this(position.X, position.Y, radius);
    }

    public CollisionBoundingCircle(float x, float y, int radius)
    {
    	mPosition.X = x;
    	mPosition.Y = y;
        mRadius = radius;

        mCircle = new Circle(mPosition, mRadius);
    }
    
	@Override
    public Point getPosition()
    {
        return mCircle.getPosition();
    }

	@Override
    public void setPosition(Point position)
    {
        mCircle.setPosition(position);
    }

	@Override
    public void setPosition(float x, float y)
    {
        mCircle.setPosition(x, y);
    }

	@Override
	public Point getCenter()
	{
        return mCircle.getPosition();
	}

    public Circle getBoundingCircle()
    {
        return mCircle;
    }

    public void setRadius(int radius)
    {
        mCircle.setRadius(radius);
    }

    public int getRadius()
    {
        return mCircle.getRadius();
    }

    public boolean intersects(CollisionBoundingCircle circle)
    {
    	return mCircle.intersects(circle.mCircle);
    }
    
    public CollisionBoundingCircle clone()
    {
    	return new CollisionBoundingCircle(this);
    }
}
