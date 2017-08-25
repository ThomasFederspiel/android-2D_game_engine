package se.federspiel.android.game.ui;

import java.util.ArrayList;

import se.federspiel.android.game.interfaces.IBounds;
import se.federspiel.android.game.interfaces.IUserInputManager.IOnClickListener;
import se.federspiel.android.game.interfaces.IUserInputManager.IOnLongClickListener;
import se.federspiel.android.game.interfaces.IUserInputManager.IOnTouchListener;
import se.federspiel.android.game.interfaces.IUserInputManager.TouchClickEvent;
import se.federspiel.android.game.interfaces.IUserInputManager.TouchEvent;

public abstract class UIInputComponent implements IOnClickListener, IOnLongClickListener, IOnTouchListener 
{
	private static final int CAPACITY = 2;
	
	private ArrayList<UIIOnClickListener> mClickListeners = new ArrayList<UIIOnClickListener>(CAPACITY);
	private ArrayList<UIIOnLongClickListener> mLongClickListeners = new ArrayList<UIIOnLongClickListener>(CAPACITY);
	private ArrayList<UIIOnTouchListener> mTouchListeners = new ArrayList<UIIOnTouchListener>(CAPACITY);
	
	private boolean mClickListenerActive = false;
	private boolean mLongClickListenerActive = false;
	private boolean mTouchListenerActive = false;

	private boolean mIsActive = false;
	
	public UIInputComponent()
	{
	}
	
	public abstract boolean isEnabled();
	
	protected abstract IBounds getComponentBounds();

	protected abstract UIAbstractWindow getWindow();
	
	protected void loadContent()
	{
		UIAbstractWindow window = getWindow();
		IBounds bounds = getComponentBounds();
		
		if (mClickListeners.size() > 0)
		{
			window.setOnClickListener(this, bounds);
			
			mClickListenerActive = true;
		}
		
		if (mLongClickListeners.size() > 0)
		{
			window.setOnLongClickListener(this, bounds);
			
			mLongClickListenerActive = true;
		}
		
		if (mTouchListeners.size() > 0)
		{
			window.setOnTouchListener(this, bounds);
			
			mTouchListenerActive = true;
		}
		
		mIsActive = true;
	}
	
	public void unloadContent()
	{
		UIAbstractWindow window = getWindow();
		
		if (mClickListenerActive)
		{
			window.removeRegisterdOnClickListener(this);
			
			mClickListenerActive = false;
		}
		
		if (mLongClickListenerActive)
		{
			window.removeRegisterdOnLongClickListener(this);
			
			mLongClickListenerActive = false;
		}
		
		if (mTouchListenerActive)
		{
			window.removeRegisterdOnTouchListener(this);
			
			mTouchListenerActive = false;
		}
		
		mIsActive = false;
	}

	@Override
	public boolean onTouch(TouchEvent event)
	{
		boolean handled = false;

		if (isEnabled())
		{
			for (int i = 0; i < mTouchListeners.size(); i++)
			{
				UIIOnTouchListener listener = mTouchListeners.get(i);
	
				handled = listener.onTouch(this, event) || handled;
			}
		}
		
		return handled;
	}

	@Override
	public boolean onLongClick(TouchClickEvent event)
	{
		boolean handled = false;
		
		if (isEnabled())
		{
			for (int i = 0; i < mLongClickListeners.size(); i++)
			{
				UIIOnLongClickListener listener = mLongClickListeners.get(i);
	
				handled = listener.onLongClick(this, event) || handled;
			}
		}
		
		return handled;
	}

	@Override
	public boolean onClick(TouchClickEvent event)
	{
		boolean handled = false;
		
		if (isEnabled())
		{
			for (int i = 0; i < mClickListeners.size(); i++)
			{
				UIIOnClickListener listener = mClickListeners.get(i);
	
				handled = listener.onClick(this, event) || handled;
			}
		}
		
		return handled;
	}

	public void setOnClickListener(UIIOnClickListener listener)
	{
		mClickListeners.add(listener);
		
		if (mIsActive && !mClickListenerActive)
		{
			getWindow().setOnClickListener(this, getComponentBounds());
			
			mClickListenerActive = true;
		}
	}
	
	public void setOnLongClickListener(UIIOnLongClickListener listener)
	{
		mLongClickListeners.add(listener);
		
		if (mIsActive && !mLongClickListenerActive)
		{
			getWindow().setOnLongClickListener(this, getComponentBounds());
			
			mLongClickListenerActive = true;
		}
	}

	public void setOnTouchListener(UIIOnTouchListener listener)
	{
		mTouchListeners.add(listener);
		
		if (mIsActive && !mTouchListenerActive)
		{
			getWindow().setOnTouchListener(this, getComponentBounds());
			
			mTouchListenerActive = true;
		}
	}

	public void removeOnClickListener(UIIOnClickListener listener)
	{
		mClickListeners.remove(listener);
		
		if (mIsActive && mClickListenerActive && (mClickListeners.size() == 0))
		{
			getWindow().removeRegisterdOnClickListener(this);
			
			mClickListenerActive = false;
		}
	}
	
	public void removeOnLongClickListener(UIIOnLongClickListener listener)
	{
		mLongClickListeners.remove(listener);
		
		if (mIsActive && mLongClickListenerActive && (mLongClickListeners.size() == 0))
		{
			getWindow().removeRegisterdOnLongClickListener(this);
			
			mLongClickListenerActive = false;
		}
	}

	public void removeOnTouchListener(UIIOnTouchListener listener)
	{
		mTouchListeners.remove(listener);
		
		if (mIsActive && mTouchListenerActive && (mTouchListeners.size() == 0))
		{
			getWindow().removeRegisterdOnTouchListener(this);
			
			mTouchListenerActive = false;
		}
	}

	protected void removeAllListeners()
	{
		for (int i = (mTouchListeners.size() - 1); i >= 0; i--)
		{
			removeOnTouchListener(mTouchListeners.get(i));
		}
		
		for (int i = (mLongClickListeners.size() - 1); i >= 0; i--)
		{
			removeOnLongClickListener(mLongClickListeners.get(i));
		}
		
		for (int i = (mClickListeners.size() - 1); i >= 0; i--)
		{
			removeOnClickListener(mClickListeners.get(i));
		}
	}
	
	public interface UIIOnClickListener
	{
		public boolean onClick(UIInputComponent component, TouchClickEvent event); 
	}
	
	public interface UIIOnLongClickListener
	{
		public boolean onLongClick(UIInputComponent component, TouchClickEvent event); 
	}

	public interface UIIOnTouchListener
	{
		public boolean onTouch(UIInputComponent component, TouchEvent event); 
	}
}
