package se.federspiel.android.game.ui.touch;

import se.federspiel.android.game.GameTime;
import se.federspiel.android.game.geometry.Point;
import se.federspiel.android.game.interfaces.IUserInputManager.TouchEvent;
import se.federspiel.android.game.ui.UIInputComponent;
import se.federspiel.android.game.ui.UITouchArea;
import se.federspiel.android.game.ui.UITouchArea.TouchAreaEvent;

public class UITouchAreaVelocityAlgorithm implements UITouchArea.IUITouchAreaAlgorithm
{
	public enum ControlDirections
	{
		CONTROL_X_POSITIVE,
		CONTROL_X_NEGATIVE,
		CONTROL_Y_POSITIVE,
		CONTROL_Y_NEGATIVE
	}

	private ControlDirections mControlDirection = ControlDirections.CONTROL_X_POSITIVE;
	
	private JPad mJPad = new JPad();
	
	private UITouchArea mUITouchAreaComponent = null;
	
	public UITouchAreaVelocityAlgorithm()
	{
	}

	public void join(UITouchAreaVelocityAlgorithm algorithm)
	{
		algorithm.mJPad.mPosition = mJPad.mPosition;
	}
	
	public void setPosition(float x, float y)
	{
		mJPad.mPosition.set(x, y);
	}

	// px/s
	public void setSpeed(float speed)
	{
		switch (mControlDirection)
		{
			case CONTROL_X_POSITIVE :
			case CONTROL_Y_POSITIVE :
				mJPad.setSpeed(speed);
				break;
				
			case CONTROL_X_NEGATIVE :
			case CONTROL_Y_NEGATIVE :
				mJPad.setSpeed(-speed);
				break;
		}
	}

	public void setControlDirection(ControlDirections direction)
	{
		switch (mControlDirection)
		{
			case CONTROL_X_POSITIVE :
			case CONTROL_Y_POSITIVE :
				
				if ((direction == ControlDirections.CONTROL_X_NEGATIVE) || (direction == ControlDirections.CONTROL_Y_NEGATIVE))
				{
					mJPad.setSpeed(-mJPad.getSpeed());
				}
				break;
				
			case CONTROL_X_NEGATIVE :
			case CONTROL_Y_NEGATIVE :
				
				if ((direction == ControlDirections.CONTROL_X_POSITIVE) || (direction == ControlDirections.CONTROL_Y_POSITIVE))
				{
					mJPad.setSpeed(-mJPad.getSpeed());
				}
				break;
		}
		
		mControlDirection = direction;
		
		switch (mControlDirection)
		{
			case CONTROL_X_POSITIVE :
			case CONTROL_X_NEGATIVE :
				mJPad.mXDirection = true;
				break;
				
			case CONTROL_Y_NEGATIVE :
			case CONTROL_Y_POSITIVE :
				mJPad.mXDirection = false;
				break;
		}
	}

	@Override
	public boolean handleOnTouch(UIInputComponent component, TouchEvent event, TouchAreaEvent touchAreaEvent)
	{
		switch (event.action)
		{
			case POINTER_DOWN :
				mJPad.mIsActive = true;
				break;
				
			case POINTER_MOVE :
				
				if (mUITouchAreaComponent.getTouchAreaBounds().contains(event.x, event.y))
				{
					mJPad.mIsActive = true;
				}
				else
				{
					mJPad.mIsActive = false;
				}
				
				break;
				
			case POINTER_UP :
				mJPad.mIsActive = false;
				break;
		}
		
		return false;
	}

	@Override
	public boolean update(GameTime gameTime, TouchAreaEvent touchAreaEvent)
	{
		if (mJPad.mIsActive)
		{
			mJPad.update(gameTime, touchAreaEvent);			
			
			return true;
		}
		
		return false;
	}
	
	@Override
	public void setUITouchAreaComponent(UITouchArea component)
	{
		mUITouchAreaComponent = component;
	}

	@Override
	public void onBoundsChanged() 
	{
	}
	
	private static class JPad
	{
		private float mSpeedPxPerSec = 0;
		private float mSpeedPxPerNS = 0;

		public boolean mXDirection = false;

		public boolean mIsActive = false;
		
		public Point mPosition = Point.Zero.clone();
		
		// px/s
		public void setSpeed(float speed)
		{
			mSpeedPxPerSec = speed;
			
			mSpeedPxPerNS = mSpeedPxPerSec / 1E9f;
		}
		
		// px/s
		public float getSpeed()
		{
			return mSpeedPxPerSec;
		}

		public void update(GameTime gameTime, TouchAreaEvent touchAreaEvent)
		{
			float dx = 0;
			float dy = 0;
			
			if (mXDirection)
			{
				dx = mSpeedPxPerNS * gameTime.getElapsedTime();
			}
			else
			{
				dy = mSpeedPxPerNS * gameTime.getElapsedTime();
			}
			
			mPosition.move(dx, dy);
			
			touchAreaEvent.dx = dx;
			touchAreaEvent.dy = dy;
			
			touchAreaEvent.x = mPosition.X;
			touchAreaEvent.y = mPosition.Y;
		}
	}
}
