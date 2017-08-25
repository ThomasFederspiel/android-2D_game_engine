package se.federspiel.android.game.ui;

import se.federspiel.android.game.GameApplication;
import se.federspiel.android.game.geometry.Point;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;

public class UILabel extends UIAbstractComponent
{
	public enum VerticalAlign
	{
		TOP,
		CENTER,
		BOTTOM
	}
	
	public enum HorizontalAlign
	{
		LEFT,
		CENTER,
		RIGHT
	}

	private String mText = "";

	private Paint mLabelPaint = new Paint();

	private HorizontalAlign mHorizontalAlignment = HorizontalAlign.LEFT;
	private VerticalAlign mVerticalAlignment = VerticalAlign.TOP;

	private Rect mTextBounds = new Rect();

	private Point mDrawPosition = Point.Zero.clone();
	
	public UILabel()
	{
		super(-1, -1);
		
		init();
	}
	
	public UILabel(int width, int height)
	{
		super(width, height);
		
		init();
	}
	
	public UILabel(int x, int y, int width, int height)
	{
		super(x, y, width, height);

		init();
		
		correctAlignPosition();
	}

	@Override
	public void setParentOffset(float x, float y)
	{
		super.setParentOffset(x, y);
		
		mDrawPosition.set(x, y);
		
		correctAlignPosition();
	}
	
	@Override
	public void draw(Point parentPosition, Canvas canvas)
	{
		canvas.drawText(mText, mDrawPosition.X + parentPosition.X, mDrawPosition.Y + parentPosition.Y, mLabelPaint);
	}
	
	public void setHorizontalAlignment(HorizontalAlign align)
	{
		mHorizontalAlignment = align;
		
		correctAlignPosition();
	}
	
	public void setVerticalAlignment(VerticalAlign align)
	{
		mVerticalAlignment = align;
		
		correctAlignPosition();
	}

	public void setText(String text)
	{
		mText = text;
		
		updateTextBounds();
	}
	
	public void setText(int resourceId)
	{
		mText = GameApplication.getResources().getString(resourceId);
		
		updateTextBounds();
	}

	private void init()
	{
		mLabelPaint.setColor(Color.BLACK);
		mLabelPaint.setStyle(Style.FILL);
		mLabelPaint.setStrokeWidth(1);

		mLabelPaint.setTextSize(GameApplication.getFontManager().getDefaultFontSize());
		mLabelPaint.setTypeface(GameApplication.getFontManager().getDefaultFontTypeFace());
	}
	
	private void updateTextBounds()
	{
		mLabelPaint.getTextBounds(mText, 0, mText.length(), mTextBounds);
		
		if ((mPreferredDimensions.getWidth() == -1) || (mPreferredDimensions.getHeight() == -1))
		{
			mPreferredDimensions.setDimensions(mTextBounds.width(), mTextBounds.height());
		}

		setInvalidate(true);
		
		correctAlignPosition();
	}
	
	private void correctAlignPosition()
	{
		switch (mHorizontalAlignment)
		{
			case CENTER :
				mDrawPosition.X = mParentOffset.X + mBounds.getWidth() / 2;
				
				mLabelPaint.setTextAlign(Paint.Align.CENTER);
					
//				mDrawPosition.X = mParentOffset.X + mTextBounds.width() / 2;
				break;
				
			case RIGHT :
				mDrawPosition.X = mParentOffset.X + mBounds.getWidth();
				
				mLabelPaint.setTextAlign(Paint.Align.RIGHT);
				
//				mDrawPosition.X = mParentOffset.X + mTextBounds.width();
				break;
				
			case LEFT :
			default :
				mLabelPaint.setTextAlign(Paint.Align.LEFT);
				
				mDrawPosition.X = mParentOffset.X;
				break;
		}
		
		switch (mVerticalAlignment)
		{
			case CENTER :
				mDrawPosition.Y = mParentOffset.Y - mTextBounds.top + mBounds.getHeight() / 2 -  mTextBounds.height() / 2;
				
//				mDrawPosition.Y = mParentOffset.Y - mTextBounds.top;
				break;
				
			case BOTTOM :
				mDrawPosition.Y = mParentOffset.Y - mTextBounds.top + mBounds.getHeight() - mTextBounds.height();
				
//				mDrawPosition.Y = mParentOffset.Y - mTextBounds.top;
				break;
				
			case TOP :
			default :
				mDrawPosition.Y = mParentOffset.Y - mTextBounds.top;
				
//				mDrawPosition.Y = mParentOffset.Y - mTextBounds.top;
				break;
		}
	}
}
