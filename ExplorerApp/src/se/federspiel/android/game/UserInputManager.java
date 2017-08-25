package se.federspiel.android.game;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import se.federspiel.android.game.interfaces.IBounds;
import se.federspiel.android.game.interfaces.IGraphicsView;
import se.federspiel.android.game.interfaces.IUserInputManager;
import se.federspiel.android.game.utils.ObjectCache;
import se.federspiel.android.util.SortingArrayList;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;

public class UserInputManager implements IUserInputManager, OnKeyListener, OnTouchListener
{
	private static final int LONG_CLICK_TIME = 800;
	private static final int NOF_SUPPORTED_POINTERS = 2;
	
	private IGraphicsView mView = null; 

	private boolean mIsTouchListening = false;
	private boolean mIsKeyListening = false;

	private TouchEventCache mTouchEventCache = new TouchEventCache();
	private TouchClickEventCache mTouchClickEventCache = new TouchClickEventCache();
	
	private BoundsListenerCache<IOnClickListener> mBoundsListenerIOnClickCache = new BoundsListenerCache<IOnClickListener>();
	private BoundsListenerCache<IOnLongClickListener> mBoundsListenerIOnLongClickCache = new BoundsListenerCache<IOnLongClickListener>();
	private BoundsListenerCache<IOnTouchListener> mBoundsListenerIOnTouchCache = new BoundsListenerCache<IOnTouchListener>();
	
	private NewBoundsListenerCache mBoundsListenerCache = new NewBoundsListenerCache();

	private Hashtable<Integer, KeyCodeEnum> mKeyMapping = new Hashtable<Integer, KeyCodeEnum>();
	
	private Hashtable<KeyCodeEnum, List<IOnKeyDownListener>> mKeyDownMap = new Hashtable<KeyCodeEnum, List<IOnKeyDownListener>>();
	private Hashtable<KeyCodeEnum, List<IOnKeyUpListener>> mKeyUpMap = new Hashtable<KeyCodeEnum, List<IOnKeyUpListener>>();

	private ArrayList<ArrayList<IOnClickListener>> mClickListeners = new ArrayList<ArrayList<IOnClickListener>>();
	private ArrayList<ArrayList<IOnLongClickListener>> mLongClickListeners = new ArrayList<ArrayList<IOnLongClickListener>>();
	private ArrayList<ArrayList<IOnTouchListener>> mTouchListeners = new ArrayList<ArrayList<IOnTouchListener>>();
	
	private SortingArrayList<BoundsListener<IOnClickListener>> mBoundedClickListeners = new SortingArrayList<BoundsListener<IOnClickListener>>(SortingArrayList.Order.DESCENT);
	private SortingArrayList<BoundsListener<IOnLongClickListener>> mBoundedLongClickListeners = new SortingArrayList<BoundsListener<IOnLongClickListener>>(SortingArrayList.Order.DESCENT);
	private SortingArrayList<BoundsListener<IOnTouchListener>> mBoundedTouchListeners = new SortingArrayList<BoundsListener<IOnTouchListener>>(SortingArrayList.Order.DESCENT);

	private SortingArrayList<NewBoundsListener> mBoundedListeners = new SortingArrayList<NewBoundsListener>(SortingArrayList.Order.DESCENT);
	
	private Hashtable<Integer, TouchClickEvent> mClickStartEvents = new Hashtable<Integer, TouchClickEvent>();
	
	private Hashtable<Integer, NewBoundsListener> mActiveNewBoundedTouchListeners = new Hashtable<Integer, NewBoundsListener>();

	private int mActivePointerListeners = 1;
	
	public UserInputManager(IGraphicsView view)
	{
		mView = view;
		
		mView.setFocusable(true);
		mView.setFocusableInTouchMode(true);
		mView.requestingFocus();

		initialize();
	}

	private void initialize()
	{
		generateKeyMapping();
		
		for (int i = 0; i < NOF_SUPPORTED_POINTERS; i++)
		{
			mClickListeners.add(null);
			mLongClickListeners.add(null);
			mTouchListeners.add(null);
		}
	}
	
	private void generateKeyMapping()
	{
		mKeyMapping.put(KeyEvent.KEYCODE_BACK, KeyCodeEnum.KeyRight);
		mKeyMapping.put(KeyEvent.KEYCODE_MENU, KeyCodeEnum.KeyLeft);
		mKeyMapping.put(KeyEvent.KEYCODE_VOLUME_UP, KeyCodeEnum.KeyUp);
		mKeyMapping.put(KeyEvent.KEYCODE_VOLUME_DOWN, KeyCodeEnum.KeyDown);
	}
	
