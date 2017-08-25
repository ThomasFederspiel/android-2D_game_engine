package se.federspiel.android.backgrounds;

import java.util.ArrayList;

import se.federspiel.android.game.GameRenderer;
import se.federspiel.android.game.geometry.Rectangle;
import se.federspiel.android.game.interfaces.IGameContext;
import se.federspiel.android.game.interfaces.IScrollableBackground;
import se.federspiel.android.util.ImageTools;
import se.federspiel.android.util.ImageTools.IColorMatcher;
import android.graphics.Bitmap;
import android.graphics.Canvas;

public class ImageSpriteBackground extends AbstractBackground implements IScrollableBackground
{
	private int mResourceId = 0;
	
	private Rectangle mCanvasRect = Rectangle.Zero.clone();
	private Rectangle mRendererRect = Rectangle.Zero.clone();
	private Rectangle mMovementRect = Rectangle.Zero.clone();
	
	private ScrollDirections mScrollDirections = ScrollDirections.SCROLL_DIRECTION_NONE;

	private IColorMatcher mSpriteIdentifier = null;

	private ArrayList<BackgroundSprite> mBackgroundSprites = new ArrayList<BackgroundSprite>();
	
	public ImageSpriteBackground(IGameContext gameContext)
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

    public void setImageSpriteIdentifier(IColorMatcher spriteIdentifier)
    {
    	mSpriteIdentifier = spriteIdentifier;
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
   		
    	switch (mScrollDirections)
    	{
	    	case SCROLL_DIRECTION_X :
	    		mBounds.setLimits(Float.NaN, 0, Float.NaN, mGameContext.getGraphicBounds().getHeight());
	    		break;
	    	
	    	case SCROLL_DIRECTION_Y :
	    		mBounds.setLimits(0, Float.NaN, mGameContext.getGraphicBounds().getWidth(), Float.NaN);
	    		break;

	    	case SCROLL_DIRECTION_XY :
	    		mBounds.setLimits(Float.NaN, Float.NaN, Float.NaN, Float.NaN);
	    		break;
	    	
	    	case SCROLL_DIRECTION_NONE :
	    	default :
	    		// Taken care of below
	    		mBounds.setLimits(0, 0, mGameContext.getGraphicBounds().getWidth(), mGameContext.getGraphicBounds().getHeight());
	    		break;
    	}
    	
    	assert mSpriteIdentifier != null;

    	ArrayList<Rectangle> imageBounds = ImageTools.getImageObjectBounds(resourceImage, mSpriteIdentifier);
			
		for (int i = 0; i < imageBounds.size(); i++)
		{
			BackgroundSprite sprite = new BackgroundSprite(mGameContext);

			int resourceId = mGameContext.getImageManager().addBitmap(ImageTools.extractBitmap(resourceImage, imageBounds.get(i)));

			sprite.setBitmapResource(resourceId);		
			sprite.setBounds(imageBounds.get(i));		

			sprite.loadContent();
			
			mBackgroundSprites.add(sprite);
		}
		
    	setDimensions(resourceImage.getWidth(), resourceImage.getHeight());
    	
		mGameContext.getImageManager().deallocateBitmap(mResourceId);
	}

