package se.federspiel.android.agraphics.primitives;

import se.federspiel.android.game.geometry.Dimensions;
import se.federspiel.android.game.geometry.Point;
import se.federspiel.android.game.interfaces.gprimitives.IImagePrimitive;
import android.graphics.Bitmap;

public class Image extends AbstractPrimitive implements IImagePrimitive 
{
    public Image() 
    {
    	super();
    }
    
	public void setPosition(float x, float y)
	{
	}
	
	public void setPosition(Point position)
	{
	}
    
    public void setDimensions(int width, int height)
	{
	}
    
    public void setDimensions(Dimensions dims)
	{
	}
    
    public void setBitmap(Bitmap bitmap, boolean setDimension)
    {
    }
}