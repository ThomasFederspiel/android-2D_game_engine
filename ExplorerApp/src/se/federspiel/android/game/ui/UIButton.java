package se.federspiel.android.game.ui;

import com.example.explorerapp.R;

import se.federspiel.android.game.GameApplication;
import se.federspiel.android.game.geometry.Dimensions;
import se.federspiel.android.game.geometry.Point;
import se.federspiel.android.game.interfaces.IUserInputManager.TouchEvent;
import se.federspiel.android.game.ui.UIInputComponent.UIIOnTouchListener;
import se.federspiel.android.game.ui.UILabel.HorizontalAlign;
import se.federspiel.android.game.ui.UILabel.VerticalAlign;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;

public class UIButton extends UIAbstractComponent implements UIIOnTouchListener
{
	private static final float TEXT_AREA = 0.95f;
	
	private UILabel mText = null;
	private Paint mBorderPaint = null;

	private RectF mBorderRect = new RectF();
	private Point mTmpDraw = Point.Zero.clone();
	
	public UIButton()
	{
		super(-1, -1);
		
		init();
	}
	
	public UIButton(int width, int height)
	{
		super(width, height);
		
		init();
	}
	
	public UIButton(int x, int y, int width, int height)
	{
		super(x, y, width, height);

		init();
	}

	@Override
	public void setDimensions(int width, int height)
	{
		super.setDimensions(width, height);
		
		updateTextLabel();
	}

	@Override
	public void setDimensions(Dimensions dim)
	{
		super.setDimensions(dim);
		
		updateTextLabel();
	}
	
	@Override
	public void loadContent()
	{
		super.loadContent();
		
		GameApplication.getGameContext().getSoundManager().addSound(R.raw.tick_01);
		
		setOnTouchListener(this);
	}
	
	@Override
	public void unloadContent()
	{
		super.unloadContent();
		
		removeOnTouchListener(this);
	}

	@Override
	public void draw(Point parentPosition, Canvas canvas)
	{
		mTmpDraw.set(parentPosition);
		mTmpDraw.addToThis(mParentOffset);
		
		mBorderRect.set(mTmpDraw.X, mTmpDraw.Y, mTmpDraw.X + mBounds.getWidth(), mTmpDraw.Y + mBounds.getHeight()); 

		canvas.drawRoundRect(mBorderRect, 5, 5, mBorderPaint);
		
		if (mText != null)
		{
			mText.draw(mTmpDraw, canvas);
		}
	}
	
	public void setHorizontalTextAlignment(HorizontalAlign align)
	{
		createTextLabel();
		
		mText.setHorizontalAlignment(align);
	}
	
	public void setVerticalTextAlignment(VerticalAlign align)
	{
		createTextLabel();
		
		mText.setVerticalAlignment(align);
	}

	public void setText(String text)
	{
		createTextLabel();
		
		mText.setText(text);
	}
	
	public void setText(int resourceId)
	{
		createTextLabel();
		
		mText.setText(resourceId);
	}


	@Override
	public boolean onTouch(UIInputComponent component, TouchEvent event)
	{
		switch (event.action)
		{
			case POINTER_DOWN :
				GameApplication.getGameContext().getSoundManager().playSound(R.raw.tick_01);
				break;
				
			case POINTER_UP :
				GameApplication.getGameContext().getSoundManager().playSound(R.raw.tick_01);
				break;
			
			default :
				break;
		}
		
		return true;
	}
	
	private void init()
	{
		mBorderPaint = new Paint();
		mBorderPaint.setColor(Color.BLACK);
		mBorderPaint.setStyle(Style.STROKE);
		mBorderPaint.setStrokeWidth(1);
	}

	private void updateTextLabel()
	{
		if (mText != null)
		{
			mText.setDimensions((int) (mBounds.getWidth() * TEXT_AREA), (int) (mBounds.getHeight() * TEXT_AREA));
			mText.setParentOffset((mBounds.getWidth() * (1 - TEXT_AREA)) / 2, (mBounds.getHeight() * (1 - TEXT_AREA)) / 2);
		}
	}
	
	private void createTextLabel()
	{
		if (mText == null)
		{
			mText = new UILabel((int) (mBounds.getWidth() * (1 - TEXT_AREA)) / 2, (int) (mBounds.getHeight() * (1 - TEXT_AREA)) / 2,
					(int) (mBounds.getWidth() * TEXT_AREA), (int) (mBounds.getHeight() * TEXT_AREA));
			mText.setHorizontalAlignment(HorizontalAlign.CENTER);
			mText.setVerticalAlignment(VerticalAlign.CENTER);
		}
	}
}
