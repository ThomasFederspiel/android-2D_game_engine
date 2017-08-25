package se.federspiel.android.game.ui.touch;

import se.federspiel.android.game.GameTime;
import se.federspiel.android.game.interfaces.IUserInputManager.TouchEvent;
import se.federspiel.android.game.ui.UIInputComponent;
import se.federspiel.android.game.ui.UITouchArea;
import se.federspiel.android.game.ui.UITouchArea.TouchAreaEvent;
import android.graphics.RectF;

public class UITouchAreaDragAlgorithm implements UITouchArea.IUITouchAreaAlgorithm
{
	public enum TouchDirection
	{
		DIRECTION_X,
		DIRECTION_Y,
		DIRECTION_XY
	}

	private static final float ROLL_OUT_FACTOR = (float) Math.pow(0.8, 25 / GameTime.FPS);
	private static final float ROLL_OUT_PX_THRESHOLD = 5;
	
	private RectF mTransformationRect = new RectF();
	private boolean mTransformationSet = false;
	
	private boolean mReverseEnabled = false;

	private boolean mRollOutEnabled = false;
	private RollOutStatus mRollOutStatus = new RollOutStatus();

	private long mLastTouchEventTime = -1;
	private TouchDirection mDirection = TouchDirection.DIRECTION_XY;
	
	private float mTransformScaleX = 1;
	private float mTransformScaleY = 1;
	
	private UITouchArea mUITouchAreaComponent = null;
	
	public UITouchAreaDragAlgorithm()
	{
	}

	public void setTransformationArea(int left, int top, int width, int height)
	{
		mTransformationRect.set(left, top, left + width, top + height);
		
		mTransformationSet = true;
		
		calculateTransformation();
	}

	public void setReverse(boolean enable)
	{
		boolean modified = (mReverseEnabled != enable);
		
		mReverseEnabled = enable;
	
		if (modified)
		{
			mTransformScaleX *= -1;
			mTransformScaleY *= -1;
		}
	}
	
	public void setRollOut(boolean enable)
	{
		mRollOutEnabled = enable;
		
		mLastTouchEventTime = -1;
	}
	
	public void setDirection(TouchDirection direction)
	{
		mDirection = direction;
	}

	@Override
	public void onBoundsChanged()
	{
		calculateTransformation();
	}
	
	@Override
	public boolean handleOnTouch(UIInputComponent component, TouchEvent event, TouchAreaEvent touchAreaEvent)
	{
		switch (event.action)
		{
			case POINTER_DOWN :
				
				mRollOutStatus.enabled = false;
					
				if (mTransformationSet)
				{
					touchAreaEvent.x = transformX(event.x);
					touchAreaEvent.y = transformY(event.y);
				}
				else
				{
					touchAreaEvent.x = event.x;
					touchAreaEvent.y = event.y;
				}
				
				touchAreaEvent.dx = 0;
				touchAreaEvent.dy = 0;
				
				touchAreaEvent.touchAreaAction = TouchAreaEvent.TouchAreaAction.POINTER_DOWN;
				
				if (mRollOutEnabled)
				{
					mLastTouchEventTime = System.nanoTime();

					mRollOutStatus.averageDx = 0;
					mRollOutStatus.averageDy = 0;
					mRollOutStatus.averageEventNs = 0;
				}
				
				break;
				
			case POINTER_MOVE :
				generateUpMoveEvent(event, touchAreaEvent);
				
				touchAreaEvent.touchAreaAction = TouchAreaEvent.TouchAreaAction.POINTER_MOVE;
				
				if (mRollOutEnabled)
				{
					long time = System.nanoTime();
					
					mRollOutStatus.averageEventNs = ((time - mLastTouchEventTime) + mRollOutStatus.averageEventNs) / 2;
					
					mLastTouchEventTime = time;

					if (Math.signum(mRollOutStatus.averageDx) != Math.signum(touchAreaEvent.dx))
					{
						mRollOutStatus.averageDx = touchAreaEvent.dx;
					}
					else
					{
						mRollOutStatus.averageDx = (mRollOutStatus.averageDx + touchAreaEvent.dx) / 2;
					}
					
					if (Math.signum(mRollOutStatus.averageDy) != Math.signum(touchAreaEvent.dy))
					{
						mRollOutStatus.averageDy = touchAreaEvent.dy;
					}
					else
					{
						mRollOutStatus.averageDy = (mRollOutStatus.averageDy + touchAreaEvent.dy) / 2;
					}
				}
				break;
				
			case POINTER_UP :
				generateUpMoveEvent(event, touchAreaEvent);
				
				touchAreaEvent.touchAreaAction = TouchAreaEvent.TouchAreaAction.POINTER_UP;
				
				if (mRollOutEnabled)
				{
					mRollOutStatus.averageDx = (mRollOutStatus.averageDx + touchAreaEvent.dx) / 2;
					mRollOutStatus.averageDy = (mRollOutStatus.averageDy + touchAreaEvent.dy) / 2;
					
					long time = System.nanoTime();
					
					mRollOutStatus.averageEventNs = ((time - mLastTouchEventTime) + mRollOutStatus.averageEventNs) / 2;
					
					evaluateRollOut(touchAreaEvent);
				}
				break;
		}
		
		return true;
	}

