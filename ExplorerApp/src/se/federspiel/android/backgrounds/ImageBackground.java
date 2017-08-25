package se.federspiel.android.backgrounds;

import se.federspiel.android.game.GameRenderer;
import se.federspiel.android.game.geometry.Rectangle;
import se.federspiel.android.game.interfaces.IGameContext;
import se.federspiel.android.game.interfaces.IScrollableBackground;
import se.federspiel.android.util.ImageTools;
import android.graphics.Bitmap;

public class ImageBackground extends AbstractBackground implements IScrollableBackground
{
	private Bitmap mImage = null;
	private int mResourceId = 0;
	
	private Rectangle mCanvasRect = Rectangle.Zero.clone();
	private Rectangle mRendererRect = Rectangle.Zero.clone();
	private Rectangle mMovementRect = Rectangle.Zero.clone();
	
	private ScrollDirections mScrollDirections = ScrollDirections.SCROLL_DIRECTION_NONE;

	public ImageBackground(IGameContext gameContext)
    {
		super(gameContext);
    }

    public void setBitmapResource(int resourceId)
    {
    	mResourceId = resourceId;
    }

    public int getBitmapResource()
    {
    	return mResourceId;
    }

	@Override
    public void setScrollDirection(ScrollDirections direction)
    {
    	mScrollDirections = direction;
    }
    
	@Override
	public void loadContent()
	{
    	super.loadContent();

    	assert mResourceId != 0;

    	Bitmap resourceImage = mGameContext.getImageManager().allocateBitmap(mResourceId);
   		
    	mImage = null;
    			
    	switch (mScrollDirections)
    	{
	    	case SCROLL_DIRECTION_X :
	    		mImage = ImageTools.extendBitmap(resourceImage, mGameContext.getGraphicBounds().getWidth(), resourceImage.getHeight());
	    		mBounds.setLimits(Float.NaN, 0, Float.NaN, mGameContext.getGraphicBounds().getHeight());
	    		break;
	    	
	    	case SCROLL_DIRECTION_Y :
	    		mImage = ImageTools.extendBitmap(resourceImage, resourceImage.getWidth(), mGameContext.getGraphicBounds().getHeight());
	    		mBounds.setLimits(0, Float.NaN, mGameContext.getGraphicBounds().getWidth(), Float.NaN);
	    		break;

	    	case SCROLL_DIRECTION_XY :
	    		mImage = ImageTools.extendBitmap(resourceImage, mGameContext.getGraphicBounds().getWidth(), mGameContext.getGraphicBounds().getHeight());
	    		mBounds.setLimits(Float.NaN, Float.NaN, Float.NaN, Float.NaN);
	    		break;
	    	
	    	case SCROLL_DIRECTION_NONE :
	    	default :
	    		// Taken care of below
	    		mBounds.setLimits(0, 0, mGameContext.getGraphicBounds().getWidth(), mGameContext.getGraphicBounds().getHeight());
	    		break;
    	}
    	
		if (mImage != null)
		{
			int newResourceId = mGameContext.getImageManager().allocateBitmap(mImage);
			
			mGameContext.getImageManager().deallocateBitmap(mResourceId);
			
			mResourceId = newResourceId;
		}
		else
		{
			mImage = resourceImage;
		}

    	assert mImage != null;

    	setDimensions(mImage.getWidth(), mImage.getHeight());
	}

	@Override
	public void unloadContent()
	{
		super.unloadContent();
		
		mGameContext.getImageManager().deallocateBitmap(mResourceId);
	}

	@Override
	public void draw(GameRenderer renderer)
	{
		if (mScrollDirections != ScrollDirections.SCROLL_DIRECTION_NONE)
		{
			if (renderer.isBounded())
			{
				mMovementRect.set(renderer.getBounds());
				mMovementRect.setPosition(mMovementRect.getLeft() + mBounds.getLeft(), mMovementRect.getTop() + mBounds.getTop());
			}		
			else
			{
				mMovementRect.set(mBounds);
			}
			
			mMovementRect.setPosition(mMovementRect.getLeft() % mImage.getWidth(), mMovementRect.getTop() % mImage.getHeight());
				
			drawBounded(renderer, mMovementRect);
		}
		else
		{
			renderer.getCanvas().drawBitmap(mImage, 0, 0, null);
		}
	}
	
