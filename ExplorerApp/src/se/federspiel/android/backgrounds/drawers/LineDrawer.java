package se.federspiel.android.backgrounds.drawers;

import se.federspiel.android.game.GameRenderer;
import se.federspiel.android.game.geometry.Point;
import se.federspiel.android.game.geometry.Rectangle;
import se.federspiel.android.game.interfaces.IBounds;
import se.federspiel.android.game.interfaces.IGraphicDrawer;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;

import com.example.explorerapp.AInstrumentation;

public class LineDrawer implements IGraphicDrawer
{
	public enum PointIconType
	{
		DOT,
		SQUARE
	}
	
	private Point[] mPath = null;
	
	private Paint mLineColor = new Paint();
	private Paint mPointColor = new Paint();

	private boolean mDrawPoints = false;
	private PointIconType mPointIcon = PointIconType.DOT;
	private int mPointSize = 0;

	private Rectangle mBounds = Rectangle.Zero.clone();
	
	public LineDrawer(Point[] path)
	{
		mPath = path;

		evaluateBounds(mPath);
		
		mLineColor.setColor(Color.BLACK);
		mLineColor.setStyle(Style.STROKE);
	}
	
	public void setLineColor(int color)
	{
		mLineColor.setColor(color);
	}
	
	public void setLineWidth(int width)
	{
		mLineColor.setStrokeWidth(width);
	}

	public void setDrawPathPoints(int color, PointIconType type, int size)
	{
		mPointColor.setColor(color);
		mPointColor.setStyle(Style.FILL);
		
		mPointIcon = type;
		mDrawPoints = true;
		
		switch (type)
		{
			case DOT :
				mPointSize = size / 2;
				break;
				
			case SQUARE :
				mPointSize = size / 2;
				break;
		}
	}
	
	@Override
	public IBounds getBounds() 
	{
		return mBounds;
	}
	
	@Override
	public void draw(GameRenderer renderer)
	{			
		Canvas canvas = renderer.getCanvas();		
		
		for (int i = 0; i < (mPath.length - 1); i++)
		{
			canvas.drawLine(mPath[i].X, mPath[i].Y, mPath[i + 1].X, mPath[i + 1].Y, mLineColor);
		}

		if (mDrawPoints)
		{
			for (int i = 0; i < mPath.length; i++)
			{
				switch (mPointIcon)
				{
					case DOT :
						canvas.drawCircle(mPath[i].X, mPath[i].Y, mPointSize, mPointColor);
						break;
						
					case SQUARE :
						canvas.drawRect(mPath[i].X - mPointSize, mPath[i].Y - mPointSize, 
								mPath[i].X + mPointSize, mPath[i].Y + mPointSize, mPointColor);
						break;
				}
			}
		}
		
		if (AInstrumentation.SHOW_BKG_DRAWER)
		{
			Paint paint = new Paint();
			paint.setColor(Color.RED);
			paint.setStyle(Style.FILL);
			
			for (int i = 0; i < mPath.length; i++)
			{
				canvas.drawCircle(mPath[i].X, mPath[i].Y, 10, paint);
			}
		}
	}
	
	private void evaluateBounds(Point[] path)
	{
		float minX = Float.MAX_VALUE;
		float minY = Float.MAX_VALUE;
		
		float maxX = Float.MIN_VALUE;
		float maxY = Float.MIN_VALUE;
		
		if ((path != null) && path.length > 0)
		{
			for (int i = 0; i < path.length; i++)
			{
				Point point = path[i];
				
				minX = Math.min(minX, point.X);
				minY = Math.min(minY, point.Y);
				maxX = Math.max(maxX, point.X);
				maxY = Math.max(maxY, point.Y);
			}
		}
		else
		{
			minX = 0;
			minY = 0;
			maxX = 0;
			maxY = 0;
		}
		
		mBounds.setPosition(minX, minY);
		mBounds.setDimensions((int) (maxX - minX), (int) (maxY - minY));
	}
}
