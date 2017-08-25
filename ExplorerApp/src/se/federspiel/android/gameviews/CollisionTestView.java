package se.federspiel.android.gameviews;

import java.util.ArrayList;
import java.util.Random;

import se.federspiel.android.agraphics.CanvasSurfaceView;
import se.federspiel.android.agraphics.DrawThread.IDrawer;
import se.federspiel.android.backgrounds.ColorBackground;
import se.federspiel.android.backgrounds.DrawBackground;
import se.federspiel.android.backgrounds.ImageBackground;
import se.federspiel.android.backgrounds.ImageSpriteBackground;
import se.federspiel.android.backgrounds.ParallaxBackground;
import se.federspiel.android.backgrounds.drawers.BezierDrawer;
import se.federspiel.android.backgrounds.trajectories.UITouchTrajectory;
import se.federspiel.android.game.ConstraintsManager.ConstraintsGroup;
import se.federspiel.android.game.GameApplication;
import se.federspiel.android.game.GameEngine;
import se.federspiel.android.game.collision.CollisionEvaluatorLibrary;
import se.federspiel.android.game.collision.CollisionSet;
import se.federspiel.android.game.constraints.FixedBaseConstraintObject;
import se.federspiel.android.game.constraints.RigidLinkConstraint;
import se.federspiel.android.game.constraints.SpringConstraint;
import se.federspiel.android.game.drawers.LinkDrawer;
import se.federspiel.android.game.geometry.Dimensions;
import se.federspiel.android.game.geometry.Point;
import se.federspiel.android.game.geometry.Rectangle;
import se.federspiel.android.game.geometry.Vector2;
import se.federspiel.android.game.interfaces.IBackground;
import se.federspiel.android.game.interfaces.ICollisionSprite;
import se.federspiel.android.game.interfaces.IDrawableComponent.DrawableZOrder;
import se.federspiel.android.game.interfaces.IGameContext;
import se.federspiel.android.game.interfaces.IGameEngine.GameViewCoordinateSystem;
import se.federspiel.android.game.interfaces.IScrollableBackground.ScrollDirections;
import se.federspiel.android.game.interfaces.ISprite;
import se.federspiel.android.game.interfaces.ISprite.ISpriteOutOfBoundsListener;
import se.federspiel.android.game.interfaces.ITrajectory;
import se.federspiel.android.game.interfaces.ITrajectory.AccelerationModeEnum;
import se.federspiel.android.game.interfaces.ITrajectory.IntegrationMethod;
import se.federspiel.android.game.interfaces.IUserInputManager;
import se.federspiel.android.game.interfaces.IUserInputManager.IOnClickListener;
import se.federspiel.android.game.interfaces.IUserInputManager.TouchClickEvent;
import se.federspiel.android.game.sprites.AbstractSprite;
import se.federspiel.android.game.sprites.GraphicSprite;
import se.federspiel.android.game.sprites.NonVisualSprite;
import se.federspiel.android.game.sprites.actions.SpriteElasticBounceAction;
import se.federspiel.android.game.sprites.actions.SpriteNonAdjustAction;
import se.federspiel.android.game.sprites.actions.SpriteStickAction;
import se.federspiel.android.game.sprites.actions.SpriteStickAction.StickDirection;
import se.federspiel.android.game.sprites.actions.SpriteTerminateAction;
import se.federspiel.android.game.sprites.actions.SpriteTerminateAction.ITerminateSpriteListener;
import se.federspiel.android.game.sprites.drawers.BallDrawer;
import se.federspiel.android.game.sprites.drawers.BoxDrawer;
import se.federspiel.android.game.sprites.drawers.LineDrawer;
import se.federspiel.android.game.trajectories.AbstractPathTrajectory.Direction;
import se.federspiel.android.game.trajectories.BezierPathTrajectory;
import se.federspiel.android.game.trajectories.CompositeTrajectory;
import se.federspiel.android.game.trajectories.GravityTrajectory;
import se.federspiel.android.game.trajectories.KeyControlledTrajectory;
import se.federspiel.android.game.trajectories.LinearPathTrajectory;
import se.federspiel.android.game.trajectories.MovementTrajectory;
import se.federspiel.android.game.trajectories.OrientationControlledTrajectory;
import se.federspiel.android.game.trajectories.OrientationControlledTrajectory.OrientationMode;
import se.federspiel.android.game.trajectories.TouchDragTrajectory;
import se.federspiel.android.game.trajectories.TouchSlideTrajectory;
import se.federspiel.android.game.trajectories.limits.BoundsLimits;
import se.federspiel.android.game.trajectories.limits.GameViewLimits;
import se.federspiel.android.game.trajectories.limits.GraphicsViewLimits;
import se.federspiel.android.game.trajectories.limits.OutOfBoundsEvent;
import se.federspiel.android.game.ui.UILabel;
import se.federspiel.android.game.ui.UITouchArea;
import se.federspiel.android.game.ui.UITouchArea.ITouchAreaListener;
import se.federspiel.android.game.ui.UITouchArea.TouchAreaEvent;
import se.federspiel.android.game.ui.UITouchButton;
import se.federspiel.android.game.ui.touch.UITouchAreaDragAlgorithm;
import se.federspiel.android.game.ui.touch.UITouchAreaDragAlgorithm.TouchDirection;
import se.federspiel.android.game.utils.AMath;
import se.federspiel.android.util.ImageTools.IColorMatcher;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;

import com.example.explorerapp.R;

public class CollisionTestView extends CanvasSurfaceView
{
	private static final int START_UNIT_TEST_INDEX = 0;
	
	private GameApplication mGameApplication = null;
	private GameEngine mGameEngine = null;
	private IGameContext mGameContext = null;

    private ScreenTouch mScreenTouch = null;

	private UILabel mtestNameLabel = null;

	private TerminationListener mTerminationListener = new TerminationListener();
	
	private ArrayList<IUnitTest> mUnitTests = new ArrayList<IUnitTest>();
	private ArrayList<ISprite> mActiveDrawSprites = new ArrayList<ISprite>();
	private ArrayList<ISprite> mActiveUpdateSprites = new ArrayList<ISprite>();
	private ArrayList<IBackground> mActiveBackgrounds = new ArrayList<IBackground>();

	private IUnitTest mCurrentRunningTest = null;
		
	public CollisionTestView(Context context)
	{
		super(context);
	}

