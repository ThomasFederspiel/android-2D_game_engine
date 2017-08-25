package se.federspiel.android.game;

import se.federspiel.android.backgrounds.trajectories.AbstractTrajectory;
import se.federspiel.android.game.geometry.MovementRectangle;
import se.federspiel.android.game.geometry.Point;
import se.federspiel.android.game.geometry.Rectangle;
import se.federspiel.android.game.interfaces.IGameContext;
import se.federspiel.android.game.interfaces.ITrajectoryControlledBackground;
import se.federspiel.android.game.interfaces.IUpdatableComponent;

public class GameView implements ITrajectoryControlledBackground, IUpdatableComponent
{
	private MovementRectangle mBounds = MovementRectangle.Zero.clone();

	private IGameContext mGameContext = null;
	
	private AbstractTrajectory mTrajectory = null;
	
	public GameView(IGameContext context)
	{
		mGameContext = context;
		
		mBounds.setDimensions(mGameContext.getGraphicBounds().getWidth(), mGameContext.getGraphicBounds().getHeight());
	}

	public void setGameViewPosition(float x, float y)
	{
		mBounds.setPositionLimited(x, y);
	}

	public void resetGameViewLimits()
	{
		mBounds.resetLimits();
	}

	public void setGameViewLimits(float leftLimit, float topLimit, float rightLimit, float bottomLimit)
	{
		mBounds.setLimits(leftLimit, topLimit, rightLimit, bottomLimit);
	}

	public void moveGameViewPosition(float dx, float dy)
	{
		mBounds.moveLimited(dx, dy);
	}

	public void setTrajectory(AbstractTrajectory trajectory)
	{
		mTrajectory = trajectory;

		if (mTrajectory != null)
		{
			mTrajectory.setTrajectoryControlledBackground(this);
			
			mGameContext.getGameEngine().addUpdateComponent(this);
		}
		else
		{
			mGameContext.getGameEngine().removeUpdateComponent(this);
		}
	}
	
	@Override
	public Rectangle getBounds()
	{
		return mBounds;
	}
	
	@Override
	public Point getPosition()
	{
		return mBounds.getPosition();
	}
	
	@Override
	public void onPositionChanged(Point position) 
	{
		mBounds.setPositionLimited(position.X, position.Y);
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
		mTrajectory.updatePosition(gameTime);
	}
}
