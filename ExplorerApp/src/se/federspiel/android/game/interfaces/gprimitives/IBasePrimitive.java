package se.federspiel.android.game.interfaces.gprimitives;

import se.federspiel.android.game.geometry.Point;


public interface IBasePrimitive
{
	public void setPosition(float x, float y);
	public void setPosition(Point position);
	public void load();
	public void unload();
}