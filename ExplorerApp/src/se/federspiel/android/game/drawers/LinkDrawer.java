package se.federspiel.android.game.drawers;

import se.federspiel.android.game.GameRenderer;
import se.federspiel.android.game.GameTime;
import se.federspiel.android.game.geometry.Point;
import se.federspiel.android.game.geometry.Rectangle;
import se.federspiel.android.game.interfaces.IBounds;
import se.federspiel.android.game.interfaces.IDrawableComponent;
import se.federspiel.android.game.interfaces.IGameContext;
import se.federspiel.android.game.utils.AMath;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class LinkDrawer implements IDrawableComponent
{
    private IGameContext mGameContext = null;

    private Point mStart = null;
    private Point mEnd = null;
    
    private int mStrokeArea = -1;
    
    private Rectangle mBoundingBox = Rectangle.Zero.clone();
	
    private Paint mLinkPaint = null;
    
    private DrawableZOrder mZOrder = DrawableZOrder.SPRITE_LAYER_1;

    public LinkDrawer(IGameContext gameContext)
    {
    	mGameContext = gameContext;
    	
    	mLinkPaint = new Paint(); 
		
		mLinkPaint.setColor(Color.WHITE); 
		mLinkPaint.setStrokeWidth(1); 
    }

	public void setColor(int color)
	{
		mLinkPaint.setColor(color);
	}
	
	public void setStrokeArea(int area)
	{
		mStrokeArea = area;
	}

	public void setStrokeWidth(int width)
	{
		mLinkPaint.setStrokeWidth(width); 
	}

	public void setLine(Point start, Point end)
    {
    	evaluateDimensions(start, end);
    }
    
    @Override
	public IBounds getBounds()
	{
		return mBoundingBox;
	}
    
	@Override
	public void draw(GameRenderer renderer)
	{
		Canvas canvas = renderer.getCanvas();
		
	    canvas.drawLine(mStart.X, mStart.Y, mEnd.X, mEnd.Y, mLinkPaint); 
	}

	@Override
	public void update(GameTime gameTime)
	{
		if (mStrokeArea > 0)
		{
			updateStrokeWidth();
		}
	}

	@Override
	public void loadContent()
	{
	}

	@Override
	public void unloadContent()
	{
	}

	@Override
	public DrawableZOrder getZOrder()
	{
		return mZOrder;
	}

	@Override
	public void setZOrder(DrawableZOrder level)
	{
		mZOrder = level;
	}

	private void updateStrokeWidth()
	{
		float length = AMath.distance(mStart, mEnd);

		if (length > 0)
		{
			float strokeWidth = mStrokeArea / length;
	
			if (strokeWidth < 1)
			{
				strokeWidth = 1;
			}
		
			mLinkPaint.setStrokeWidth(strokeWidth);
		}
		else
		{
			mLinkPaint.setStrokeWidth(mStrokeArea);
		}
	}
	
	private void evaluateDimensions(Point start, Point end)
	{
		mStart = start;
		mEnd = end;
		
		float dx = end.X - start.X;
		float dy = end.Y - start.Y;
		
		mBoundingBox.setDimensions(Math.abs(Math.round(dx)), Math.abs(Math.round(dy)));
	}
}
