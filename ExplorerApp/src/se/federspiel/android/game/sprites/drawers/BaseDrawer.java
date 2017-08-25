package se.federspiel.android.game.sprites.drawers;

import se.federspiel.android.game.interfaces.IGameContext;

public class BaseDrawer
{
	protected IGameContext mGameContext = null;

    public BaseDrawer(IGameContext gameContext)
    {
    	mGameContext = gameContext;
    }
}
