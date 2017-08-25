package se.federspiel.android.game.interfaces;

import se.federspiel.android.game.geometry.Dimensions;
import se.federspiel.android.game.geometry.Point;
import android.graphics.Canvas;

public interface IUIComponent
{
	public Dimensions getDimensions();
	public void setDimensions(int width, int height);

	public Point getPosition();
	public void setPosition(Point position);
	public void setPosition(float x, float y);
	
	public Point getParentOffset();
	public void setParentOffset(float x, float y);

	public void setEnable(boolean enable);
	public boolean isEnabled();
	
	public void loadContent();
	public void unloadContent();
	public void draw(Point parentPosition, Canvas canvas);
}
