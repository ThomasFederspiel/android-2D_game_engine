package se.federspiel.android.game.interfaces;

import se.federspiel.android.game.ConstraintsManager;
import se.federspiel.android.game.FontManager;
import se.federspiel.android.game.geometry.Rectangle;
import se.federspiel.android.game.ui.UIMainWindow;
import android.content.Context;


public interface IGameContext
{
    public IGameEngine getGameEngine();
    
    public Context getApplContext();
    
    public ISpriteFactory getSpriteFactory();
    public ITrajectoryFactory getTrajectoryFactory();
    public IBackgroundFactory getBackgroundFactory();
    public IGraphicPrimitiveFactory getGraphicPrimitiveFactory();
    
    public ICollisionManager getCollisionManager();
    public ISoundManager getSoundManager();
    public IServiceManager getServiceManager();
    public IUserInputManager getUserInputManager();
    public ISensorManager getSensorManager();
    public IImageManager getImageManager();
    public UIMainWindow getMainWindow();
    public FontManager getFontManager();
    public ConstraintsManager getConstraintsManager();
	public boolean hasPermission(String permName);
	public boolean isApplicationFlagEnabled(int flag);

    public Rectangle getGraphicBounds();

    public void load();
    public void unload();
    public void destroy();
}
