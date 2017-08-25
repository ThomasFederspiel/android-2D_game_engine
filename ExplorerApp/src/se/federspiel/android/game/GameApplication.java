package se.federspiel.android.game;

import se.federspiel.android.game.interfaces.IGameContext;
import se.federspiel.android.game.interfaces.IGraphicsView;
import se.federspiel.android.game.ui.UIMainWindow;
import android.content.res.Resources;
import android.util.DisplayMetrics;

public class GameApplication
{
    private static GameEngine sGameEngine = null;

    private static DisplayMetrics sDisplayMetrics = null;
    
    public GameApplication(IGraphicsView  graphicsView)
    {
		sGameEngine = new GameEngine(graphicsView);
		
		sDisplayMetrics = sGameEngine.getGameContext().getApplContext().getResources().getDisplayMetrics();
    }
    
    public static UIMainWindow getMainWindow()
    {
    	return sGameEngine.getGameContext().getMainWindow();
    }

	public static GameEngine getGameEngine()
	{
		return sGameEngine;
	}
	
	public static IGameContext getGameContext()
	{
		return sGameEngine.getGameContext();
	}

	public static DisplayMetrics getDisplayMetrics()
	{
		return sDisplayMetrics;
	}

	public static FontManager getFontManager()
    {
		return sGameEngine.getGameContext().getFontManager();
	}
    
    public static Resources getResources()
    {
		return sGameEngine.getGameContext().getApplContext().getResources();
	}
}
