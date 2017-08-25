package se.federspiel.android.gameviews;

import se.federspiel.android.agraphics.CanvasSurfaceView;
import se.federspiel.android.agraphics.DrawThread.IDrawer;
import se.federspiel.android.backgrounds.ColorBackground;
import se.federspiel.android.game.GameApplication;
import se.federspiel.android.game.GameEngine;
import se.federspiel.android.game.geometry.Point;
import se.federspiel.android.game.interfaces.IBackground;
import se.federspiel.android.game.interfaces.ICollisionSprite;
import se.federspiel.android.game.interfaces.IGameContext;
import se.federspiel.android.game.interfaces.ISprite;
import se.federspiel.android.game.interfaces.ITrajectory;
import se.federspiel.android.game.sprites.GraphicSprite;
import se.federspiel.android.game.sprites.ImageSprite;
import se.federspiel.android.game.sprites.actions.SpriteStickAction;
import se.federspiel.android.game.sprites.actions.SpriteStickAction.StickDirection;
import se.federspiel.android.game.sprites.drawers.BoxDrawer;
import se.federspiel.android.game.sprites.drawers.ImageDrawer;
import se.federspiel.android.game.trajectories.MovementTrajectory;
import se.federspiel.android.game.trajectories.KeyControlledTrajectory;
import se.federspiel.android.game.trajectories.OrientationControlledTrajectory;
import se.federspiel.android.game.trajectories.TouchSlideTrajectory;
import se.federspiel.android.game.trajectories.TouchSlideTrajectory.TouchLock;
import se.federspiel.android.game.trajectories.limits.GraphicsViewLimits;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;

import com.example.explorerapp.R;

public class SlideShooterView extends CanvasSurfaceView
{
	private static final int SPACE_SHIP_WIDTH = 102;
	private static final int SPACE_SHIP_HEIGHT = 84;
	
	private GameApplication mGameApplication = null;
	private GameEngine mGameEngine = null;
	private IGameContext mGameContext = null;
	
	public SlideShooterView(Context context)
	{
		super(context);
	}

	public SlideShooterView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}
	
	public SlideShooterView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}

	@Override
	protected IDrawer createDrawer()
	{
    	if (mGameEngine == null)
    	{
	    	initGame();
    	}
    	
    	return mGameEngine;
	}
	
    private void initGame()
	{
    	mGameApplication = new GameApplication(this);

		mGameEngine = GameApplication.getGameEngine();
		mGameContext = mGameEngine.getGameContext();
		
        mGameContext.getSpriteFactory().registerSprite("Image", ImageSprite.class);
        mGameContext.getSpriteFactory().registerSprite("Graphic", GraphicSprite.class);
    
        mGameContext.getTrajectoryFactory().registerTrajectory("ConstantVelocityTrajectory", MovementTrajectory.class);
        mGameContext.getTrajectoryFactory().registerTrajectory("KeyControlledTrajectory", KeyControlledTrajectory.class);
        mGameContext.getTrajectoryFactory().registerTrajectory("OrientationControlledTrajectory", OrientationControlledTrajectory.class);
        mGameContext.getTrajectoryFactory().registerTrajectory("TouchSlideTrajectory", TouchSlideTrajectory.class);
        
        mGameContext.getBackgroundFactory().registerBackground("Color", ColorBackground.class);
		
        ISprite sprite = mGameContext.getSpriteFactory().createSprite("Image");
        ImageSprite imageSprite = (ImageSprite) sprite;
        
        ImageDrawer drawer = new ImageDrawer(mGameContext);
        drawer.setBitmapResource(R.raw.spaceship_0, SPACE_SHIP_WIDTH, SPACE_SHIP_HEIGHT);
        imageSprite.addImageDrawer(drawer);

        ITrajectory trajectory = mGameContext.getTrajectoryFactory().createTrajectory("TouchSlideTrajectory", sprite);
        ((TouchSlideTrajectory) trajectory).setSlideRelease(true);
        ((TouchSlideTrajectory) trajectory).setLock(TouchLock.LOCK_NO);
        trajectory.setPositionLimits(new GraphicsViewLimits(mGameContext));
        
        SpriteStickAction action = new SpriteStickAction();
        action.setStickDirection(StickDirection.NORMAL_DIRECTION);
        ((ICollisionSprite) sprite).setCollisionAction(action);
        
        sprite.setInitialPosition(new Point(360, 100));

        mGameContext.getCollisionManager().add(sprite.getCollisionObject());
        
		mGameEngine.addComponent(sprite);

        sprite = mGameContext.getSpriteFactory().createSprite("Image");
        imageSprite = (ImageSprite) sprite;

        drawer = new ImageDrawer(mGameContext);
        drawer.setBitmapResource(R.raw.spaceship_0, SPACE_SHIP_WIDTH, SPACE_SHIP_HEIGHT);
        imageSprite.addImageDrawer(drawer);
        
        trajectory = mGameContext.getTrajectoryFactory().createTrajectory("TouchSlideTrajectory", sprite);
        ((TouchSlideTrajectory) trajectory).setSlideRelease(true);
        ((TouchSlideTrajectory) trajectory).setLock(TouchLock.LOCK_NO);
        trajectory.setPositionLimits(new GraphicsViewLimits(mGameContext));
        
        action = new SpriteStickAction();
        action.setStickDirection(StickDirection.NORMAL_DIRECTION);
        ((ICollisionSprite) sprite).setCollisionAction(action);
        
        sprite.setInitialPosition(new Point(360, 900));

        mGameContext.getCollisionManager().add(sprite.getCollisionObject());
        
		mGameEngine.addComponent(sprite);

		//-----
		
        sprite = mGameContext.getSpriteFactory().createSprite("Graphic");
        
        BoxDrawer boxDrawer = new BoxDrawer(mGameContext);
        boxDrawer.setDimensions(200, 5);
        ((GraphicSprite)sprite).setGraphicDrawer(boxDrawer);
        
//        LineDrawer drawer = new LineDrawer();
//        drawer.setLine(300, 640, 600, 640);
//        ((GraphicSprite)sprite).setGraphicDrawer(drawer);

        sprite.setInitialPosition(new Point(300, 600));
            
        mGameContext.getCollisionManager().add(sprite.getCollisionObject());

        mGameEngine.addComponent(sprite);

		//-----
		
        IBackground background = mGameContext.getBackgroundFactory().createBackground("Color");
        ((ColorBackground) background).setColor(Color.BLUE);
        
		mGameEngine.addBackground(background);

		mGameContext.getSoundManager().addSound(com.example.explorerapp.R.raw.boing_rebound_01);
	}
}