	@Override
	public boolean update(GameTime gameTime, TouchAreaEvent touchAreaEvent)
	{
		if (mRollOutStatus.enabled)
		{
			float dx = mRollOutStatus.xVel * gameTime.getElapsedTime();
			float dy = mRollOutStatus.yVel * gameTime.getElapsedTime();
					
			mRollOutStatus.xVel *= ROLL_OUT_FACTOR;
			mRollOutStatus.yVel *= ROLL_OUT_FACTOR;
		
			touchAreaEvent.dx = dx; 
			touchAreaEvent.dy = dy;
			
			touchAreaEvent.x += dx;
			touchAreaEvent.y += dy;
			
			if ((Math.abs(dx) < ROLL_OUT_PX_THRESHOLD) && (Math.abs(dy) < ROLL_OUT_PX_THRESHOLD))
			{
				mRollOutStatus.enabled = false;	
			}
			
			return true;
		}
		
		return false;
	}
	
	@Override
	public void setUITouchAreaComponent(UITouchArea component)
	{
		mUITouchAreaComponent = component;
		
		calculateTransformation();
	}

	private void generateUpMoveEvent(TouchEvent event, TouchAreaEvent touchAreaEvent)
	{
		if (mTransformationSet)
		{
			float x = transformX(event.x);
			float y = transformY(event.y);
			
			touchAreaEvent.dx = x - touchAreaEvent.x; 
			touchAreaEvent.dy = y - touchAreaEvent.y;
			
			touchAreaEvent.x = x;
			touchAreaEvent.y = y;
		}
		else
		{
			touchAreaEvent.dx = event.x - touchAreaEvent.x; 
			touchAreaEvent.dy = event.y - touchAreaEvent.y;
			
			touchAreaEvent.x = event.x;
			touchAreaEvent.y = event.y;
		}

		filterEvent(touchAreaEvent);
	}

	private void evaluateRollOut(TouchAreaEvent event)
	{
		mRollOutStatus.x = event.x;
		mRollOutStatus.y = event.y;
		
		mRollOutStatus.xVel = mRollOutStatus.averageDx / mRollOutStatus.averageEventNs; // px/Ns
		mRollOutStatus.yVel = mRollOutStatus.averageDy / mRollOutStatus.averageEventNs; // px/Ns

		mRollOutStatus.enabled = true;
	}
	
	private TouchAreaEvent filterEvent(TouchAreaEvent event)
	{
		switch (mDirection)
		{
			case DIRECTION_X :
				event.dy = 0;
				event.y = 0;
				break;
				
			case DIRECTION_Y :
				event.dx = 0;
				event.x = 0;
				break;
				
			default :
				break;
		}
		
		return event;
	}
	
	private float transformX(float x)
	{
		return (mTransformationRect.left + (x - mUITouchAreaComponent.getTouchAreaBounds().getLeft()) * mTransformScaleX);
	}

	private float transformY(float y)
	{
		return (mTransformationRect.top + (y - mUITouchAreaComponent.getTouchAreaBounds().getTop()) * mTransformScaleY);
	}

	private void calculateTransformation()
	{
		if (mTransformationSet && (mUITouchAreaComponent != null))
		{
			mTransformScaleX = mTransformationRect.width() / mUITouchAreaComponent.getTouchAreaBounds().getWidth();
			mTransformScaleY = mTransformationRect.height() / mUITouchAreaComponent.getTouchAreaBounds().getHeight();
			
			if (mReverseEnabled)
			{
				mTransformScaleX *= -1;
				mTransformScaleY *= -1;
			}
		}
	}

	public static class RollOutStatus
	{
		public float averageDx = 0;
		public float averageDy = 0;
		public long averageEventNs = 0;
		
		public float x = 0;
		public float y = 0;
		public float xVel = 0; // px/Ns
		public float yVel = 0; // px/Ns
		public boolean enabled = false;
	}
}