	@Override
	public void setOnKeyDownListener(IOnKeyDownListener listener, KeyCodeEnum keyCode)
	{
		assert listener != null;
		assert !mKeyMapping.contains(keyCode);
		
		List<IOnKeyDownListener> listeners = mKeyDownMap.get(keyCode);
				
		if (listeners == null)
		{
			listeners = new ArrayList<IOnKeyDownListener>();
		}

		listeners.add(listener);
		
		mKeyDownMap.put(keyCode, listeners);

		checkKeyListener();
	}

	@Override
	public void setOnKeyUpListener(IOnKeyUpListener listener, KeyCodeEnum keyCode)
	{
		assert listener != null;
		assert !mKeyMapping.contains(keyCode);
		
		List<IOnKeyUpListener> listeners = mKeyUpMap.get(keyCode);
		
		if (listeners == null)
		{
			listeners = new ArrayList<IOnKeyUpListener>();
		}

		listeners.add(listener);
		
		mKeyUpMap.put(keyCode, listeners);
		
		checkKeyListener();
	}

	@Override
	public void setBoundedOnClickListener(IOnClickListener listener, IBounds bounds, TouchZOrder layer)
	{
		assert listener != null;
		assert bounds != null;
		
		assert !mBoundedClickListeners.contains(listener);
		
		BoundsListener<IOnClickListener> item = mBoundsListenerIOnClickCache.get();
		item.set(listener, bounds, layer);
		
		mBoundedClickListeners.add(item);
		
		assert !mBoundedListeners.contains(listener);
		
		NewBoundsListener bitem = mBoundsListenerCache.get();
		bitem.set(listener, bounds, layer);
		
		mBoundedListeners.add(bitem);

		checkTouchListener();
	}
	
	@Override
	public void setOnClickListener(IOnClickListener listener)
	{
		setOnClickListener(listener, 0);
	}
	
	@Override
	public void setOnClickListener(IOnClickListener listener, int pointerId)
	{
		assert listener != null;
		assert pointerId < NOF_SUPPORTED_POINTERS;
		
		ArrayList<IOnClickListener> list = mClickListeners.get(pointerId);
		
		if (list == null)
		{
			list = new ArrayList<IOnClickListener>();
			
			mClickListeners.set(pointerId, list);
			
			mActivePointerListeners++;
		}
		
		assert !list.contains(listener);
				
		list.add(listener);

		checkTouchListener();
	}

	@Override
	public void setBoundedOnLongClickListener(IOnLongClickListener listener, IBounds bounds, TouchZOrder layer)
	{
		assert listener != null;
		assert bounds != null;
		
		assert !mBoundedLongClickListeners.contains(listener);

		BoundsListener<IOnLongClickListener> item = mBoundsListenerIOnLongClickCache.get();
		item.set(listener, bounds, layer);
		
		mBoundedLongClickListeners.add(item);

		assert !mBoundedListeners.contains(listener);
		
		NewBoundsListener bitem = mBoundsListenerCache.get();
		bitem.set(listener, bounds, layer);
		
		mBoundedListeners.add(bitem);
		
		checkTouchListener();
	}
	
	@Override
	public void setOnLongClickListener(IOnLongClickListener listener)
	{
		setOnLongClickListener(listener, 0);
	}

	@Override
	public void setOnLongClickListener(IOnLongClickListener listener, int pointerId)
	{
		assert listener != null;
		assert pointerId < NOF_SUPPORTED_POINTERS;
		
		ArrayList<IOnLongClickListener> list = mLongClickListeners.get(pointerId);
		
		if (list == null)
		{
			list = new ArrayList<IOnLongClickListener>();
			
			mLongClickListeners.set(pointerId, list);
			
			mActivePointerListeners++;
		}
		
		assert !list.contains(listener);
				
		list.add(listener);

		checkTouchListener();
	}

