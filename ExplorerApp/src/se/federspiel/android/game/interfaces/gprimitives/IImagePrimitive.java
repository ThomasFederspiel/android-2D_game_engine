package se.federspiel.android.game.interfaces.gprimitives;

import android.graphics.Bitmap;
import se.federspiel.android.game.geometry.Dimensions;

public interface IImagePrimitive extends IBasePrimitive
{
    public void setDimensions(int width, int height);
    public void setDimensions(Dimensions dims);
    public void setBitmap(Bitmap bitmap, boolean setDimension);
}