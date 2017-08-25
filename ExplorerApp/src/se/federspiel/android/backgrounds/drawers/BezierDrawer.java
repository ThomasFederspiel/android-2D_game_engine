package se.federspiel.android.backgrounds.drawers;

import se.federspiel.android.game.GameRenderer;
import se.federspiel.android.game.geometry.Point;
import se.federspiel.android.game.geometry.Rectangle;
import se.federspiel.android.game.interfaces.IBounds;
import se.federspiel.android.game.interfaces.IGraphicDrawer;
import se.federspiel.android.game.utils.AMath;
import se.federspiel.android.game.utils.AMath.BezierFactors;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;

import com.example.explorerapp.AInstrumentation;

public class BezierDrawer implements IGraphicDrawer
{
	public enum PointIconType
	{
		DOT,
		SQUARE
	}
	
	private BezierFactors[] mFactors = null;

	private Point[] mControlPoints = null;
	
	private Point[] mPath = null;
	
	private BezierFactors mTmpFactors = new BezierFactors();
	private Point mPreviousPoint = Point.Zero.clone();
	
	private Paint mLineColor = new Paint();
	private Paint mPointColor = new Paint();

	private boolean mDrawPoints = false;
	private PointIconType mPointIcon = PointIconType.DOT;
	private int mPointSize = 0;
	
	private Rectangle mBounds = Rectangle.Zero.clone();
	
	public BezierDrawer(Point[] controlPoints)
	{
		evaluateControlPoints(controlPoints);

		mLineColor.setColor(Color.WHITE);
		mLineColor.setStyle(Style.STROKE);
	}
	
	public BezierDrawer(Point[] path, float scale)
	{
		if (AInstrumentation.SHOW_BKG_DRAWER_CTRL)
		{
			mPath = path;
		}
		
		evaluateControlPoints(AMath.bezierInterpolateControlPoints(path, scale));
		
		mLineColor.setColor(Color.WHITE);
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
		
		for (int i = 0; i < mFactors.length; i++)
		{
			mTmpFactors.set(mFactors[i]);
			
			mPreviousPoint.set(mTmpFactors.mFirstPoint);
			
			for (int j = 0; j <= mTmpFactors.mSteps; j++)
			{
				float newX = mPreviousPoint.X + mTmpFactors.mDfx;
				float newY = mPreviousPoint.Y + mTmpFactors.mDfy;
				
				canvas.drawLine(mPreviousPoint.X, mPreviousPoint.Y, newX, newY, mLineColor);
				
				mPreviousPoint.set(newX, newY);
				
				mTmpFactors.takeAStep();
			}
		}
		
		if (mDrawPoints)
		{
			for (int i = 0; i < mControlPoints.length; i += 3)
			{
				switch (mPointIcon)
				{
					case DOT :
						canvas.drawCircle(mControlPoints[i].X, mControlPoints[i].Y, mPointSize, mPointColor);
						break;
						
					case SQUARE :
						canvas.drawRect(mControlPoints[i].X - mPointSize, mControlPoints[i].Y - mPointSize, 
								mControlPoints[i].X + mPointSize, mControlPoints[i].Y + mPointSize, mPointColor);
						break;
				}
			}
		}
		
		
		if (AInstrumentation.SHOW_BKG_DRAWER)
		{
			Paint paint = new Paint();
			paint.setColor(Color.RED);
			paint.setStyle(Style.FILL);
			
			if (AInstrumentation.SHOW_BKG_DRAWER_CTRL)
			{
				for (int j = 0; j < mControlPoints.length; j++)
				{
					canvas.drawCircle(mControlPoints[j].X, mControlPoints[j].Y, 10, paint);

					if (j < (mControlPoints.length - 1))
					{
						canvas.drawLine(mControlPoints[j].X, mControlPoints[j].Y, mControlPoints[j + 1].X, mControlPoints[j + 1].Y, mLineColor);
					}
				}
				
				if (mPath != null)
				{
					paint.setColor(Color.GREEN);
					
					for (int j = 0; j < mPath.length; j++)
					{
						canvas.drawCircle(mPath[j].X, mPath[j].Y, 10, paint);
					}
				}
			}
		}
	}

    private void evaluateControlPoints(Point[] controlPoints)
    {
		if ((controlPoints.length >= 4) && (((controlPoints.length - 4) % 3) == 0))
		{
			int size = 1 + (controlPoints.length - 4) / 3;
			
			mFactors = new BezierFactors[size];
	
			for (int i = 1; i < controlPoints.length; i += 3)
			{
				 mFactors[i / 3] = calculateFactors(controlPoints, i - 1, new BezierFactors());
			}
			
			mControlPoints = controlPoints;
			
		    evaluateBounds(mFactors);
		}
    }
    
    private BezierFactors calculateFactors(Point[] controlPoints, int startIndex, BezierFactors factors)
    {
    	Point point1 = controlPoints[startIndex];
		Point point2 = controlPoints[startIndex + 1];
		Point point3 = controlPoints[startIndex + 2];
		Point point4 = controlPoints[startIndex + 3];

	    AMath.bezierCalculateFactors(point1, point2, point3, point4, 30, factors);
		
		return factors;
    }

    private void evaluateBounds(BezierFactors[] factors)
	{
		float minX = Float.MAX_VALUE;
		float minY = Float.MAX_VALUE;
		
		float maxX = Float.MIN_VALUE;
		float maxY = Float.MIN_VALUE;
		
		if ((factors != null) && factors.length > 0)
		{
			for (int i = 0; i < factors.length; i++)
			{
				mTmpFactors.set(factors[i]);
				
				mPreviousPoint.set(mTmpFactors.mFirstPoint);
				
				minX = Math.min(minX, mPreviousPoint.X);
				minY = Math.min(minY, mPreviousPoint.Y);
				maxX = Math.max(maxX, mPreviousPoint.X);
				maxY = Math.max(maxY, mPreviousPoint.Y);
				
				for (int j = 0; j <= mTmpFactors.mSteps; j++)
				{
					float newX = mPreviousPoint.X + mTmpFactors.mDfx;
					float newY = mPreviousPoint.Y + mTmpFactors.mDfy;
					
					minX = Math.min(minX, newX);
					minY = Math.min(minY, newY);
					maxX = Math.max(maxX, newX);
					maxY = Math.max(maxY, newY);

					mPreviousPoint.set(newX, newY);
					
					mTmpFactors.takeAStep();
				}
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