	@Override
	public void setBoundedOnTouchListener(IOnTouchListener listener, IBounds bounds, TouchZOrder layer)
	{
		assert listener != null;
		assert bounds != null;
		
		assert !mBoundedTouchListeners.contains(listener);

		BoundsListener<IOnTouchListener> item = mBoundsListenerIOnTouchCache.get();
		item.set(listener, bounds, layer);
		
		mBoundedTouchListeners.add(item);

		assert !mBoundedListeners.contains(listener);
		
		NewBoundsListener bitem = mBoundsListenerCache.get();
		bitem.set(listener, bounds, layer);
		
		mBoundedListeners.add(bitem);
		
		checkTouchListener();
	}
	
	@Override
	public void setOnTouchListener(IOnTouchListener listener)
	{
		setOnTouchListener(listener, 0);
	}

	@Override
	public void setOnTouchListener(IOnTouchListener listener, int pointerId)
	{
		assert listener != null;
		assert pointerId < NOF_SUPPORTED_POINTERS;
		
		ArrayList<IOnTouchListener> list = mTouchListeners.get(pointerId);
		
		if (list == null)
		{
			list = new ArrayList<IOnTouchListener>();
			
			mTouchListeners.set(pointerId, list);
			
			mActivePointerListeners++;
		}
		
		assert !list.contains(listener);
				
		list.add(listener);

		checkTouchListener();
	}

	@Override
	public void removeOnClickListener(IOnClickListener listener)
	{
		removeOnClickListener(listener, 0);
	}

	@Override
	public void removeOnClickListener(IOnClickListener listener, int pointerId)
	{
		assert listener != null;
		assert pointerId < NOF_SUPPORTED_POINTERS;
		
		ArrayList<IOnClickListener> list = mClickListeners.get(pointerId);

		assert list != null;
		assert list.contains(listener);
		
		list.remove(listener);

		if (list.size() == 0)
		{
			mClickListeners.remove(pointerId);
			
			mActivePointerListeners--;
		}
		
		checkTouchListener();
	}

	@Override
	public void removeOnLongClickListener(IOnLongClickListener listener)
	{
		removeOnLongClickListener(listener, 0);
	}

	@Override
	public void removeOnLongClickListener(IOnLongClickListener listener, int pointerId)
	{
		assert listener != null;
		assert pointerId < NOF_SUPPORTED_POINTERS;
		
		ArrayList<IOnLongClickListener> list = mLongClickListeners.get(pointerId);

		assert list != null;
		assert list.contains(listener);
		
		list.remove(listener);

		if (list.size() == 0)
		{
			mLongClickListeners.remove(pointerId);
			
			mActivePointerListeners--;
		}
		
		checkTouchListener();
	}

	@Override
	public void removeOnTouchListener(IOnTouchListener listener)
	{
		removeOnTouchListener(listener, 0);
	}

	@Override
	public void removeOnTouchListener(IOnTouchListener listener, int pointerId)
	{
		assert listener != null;
		assert pointerId < NOF_SUPPORTED_POINTERS;
		
		ArrayList<IOnTouchListener> list = mTouchListeners.get(pointerId);

		assert list != null;
		assert list.contains(listener);
		
		list.remove(listener);

		if (list.size() == 0)
		{
			mTouchListeners.remove(pointerId);
			
			mActivePointerListeners--;
		}
		
		checkTouchListener();
	}
	
	@Override
	public void removeOnKeyDownListener(IOnKeyDownListener listener, KeyCodeEnum keyCode)
	{
		assert listener != null;
		
		List<IOnKeyDownListener> listeners = mKeyDownMap.get(keyCode);
		
		if (listeners != null)
		{
			if (listeners.remove(listener))
			{
				checkKeyListener();
			}
		}
	}

	@Override
	public void removeOnKeyUpListener(IOnKeyUpListener listener, KeyCodeEnum keyCode)
	{
		assert listener != null;
		
		List<IOnKeyUpListener> listeners = mKeyUpMap.get(keyCode);
		
		if (listeners != null)
		{
			if (listeners.remove(listener))
			{
				checkKeyListener();
			}
		}
	}

	@Override
	public void removeBoundedOnClickListener(IOnClickListener listener)
	{
		assert listener != null;
		
		boolean removed = false;
		
		for (int i = 0; i < mBoundedClickListeners.size(); i++)
		{
			BoundsListener<IOnClickListener> bounds = mBoundedClickListeners.get(i);
			
			if (bounds.mListener == listener)
			{
				removed = mBoundedClickListeners.remove(bounds);
				
				mBoundsListenerIOnClickCache.add(bounds);
				
				break;
			}
		}

		assert removed == true;

		removed = false;
		
		for (int i = 0; i < mBoundedListeners.size(); i++)
		{
			NewBoundsListener bounds = mBoundedListeners.get(i);
			
			if (bounds.mClickListener == listener)
			{
				removed = mBoundedListeners.remove(bounds);

				mBoundsListenerCache.add(bounds);
				
				break;
			}
		}

		assert removed == true;

		checkTouchListener();
	}

