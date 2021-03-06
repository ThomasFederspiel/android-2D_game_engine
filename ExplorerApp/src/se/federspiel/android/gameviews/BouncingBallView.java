package se.federspiel.android.gameviews;

import java.util.Random;

import se.federspiel.android.agraphics.CanvasSurfaceView;
import se.federspiel.android.agraphics.DrawThread.IDrawer;
import se.federspiel.android.backgrounds.ColorBackground;
import se.federspiel.android.game.GameApplication;
import se.federspiel.android.game.GameEngine;
import se.federspiel.android.game.geometry.Dimensions;
import se.federspiel.android.game.geometry.Point;
import se.federspiel.android.game.geometry.Vector2;
import se.federspiel.android.game.interfaces.IBackground;
import se.federspiel.android.game.interfaces.ICollisionSprite;
import se.federspiel.android.game.interfaces.IGameContext;
import se.federspiel.android.game.interfaces.ISprite;
import se.federspiel.android.game.interfaces.ITrajectory;
import se.federspiel.android.game.sprites.GraphicSprite;
import se.federspiel.android.game.sprites.actions.SpriteElasticBounceAction;
import se.federspiel.android.game.sprites.actions.SpriteNonAdjustAction;
import se.federspiel.android.game.sprites.drawers.BallDrawer;
import se.federspiel.android.game.sprites.drawers.BoxDrawer;
import se.federspiel.android.game.trajectories.MovementTrajectory;
import se.federspiel.android.game.trajectories.KeyControlledTrajectory;
import se.federspiel.android.game.trajectories.OrientationControlledTrajectory;
import se.federspiel.android.game.trajectories.limits.GraphicsViewLimits;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;

public class BouncingBallView extends CanvasSurfaceView
{
	private GameApplication mGameApplication = null;
	private GameEngine mGameEngine = null;
	private IGameContext mGameContext = null;
	
	public BouncingBallView(Context context)
	{
		super(context);
	}

	public BouncingBallView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}
	
	public BouncingBallView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}

    private void createLotsOfBalls(int nofBalls, GameEngine gameEngine, IGameContext gameContext)
    {
        Random rand = new Random();

        for (int i = 0; i < nofBalls; i++)
        {
            int speed = 250; // px/s
            int positionX = 100;
            int positionY = 100;
            
            
//            int positionX = rand.nextInt(getWidth());
//            int positionY = rand.nextInt(getHeight());    
            
            ISprite sprite = gameContext.getSpriteFactory().createSprite("Graphic");
            
            BallDrawer drawer = new BallDrawer(gameContext);
            drawer.setRadius(10);
            ((GraphicSprite)sprite).setGraphicDrawer(drawer);

            ITrajectory trajectory = gameContext.getTrajectoryFactory().createTrajectory("ConstantVelocityTrajectory", sprite);

            trajectory.setInitialSpeed(new Vector2(speed, speed));
            trajectory.setPositionLimits(new GraphicsViewLimits(gameContext));
   
            SpriteElasticBounceAction elasticAction = new SpriteElasticBounceAction();
            ((ICollisionSprite) sprite).setCollisionAction(elasticAction);
            
            sprite.setInitialPosition(new Point(positionX, positionY));
            sprite.getPhysicalProperties().setMass(1);
            
            gameContext.getCollisionManager().add(sprite.getCollisionObject());

            gameEngine.addComponent(sprite);
        }
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
		
        mGameContext.getSpriteFactory().registerSprite("Graphic", GraphicSprite.class);
        
        mGameContext.getTrajectoryFactory().registerTrajectory("ConstantVelocityTrajectory", MovementTrajectory.class);
        mGameContext.getTrajectoryFactory().registerTrajectory("KeyControlledTrajectory", KeyControlledTrajectory.class);
        mGameContext.getTrajectoryFactory().registerTrajectory("OrientationControlledTrajectory", OrientationControlledTrajectory.class);
        
        mGameContext.getBackgroundFactory().registerBackground("Color", ColorBackground.class);
		
        int viewHeight = getHeight();

        createLotsOfBalls(1, mGameEngine, mGameContext);

        ISprite sprite = mGameContext.getSpriteFactory().createSprite("Graphic");
        
        BoxDrawer drawer = new BoxDrawer(mGameContext);
        drawer.setDimensions(new Dimensions(100, 10));
        ((GraphicSprite) sprite).setGraphicDrawer(drawer);

        int speed = 250; // px/s
        int acceleration = 40; // px/s^2

        KeyControlledTrajectory trajectory = new KeyControlledTrajectory(mGameContext, sprite);
        
        trajectory.setInitialSpeed(new Vector2(speed, 0));
        trajectory.setInitialAcceleration(new Vector2(acceleration, 0));
        trajectory.setPositionLimits(new GraphicsViewLimits(mGameContext));
        
        SpriteNonAdjustAction spriteAction = new SpriteNonAdjustAction();
        ((ICollisionSprite) sprite).setCollisionAction(spriteAction);
        
        sprite.setTrajectory(trajectory);
        sprite.setInitialPosition(new Point(100, 20));

        mGameContext.getCollisionManager().add(sprite.getCollisionObject());

		mGameEngine.addComponent(sprite);
		
        sprite = mGameContext.getSpriteFactory().createSprite("Graphic");
        
        drawer = new BoxDrawer(mGameContext);
        drawer.setDimensions(new Dimensions(100, 10));
        ((GraphicSprite) sprite).setGraphicDrawer(drawer);
        
        trajectory = new KeyControlledTrajectory(mGameContext, sprite);

        trajectory.setInitialSpeed(new Vector2(speed, 0));
        trajectory.setInitialAcceleration(new Vector2(acceleration, 0));
        trajectory.setPositionLimits(new GraphicsViewLimits(mGameContext));
        
        spriteAction = new SpriteNonAdjustAction();
        ((ICollisionSprite) sprite).setCollisionAction(spriteAction);
        
        sprite.setTrajectory(trajectory);
        sprite.setInitialPosition(new Point(100, viewHeight - 20));
        
        mGameContext.getCollisionManager().add(sprite.getCollisionObject());

        mGameEngine.addComponent(sprite);
		
        IBackground background = mGameContext.getBackgroundFactory().createBackground("Color");
        ((ColorBackground) background).setColor(Color.BLUE);
               
		mGameEngine.addBackground(background);

		mGameContext.getSoundManager().addSound(com.example.explorerapp.R.raw.boing_rebound_01);
	}
}
