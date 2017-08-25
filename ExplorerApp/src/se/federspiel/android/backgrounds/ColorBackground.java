package se.federspiel.android.backgrounds;

import se.federspiel.android.game.GameRenderer;
import se.federspiel.android.game.interfaces.IGameContext;
import android.graphics.Canvas;
import android.graphics.Paint;

public class ColorBackground extends AbstractBackground
{
	private Paint mBackgroundColor = null;

	public ColorBackground(IGameContext gameContext)
    {
		super(gameContext);
    }

	public void setColor(int color)
	{
		mBackgroundColor = new Paint();
		mBackgroundColor.setColor(color);
	}
	
	@Override
	public void draw(GameRenderer renderer)
	{
		Canvas canvas = renderer.getCanvas();
		
		canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), mBackgroundColor);
	}
}