	@Override
	public void removeBoundedOnLongClickListener(IOnLongClickListener listener)
	{
		assert listener != null;
		
		boolean removed = false;
		
		for (int i = 0; i < mBoundedLongClickListeners.size(); i++)
		{
			BoundsListener<IOnLongClickListener> bounds = mBoundedLongClickListeners.get(i);
			
			if (bounds.mListener == listener)
			{
				removed = mBoundedLongClickListeners.remove(bounds);
				
				mBoundsListenerIOnLongClickCache.add(bounds);
				
				break;
			}
		}

		assert removed == true;
		
		removed = false;
		
		for (int i = 0; i < mBoundedListeners.size(); i++)
		{
			NewBoundsListener bounds = mBoundedListeners.get(i);
			
			if (bounds.mLongClickListener == listener)
			{
				removed = mBoundedListeners.remove(bounds);
				
				mBoundsListenerCache.add(bounds);
				
				break;
			}
		}

		assert removed == true;
		
		checkTouchListener();
	}

	@Override
	public void removeBoundedOnTouchListener(IOnTouchListener listener)
	{
		assert listener != null;
		
		boolean removed = false;
		
		for (int i = 0; i < mBoundedTouchListeners.size(); i++)
		{
			BoundsListener<IOnTouchListener> bounds = mBoundedTouchListeners.get(i);
			
			if (bounds.mListener == listener)
			{
				removed = mBoundedTouchListeners.remove(bounds);

				mBoundsListenerIOnTouchCache.add(bounds);
				
				break;
			}
		}
		
		assert removed == true;
		
		removed = false;
		
		for (int i = 0; i < mBoundedListeners.size(); i++)
		{
			NewBoundsListener bounds = mBoundedListeners.get(i);
			
			if (bounds.mTouchListener == listener)
			{
				removed = mBoundedListeners.remove(bounds);
				
				mBoundsListenerCache.add(bounds);
				
				break;
			}
		}

		assert removed == true;
		
		checkTouchListener();
	}
	
	@Override
	public boolean onKey(View vikew, int keyCode, KeyEvent event)
	{
		boolean handled = false;

		KeyCodeEnum mappedKeyCode = mKeyMapping.get(keyCode);
		
		if (mappedKeyCode != null)
		{
			switch (event.getAction())
			{
				case KeyEvent.ACTION_DOWN :
					if (event.getRepeatCount() == 0)
					{
						handled = notifyKeyDown(mappedKeyCode, event);
					}
					break;
					
				case KeyEvent.ACTION_UP :
					if (event.getRepeatCount() == 0)
					{
						handled = notifyKeyUp(mappedKeyCode, event);
					}
					break;
			}
		}
		
		return handled;
	}

	private boolean notifyKeyUp(KeyCodeEnum keyCode, KeyEvent event)
	{
		boolean handled = false;
		
		List<IOnKeyUpListener> listeners = mKeyUpMap.get(keyCode);
		
		if (listeners != null)
		{
			int count = 0;
			
			for (int i = 0; i < listeners.size(); i++)
			{
				IOnKeyUpListener listener = listeners.get(i);
			
				if (listener.onKeyUp(keyCode))
				{
					count++;
				}
			}

			if (count != 0)
			{
				handled = true;
			}
		}
		
		return handled;
	}

	private boolean notifyKeyDown(KeyCodeEnum keyCode, KeyEvent event)
	{
		boolean handled = false;
		
		List<IOnKeyDownListener> listeners = mKeyDownMap.get(keyCode);
		
		if (listeners != null)
		{
			int count = 0;
			
			for (int i = 0; i < listeners.size(); i++)
			{
				IOnKeyDownListener listener = listeners.get(i);
			
				if (listener.onKeyDown(keyCode))
				{
					count++;
				}
			}
			
			if (count != 0)
			{
				handled = true;
			}
		}
		
		return handled;
	}

