package se.federspiel.android.game.sprites.drawers;

import java.util.ArrayList;

import se.federspiel.android.game.geometry.Dimensions;
import se.federspiel.android.game.geometry.Point;
import se.federspiel.android.game.interfaces.IBounds;
import se.federspiel.android.game.interfaces.ICollisionBound;
import se.federspiel.android.game.interfaces.IImageSpriteDrawer;
import se.federspiel.android.game.interfaces.ISprite;
import android.graphics.Canvas;

public class ImageDrawerCollection implements IImageSpriteDrawer
{
	private ArrayList<IImageSpriteDrawer> mDrawers = new ArrayList<IImageSpriteDrawer>();
	private IImageSpriteDrawer mCurrentDrawer = null;
	
    public ImageDrawerCollection()
    {
    }

    public void addDrawer(IImageSpriteDrawer drawer)
    {
    	mDrawers.add(drawer);
    	
    	if (mCurrentDrawer == null)
    	{
    		mCurrentDrawer = drawer;
    	}
    }

    public void selectDrawer(int index)
    {
    	assert index < mDrawers.size();
    	
    	mCurrentDrawer = mDrawers.get(index);
    }
    
    @Override
    public Dimensions getDimensions()
    {
        return mCurrentDrawer.getDimensions();
    }

    @Override
	public IBounds getBounds()
	{
		return mCurrentDrawer.getBounds();
	}
    
	@Override
	public void updatePosition(Point position)
	{
		mCurrentDrawer.updatePosition(position);
	}
	
    @Override
    public ICollisionBound getCollisionBounds()
    {
		return mCurrentDrawer.getCollisionBounds();
    }
    
    @Override
    public void draw(ISprite sprite, Canvas canvas)
    {
    	mCurrentDrawer.draw(sprite, canvas);
    }

    @Override
    public void loadContent(ISprite sprite)
    {
    	for (int i = 0; i < mDrawers.size(); i++)
    	{
    		mDrawers.get(i).loadContent(sprite);
    	}
    }

    @Override
    public void unloadContent()
    {
    	for (int i = 0; i < mDrawers.size(); i++)
    	{
    		mDrawers.get(i).unloadContent();
    	}
    }
}
