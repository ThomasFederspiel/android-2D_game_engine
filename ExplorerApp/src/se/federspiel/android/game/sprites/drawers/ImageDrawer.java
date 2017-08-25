package se.federspiel.android.game.sprites.drawers;

import se.federspiel.android.game.collision.bounds.CollisionBoundingBox;
import se.federspiel.android.game.collision.bounds.CollisionBoundingCircle;
import se.federspiel.android.game.geometry.Dimensions;
import se.federspiel.android.game.geometry.Point;
import se.federspiel.android.game.geometry.Rectangle;
import se.federspiel.android.game.geometry.RectangleC;
import se.federspiel.android.game.interfaces.IBounds;
import se.federspiel.android.game.interfaces.ICollisionBound;
import se.federspiel.android.game.interfaces.IGameContext;
import se.federspiel.android.game.interfaces.IImageManager;
import se.federspiel.android.game.interfaces.ISprite;
import se.federspiel.android.game.interfaces.IImageManager.ScaleOperator;
import se.federspiel.android.game.interfaces.IImageSpriteDrawer;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;

import com.example.explorerapp.AInstrumentation;

public class ImageDrawer extends BaseDrawer implements IImageSpriteDrawer
{
	private static Paint sSpriteDrawBounds = null; 
	
	private ICollisionBound mCollisionBound = null;
    private IBounds mBoundingBox = null;

    private int mReqWidth = -1;
    private int mReqHeight = -1;
    private ScaleOperator mImageScaleOperator = IImageManager.ScaleOperator.Scale;
    
	private Bitmap mImage = null;
	private int mResourceId = 0;

	private BitmapCollisionBounds mCollisionBoundsType = BitmapCollisionBounds.UNDEFINED;
	private Point mBitmapOriginOffset = Point.Zero.clone();
	
    private Dimensions mDims = Dimensions.Zero.clone();

    {
		sSpriteDrawBounds = new Paint(); 
		
		sSpriteDrawBounds.setColor(Color.BLACK); 
		sSpriteDrawBounds.setStrokeWidth(2); 
		sSpriteDrawBounds.setStyle(Style.STROKE);
    }
    
    public ImageDrawer(IGameContext gameContext)
    {
    	super(gameContext);
    }

    @Override
    public Dimensions getDimensions()
    {
        return mDims;
    }

    @Override
	public IBounds getBounds()
	{
		return mBoundingBox;
	}
    
	@Override
	public void updatePosition(Point position)
	{
    	updateBoundingBox(position);
	}
	
    public void setBitmapResource(int resourceId)
    {
    	setBitmapResource(resourceId, BitmapCollisionBounds.RECT_UPPER_LEFT);
    }
    
	public void setBitmapResource(int resourceId, BitmapCollisionBounds collisionBoundsType)
	{
    	mResourceId = resourceId;

    	mCollisionBoundsType = collisionBoundsType;
    
    	createBounds(mCollisionBoundsType);
	}

	public void setBitmapResource(int resourceId, int width, int height)
	{
    	setBitmapResource(resourceId, width, height, ScaleOperator.Scale, BitmapCollisionBounds.RECT_UPPER_LEFT);
	}

	public void setBitmapResource(int resourceId, int width, int height, ScaleOperator operator)
	{
    	setBitmapResource(resourceId, width, height, operator, BitmapCollisionBounds.RECT_UPPER_LEFT);
	}

	public void setBitmapResource(int resourceId, int width, int height,
			BitmapCollisionBounds collisionBoundsType)
	{
    	setBitmapResource(resourceId, width, height, ScaleOperator.Scale, collisionBoundsType);
	}
	
	public void setBitmapResource(int resourceId, int width, int height,
			ScaleOperator operator, BitmapCollisionBounds collisionBoundsType)
	{
		assert width < 1;
		assert height < 1;
		
		setBitmapResource(resourceId, collisionBoundsType);
    	
        mReqWidth = width;
        mReqHeight = height;
        mImageScaleOperator = operator;
	}
	
    @Override
    public ICollisionBound getCollisionBounds()
    {
        return mCollisionBound;
    }
    
    @Override
    public void draw(ISprite sprite, Canvas canvas)
    {
		Point position = sprite.getPosition();
		
    	canvas.drawBitmap(mImage, position.X - mBitmapOriginOffset.X, position.Y - mBitmapOriginOffset.Y, null);
    	
        // ;+
    	if (AInstrumentation.SPRITE_DRAW_BOUNDS)
    	{
    		IBounds bounds = getBounds();
    		
            canvas.drawRect(bounds.getLeft(), bounds.getTop(), bounds.getRight(), bounds.getBottom(), sSpriteDrawBounds);
    	}
        // ;+
    }

    @Override
    public void loadContent(ISprite sprite)
    {
    	assert mResourceId != 0;
    	assert mCollisionBound != null;
    	assert mBoundingBox != null;

    	if (mReqWidth > -1)
    	{
    		mImage = mGameContext.getImageManager().allocateBitmap(mResourceId, mReqWidth, mReqHeight, mImageScaleOperator);
    	}
    	else
    	{
    		mImage = mGameContext.getImageManager().allocateBitmap(mResourceId);
    	}
    	
    	assert mImage != null;

    	calculateBitmapOffset(mCollisionBoundsType, mImage);
    	
    	mDims.setDimensions(mImage.getWidth(), mImage.getHeight());
    	
    	setDimensions(mCollisionBoundsType, mDims);
    	
    	Point position = sprite.getPosition();
    	
    	mBoundingBox.setPosition(position.X, position.Y);
    	mCollisionBound.setPosition(position.X, position.Y);
    }

    @Override
    public void unloadContent()
    {
		mGameContext.getImageManager().deallocateBitmap(mResourceId);
    }
    
	private void setDimensions(BitmapCollisionBounds collisionBoundsType, Dimensions dims)
	{
    	mBoundingBox.setDimensions(mDims);
    	
		switch (collisionBoundsType)
		{
			case RECT_UPPER_LEFT :
		        ((CollisionBoundingBox) mCollisionBound).setDimensions(mDims);
				break;
	
			case CIRCLE :
				int radius = Math.max(mDims.getWidth(), mDims.getHeight()) / 2;
		        ((CollisionBoundingCircle) mCollisionBound).setRadius(radius);
				break;
			
			case UNDEFINED :
			default :
				assert false;
		}
	}
	
	private void calculateBitmapOffset(BitmapCollisionBounds collisionBoundsType, Bitmap image)
	{
		switch (collisionBoundsType)
		{
			case RECT_UPPER_LEFT :
				mBitmapOriginOffset.X = 0;
				mBitmapOriginOffset.Y = 0;
				break;
	
			case CIRCLE :
				mBitmapOriginOffset.X = image.getWidth() / 2;
				mBitmapOriginOffset.Y = image.getHeight() / 2;
				break;
			
			case UNDEFINED :
			default :
				assert false;
		}
	}
	
	private void createBounds(BitmapCollisionBounds collisionBoundsType)
	{
		switch (collisionBoundsType)
		{
			case RECT_UPPER_LEFT :
				mCollisionBound = CollisionBoundingBox.Zero.clone();
				mBoundingBox = Rectangle.Zero.clone();
				break;
	
			case CIRCLE :
				mCollisionBound = CollisionBoundingCircle.Zero.clone();
				mBoundingBox = RectangleC.Zero.clone();
				break;
			
			case UNDEFINED :
			default :
				assert false;
		}
	}
	
    private void updateBoundingBox(Point position)
    {
    	mBoundingBox.setPosition(position.X, position.Y);
    	mCollisionBound.setPosition(position.X, position.Y);
    }
}