	@Override
	public boolean onTouch(View view, MotionEvent event)
	{
		mView.onTouchEvent(event);

		int action = event.getActionMasked();
		
		switch (action) 
		{
		    case MotionEvent.ACTION_DOWN :
		    case MotionEvent.ACTION_POINTER_DOWN :
		    	serviceTouchDown(event);
		        break;
		        
		    case MotionEvent.ACTION_MOVE :
		    	serviceTouchMove(event);
		        break;
		        
		    case MotionEvent.ACTION_UP :
		    case MotionEvent.ACTION_POINTER_UP :
		    	serviceTouchUp(event);
		        break;
	    }

	    return true;
	}
	
	public void serviceTouchDown(MotionEvent event)
	{
		int pointerId = event.getPointerId(event.getActionIndex());

		TouchEvent tEvent = mTouchEventCache.get();
		
		tEvent.action = TouchEvent.TouchAction.POINTER_DOWN;
		tEvent.x = event.getX();
		tEvent.y = event.getY();
		tEvent.touchId = pointerId;
		
		boolean touchHandled = false;
		boolean clickHandled = false;
		
		NewBoundsListener touchItem = null;
		NewBoundsListener clickItem = null;
		
		if (mBoundedListeners.size() > 0)
		{
			for (int i = 0; i < mBoundedListeners.size();)
			{
				NewBoundsListener item = mBoundedListeners.get(i);
		
				if (item.contains(event.getX(), event.getY()))
				{
					if (!touchHandled && item.isTouch())
					{
						if (!clickHandled || (clickItem.mClickListener == item.mTouchListener))
						{
							touchHandled = item.mTouchListener.onTouch(tEvent);
							
							if (touchHandled)
							{
								mBoundedListeners.remove(item);
								
								touchItem = item;
								
								mActiveNewBoundedTouchListeners.put(pointerId, item);
							}
							else
							{
								i++;
							}
						}
						else
						{
							i++;
						}
					}
					else if (!clickHandled && (item.isClick() || item.isLongClick()))
					{
						if (!touchHandled || (touchItem.mTouchListener == item.mClickListener))
						{
							TouchClickEvent cEvent = mTouchClickEventCache.get();
							
							cEvent.x = event.getX();
							cEvent.y = event.getY();
							cEvent.duration = event.getDownTime();
							cEvent.touchId = pointerId;
				
							mClickStartEvents.put(cEvent.touchId, cEvent);
	
							clickItem = item;
							
							clickHandled = true;
							
							i++;
						}
						else
						{
							i++;
						}
					}
					
					if (clickHandled && touchHandled)
					{
						break;
					}
				}
				else
				{
					i++;
				}
			}
		}
		
		if (!clickHandled && !touchHandled)
		{
			ArrayList<IOnTouchListener> touchListeners = mTouchListeners.get(pointerId);
			
			if (touchListeners != null)
			{
				for (int i = 0; i < touchListeners.size(); i++)
				{
					IOnTouchListener listener = touchListeners.get(i);
				
					listener.onTouch(tEvent);
				}
			}
		
			if ((mClickListeners.size() != 0) || (mLongClickListeners.size() != 0))
			{
				TouchClickEvent cEvent = mTouchClickEventCache.get();
				
				cEvent.x = event.getX();
				cEvent.y = event.getY();
				cEvent.duration = event.getDownTime();
				cEvent.touchId = pointerId;
	
				mClickStartEvents.put(cEvent.touchId, cEvent);
			}
		}
		
		mTouchEventCache.add(tEvent);
	}
	
