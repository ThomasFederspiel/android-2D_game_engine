package se.federspiel.android.game.sprites.drawers;

import se.federspiel.android.game.GameRenderer;
import se.federspiel.android.game.collision.bounds.CollisionBoundingLine;
import se.federspiel.android.game.geometry.Dimensions;
import se.federspiel.android.game.geometry.Point;
import se.federspiel.android.game.geometry.Rectangle;
import se.federspiel.android.game.geometry.Vector2;
import se.federspiel.android.game.interfaces.IBounds;
import se.federspiel.android.game.interfaces.ICollisionBound;
import se.federspiel.android.game.interfaces.IGameContext;
import se.federspiel.android.game.interfaces.IGraphicSpriteDrawer;
import se.federspiel.android.game.interfaces.ISprite;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class LineDrawer extends BaseDrawer implements IGraphicSpriteDrawer
{
	private Vector2 mLineVector = new Vector2(0, 0);
	
	private Dimensions mDims = Dimensions.Zero.clone();
    private Rectangle mBoundingBox = Rectangle.Zero.clone();
	private CollisionBoundingLine mCollisionBounds = CollisionBoundingLine.Zero.clone();

	private Paint mLinePaint = null;
	
	public LineDrawer(IGameContext gameContext)
	{
		super(gameContext);
		
		mLinePaint = new Paint(); 
		
		mLinePaint.setColor(Color.WHITE); 
		mLinePaint.setStrokeWidth(3); 
	}
	
	public void setColor(int color)
	{
		mLinePaint.setColor(color);
	}
	
    public void setLine(int x1, int y1, int x2, int y2)
    {
    	evaluateDimensions(x1, y1, x2, y2);
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
		mCollisionBounds.setPosition(position.X, position.Y);
		
		float left = position.X;
		float top = position.Y;
		
		if (mLineVector.X < 0)
		{
			left = position.X + mLineVector.X;
		}

		if (mLineVector.Y < 0)
		{
			top = position.Y + mLineVector.Y;
		}
		
		mBoundingBox.setPosition(left, top);
	}
	
	@Override
	public void draw(ISprite sprite, GameRenderer renderer)
	{
		Canvas canvas = renderer.getCanvas();
		
		Point position = sprite.getPosition();

        canvas.drawLine(position.X, position.Y, 
				position.X + mLineVector.X, 
				position.Y + mLineVector.Y, mLinePaint);
	}
	
	private void evaluateDimensions(int x1, int y1, int x2, int y2)
	{
		mLineVector.X = x2 - x1;
		mLineVector.Y = y2 - y1;
		
		mDims.setDimensions(Math.abs(Math.round(mLineVector.X)), Math.abs(Math.round(mLineVector.Y)));
		
		mBoundingBox.setDimensions(mDims);
		
		mCollisionBounds.setDirection(mLineVector.X, mLineVector.Y);
	}
}
