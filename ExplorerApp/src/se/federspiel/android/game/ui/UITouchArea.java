package se.federspiel.android.game.ui;

import se.federspiel.android.game.GameTime;
import se.federspiel.android.game.geometry.Dimensions;
import se.federspiel.android.game.geometry.Point;
import se.federspiel.android.game.interfaces.IBounds;
import se.federspiel.android.game.interfaces.IUserInputManager.TouchEvent;
import se.federspiel.android.game.ui.UIInputComponent.UIIOnTouchListener;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;

public class UITouchArea extends UIAbstractComponent implements UIIOnTouchListener
{
	private ITouchAreaListener mTouchAreaListener = null;

	private Paint mBorderPaint = null;

	private boolean mBorderEnabled = false;
	
	private RectF mBorderRect = new RectF();
	private Point mTmpDraw = Point.Zero.clone();
	private TouchAreaEvent mTouchAreaEvent = new TouchAreaEvent();

	private IUITouchAreaAlgorithm mAlgorithm = null;
	
	public UITouchArea()
	{
		super(-1, -1);
		
		init();
	}
	
	public UITouchArea(int width, int height)
	{
		super(width, height);
		
		init();
	}
	
	public UITouchArea(int x, int y, int width, int height)
	{
		super(x, y, width, height);
		
		init();
	}

	public void setAlgorithm(IUITouchAreaAlgorithm algorithm)
	{
		mAlgorithm = algorithm;
		
		mAlgorithm.setUITouchAreaComponent(this);
	}
	
	public void setBorder(boolean enable)
	{
		mBorderEnabled = enable;
	}

	public void setOnTouchAreaListener(ITouchAreaListener listener)
	{
		if (listener == null)
		{
			if (mTouchAreaListener != null)
			{
				removeOnTouchListener(this);
			}
		}
		else
		{
			if (mTouchAreaListener == null)
			{
				setOnTouchListener(this);
			}
		}
		
		mTouchAreaListener = listener;
	}

	public IBounds getTouchAreaBounds()
	{
		return getComponentBounds();
	}
	
	@Override
	public void setDimensions(int width, int height)
	{
		super.setDimensions(width, height);

		if (mAlgorithm != null)
		{	
			mAlgorithm.onBoundsChanged();
		}
	}

	@Override
	public void setDimensions(Dimensions dim)
	{
		super.setDimensions(dim);
		
		if (mAlgorithm != null)
		{	
			mAlgorithm.onBoundsChanged();
		}
	}
	
	@Override
	public boolean onTouch(UIInputComponent component, TouchEvent event)
	{
		assert mTouchAreaListener != null;
		assert mAlgorithm != null;
		
		if (mAlgorithm != null)
		{
			if (mAlgorithm.handleOnTouch(component, event, mTouchAreaEvent))
			{
				mTouchAreaListener.onTouch(this, mTouchAreaEvent);
			}
		}
		else
		{
			mTouchAreaEvent.touchAreaAction = toTouchAreaEvent(event.action);
			mTouchAreaEvent.x = event.x;
			mTouchAreaEvent.y = event.y;
			mTouchAreaEvent.dx = 0;
			mTouchAreaEvent.dy = 0;
			
			mTouchAreaListener.onTouch(this, mTouchAreaEvent);
		}
		
		return true;
	}

	@Override
	public void draw(Point parentPosition, Canvas canvas)
	{
		if (mBorderEnabled)
		{
			mTmpDraw.set(parentPosition);
			mTmpDraw.addToThis(mParentOffset);
			
			mBorderRect.set(mTmpDraw.X, mTmpDraw.Y, mTmpDraw.X + mBounds.getWidth(), mTmpDraw.Y + mBounds.getHeight()); 
	
			canvas.drawRoundRect(mBorderRect, 5, 5, mBorderPaint);
		}
	}
	
	@Override
	void update(GameTime gameTime)
	{
		if (mAlgorithm != null)
		{
			if (mAlgorithm.update(gameTime, mTouchAreaEvent))
			{
				mTouchAreaListener.onTouch(this, mTouchAreaEvent);
			}
		}
	}
	
	private void init()
	{
		mBorderPaint = new Paint();
		mBorderPaint.setColor(Color.BLACK);
		mBorderPaint.setStyle(Style.STROKE);
		mBorderPaint.setStrokeWidth(1);
	}

	private TouchAreaEvent.TouchAreaAction toTouchAreaEvent(TouchEvent.TouchAction action)
	{
		TouchAreaEvent.TouchAreaAction touchAction = TouchAreaEvent.TouchAreaAction.POINTER_DOWN;
		
		switch (action)
		{
			case POINTER_DOWN :
				touchAction = TouchAreaEvent.TouchAreaAction.POINTER_DOWN;
				break;
				
			case POINTER_UP :
				touchAction = TouchAreaEvent.TouchAreaAction.POINTER_UP;
				break;
				
			case POINTER_MOVE :
				touchAction = TouchAreaEvent.TouchAreaAction.POINTER_MOVE;
				break;
		}
		
		return touchAction;
	}
	
	public static class TouchAreaEvent
	{
		public enum TouchAreaAction
		{
			POINTER_DOWN,
			POINTER_UP,
			POINTER_MOVE;
		}
		
		public TouchAreaAction touchAreaAction = TouchAreaAction.POINTER_DOWN;
		
		public float x = 0;
		public float y = 0;
		public float dx = 0;
		public float dy = 0;
	}
	
	public interface ITouchAreaListener
	{
		public void onTouch(UITouchArea component, TouchAreaEvent event); 
	}

	public interface IUITouchAreaAlgorithm
	{
		public void onBoundsChanged();
		public boolean handleOnTouch(UIInputComponent component, TouchEvent event, TouchAreaEvent touchAreaEvent);
		public boolean update(GameTime gameTime, TouchAreaEvent touchAreaEvent);
		public void setUITouchAreaComponent(UITouchArea component);
	}
}
