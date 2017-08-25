package se.federspiel.android.game.interfaces.gprimitives;

import se.federspiel.android.game.geometry.Dimensions;

public interface ISquarePrimitive extends IBasePrimitive
{
    public void setDimensions(int width, int height);
    public void setDimensions(Dimensions dims);
}