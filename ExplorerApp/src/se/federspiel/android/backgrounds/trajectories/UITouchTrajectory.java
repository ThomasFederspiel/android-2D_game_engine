package se.federspiel.android.backgrounds.trajectories;

import se.federspiel.android.game.GameTime;
import se.federspiel.android.game.geometry.Point;
import se.federspiel.android.game.ui.UITouchArea;
import se.federspiel.android.game.ui.UITouchArea.ITouchAreaListener;
import se.federspiel.android.game.ui.UITouchArea.TouchAreaEvent;

public class UITouchTrajectory extends AbstractTrajectory implements ITouchAreaListener
{
	public enum Directions
	{
		DIRECTION_X_POSITIVE,
		DIRECTION_X_NEGATIVE,
		DIRECTION_Y_POSITIVE,
		DIRECTION_Y_NEGATIVE
	}
	
	private float mSpeedPxPerNS = 0;

	private boolean mIsActive = false;
	
	private Point mTmpPosition = Point.Zero.clone();
	
	private UITouchArea mUITouchArea = null;
	private Directions mDirection = Directions.DIRECTION_X_POSITIVE;
	
	public UITouchTrajectory(UITouchArea touchArea, Directions direction)
	{
		mUITouchArea = touchArea;
		mDirection = direction;
	}
	
	// px/s
	public void setSpeed(float speed)
	{
		switch (mDirection)
		{
			case DIRECTION_X_POSITIVE :
			case DIRECTION_Y_POSITIVE :
				mSpeedPxPerNS = speed / 1E9f;
				break;
				
			case DIRECTION_X_NEGATIVE :
			case DIRECTION_Y_NEGATIVE :
				mSpeedPxPerNS = -speed / 1E9f;
				break;
		}
	}
	
	@Override
	public void onTouch(UITouchArea component, TouchAreaEvent event) 
	{
		switch (event.touchAreaAction)
		{
			case POINTER_DOWN :
				mIsActive = true;
				break;
				
			case POINTER_MOVE :
				
				if (component.getTouchAreaBounds().contains(event.x, event.y))
				{
					mIsActive = true;
				}
				else
				{
					mIsActive = false;
				}
				
				break;
				
			case POINTER_UP :
				mIsActive = false;
				break;
		}
	}
	
	@Override
	public void setup()
	{
		mUITouchArea.setOnTouchAreaListener(this);
	}

	@Override
	public void teardown()
	{
		mUITouchArea.setOnTouchAreaListener(null);
	}
	
	@Override
	public void updatePosition(GameTime gameTime) 
	{
		if (mIsActive)
		{
	    	mTmpPosition.set(mTrajectoryControlledBackground.getPosition());
	    	
			switch (mDirection)
			{
				case DIRECTION_X_NEGATIVE :
				case DIRECTION_X_POSITIVE :
					mTmpPosition.move(mSpeedPxPerNS * gameTime.getElapsedTime(), 0);
					break;
					
				case DIRECTION_Y_POSITIVE :
				case DIRECTION_Y_NEGATIVE :
					mTmpPosition.move(0, mSpeedPxPerNS * gameTime.getElapsedTime());
					break;
			}
			
    		mTrajectoryControlledBackground.onPositionChanged(mTmpPosition);
		}
	}
}
