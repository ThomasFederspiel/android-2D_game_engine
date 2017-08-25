package se.federspiel.android.backgrounds;

import java.util.ArrayList;

import se.federspiel.android.backgrounds.trajectories.AbstractTrajectory;
import se.federspiel.android.game.GameRenderer;
import se.federspiel.android.game.GameTime;
import se.federspiel.android.game.geometry.Point;
import se.federspiel.android.game.interfaces.IGameContext;
import se.federspiel.android.game.interfaces.IScrollableBackground;

public class ParallaxBackground extends AbstractBackground implements IScrollableBackground
{
	private ArrayList<BackgroundItem> mBackgrounds = new ArrayList<BackgroundItem>();
	
	private ScrollDirections mScrollDirections = ScrollDirections.SCROLL_DIRECTION_NONE;

	private Point mTmpPosition = Point.Zero.clone();
	
	public ParallaxBackground(IGameContext gameContext)
    {
		super(gameContext);
    }

    public void addBackground(IScrollableBackground background, float dragFactor)
	{
		mBackgrounds.add(new BackgroundItem(background, dragFactor));
		
		background.setScrollDirection(mScrollDirections);
		background.setTrajectory(new ParallaxTrajectory(dragFactor));
	}

	@Override
	public void setScrollDirection(ScrollDirections direction) 
	{
		mScrollDirections = direction;
		
		for (int i = 0; i < mBackgrounds.size(); i++)
		{
			mBackgrounds.get(i).mBackground.setScrollDirection(direction);
		}
	}
	
	@Override
	public void loadContent()
	{
		super.loadContent();
		
		for (int i = 0; i < mBackgrounds.size(); i++)
		{
			mBackgrounds.get(i).mBackground.loadContent();
		}
	}

	@Override
	public void unloadContent()
	{
		super.unloadContent();
		
		for (int i = 0; i < mBackgrounds.size(); i++)
		{
			mBackgrounds.get(i).mBackground.unloadContent();
		}
	}

	@Override
	public void update(GameTime gameTime)
	{
		super.update(gameTime);
		
		for (int i = 0; i < mBackgrounds.size(); i++)
		{
			mBackgrounds.get(i).mBackground.update(gameTime);
		}
	}

	@Override
	public void draw(GameRenderer renderer) 
	{
		if (renderer.isBounded())
		{
			mTmpPosition.set(renderer.getBounds().getPosition());
		
			for (int i = 0; i < mBackgrounds.size(); i++)
			{
				BackgroundItem item = mBackgrounds.get(i);
						
				renderer.getBounds().setPosition(mTmpPosition.X * item.mDragFactor, mTmpPosition.Y * item.mDragFactor);
				
				item.mBackground.draw(renderer);
			}
			
			renderer.getBounds().setPosition(mTmpPosition);
		}
		else
		{
			for (int i = 0; i < mBackgrounds.size(); i++)
			{
				mBackgrounds.get(i).mBackground.draw(renderer);
			}
		}
	}

	public class ParallaxTrajectory extends AbstractTrajectory
	{
		private float mDragFactor = 1.0f;
		
		private Point mTmpPosition = Point.Zero.clone();
		
		public ParallaxTrajectory(float dragFactor)
		{
			mDragFactor = dragFactor;
		}
		
		@Override
		public void updatePosition(GameTime gameTime) 
		{
			mTmpPosition.set(getPosition());
			
			mTmpPosition.set(mTmpPosition.X * mDragFactor, mTmpPosition.Y * mDragFactor);
			
			mTrajectoryControlledBackground.onPositionChanged(mTmpPosition);
		}
	}
	
	private static class BackgroundItem
	{
		IScrollableBackground mBackground = null;
		
		float mDragFactor = 1.0f;
				
		public BackgroundItem(IScrollableBackground background, float dragFactor)
		{
			mBackground = background;
			mDragFactor = dragFactor;
		}
	}
}
