package se.federspiel.android.game.ui;

import se.federspiel.android.game.GameApplication;
import se.federspiel.android.game.geometry.Point;
import se.federspiel.android.game.interfaces.IImageManager.ScaleOperator;
import android.graphics.Bitmap;
import android.graphics.Canvas;

public class UITouchButton extends UITouchArea
{
	private int mResourceId = 0;
	
	private Bitmap mImage = null;

	private Point mTmpDraw = Point.Zero.clone();

	public UITouchButton()
	{
		super(-1, -1);
	}
	
	public UITouchButton(int width, int height)
	{
		super(width, height);
	}
	
	public UITouchButton(int x, int y, int width, int height)
	{
		super(x, y, width, height);
	}

    public void setBitmapResource(int resourceId)
    {
    	mResourceId = resourceId;
    }
    
	@Override
	public void loadContent()
	{
		super.loadContent();
		
    	if ((mPreferredDimensions.getWidth() > -1) && (mPreferredDimensions.getHeight() > -1))
    	{
    		mImage = GameApplication.getGameContext().getImageManager().allocateBitmap(mResourceId, mPreferredDimensions.getWidth(),
    				mPreferredDimensions.getHeight(), ScaleOperator.Scale);
    	}
    	else
    	{
    		mImage = GameApplication.getGameContext().getImageManager().allocateBitmap(mResourceId);
    	}
    	
    	assert mImage != null;
	}
	
	@Override
	public void unloadContent()
	{
		super.unloadContent();
		
    	if ((mPreferredDimensions.getWidth() > -1) && (mPreferredDimensions.getHeight() > -1))
    	{
			GameApplication.getGameContext().getImageManager().deallocateBitmap(mResourceId, mPreferredDimensions.getWidth(),
    				mPreferredDimensions.getHeight());
		}
		else
		{
			GameApplication.getGameContext().getImageManager().deallocateBitmap(mResourceId);
		}
	}

	@Override
	public void draw(Point parentPosition, Canvas canvas)
	{
		mTmpDraw.set(parentPosition);
		mTmpDraw.addToThis(mParentOffset);

    	canvas.drawBitmap(mImage, mTmpDraw.X, mTmpDraw.Y, null);
		
		super.draw(parentPosition, canvas);
	}
}
