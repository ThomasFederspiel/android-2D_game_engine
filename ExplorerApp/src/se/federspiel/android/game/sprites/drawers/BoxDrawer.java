package se.federspiel.android.game.sprites.drawers;

import se.federspiel.android.game.GameRenderer;
import se.federspiel.android.game.collision.bounds.CollisionBoundingBox;
import se.federspiel.android.game.geometry.Dimensions;
import se.federspiel.android.game.geometry.Point;
import se.federspiel.android.game.geometry.Rectangle;
import se.federspiel.android.game.interfaces.IBounds;
import se.federspiel.android.game.interfaces.ICollisionBound;
import se.federspiel.android.game.interfaces.IGameContext;
import se.federspiel.android.game.interfaces.IGraphicSpriteDrawer;
import se.federspiel.android.game.interfaces.ISprite;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;

public class BoxDrawer extends BaseDrawer implements IGraphicSpriteDrawer
{
    private Dimensions mDims = Dimensions.Zero.clone();
    private Rectangle mBoundingBox = Rectangle.Zero.clone();
	private CollisionBoundingBox mCollisionBounds = CollisionBoundingBox.Zero.clone();

	private Paint mBoxPaint = null;
	private Paint mBorderPaint = null;

	private boolean mHasBorder = false;
	
	public BoxDrawer(IGameContext gameContext)
	{
		super(gameContext);
		
		mBoxPaint = new Paint(); 
		
		mBoxPaint.setColor(Color.WHITE); 
		mBoxPaint.setStrokeWidth(1);
		mBoxPaint.setStyle(Style.FILL);
		
		mBorderPaint = new Paint(); 
		
		mBorderPaint.setColor(Color.BLACK); 
		mBorderPaint.setStrokeWidth(2); 
		mBorderPaint.setStyle(Style.STROKE);
	}

	public void setColor(int color)
	{
		mBoxPaint.setColor(color);
	}
	
	public void setBorderColor(int color)
	{
		mBorderPaint.setColor(color);
	}

	public void setBorder(boolean enable)
	{
		mHasBorder = enable;
	}
	
	public void setDimensions(int width, int height)
    {
    	mDims.setDimensions(width, height);
    	
    	evaluateDimensions(mDims);
    }
    
    public void setDimensions(Dimensions dims)
    {
    	mDims.set(dims);
    	
    	evaluateDimensions(mDims);
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
    public ICollisionBound getCollisionBounds()
    {
        return mCollisionBounds;
    }
    
	@Override
	public void updatePosition(Point position)
	{
		mBoundingBox.setPosition(position.X, position.Y);
		mCollisionBounds.setPosition(position.X, position.Y);
	}
	
	@Override
	public void draw(ISprite sprite, GameRenderer renderer)
	{
		Canvas canvas = renderer.getCanvas();
		
		Point position = sprite.getPosition();
		Dimensions dimensions = sprite.getDimensions();
		
        canvas.drawRect(position.X, position.Y, 
				position.X + dimensions.getWidth(), 
				position.Y + dimensions.getHeight(), mBoxPaint);
        
        if (mHasBorder)
        {
        	canvas.drawRect(position.X, position.Y, 
    				position.X + dimensions.getWidth(), 
    				position.Y + dimensions.getHeight(), mBorderPaint);
        }
	}
	
	private void evaluateDimensions(Dimensions dims)
	{
		mBoundingBox.setDimensions(dims);
		
		mCollisionBounds.setDimensions(dims);
	}
}
