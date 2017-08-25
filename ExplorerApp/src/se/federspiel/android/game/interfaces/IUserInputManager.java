package se.federspiel.android.game.interfaces;

public interface IUserInputManager
{
	public enum TouchZOrder
	{
		BACKGROUND,
		SPRITE,
		UI_MAIN_WINDOW,
		UI_MAIN_WINDOW_PANE,
		UI_DIALOG
	}
	
	public enum KeyCodeEnum
	{
		KeyLeft,
		KeyRight,
		KeyUp,
		KeyDown;
	}

	public class TouchClickEvent
	{
		public float x = 0;
		public float y = 0;
		public long duration = 0;
		public int touchId = 0;
	}
	
	public class TouchEvent
	{
		public enum TouchAction
		{
			POINTER_DOWN,
			POINTER_UP,
			POINTER_MOVE;
		}
		
		public TouchAction action = TouchAction.POINTER_DOWN;
		public float x = 0;
		public float y = 0;
		public int touchId = 0;
	}

	public void setBoundedOnClickListener(IOnClickListener listener, IBounds bounds, TouchZOrder layer);
	public void setBoundedOnLongClickListener(IOnLongClickListener listener, IBounds bounds, TouchZOrder layer);
	public void setBoundedOnTouchListener(IOnTouchListener listener, IBounds bounds, TouchZOrder layer);
	
	public void removeBoundedOnClickListener(IOnClickListener listener);
	public void removeBoundedOnLongClickListener(IOnLongClickListener listener);
	public void removeBoundedOnTouchListener(IOnTouchListener listener);
	
	public void setOnClickListener(IOnClickListener listener);
	public void setOnClickListener(IOnClickListener listener, int pointerId);
	public void setOnLongClickListener(IOnLongClickListener listener);
	public void setOnLongClickListener(IOnLongClickListener listener, int pointerId);
	public void setOnTouchListener(IOnTouchListener listener);
	public void setOnTouchListener(IOnTouchListener listener, int pointerId);
	
	public void setOnKeyDownListener(IOnKeyDownListener listener, KeyCodeEnum keyCode);
	public void setOnKeyUpListener(IOnKeyUpListener listener, KeyCodeEnum keyCode);

	public void removeOnClickListener(IOnClickListener listener);
	public void removeOnClickListener(IOnClickListener listener, int pointerId);
	public void removeOnLongClickListener(IOnLongClickListener listener);
	public void removeOnLongClickListener(IOnLongClickListener listener, int pointerId);
	public void removeOnTouchListener(IOnTouchListener listener);
	public void removeOnTouchListener(IOnTouchListener listener, int pointerId);
	
	public void removeOnKeyDownListener(IOnKeyDownListener listener, KeyCodeEnum keyCode);
	public void removeOnKeyUpListener(IOnKeyUpListener listener, KeyCodeEnum keyCode);
	
	public interface IOnKeyDownListener
	{
		public boolean onKeyDown(KeyCodeEnum keyCode); 
	}

	public interface IOnKeyUpListener
	{
		public boolean onKeyUp(KeyCodeEnum keyCode); 
	}

	public interface IOnClickListener
	{
		public boolean onClick(TouchClickEvent event); 
	}
	
	public interface IOnLongClickListener
	{
		public boolean onLongClick(TouchClickEvent event); 
	}

	public interface IOnTouchListener
	{
		public boolean onTouch(TouchEvent event); 
	}
}
