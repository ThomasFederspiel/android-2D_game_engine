package se.federspiel.android.game;

import static junit.framework.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import se.federspiel.android.agraphics.DrawThread.IDrawer;
import se.federspiel.android.game.interfaces.ICollisionManager;
import se.federspiel.android.game.interfaces.IDrawableComponent;
import se.federspiel.android.game.interfaces.IDrawableComponent.DrawableZOrder;
import se.federspiel.android.game.interfaces.IGameContext;
import se.federspiel.android.game.interfaces.IGameEngine;
import se.federspiel.android.game.interfaces.IGraphicsView;
import se.federspiel.android.game.interfaces.IUpdatableComponent;
import se.federspiel.android.util.ALog;
import android.graphics.Canvas;

import com.example.explorerapp.AInstrumentation;

public class GameEngine implements IDrawer, IGameEngine
{
	private enum GameEngineState
	{
		CREATED,
		RUNNING,
		PAUSED,
		SHUTDOWN,
		DESTROYED
	}
	
	private long mPreviousUpdateTime = 0;
    private long mLastDrawTime = 0;
    private int mFpsCount = 0;

    private GameRenderer mGameRenderer = new GameRenderer();
    
    private boolean mGameViewEnabled = false;
    
    private GameEngineState mState = GameEngineState.CREATED;

	private IGameContext mGameContext = null;
	private ICollisionManager mCollisionManger = null;
	private ConstraintsManager mConstraintsManager = null;
	
	private GameTime mGameTime = new GameTime();
	
	private boolean mDrawableComponentsLocked = false;
	private ZOrderCollection<IDrawableComponent> mDrawableComponents = new ZOrderCollection<IDrawableComponent>(DrawableZOrder.nofLayers());

	private boolean mUpdatableComponentsLocked = false;
	private ArrayList<IUpdatableComponent> mUpdatableComponents = new ArrayList<IUpdatableComponent>();
	
	private List<Runnable> mRunnableQueue = Collections.synchronizedList(new ArrayList<Runnable>());

	private ComponentOperationQueue mComponentOperationQueue = null;

	private GameView mGameView = null;
	
	public GameEngine(IGraphicsView  graphicsView)
	{
		mGameContext = new GameContext(graphicsView, this);

		mCollisionManger = mGameContext.getCollisionManager();
		mConstraintsManager = mGameContext.getConstraintsManager();
			
		mGameView = new GameView(mGameContext);
		
		mComponentOperationQueue = new ComponentOperationQueue(this);
	}

	@Override
	public GameView getGameView()
	{
		return mGameView;
	}
	
	@Override
	public void enableGameView(boolean enable)
	{
		mGameViewEnabled = enable;

		if (mGameViewEnabled)
		{
			mGameRenderer.mBounds = mGameView.getBounds();
		}
		else
		{
			mGameRenderer.mBounds = null;
			
			mDrawableComponents.resetLayersCoordinateSystem();
		}
	}
	
	@Override
	public void resetLayerCoordinateSystem()
	{
		mDrawableComponents.resetLayersCoordinateSystem();
	}

	@Override
	public void setLayerCoordinateSystem(DrawableZOrder zOrder, GameViewCoordinateSystem coord)
	{
		mDrawableComponents.setLayerCoordinateSystem(zOrder, coord);
	}
	
	@Override
	public void invokeOnGameThread(Runnable runnable)
	{
		synchronized (mRunnableQueue)
		{
			mRunnableQueue.add(runnable);
		}
	}

	@Override
	public IGameContext getGameContext()
	{
		return mGameContext;
	}
	
	@SuppressWarnings("incomplete-switch")
	@Override
	public void togglePause()
	{
		switch (mState)
		{
			case RUNNING :
				mState = GameEngineState.PAUSED;
				break;
				
			case PAUSED :
				mState = GameEngineState.RUNNING;
				break;
		}
	}
	
	@Override
	public void pause()
	{
		mState = GameEngineState.PAUSED;
	}

	@Override
	public void unPause()
	{
		mState = GameEngineState.RUNNING;
	}

	@Override
	public void addBackground(IDrawableComponent component)
	{
		addComponent(component);
	}
	
	@Override
	public void addComponent(IDrawableComponent component)
	{
		if (mUpdatableComponentsLocked || mDrawableComponentsLocked)
		{
			mComponentOperationQueue.addComponent(component);
		}
		else
		{
			addComponentInternal(component);
		}
	}
	
