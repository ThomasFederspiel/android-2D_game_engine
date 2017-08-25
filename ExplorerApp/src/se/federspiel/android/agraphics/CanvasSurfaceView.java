package se.federspiel.android.agraphics;

import se.federspiel.android.agraphics.DrawThread.IDrawer;
import se.federspiel.android.game.geometry.Rectangle;
import se.federspiel.android.game.interfaces.IGraphicsView;
import se.federspiel.android.util.ALog;
import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;

public abstract class CanvasSurfaceView extends SurfaceView implements IGraphicsView, SurfaceHolder.Callback
{
	private DrawThread mDrawThread = null;
	private IDrawer mDrawer = null;
	private Rectangle mBounds = null;
	
	public CanvasSurfaceView(Context context)
	{
		super(context);
		
		initView();
	}

	public CanvasSurfaceView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		
		initView();
	}
	
	public CanvasSurfaceView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		
		initView();
	}

	public Rectangle getBounds()
	{
		if (mBounds == null)
		{
			mBounds = new Rectangle(0, 0, getWidth(), getHeight());
		}
		
		return mBounds;
	}

	public boolean requestingFocus()
	{
		return requestFocus();
	}
	
	@Override
	public void surfaceCreated(SurfaceHolder holder)
	{
		if (mDrawThread != null)
		{
			throw new RuntimeException("Draw thread exists during surface create");
		}
		
		if (mDrawer == null)
		{
			throw new RuntimeException("Drawer is not defined");
		}

		ALog.debug(this, "surfaceCreated");
		
		mDrawThread = new DrawThread(this, mDrawer);
		mDrawThread.start();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height)
	{
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder)
	{
		if (mDrawThread == null)
		{
			throw new RuntimeException("Draw thread doesn't exist during surface destroy");
		}
		
		ALog.debug(this, "surfaceDestroyed");
		
        mDrawThread.stop();
        
        mDrawThread = null;
	}
	
	public void onStart()
    {
    	ALog.debug(this, "onStart()");
    }
    
    public void onRestart()
    {
    	ALog.debug(this, "onRestart()");
    }

    public void onResume()
    {
    	ALog.debug(this, "onResume()");
    }

    public void onPause()
    {
    	ALog.debug(this, "onPause()");
    }

    public void onStop()
    {
    	ALog.debug(this, "onStop()");
    }

    public void onDestroy()
    {
    	ALog.debug(this, "onDestroy()");
    }
    
	protected abstract IDrawer createDrawer();
	
	private void setupSurface()
	{
		initSurface(createDrawer());
	}
	
	private void initSurface(IDrawer drawer)
	{
		mDrawer = drawer;
		
		SurfaceHolder holder = getHolder();
		
		holder.addCallback(this);
	}

	private void initView()
	{
		getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() 
		{ 
		    @Override 
		    public void onGlobalLayout() 
		    {
		    	setupSurface();
		    } 
		}); 
	}
}
