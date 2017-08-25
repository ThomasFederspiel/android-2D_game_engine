package se.federspiel.android.game.interfaces;

import se.federspiel.android.game.geometry.Point;

public interface ICollisionBound
{
    public Point getPosition();
    public void setPosition(Point point);
    public void setPosition(float x, float y);

    public Point getCenter();
}
