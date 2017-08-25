package se.federspiel.android.game.sprites;

import se.federspiel.android.game.GameRenderer;
import se.federspiel.android.game.geometry.Dimensions;
import se.federspiel.android.game.geometry.Point;
import se.federspiel.android.game.interfaces.IBounds;
import se.federspiel.android.game.interfaces.ICollisionBound;
import se.federspiel.android.game.interfaces.IGameContext;
import se.federspiel.android.game.interfaces.IGraphicSprite;
import se.federspiel.android.game.interfaces.IGraphicSpriteDrawer;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.example.explorerapp.AInstrumentation;

public class GraphicSprite extends AbstractSprite implements IGraphicSprite
{
	private IGraphicSpriteDrawer mDrawer = null;
	
    public GraphicSprite(IGameContext gameContext)
    {
    	super(gameContext);
    }

    @Override
    public Dimensions getDimensions()
    {
    	assert mDrawer != null;
    	
        return mDrawer.getDimensions();
    }

    @Override
	public IBounds getBounds()
	{
    	assert mDrawer != null;
    	
        return mDrawer.getBounds();
	}
    
    @Override
    public ICollisionBound getCollisionBounds()
    {
    	assert mDrawer != null;
    	
        return mDrawer.getCollisionBounds();
    }
    
    @Override
	public Point getMassCenter()
	{
		return mDrawer.getBounds().getCenter();
	}
    
	@Override
	public void setGraphicDrawer(IGraphicSpriteDrawer drawer)
	{
		assert drawer != null;
		
		mDrawer = drawer;
		
		mDrawer.updatePosition(getPosition());
	}
	
	@Override
	public IGraphicSpriteDrawer getGraphicDrawer()
	{
		return mDrawer;
	}

	@Override
    public void paint(GameRenderer renderer)
    {
    	mDrawer.draw(this, renderer);
    	
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
		assert mDrawer != null;
		
		mDrawer.updatePosition(position);
    }
}
