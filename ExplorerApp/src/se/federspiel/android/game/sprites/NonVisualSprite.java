package se.federspiel.android.game.sprites;

import com.example.explorerapp.AInstrumentation;

import se.federspiel.android.game.GameRenderer;
import se.federspiel.android.game.collision.bounds.CollisionBoundingBox;
import se.federspiel.android.game.geometry.Dimensions;
import se.federspiel.android.game.geometry.Point;
import se.federspiel.android.game.geometry.RectangleC;
import se.federspiel.android.game.interfaces.IBounds;
import se.federspiel.android.game.interfaces.ICollisionBound;
import se.federspiel.android.game.interfaces.IGameContext;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class NonVisualSprite extends AbstractSprite
{
	private CollisionBoundingBox mCollisionBound = CollisionBoundingBox.Zero.clone();
	
    private Dimensions mDims = Dimensions.Zero.clone();
    private RectangleC mBoundingBox = RectangleC.Zero.clone();

    private Paint mPaddlePaint = null;
    
    public NonVisualSprite(IGameContext gameContext)
    {
    	super(gameContext);
    	
    	mPaddlePaint = new Paint(); 
		
    	mPaddlePaint.setColor(Color.WHITE); 
    	mPaddlePaint.setStrokeWidth(3); 
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

    public void setCollisionDimensions(Dimensions dim)
    {
    	updateCollisionBounds(dim);
    }

    @Override
	public IBounds getBounds()
	{
		return mBoundingBox;
	}
    
    @Override
    public ICollisionBound getCollisionBounds()
    {
        return mCollisionBound;
    }
    
    @Override
	public Point getMassCenter()
	{
		return mBoundingBox.getCenter();
	}
    
    @Override
    public void paint(GameRenderer renderer)
    {
    	// ;+
    	if (AInstrumentation.SPRITE_DRAW_MOVE)
    	{
    		Canvas canvas = renderer.getCanvas();

			Paint paint = new Paint(); 
			paint.setColor(Color.BLACK); 
			paint.setStrokeWidth(3); 
			Point st = getLastUpdateMovementRay().getStartPosition();
			Point end = getLastUpdateMovementRay().getEndPosition();
	    	canvas.drawLine(st.X, st.Y, end.X, end.Y, paint);
    	}
    }

    @Override
    protected void onPositionUpdate(Point position)
    {
    	updateBoundingBox(position);
    }
   
    private void updateBoundingBox(Point position)
    {
    	mBoundingBox.setPosition(position);
        mCollisionBound.setPosition(position);
    }
    
    private void updateBoundingBox(Dimensions dims)
    {
    	mBoundingBox.setDimensions(dims);
    }
    
    private void updateCollisionBounds(Dimensions dims)
    {
    	mCollisionBound.setDimensions(dims);
    }
}
