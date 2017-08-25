package se.federspiel.android.game.sprites;

import se.federspiel.android.game.GameRenderer;
import se.federspiel.android.game.collision.bounds.CollisionBoundingBox;
import se.federspiel.android.game.geometry.Dimensions;
import se.federspiel.android.game.geometry.Point;
import se.federspiel.android.game.geometry.Rectangle;
import se.federspiel.android.game.interfaces.IBounds;
import se.federspiel.android.game.interfaces.ICollisionBound;
import se.federspiel.android.game.interfaces.IGameContext;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

public class BallSprite extends AbstractSprite
{
	private ICollisionBound mCollisionBound = null;
	
    private Dimensions mDims = new Dimensions(0, 0);
    private Rectangle mBoundingBox = Rectangle.Zero.clone();

    private Paint mBallPaint = null;
    
    public BallSprite(IGameContext gameContext)
    {
    	super(gameContext);
    	
		mBallPaint = new Paint(); 
		
		mBallPaint.setColor(Color.WHITE); 
		mBallPaint.setStrokeWidth(3); 
    }

    @Override
    public Dimensions getDimensions()
    {
        return mDims;
    }

    public void setDimensions(Dimensions dim)
    {
    	mDims = dim;
    	
    	updateBoundingBox(mDims);
    }

    @Override
	public IBounds getBounds()
	{
		return mBoundingBox;
	}
    
    @Override
	public Point getMassCenter()
	{
		return getPosition();
	}
    
    @Override
    public ICollisionBound getCollisionBounds()
    {
        if (mCollisionBound == null)
        {
            mCollisionBound = new CollisionBoundingBox(getPosition(), getDimensions().getWidth(), getDimensions().getHeight());
        }
        else
        {
            mCollisionBound.setPosition(getPosition());
        }

        return mCollisionBound;
    }
    
    @Override
    public void paint(GameRenderer renderer)
    {
		Canvas canvas = renderer.getCanvas();
		
		RectF rect = new RectF(getPosition().X, getPosition().Y, 
				getPosition().X + getDimensions().getWidth(), 
				getPosition().Y + getDimensions().getHeight());
		
        canvas.drawOval(rect, mBallPaint);
    }

    @Override
    protected void onPositionUpdate(Point position)
    {
    	updateBoundingBox(position);
    }
    
    private void updateBoundingBox(Point position)
    {
    	mBoundingBox.setPosition(position.X, position.Y);
    }
    
    private void updateBoundingBox(Dimensions dims)
    {
    	mBoundingBox.setDimensions(dims);
    }
}
