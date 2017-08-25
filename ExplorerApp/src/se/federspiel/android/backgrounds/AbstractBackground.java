package se.federspiel.android.backgrounds;

import se.federspiel.android.backgrounds.trajectories.AbstractTrajectory;
import se.federspiel.android.game.GameTime;
import se.federspiel.android.game.geometry.MovementRectangle;
import se.federspiel.android.game.geometry.Point;
import se.federspiel.android.game.geometry.Rectangle;
import se.federspiel.android.game.interfaces.IBackground;
import se.federspiel.android.game.interfaces.IBounds;
import se.federspiel.android.game.interfaces.IGameContext;

public abstract class AbstractBackground implements IBackground
{
	protected IGameContext mGameContext = null;

	private DrawableZOrder mZOrder = DrawableZOrder.BACKGROUND_LAYER_1;

	private AbstractTrajectory mTrajectory = null;
	
	protected MovementRectangle mBounds = MovementRectangle.Zero.clone();
	
	public AbstractBackground(IGameContext gameContext)
    {
		mGameContext = gameContext;
		
		Rectangle graphicBounds = mGameContext.getGraphicBounds();
		
		setDimensions(graphicBounds.getWidth(), graphicBounds.getHeight());
		
		mBounds.setLimits(0, 0, graphicBounds.getWidth(), graphicBounds.getHeight());
		
		setPosition(0, 0);
    }

	@Override
	public DrawableZOrder getZOrder()
	{
		return mZOrder;
	}
	
	@Override
	public void setZOrder(DrawableZOrder level)
	{
		mZOrder = level;
	}
	
	@Override
	public IBounds getBounds()
	{
		return mBounds;
	}
	
	@Override
	public Point getPosition()
	{
		return mBounds.getPosition();
	}

	@Override
	public void loadContent()
	{
		if (mTrajectory != null)
		{
			mTrajectory.setup();
		}
	}

	@Override
	public void unloadContent()
	{
		if (mTrajectory != null)
		{
			mTrajectory.teardown();
		}
	}

	@Override
	public void update(GameTime gameTime)
	{
		if (mTrajectory != null)
		{
			mTrajectory.updatePosition(gameTime);
		}
	}

	@Override
	public void setTrajectory(AbstractTrajectory trajectory)
	{
		mTrajectory = trajectory;
		
		mTrajectory.setTrajectoryControlledBackground(this);
	}
	
	@Override
	public void onPositionChanged(Point position)
	{
		mBounds.setPositionLimited(position.X, position.Y);
	}
    
	protected void setDimensions(int width, int height)
	{
		mBounds.setDimensions(width, height);
	}
	
	protected void setPosition(float x, float y)
	{
		mBounds.setPosition(x, y);
	}
}
