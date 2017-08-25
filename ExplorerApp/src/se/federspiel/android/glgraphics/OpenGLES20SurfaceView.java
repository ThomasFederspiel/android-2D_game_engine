package se.federspiel.android.glgraphics;

import se.federspiel.android.agraphics.DrawThread;
import se.federspiel.android.agraphics.DrawThread.IDrawer;
import se.federspiel.android.game.geometry.Rectangle;
import se.federspiel.android.game.interfaces.IGraphicsView;
import se.federspiel.android.util.ALog;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;

public abstract class OpenGLES20SurfaceView extends GLSurfaceView implements IGraphicsView, SurfaceHolder.Callback
{
	private DrawThread mDrawThread = null;
	private IDrawer mDrawer = null;
	private Rectangle mBounds = null;
	
	public OpenGLES20SurfaceView(Context context)
	{
		super(context);
		
		initView();
	}

	public OpenGLES20SurfaceView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		
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
		super.surfaceCreated(holder);
		
//		if (mDrawThread != null)
//		{
//			throw new RuntimeException("Draw thread exists during surface create");
//		}
//		
//		if (mDrawer == null)
//		{
//			throw new RuntimeException("Drawer is not defined");
//		}
//
		ALog.debug(this, "surfaceCreated");
//		
//		mDrawThread = new DrawThread(this, mDrawer);
//		mDrawThread.start();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height)
	{
		super.surfaceChanged(holder, format, width, height);
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder)
	{
		super.surfaceDestroyed(holder);
		
//		if (mDrawThread == null)
//		{
//			throw new RuntimeException("Draw thread doesn't exist during surface destroy");
//		}
//		
		ALog.debug(this, "surfaceDestroyed");
//		
//        mDrawThread.stop();
//        
//        mDrawThread = null;
	}
	
	public void onStart()
    {
		ALog.debug(this, "onStart()");
    }
    
    public void onResume()
    {
    	super.onResume();
    	
    	ALog.debug(this, "onResume()");
    }

    public void onPause()
    {
    	super.onPause();
    	
    	ALog.debug(this, "onPause()");
    }

	protected abstract IDrawer createDrawer();
	
	private void setupSurface()
	{
		initSurface(createDrawer());
	}
	
	private void initSurface(IDrawer drawer)
	{
		mDrawer = drawer;
		
//		SurfaceHolder holder = getHolder();
//		
//		holder.addCallback(this);
	}

	private void initView()
	{
		setEGLContextClientVersion(2);
		setRenderer(new OpenGLES20Renderer());
		setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
		
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
