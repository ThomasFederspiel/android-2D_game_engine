package se.federspiel.android.game.utils;

import java.util.ArrayList;

import se.federspiel.android.game.GameApplication;
import se.federspiel.android.game.GameRenderer;
import se.federspiel.android.game.geometry.Rectangle;
import se.federspiel.android.game.interfaces.IBounds;
import se.federspiel.android.game.interfaces.IGraphicDrawer;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

public class GameTools 
{
	public static Bitmap createBitmap(ArrayList<IGraphicDrawer> drawers, int backgroundColor)
	{
		Rectangle bounds = Rectangle.Zero.clone();
		bounds.set(GameApplication.getGameContext().getGraphicBounds());

		evaluateDrawersMinEnclosingBounds(drawers, bounds);

		Bitmap image = Bitmap.createBitmap(bounds.getWidth(), bounds.getHeight(), Bitmap.Config.RGB_565);

		Canvas canvas = new Canvas();
		canvas.setBitmap(image);

		Paint paint = new Paint();
		paint.setColor(backgroundColor);
		
		canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), paint);
		
		GameRenderer renderer = new GameRenderer(canvas);
		renderer.setBounds(bounds);
		
		for (int i = 0; i < drawers.size(); i++)
		{
			drawers.get(i).draw(renderer);			
		}

		return image;
	}
	
	public static Rectangle evaluateDrawersMinEnclosingBounds(ArrayList<IGraphicDrawer> drawers, Rectangle bounds)
	{
		for (int i = 0; i < drawers.size(); i++)
		{
			IBounds bound = drawers.get(i).getBounds();

			bounds.union(bound);
		}
		
		return bounds;
	}
}