	public CollisionTestView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}
	
	public CollisionTestView(Context context, AttributeSet attrs, int defStyle)
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
	
    private void createLotsOfBalls(int nofBalls, int radius, int speed, GameEngine gameEngine, IGameContext gameContext)
    {
        Random rand = new Random();

        for (int i = 0; i < nofBalls; i++)
        {
            int positionX = rand.nextInt(getWidth());
            int positionY = rand.nextInt(getHeight());    
            
            ISprite sprite = gameContext.getSpriteFactory().createSprite("Graphic");
            
            BallDrawer drawer = new BallDrawer(gameContext);
            drawer.setRadius(radius);
            ((GraphicSprite)sprite).setGraphicDrawer(drawer);

            ITrajectory trajectory = gameContext.getTrajectoryFactory().createTrajectory("MovementTrajectory", sprite);

            trajectory.setInitialSpeed(new Vector2(speed, speed));
            trajectory.setPositionLimits(new GraphicsViewLimits(gameContext));
   
            SpriteElasticBounceAction spriteAction = new SpriteElasticBounceAction();
            ((ICollisionSprite) sprite).setCollisionAction(spriteAction);
            
            sprite.setInitialPosition(new Point(positionX, positionY));
            sprite.getPhysicalProperties().setMass(1);
            
            gameContext.getCollisionManager().add(sprite.getCollisionObject());

            gameEngine.addComponent(sprite);
            mActiveDrawSprites.add(sprite);
        }
    }

    private void createNonMovingBall(int radius, int x, int y, GameEngine gameEngine, IGameContext gameContext)
    {
        ISprite sprite = gameContext.getSpriteFactory().createSprite("Graphic");
   
        BallDrawer drawer = new BallDrawer(gameContext);
        drawer.setRadius(radius);
        ((GraphicSprite)sprite).setGraphicDrawer(drawer);

        sprite.setInitialPosition(new Point(x, y));
            
        gameContext.getCollisionManager().add(sprite.getCollisionObject());

        gameEngine.addComponent(sprite);
        mActiveDrawSprites.add(sprite);
    }

    private void createNonMovingLine(int x1, int y1, int x2, int y2, GameEngine gameEngine, IGameContext gameContext)
    {
        ISprite sprite = gameContext.getSpriteFactory().createSprite("Graphic");
   
        LineDrawer drawer = new LineDrawer(gameContext);
        drawer.setLine(x1, y1, x2, y2);
        ((GraphicSprite)sprite).setGraphicDrawer(drawer);

        sprite.setInitialPosition(new Point(x1, y1));
            
        gameContext.getCollisionManager().add(sprite.getCollisionObject());

        gameEngine.addComponent(sprite);
        mActiveDrawSprites.add(sprite);
    }

    private void createMovingLine(int x1, int y1, int x2, int y2, 
    		int xMin, int xMax, int yMin, int yMax, int xSpeed, int ySpeed, GameEngine gameEngine, IGameContext gameContext)
    {
        ISprite sprite = gameContext.getSpriteFactory().createSprite("Graphic");
        
        LineDrawer drawer = new LineDrawer(mGameContext);
        drawer.setLine(x1, y1, x2, y2);
        ((GraphicSprite)sprite).setGraphicDrawer(drawer);
        
        ITrajectory trajectory = gameContext.getTrajectoryFactory().createTrajectory("MovementTrajectory", sprite);

        trajectory.setInitialSpeed(new Vector2(xSpeed, ySpeed));
        trajectory.setPositionLimits(new BoundsLimits(xMin, xMax, yMin, yMax));
 
        SpriteNonAdjustAction spriteAction = new SpriteNonAdjustAction();
        ((ICollisionSprite) sprite).setCollisionAction(spriteAction);
        
        sprite.setInitialPosition(new Point(x1, y1));
            
        gameContext.getCollisionManager().add(sprite.getCollisionObject());

        gameEngine.addComponent(sprite);
        mActiveDrawSprites.add(sprite);
    }

    private void createMovingBall(int radius, int x, int y, int speedX, int speedY,
    		GameEngine gameEngine, IGameContext gameContext)
    {
    	createMovingBall(radius, x, y, speedX, speedY, 1, 1, gameEngine, gameContext);
    }
    
    private void createMovingBall(int radius, int x, int y, int speedX, int speedY, 
    		float mass, float restitution, GameEngine gameEngine, IGameContext gameContext)
    {
        ISprite sprite = gameContext.getSpriteFactory().createSprite("Graphic");
   
        BallDrawer drawer = new BallDrawer(gameContext);
        drawer.setRadius(radius);
        ((GraphicSprite)sprite).setGraphicDrawer(drawer);

        ITrajectory trajectory = gameContext.getTrajectoryFactory().createTrajectory("MovementTrajectory", sprite);

        trajectory.setInitialSpeed(new Vector2(speedX, speedY));
        trajectory.setPositionLimits(new GraphicsViewLimits(gameContext));
        
        SpriteElasticBounceAction spriteAction = new SpriteElasticBounceAction();
        ((ICollisionSprite) sprite).setCollisionAction(spriteAction);
        
        sprite.setInitialPosition(new Point(x, y));
        sprite.getPhysicalProperties().setMass(mass);
        sprite.getPhysicalProperties().setRestitution(restitution);
        
        gameContext.getCollisionManager().add(sprite.getCollisionObject());

        gameEngine.addComponent(sprite);
        mActiveDrawSprites.add(sprite);
    }
    private void createMovingRectBounce(int width, int height, int x, int y, int speedX, int speedY, 
    		GameEngine gameEngine, IGameContext gameContext)
    {
    	createMovingRectBounce(width, height, x, y, speedX, speedY, 1, 1, gameEngine, gameContext);
    }

    private void createMovingRectBounce(int width, int height, int x, int y, int speedX, int speedY, 
    		float mass, float restitution, GameEngine gameEngine, IGameContext gameContext)
    {
        ISprite sprite = gameContext.getSpriteFactory().createSprite("Graphic");
   
        BoxDrawer drawer = new BoxDrawer(mGameContext);
        drawer.setDimensions(width, height);
        ((GraphicSprite)sprite).setGraphicDrawer(drawer);

        ITrajectory trajectory = gameContext.getTrajectoryFactory().createTrajectory("MovementTrajectory", sprite);

        trajectory.setInitialSpeed(new Vector2(speedX, speedY));
        trajectory.setPositionLimits(new GraphicsViewLimits(gameContext));
        
        SpriteElasticBounceAction spriteAction = new SpriteElasticBounceAction();
        ((ICollisionSprite) sprite).setCollisionAction(spriteAction);
        
        sprite.setInitialPosition(new Point(x, y));
        sprite.getPhysicalProperties().setMass(mass);
        sprite.getPhysicalProperties().setRestitution(restitution);
            
        gameContext.getCollisionManager().add(sprite.getCollisionObject());

        gameEngine.addComponent(sprite);
        mActiveDrawSprites.add(sprite);
    }

    private void createNonMovingRect(int width, int height, int x, int y, GameEngine gameEngine, IGameContext gameContext)
    {
        ISprite sprite = gameContext.getSpriteFactory().createSprite("Graphic");
   
        BoxDrawer drawer = new BoxDrawer(mGameContext);
        drawer.setDimensions(width, height);
        ((GraphicSprite)sprite).setGraphicDrawer(drawer);

        sprite.setInitialPosition(new Point(x, y));
            
        gameContext.getCollisionManager().add(sprite.getCollisionObject());

        gameEngine.addComponent(sprite);
        mActiveDrawSprites.add(sprite);
     }

    private void createNonMovingRectTerminate(int width, int height, int x, int y, GameEngine gameEngine, IGameContext gameContext)
    {
        ISprite sprite = gameContext.getSpriteFactory().createSprite("Graphic");
   
        BoxDrawer drawer = new BoxDrawer(mGameContext);
        drawer.setDimensions(width, height);
        ((GraphicSprite)sprite).setGraphicDrawer(drawer);

        SpriteTerminateAction terminateAction = new SpriteTerminateAction(gameContext);
        terminateAction.setTerminationListener(mTerminationListener);
        
        ((ICollisionSprite) sprite).setCollisionAction(terminateAction);
        
        sprite.setInitialPosition(new Point(x, y));
            
        gameContext.getCollisionManager().add(sprite.getCollisionObject());

        gameEngine.addComponent(sprite);
        mActiveDrawSprites.add(sprite);
     }

    private void createNonAdjustingMovingRect(int width, int height, int x, int y, 
    		int xMin, int xMax, int yMin, int yMax, int speedX, int speedY, GameEngine gameEngine, IGameContext gameContext)
    {
        ISprite sprite = gameContext.getSpriteFactory().createSprite("Graphic");
        
        BoxDrawer drawer = new BoxDrawer(mGameContext);
        drawer.setDimensions(width, height);
        ((GraphicSprite)sprite).setGraphicDrawer(drawer);

        ITrajectory trajectory = gameContext.getTrajectoryFactory().createTrajectory("MovementTrajectory", sprite);

        trajectory.setInitialSpeed(new Vector2(speedX, speedY));
        trajectory.setPositionLimits(new BoundsLimits(xMin, xMax, yMin, yMax));
        
        SpriteNonAdjustAction spriteAction = new SpriteNonAdjustAction();
        ((ICollisionSprite) sprite).setCollisionAction(spriteAction);

        sprite.setInitialPosition(new Point(x, y));
            
        gameContext.getCollisionManager().add(sprite.getCollisionObject());

        gameEngine.addComponent(sprite);
        mActiveDrawSprites.add(sprite);
     }
    
    private GraphicSprite createBall(int radius, GameEngine gameEngine, IGameContext gameContext)
    {
    	GraphicSprite sprite = (GraphicSprite) gameContext.getSpriteFactory().createSprite("Graphic");
   
        BallDrawer drawer = new BallDrawer(gameContext);
        drawer.setRadius(radius);
        sprite.setGraphicDrawer(drawer);

        return sprite;
    }
        
    private void activateDrawSprite(ISprite sprite, GameEngine gameEngine, IGameContext gameContext)
    {
        gameContext.getCollisionManager().add(sprite.getCollisionObject());
        gameEngine.addComponent(sprite);
        
        mActiveDrawSprites.add(sprite);
    }
    
    private void activateUpdateSprite(ISprite sprite, GameEngine gameEngine, IGameContext gameContext)
    {
        gameContext.getCollisionManager().add(sprite.getCollisionObject());
        gameEngine.addUpdateComponent(sprite);
        
        mActiveUpdateSprites.add(sprite);
    }

    private void initGame()
	{
    	mGameApplication = new GameApplication(this);

		mGameEngine = GameApplication.getGameEngine();
		mGameContext = mGameEngine.getGameContext();

        mGameContext.getSpriteFactory().registerSprite("Graphic", GraphicSprite.class);

        mGameContext.getTrajectoryFactory().registerTrajectory("MovementTrajectory", MovementTrajectory.class);
        mGameContext.getTrajectoryFactory().registerTrajectory("KeyControlledTrajectory", KeyControlledTrajectory.class);
        mGameContext.getTrajectoryFactory().registerTrajectory("OrientationControlledTrajectory", OrientationControlledTrajectory.class);
        mGameContext.getTrajectoryFactory().registerTrajectory("TouchSlideTrajectory", TouchSlideTrajectory.class);
        
        mGameContext.getBackgroundFactory().registerBackground("Color", ColorBackground.class);
        mGameContext.getBackgroundFactory().registerBackground("Image", ImageBackground.class);
		
		mtestNameLabel = new UILabel(15, 15, 10, 10);
		
		mGameContext.getMainWindow().addComponent(mtestNameLabel);

		mGameContext.getImageManager().setRecycleUnallocated(true);
		
	    mScreenTouch = new ScreenTouch(this, mGameEngine, mGameContext);
		
        setupTests();
    
        switchTest(START_UNIT_TEST_INDEX);
        
		mGameContext.getSoundManager().addSound(com.example.explorerapp.R.raw.boing_rebound_01);
	}
    
    private void testBackground()
    {
    	IBackground background = mGameContext.getBackgroundFactory().createBackground("Color");
        ((ColorBackground) background).setColor(Color.BLUE);
        mGameEngine.addBackground(background);
        
        mActiveBackgrounds.add(background);
    }
    
    private void setupTests()
    {
    	mUnitTests.add(new AbstractUnitTest()
    	{
			@Override
			public String title()
			{
				return "Fixed vs moving";
			}

			@Override
			public void run()
			{
				createLotsOfBalls(1, 30, 200, mGameEngine, mGameContext);
				createNonMovingBall(70, 360, 640, mGameEngine, mGameContext);
				testBackground();
			}
    	});
    	
    	mUnitTests.add(new AbstractUnitTest()
    	{
			@Override
			public String title()
			{
				return "Massive vs massive, moving";
			}

			@Override
			public void run()
			{
			    createMovingBall(30, 360, 200, 0, 100, 0, 1, mGameEngine, mGameContext); 
			    createMovingBall(30, 360, 800, 0, -100, 0, 1, mGameEngine, mGameContext); 

				testBackground();
			}
    	});
    	
    	mUnitTests.add(new AbstractUnitTest()
    	{
			@Override
			public String title()
			{
				return "Straight on";
			}

			@Override
			public void run()
			{
				createMovingBall(30, 360, 400, 0, 220, mGameEngine, mGameContext);
				createMovingBall(30, 360, 800, 0, -220, mGameEngine, mGameContext);
				testBackground();
			}
    	});
    	
    	mUnitTests.add(new AbstractUnitTest()
    	{
			@Override
			public String title()
			{
				return "Straight on side";
			}

			@Override
			public void run()
			{
				createMovingBall(30, 360, 400, 0, 220, mGameEngine, mGameContext);
				createMovingBall(30, 410, 800, 0, -220, mGameEngine, mGameContext);
				testBackground();
			}
    	});

    	mUnitTests.add(new AbstractUnitTest()
    	{
			@Override
			public String title()
			{
				return "Catch up on side";
			}

			@Override
			public void run()
			{
		    	createMovingBall(30, 360, 650, 0, -200, mGameEngine, mGameContext);
		    	createMovingBall(30, 340, 800, 0, -250, mGameEngine, mGameContext);
				testBackground();
			}
    	});

    
    	mUnitTests.add(new AbstractUnitTest()
    	{
			@Override
			public String title()
			{
				return "Catch up straight on";
			}

			@Override
			public void run()
			{
		    	createMovingBall(30, 360, 650, 0, -200, mGameEngine, mGameContext);
		    	createMovingBall(30, 360, 800, 0, -250, mGameEngine, mGameContext);
				testBackground();
			}
    	});
    	
    	mUnitTests.add(new AbstractUnitTest()
    	{
			@Override
			public String title()
			{
				return "Stationary on side";
			}

			@Override
			public void run()
			{
		    	createMovingBall(30, 360, 650, 0, 0, mGameEngine, mGameContext);
		    	createMovingBall(30, 320, 800, 0, -250, mGameEngine, mGameContext);
				testBackground();
			}
    	});

    	// moving ball catch up stationary ball straight on
    	
    	mUnitTests.add(new AbstractUnitTest()
    	{
			@Override
			public String title()
			{
		    	return "Stationary straight on";
			}

			@Override
			public void run()
			{
		    	createMovingBall(30, 360, 650, 0, 0, mGameEngine, mGameContext);
		    	createMovingBall(30, 360, 800, 0, -250, mGameEngine, mGameContext);
				testBackground();
			}
    	});
    	
    	// moving balls catch up stright on, downwards
    	
    	mUnitTests.add(new AbstractUnitTest()
    	{
			@Override
			public String title()
			{
		    	return "Catch up straight on downwords";
			}

			@Override
			public void run()
			{
		    	createMovingBall(30, 360, 450, 0, 250, mGameEngine, mGameContext);
		    	createMovingBall(30, 360, 600, 0, 200, mGameEngine, mGameContext);
				testBackground();
			}
    	});
    	
    	// Lot of moving balls
    	mUnitTests.add(new AbstractUnitTest()
    	{
			@Override
			public String title()
			{
		    	return "Lot of balls";
			}

			@Override
			public void run()
			{
		    	createMovingBall(30, 160, 500, 200, 200, mGameEngine, mGameContext);
		    	createLotsOfBalls(10, 30, 200, mGameEngine, mGameContext);
				testBackground();
			}
    	});
    	
    	// ball none moving line
    	mUnitTests.add(new AbstractUnitTest()
    	{
			@Override
			public String title()
			{
		    	return "Fixed line";
			}

			@Override
			public void run()
			{
				createMovingBall(30, 100, 100, 200, 200, mGameEngine, mGameContext);
				createNonMovingLine(210, 320, 510, 380, mGameEngine, mGameContext);
				testBackground();
			}
    	});

    	// Up - down
    	mUnitTests.add(new AbstractUnitTest()
    	{
			@Override
			public String title()
			{
		    	return "Line up and down";
			}

			@Override
			public void run()
			{
		    	createMovingBall(30, 460, 420, 0, 200, 1, 0, mGameEngine, mGameContext);
		    	createMovingLine(360, 720, 460, 720, 50, 510, 500, 900, 0, 250, mGameEngine, mGameContext);
				testBackground();
			}
    	});
    
    	// Left - right
    	mUnitTests.add(new AbstractUnitTest()
    	{
			@Override
			public String title()
			{
		    	return "Line right and left";
			}

			@Override
			public void run()
			{
		    	createMovingBall(30, 360, 700, 200, 0, 1, 0, mGameEngine, mGameContext);
		    	createMovingLine(260, 600, 260, 800, 100, 420, 200, 900, 250, 0, mGameEngine, mGameContext);
				testBackground();
			}
    	});

    	// Up down slanted
    	mUnitTests.add(new AbstractUnitTest()
    	{
			@Override
			public String title()
			{
				return "Slanted line up and down";
			}

			@Override
			public void run()
			{
				createMovingBall(30, 400, 500, 200, 0, 1, 0, mGameEngine, mGameContext);
				createMovingLine(200, 600, 600, 800, 50, 750, 400, 1000, 0, 250, mGameEngine, mGameContext);
				testBackground();
			}
    	});

		// Up down right left slanted
    	mUnitTests.add(new AbstractUnitTest()
    	{
			@Override
			public String title()
			{
				return "Slanted line all directions";
			}

			@Override
			public void run()
			{
				createMovingBall(30, 200, 400, 200, 0, 1, 0, mGameEngine, mGameContext);
				createMovingLine(250, 500, 600, 700, 200, 650, 200, 900, 250, 250, mGameEngine, mGameContext);
				testBackground();
			}
    	});

		// Horz Line end straight on
    	mUnitTests.add(new AbstractUnitTest()
    	{
			@Override
			public String title()
			{
				return "Horz line end straight on";
			}

			@Override
			public void run()
			{
				createMovingBall(30, 720, 720, -200, 0, mGameEngine, mGameContext);
				createNonMovingLine(250, 720, 400, 720, mGameEngine, mGameContext);
				testBackground();
			}
    	});

		// Horz line end on side straight on
    	mUnitTests.add(new AbstractUnitTest()
    	{
			@Override
			public String title()
			{
				return "Horz line end on side straight on";
			}

			@Override
			public void run()
			{
				createMovingBall(30, 710, 710, -200, 0, mGameEngine, mGameContext);
				createNonMovingLine(250, 720, 400, 720, mGameEngine, mGameContext);
				testBackground();
			}
    	});

		// Horz line end from side
    	mUnitTests.add(new AbstractUnitTest()
    	{
			@Override
			public String title()
			{
				return "Horz line end from side";
			}

			@Override
			public void run()
			{
				createMovingBall(30, 410, 200, 0, 200, mGameEngine, mGameContext);
				createNonMovingLine(250, 720, 400, 720, mGameEngine, mGameContext);
				testBackground();
			}
    	});

		// Vert Line end straight on
    	mUnitTests.add(new AbstractUnitTest()
    	{
			@Override
			public String title()
			{
				return "Vert line end straight on";
			}

			@Override
			public void run()
			{
				createMovingBall(30, 360, 420, 0, 200, mGameEngine, mGameContext);
				createNonMovingLine(360, 620, 360, 820, mGameEngine, mGameContext);
				testBackground();
			}
    	});

		// Vert line end on side straight on
    	mUnitTests.add(new AbstractUnitTest()
    	{
			@Override
			public String title()
			{
				return "Vert line end on side straight on";
			}

			@Override
			public void run()
			{
				createMovingBall(30, 380, 420, 0, 200, mGameEngine, mGameContext);
				createNonMovingLine(360, 620, 360, 820, mGameEngine, mGameContext);
				testBackground();
			}
    	});

    	// vert line end from side
    	mUnitTests.add(new AbstractUnitTest()
    	{
			@Override
			public String title()
			{
				return "Vert line end from side";
			}

			@Override
			public void run()
			{
				createMovingBall(30, 200, 600, 200, 0, mGameEngine, mGameContext);
				createNonMovingLine(360, 620, 360, 820, mGameEngine, mGameContext);
				testBackground();
			}
    	});
    
    	// Rect vert down fixed 
    	mUnitTests.add(new AbstractUnitTest()
    	{
			@Override
			public String title()
			{
				return "Rect vert down fixed";
			}

			@Override
			public void run()
			{
				createMovingRectBounce(30, 30, 200, 600, 0, 200, mGameEngine, mGameContext);
				createNonMovingRect(30, 30, 200, 720, mGameEngine, mGameContext);
				testBackground();
			}
    	});

		// Rect horizontal fixed 
    	mUnitTests.add(new AbstractUnitTest()
    	{
			@Override
			public String title()
			{
				return "Rect horizontal fixed";
			}

			@Override
			public void run()
			{
				createMovingRectBounce(30, 30, 500, 600, 200, 0, mGameEngine, mGameContext);
				createNonMovingRect(30, 30, 200, 620, mGameEngine, mGameContext);
				testBackground();
			}
    	});

		// Rect vertical straight on 
    	mUnitTests.add(new AbstractUnitTest()
    	{
			@Override
			public String title()
			{
				return "Rect vertical straight on";
			}

			@Override
			public void run()
			{
				createMovingRectBounce(30, 30, 200, 700, 0, -200, mGameEngine, mGameContext);
				createMovingRectBounce(30, 30, 200, 500, 0, 200, mGameEngine, mGameContext);
				testBackground();
			}
    	});

		// Rect horizontal straight on 
    	mUnitTests.add(new AbstractUnitTest()
    	{
			@Override
			public String title()
			{
				return "Rect horizontal straight on";
			}

			@Override
			public void run()
			{
				createMovingRectBounce(30, 30, 600, 520, -200, 0, mGameEngine, mGameContext);
				createMovingRectBounce(30, 30, 200, 500, 200, 0, mGameEngine, mGameContext);
				testBackground();
			}
    	});

		// Rect vertical non flexible 
    	mUnitTests.add(new AbstractUnitTest()
    	{
			@Override
			public String title()
			{
				return "Rect vertical non flexible";
			}

			@Override
			public void run()
			{
				createMovingRectBounce(30, 30, 200, 300, 0, 200, 1, 0, mGameEngine, mGameContext);
				createNonAdjustingMovingRect(30, 30, 200, 700, 
						100, 300, 200, 1100, 0, -200, mGameEngine, mGameContext);
				testBackground();
			}
    	});

		// Rect all dir non flexible
    	mUnitTests.add(new AbstractUnitTest()
    	{
			@Override
			public String title()
			{
				return "Rect all dir non flexible";
			}

			@Override
			public void run()
			{
				createMovingRectBounce(30, 30, 200, 300, 200, 200, 1, 0, mGameEngine, mGameContext);
				createNonAdjustingMovingRect(300, 40, 200, 700, 
						150, 550, 300, 900, -100, -200, mGameEngine, mGameContext);
				testBackground();
			}
    	});

		// Cirlce - Rect vertical fixed 
    	mUnitTests.add(new AbstractUnitTest()
    	{
			@Override
			public String title()
			{
				return "Circle - Rect vertical fixed";
			}

			@Override
			public void run()
			{
				createMovingBall(30, 220, 320, 0, 200, mGameEngine, mGameContext);
				createNonMovingRect(40, 40, 200, 720, mGameEngine, mGameContext);
				testBackground();
			}
    	});

		// Cirlce - Rect horizontal fixed 
    	mUnitTests.add(new AbstractUnitTest()
    	{
			@Override
			public String title()
			{
				return "Circle - Rect horizontal fixed";
			}

			@Override
			public void run()
			{
				createMovingBall(30, 400, 710, -200, 0, mGameEngine, mGameContext);
				createNonMovingRect(40, 40, 200, 720, mGameEngine, mGameContext);
				testBackground();
			}
    	});

		// Cirlce - Rect diagonally fixed 
    	mUnitTests.add(new AbstractUnitTest()
    	{
			@Override
			public String title()
			{
				return "Circle - Rect diagonally fixed";
			}

			@Override
			public void run()
			{
				createMovingBall(60, 440, 520, -120, 120, mGameEngine, mGameContext);
				createNonMovingRect(40, 40, 200, 720, mGameEngine, mGameContext);
				testBackground();
			}
    	});

    	// Cirlce - Rect non flexible 
    	mUnitTests.add(new AbstractUnitTest()
    	{
			@Override
			public String title()
			{
				return "Circle - Rect non flexible";
			}

			@Override
			public void run()
			{
				createMovingBall(30, 210, 320, 0, 200, 1, 0, mGameEngine, mGameContext);
				createNonAdjustingMovingRect(30, 30, 200, 720, 
						100, 300, 200, 900, 0, -200, mGameEngine, mGameContext);
				testBackground();
			}
    	});

		// Cirlce - Rect all dir non flexible 
    	mUnitTests.add(new AbstractUnitTest()
    	{
			@Override
			public String title()
			{
				return "Circle - Rect all dir non flexible";
			}

			@Override
			public void run()
			{
				createMovingBall(30, 610, 120, 200, 200, 1, 0, mGameEngine, mGameContext);
				createNonAdjustingMovingRect(300, 20, 200, 720, 
						100, 600, 200, 900, -100, -200, mGameEngine, mGameContext);
				testBackground();
			}
    	});

		// Touch 
    	mUnitTests.add(new AbstractUnitTest()
    	{
			@Override
			public String title()
			{
				return "Touch";
			}

			@Override
			public void run()
			{
	            ISprite sprite = mGameContext.getSpriteFactory().createSprite("Graphic");
	            
	            BoxDrawer drawer = new BoxDrawer(mGameContext);
	            drawer.setDimensions(100, 100);
	            ((GraphicSprite)sprite).setGraphicDrawer(drawer);

		        ITrajectory trajectory = mGameContext.getTrajectoryFactory().createTrajectory("TouchSlideTrajectory", sprite);
		        ((TouchSlideTrajectory) trajectory).setSlideRelease(true);
		        trajectory.setPositionLimits(new GraphicsViewLimits(mGameContext));
		        
		        SpriteStickAction action = new SpriteStickAction();
		        action.setStickDirection(StickDirection.NORMAL_DIRECTION);
		        ((ICollisionSprite) sprite).setCollisionAction(action);
		        
		        sprite.setInitialPosition(new Point(350, 100));

		        mGameContext.getCollisionManager().add(sprite.getCollisionObject());
		        
				mGameEngine.addComponent(sprite);
		        mActiveDrawSprites.add(sprite);

	            sprite = mGameContext.getSpriteFactory().createSprite("Graphic");
	            
	            drawer = new BoxDrawer(mGameContext);
	            drawer.setDimensions(100, 100);
	            ((GraphicSprite)sprite).setGraphicDrawer(drawer);

		        trajectory = mGameContext.getTrajectoryFactory().createTrajectory("TouchSlideTrajectory", sprite);
		        ((TouchSlideTrajectory) trajectory).setSlideRelease(true);
		        trajectory.setPositionLimits(new GraphicsViewLimits(mGameContext));
		        
		        action = new SpriteStickAction();
		        action.setStickDirection(StickDirection.NORMAL_DIRECTION);
		        ((ICollisionSprite) sprite).setCollisionAction(action);
		        
		        sprite.setInitialPosition(new Point(350, 900));

		        mGameContext.getCollisionManager().add(sprite.getCollisionObject());
		        
				mGameEngine.addComponent(sprite);
		        mActiveDrawSprites.add(sprite);
		        
				testBackground();
			}
    	});
    	
		// Brick collisions 
    	mUnitTests.add(new AbstractUnitTest()
    	{
			@Override
			public String title()
			{
				return "Brick Ball Collisions";
			}

			@Override
			public void run()
			{
				int height = mGameContext.getGraphicBounds().getHeight();
				int width = mGameContext.getGraphicBounds().getWidth();

				for (int x = 20; x < width; x += 70)
				{
					for (int y = 70; y < height; y += 45)
					{
						if ( (x < (width / 2 - 70)) || (x > (width / 2 + 70)) 
								|| (y < (height / 2 - 70)) || (y > (height / 2 + 70)))
						{
						    createNonMovingRectTerminate(50, 25, x, y, mGameEngine, mGameContext);
						}
					}
				}
				
				createMovingBall(15, width / 2, height / 2, 200, 200, mGameEngine, mGameContext);
				
				testBackground();
			}
    	});
    	
		// Brick collisions bounce
    	mUnitTests.add(new AbstractUnitTest()
    	{
			@Override
			public String title()
			{
				return "Brick Ball Collisions Bounce";
			}

			@Override
			public void run()
			{
				int height = mGameContext.getGraphicBounds().getHeight();
				int width = mGameContext.getGraphicBounds().getWidth();
				
				for (int x = 20; x < width; x += 140)
				{
					for (int y = 70; y < height; y += 90)
					{
						if ( (x < (width / 2 - 70)) || (x > (width / 2 + 70)) 
								|| (y < (height / 2 - 70)) || (y > (height / 2 + 70)))
						{
						    createNonMovingRect(50, 25, x, y, mGameEngine, mGameContext);
						}
					}
				}
				
				createMovingBall(15, width / 2, height / 2, 200, 200, mGameEngine, mGameContext);
				
				testBackground();
			}
    	});
    	
		// Brick collisions bounce
    	mUnitTests.add(new AbstractUnitTest()
    	{
			@Override
			public String title()
			{
				return "Brick Brick Collisions Bounce";
			}

			@Override
			public void run()
			{
				int height = mGameContext.getGraphicBounds().getHeight();
				int width = mGameContext.getGraphicBounds().getWidth();

				for (int x = 20; x < width; x += 140)
				{
					for (int y = 70; y < height; y += 90)
					{
						if ( (x < (width / 2 - 70)) || (x > (width / 2 + 70)) 
								|| (y < (height / 2 - 70)) || (y > (height / 2 + 70)))
						{
						    createNonMovingRect(50, 25, x, y, mGameEngine, mGameContext);
						}
					}
				}
				
				createMovingRectBounce(30, 30, width / 2, height / 2, 200, 200, mGameEngine, mGameContext);
				
				testBackground();
			}
    	});
    	
		// Linear & bezier path
    	mUnitTests.add(new AbstractUnitTest()
    	{
			@Override
			public String title()
			{
				return "Linear & Bezier path";
			}

			@Override
			public void run()
			{
				DrawBackground background = new DrawBackground(mGameContext);
		        background.setZOrder(DrawableZOrder.BACKGROUND_LAYER_2);
		        background.setColor(Color.BLUE);
		        
				GraphicSprite sprite = createBall(10, mGameEngine, mGameContext);

				LinearPathTrajectory trajectory = new LinearPathTrajectory(mGameContext, sprite);
				
				Point[] path = new Point[]	
				{
					new Point(100, 100),
					new Point(600, 100),
					new Point(600, 1000),
					new Point(100, 1000)
				};
				
				trajectory.definePath(Direction.FORWARD, 4, path);
		        trajectory.setInitialSpeed(new Vector2(300, 0));
				
		        se.federspiel.android.backgrounds.drawers.LineDrawer lineDrawer = new se.federspiel.android.backgrounds.drawers.LineDrawer(path);
				lineDrawer.setLineColor(Color.WHITE);
	    		background.addDrawer(lineDrawer);
	    		
				sprite.setTrajectory(trajectory);
				
		    	activateDrawSprite(sprite, mGameEngine, mGameContext);

 		    	GraphicSprite sprite2 = createBall(10, mGameEngine, mGameContext);

		    	LinearPathTrajectory trajectory2 = new LinearPathTrajectory(mGameContext, sprite2);
				
				Point[] path2 = new Point[]	
				{
					new Point(200, 200),
					new Point(500, 500)
				};
				
				trajectory2.definePath(Direction.BACK_AND_FORTH, 4, path2);
		        trajectory2.setInitialSpeed(new Vector2(350, 0));
				
				lineDrawer = new se.federspiel.android.backgrounds.drawers.LineDrawer(path2);
	    		lineDrawer.setLineColor(Color.WHITE);
	    		background.addDrawer(lineDrawer);
	    		
				sprite2.setTrajectory(trajectory2);
				
		    	activateDrawSprite(sprite2, mGameEngine, mGameContext);
		    	
		    	GraphicSprite sprite3 = createBall(10, mGameEngine, mGameContext);

		    	LinearPathTrajectory trajectory3 = new LinearPathTrajectory(mGameContext, sprite3);
				
				Point[] path3 = new Point[]	
				{
					new Point(150, 300),
					new Point(650, 800)
				};
				
				trajectory3.definePath(Direction.BACKWARDS, 4, path3);
		        trajectory3.setInitialSpeed(new Vector2(250, 0));
				
		        lineDrawer = new se.federspiel.android.backgrounds.drawers.LineDrawer(path3);
		        lineDrawer.setLineColor(Color.WHITE);
	    		background.addDrawer(lineDrawer);
	    		
				sprite3.setTrajectory(trajectory3);
				
		    	activateDrawSprite(sprite3, mGameEngine, mGameContext);
		    	
		    	GraphicSprite sprite4 = createBall(10, mGameEngine, mGameContext);

				BezierPathTrajectory trajectory4 = new BezierPathTrajectory(mGameContext, sprite4);
				
				Point[] path4 = new Point[]	
				{
					new Point(350, 500),
					new Point(150, 50),
					new Point(650, 50),
					new Point(450, 500),
					new Point(150, 950),
					new Point(650, 950),
					new Point(350, 500)
				};
				
				trajectory4.definePath(Direction.FORWARD, 4, path4);
		        trajectory4.setInitialSpeed(new Vector2(250, 0));
				
		        BezierDrawer bezierDrawer = new BezierDrawer(path4);
	    		bezierDrawer.setLineColor(Color.WHITE);
	    		background.addDrawer(bezierDrawer);
	    		
				sprite4.setTrajectory(trajectory4);
				
		    	activateDrawSprite(sprite4, mGameEngine, mGameContext);

		    	GraphicSprite sprite5 = createBall(10, mGameEngine, mGameContext);

				BezierPathTrajectory trajectory5 = new BezierPathTrajectory(mGameContext, sprite5);
				
				Point[] path5 = new Point[]	
				{
					new Point(150, 300),
					new Point(500, 300),
					new Point(200, 800),
					new Point(650, 800)
				};
				
				trajectory5.definePath(Direction.BACK_AND_FORTH, 4, path5);
		        trajectory5.setInitialSpeed(new Vector2(250, 0));
				
				bezierDrawer = new BezierDrawer(path5);
	    		bezierDrawer.setLineColor(Color.WHITE);
	    		background.addDrawer(bezierDrawer);
	    		
				sprite5.setTrajectory(trajectory5);
				
		    	activateDrawSprite(sprite5, mGameEngine, mGameContext);
		    	
				mGameEngine.addBackground(background);
				
		        mActiveBackgrounds.add(background);
			}
    	});
    	
		// Bezier
    	mUnitTests.add(new AbstractUnitTest()
    	{
			@Override
			public String title()
			{
				return "Bezier path";
			}

			@Override
			public void run()
			{
				int height = mGameContext.getGraphicBounds().getHeight();
				int width = mGameContext.getGraphicBounds().getWidth();
				
				DrawBackground background = new DrawBackground(mGameContext);
		        background.setZOrder(DrawableZOrder.BACKGROUND_LAYER_2);
		        background.setColor(Color.BLUE);
		        
		    	Point[] path = new Point[]	
    			{
    				new Point(0, height - 200),
    				new Point(500, height - 300),
    				new Point(width - 500, 200),
    				new Point(width, 100)
    			};
		    	
		    	float scale1 = 0.4f;
				BezierDrawer bezierDrawer = new BezierDrawer(path, scale1);
	    		bezierDrawer.setLineColor(Color.WHITE);
	    		background.addDrawer(bezierDrawer);
	    		
		    	GraphicSprite sprite = createBall(10, mGameEngine, mGameContext);
				BezierPathTrajectory trajectory = new BezierPathTrajectory(mGameContext, sprite);
				trajectory.definePath(Direction.BACK_AND_FORTH, 4, path, scale1);
		        trajectory.setInitialSpeed(new Vector2(250, 0));
				sprite.setTrajectory(trajectory);
		    	activateDrawSprite(sprite, mGameEngine, mGameContext);

		    	path = new Point[]	
    			{
    				new Point(300, 200),
    				new Point(150, 350),
    				new Point(300, 500),
    				new Point(450, 350),
    				new Point(300, 200)
    			};
		    	
		    	float scale2 = 0.7f;
				bezierDrawer = new BezierDrawer(path, scale2);
	    		bezierDrawer.setLineColor(Color.RED);
	    		background.addDrawer(bezierDrawer);
	    		
		    	sprite = createBall(10, mGameEngine, mGameContext);
				trajectory = new BezierPathTrajectory(mGameContext, sprite);
				trajectory.definePath(Direction.BACK_AND_FORTH, 4, path, scale2);
		        trajectory.setInitialSpeed(new Vector2(200, 0));
				sprite.setTrajectory(trajectory);
		    	activateDrawSprite(sprite, mGameEngine, mGameContext);
		    	
	    		mGameEngine.addBackground(background);
				
		        mActiveBackgrounds.add(background);
			}
    	});
    	
		// Bezier & ball
    	mUnitTests.add(new AbstractUnitTest()
    	{
			@Override
			public String title()
			{
				return "Bezier & ball";
			}

			@Override
			public void run()
			{
				int height = mGameContext.getGraphicBounds().getHeight();
				int width = mGameContext.getGraphicBounds().getWidth();
				
				CollisionSet lineCollisionSet = mGameContext.getCollisionManager().createCollisionSet();
				lineCollisionSet.setCollisionsWithinSet(false);
				
				CollisionSet ballCollisionSet = mGameContext.getCollisionManager().createCollisionSet();
				ballCollisionSet.setCollisionsWithinSet(false);
				
				ballCollisionSet.joinSet(lineCollisionSet);
				
				mGameContext.getCollisionManager().setCollisionEvaluator(new CollisionEvaluatorLibrary.RepeatNearestFoundCollisionSelectorEvaluator());
				
				DrawBackground background = new DrawBackground(mGameContext);
		        background.setZOrder(DrawableZOrder.BACKGROUND_LAYER_2);
		        background.setColor(Color.BLUE);

//		    	Point[] path = new Point[]	
//    			{
//    				new Point(0, height - 200),
//    				new Point(500, height - 300),
//    				new Point(width - 500, 200),
//    				new Point(width, 100)
//    			};

		    	Point[] path = new Point[]	
    			{
    				new Point(150, 200),
    				new Point(200, 500),
    				new Point(width / 2 - 130, height - 300),
    				new Point(width / 2, height - 200),
    				new Point(width / 2 + 130, height - 300),
    				new Point(width - 200, 500),
    				new Point(width - 150, 200)
    			};

		    	BezierDrawer bezierDrawer = new BezierDrawer(path, 0.4f);
	    		bezierDrawer.setLineColor(Color.WHITE);
	    		bezierDrawer.setDrawPathPoints(Color.BLACK, BezierDrawer.PointIconType.SQUARE, 10);
	    		background.addDrawer(bezierDrawer);
	    		
		    	ArrayList<Point> bezierPath = AMath.generateBezierLines(path, 4, 0.4f);

		    	for (int i = 0; i < (bezierPath.size() - 1); i++)
		    	{
		    		Point start = bezierPath.get(i);
		    		Point end = bezierPath.get(i + 1);
		    		
		    		GraphicSprite grSprite = new GraphicSprite(mGameContext);
		            
		            LineDrawer lineDrawer = new LineDrawer(mGameContext);
		            lineDrawer.setLine((int) start.X, (int) start.Y, (int) end.X, (int) end.Y);
		            grSprite.setGraphicDrawer(lineDrawer);

		            grSprite.setInitialPosition(start);

		            grSprite.setCollisionSet(lineCollisionSet);
			        
			    	activateUpdateSprite(grSprite, mGameEngine, mGameContext);
		    	}
		    	
		    	GraphicSprite sprite = createBall(20, mGameEngine, mGameContext);
		    	
		    	OrientationControlledTrajectory trajectory = new OrientationControlledTrajectory(mGameContext, sprite);

		        trajectory.setOrientationMode(OrientationMode.SPEED);
		        trajectory.setSpeedGain(15, 15);
		        trajectory.setPositionLimits(new GraphicsViewLimits(mGameContext));
		        sprite.setTrajectory(trajectory);
		        
		        SpriteStickAction action = new SpriteStickAction();
		        action.setStickDirection(StickDirection.NORMAL_DIRECTION);
		        sprite.setCollisionAction(action);
		        
		        sprite.setInitialPosition(new Point(width / 2, height / 2));
		        
		        sprite.setCollisionSet(ballCollisionSet);
		        
		    	activateDrawSprite(sprite, mGameEngine, mGameContext);
		        
	    		mGameEngine.addBackground(background);
				
		        mActiveBackgrounds.add(background);
			}
    	});
    	
		// Touch area
    	mUnitTests.add(new AbstractUnitTest()
    	{
    		UITouchArea mTouchArea = null;
    		ImageBackground mImageBackground = null;
    		
			@Override
			public String title()
			{
				return "Touch area";
			}

			@Override
			public void run()
			{
				int height = mGameContext.getGraphicBounds().getHeight();
				int width = mGameContext.getGraphicBounds().getWidth();

				mGameEngine.setLayerCoordinateSystem(DrawableZOrder.SPRITE_LAYER_1, GameViewCoordinateSystem.GAME_VIEW_RELATIVE);
				mGameEngine.enableGameView(true);
				mGameEngine.getGameView().setGameViewLimits(0, Float.NaN, width, Float.NaN);
//				mGameEngine.setGameViewLimits(Float.NaN, 0, Float.NaN, height);
				
				createMovingBall(30, 360, 400, 220, 220, mGameEngine, mGameContext);
				createMovingBall(30, 360, 800, -220, -220, mGameEngine, mGameContext);

				mTouchArea = new UITouchArea((width - 700) / 2, (height - 500) / 2, 700,  500);
				mTouchArea.setBorder(true);
				
				UITouchAreaDragAlgorithm algorithm = new UITouchAreaDragAlgorithm();
				
				algorithm.setTransformationArea(0, 0, width, height);
				algorithm.setReverse(true);
				algorithm.setDirection(TouchDirection.DIRECTION_Y);
//				algorithm.setDirection(TouchDirection.DIRECTION_X);
				algorithm.setRollOut(true);

				mTouchArea.setAlgorithm(algorithm);
				
				mTouchArea.setOnTouchAreaListener(new ITouchAreaListener()
				{
					@Override
					public void onTouch(UITouchArea component, TouchAreaEvent event)
					{
						mGameEngine.getGameView().moveGameViewPosition(event.dx, event.dy);
					}
				});

//				mGameContext.getMainWindow().setLayout(PanelLayout.LAYOUT_LINEAR_Y);
				mGameContext.getMainWindow().addComponent(mTouchArea);

				se.federspiel.android.backgrounds.trajectories.ConstantVelocityTrajectory trajectory = 
						new se.federspiel.android.backgrounds.trajectories.ConstantVelocityTrajectory();
				trajectory.setSpeed(new Vector2(200, -200));
				
				mImageBackground = new ImageBackground(mGameContext);
				mImageBackground.setBitmapResource(R.raw.tree_background_0);
				mImageBackground.setZOrder(DrawableZOrder.BACKGROUND_LAYER_1);
				mImageBackground.setScrollDirection(ScrollDirections.SCROLL_DIRECTION_Y);
//				mImageBackground.setTrajectory(trajectory);
				
				mGameEngine.addBackground(mImageBackground);
		        mActiveBackgrounds.add(mImageBackground);
			}
			
			@Override
	    	public void teardown()
	    	{
				mGameEngine.enableGameView(false);
				mGameContext.getMainWindow().removeComponent(mTouchArea);
	    	}
    	});

    	// Touch controls
    	mUnitTests.add(new AbstractUnitTest()
    	{
    		UITouchButton mYPTouchArea = null;
    		UITouchButton mYNTouchArea = null;

    		ImageBackground mImageBackground = null;

    		private static final int BUTTON_SIZE = 150;
    		
    		@Override
			public String title()
			{
				return "Touch controls";
			}

			@Override
			public void run()
			{
				int height = mGameContext.getGraphicBounds().getHeight();
				int width = mGameContext.getGraphicBounds().getWidth();

				mGameEngine.setLayerCoordinateSystem(DrawableZOrder.SPRITE_LAYER_1, GameViewCoordinateSystem.GAME_VIEW_RELATIVE);
				mGameEngine.enableGameView(true);
				mGameEngine.getGameView().setGameViewLimits(0, Float.NaN, width, Float.NaN);
//				mGameEngine.setGameViewLimits(Float.NaN, 0, Float.NaN, height);
				
				createMovingBall(30, 360, 400, 220, 220, mGameEngine, mGameContext);
				createMovingBall(30, 360, 800, -220, -220, mGameEngine, mGameContext);

				mYNTouchArea = new UITouchButton(width - BUTTON_SIZE, 0, BUTTON_SIZE, BUTTON_SIZE);
				mYNTouchArea.setBorder(true);
				mYNTouchArea.setBitmapResource(R.raw.button_arrow_up_0);

				UITouchTrajectory uiYNTouchTrajectory = new UITouchTrajectory(mYNTouchArea, UITouchTrajectory.Directions.DIRECTION_Y_NEGATIVE);			
				uiYNTouchTrajectory.setSpeed(250);
				
				mYPTouchArea = new UITouchButton(width - BUTTON_SIZE, (height - BUTTON_SIZE), BUTTON_SIZE, BUTTON_SIZE);
				
				mYPTouchArea.setBorder(true);
				mYPTouchArea.setBitmapResource(R.raw.button_arrow_down_0);
				
				UITouchTrajectory uiYPTouchTrajectory = new UITouchTrajectory(mYPTouchArea, UITouchTrajectory.Directions.DIRECTION_Y_POSITIVE);			
				uiYPTouchTrajectory.setSpeed(250);
				
//				mGameContext.getMainWindow().setLayout(PanelLayout.LAYOUT_LINEAR_Y);
				mGameContext.getMainWindow().addComponent(mYPTouchArea);
				mGameContext.getMainWindow().addComponent(mYNTouchArea);

				se.federspiel.android.backgrounds.trajectories.CompositeTrajectory compositeTrajectory = 
						new se.federspiel.android.backgrounds.trajectories.CompositeTrajectory();
				
				compositeTrajectory.addTrajectory(uiYNTouchTrajectory);
				compositeTrajectory.addTrajectory(uiYPTouchTrajectory);
				
		        mImageBackground = new ImageBackground(mGameContext);
		        mImageBackground.setBitmapResource(R.raw.city_background_0);
		        mImageBackground.setZOrder(DrawableZOrder.BACKGROUND_LAYER_1);
		        mImageBackground.setScrollDirection(ScrollDirections.SCROLL_DIRECTION_Y);
		        
		        mGameEngine.getGameView().setTrajectory(compositeTrajectory);
		        
				mGameEngine.addBackground(mImageBackground);
		        mActiveBackgrounds.add(mImageBackground);
			}
			
			@Override
	    	public void teardown()
	    	{
				mGameEngine.enableGameView(false);
				mGameContext.getMainWindow().removeComponent(mYPTouchArea);
				mGameContext.getMainWindow().removeComponent(mYNTouchArea);
	    	}
    	});

    	// Background trajectory
    	mUnitTests.add(new AbstractUnitTest()
    	{
    		ImageBackground mImageBackground = null;

    		@Override
			public String title()
			{
				return "Background trajectory";
			}

			@Override
			public void run()
			{
				int height = mGameContext.getGraphicBounds().getHeight();
				int width = mGameContext.getGraphicBounds().getWidth();

				se.federspiel.android.backgrounds.trajectories.ConstantVelocityTrajectory trajectory = 
						new se.federspiel.android.backgrounds.trajectories.ConstantVelocityTrajectory();
				trajectory.setSpeed(new Vector2(200, -200));
				
		        mImageBackground = new ImageBackground(mGameContext);
		        mImageBackground.setBitmapResource(R.raw.city_background_0);
		        mImageBackground.setZOrder(DrawableZOrder.BACKGROUND_LAYER_1);
		        mImageBackground.setScrollDirection(ScrollDirections.SCROLL_DIRECTION_Y);
		        mImageBackground.setTrajectory(trajectory);
		        
				mGameEngine.addBackground(mImageBackground);
		        mActiveBackgrounds.add(mImageBackground);
			}
			
			@Override
	    	public void teardown()
	    	{
	    	}
    	});

    	// Touch controls/move sprite
    	mUnitTests.add(new AbstractUnitTest()
    	{
    		UITouchButton mYPTouchArea = null;
    		UITouchButton mYNTouchArea = null;

    		ImageBackground mImageBackground = null;

    		private static final int BUTTON_SIZE = 150;
    		
    		@Override
			public String title()
			{
				return "Touch controls/move sprite";
			}

			@Override
			public void run()
			{
				int height = mGameContext.getGraphicBounds().getHeight();
				int width = mGameContext.getGraphicBounds().getWidth();

//				mGameEngine.setLayerCoordinateSystem(DrawableZOrder.SPRITE_LAYER_1, GameViewCoordinateSystem.GAME_VIEW_RELATIVE);
//				mGameEngine.enableGameView(true);
//				mGameEngine.getGameView().setGameViewLimits(0, Float.NaN, width, Float.NaN);
//				mGameEngine.setGameViewLimits(Float.NaN, 0, Float.NaN, height);
	
				mYNTouchArea = new UITouchButton(width - BUTTON_SIZE, 0, BUTTON_SIZE, BUTTON_SIZE);
				mYNTouchArea.setBorder(true);
				mYNTouchArea.setBitmapResource(R.raw.button_arrow_up_0);

				mYPTouchArea = new UITouchButton(width - BUTTON_SIZE, (height - BUTTON_SIZE), BUTTON_SIZE, BUTTON_SIZE);
				mYPTouchArea.setBorder(true);
				mYPTouchArea.setBitmapResource(R.raw.button_arrow_down_0);

				mGameContext.getMainWindow().addComponent(mYPTouchArea);
				mGameContext.getMainWindow().addComponent(mYNTouchArea);

		        GraphicSprite sprite = new GraphicSprite(mGameContext);
		        
		        BallDrawer drawer = new BallDrawer(mGameContext);
		        drawer.setRadius(40);
		        sprite.setGraphicDrawer(drawer);

		        se.federspiel.android.game.trajectories.UITouchTrajectory trajectory = 
		        		new se.federspiel.android.game.trajectories.UITouchTrajectory(mGameContext, sprite);
	
		        trajectory.setInitialSpeed(200, 200);
		        trajectory.setPositionLimits(new GraphicsViewLimits(mGameContext));
		        trajectory.setInitialAcceleration(0, 200);
//		        trajectory.setAccelerationMode(AccelerationModeEnum.ACCELERATION_DIRECTIONAL_MODE);

		        trajectory.addTouchArea(mYPTouchArea, 
		        		se.federspiel.android.game.trajectories.UITouchTrajectory.Directions.DIRECTION_Y_POSITIVE);
		        
		        trajectory.addTouchArea(mYNTouchArea, 
		        		se.federspiel.android.game.trajectories.UITouchTrajectory.Directions.DIRECTION_Y_NEGATIVE);
		        
		        sprite.setTrajectory(trajectory);
		        sprite.setInitialPosition(new Point(360, 640));
		            
		        sprite.setOutOfBoundsListener(new ISpriteOutOfBoundsListener()
		        {
					@Override
					public boolean onOutOfBounds(ISprite sprite, OutOfBoundsEvent event)
					{
						boolean handled = false;
						
						if ((event.limitReached == OutOfBoundsEvent.OutOfBoundsLimit.BOUNDS_LIMIT_BOTTOM)
							|| (event.limitReached == OutOfBoundsEvent.OutOfBoundsLimit.BOUNDS_LIMIT_TOP))
						{
							event.adjust = true;
							
							handled = true;
						}
						else if ((event.limitReached == OutOfBoundsEvent.OutOfBoundsLimit.BOUNDS_LIMIT_LEFT)
								|| (event.limitReached == OutOfBoundsEvent.OutOfBoundsLimit.BOUNDS_LIMIT_RIGHT))
						{
							sprite.getTrajectory().setSpeedX(-sprite.getSpeed().X);
							
							event.adjust = true;
							
							handled = true;
						}
						
						return handled;
					}
		        });

		        
		        mGameEngine.addComponent(sprite);
		        mActiveDrawSprites.add(sprite);
				
		        mImageBackground = new ImageBackground(mGameContext);
		        mImageBackground.setBitmapResource(R.raw.city_background_0);
		        mImageBackground.setZOrder(DrawableZOrder.BACKGROUND_LAYER_1);
		        mImageBackground.setScrollDirection(ScrollDirections.SCROLL_DIRECTION_Y);
		        
				mGameEngine.addBackground(mImageBackground);
		        mActiveBackgrounds.add(mImageBackground);
			}
			
			@Override
	    	public void teardown()
	    	{
				mGameContext.getMainWindow().removeComponent(mYPTouchArea);
				mGameContext.getMainWindow().removeComponent(mYNTouchArea);
	    	}
    	});

    	// Parallax full image background
    	mUnitTests.add(new AbstractUnitTest()
    	{
    		UITouchButton mYPTouchArea = null;
    		UITouchButton mYNTouchArea = null;

    		ParallaxBackground mParallaxBackground = null;
    		ImageBackground mImageBackground = null;

    		private static final int BUTTON_SIZE = 150;
    		
    		@Override
			public String title()
			{
				return "Parallax full";
			}

			@Override
			public void run()
			{
				int height = mGameContext.getGraphicBounds().getHeight();
				int width = mGameContext.getGraphicBounds().getWidth();

				mGameEngine.setLayerCoordinateSystem(DrawableZOrder.SPRITE_LAYER_1, GameViewCoordinateSystem.GAME_VIEW_RELATIVE);
				mGameEngine.enableGameView(true);
				mGameEngine.getGameView().setGameViewLimits(0, Float.NaN, width, Float.NaN);

				mGameContext.getImageManager().setRecycleUnallocated(true);
				
				mYNTouchArea = new UITouchButton(width - BUTTON_SIZE, 0, BUTTON_SIZE, BUTTON_SIZE);
				mYNTouchArea.setBorder(true);
				mYNTouchArea.setBitmapResource(R.raw.button_arrow_up_0);

				mYPTouchArea = new UITouchButton(width - BUTTON_SIZE, (height - BUTTON_SIZE), BUTTON_SIZE, BUTTON_SIZE);
				mYPTouchArea.setBorder(true);
				mYPTouchArea.setBitmapResource(R.raw.button_arrow_down_0);

				mGameContext.getMainWindow().addComponent(mYPTouchArea);
				mGameContext.getMainWindow().addComponent(mYNTouchArea);

		        GraphicSprite sprite = new GraphicSprite(mGameContext);
		        
		        BallDrawer drawer = new BallDrawer(mGameContext);
		        drawer.setRadius(40);
		        sprite.setGraphicDrawer(drawer);

		        se.federspiel.android.game.trajectories.UITouchTrajectory trajectory = 
		        		new se.federspiel.android.game.trajectories.UITouchTrajectory(mGameContext, sprite);
	
		        trajectory.setInitialSpeed(200, 200);
		        trajectory.setInitialAcceleration(0, 200);
		        trajectory.setPositionLimits(new GameViewLimits(mGameContext, 0, 1, 0.4f, 0.6f));
			
//		        trajectory.setAccelerationMode(AccelerationModeEnum.ACCELERATION_DIRECTIONAL_MODE);

		        trajectory.addTouchArea(mYPTouchArea, 
		        		se.federspiel.android.game.trajectories.UITouchTrajectory.Directions.DIRECTION_Y_POSITIVE);
		        
		        trajectory.addTouchArea(mYNTouchArea, 
		        		se.federspiel.android.game.trajectories.UITouchTrajectory.Directions.DIRECTION_Y_NEGATIVE);
		        
		        sprite.setTrajectory(trajectory);
		        sprite.setInitialPosition(new Point(360, 640));
		            
		        sprite.setOutOfBoundsListener(new ISpriteOutOfBoundsListener()
		        {
					@Override
					public boolean onOutOfBounds(ISprite sprite, OutOfBoundsEvent event)
					{
						boolean handled = false;
						
						if ((event.limitReached == OutOfBoundsEvent.OutOfBoundsLimit.BOUNDS_LIMIT_BOTTOM)
							|| (event.limitReached == OutOfBoundsEvent.OutOfBoundsLimit.BOUNDS_LIMIT_TOP))
						{
							mGameContext.getGameEngine().getGameView().moveGameViewPosition(0, -event.distance);
							
							event.adjust = false;
							
							handled = true;
						}
						else if ((event.limitReached == OutOfBoundsEvent.OutOfBoundsLimit.BOUNDS_LIMIT_LEFT)
								|| (event.limitReached == OutOfBoundsEvent.OutOfBoundsLimit.BOUNDS_LIMIT_RIGHT))
						{
							sprite.getTrajectory().setSpeedX(-sprite.getSpeed().X);
							
							event.adjust = true;
							
							handled = true;
						}
						
						return handled;
					}
		        });

		        mGameEngine.addComponent(sprite);
		        mActiveDrawSprites.add(sprite);
				
		        mImageBackground = new ImageBackground(mGameContext);
		        mImageBackground.setBitmapResource(R.raw.background_series1_layer1);
		        mImageBackground.setZOrder(DrawableZOrder.BACKGROUND_LAYER_1);
		        
		        mGameEngine.addBackground(mImageBackground);
		        mActiveBackgrounds.add(mImageBackground);
		        
		        mParallaxBackground = new ParallaxBackground(mGameContext);
		        mParallaxBackground.setZOrder(DrawableZOrder.BACKGROUND_LAYER_2);
		        mParallaxBackground.setScrollDirection(ScrollDirections.SCROLL_DIRECTION_Y);
		        
		        ImageBackground tmpImageBackground = new ImageBackground(mGameContext);
		        tmpImageBackground.setBitmapResource(R.raw.background_series1_layer2);
		        mParallaxBackground.addBackground(tmpImageBackground, 0.2f);
		        
				tmpImageBackground = new ImageBackground(mGameContext);
		        tmpImageBackground.setBitmapResource(R.raw.background_series1_layer3);
		        mParallaxBackground.addBackground(tmpImageBackground, 0.4f);
		        
		        tmpImageBackground = new ImageBackground(mGameContext);
		        tmpImageBackground.setBitmapResource(R.raw.background_series1_layer5);
		        mParallaxBackground.addBackground(tmpImageBackground, 0.6f);
		        
				mGameEngine.addBackground(mParallaxBackground);
		        mActiveBackgrounds.add(mParallaxBackground);
			}
			
			@Override
	    	public void teardown()
	    	{
				mGameContext.getMainWindow().removeComponent(mYPTouchArea);
				mGameContext.getMainWindow().removeComponent(mYNTouchArea);
				
				mGameEngine.enableGameView(false);
				mGameEngine.getGameView().resetGameViewLimits();

				mGameContext.getImageManager().setRecycleUnallocated(false);
	    	}
    	});

    	// Parallax sprite image background
    	mUnitTests.add(new AbstractUnitTest()
    	{
    		UITouchButton mYPTouchArea = null;
    		UITouchButton mYNTouchArea = null;

    		ParallaxBackground mParallaxBackground = null;
    		ImageBackground mImageBackground = null;

    		private static final int BUTTON_SIZE = 150;
    		
    		@Override
			public String title()
			{
				return "Parallax sprite";
			}

			@Override
			public void run()
			{
				int height = mGameContext.getGraphicBounds().getHeight();
				int width = mGameContext.getGraphicBounds().getWidth();

				mGameEngine.setLayerCoordinateSystem(DrawableZOrder.SPRITE_LAYER_1, GameViewCoordinateSystem.GAME_VIEW_RELATIVE);
				mGameEngine.enableGameView(true);
				mGameEngine.getGameView().setGameViewLimits(0, Float.NaN, width, Float.NaN);

				mGameContext.getImageManager().setRecycleUnallocated(true);
				
				mYNTouchArea = new UITouchButton(width - BUTTON_SIZE, 0, BUTTON_SIZE, BUTTON_SIZE);
				mYNTouchArea.setBorder(true);
				mYNTouchArea.setBitmapResource(R.raw.button_arrow_up_0);

				mYPTouchArea = new UITouchButton(width - BUTTON_SIZE, (height - BUTTON_SIZE), BUTTON_SIZE, BUTTON_SIZE);
				mYPTouchArea.setBorder(true);
				mYPTouchArea.setBitmapResource(R.raw.button_arrow_down_0);

				mGameContext.getMainWindow().addComponent(mYPTouchArea);
				mGameContext.getMainWindow().addComponent(mYNTouchArea);

		        GraphicSprite sprite = new GraphicSprite(mGameContext);
		        
		        BallDrawer drawer = new BallDrawer(mGameContext);
		        drawer.setRadius(40);
		        sprite.setGraphicDrawer(drawer);

		        se.federspiel.android.game.trajectories.UITouchTrajectory trajectory = 
		        		new se.federspiel.android.game.trajectories.UITouchTrajectory(mGameContext, sprite);
	
		        trajectory.setInitialSpeed(200, 200);
		        trajectory.setPositionLimits(new GraphicsViewLimits(mGameContext));
		        trajectory.setInitialAcceleration(0, 200);
		        trajectory.setPositionLimits(new GameViewLimits(mGameContext, 0, 1, 0.4f, 0.6f));
			
//		        trajectory.setAccelerationMode(AccelerationModeEnum.ACCELERATION_DIRECTIONAL_MODE);

		        trajectory.addTouchArea(mYPTouchArea, 
		        		se.federspiel.android.game.trajectories.UITouchTrajectory.Directions.DIRECTION_Y_POSITIVE);
		        
		        trajectory.addTouchArea(mYNTouchArea, 
		        		se.federspiel.android.game.trajectories.UITouchTrajectory.Directions.DIRECTION_Y_NEGATIVE);
		        
		        sprite.setTrajectory(trajectory);
		        sprite.setInitialPosition(new Point(360, 640));
		            
		        sprite.setOutOfBoundsListener(new ISpriteOutOfBoundsListener()
		        {
					@Override
					public boolean onOutOfBounds(ISprite sprite, OutOfBoundsEvent event)
					{
						boolean handled = false;
						
						if ((event.limitReached == OutOfBoundsEvent.OutOfBoundsLimit.BOUNDS_LIMIT_BOTTOM)
							|| (event.limitReached == OutOfBoundsEvent.OutOfBoundsLimit.BOUNDS_LIMIT_TOP))
						{
							mGameContext.getGameEngine().getGameView().moveGameViewPosition(0, -event.distance);
							
							event.adjust = false;
							
							handled = true;
						}
						else if ((event.limitReached == OutOfBoundsEvent.OutOfBoundsLimit.BOUNDS_LIMIT_LEFT)
								|| (event.limitReached == OutOfBoundsEvent.OutOfBoundsLimit.BOUNDS_LIMIT_RIGHT))
						{
							sprite.getTrajectory().setSpeedX(-sprite.getSpeed().X);
							
							event.adjust = true;
							
							handled = true;
						}
						
						return handled;
					}
		        });

		        mGameEngine.addComponent(sprite);
		        mActiveDrawSprites.add(sprite);
				
		        mImageBackground = new ImageBackground(mGameContext);
		        mImageBackground.setBitmapResource(R.raw.background_series1_layer1);
		        mImageBackground.setZOrder(DrawableZOrder.BACKGROUND_LAYER_1);
		        
		        mGameEngine.addBackground(mImageBackground);
		        mActiveBackgrounds.add(mImageBackground);
		        
		        mParallaxBackground = new ParallaxBackground(mGameContext);
		        mParallaxBackground.setZOrder(DrawableZOrder.BACKGROUND_LAYER_2);
		        mParallaxBackground.setScrollDirection(ScrollDirections.SCROLL_DIRECTION_Y);
		        
		        IColorMatcher spriteIdentifier = new IColorMatcher()
				{
					@Override
					public boolean match(int color) 
					{
						return (Color.alpha(color) != Color.TRANSPARENT);
					}
					
				};
				
		        ImageSpriteBackground tmpImageBackground = new ImageSpriteBackground(mGameContext);
		        tmpImageBackground.setBitmapResource(R.raw.background_series1_layer2);
		        tmpImageBackground.setImageSpriteIdentifier(spriteIdentifier);
		        mParallaxBackground.addBackground(tmpImageBackground, 0.2f);
		        
				tmpImageBackground = new ImageSpriteBackground(mGameContext);
		        tmpImageBackground.setBitmapResource(R.raw.background_series1_layer3);
		        tmpImageBackground.setImageSpriteIdentifier(spriteIdentifier);
		        mParallaxBackground.addBackground(tmpImageBackground, 0.4f);
		        
		        tmpImageBackground = new ImageSpriteBackground(mGameContext);
		        tmpImageBackground.setBitmapResource(R.raw.background_series1_layer5);
		        tmpImageBackground.setImageSpriteIdentifier(spriteIdentifier);
		        mParallaxBackground.addBackground(tmpImageBackground, 0.6f);
		        
				mGameEngine.addBackground(mParallaxBackground);
		        mActiveBackgrounds.add(mParallaxBackground);
			}
			
			@Override
	    	public void teardown()
	    	{
				mGameContext.getMainWindow().removeComponent(mYPTouchArea);
				mGameContext.getMainWindow().removeComponent(mYNTouchArea);
				
				mGameEngine.enableGameView(false);
				mGameEngine.getGameView().resetGameViewLimits();

				mGameContext.getImageManager().setRecycleUnallocated(false);
	    	}
    	});
    
    	mUnitTests.add(new AbstractUnitTest()
    	{
    		// px/s^2
    		private static final float GRAVITY = 98.1f;
    		
	        private ArrayList<SpringConstraint> mSprings = new ArrayList<SpringConstraint>();
	        private ArrayList<UILabel> mLabels = new ArrayList<UILabel>();
	        private ArrayList<LinkDrawer> mSpringLinks = new ArrayList<LinkDrawer>();
	        
			@Override
			public String title()
			{
				return "Springs";
			}

			private void createSpringSprite(float x, IntegrationMethod method)
			{
				GraphicSprite sprite = new GraphicSprite(mGameContext);
		        
		        BallDrawer drawer = new BallDrawer(mGameContext);
		        drawer.setRadius(40);
		        sprite.setGraphicDrawer(drawer);

		        sprite.setZOrder(DrawableZOrder.SPRITE_LAYER_2);
		        sprite.getPhysicalProperties().setMass(100);
		        
		        sprite.setOutOfBoundsListener(new ISpriteOutOfBoundsListener()
		        {
					@Override
					public boolean onOutOfBounds(ISprite sprite, OutOfBoundsEvent event)
					{
						boolean handled = false;
						
						if (event.limitReached == OutOfBoundsEvent.OutOfBoundsLimit.BOUNDS_LIMIT_BOTTOM)
						{
							event.adjust = true;

							if (sprite.getSpeed().Y > 0)
							{
								sprite.getTrajectory().setSpeedY(0);
							}
							
							handled = true;
						}
						else if (event.limitReached == OutOfBoundsEvent.OutOfBoundsLimit.BOUNDS_LIMIT_TOP)
						{
							event.adjust = true;

							if (sprite.getSpeed().Y < 0)
							{
								sprite.getTrajectory().setSpeedY(0);
							}
							
							handled = true;
						}
						
						return handled;
					}
		        });
		        
		        GravityTrajectory trajectory = new GravityTrajectory(mGameContext, sprite);
		        sprite.setTrajectory(trajectory);
		        
		        trajectory.setGravity(0, GRAVITY);
		        trajectory.setIntegrationMethod(method);
		        trajectory.setInitialPositionX(x);
		        trajectory.setInitialPositionY(500);
		        trajectory.setAccelerationMode(AccelerationModeEnum.ACCELERATION_DIRECTIONAL_MODE);
		        trajectory.setPositionLimits(new GameViewLimits(mGameContext, 0, 1, 0, 1));
				
		        FixedBaseConstraintObject fixedBase = new FixedBaseConstraintObject(new Point(x, 100));
		        
		        SpringConstraint spring = new SpringConstraint(fixedBase, sprite);
		        spring.setSpringFactors(20, 200);
		        
		        mGameContext.getConstraintsManager().add(spring);

		        mSprings.add(spring);

		        UILabel label = null;
		        
		        switch (method)
		        {
			        case INTEGRATION_METHOD_EULER :
				        label = new UILabel((int) (x - 50), 300, 20, 10);

			        	label.setText("Euler");
			        	break;
			        	
			        case INTEGRATION_METHOD_SYMPLECTIC_EULER :
				        label = new UILabel((int) (x - 90), 300, 20, 10);

			        	label.setText("Sym. Euler");
			        	break;
			        	
			        case INTEGRATION_METHOD_POSITION_VERLET :
				        label = new UILabel((int) (x - 50), 300, 20, 10);

			        	label.setText("Verlet");
			        	break;
		        }
		        
				mGameContext.getMainWindow().addComponent(label);

				mLabels.add(label);
		        
				mGameEngine.addComponent(sprite);
		        mActiveDrawSprites.add(sprite);
		        
		        createSpringLink(fixedBase.getMassCenter(), sprite.getMassCenter());
			}

			private void createSpringLink(Point base, Point pull)
			{
				LinkDrawer springLink = new LinkDrawer(mGameContext);

				springLink.setLine(base, pull);
				
				springLink.setStrokeArea(5000);
				
	            mGameEngine.addComponent(springLink);

		        mSpringLinks.add(springLink);
			}
			
			@Override
			public void run()
			{
				createSpringSprite(180, IntegrationMethod.INTEGRATION_METHOD_EULER);
				createSpringSprite(360, IntegrationMethod.INTEGRATION_METHOD_SYMPLECTIC_EULER);
				createSpringSprite(540, IntegrationMethod.INTEGRATION_METHOD_POSITION_VERLET);
				
				testBackground();
			}
			
			@Override
	    	public void teardown()
	    	{
				for (int i = 0; i < mSprings.size(); i++)
				{
					mGameContext.getConstraintsManager().remove(mSprings.get(i));
				}
				
				mSprings.clear();
				
				for (int i = 0; i < mLabels.size(); i++)
				{
					mGameContext.getMainWindow().removeComponent(mLabels.get(i));
				}
				
				mLabels.clear();
				
				for (int i = 0; i < mSpringLinks.size(); i++)
				{
					mGameEngine.removeComponent(mSpringLinks.get(i));
				}
				
				mSpringLinks.clear();
	    	}
    	});

    	mUnitTests.add(new AbstractUnitTest()
    	{
    		// px/s^2
    		private static final float GRAVITY = 98.1f;

    		private static final boolean USE_VERLET = true;
    		
	        private ArrayList<SpringConstraint> mSprings = new ArrayList<SpringConstraint>();
	        private ArrayList<LinkDrawer> mSpringLinks = new ArrayList<LinkDrawer>();

	        @Override
			public String title()
			{
				return "Spring Links";
			}

			private AbstractSprite createSprite(float x, float y, int color, IntegrationMethod method)
			{
				GraphicSprite sprite = new GraphicSprite(mGameContext);
		        
		        BallDrawer drawer = new BallDrawer(mGameContext);
		        drawer.setRadius(20);
		        drawer.setColor(color);
		        sprite.setGraphicDrawer(drawer);

		        sprite.setZOrder(DrawableZOrder.SPRITE_LAYER_2);
		        sprite.getPhysicalProperties().setMass(100);
		        
/*		        GravityTrajectory trajectory = new GravityTrajectory(mGameContext, sprite);
		        sprite.setTrajectory(trajectory);
		        
		        trajectory.setGravity(0, GRAVITY);
		        trajectory.setGravity(0, 0);
		        trajectory.setIntegrationMethod(method);
		        trajectory.setInitialPositionX(x);
		        trajectory.setInitialPositionY(y);
		        trajectory.setAccelerationMode(AccelerationModeEnum.ACCELERATION_DIRECTIONAL_MODE);
		        trajectory.setPositionLimits(new GameViewLimits(mGameContext, 0, 1, 0, 1));
*/				
		    	OrientationControlledTrajectory trajectory = new OrientationControlledTrajectory(mGameContext, sprite);
		        sprite.setTrajectory(trajectory);

		        trajectory.setOrientationMode(OrientationMode.ACCELERATION);
		        trajectory.setAccelerationGain(2);
//		        trajectory.setSpeedGain(5, 5);
		        trajectory.setIntegrationMethod(method);
		        trajectory.setInitialPositionX(x);
		        trajectory.setInitialPositionY(y);
		        trajectory.setAccelerationMode(AccelerationModeEnum.ACCELERATION_DIRECTIONAL_MODE);
		        trajectory.setPositionLimits(new GameViewLimits(mGameContext, 0, 1, 0, 1));

		        SpriteElasticBounceAction spriteAction = new SpriteElasticBounceAction();
	            sprite.setCollisionAction(spriteAction);

	            mGameEngine.addComponent(sprite);
		        mActiveDrawSprites.add(sprite);
		        
		        return sprite;
			}

			private void createSpringLink(AbstractSprite base, AbstractSprite pull)
			{
				LinkDrawer springLink = new LinkDrawer(mGameContext);

				springLink.setLine(base.getPosition(), pull.getPosition());
				springLink.setStrokeArea(2000);
				
	            mGameEngine.addComponent(springLink);

		        mSpringLinks.add(springLink);
			}
			
			private void createSpring(AbstractSprite base, AbstractSprite pull)
			{
		        SpringConstraint spring = new SpringConstraint(base, pull);
		        spring.setSpringFactors(60, 300);
		        spring.setIncompressible(100);
		        
		        mGameContext.getConstraintsManager().add(spring);

		        mSprings.add(spring);
		        
		        createSpringLink(base, pull);
			}

			@Override
			public void run()
			{
				IntegrationMethod method = IntegrationMethod.INTEGRATION_METHOD_SYMPLECTIC_EULER;
				
				if (USE_VERLET)
				{
					method = IntegrationMethod.INTEGRATION_METHOD_POSITION_VERLET;
				}
				
				AbstractSprite sprite1 = createSprite(300, 100, Color.RED, method);
				AbstractSprite sprite2 = createSprite(400, 100, Color.WHITE, method);
				AbstractSprite sprite3 = createSprite(400, 200, Color.GREEN, method);
				AbstractSprite sprite4 = createSprite(300, 200, Color.YELLOW, method);

				createSpring(sprite1, sprite2);
				createSpring(sprite2, sprite3);
				createSpring(sprite3, sprite4);
				createSpring(sprite4, sprite1);

				createSpring(sprite1, sprite3);
				createSpring(sprite2, sprite4);

	            mGameContext.getCollisionManager().add(sprite1.getCollisionObject());
	            mGameContext.getCollisionManager().add(sprite2.getCollisionObject());
	            mGameContext.getCollisionManager().add(sprite3.getCollisionObject());
	            mGameContext.getCollisionManager().add(sprite4.getCollisionObject());

	            testBackground();
			}
			
			@Override
	    	public void teardown()
	    	{
				for (int i = 0; i < mSprings.size(); i++)
				{
					mGameContext.getConstraintsManager().remove(mSprings.get(i));
				}
				
				mSprings.clear();
				
				for (int i = 0; i < mSpringLinks.size(); i++)
				{
					mGameEngine.removeComponent(mSpringLinks.get(i));
				}
				
				mSpringLinks.clear();
	    	}
    	});

    	mUnitTests.add(new AbstractUnitTest()
    	{
    		// px/s^2
    		private static final float GRAVITY = 25.1f;

    		private static final boolean USE_VERLET = true;

    		private static final int BOX_SIZE = 70;
    		private static final int BOX_MASS = 100;

    		private static final int PLATFORM_WIDTH = 300;
    		private static final int PLATFORM_HEIGHT = 50;
    		
    		private static final int PLATFORM_MASS = 100;
    		private static final int SPRING_RESTING_DISTANCE = 300;
    		private static final int SPRING_CONSTANT = 60;
    		private static final int SPRING_DAMPING = 0; // 40;
    		
    		private static final int STROKE_AREA = 6000;
    		
    		private ArrayList<SpringConstraint> mSprings = new ArrayList<SpringConstraint>();
	        private ArrayList<LinkDrawer> mSpringLinks = new ArrayList<LinkDrawer>();

	        @Override
			public String title()
			{
				return "Spring Platform";
			}

			private AbstractSprite createBoxSprite(float x, float y, int color)
			{
				GraphicSprite sprite = new GraphicSprite(mGameContext);
		        
		        BoxDrawer drawer = new BoxDrawer(mGameContext);
		        drawer.setDimensions(BOX_SIZE, BOX_SIZE);
		        drawer.setColor(color);
		        sprite.setGraphicDrawer(drawer);

		        sprite.setOutOfBoundsListener(new ISpriteOutOfBoundsListener()
		        {
					@Override
					public boolean onOutOfBounds(ISprite sprite, OutOfBoundsEvent event)
					{
						boolean handled = false;
						
						if (event.limitReached == OutOfBoundsEvent.OutOfBoundsLimit.BOUNDS_LIMIT_BOTTOM)
						{
							event.adjust = true;

							if (sprite.getSpeed().Y > 0)
							{
								sprite.getTrajectory().setSpeedY(0);
							}
							
							handled = true;
						}
						else if (event.limitReached == OutOfBoundsEvent.OutOfBoundsLimit.BOUNDS_LIMIT_TOP)
						{
							event.adjust = true;

							if (sprite.getSpeed().Y < 0)
							{
								sprite.getTrajectory().setSpeedY(0);
							}
							
							handled = true;
						}
						else if (event.limitReached == OutOfBoundsEvent.OutOfBoundsLimit.BOUNDS_LIMIT_RIGHT)
						{
							event.adjust = true;

							if (sprite.getSpeed().X > 0)
							{
								sprite.getTrajectory().setSpeedX(0);
							}
							
							handled = true;
						}
						else if (event.limitReached == OutOfBoundsEvent.OutOfBoundsLimit.BOUNDS_LIMIT_LEFT)
						{
							event.adjust = true;

							if (sprite.getSpeed().X < 0)
							{
								sprite.getTrajectory().setSpeedX(0);
							}
							
							handled = true;
						}
						
						return handled;
					}
		        });
		        
		        sprite.setZOrder(DrawableZOrder.SPRITE_LAYER_2);
		        sprite.getPhysicalProperties().setMass(BOX_MASS);
		        
//		        SpriteAdjustPositionAction action = new SpriteAdjustPositionAction();
//		        sprite.setCollisionAction(action);
		        
		        SpriteStickAction action = new SpriteStickAction();
		        sprite.setCollisionAction(action);
		        
//		        SpriteInelasticBounceAction action = new SpriteInelasticBounceAction();
//		        sprite.setCollisionAction(action);
		        
		        
		        CompositeTrajectory compositeTrajectory = new CompositeTrajectory(mGameContext, sprite);
		        compositeTrajectory.setPositionLimits(new GameViewLimits(mGameContext, 0, 1, 0, 1));
		        sprite.setTrajectory(compositeTrajectory);
		        
		        TouchSlideTrajectory touchTrajectory = new TouchSlideTrajectory(mGameContext);
		        touchTrajectory.setSlideRelease(false);

		        GravityTrajectory trajectory = new GravityTrajectory(mGameContext);

		        trajectory.setGravity(0, GRAVITY);
		        trajectory.setIntegrationMethod(IntegrationMethod.INTEGRATION_METHOD_SYMPLECTIC_EULER);
		        trajectory.setAccelerationMode(AccelerationModeEnum.ACCELERATION_DIRECTIONAL_MODE);
				
		        compositeTrajectory.addServeAllTrajectory(trajectory);
		        compositeTrajectory.addServeFirstTrajectory(touchTrajectory);
		        
	        	sprite.setInitialPositionX(x);
	        	sprite.setInitialPositionY(y);
	        	
	            mGameEngine.addComponent(sprite);
		        mActiveDrawSprites.add(sprite);
		        
		        return sprite;
			}

			private AbstractSprite createPlatformSprite(float x, float y, int color, IntegrationMethod method)
			{
				GraphicSprite sprite = new GraphicSprite(mGameContext);
		        
		        BoxDrawer drawer = new BoxDrawer(mGameContext);
		        drawer.setDimensions(PLATFORM_WIDTH, PLATFORM_HEIGHT);
		        drawer.setColor(color);
		        sprite.setGraphicDrawer(drawer);

		        sprite.setZOrder(DrawableZOrder.SPRITE_LAYER_2);
		        sprite.getPhysicalProperties().setMass(PLATFORM_MASS);

		        SpriteNonAdjustAction action = new SpriteNonAdjustAction();
		        sprite.setCollisionAction(action);

//		        SpriteStickAction action = new SpriteStickAction();
//		        action.setStickDirection(StickDirection.ALL_DIRECTIONS);
//		        sprite.setCollisionAction(action);
		        
		        GravityTrajectory trajectory = new GravityTrajectory(mGameContext, sprite);
		        sprite.setTrajectory(trajectory);
		        
		        trajectory.setGravity(0, GRAVITY);
		        trajectory.setIntegrationMethod(method);
		        trajectory.setAccelerationMode(AccelerationModeEnum.ACCELERATION_DIRECTIONAL_MODE);
		        trajectory.setPositionLimits(new GameViewLimits(mGameContext, 0, 1, 0, 1));
				
	        	sprite.setInitialPositionX(x);
	        	sprite.setInitialPositionY(y);
	        	
	            mGameEngine.addComponent(sprite);
		        mActiveDrawSprites.add(sprite);
		        
		        return sprite;
			}

			private void createSpringLink(Point base, Point pull)
			{
				LinkDrawer springLink = new LinkDrawer(mGameContext);

				springLink.setLine(base, pull);
				springLink.setStrokeArea(STROKE_AREA);
				
	            mGameEngine.addComponent(springLink);

		        mSpringLinks.add(springLink);
			}
			
			private void createSpring(int baseX, int baseY, AbstractSprite pull)
			{
		        FixedBaseConstraintObject base = new FixedBaseConstraintObject(new Point(baseX, baseY));

		        SpringConstraint spring = new SpringConstraint(base, pull);
		        spring.setSpringFactors(SPRING_CONSTANT, SPRING_RESTING_DISTANCE);
		        spring.setDamping(SPRING_DAMPING);
		        spring.setIncompressible(100);
		        
		        mGameContext.getConstraintsManager().add(spring);

		        mSprings.add(spring);
		        
		        createSpringLink(base.getMassCenter(), pull.getMassCenter());
			}

			@Override
			public void run()
			{
				int height = mGameContext.getGraphicBounds().getHeight();
				int width = mGameContext.getGraphicBounds().getWidth();
				
				mGameContext.getCollisionManager().setCollisionEvaluator(new CollisionEvaluatorLibrary.RepeatNearestFoundCollisionSelectorEvaluator());
//				mGameContext.getCollisionManager().setCollisionEvaluator(new CollisionEvaluatorLibrary.NearestFoundCollisionSelectorEvaluator());
//				mGameContext.getCollisionManager().setCollisionEvaluator(new CollisionEvaluatorLibrary.NearestFoundCollisionEvaluator());
//				mGameContext.getCollisionManager().setCollisionEvaluator(new CollisionEvaluatorLibrary.FirstFoundCollisionEvaluator());
				
				IntegrationMethod method = IntegrationMethod.INTEGRATION_METHOD_SYMPLECTIC_EULER;
				
				if (USE_VERLET)
				{
					method = IntegrationMethod.INTEGRATION_METHOD_POSITION_VERLET;
				}
				
				AbstractSprite platform = createPlatformSprite(
						(width - PLATFORM_WIDTH) / 2, height - SPRING_RESTING_DISTANCE - PLATFORM_HEIGHT, Color.RED, method);

				createSpring(width / 2, height, platform);

	            mGameContext.getCollisionManager().add(platform);

	            AbstractSprite box = createBoxSprite(100, 10, Color.GREEN);
	            mGameContext.getCollisionManager().add(box);
	            
				box = createBoxSprite(width - 100 - BOX_SIZE, 10, Color.YELLOW);
	            mGameContext.getCollisionManager().add(box);
	            
	            testBackground();
			}
			
			@Override
	    	public void teardown()
	    	{
				for (int i = 0; i < mSprings.size(); i++)
				{
					mGameContext.getConstraintsManager().remove(mSprings.get(i));
				}
				
				mSprings.clear();
				
				for (int i = 0; i < mSpringLinks.size(); i++)
				{
					mGameEngine.removeComponent(mSpringLinks.get(i));
				}
				
				mSpringLinks.clear();
	    	}
    	});

    	mUnitTests.add(new AbstractUnitTest()
    	{
    		// px/s^2
    		private static final int TOTAL_LINK_DISTANCE = 500;
       		private static final int MASS = 100;
       	    private static final int VISUAL_BALL_SIZE = 2;		
       	    private static final int NON_VISUAL_TOUCH_ZONE_SIZE = 20;		
       	    private static final int NON_VISUAL_COLLISION_ZONE_SIZE = 2;		
       	 
       	    private static final boolean COLLISION = false;
       	    private static final boolean VISUAL = false;
       	    
       		private static final int NOF_LINKS = 40;
       		private static final int CONSTRAINTS_ITERATIONS = 10;
       		
	        private ArrayList<LinkDrawer> mLinkDrawers = new ArrayList<LinkDrawer>();

	        private ConstraintsGroup mConstraintsGroup = new ConstraintsGroup(CONSTRAINTS_ITERATIONS);
	        
	        @Override
			public String title()
			{
				return "Rigid links";
			}

			private AbstractSprite createSprite(float x, float y, int color, 
					float mass, IntegrationMethod method)
			{
				AbstractSprite sprite = null;
				
				if (VISUAL)
				{
					GraphicSprite grSprite = new GraphicSprite(mGameContext);
			        
			        BallDrawer drawer = new BallDrawer(mGameContext);
			        drawer.setRadius(VISUAL_BALL_SIZE);
			        drawer.setColor(color);
			        grSprite.setGraphicDrawer(drawer);
			        
			        sprite = grSprite;
				}
				else
				{
					NonVisualSprite nonVSprite = new NonVisualSprite(mGameContext);
			        
					nonVSprite.setDimensions(new Dimensions(NON_VISUAL_TOUCH_ZONE_SIZE, NON_VISUAL_TOUCH_ZONE_SIZE));
					nonVSprite.setCollisionDimensions(new Dimensions(NON_VISUAL_COLLISION_ZONE_SIZE, NON_VISUAL_COLLISION_ZONE_SIZE));
					
			        sprite = nonVSprite;
				}
				
		        sprite.setZOrder(DrawableZOrder.SPRITE_LAYER_2);
		        sprite.getPhysicalProperties().setMass(mass);
		        
		        
		        if (mass > 0)
		        {
		        	CompositeTrajectory compositeTrajectory = new CompositeTrajectory(mGameContext, sprite);
			        compositeTrajectory.setPositionLimits(new GameViewLimits(mGameContext, 0, 1, 0, 1));
			        sprite.setTrajectory(compositeTrajectory);

		        	OrientationControlledTrajectory trajectory = new OrientationControlledTrajectory(mGameContext);
	
			        trajectory.setOrientationMode(OrientationMode.ACCELERATION);
			        trajectory.setAccelerationGain(2);
			        trajectory.setIntegrationMethod(method);
			        trajectory.setAccelerationMode(AccelerationModeEnum.ACCELERATION_DIRECTIONAL_MODE);

			        TouchDragTrajectory touchTrajectory = new TouchDragTrajectory(mGameContext);			        
			        touchTrajectory.setContinuousUpdate(true);
			        
			        compositeTrajectory.addServeAllTrajectory(trajectory);
			        compositeTrajectory.addServeAllTrajectory(touchTrajectory);
			        
		        	sprite.setInitialPositionX(x);
		        	sprite.setInitialPositionY(y);
		        }
		        else
		        {
		        	sprite.setInitialPositionX(x);
		        	sprite.setInitialPositionY(y);
		        }

		        if (COLLISION)
		        {	
			        SpriteElasticBounceAction spriteAction = new SpriteElasticBounceAction();
		            sprite.setCollisionAction(spriteAction);
		        }
		        
	            mGameEngine.addComponent(sprite);
		        mActiveDrawSprites.add(sprite);
		        
		        return sprite;
			}

			private void createLinkDrawer(AbstractSprite baseOne, AbstractSprite baseTwo)
			{
				LinkDrawer springLink = new LinkDrawer(mGameContext);

				springLink.setLine(baseOne.getMassCenter(), baseTwo.getMassCenter());
				springLink.setStrokeWidth(2);
				
	            mGameEngine.addComponent(springLink);

		        mLinkDrawers.add(springLink);
			}
			
			private void createRigidLink(AbstractSprite baseOne, AbstractSprite baseTwo, float distance,
					ConstraintsGroup constraintsGroup)
			{
				RigidLinkConstraint link = new RigidLinkConstraint(baseOne, baseTwo);
		        link.setLinkDistance(distance);
		        
		        constraintsGroup.add(link);
		        
		        createLinkDrawer(baseOne, baseTwo);
			}

			@Override
			public void run()
			{
				int xPos = 360;
				int yPos = 100;
				
				float linkDistance = TOTAL_LINK_DISTANCE / NOF_LINKS;
				
				mScreenTouch.reduceTouchArea();
				
				AbstractSprite prevSprite = createSprite(xPos, yPos, Color.RED, 0, IntegrationMethod.INTEGRATION_METHOD_POSITION_VERLET);
				
				mGameContext.getCollisionManager().add(prevSprite.getCollisionObject());
				
				for (int i = 0; i < NOF_LINKS; i++)
				{
					yPos += linkDistance;
					
					AbstractSprite sprite = createSprite(xPos, yPos, Color.WHITE, MASS, IntegrationMethod.INTEGRATION_METHOD_POSITION_VERLET);
					
			        if (COLLISION)
			        {	
			        	mGameContext.getCollisionManager().add(sprite.getCollisionObject());
			        }
			        
					createRigidLink(prevSprite, sprite, linkDistance, mConstraintsGroup);
					
					prevSprite = sprite;
				}

		        mGameContext.getConstraintsManager().add(mConstraintsGroup);

	            testBackground();
			}
			
			@Override
	    	public void teardown()
	    	{
				for (int i = 0; i < mLinkDrawers.size(); i++)
				{
					mGameEngine.removeComponent(mLinkDrawers.get(i));
				}
				
				mLinkDrawers.clear();
				
		        mGameContext.getConstraintsManager().remove(mConstraintsGroup);
	    	}
    	});

    	mUnitTests.add(new AbstractUnitTest()
    	{
    		private static final int CLOTH_HEIGHT = 500;
    		private static final int CLOTH_WIDTH = 300;

    		private static final int Y_START = 100;
    		private static final int X_START = (720 - CLOTH_WIDTH) / 2;

    		private static final int X_CLOTH_POINTS = 30; //6;
    		private static final int Y_CLOTH_POINTS = 20; //4;

    		private static final int MASS = 100;
       	    private static final int TOUCH_ZONE_SIZE = 20;		
       	    private static final int COLLISION_ZONE_SIZE = 2;		

       	    private static final boolean COLLISION = false;
       	    
       		private static final int NOF_LINKS = 1;
       		private static final int CONSTRAINTS_ITERATIONS = 10;
       		
	        private ArrayList<LinkDrawer> mLinkDrawers = new ArrayList<LinkDrawer>();

	        private ConstraintsGroup mConstraintsGroup = new ConstraintsGroup(CONSTRAINTS_ITERATIONS);
	        
	        @Override
			public String title()
			{
				return "Rigid links (cloth)";
			}

			private AbstractSprite createSprite(float x, float y, int color, float mass, IntegrationMethod method)
			{
				NonVisualSprite sprite = new NonVisualSprite(mGameContext);
		        
				sprite.setDimensions(new Dimensions(TOUCH_ZONE_SIZE, TOUCH_ZONE_SIZE));
				sprite.setCollisionDimensions(new Dimensions(COLLISION_ZONE_SIZE, COLLISION_ZONE_SIZE));
							
		        sprite.setZOrder(DrawableZOrder.SPRITE_LAYER_2);
		        sprite.getPhysicalProperties().setMass(mass);
		        
		        if (mass > 0)
		        {
		        	CompositeTrajectory compositeTrajectory = new CompositeTrajectory(mGameContext, sprite);
			        compositeTrajectory.setPositionLimits(new GameViewLimits(mGameContext, 0, 1, 0, 1));
			        sprite.setTrajectory(compositeTrajectory);

		        	OrientationControlledTrajectory trajectory = new OrientationControlledTrajectory(mGameContext);
	
			        trajectory.setOrientationMode(OrientationMode.ACCELERATION);
			        trajectory.setAccelerationGain(2);
			        trajectory.setIntegrationMethod(method);
			        trajectory.setAccelerationMode(AccelerationModeEnum.ACCELERATION_DIRECTIONAL_MODE);

			        TouchDragTrajectory touchTrajectory = new TouchDragTrajectory(mGameContext);			        
			        
			        compositeTrajectory.addServeAllTrajectory(trajectory);
			        compositeTrajectory.addServeAllTrajectory(touchTrajectory);
			        
		        	sprite.setInitialPositionX(x);
		        	sprite.setInitialPositionY(y);
		        }
		        else
		        {
		        	sprite.setInitialPositionX(x);
		        	sprite.setInitialPositionY(y);
		        }

		        if (COLLISION)
		        {	
			        SpriteElasticBounceAction spriteAction = new SpriteElasticBounceAction();
		            sprite.setCollisionAction(spriteAction);
		        }
		        
	            mGameEngine.addComponent(sprite);
		        mActiveDrawSprites.add(sprite);
		        
		        return sprite;
			}

			private void createLinkDrawer(AbstractSprite baseOne, AbstractSprite baseTwo)
			{
				LinkDrawer springLink = new LinkDrawer(mGameContext);

				springLink.setLine(baseOne.getMassCenter(), baseTwo.getMassCenter());
				springLink.setStrokeWidth(2);
				
	            mGameEngine.addComponent(springLink);

		        mLinkDrawers.add(springLink);
			}
			
			private void createRigidLink(AbstractSprite baseOne, AbstractSprite baseTwo, float distance,
					ConstraintsGroup constraintsGroup)
			{
				RigidLinkConstraint link = new RigidLinkConstraint(baseOne, baseTwo);
		        link.setLinkDistance(distance);
		        
		        constraintsGroup.add(link);
		        
		        createLinkDrawer(baseOne, baseTwo);
			}

			@Override
			public void run()
			{
				mScreenTouch.reduceTouchArea();
				
				int xPos = X_START;
				int yPos = Y_START;

				int xLinkWidth = CLOTH_WIDTH / (X_CLOTH_POINTS - 1);
				int yLinkHeight = CLOTH_HEIGHT / (Y_CLOTH_POINTS - 1);

				float xLinkDistance = xLinkWidth / NOF_LINKS;
				float yLinkDistance = yLinkHeight / NOF_LINKS;

				AbstractSprite[][] cMatrix = new AbstractSprite[Y_CLOTH_POINTS][X_CLOTH_POINTS];

				int mass = 0;
				
				for(int y = 0; y < Y_CLOTH_POINTS; y++)
				{
					xPos = X_START;
					
					for (int x = 0; x < X_CLOTH_POINTS; x++)
					{
						cMatrix[y][x] = createSprite(xPos, yPos, Color.RED, mass, IntegrationMethod.INTEGRATION_METHOD_POSITION_VERLET);
						
				        if (COLLISION)
				        {	
							mGameContext.getCollisionManager().add(cMatrix[y][x].getCollisionObject());
				        }
				        
						xPos += xLinkWidth;
					}
					
					mass = MASS;
					
					yPos += yLinkHeight;
				}
				
				// X direction
				yPos = Y_START;
						
				for (int y = 0; y < Y_CLOTH_POINTS; y++)
				{
					xPos = X_START;
					
					for (int x = 0; x < (X_CLOTH_POINTS - 1); x++)
					{
						AbstractSprite prevSprite = cMatrix[y][x];
						           
						for (int i = 0; i < (NOF_LINKS - 1); i++)
						{
							xPos += xLinkDistance;
							
							AbstractSprite linkSprite = createSprite(xPos, yPos, Color.WHITE, MASS, IntegrationMethod.INTEGRATION_METHOD_POSITION_VERLET);
							
					        if (COLLISION)
					        {	
					        	mGameContext.getCollisionManager().add(linkSprite.getCollisionObject());
					        }
					        
							createRigidLink(prevSprite, linkSprite, xLinkDistance, mConstraintsGroup);
							
							prevSprite = linkSprite;
						}

						AbstractSprite endSprite = cMatrix[y][x + 1];
						
				        if (COLLISION)
				        {	
				        	mGameContext.getCollisionManager().add(endSprite.getCollisionObject());
				        }
				        
						createRigidLink(prevSprite, endSprite, xLinkDistance, mConstraintsGroup);
					}
					
					yPos += yLinkHeight;
				}
				
				// Y direction
				xPos = X_START;
						
				for (int x = 0; x < X_CLOTH_POINTS; x++)
				{
					yPos = Y_START;
					
					for (int y = 0; y < (Y_CLOTH_POINTS - 1); y++)
					{
						AbstractSprite prevSprite = cMatrix[y][x];
						           
						for (int i = 0; i < (NOF_LINKS - 1); i++)
						{
							yPos += yLinkDistance;
							
							AbstractSprite linkSprite = createSprite(xPos, yPos, Color.WHITE, MASS, IntegrationMethod.INTEGRATION_METHOD_POSITION_VERLET);
							
					        if (COLLISION)
					        {	
					        	mGameContext.getCollisionManager().add(linkSprite.getCollisionObject());
					        }
					        
							createRigidLink(prevSprite, linkSprite, yLinkDistance, mConstraintsGroup);
							
							prevSprite = linkSprite;
						}

						AbstractSprite endSprite = cMatrix[y + 1][x];
						
				        if (COLLISION)
				        {	
				        	mGameContext.getCollisionManager().add(endSprite.getCollisionObject());
				        }
				        
						createRigidLink(prevSprite, endSprite, yLinkDistance, mConstraintsGroup);
					}
					
					xPos += xLinkWidth;
				}

				mGameContext.getConstraintsManager().add(mConstraintsGroup);

	            testBackground();
			}
			
			@Override
	    	public void teardown()
	    	{
				for (int i = 0; i < mLinkDrawers.size(); i++)
				{
					mGameEngine.removeComponent(mLinkDrawers.get(i));
				}
				
				mLinkDrawers.clear();
				
		        mGameContext.getConstraintsManager().remove(mConstraintsGroup);
	    	}
    	}); 
    }
    
    private int switchTest(int index)
    {
    	if (mCurrentRunningTest != null)
    	{
    		mCurrentRunningTest.teardown();
    	}
    	
    	if (index >= mUnitTests.size())
    	{
    		index = 0;
    	}

    	for (ISprite sprite : mActiveDrawSprites)
    	{
    		mGameContext.getCollisionManager().remove(sprite.getCollisionObject());
    		mGameEngine.removeComponent(sprite);
    	}
    	
    	for (ISprite sprite : mActiveUpdateSprites)
    	{
    		mGameContext.getCollisionManager().remove(sprite.getCollisionObject());
    		mGameEngine.removeUpdateComponent(sprite);
    	}

    	mActiveDrawSprites.clear();

    	for (IBackground background : mActiveBackgrounds)
    	{
    		mGameEngine.removeBackground(background);
    	}
    	
    	mActiveBackgrounds.clear();

    	mGameContext.getImageManager().recycleUnallocated();
    	
    	mCurrentRunningTest = mUnitTests.get(index);

    	mtestNameLabel.setText("" + (index + 1) + ":" + mCurrentRunningTest.title());
    	
    	mCurrentRunningTest.run();
    	
    	return index;
    }
    
    private static abstract class AbstractUnitTest implements IUnitTest
    {
    	public void teardown()
    	{
    	}
    }
    
    private interface IUnitTest
    {
    	public String title();
    	public void run();
    	public void teardown();
    }
    
    private class ScreenTouch implements IOnClickListener
    {
    	private SwitchTestRunner mRunner = null;
    	private Rectangle mBounds = null; 
    	private GameEngine mGameEngine = null;
    	private IGameContext mGameContext = null;
    	
    	public ScreenTouch(CollisionTestView view, GameEngine gameEngine, IGameContext gameContext)
    	{
    		mRunner = new SwitchTestRunner(gameEngine, view);
    		mBounds = gameContext.getGraphicBounds().clone();
    		mGameEngine = gameEngine;
    		mGameContext = gameContext;
    		
    		gameContext.getUserInputManager().setBoundedOnClickListener(this, mBounds, IUserInputManager.TouchZOrder.BACKGROUND);
    	}

    	public void reduceTouchArea()
    	{
    		mBounds.setPosition(mBounds.getLeft(), mBounds.getTop() + mBounds.getHeight() / 2);
    		mBounds.setDimensions(mBounds.getWidth(), mBounds.getHeight() / 2);
    	}
    	
		@Override
		public boolean onClick(TouchClickEvent event)
		{
			mRunner.nextTest();
			
			mBounds.set(mGameContext.getGraphicBounds());
			
			return true;
		}

		private class SwitchTestRunner implements Runnable
		{
			private boolean mScheduled = false;
			private int mIndex = START_UNIT_TEST_INDEX;
			
			private CollisionTestView mView = null;
	    	private GameEngine mGameEngine = null;
			
			public SwitchTestRunner(GameEngine gameEngine, CollisionTestView view)
			{
				mGameEngine = gameEngine;
				mView = view;
			}

			public synchronized void nextTest()
			{
				mIndex++;
				
				if (!mScheduled)
				{
					mGameEngine.invokeOnGameThread(mRunner);
					
					mScheduled = true;
				}
			}
			
			@Override
			public synchronized void run()
			{
				mIndex = mView.switchTest(mIndex);
				
				mScheduled = false;
			}
		}
    }
    
    private class TerminationListener implements ITerminateSpriteListener
    {
		@Override
		public void onSpriteTermination(ISprite sprite)
		{
	        mActiveDrawSprites.remove(sprite);
		}
    }
}
