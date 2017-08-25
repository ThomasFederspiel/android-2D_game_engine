package se.federspiel.android.game.interfaces;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import se.federspiel.android.game.geometry.Rectangle;

public interface IGraphicsView
{
	public Context getContext();
	
	public void setOnTouchListener(OnTouchListener listener); 
	public void setOnKeyListener(OnKeyListener listener);

	public void setFocusable(boolean focusable);
	public void setFocusableInTouchMode(boolean focusableInTouchMode);
	public boolean requestingFocus();
	
	public boolean onTouchEvent(MotionEvent event);
	
	public int getWidth();
	public int getHeight();

	public Rectangle getBounds();
}