	public void serviceTouchMove(MotionEvent event)
	{
		TouchEvent tEvent = mTouchEventCache.get();

		tEvent.action = TouchEvent.TouchAction.POINTER_MOVE;

		if (event.getHistorySize() > 0)
		{
			for (int j = 0; j < event.getHistorySize(); j++)
			{
			    for (int i = 0; i < event.getPointerCount(); i++)
			    {
			    	tEvent.touchId = event.getPointerId(i);
			    	tEvent.x = event.getHistoricalX(i, j);
			    	tEvent.y = event.getHistoricalY(i, j);

					boolean handled = false;
					
					NewBoundsListener boundedListener = mActiveNewBoundedTouchListeners.get(tEvent.touchId);

					if (boundedListener != null)
					{
						boundedListener.mTouchListener.onTouch(tEvent);
						
						handled = true;
					}
			    	
					if (!handled)
					{
						ArrayList<IOnTouchListener> touchListeners = mTouchListeners.get(tEvent.touchId);
						
						if (touchListeners != null)
						{
							for (int k = 0; k < touchListeners.size(); k++)
							{
								IOnTouchListener listener = touchListeners.get(k);
							
								listener.onTouch(tEvent);
							}
						}
					}
			    }
			}
		}
		else
		{
		    for (int i = 0; i < event.getPointerCount(); i++)
		    {
		    	tEvent.touchId = event.getPointerId(i);
		    	tEvent.x = event.getX(i);
		    	tEvent.y = event.getY(i);
		    	
				boolean handled = false;
				
				NewBoundsListener boundedListener = mActiveNewBoundedTouchListeners.get(tEvent.touchId);

				if (boundedListener != null)
				{
					boundedListener.mTouchListener.onTouch(tEvent);
					
					handled = true;
				}
		    	
				if (!handled)
				{
					ArrayList<IOnTouchListener> touchListeners = mTouchListeners.get(tEvent.touchId);
					
					if (touchListeners != null)
					{
						for (int j = 0; j < touchListeners.size(); j++)
						{
							IOnTouchListener listener = touchListeners.get(i);
						
							listener.onTouch(tEvent);
						}
					}
				}
		    }
		}
		
		mTouchEventCache.add(tEvent);
	}
	
	public void serviceTouchUp(MotionEvent event)
	{
		int pointerId = event.getPointerId(event.getActionIndex());

		TouchEvent tEvent = mTouchEventCache.get();
		
		tEvent.action = TouchEvent.TouchAction.POINTER_UP;
		tEvent.x = event.getX();
		tEvent.y = event.getY();
		tEvent.touchId = pointerId;
		
		boolean handled = false;
		
		NewBoundsListener boundedListener = mActiveNewBoundedTouchListeners.remove(pointerId);

		if (boundedListener != null)
		{
			mBoundedListeners.add(boundedListener);
			
			boundedListener.mTouchListener.onTouch(tEvent);
			
			handled = true;
		}
		
		if (!handled)
		{
			ArrayList<IOnTouchListener> touchListeners = mTouchListeners.get(pointerId);
			
			if (touchListeners != null)
			{
				for (int i = 0; i < touchListeners.size(); i++)
				{
					IOnTouchListener listener = touchListeners.get(i);
				
					listener.onTouch(tEvent);
				}
			}
		}
		
		TouchClickEvent cEvent = mClickStartEvents.remove(pointerId);

		if (cEvent != null)
		{
			cEvent.duration = event.getEventTime() - cEvent.duration;

			handled = false;
			
			if (mBoundedListeners.size() > 0)
			{
				for (int i = 0; i < mBoundedListeners.size(); i++)
				{
					NewBoundsListener item = mBoundedListeners.get(i);

					if (item.isClick())
					{
						if (item.contains(cEvent.x, cEvent.y))
						{
							handled = item.mClickListener.onClick(cEvent);
							
							if (handled)
							{
								break;
							}
						}
					}
				}
			}

			if (!handled)
			{
				ArrayList<IOnClickListener> clickListeners = mClickListeners.get(pointerId);
	
				if (clickListeners != null)
				{
					for (int i = 0; i < clickListeners.size(); i++)
					{
						IOnClickListener listener = clickListeners.get(i);
						
						listener.onClick(cEvent);
					}
				}
			}
			
			if (cEvent.duration > LONG_CLICK_TIME)
			{
				handled = false;
				
				if (mBoundedListeners.size() > 0)
				{
					for (int i = 0; i < mBoundedListeners.size(); i++)
					{
						NewBoundsListener item = mBoundedListeners.get(i);

						if (item.isLongClick())
						{
							if (item.contains(cEvent.x, cEvent.y))
							{
								handled = item.mLongClickListener.onLongClick(cEvent);
								
								if (handled)
								{
									break;
								}
							}
						}
					}
				}

				if (!handled)
				{
					ArrayList<IOnLongClickListener> longClickListeners = mLongClickListeners.get(pointerId);
	
					if (longClickListeners != null)
					{
						for (int i = 0; i < longClickListeners.size(); i++)
						{
							IOnLongClickListener listener = longClickListeners.get(i);
						
							listener.onLongClick(cEvent);
						}
					}
				}
			}
			
			mTouchClickEventCache.add(cEvent);
		}
		
		mTouchEventCache.add(tEvent);
	}
	
