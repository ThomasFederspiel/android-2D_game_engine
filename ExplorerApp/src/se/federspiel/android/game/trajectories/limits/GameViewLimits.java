package se.federspiel.android.game.trajectories.limits;

import se.federspiel.android.game.GameView;
import se.federspiel.android.game.geometry.Rectangle;
import se.federspiel.android.game.interfaces.IBounds;
import se.federspiel.android.game.interfaces.IGameContext;

public class GameViewLimits extends BoundsLimits
{
	private int mXMinDelta = 0;
	private int mXMaxDelta = 0;
	private int mYMinDelta = 0;
	private int mYMaxDelta = 0;
	
	private GameView mGameView = null;
	
    public GameViewLimits(IGameContext gameContext, float xMinFraction, float xMaxFraction, float yMinFraction, float yMaxFraction)
    {
    	super((int) gameContext.getGameEngine().getGameView().getBounds().getLeft(), 
    			(int) gameContext.getGameEngine().getGameView().getBounds().getRight(), 
    			(int) gameContext.getGameEngine().getGameView().getBounds().getTop(), 
    			(int) gameContext.getGameEngine().getGameView().getBounds().getBottom());
    	
    	assert (xMinFraction >= 0.0) && (xMinFraction <= 1.0);
    	assert (xMaxFraction >= 0.0) && (xMaxFraction <= 1.0);
    	assert (yMinFraction >= 0.0) && (yMinFraction <= 1.0);
    	assert (yMaxFraction >= 0.0) && (yMaxFraction <= 1.0);
    	
		mGameView = gameContext.getGameEngine().getGameView();
		
		Rectangle bounds = mGameView.getBounds();
		
    	mXMinDelta = (int) (bounds.getWidth() * xMinFraction); 
    	mXMaxDelta = (int) (bounds.getWidth() * (xMaxFraction - 1.0f));
		
    	mYMinDelta = (int) (bounds.getHeight() * yMinFraction); 
    	mYMaxDelta = (int) (bounds.getHeight() * (yMaxFraction - 1.0f));
    }
    
	@Override
    public float limitX(IBounds bounds)
    {
		Rectangle viewBounds = mGameView.getBounds();
		
		setXLimits((int) (viewBounds.getLeft() + mXMinDelta), 
				(int) (viewBounds.getRight() + mXMaxDelta));
		
		return super.limitX(bounds);
    }
	
	@Override
    public float limitY(IBounds bounds)
    {
		Rectangle viewBounds = mGameView.getBounds();

		setYLimits((int) (viewBounds.getTop() + mYMinDelta), 
				(int) (viewBounds.getBottom() + mYMaxDelta));
		
		return super.limitY(bounds);
    }
}
 
