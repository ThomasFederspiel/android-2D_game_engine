package se.federspiel.android.game;

import se.federspiel.android.game.geometry.Rectangle;
import se.federspiel.android.game.interfaces.IBounds;
import android.graphics.Canvas;

public class GameRenderer
{
	Canvas mCanvas = null;
	Rectangle mBounds = null;
	
	public GameRenderer()
	{
	}
	
	public GameRenderer(Canvas canvas)
	{
		mCanvas = canvas;
	}
	
	public Canvas getCanvas()
	{
		return mCanvas;
	}

	public Rectangle getBounds()
	{
		return mBounds;
	}
	
	public void setBounds(Rectangle bounds)
	{
		mBounds = bounds;
	}

	public boolean isBounded()
	{
		return (mBounds != null);
	}
	
	public boolean isWithinBounds(IBounds bounds)
	{
		boolean within = true;
		
		if (mBounds != null)
		{
			within = mBounds.intersects(mBounds);
		}
		
		return within;
	}
}
