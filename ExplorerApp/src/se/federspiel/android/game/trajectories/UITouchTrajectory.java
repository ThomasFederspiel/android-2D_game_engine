package se.federspiel.android.game.trajectories;

import java.util.ArrayList;

import se.federspiel.android.game.GameTime;
import se.federspiel.android.game.geometry.Vector2;
import se.federspiel.android.game.interfaces.IGameContext;
import se.federspiel.android.game.interfaces.ITrajectoryControlledSprite;
import se.federspiel.android.game.ui.UITouchArea;
import se.federspiel.android.game.ui.UITouchArea.ITouchAreaListener;
import se.federspiel.android.game.ui.UITouchArea.TouchAreaEvent;

public class UITouchTrajectory extends AbstractTrajectory
{
	public enum Directions
	{
		DIRECTION_X_POSITIVE,
		DIRECTION_X_NEGATIVE,
		DIRECTION_Y_POSITIVE,
		DIRECTION_Y_NEGATIVE
	}

	private ArrayList<TouchArea> mTouchAreas = new ArrayList<TouchArea>();

	private Vector2 mInitialSpeed = Vector2.Zero.clone();
	private Vector2 mInitialAcceleration = Vector2.Zero.clone();
	
    public UITouchTrajectory(IGameContext gameContext)
    {
    	super(gameContext);
    }
    
	public UITouchTrajectory(IGameContext gameContext, ITrajectoryControlledSprite sprite)
    {
    	super(gameContext, sprite);
    }

	public void addTouchArea(UITouchArea touchArea, Directions direction)
	{
		for (int i = 0; i < mTouchAreas.size(); i++)
		{
			TouchArea area = mTouchAreas.get(i);
			
			assert (area.mTouchArea != touchArea); 
			assert (area.mDirection != direction); 
		}
		
		TouchArea area = new TouchArea(touchArea, direction); 

		area.setSpeed(mInitialSpeed.X, mInitialSpeed.Y);
		area.setAcceleration(mInitialAcceleration.X, mInitialAcceleration.Y);
		
		mTouchAreas.add(area);
	}
	
	@Override
    public void setInitialSpeed(Vector2 speed)
    {
		setInitialSpeed(speed.X, speed.Y);
    }
	
	@Override
    public void setInitialSpeed(float x, float y)
    {
		mInitialSpeed.set(x, y);

		for (int i = 0; i < mTouchAreas.size(); i++)
		{
			TouchArea area = mTouchAreas.get(i);
			
			area.setSpeed(mInitialSpeed.X, mInitialSpeed.Y);
		}
    }

	@Override
    public void setInitialAcceleration(Vector2 acceleration)
	{
		setInitialAcceleration(acceleration.X, acceleration.Y);
    }
	
	@Override
    public void setInitialAcceleration(float accX, float accY)
    {
		mInitialAcceleration.set(accX, accY);

		for (int i = 0; i < mTouchAreas.size(); i++)
		{
			TouchArea area = mTouchAreas.get(i);
			
			area.setAcceleration(mInitialAcceleration.X, mInitialAcceleration.Y);
		}
    }

	@Override
	public void setup()
	{
		super.setup();
		
		boolean xIsControlled = false;
		boolean yIsControlled = false;
		
		for (int i = 0; i < mTouchAreas.size(); i++)
		{
			TouchArea area = mTouchAreas.get(i);
			
			area.setup();

			xIsControlled = xIsControlled || area.isControllingX();
			yIsControlled = yIsControlled || area.isControllingY();
		}

		super.setInitialSpeed(xIsControlled ? 0 : mInitialSpeed.X, yIsControlled ? 0 : mInitialSpeed.Y);
		super.setInitialAcceleration(xIsControlled ? 0 : mInitialAcceleration.X, yIsControlled ? 0 : mInitialAcceleration.Y);
	}

	@Override
	public void teardown()
	{
		super.teardown();
		
		for (int i = 0; i < mTouchAreas.size(); i++)
		{
			mTouchAreas.get(i).teardown();
		}
	}
	
	@Override
    public boolean update(GameTime gameTime)
    {
        return updatePosition(gameTime);
    }

	private class TouchArea implements ITouchAreaListener
	{
		public UITouchArea mTouchArea = null;
		public Directions mDirection = Directions.DIRECTION_X_POSITIVE;

		private float mSpeed = 0;
		private float mAcceleration = 0;
		
		private boolean mIsActive = false;
		
		public TouchArea(UITouchArea touchArea, Directions direction)
		{
			mTouchArea = touchArea;
			mDirection = direction;
		}

		public boolean isControllingX()
		{
			return ((mDirection == Directions.DIRECTION_X_NEGATIVE) || (mDirection == Directions.DIRECTION_X_POSITIVE));
		}
		
		public boolean isControllingY()
		{
			return ((mDirection == Directions.DIRECTION_Y_NEGATIVE) || (mDirection == Directions.DIRECTION_Y_POSITIVE));
		}
		
		public void setSpeed(float x, float y)
		{
			switch (mDirection)
			{
				case DIRECTION_X_NEGATIVE :
					mSpeed = -x;
					break;
					
				case DIRECTION_X_POSITIVE :
					mSpeed = x;
					break;
					
				case DIRECTION_Y_POSITIVE :
					mSpeed = y;
					break;
					
				case DIRECTION_Y_NEGATIVE :
					mSpeed = -y;
					break;
			}
		}
		
		public void setAcceleration(float x, float y)
		{
			switch (mDirection)
			{
				case DIRECTION_X_NEGATIVE :
				case DIRECTION_X_POSITIVE :
					mAcceleration = x;
					break;
					
				case DIRECTION_Y_POSITIVE :
				case DIRECTION_Y_NEGATIVE :
					mAcceleration = y;
					break;
			}
		}

		public void setup()
		{
			mTouchArea.setOnTouchAreaListener(this);
		}

		public void teardown()
		{
			mTouchArea.setOnTouchAreaListener(null);
		}
		
		@Override
		public void onTouch(UITouchArea component, TouchAreaEvent event) 
		{
			switch (event.touchAreaAction)
			{
				case POINTER_DOWN :
					setSpeed(mSpeed);
					setAcceleration(mAcceleration);

					mIsActive = true;
					break;
					
				case POINTER_MOVE :
					
					if (component.getTouchAreaBounds().contains(event.x, event.y))
					{
						if (!mIsActive)
						{
							setSpeed(mSpeed);
							setAcceleration(mAcceleration);
							
							mIsActive = true;
						}
					}
					else
					{
						if (mIsActive)
						{
							setSpeed(0);
							setAcceleration(0);
							
							mIsActive = false;
						}
					}
					
					break;
					
				case POINTER_UP :
					setSpeed(0);
					setAcceleration(0);
					
					mIsActive = false;
					break;
			}
		}
		
		private void setSpeed(float speed)
		{
			switch (mDirection)
			{
				case DIRECTION_X_NEGATIVE :
				case DIRECTION_X_POSITIVE :
					setSpeedX(speed);
					break;
					
				case DIRECTION_Y_POSITIVE :
				case DIRECTION_Y_NEGATIVE :
					setSpeedY(speed);
					break;
			}
		}

		private void setAcceleration(float acceleration)
		{
			switch (mDirection)
			{
				case DIRECTION_X_NEGATIVE :
				case DIRECTION_X_POSITIVE :
					setAccelerationX(acceleration);
					break;
					
				case DIRECTION_Y_POSITIVE :
				case DIRECTION_Y_NEGATIVE :
					setAccelerationY(acceleration);
					break;
			}
		}
	}
}
