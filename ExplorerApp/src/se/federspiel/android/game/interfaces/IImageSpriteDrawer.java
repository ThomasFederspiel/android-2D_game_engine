package se.federspiel.android.game.interfaces;

import android.graphics.Canvas;
import se.federspiel.android.game.geometry.Dimensions;
import se.federspiel.android.game.geometry.Point;

public interface IImageSpriteDrawer
{
	public enum BitmapCollisionBounds
	{
		UNDEFINED,
		RECT_UPPER_LEFT,
		CIRCLE
	}
	
    public Dimensions getDimensions();
	public IBounds getBounds();
    public ICollisionBound getCollisionBounds();
	public void updatePosition(Point position);
    public void loadContent(ISprite sprite);
    public void unloadContent();
    public void draw(ISprite sprite, Canvas canvas);
}