	@Override
	public void addUpdateComponent(IUpdatableComponent component)
	{
		if (mUpdatableComponentsLocked)
		{
			mComponentOperationQueue.addUpdateComponent(component);
		}
		else
		{
			addUpdateComponentInternal(component);
		}
	}
	
	@Override
	public void removeUpdateComponent(IUpdatableComponent component)
	{
		if (mUpdatableComponentsLocked)
		{
			mComponentOperationQueue.removeUpdateComponent(component);
		}
		else
		{
			removeUpdateComponentInternal(component);
		}
	}
	
	@Override
	public void removeBackground(IDrawableComponent component)
	{
		removeComponent(component);
	}
	
	@Override
	public void removeComponent(IDrawableComponent component)
	{
		if (mUpdatableComponentsLocked || mDrawableComponentsLocked)
		{
			mComponentOperationQueue.removeComponent(component);
		}
		else
		{
			removeComponentInternal(component);
		}
	}

	@Override
	public void load()
	{
		mGameContext.load();
		
		for (int i = 0; i < mUpdatableComponents.size(); i++)
		{
			mUpdatableComponents.get(i).loadContent();
		}
		
		mPreviousUpdateTime = System.nanoTime();
		mLastDrawTime = System.nanoTime();
		
	    mState = GameEngineState.RUNNING;
	}
	
	@Override
	public void unload()
	{
		for (int i = 0; i < mUpdatableComponents.size(); i++)
		{
			mUpdatableComponents.get(i).unloadContent();
		}
		
		mGameContext.unload();
		
	    mState = GameEngineState.SHUTDOWN;
	}
	
	@Override
	public void draw(Canvas canvas)
	{
		mLastDrawTime = System.nanoTime();

		mGameRenderer.mCanvas = canvas;		
		
		if (mGameViewEnabled)
		{
			GameViewCoordinateSystem coordSystem = GameViewCoordinateSystem.GAME_VIEW_ABSOLUTE;
			
			mDrawableComponentsLocked = true;
			ZOrderCollection<IDrawableComponent>.ZOrderIterator iterator = mDrawableComponents.getIterator();
			
			while (iterator.hasNext())
			{
				IDrawableComponent item = iterator.nextItem();

				GameViewCoordinateSystem nextCoordSystem = iterator.getCoordinateSystem();
				
				if (coordSystem != nextCoordSystem)
				{
					if (nextCoordSystem == GameViewCoordinateSystem.GAME_VIEW_ABSOLUTE)
					{
						canvas.translate(mGameRenderer.mBounds.getLeft(), mGameRenderer.mBounds.getTop());
					}
					else
					{
						canvas.translate(-mGameRenderer.mBounds.getLeft(), -mGameRenderer.mBounds.getTop());
					}
					
					coordSystem = nextCoordSystem;
				}
				
				item.draw(mGameRenderer);
			}
			mDrawableComponentsLocked = false;
		}
		else
		{
			mDrawableComponentsLocked = true;
			ZOrderCollection<IDrawableComponent>.ZOrderIterator iterator = mDrawableComponents.getIterator();
			
			while (iterator.hasNext())
			{
				iterator.nextItem().draw(mGameRenderer);
			}
			mDrawableComponentsLocked = false;
		}
	}
	
	@Override
	public void update()
	{
		if (AInstrumentation.LOG_GAME_DURATIONS)
		{
			long upDraw = (System.nanoTime() - mLastDrawTime) / 1000000L;
	    
			ALog.debug(this, "DrawTime = " + upDraw + " ms");
		}
		
		long lastUpdateTime = System.nanoTime();

		mGameTime.setElapsedTime((lastUpdateTime - mPreviousUpdateTime));
		mGameTime.setFPS((mFpsCount % GameTime.FPS) + 1);

		mPreviousUpdateTime = lastUpdateTime;

		if (mState == GameEngineState.RUNNING)
		{
			synchronized (mRunnableQueue)
			{
				if (mRunnableQueue.size() > 0)
				{
					for (Runnable runner : mRunnableQueue)
					{
						runner.run();
					}
					
					mRunnableQueue.clear();
				}
			}
	
			mUpdatableComponentsLocked = true;
			for (int i = 0; i < mUpdatableComponents.size(); i++)
			{
				mUpdatableComponents.get(i).update(mGameTime);
			}
			mUpdatableComponentsLocked = false;
			
			mConstraintsManager.update();
			mCollisionManger.update();
			
			mComponentOperationQueue.processQueue();
		}
		
	    long sleepTime = (GameTime.FPS_TIME_NS - (System.nanoTime() - mLastDrawTime)) / 1000000L;

        try 
        {
    		if (AInstrumentation.LOG_GAME_DURATIONS)
    		{
    		    long upDur = (System.nanoTime() - mPreviousUpdateTime) / 1000000L;
    		    
	            ALog.debug(this, "UpdateTime = " + upDur + " ms");
	            ALog.debug(this, "SleepTime = " + sleepTime + " ms");
    		}
    		
            if (sleepTime > 0)
            {
                
            	Thread.sleep(sleepTime);
            }
            else
            {
        		if (AInstrumentation.LOG_GAME_LAGGING)
        		{
	                ALog.error(this, "Game is lagging");
        		}
            }
        } 
        catch (InterruptedException ex) 
        {
            ALog.error(this, ex);
        }
	}

