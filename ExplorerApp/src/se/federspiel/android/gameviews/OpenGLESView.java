package se.federspiel.android.gameviews;

import se.federspiel.android.agraphics.DrawThread.IDrawer;
import se.federspiel.android.game.GameApplication;
import se.federspiel.android.game.GameEngine;
import se.federspiel.android.game.interfaces.IGameContext;
import se.federspiel.android.glgraphics.OpenGLES20SurfaceView;
import android.content.Context;
import android.util.AttributeSet;

public class OpenGLESView extends OpenGLES20SurfaceView
{
	private GameApplication mGameApplication = null;
	private GameEngine mGameEngine = null;
	private IGameContext mGameContext = null;
	
	public OpenGLESView(Context context)
	{
		super(context);
	}

	public OpenGLESView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}
	
	public OpenGLESView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs);
	}

	@Override
	protected IDrawer createDrawer()
	{
    	if (mGameEngine == null)
    	{
	    	initGame();
    	}
    	
    	return mGameEngine;
//    	return new IDrawer()
//    	{
//			@Override
//			public void update()
//			{
//			}
//
//			@Override
//			public void draw(Canvas canvas)
//			{
//			}
//
//			@Override
//			public void unload()
//			{
//			}
//
//			@Override
//			public void load()
//			{
//			}
//
//			@Override
//			public void terminate()
//			{
//			}
//    	};
	}

	private void initGame()
	{
    	mGameApplication = new GameApplication(this);

		mGameEngine = GameApplication.getGameEngine();
		mGameContext = mGameEngine.getGameContext();
	}
}
