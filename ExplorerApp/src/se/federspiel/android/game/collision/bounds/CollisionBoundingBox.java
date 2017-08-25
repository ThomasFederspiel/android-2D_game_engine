package se.federspiel.android.game.collision.bounds;

import se.federspiel.android.game.geometry.Dimensions;
import se.federspiel.android.game.geometry.Margins;
import se.federspiel.android.game.geometry.Point;
import se.federspiel.android.game.geometry.Rectangle;
import se.federspiel.android.game.interfaces.IBounds;
import se.federspiel.android.game.interfaces.ICollisionBound;

public class CollisionBoundingBox implements ICollisionBound
{
	public static final CollisionBoundingBox Zero = new CollisionBoundingBox(0, 0, 0, 0);
	
	private Rectangle mBox = Rectangle.Zero.clone();

	private Margins mBoundMargins = Margins.Zero.clone();
	
    public CollisionBoundingBox(CollisionBoundingBox box)
    {
    	this(box.mBox.getPosition(), box.mBox.getWidth(), box.mBox.getHeight());
    }
    
    public CollisionBoundingBox(Point position, int width, int height)
    {
    	this(position.X, position.Y, width, height);
    }

    public CollisionBoundingBox(float x, float y, int width, int height)
    {
    	updatePosition(x, y);
    	updateDimensions(width, height);
    }

    public IBounds getBoundingBox()
    {
        return mBox;
    }

    public void setMargins(int left, int top, int right, int bottom)
    {
        updateMargins(left, top, right, bottom);
    }
    
    public void setMargins(Margins margins)
    {
    	updateMargins(margins);
    }
    
	@Override
    public Point getPosition()
    {
    	return mBox.getPosition();
    }

	@Override
    public void setPosition(Point position)
    {
		updatePosition(position.X, position.Y);
    }

	@Override
    public void setPosition(float x, float y)
    {
		updatePosition(x, y);
    }

    public void setDimensions(Dimensions dims)
    {
    	updateDimensions(dims);
    }
    
	@Override
    public Point getCenter()
    {
		return mBox.getCenter();
		
//	;+	return new Point(mBox.getCenterX(), mBox.getCenterY()); 
    }

    public boolean intersects(CollisionBoundingBox box)
    {
    	return mBox.intersects(box.mBox);
    }
    
    public CollisionBoundingBox set(CollisionBoundingBox box)
    {
    	mBox.set(box.mBox);
    	mBoundMargins.set(box.mBoundMargins);
    	
    	return this;
    }
    
    public CollisionBoundingBox clone()
    {
    	return new CollisionBoundingBox(this);
    }

    public String toString()
    {
    	return "(position = " + mBox.toString() + ", margins = " + mBoundMargins.toString() + ")";
    }
    
    private void updatePosition(float x, float y)
    {
    	mBox.setPosition(x + mBoundMargins.getLeft(), y + mBoundMargins.getTop());
    }

    private void updateDimensions(Dimensions dims)
    {
    	updateDimensions(dims.getWidth(), dims.getHeight());
    }
    
    private void updateDimensions(int width, int height)
    {
    	width = width - mBoundMargins.getLeft() - mBoundMargins.getRight();
    	height = height - mBoundMargins.getTop() - mBoundMargins.getBottom();
    	
    	mBox.setDimensions(width, height);
    }
    
	private void updateMargins(Margins margins)
	{
	    updateMargins(margins.getLeft(), margins.getTop(), 
	    		margins.getRight(), margins.getBottom());
	}

    public void updateMargins(int left, int top, int right, int bottom)
    {
    	float x = mBox.getLeft() - mBoundMargins.getLeft();
    	float y = mBox.getTop() - mBoundMargins.getTop();
    	
    	int width = mBox.getWidth() + mBoundMargins.getLeft() + mBoundMargins.getRight();
    	int height = mBox.getHeight() + mBoundMargins.getTop() + mBoundMargins.getBottom();
    	
		mBoundMargins.setMargins(left, top, right, bottom);
		
		updatePosition(x, y);
		updateDimensions(width, height);
    }
}