	private void checkTouchListener()
	{
		boolean existsListeners = (mTouchListeners.size() 
				+ mClickListeners.size() + mLongClickListeners.size()
				+ mBoundedListeners.size()
				+ mBoundedTouchListeners.size() 
				+ mBoundedClickListeners.size() + mBoundedLongClickListeners.size()) > 0;

		if (mIsTouchListening)
		{
			if (!existsListeners)
			{
				mView.setOnTouchListener(null);
				
				mIsTouchListening = false;
			}
		}
		else
		{
			if (existsListeners)
			{
				mView.setOnTouchListener(this);

				mIsTouchListening = true;
			}
		}
	}

	private void checkKeyListener()
	{
		boolean existsListeners = (mKeyDownMap.size() + mKeyUpMap.size()) > 0;

		if (mIsKeyListening)
		{
			if (!existsListeners)
			{
				mView.setOnKeyListener(null);
				
				mIsKeyListening = false;
			}
		}
		else
		{
			if (existsListeners)
			{
				mView.setOnKeyListener(this);

				mIsKeyListening = true;
			}
		}
	}
	
	private static class TouchEventCache extends ObjectCache<TouchEvent>
	{
		@Override
		protected TouchEvent createObject()
		{
			return new TouchEvent();
		}
	}
	
	private static class TouchClickEventCache extends ObjectCache<TouchClickEvent>
	{
		@Override
		protected TouchClickEvent createObject()
		{
			return new TouchClickEvent();
		}
	}

	private static class BoundsListenerCache<T> extends ObjectCache<BoundsListener<T>>
	{
		@Override
		protected BoundsListener<T> createObject()
		{
			return new BoundsListener<T>();
		}
	}

	private static class NewBoundsListenerCache extends ObjectCache<NewBoundsListener>
	{
		@Override
		protected NewBoundsListener createObject()
		{
			return new NewBoundsListener();
		}
	}
	
	private static class NewBoundsListener implements Comparable<NewBoundsListener>
	{
		public IOnClickListener mClickListener = null;
		public IOnLongClickListener mLongClickListener = null;
		public IOnTouchListener mTouchListener = null;
		
		public IBounds mBounds = null;
		public TouchZOrder mLayer = TouchZOrder.SPRITE; 
		
		public NewBoundsListener()
		{
		}

		public void set(IOnClickListener listener, IBounds bounds, TouchZOrder layer)
		{
			mClickListener = listener;
			mBounds = bounds;
			mLayer = layer;
		}
		
		public void set(IOnLongClickListener listener, IBounds bounds, TouchZOrder layer)
		{
			mLongClickListener = listener;
			mBounds = bounds;
			mLayer = layer;
		}

		public void set(IOnTouchListener listener, IBounds bounds, TouchZOrder layer)
		{
			mTouchListener = listener;
			mBounds = bounds;
			mLayer = layer;
		}

		public boolean isTouch()
		{
			return (mTouchListener != null);
		}
		
		public boolean isClick()
		{
			return (mClickListener != null);
		}
		
		public boolean isLongClick()
		{
			return (mLongClickListener != null);
		}

		@Override
		public int compareTo(NewBoundsListener object)
		{
			int ordinal = mLayer.ordinal();
			int listOrdinal = object.mLayer.ordinal();
			
			int comp = 0;
			
			if (ordinal > listOrdinal)
			{
				comp = 1;
			}
			else if (ordinal < listOrdinal)
			{
				comp = -1;
			}
			
			return comp;
		}
		
		public boolean contains(float x, float y)
		{
			return mBounds.contains(x, y);
		}
	}
	
	private static class BoundsListener<T> implements Comparable<BoundsListener<T>>
	{
		public T mListener = null;
		public IBounds mBounds = null;
		public TouchZOrder mLayer = TouchZOrder.SPRITE; 
		
		public BoundsListener()
		{
		}

		public void set(T listener, IBounds bounds, TouchZOrder layer)
		{
			mListener = listener;
			mBounds = bounds;
			mLayer = layer;
		}

		@Override
		public int compareTo(BoundsListener<T> object)
		{
			int ordinal = mLayer.ordinal();
			int listOrdinal = object.mLayer.ordinal();
			
			int comp = 0;
			
			if (ordinal > listOrdinal)
			{
				comp = 1;
			}
			else if (ordinal < listOrdinal)
			{
				comp = -1;
			}
			
			return comp;
		}
		
		public boolean contains(float x, float y)
		{
			return mBounds.contains(x, y);
		}
	}
}