	@Override
	public void terminate()
	{
	}
	
	@Override
	public void destroy()
	{
		mGameContext = null;
		mGameTime = null;
		
		mRunnableQueue.clear();
		mRunnableQueue = null;

		mComponentOperationQueue.clear();
		mComponentOperationQueue = null;
		
		mDrawableComponents.clear();
		mDrawableComponents = null;
		
		for (int i = 0; i < mUpdatableComponents.size(); i++)
		{
			teardownComponent(mUpdatableComponents.get(i));
		}
		
		mUpdatableComponents.clear();
		mUpdatableComponents = null;

		mState = GameEngineState.DESTROYED;
	}
	
	protected void addComponentInternal(IDrawableComponent component)
	{
		mDrawableComponents.add(component);
		mUpdatableComponents.add(component);
		
		setupComponent(component);
	}
	
	protected void removeComponentInternal(IDrawableComponent component)
	{
		mDrawableComponents.remove(component);
		mUpdatableComponents.remove(component);
		
		teardownComponent(component);
	}
	
	protected void addUpdateComponentInternal(IUpdatableComponent component)
	{
		mUpdatableComponents.add(component);
		
		setupComponent(component);
	}
	
	protected void removeUpdateComponentInternal(IUpdatableComponent component)
	{
		mUpdatableComponents.remove(component);
		
		teardownComponent(component);
	}
	
	private void setupComponent(IUpdatableComponent component)
	{
		if (isActive())
		{
			component.loadContent();
		}
	}
	
	private void teardownComponent(IUpdatableComponent component)
	{
		if (isActive())
		{
			component.unloadContent();
		}
	}

	private boolean isActive()
	{
		return ((mState == GameEngineState.RUNNING) || (mState == GameEngineState.PAUSED));
	}
	
	private static class ComponentOperationQueue
	{
		private int mCount = 0;
		private ArrayList<IDrawableComponent> mRemoveDrawableQueue = new ArrayList<IDrawableComponent>(2);
		private ArrayList<IDrawableComponent> mAddDrawableQueue = new ArrayList<IDrawableComponent>(2);
		private ArrayList<IUpdatableComponent> mAddUpdatableQueue = new ArrayList<IUpdatableComponent>(2);
		private ArrayList<IUpdatableComponent> mRemoveUpdatableQueue = new ArrayList<IUpdatableComponent>(2);

		private GameEngine mGameEngine = null;

		public ComponentOperationQueue(GameEngine gameEngine)
		{
			mGameEngine = gameEngine;
		}
		
		public void clear()
		{
			mAddDrawableQueue.clear();
			mRemoveDrawableQueue.clear();
			
			mAddUpdatableQueue.clear();
			mRemoveUpdatableQueue.clear();
			
			mCount = 0;
		}
		
		public void addComponent(IDrawableComponent component)
		{
			mAddDrawableQueue.add(component);
			mCount++;
		}
		
		public void removeComponent(IDrawableComponent component)
		{
			mRemoveDrawableQueue.add(component);
			mCount++;
		}
		
		public void addUpdateComponent(IUpdatableComponent component)
		{
			mAddUpdatableQueue.add(component);
			mCount++;
		}
		
		public void removeUpdateComponent(IUpdatableComponent component)
		{
			mRemoveUpdatableQueue.add(component);
			mCount++;
		}

