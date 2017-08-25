package se.federspiel.android.game.interfaces;

import se.federspiel.android.game.GameRenderer;
import se.federspiel.android.game.geometry.Dimensions;
import se.federspiel.android.game.geometry.Point;

public interface IGraphicSpriteDrawer
{
    public Dimensions getDimensions();
	public IBounds getBounds();
	public void updatePosition(Point position);
    public ICollisionBound getCollisionBounds();
    public void draw(ISprite sprite, GameRenderer renderer);
}
