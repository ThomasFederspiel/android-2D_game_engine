package se.federspiel.android.agraphics;

import android.graphics.Canvas;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class DrawThread implements Runnable
{
	private boolean mRunning = false;
	
	private SurfaceView mSurfaceView = null;
	private SurfaceHolder mSurfaceHolder = null;
	
	private Thread mThread = null;
	private IDrawer mDrawer = null;

	public DrawThread(SurfaceView surface, IDrawer drawer)
	{
		mSurfaceView = surface;
		mSurfaceHolder = surface.getHolder();
		mDrawer  = drawer;
	}

	public void start()
	{
		if ((mThread != null) || (mRunning))
		{
			throw new RuntimeException("Illegal state");
		}

		mRunning = true;

		mDrawer.load();
		
		mThread = new Thread(this);
		
		mThread.start();		
	}
	
	public void stop()
	{
		if ((mThread == null) || (!mRunning))
		{
			throw new RuntimeException("Illegal state");
		}
		
		boolean retry = true;
		   
		mRunning = false;

		mDrawer.terminate();
		
		while (retry)
		{
			try 
			{
				mThread.join();
				 
				retry = false;
			} 
			catch (InterruptedException e) 
			{
				
			}
		}		
		
		mThread = null;
		
		mDrawer.unload();
	}
	
	@Override
	public void run()
	{
		Canvas canvas = null;
	    
		while (mRunning)
		{
	        mDrawer.update();
	        
			try 
		    {
				canvas = mSurfaceHolder.lockCanvas();
				
				if (canvas != null)
				{
					synchronized (mSurfaceView) 
					{
				        mDrawer.draw(canvas);
					}
				}
		    } 
			finally 
			{
				 if (canvas != null) 
				 {
					 mSurfaceHolder.unlockCanvasAndPost(canvas);
				 }
		     }			
		}
	}
	
	public interface IDrawer
	{
		public void update();
		
		public void draw(Canvas canvas);
		
		public void unload();

		public void load();
		
		public void terminate();
	}
}
