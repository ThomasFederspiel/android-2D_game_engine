package se.federspiel.android.game.interfaces;

import se.federspiel.android.game.geometry.Dimensions;
import se.federspiel.android.game.geometry.Point;
import se.federspiel.android.game.geometry.Rectangle;

public interface IBounds
{
	public float getTop();
	public float getBottom();
	public float getRight();
	public float getLeft();
	public int getWidth();
	public int getHeight();
	
    public Point getCenter();
    public Point getPosition();
    public void setPosition(float x, float y);
    public void setPosition(Point position);
    
    public void setDimensions(Dimensions dims);
    public Dimensions getDimensions();

    public void copy(Rectangle rectangle, float x, float y);
    public boolean intersects(Rectangle rectangle);
    
	public boolean contains(float x, float y);
}
