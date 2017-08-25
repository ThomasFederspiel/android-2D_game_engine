package se.federspiel.android.game.interfaces;

import se.federspiel.android.game.GameRenderer;

public interface IGraphicDrawer
{
	public IBounds getBounds();
    public void draw(GameRenderer renderer);
}