	@Override
	public void unloadContent()
	{
		super.unloadContent();
		
		for (int i = 0; i < mBackgroundSprites.size(); i++)
		{
			BackgroundSprite sprite = mBackgroundSprites.get(i);

			sprite.unloadContent();
		}
		
		mBackgroundSprites.clear();
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

			mMovementRect.setPosition(mMovementRect.getLeft() % getBounds().getWidth(), mMovementRect.getTop() % getBounds().getHeight());
				
			drawBounded(renderer, mMovementRect);
		}
		else
		{
			for (int i = 0; i < mBackgroundSprites.size(); i++)
			{
				BackgroundSprite sprite = mBackgroundSprites.get(i);

				sprite.draw(renderer.getCanvas());
			}
		}
	}
	
	private void drawBounded(GameRenderer renderer, Rectangle movementRect)
	{
		int rightExtent = (int) (mBounds.getWidth() - movementRect.getRight());
		int leftExtent = (int) movementRect.getLeft();
		
		int bottomExtent = (int) (mBounds.getHeight() - movementRect.getBottom());
		int topExtent = (int) movementRect.getTop();
		
		if ((bottomExtent >= 0) && (topExtent >= 0) && (rightExtent >= 0) && (leftExtent >= 0))
		{
			mCanvasRect.set(movementRect);
			mCanvasRect.setPosition(0, 0);
			
			mRendererRect.set(movementRect);
			
			for (int i = 0; i < mBackgroundSprites.size(); i++)
			{
				BackgroundSprite sprite = mBackgroundSprites.get(i);

				sprite.draw(renderer.getCanvas(), mRendererRect, mCanvasRect);
			}
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
				
				for (int i = 0; i < mBackgroundSprites.size(); i++)
				{
					BackgroundSprite sprite = mBackgroundSprites.get(i);
	
					sprite.draw(renderer.getCanvas(), mRendererRect, mCanvasRect);
				}
				
				mCanvasRect.set(movementRect);
				mCanvasRect.setPosition(mCanvasRect.getWidth() + rightExtent, 0);
				mCanvasRect.setDimensions(-rightExtent, mCanvasRect.getHeight());
				
				mRendererRect.set(movementRect);
				mRendererRect.setPosition(0, 0);
				mRendererRect.setDimensions(-rightExtent, mRendererRect.getHeight());
				
				for (int i = 0; i < mBackgroundSprites.size(); i++)
				{
					BackgroundSprite sprite = mBackgroundSprites.get(i);
	
					sprite.draw(renderer.getCanvas(), mRendererRect, mCanvasRect);
				}
			}
			else if (leftExtent < 0)
			{
				mCanvasRect.set(movementRect);
				mCanvasRect.setPosition(-leftExtent, 0);
				mCanvasRect.setDimensions(mCanvasRect.getWidth() + leftExtent, mCanvasRect.getHeight());
	
				mRendererRect.set(movementRect);
				mRendererRect.setPosition(0, 0);
				mRendererRect.setDimensions(mRendererRect.getWidth() + leftExtent, mRendererRect.getHeight());
				
				for (int i = 0; i < mBackgroundSprites.size(); i++)
				{
					BackgroundSprite sprite = mBackgroundSprites.get(i);
	
					sprite.draw(renderer.getCanvas(), mRendererRect, mCanvasRect);
				}
				
				mCanvasRect.set(movementRect);
				mCanvasRect.setPosition(0, 0);
				mCanvasRect.setDimensions(-leftExtent, mCanvasRect.getHeight());
				
				mRendererRect.set(movementRect);
				mRendererRect.setPosition(mBounds.getWidth() + leftExtent, 0);
				mRendererRect.setDimensions(-leftExtent, mRendererRect.getHeight());
				
				for (int i = 0; i < mBackgroundSprites.size(); i++)
				{
					BackgroundSprite sprite = mBackgroundSprites.get(i);
	
					sprite.draw(renderer.getCanvas(), mRendererRect, mCanvasRect);
				}
			}
		
			if (bottomExtent < 0)
			{
				mCanvasRect.set(movementRect);
				mCanvasRect.setPosition(0, 0);
				mCanvasRect.setDimensions(mCanvasRect.getWidth(), mCanvasRect.getHeight() + bottomExtent);
	
				mRendererRect.set(movementRect);
				mRendererRect.setDimensions(mRendererRect.getWidth(), mRendererRect.getHeight() + bottomExtent);
				
				for (int i = 0; i < mBackgroundSprites.size(); i++)
				{
					BackgroundSprite sprite = mBackgroundSprites.get(i);
	
					sprite.draw(renderer.getCanvas(), mRendererRect, mCanvasRect);
				}
				
				mCanvasRect.set(movementRect);
				mCanvasRect.setPosition(0, mCanvasRect.getHeight() + bottomExtent);
				mCanvasRect.setDimensions(mCanvasRect.getWidth(), -bottomExtent);

				mRendererRect.set(movementRect);
				mRendererRect.setPosition(0, 0);
				mRendererRect.setDimensions(mRendererRect.getWidth(), -bottomExtent);
				
				for (int i = 0; i < mBackgroundSprites.size(); i++)
				{
					BackgroundSprite sprite = mBackgroundSprites.get(i);
	
					sprite.draw(renderer.getCanvas(), mRendererRect, mCanvasRect);
				}
			}
			else if (topExtent < 0)
			{
				mCanvasRect.set(movementRect);
				mCanvasRect.setPosition(0, -topExtent);
				mCanvasRect.setDimensions(mCanvasRect.getWidth(), mCanvasRect.getHeight() + topExtent);
	
				mRendererRect.set(movementRect);
				mRendererRect.setPosition(0, 0);
				mRendererRect.setDimensions(mRendererRect.getWidth(), mRendererRect.getHeight() + topExtent);
				
				for (int i = 0; i < mBackgroundSprites.size(); i++)
				{
					BackgroundSprite sprite = mBackgroundSprites.get(i);
	
					sprite.draw(renderer.getCanvas(), mRendererRect, mCanvasRect);
				}
				
				mCanvasRect.set(movementRect);
				mCanvasRect.setPosition(0, 0);
				mCanvasRect.setDimensions(mCanvasRect.getWidth(), -topExtent);
				
				mRendererRect.set(movementRect);
				mRendererRect.setPosition(0, mBounds.getHeight() + topExtent);
				mRendererRect.setDimensions(mRendererRect.getWidth(), -topExtent);
				
				for (int i = 0; i < mBackgroundSprites.size(); i++)
				{
					BackgroundSprite sprite = mBackgroundSprites.get(i);
	
					sprite.draw(renderer.getCanvas(), mRendererRect, mCanvasRect);
				}
			}
		}
	}

    private static class BackgroundSprite
    {
    	private IGameContext mGameContext = null;
    	
    	private Bitmap mImage = null;
    	private int mResourceId = 0;
    	private Rectangle mBounds = Rectangle.Zero.clone();
    	
    	public BackgroundSprite(IGameContext gameContext)
    	{
    		mGameContext = gameContext;
    	}
    	
        public void setBitmapResource(int resourceId)
        {
        	mResourceId = resourceId;
        }
    	
        public void setBounds(Rectangle bounds)
        {
        	mBounds = bounds;
        }

        public void loadContent()
    	{
        	mImage = mGameContext.getImageManager().allocateBitmap(mResourceId);
    	}

    	public void unloadContent()
    	{
    		mGameContext.getImageManager().deallocateBitmap(mResourceId);
    	}

    	public void draw(Canvas canvas, Rectangle renderArea, Rectangle canvasArea)
    	{
    		if (renderArea.intersects(mBounds))
    		{
        		canvas.drawBitmap(mImage, canvasArea.getLeft() + (mBounds.getLeft() - renderArea.getLeft()) , 
        				canvasArea.getTop() + (mBounds.getTop() - renderArea.getTop()) , null);
    		}
    	}

    	public void draw(Canvas canvas)
    	{
			canvas.drawBitmap(mImage, mBounds.getLeft(), mBounds.getTop(), null);
    	}
    }
}
