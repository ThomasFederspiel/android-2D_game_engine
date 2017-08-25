package se.federspiel.android.game.sprites;

import se.federspiel.android.game.GameRenderer;
import se.federspiel.android.game.GameTime;
import se.federspiel.android.game.geometry.Dimensions;
import se.federspiel.android.game.geometry.Point;
import se.federspiel.android.game.interfaces.IBounds;
import se.federspiel.android.game.interfaces.ICollisionBound;
import se.federspiel.android.game.interfaces.IGameContext;
import se.federspiel.android.game.interfaces.IImageSprite;
import se.federspiel.android.game.interfaces.IImageSpriteDrawer;
import se.federspiel.android.game.sprites.drawers.ImageDrawerAnimator;
import se.federspiel.android.game.sprites.drawers.ImageDrawerCollection;
import android.graphics.Canvas;

public class ImageSprite extends AbstractSprite implements IImageSprite, ImageDrawerAnimator.IAnimationContext
{
    private ImageDrawerCollection mDrawerCollection = new ImageDrawerCollection();
    private ImageDrawerAnimator mAnimator = null;
    
    public ImageSprite(IGameContext gameContext)
    {
    	super(gameContext);
    }

    @Override
    public Dimensions getDimensions()
    {
    	return mDrawerCollection.getDimensions();
    }

    @Override
	public IBounds getBounds()
	{
    	return mDrawerCollection.getBounds();
	}
    
    @Override
	public Point getMassCenter()
	{
		return mDrawerCollection.getBounds().getCenter();
	}
    
	@Override
	public void addImageDrawer(IImageSpriteDrawer drawer)
	{
		assert drawer != null;
		
		mDrawerCollection.addDrawer(drawer);
		
		drawer.updatePosition(getPosition());
	}
	
    public void selectImageDrawer(int id)
    {
		mDrawerCollection.selectDrawer(id);
    }
    
	public void setImageAnimator(ImageDrawerAnimator animator)
	{
		mAnimator = animator;
		
		mAnimator.setContext(this);
	}
	
    @Override
    public ICollisionBound getCollisionBounds()
    {
    	return mDrawerCollection.getCollisionBounds();
    }
    
	@Override
    public void update(GameTime gameTime)
    {
		super.update(gameTime);
		
		if (mAnimator != null)
		{
			mAnimator.update(gameTime);
		}
    }
	
    @Override
    public void paint(GameRenderer renderer)
    {
		Canvas canvas = renderer.getCanvas();

    	mDrawerCollection.draw(this, canvas);
    }

    @Override
    public void loadContent()
    {
    	super.loadContent();

    	mDrawerCollection.loadContent(this);
    }

    @Override
    public void unloadContent()
    {
    	super.unloadContent();
    	
    	mDrawerCollection.unloadContent();
    }
    
	@Override
    protected void onPositionUpdate(Point position)
    {
		mDrawerCollection.updatePosition(position);
    }
}
