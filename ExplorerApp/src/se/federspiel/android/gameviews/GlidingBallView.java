package se.federspiel.android.gameviews;

import java.util.Random;

import se.federspiel.android.agraphics.CanvasSurfaceView;
import se.federspiel.android.agraphics.DrawThread.IDrawer;
import se.federspiel.android.backgrounds.ColorBackground;
import se.federspiel.android.game.GameApplication;
import se.federspiel.android.game.GameEngine;
import se.federspiel.android.game.collision.CollisionEvaluatorLibrary;
import se.federspiel.android.game.geometry.Point;
import se.federspiel.android.game.geometry.Vector2;
import se.federspiel.android.game.interfaces.IBackground;
import se.federspiel.android.game.interfaces.ICollisionSprite;
import se.federspiel.android.game.interfaces.IGameContext;
import se.federspiel.android.game.interfaces.ISprite;
import se.federspiel.android.game.sprites.GraphicSprite;
import se.federspiel.android.game.sprites.actions.SpriteElasticBounceAction;
import se.federspiel.android.game.sprites.drawers.BallDrawer;
import se.federspiel.android.game.trajectories.MovementTrajectory;
import se.federspiel.android.game.trajectories.KeyControlledTrajectory;
import se.federspiel.android.game.trajectories.OrientationControlledTrajectory;
import se.federspiel.android.game.trajectories.limits.GraphicsViewLimits;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;

public class GlidingBallView extends CanvasSurfaceView
{
	private GameApplication mGameApplication = null;
	private GameEngine mGameEngine = null;
	private IGameContext mGameContext = null;
	
	public GlidingBallView(Context context)
	{
		super(context);
	}

	public GlidingBallView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}
	
	public GlidingBallView(Context context, AttributeSet attrs, int defStyle)
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
	
    private void createLotsOfBalls(int nofBalls, GameEngine gameEngine, IGameContext gameContext)
    {
        Random rand = new Random();

        for (int i = 0; i < nofBalls; i++)
        {
            int speed = 250; // px/s
            
            int positionX = rand.nextInt(getWidth());
            int positionY = rand.nextInt(getHeight());    
            
            ISprite sprite = gameContext.getSpriteFactory().createSprite("Graphic");
            
            BallDrawer drawer = new BallDrawer(mGameContext);
            drawer.setRadius(10);
            ((GraphicSprite)sprite).setGraphicDrawer(drawer);

            OrientationControlledTrajectory trajectory = (OrientationControlledTrajectory) mGameContext.getTrajectoryFactory().createTrajectory("OrientationControlledTrajectory", sprite);

            trajectory.setInitialSpeed(new Vector2(0, 0));
            trajectory.setAccelerationGain(20);
            trajectory.setLimitBounceFactor(0.4f);
            trajectory.setResistence(rand.nextInt(40));
            trajectory.setPositionLimits(new GraphicsViewLimits(gameContext));
   
            gameContext.getCollisionManager().add(sprite.getCollisionObject());

            SpriteElasticBounceAction spriteAction = new SpriteElasticBounceAction();
            ((ICollisionSprite) sprite).setCollisionAction(spriteAction);
            
            sprite.setInitialPosition(new Point(positionX, positionY));
            sprite.getPhysicalProperties().setMass(1);
            sprite.getPhysicalProperties().setRestitution(0.6f);

            gameEngine.addComponent(sprite);
        }
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
		
		mGameContext.getCollisionManager().setCollisionEvaluator(new CollisionEvaluatorLibrary.RepeatNearestFoundCollisionSelectorEvaluator());

		int viewWidth = getWidth();
        int viewHeight = getHeight();

        createLotsOfBalls(3, mGameEngine, mGameContext);

        IBackground background = mGameContext.getBackgroundFactory().createBackground("Color");
        ((ColorBackground) background).setColor(Color.BLUE);
        
		mGameEngine.addBackground(background);

		mGameContext.getSoundManager().addSound(com.example.explorerapp.R.raw.boing_rebound_01);
	}
}
