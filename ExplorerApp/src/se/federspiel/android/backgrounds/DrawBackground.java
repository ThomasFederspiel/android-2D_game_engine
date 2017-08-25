package se.federspiel.android.backgrounds;

import java.util.ArrayList;

import se.federspiel.android.game.GameRenderer;
import se.federspiel.android.game.geometry.Rectangle;
import se.federspiel.android.game.interfaces.IGameContext;
import se.federspiel.android.game.interfaces.IGraphicDrawer;
import se.federspiel.android.game.utils.GameTools;
import android.graphics.Canvas;
import android.graphics.Paint;


public class DrawBackground extends AbstractBackground
{
	private ArrayList<IGraphicDrawer> mDrawers = new ArrayList<IGraphicDrawer>();

	private Paint mBackgroundColor = new Paint();

	private Rectangle mDrawerEnclosingBounds = Rectangle.Zero.clone();
	
	public DrawBackground(IGameContext gameContext)
    {
		super(gameContext);
    }
	
	public void addDrawer(IGraphicDrawer drawer)
	{
		mDrawers.add(drawer);

		evaluateDrawersBounds();
	}

	public void setColor(int color)
	{
		mBackgroundColor.setColor(color);
	}
	
	@Override
	public void draw(GameRenderer renderer)
	{
		drawDrawers(renderer);
	}

	private void drawDrawers(GameRenderer renderer)
	{
		Canvas canvas = renderer.getCanvas();
		
		canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), mBackgroundColor);
		
		for (int i = 0; i < mDrawers.size(); i++)
		{
			mDrawers.get(i).draw(renderer);			
		}
	}

	private void evaluateDrawersBounds()
	{
		mDrawerEnclosingBounds.set(mGameContext.getGraphicBounds());

		GameTools.evaluateDrawersMinEnclosingBounds(mDrawers, mDrawerEnclosingBounds);		
		
		setPosition(mDrawerEnclosingBounds.getLeft(), mDrawerEnclosingBounds.getTop());
		setDimensions(mDrawerEnclosingBounds.getWidth(), mDrawerEnclosingBounds.getHeight());
	}
}
