package se.federspiel.android.game;

import se.federspiel.android.game.geometry.Rectangle;
import se.federspiel.android.game.interfaces.IBackgroundFactory;
import se.federspiel.android.game.interfaces.ICollisionManager;
import se.federspiel.android.game.interfaces.IGameContext;
import se.federspiel.android.game.interfaces.IGameEngine;
import se.federspiel.android.game.interfaces.IGraphicPrimitiveFactory;
import se.federspiel.android.game.interfaces.IGraphicsView;
import se.federspiel.android.game.interfaces.IImageManager;
import se.federspiel.android.game.interfaces.ISensorManager;
import se.federspiel.android.game.interfaces.IServiceManager;
import se.federspiel.android.game.interfaces.ISoundManager;
import se.federspiel.android.game.interfaces.ISpriteFactory;
import se.federspiel.android.game.interfaces.ITrajectoryFactory;
import se.federspiel.android.game.interfaces.IUserInputManager;
import se.federspiel.android.game.ui.UIMainWindow;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

public class GameContext implements IGameContext
{
    private ISpriteFactory mSpriteFactory = null;
    private ITrajectoryFactory mTrajectoryFactory = null;
    private IBackgroundFactory mBackgroundFactory = null;
	private IGraphicPrimitiveFactory mGraphicPrimitiveFactory = null;

    private ICollisionManager mCollisionManager = null;
    private IGraphicsView mGraphicsView = null;
    private ISoundManager mSoundManager = null;
    private IServiceManager mServiceManager = null;
    private IUserInputManager mUserInputManager = null;
    private ISensorManager mSensorManager = null;
    private IImageManager mImageManager = null;

    private ConstraintsManager mConstraintsManager = null;
    
    private UIMainWindow mMainUIWindow = null;

    private FontManager mFontManager = null;
    
    private IGameEngine mGameEngine = null;
    
    private Context mApplicationContext = null;

    public GameContext(IGraphicsView graphicsView, IGameEngine gameEngine)
    {
    	mApplicationContext = graphicsView.getContext();
    	mGraphicsView = graphicsView;
        mGameEngine = gameEngine;
    	
        mSpriteFactory = new SpriteFactory(this);
        mTrajectoryFactory = new TrajectoryFactory(this);
        mBackgroundFactory = new BackgroundFactory(this);
        mCollisionManager = new CollisionManager(this);
        mSoundManager = new SoundManager(this);
        mServiceManager = new ServiceManager(mApplicationContext);
        mUserInputManager = new UserInputManager(graphicsView);
        mSensorManager = new SensorManager(mApplicationContext);
        mImageManager = new ImageManager(this);

        mConstraintsManager = new ConstraintsManager(this);
        
		mMainUIWindow = new UIMainWindow(0, 0, mGraphicsView.getWidth(), mGraphicsView.getHeight());
		
		mGameEngine.addComponent(mMainUIWindow);
		
        mFontManager = new FontManager(mApplicationContext);
        // mFontManager.loadFont("venusris.ttf");
    }

    @Override
    public Context getApplContext()
    {
        return mApplicationContext;
    }
    
    @Override
    public ISpriteFactory getSpriteFactory()
    {
        return mSpriteFactory;
    }

    @Override
    public ITrajectoryFactory getTrajectoryFactory()
    {
        return mTrajectoryFactory;
    }

    @Override
    public IBackgroundFactory getBackgroundFactory()
    {
        return mBackgroundFactory;
    }

	@Override
	public IGraphicPrimitiveFactory getGraphicPrimitiveFactory()
	{
        return mGraphicPrimitiveFactory;
	}
	
    @Override
    public ICollisionManager getCollisionManager()
    {
        return mCollisionManager;
    }

    @Override
    public ISoundManager getSoundManager()
    {
        return mSoundManager;
    }

    @Override
    public IServiceManager getServiceManager()
    {
    	return mServiceManager;
    }
    
    @Override
    public IUserInputManager getUserInputManager()
    {
    	return mUserInputManager;
    }

    @Override
    public ISensorManager getSensorManager()
    {
    	return mSensorManager;
    }
    
    @Override
    public IImageManager getImageManager()
    {
    	return mImageManager;
    }
    
    @Override
    public UIMainWindow getMainWindow()
    {
    	return mMainUIWindow;
    }

    @Override
	public IGameEngine getGameEngine()
	{
		return mGameEngine;
	}
	
	@Override
    public FontManager getFontManager()
    {
    	return mFontManager;
    }

	@Override
	public ConstraintsManager getConstraintsManager()
	{
		return mConstraintsManager;
	}
	
	@Override
	public boolean hasPermission(String permName)
	{
	    PackageManager pm = mApplicationContext.getPackageManager();
	    
	    return (pm.checkPermission(permName, mApplicationContext.getPackageName()) == PackageManager.PERMISSION_GRANTED);
	}
	
	@Override
	public boolean isApplicationFlagEnabled(int flag)
	{
	    PackageManager pm = mApplicationContext.getPackageManager();

	    try
	    {
		    PackageInfo packageInfo = pm.getPackageInfo(mApplicationContext.getPackageName(), 0);
	
		    if (packageInfo != null)
		    {
		    	ApplicationInfo applicationInfo = packageInfo.applicationInfo;
		    	
		    	if (applicationInfo != null)
		    	{
		    		return (applicationInfo.flags & flag) != 0;
		    	}
		    }
	    }
	    catch (PackageManager.NameNotFoundException nnfe)
	    {
	    	// Ignore
	    }
	    
	    return false;
	}


	@Override
	public Rectangle getGraphicBounds()
	{
		return mGraphicsView.getBounds();
	}

	@Override
    public void load()
    {
    }
    
	@Override
    public void unload()
    {
    	mSoundManager.release();
    }
    
	@Override
    public void destroy()
    {
		mGameEngine.destroy();
		mCollisionManager.destroy();
		mImageManager.destroy();
		
	    mSpriteFactory.destroy();
	    mTrajectoryFactory.destroy();
	    mBackgroundFactory.destroy();
    }
}