	private void drawBounded(GameRenderer renderer, Rectangle movementRect)
	{
		int rightExtent = (int) (mImage.getWidth() - movementRect.getRight());
		int leftExtent = (int) movementRect.getLeft();

		int bottomExtent = (int) (mImage.getHeight() - movementRect.getBottom());
		int topExtent = (int) movementRect.getTop();

		if ((rightExtent >= 0) && (leftExtent >= 0) && (bottomExtent >= 0) && (topExtent >= 0))
		{
			mCanvasRect.set(movementRect);
			mCanvasRect.setPosition(0, 0);
			
			mRendererRect.set(movementRect);
			
			renderer.getCanvas().drawBitmap(mImage, mRendererRect.getAndroidRect(), mCanvasRect.getAndroidRect(), null);
		}
		else
		{
			if (rightExtent < 0)
			{
				mCanvasRect.set(movementRect);
				mCanvasRect.setPosition(0, 0);
				mCanvasRect.setDimensions(mCanvasRect.getWidth() + rightExtent, mCanvasRect.getHeight());
	
				mRendererRect.set(movementRect);
				mRendererRect.setDimensions(mRendererRect.getWidth() + rightExtent, mRendererRect.getHeight());
				
				renderer.getCanvas().drawBitmap(mImage, mRendererRect.getAndroidRect(), mCanvasRect.getAndroidRect(), null);
				
				mCanvasRect.set(movementRect);
				mCanvasRect.setPosition(mCanvasRect.getWidth() + rightExtent, 0);
				mCanvasRect.setDimensions(-rightExtent, mCanvasRect.getHeight());
				
				mRendererRect.set(movementRect);
				mRendererRect.setPosition(0, 0);
				mRendererRect.setDimensions(-rightExtent, mRendererRect.getHeight());
				
				renderer.getCanvas().drawBitmap(mImage, mRendererRect.getAndroidRect(), mCanvasRect.getAndroidRect(), null);
			}
			else if (leftExtent < 0)
			{
				mCanvasRect.set(movementRect);
				mCanvasRect.setPosition(-leftExtent, 0);
				mCanvasRect.setDimensions(mCanvasRect.getWidth() + leftExtent, mCanvasRect.getHeight());
	
				mRendererRect.set(movementRect);
				mRendererRect.setPosition(0, 0);
				mRendererRect.setDimensions(mRendererRect.getWidth() + leftExtent, mRendererRect.getHeight());
				
				renderer.getCanvas().drawBitmap(mImage, mRendererRect.getAndroidRect(), mCanvasRect.getAndroidRect(), null);
				
				mCanvasRect.set(movementRect);
				mCanvasRect.setPosition(0, 0);
				mCanvasRect.setDimensions(-leftExtent, mCanvasRect.getHeight());
				
				mRendererRect.set(movementRect);
				mRendererRect.setPosition(mImage.getWidth() + leftExtent, 0);
				mRendererRect.setDimensions(-leftExtent, mRendererRect.getHeight());
				
				renderer.getCanvas().drawBitmap(mImage, mRendererRect.getAndroidRect(), mCanvasRect.getAndroidRect(), null);
			}
		
			if (bottomExtent < 0)
			{
				mCanvasRect.set(movementRect);
				mCanvasRect.setPosition(0, 0);
				mCanvasRect.setDimensions(mCanvasRect.getWidth(), mCanvasRect.getHeight() + bottomExtent);
	
				mRendererRect.set(movementRect);
				mRendererRect.setDimensions(mRendererRect.getWidth(), mRendererRect.getHeight() + bottomExtent);
				
				renderer.getCanvas().drawBitmap(mImage, mRendererRect.getAndroidRect(), mCanvasRect.getAndroidRect(), null);
				
				mCanvasRect.set(movementRect);
				mCanvasRect.setPosition(0, mCanvasRect.getHeight() + bottomExtent);
				mCanvasRect.setDimensions(mCanvasRect.getWidth(), -bottomExtent);
				
				mRendererRect.set(movementRect);
				mRendererRect.setPosition(0, 0);
				mRendererRect.setDimensions(mRendererRect.getWidth(), -bottomExtent);
				
				renderer.getCanvas().drawBitmap(mImage, mRendererRect.getAndroidRect(), mCanvasRect.getAndroidRect(), null);
			}
			else if (topExtent < 0)
			{
				mCanvasRect.set(movementRect);
				mCanvasRect.setPosition(0, -topExtent);
				mCanvasRect.setDimensions(mCanvasRect.getWidth(), mCanvasRect.getHeight() + topExtent);
	
				mRendererRect.set(movementRect);
				mRendererRect.setPosition(0, 0);
				mRendererRect.setDimensions(mRendererRect.getWidth(), mRendererRect.getHeight() + topExtent);
				
				renderer.getCanvas().drawBitmap(mImage, mRendererRect.getAndroidRect(), mCanvasRect.getAndroidRect(), null);
				
				mCanvasRect.set(movementRect);
				mCanvasRect.setPosition(0, 0);
				mCanvasRect.setDimensions(mCanvasRect.getWidth(), -topExtent);
				
				mRendererRect.set(movementRect);
				mRendererRect.setPosition(0, mImage.getHeight() + topExtent);
				mRendererRect.setDimensions(mRendererRect.getWidth(), -topExtent);
				
				renderer.getCanvas().drawBitmap(mImage, mRendererRect.getAndroidRect(), mCanvasRect.getAndroidRect(), null);
			}
		}
	}
}
