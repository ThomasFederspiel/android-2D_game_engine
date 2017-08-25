package se.federspiel.android.game.trajectories.limits;

import se.federspiel.android.game.interfaces.IGameContext;
import android.graphics.Rect;

public class GraphicsViewLimits extends BoundsLimits
{
    public GraphicsViewLimits(IGameContext gameContext, Rect margin)
    {
    	super((int) (gameContext.getGraphicBounds().getLeft() + margin.left),
	    	(int) (gameContext.getGraphicBounds().getRight() - margin.right),
	    	(int) (gameContext.getGraphicBounds().getTop() + margin.top),
	    	(int) (gameContext.getGraphicBounds().getBottom() - margin.bottom));
    }

    public GraphicsViewLimits(IGameContext gameContext)
    {
    	super((int) gameContext.getGraphicBounds().getLeft(),
    			(int) gameContext.getGraphicBounds().getRight(),
    			(int) gameContext.getGraphicBounds().getTop(),
    			(int) gameContext.getGraphicBounds().getBottom());
    }
}
