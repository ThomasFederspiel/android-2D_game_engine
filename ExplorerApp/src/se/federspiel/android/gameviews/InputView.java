package se.federspiel.android.gameviews;

import se.federspiel.android.agraphics.CanvasSurfaceView;
import se.federspiel.android.agraphics.DrawThread.IDrawer;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

public class InputView extends CanvasSurfaceView
{
	public InputView(Context context)
	{
		super(context);
	}

	public InputView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}
	
	public InputView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}

	@Override
	protected IDrawer createDrawer()
	{
    	return new IDrawer()
    	{
			@Override
			public void update()
			{
			}

			@Override
			public void draw(Canvas canvas)
			{
			}

			@Override
			public void unload()
			{
			}

			@Override
			public void load()
			{
			}

			@Override
			public void terminate()
			{
			}
    	};
	}
}
