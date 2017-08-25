package se.federspiel.android.game.sprites.drawers;

import se.federspiel.android.game.GameRenderer;
import se.federspiel.android.game.collision.bounds.CollisionBoundingCircle;
import se.federspiel.android.game.geometry.Dimensions;
import se.federspiel.android.game.geometry.Point;
import se.federspiel.android.game.geometry.RectangleC;
import se.federspiel.android.game.interfaces.IBounds;
import se.federspiel.android.game.interfaces.ICollisionBound;
import se.federspiel.android.game.interfaces.IGameContext;
import se.federspiel.android.game.interfaces.IGraphicSpriteDrawer;
import se.federspiel.android.game.interfaces.ISprite;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;

import com.example.explorerapp.AInstrumentation;

public class BallDrawer extends BaseDrawer implements IGraphicSpriteDrawer
{
	private int mRadius = 0;
	private Dimensions mDims = Dimensions.Zero.clone();
    private RectangleC mBoundingBox = RectangleC.Zero.clone();
	private CollisionBoundingCircle mCollisionBounds = new CollisionBoundingCircle(0, 0, 0);

	private Paint mBallPaint = null;

	public BallDrawer(IGameContext gameContext)
	{
		super(gameContext);
		
		mBallPaint = new Paint(); 
		
		mBallPaint.setColor(Color.WHITE); 
		mBallPaint.setStrokeWidth(3); 
	}
	
	public void setColor(int color)
	{
		mBallPaint.setColor(color);
	}
	
    public void setRadius(int radius)
    {
    	mRadius = radius;
    	evaluateDimensions(radius);
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

        canvas.drawCircle(position.X, position.Y, mRadius, mBallPaint);
		
        // ;+
    	if (AInstrumentation.SPRITE_DRAW_BOUNDS)
    	{
    		Paint paint2 = new Paint(); 
    		
    		paint2.setColor(Color.BLACK); 
    		paint2.setStrokeWidth(2); 
    		paint2.setStyle(Style.STROKE);
    		
    		IBounds bounds = getBounds();
    		
            canvas.drawRect(bounds.getLeft(), bounds.getTop(), bounds.getRight(), bounds.getBottom(), paint2);
    	}
        // ;+
	}
	
	private void evaluateDimensions(int radius)
	{
		int size = radius * 2;
		
		mDims.setDimensions(size, size);
	
		mBoundingBox.setDimensions(mDims);
		
		mCollisionBounds.setRadius(radius);
	}
}