		public void processQueue()
		{
			if (mCount > 0)
			{
				for (int i = 0; i < mRemoveDrawableQueue.size(); i++)
				{
					mGameEngine.removeComponentInternal(mRemoveDrawableQueue.get(i));
				}
				
				mRemoveDrawableQueue.clear();
				
				for (int i = 0; i < mRemoveUpdatableQueue.size(); i++)
				{
					mGameEngine.removeUpdateComponentInternal(mRemoveUpdatableQueue.get(i));
				}
				
				mRemoveUpdatableQueue.clear();

				for (int i = 0; i < mAddDrawableQueue.size(); i++)
				{
					mGameEngine.addComponentInternal(mAddDrawableQueue.get(i));
				}
				
				mAddDrawableQueue.clear();
				
				for (int i = 0; i < mAddUpdatableQueue.size(); i++)
				{
					mGameEngine.addUpdateComponentInternal(mAddUpdatableQueue.get(i));
				}
				
				mAddUpdatableQueue.clear();
				
				mCount = 0;
			}
		}
	}
	
	private class ZOrderCollection<T extends IDrawableComponent>
	{
		private ArrayList<T> mVector = new ArrayList<T>();
		private int[] mZOrderIndices = null;
		private GameViewCoordinateSystem[] mZOrderGameViewCoordSystem = null;
		
		private ZOrderIterator mIterator = new ZOrderIterator();
		
		public ZOrderCollection(int layers)
		{
			mZOrderIndices = new int[layers];
			mZOrderGameViewCoordSystem = new GameViewCoordinateSystem[layers];
			
			for (int i = 0; i < mZOrderIndices.length; i++)
			{
				mZOrderIndices[i] = 0;
				mZOrderGameViewCoordSystem[i] = GameViewCoordinateSystem.GAME_VIEW_ABSOLUTE;
			}
		}

		public void resetLayersCoordinateSystem()
		{
			for (int i = 0; i < mZOrderIndices.length; i++)
			{
				mZOrderGameViewCoordSystem[i] = GameViewCoordinateSystem.GAME_VIEW_ABSOLUTE;
			}
		}
		
		public void clear()
		{
			mVector.clear();
			mZOrderIndices = null;
			mZOrderGameViewCoordSystem = null;
		}
		
		public void setLayerCoordinateSystem(DrawableZOrder zOrder, GameViewCoordinateSystem coord)
		{
			mZOrderGameViewCoordSystem[zOrder.layer()] = coord;
		}
		
		public void add(T item)
		{
			int index = findInsertIndex(item);

			mZOrderIndices[item.getZOrder().layer()]++;
	
			mVector.add(index, item);
		}
		
		public boolean remove(T item)
		{
			boolean removed = mVector.remove(item);
			
			assertTrue(removed == true);
			
			mZOrderIndices[item.getZOrder().layer()]--;
			
			assertTrue(mZOrderIndices[item.getZOrder().layer()] >= 0);
			
			return removed;
		}

		public ZOrderIterator getIterator()
		{
			mIterator.reset();
			
			return mIterator;
		}
		
		private int findInsertIndex(T item)
		{
			int index = 0;

			int layer = item.getZOrder().layer();
			
			for (int i = 0; i <= layer; i++)
			{
				index += mZOrderIndices[i];
			}
			
			return index;
		}
		
		public class ZOrderIterator
		{
			private int mItemIndex = 0;
			private int mZOrderIndex = 0;
			private int mItemsATZOderOrBelow = 0;
			private GameViewCoordinateSystem mCoordinateSystem = GameViewCoordinateSystem.GAME_VIEW_ABSOLUTE;
			
			public void reset()
			{
				mItemIndex = 0;
				mZOrderIndex = 0;
				mItemsATZOderOrBelow = mZOrderIndices[mZOrderIndex];
						
				mCoordinateSystem = GameViewCoordinateSystem.GAME_VIEW_ABSOLUTE;
			}
			
			public boolean hasNext()
			{
				return (mItemIndex < mVector.size());
			}

			public T nextItem()
			{
				assertTrue(hasNext());
				
				T item = mVector.get(mItemIndex);
				
				while (mItemsATZOderOrBelow <= mItemIndex)
				{
					mZOrderIndex++;
					
					mItemsATZOderOrBelow += mZOrderIndices[mZOrderIndex];
				}
				
				mCoordinateSystem = mZOrderGameViewCoordSystem[mZOrderIndex];
				
				mItemIndex++;
				
				return item;
			}
			
			public GameViewCoordinateSystem getCoordinateSystem()
			{
				return mCoordinateSystem;
			}
		}
	}
}
