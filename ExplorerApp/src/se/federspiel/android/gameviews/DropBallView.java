package se.federspiel.android.gameviews;

import java.util.ArrayList;

import se.federspiel.android.agraphics.CanvasSurfaceView;
import se.federspiel.android.agraphics.DrawThread.IDrawer;
import se.federspiel.android.backgrounds.ColorBackground;
import se.federspiel.android.backgrounds.ImageBackground;
import se.federspiel.android.game.GameApplication;
import se.federspiel.android.game.GameEngine;
import se.federspiel.android.game.collision.CollisionEvaluatorLibrary;
import se.federspiel.android.game.collision.CollisionSet;
import se.federspiel.android.game.geometry.Point;
import se.federspiel.android.game.geometry.Vector2;
import se.federspiel.android.game.interfaces.ICollisionSprite;
import se.federspiel.android.game.interfaces.IDrawableComponent.DrawableZOrder;
import se.federspiel.android.game.interfaces.IGameContext;
import se.federspiel.android.game.interfaces.IGameEngine;
import se.federspiel.android.game.interfaces.IImageManager.ScaleOperator;
import se.federspiel.android.game.interfaces.IImageSpriteDrawer.BitmapCollisionBounds;
import se.federspiel.android.game.interfaces.IScrollableBackground.ScrollDirections;
import se.federspiel.android.game.interfaces.ISprite;
import se.federspiel.android.game.interfaces.ISprite.ISpriteOutOfBoundsListener;
import se.federspiel.android.game.interfaces.ITrajectory;
import se.federspiel.android.game.interfaces.IUserInputManager.IOnClickListener;
import se.federspiel.android.game.interfaces.IUserInputManager.TouchClickEvent;
import se.federspiel.android.game.sprites.GraphicSprite;
import se.federspiel.android.game.sprites.ImageSprite;
import se.federspiel.android.game.sprites.actions.SpriteCompositeAction;
import se.federspiel.android.game.sprites.actions.SpriteNonAdjustAction;
import se.federspiel.android.game.sprites.actions.SpriteStickAction;
import se.federspiel.android.game.sprites.actions.SpriteStickAction.StickDirection;
import se.federspiel.android.game.sprites.actions.SpriteTerminateAction;
import se.federspiel.android.game.sprites.actions.SpriteTerminateAction.ITerminateSpriteListener;
import se.federspiel.android.game.sprites.drawers.ImageDrawer;
import se.federspiel.android.game.sprites.drawers.ImageDrawerAnimator;
import se.federspiel.android.game.trajectories.AbstractPathTrajectory.Direction;
import se.federspiel.android.game.trajectories.BezierPathTrajectory;
import se.federspiel.android.game.trajectories.MovementTrajectory;
import se.federspiel.android.game.trajectories.KeyControlledTrajectory;
import se.federspiel.android.game.trajectories.OrientationControlledTrajectory;
import se.federspiel.android.game.trajectories.OrientationControlledTrajectory.OrientationAxis;
import se.federspiel.android.game.trajectories.OrientationControlledTrajectory.OrientationDirections;
import se.federspiel.android.game.trajectories.OrientationControlledTrajectory.OrientationMode;
import se.federspiel.android.game.trajectories.TouchSlideTrajectory;
import se.federspiel.android.game.trajectories.limits.BoundsLimits;
import se.federspiel.android.game.trajectories.limits.OutOfBoundsEvent;
import se.federspiel.android.game.trajectories.limits.GraphicsViewLimits;
import se.federspiel.android.game.ui.UIDialog;
import se.federspiel.android.game.ui.UIFactory;
import se.federspiel.android.game.ui.UIFactory.UITapDialog;
import se.federspiel.android.game.ui.UIInputComponent;
import se.federspiel.android.game.ui.UIInputComponent.UIIOnClickListener;
import se.federspiel.android.game.utils.AMath;
import se.federspiel.android.game.utils.ObjectCache;
import android.content.Context;
import android.util.AttributeSet;

import com.example.explorerapp.R;

public class DropBallView extends CanvasSurfaceView
{
	private static final int SCREEN_WIDTH = 720;
	
	private static final int BACKGROUND_IMAGE_RES = R.raw.background_drops_0;
	private static final int BRIDGE_IMAGE_RES = R.raw.wall_1;
	private static final int FENCE_IMAGE_RES = R.raw.wall_1;
			
	private static final int BRIDGE_SEPARATION = 150;
	private static final int BRIDGE_HOLE_WIDTH = 100;
	private static final int BRIDGE_VISIBLE_MIN_WIDTH = 50;
	
	private static final int GHOST_WIDTH = 80;
	private static final int GHOST_HEIGHT = 80;
	private static final int GHOST_SPEED_GAIN_Y = 15;
	private static final int GHOST_SPEED_GAIN_X = 25;
	private static final int GHOST_BOUNCE_MARGIN = 10;
	
	private static final int BRIDGE_EXTENT = 2 * GHOST_WIDTH;

	private static final int BRIDGE_HEIGHT = 25;
	private static final int BRIDGE_WIDTH = SCREEN_WIDTH - BRIDGE_HOLE_WIDTH - BRIDGE_VISIBLE_MIN_WIDTH + BRIDGE_EXTENT;

	private static final int LEFT_BRIDGE_MIN_X = BRIDGE_VISIBLE_MIN_WIDTH - BRIDGE_WIDTH;
	private static final int LEFT_BRIDGE_MAX_X = BRIDGE_WIDTH - BRIDGE_EXTENT;
	
	private static final int RIGHT_BRIDGE_MIN_X = BRIDGE_VISIBLE_MIN_WIDTH + BRIDGE_HOLE_WIDTH;
	private static final int RIGHT_BRIDGE_MAX_X = BRIDGE_WIDTH + SCREEN_WIDTH - BRIDGE_VISIBLE_MIN_WIDTH;
	
	private static final int BRIDGE_SPEED_Y = -150;
	private static final int BRIDGE_SPEED_X = 100;

	private static final int CANDY_WIDTH = 50;
	private static final int CANDY_HEIGHT = 50;
	private static final int BRIDGE_CANDY_MARGIN = 2;
	
	private static final int FENCE_HEIGHT = 50;
	private static final int FENCE_WIDTH = BRIDGE_HEIGHT;
	private static final int BRIDGE_FENCE_MARGIN = 0;

	private static final int VULTURE_WIDTH = 75;
	private static final int VULTURE_HEIGHT = 75;
	private static final int VULTURE_SPEED = 210;
	
	private static final int BRIDGE_NOF_HOLE_SNAPS = 4;

	private static final int[][] BRIDGE_SNAP_INDEXES = new int[][]
	{
		{ 0, BRIDGE_NOF_HOLE_SNAPS },
		{ 0, BRIDGE_NOF_HOLE_SNAPS / 2 },
		{ BRIDGE_NOF_HOLE_SNAPS / 2, BRIDGE_NOF_HOLE_SNAPS }
	};

	private GameApplication mGameApplication = null;
	private GameEngine mGameEngine = null;
	private IGameContext mGameContext = null;

    private ImageSprite mGhostSprite = null;
	
	private int mSnapIndexSelector = 0;
	
	private CollisionSet mCandyCollisionSet = null;
	private CollisionSet mGhostCollisionSet = null;
	
	private RightBridgeCache mRightBridgeCache = null;
	private LeftBridgeCache mLeftBridgeCache = null;
	private FenceCache mFenceCache = null;
	private VultureCache mVultureCache = null;
	
	private ArrayList<GraphicSprite> mCandyCache = new ArrayList<GraphicSprite>();

    private Point mTmpPosition = Point.Zero.clone();
    private Vector2 mTmpVector = Vector2.Zero.clone();
    
	public DropBallView(Context context)
	{
		super(context);
	}

	public DropBallView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}
	
	public DropBallView(Context context, AttributeSet attrs, int defStyle)
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
	
    private void registerObjects(IGameContext gameContext)
    {
        gameContext.getSpriteFactory().registerSprite("Image", ImageSprite.class);
        gameContext.getSpriteFactory().registerSprite("Graphic", GraphicSprite.class);
    
        gameContext.getTrajectoryFactory().registerTrajectory("ConstantVelocityTrajectory", MovementTrajectory.class);
        gameContext.getTrajectoryFactory().registerTrajectory("KeyControlledTrajectory", KeyControlledTrajectory.class);
        gameContext.getTrajectoryFactory().registerTrajectory("OrientationControlledTrajectory", OrientationControlledTrajectory.class);
        gameContext.getTrajectoryFactory().registerTrajectory("TouchSlideTrajectory", TouchSlideTrajectory.class);
        
        gameContext.getBackgroundFactory().registerBackground("Color", ColorBackground.class);
        gameContext.getBackgroundFactory().registerBackground("Image", ImageBackground.class);
    }
    
    private void loadResources(IGameContext gameContext)
    {
		gameContext.getSoundManager().addSound(com.example.explorerapp.R.raw.boing_rebound_01);
    }

    private BoundsLimits createBallBoundsLimits()
    {
    	return new BoundsLimits(0, getWidth(), 0, getHeight());
    }
    
    private ImageSprite createGhostSprite()
    {
    	ImageSprite imageSprite = (ImageSprite) mGameContext.getSpriteFactory().createSprite("Image");
        
        imageSprite.setZOrder(DrawableZOrder.SPRITE_LAYER_2);
        imageSprite.setCollisionSet(mGhostCollisionSet);
        
        ImageDrawer drawer = new ImageDrawer(mGameContext);
        drawer.setBitmapResource(R.raw.ghost_0, GHOST_WIDTH, GHOST_HEIGHT, BitmapCollisionBounds.RECT_UPPER_LEFT);
        imageSprite.addImageDrawer(drawer);

        drawer = new ImageDrawer(mGameContext);
        drawer.setBitmapResource(R.raw.ghost_1, GHOST_WIDTH, GHOST_HEIGHT, BitmapCollisionBounds.RECT_UPPER_LEFT);
        imageSprite.addImageDrawer(drawer);
        
        drawer = new ImageDrawer(mGameContext);
        drawer.setBitmapResource(R.raw.ghost_2, GHOST_WIDTH, GHOST_HEIGHT, BitmapCollisionBounds.RECT_UPPER_LEFT);
        imageSprite.addImageDrawer(drawer);

        drawer = new ImageDrawer(mGameContext);
        drawer.setBitmapResource(R.raw.ghost_3, GHOST_WIDTH, GHOST_HEIGHT, BitmapCollisionBounds.RECT_UPPER_LEFT);
        imageSprite.addImageDrawer(drawer);
        
        drawer = new ImageDrawer(mGameContext);
        drawer.setBitmapResource(R.raw.ghost_5, GHOST_WIDTH, GHOST_HEIGHT, BitmapCollisionBounds.RECT_UPPER_LEFT);
        imageSprite.addImageDrawer(drawer);
        
        drawer = new ImageDrawer(mGameContext);
        drawer.setBitmapResource(R.raw.ghost_6, GHOST_WIDTH, GHOST_HEIGHT, BitmapCollisionBounds.RECT_UPPER_LEFT);
        imageSprite.addImageDrawer(drawer);

        drawer = new ImageDrawer(mGameContext);
        drawer.setBitmapResource(R.raw.ghost_7, GHOST_WIDTH, GHOST_HEIGHT, BitmapCollisionBounds.RECT_UPPER_LEFT);
        imageSprite.addImageDrawer(drawer);

        drawer = new ImageDrawer(mGameContext);
        drawer.setBitmapResource(R.raw.ghost_8, GHOST_WIDTH, GHOST_HEIGHT, BitmapCollisionBounds.RECT_UPPER_LEFT);
        imageSprite.addImageDrawer(drawer);

        drawer = new ImageDrawer(mGameContext);
        drawer.setBitmapResource(R.raw.ghost_9, GHOST_WIDTH, GHOST_HEIGHT, BitmapCollisionBounds.RECT_UPPER_LEFT);
        imageSprite.addImageDrawer(drawer);

        ImageDrawerAnimator ghostAnimator = new ImageDrawerAnimator();

        int drawDelay = 300;
        ImageDrawerAnimator.IAnimationAction[] eyeLeftRightAnimation = 
    	{
        	new ImageDrawerAnimator.DrawAction(2, drawDelay),
        	new ImageDrawerAnimator.DrawAction(3, drawDelay),
        	new ImageDrawerAnimator.DrawAction(4, drawDelay),
        	new ImageDrawerAnimator.DrawAction(1, drawDelay),
        	new ImageDrawerAnimator.DrawAction(5, drawDelay),
        	new ImageDrawerAnimator.DrawAction(6, drawDelay),
        	new ImageDrawerAnimator.DrawAction(7, drawDelay),
        	new ImageDrawerAnimator.DrawAction(8, drawDelay),
        	new ImageDrawerAnimator.DrawAction(7, drawDelay),
        	new ImageDrawerAnimator.DrawAction(6, drawDelay),
        	new ImageDrawerAnimator.DrawAction(5, drawDelay),
        	new ImageDrawerAnimator.DrawAction(1, drawDelay),
        	new ImageDrawerAnimator.DrawAction(4, drawDelay),
        	new ImageDrawerAnimator.DrawAction(3, drawDelay),
        	new ImageDrawerAnimator.DrawAction(2, drawDelay),
        	new ImageDrawerAnimator.RepeatAction(100, 1)
    	};

        ghostAnimator.addAnimation(eyeLeftRightAnimation);
        ghostAnimator.activateAnimation(0);
        
        imageSprite.setImageAnimator(ghostAnimator);
        
        
//        ((CollisionBoundingBox) imageSprite.getCollisionBounds()).setMargins(GHOST_BOUNCE_MARGIN, GHOST_BOUNCE_MARGIN, GHOST_BOUNCE_MARGIN, GHOST_BOUNCE_MARGIN);
//        ((CollisionBoundingBox) imageSprite.getCollisionBounds()).setMargins(GHOST_BOUNCE_MARGIN, 0, GHOST_BOUNCE_MARGIN, GHOST_BOUNCE_MARGIN);
        
        return imageSprite;
    }
    
    private ISprite createBallSprite()
    {
    	ImageSprite imageSprite = (ImageSprite) mGameContext.getSpriteFactory().createSprite("Image");
        
        ImageDrawer drawer = new ImageDrawer(mGameContext);
        drawer.setBitmapResource(R.raw.ball_0, GHOST_WIDTH, GHOST_HEIGHT, BitmapCollisionBounds.CIRCLE);
        imageSprite.addImageDrawer(drawer);
        
        imageSprite.setZOrder(DrawableZOrder.SPRITE_LAYER_2);
        imageSprite.setCollisionSet(mGhostCollisionSet);

        return imageSprite;
    }

    private ISpriteOutOfBoundsListener createBallBoundsListener()
    {
    	return new ISpriteOutOfBoundsListener()
	    {
			@Override
			public boolean onOutOfBounds(ISprite sprite, OutOfBoundsEvent event)
			{
				boolean handled = false;
	
				switch (event.limitReached)
				{
					case BOUNDS_LIMIT_TOP :
						handled = true;
						break;
	
					case BOUNDS_LIMIT_BOTTOM :
						break;
						
					case BOUNDS_LIMIT_LEFT:
	
						if (event.isOutOfBounds(sprite.getDimensions().getWidth()))
						{
							sprite.getTrajectory().setInitialPositionX(getWidth() + GHOST_WIDTH / 2);
						}
						
						handled = true;
						
						break;
						
					case BOUNDS_LIMIT_RIGHT :
						
						if (event.isOutOfBounds(sprite.getDimensions().getWidth()))
						{
							sprite.getTrajectory().setInitialPositionX(-GHOST_WIDTH / 2);
						}
						
						handled = true;
						
						break;
				}
				
				return handled;
			}
	    };
    }
    
    private ISpriteOutOfBoundsListener createGhostBoundsListener()
    {
    	return new ISpriteOutOfBoundsListener()
	    {
			@Override
			public boolean onOutOfBounds(ISprite sprite, OutOfBoundsEvent event)
			{
				boolean handled = false;
	
				switch (event.limitReached)
				{
					case BOUNDS_LIMIT_TOP :
						handled = true;
						break;
	
					case BOUNDS_LIMIT_BOTTOM :
						break;
						
					case BOUNDS_LIMIT_LEFT:
	
						if (event.isOutOfBounds(sprite.getDimensions().getWidth()))
						{
							sprite.getTrajectory().setInitialPositionX(getWidth());
						}
						
						handled = true;
						
						break;
						
					case BOUNDS_LIMIT_RIGHT :
						
						if (event.isOutOfBounds(sprite.getDimensions().getWidth()))
						{
							sprite.getTrajectory().setInitialPositionX(-GHOST_WIDTH);
						}
						
						handled = true;
						
						break;
				}
				
				return handled;
			}
	    };
    }

    private void createGhost(int y, IGameEngine gameEngine, IGameContext gameContext)
    {
//        ISprite sprite = createBallSprite();
        ImageSprite sprite = createGhostSprite();
        
        mGhostSprite = sprite;
        
        OrientationControlledTrajectory trajectory = (OrientationControlledTrajectory) gameContext.getTrajectoryFactory().createTrajectory("OrientationControlledTrajectory", sprite);
        trajectory.setAxis(OrientationAxis.X_AND_Y);
        trajectory.setXDirections(OrientationDirections.BOTH);
        trajectory.setYDirections(OrientationDirections.POSITIVE);
        
        trajectory.setOrientationMode(OrientationMode.SPEED);
        trajectory.setSpeedGain(GHOST_SPEED_GAIN_X, GHOST_SPEED_GAIN_Y);
        trajectory.setPositionLimits(createBallBoundsLimits());
   
        SpriteCompositeAction compositeAction = new SpriteCompositeAction();
        
        SpriteStickAction spriteStickAction = new SpriteStickAction();
        spriteStickAction.setStickDirection(StickDirection.NORMAL_DIRECTION);
        
        compositeAction.addAction(spriteStickAction);
        
        ((ICollisionSprite) sprite).setCollisionAction(compositeAction);

//        sprite.setOutOfBoundsListener(createBallBoundsListener());
        sprite.setOutOfBoundsListener(createGhostBoundsListener());
        
        mTmpVector.X = 0;
        mTmpVector.Y = 0;
        sprite.setInitialSpeed(mTmpVector);
        
        mTmpPosition.X = getWidth() / 2;
        mTmpPosition.Y = y - GHOST_HEIGHT;
//        mTmpPosition.Y = y - GHOST_HEIGHT / 2;
        sprite.setInitialPosition(mTmpPosition);
            
        gameContext.getCollisionManager().add(sprite.getCollisionObject());
        gameEngine.addComponent(sprite);
    }
    
    private ISprite createCandy(int resourceId)
    {
        ISprite sprite = mGameContext.getSpriteFactory().createSprite("Image");
        
        ImageSprite imageSprite = (ImageSprite) sprite;
        
        ImageDrawer drawer = new ImageDrawer(mGameContext);
        drawer.setBitmapResource(resourceId, CANDY_WIDTH, CANDY_HEIGHT, BitmapCollisionBounds.RECT_UPPER_LEFT);
        imageSprite.addImageDrawer(drawer);

        imageSprite.setCollisionSet(mCandyCollisionSet);

        ITrajectory trajectory = mGameContext.getTrajectoryFactory().createTrajectory("ConstantVelocityTrajectory", sprite);
        trajectory.setPositionLimits(new GraphicsViewLimits(mGameContext));

        SpriteTerminateAction terminateAction = new SpriteTerminateAction(mGameContext);
        
        terminateAction.setTerminationListener(new ITerminateSpriteListener()
        {
			@Override
			public void onSpriteTermination(ISprite sprite)
			{
			}
        });
        
        imageSprite.setCollisionAction(terminateAction);
        
    	sprite.setOutOfBoundsListener(new CandyOutOfBoundsListener());
    	
        return sprite;
    }

    private void createBridge()
	{
		createBridge(getHeight(), mGameEngine);
	}

    private int getHoleIndex()
    {
    	mSnapIndexSelector++;
    	
    	if (mSnapIndexSelector >= BRIDGE_SNAP_INDEXES.length)
    	{
    		mSnapIndexSelector = 0;
    	}
    	
    	return AMath.randomInt(BRIDGE_SNAP_INDEXES[mSnapIndexSelector][0], BRIDGE_SNAP_INDEXES[mSnapIndexSelector][1]);
    }
    
	private void createBridge(int y, IGameEngine gameEngine)
	{
		boolean createCandy = isCandyNeeded();
		boolean createFence = isFenceNeeded();
		boolean createVulture = isVultureNeeded();

		int snapWidth = (getWidth() - 2 * BRIDGE_VISIBLE_MIN_WIDTH - BRIDGE_HOLE_WIDTH) / BRIDGE_NOF_HOLE_SNAPS;

		int visibleBridgeWidthLeft = getHoleIndex() * snapWidth + BRIDGE_VISIBLE_MIN_WIDTH;
		int visibleBridgeWidthRight = getWidth() - visibleBridgeWidthLeft - BRIDGE_HOLE_WIDTH;

		int bridge_speed_x = 0;
		
		if (isBridgeMovingHorizontal() && !createCandy && !createFence)
		{
			bridge_speed_x = BRIDGE_SPEED_X;
		}
		
		ImageSprite sprite = mLeftBridgeCache.get();
		
        mTmpPosition.X = visibleBridgeWidthLeft - BRIDGE_WIDTH;
        mTmpPosition.Y = y;
        
        sprite.setInitialPosition(mTmpPosition);
        
        mTmpVector.X = bridge_speed_x;
        mTmpVector.Y = BRIDGE_SPEED_Y;
        
        sprite.setInitialSpeed(mTmpVector);

        mGameContext.getCollisionManager().add(sprite.getCollisionObject());
		mGameEngine.addComponent(sprite);
		
		sprite = mRightBridgeCache.get();
		
        mTmpPosition.X = visibleBridgeWidthLeft + BRIDGE_HOLE_WIDTH;
        mTmpPosition.Y = y;
        
        sprite.setInitialPosition(mTmpPosition);
        
        mTmpVector.X = bridge_speed_x;
        mTmpVector.Y = BRIDGE_SPEED_Y;
        
        sprite.setInitialSpeed(mTmpVector);

        mGameContext.getCollisionManager().add(sprite.getCollisionObject());
		mGameEngine.addComponent(sprite);

		if (createCandy)
		{
			createCandy(y, visibleBridgeWidthLeft, visibleBridgeWidthRight);
		}
		
		if (createFence)
		{
			createFence(y, visibleBridgeWidthLeft, visibleBridgeWidthRight);
		}
		
		if (createFence)
		{
			createFence(y, visibleBridgeWidthLeft, visibleBridgeWidthRight);
		}

		if (createVulture)
		{
			createVulture();
		}
	}

	private boolean isBridgeMovingHorizontal()
	{
		boolean move = false;
				
		if (mLeftBridgeCache.nofDestructedBridges >= 10)
		{
			move = (mLeftBridgeCache.nofDestructedBridges % 7) == 0;
		}
		
		return move;
	}
	
	private boolean isVultureNeeded()
	{
		boolean vulture = false;
				
		if (mLeftBridgeCache.nofDestructedBridges >= 10)
		{
			vulture = (mLeftBridgeCache.nofDestructedBridges % 12) == 0;
		}
		
		return vulture;
	}
	
	private boolean isCandyNeeded()
	{
		boolean needCandy = false;
				
		if (mLeftBridgeCache.nofDestructedBridges >= 10)
		{
			needCandy = (mLeftBridgeCache.nofDestructedBridges % 8) == 0;
		}
		
		return needCandy;
	}
	
	private boolean isFenceNeeded()
	{
		boolean needFence = false;
				
		if (mLeftBridgeCache.nofDestructedBridges >= 10)
		{
			needFence = (mLeftBridgeCache.nofDestructedBridges % 5) == 0;
		}
		
		return needFence;
	}
	
	private void createCandy(int y, int bridgeWidthOne, int bridgeWidthTwo)
	{
		ISprite sprite = createCandy(R.raw.mummy_0);
		
		if (bridgeWidthOne > bridgeWidthTwo)
		{
			mTmpPosition.X = bridgeWidthOne - 100;
			mTmpPosition.Y = y - CANDY_HEIGHT - BRIDGE_CANDY_MARGIN;
	        
			sprite.setInitialPosition(mTmpPosition);
			
	        mTmpVector.X = 0;
	        mTmpVector.Y = BRIDGE_SPEED_Y;
	        
	        sprite.setInitialSpeed(mTmpVector);
		}
		else
		{
			mTmpPosition.X = bridgeWidthOne + BRIDGE_HOLE_WIDTH + 100;
			mTmpPosition.Y = y - CANDY_HEIGHT - BRIDGE_CANDY_MARGIN;
	        
			sprite.setInitialPosition(mTmpPosition);
			
	        mTmpVector.X = 0;
	        mTmpVector.Y = BRIDGE_SPEED_Y;
	        
	        sprite.setInitialSpeed(mTmpVector);
		}
		
        mGameContext.getCollisionManager().add(sprite.getCollisionObject());
		mGameEngine.addComponent(sprite);
	}
	
	private void createFence(int y, int bridgeWidthOne, int bridgeWidthTwo)
	{
		ImageSprite fence = mFenceCache.get();
		
		if (bridgeWidthOne > bridgeWidthTwo)
		{
			mTmpPosition.X = bridgeWidthOne - FENCE_WIDTH;
			mTmpPosition.Y = y - FENCE_HEIGHT - BRIDGE_FENCE_MARGIN;
	        
			fence.setInitialPosition(mTmpPosition);
			
	        mTmpVector.X = 0;
	        mTmpVector.Y = BRIDGE_SPEED_Y;
	        
	        fence.setInitialSpeed(mTmpVector);
		}
		else
		{
			mTmpPosition.X = bridgeWidthOne + BRIDGE_HOLE_WIDTH;
			mTmpPosition.Y = y - FENCE_HEIGHT - BRIDGE_FENCE_MARGIN;
	        
			fence.setInitialPosition(mTmpPosition);
			
	        mTmpVector.X = 0;
	        mTmpVector.Y = BRIDGE_SPEED_Y;
	        
	        fence.setInitialSpeed(mTmpVector);
		}
		
        mGameContext.getCollisionManager().add(fence.getCollisionObject());
		mGameEngine.addComponent(fence);
	}
	
	private void createVulture()
	{
		ImageSprite vulture = mVultureCache.get();
		
        mTmpVector.X = VULTURE_SPEED;
        
        vulture.setInitialSpeed(mTmpVector);
		
        mGameContext.getCollisionManager().add(vulture.getCollisionObject());
		mGameEngine.addComponent(vulture);
	}
	
	private void createInitialBridges(IGameEngine gameEngine, IGameContext gameContext)
	{
		int nofBridges = getHeight() / (BRIDGE_HEIGHT + BRIDGE_SEPARATION);
		
		int bridgeSeparation = (getHeight() - nofBridges * BRIDGE_HEIGHT) / nofBridges;
		
		int yPos = BRIDGE_HEIGHT + bridgeSeparation;
		
		for (int i = 0; i < nofBridges; i++)
		{
			createBridge(yPos, gameEngine);

			yPos += (BRIDGE_HEIGHT + bridgeSeparation);
		}
		
		createGhost(2 * (BRIDGE_HEIGHT + bridgeSeparation), gameEngine, gameContext);
	}
	
	private void createBackground(IGameEngine gameEngine)
	{
//	    ColorBackground background = (ColorBackground) mGameContext.getBackgroundFactory().createBackground("Color");
//	    background.setColor(Color.BLUE);
	    
		se.federspiel.android.backgrounds.trajectories.ConstantVelocityTrajectory trajectory = 
				new se.federspiel.android.backgrounds.trajectories.ConstantVelocityTrajectory();
		trajectory.setSpeed(new Vector2(0, -100));
		
	    ImageBackground background = (ImageBackground) mGameContext.getBackgroundFactory().createBackground("Image");
	    background.setBitmapResource(BACKGROUND_IMAGE_RES);
	    background.setScrollDirection(ScrollDirections.SCROLL_DIRECTION_Y);
	    background.setTrajectory(trajectory);

		gameEngine.addBackground(background);
	}

	private void setupListeners(final IGameContext gameContext)
	{
		gameContext.getUserInputManager().setOnClickListener(new IOnClickListener()
		{
			@Override
			public boolean onClick(TouchClickEvent event)
			{
				gameContext.getGameEngine().pause();

				UITapDialog dialog = UIFactory.getTapDialog();
				
				dialog.setInfoText(R.string.str_paused);
				dialog.setTapText(R.string.str_tapToStart);
				
				dialog.setBackgroundBitmap(R.raw.pergament_0);

				dialog.setOnClickListener(new UIIOnClickListener()
				{
					@Override
					public boolean onClick(UIInputComponent component,
							TouchClickEvent event)
					{
						((UIDialog) component).close();
						
						GameApplication.getGameEngine().unPause();
						
						return true;
					}
				});
				
				dialog.show();
				
				return false;
			}
		});
	}

	private void createCaches(IGameContext gameContext)
	{
		gameContext.getCollisionManager().enableCollisionSets(true);

		CollisionSet bridgeCollisionSet = gameContext.getCollisionManager().createCollisionSet();
		bridgeCollisionSet.setCollisionsWithinSet(false);
		
		CollisionSet fenceCollisionSet = gameContext.getCollisionManager().createCollisionSet();
		fenceCollisionSet.setCollisionsWithinSet(false);
		
		CollisionSet vultureCollisionSet = gameContext.getCollisionManager().createCollisionSet();
		vultureCollisionSet.setCollisionsWithinSet(false);

		mCandyCollisionSet = gameContext.getCollisionManager().createCollisionSet();
		mCandyCollisionSet.setCollisionsWithinSet(false);
		
		mGhostCollisionSet = gameContext.getCollisionManager().createCollisionSet();
		mGhostCollisionSet.setCollisionsWithinSet(false);
		
		mGhostCollisionSet.joinSet(fenceCollisionSet);
		mGhostCollisionSet.joinSet(bridgeCollisionSet);
		mGhostCollisionSet.joinSet(vultureCollisionSet);
		mGhostCollisionSet.joinSet(mCandyCollisionSet);
		
		mRightBridgeCache = new RightBridgeCache(gameContext, bridgeCollisionSet);
		mLeftBridgeCache = new LeftBridgeCache(gameContext, bridgeCollisionSet);
		mFenceCache = new FenceCache(gameContext, fenceCollisionSet);
		mVultureCache = new VultureCache(gameContext, vultureCollisionSet);
	}
	
    private void initGame()
	{
    	mGameApplication = new GameApplication(this);

		mGameEngine = GameApplication.getGameEngine();
		mGameContext = mGameEngine.getGameContext();
		
		mGameContext.getCollisionManager().setCollisionEvaluator(new CollisionEvaluatorLibrary.RepeatNearestFoundCollisionSelectorEvaluator());
		
		createCaches(mGameContext);

		setupListeners(mGameContext);
		
		registerObjects(mGameContext);
		loadResources(mGameContext);

    	createBackground(mGameEngine);
    	
		createInitialBridges(mGameEngine, mGameContext);
	}

    private class CandyOutOfBoundsListener implements ISpriteOutOfBoundsListener
    {
    	@Override
		public boolean onOutOfBounds(ISprite sprite, OutOfBoundsEvent event)
		{
			if (event.limitReached == OutOfBoundsEvent.OutOfBoundsLimit.BOUNDS_LIMIT_TOP)
			{
				if (event.isOutOfBounds(sprite.getDimensions().getHeight()))
				{
					mGameContext.getCollisionManager().remove(sprite.getCollisionObject());
					mGameContext.getGameEngine().removeComponent(sprite);
					
//					mCandyCache.add((GraphicSprite) sprite);
				}
			}
			
			return true;
		}
    }

    private class VultureCache extends ObjectCache<ImageSprite>
    {
    	private IGameContext mGameContext = null;
    	
    	private CollisionSet mCollisionSet = null;
    	
    	public VultureCache(IGameContext gameContext, CollisionSet collisionSet)
    	{
    		mGameContext = gameContext;
    		mCollisionSet = collisionSet;
    	}
    
    	@Override
		protected ImageSprite createObject()
    	{
	        ImageSprite sprite = (ImageSprite) mGameContext.getSpriteFactory().createSprite("Image");
	        
            ImageDrawer drawer = new ImageDrawer(mGameContext);
            drawer.setBitmapResource(R.raw.devil_0, VULTURE_WIDTH, VULTURE_HEIGHT, BitmapCollisionBounds.RECT_UPPER_LEFT);
            sprite.addImageDrawer(drawer);

            sprite.setZOrder(DrawableZOrder.SPRITE_LAYER_2);
            
            sprite.setCollisionSet(mCollisionSet);
            
			BezierPathTrajectory trajectory = new BezierPathTrajectory(mGameContext, sprite);
	        trajectory.setPositionLimits(new GraphicsViewLimits(mGameContext));
		
	        int width = mGameContext.getGraphicBounds().getWidth();
	        int height = mGameContext.getGraphicBounds().getHeight();
	        
//			Point[] path = new Point[]	
//			{
//				new Point(-VULTURE_WIDTH, height - 200),
//				new Point(500, height - 200),
//				new Point(width - 300, height - 300),
//				new Point(300, 200),
//				new Point(300, 200),
//				new Point(width - 500, 100),
//				new Point(width + VULTURE_WIDTH, 100)
//			};
			
	    	Point[] path = new Point[]	
			{
				new Point(0, height - 200),
				new Point(500, height - 300),
				new Point(width - 500, 200),
				new Point(width, 100)
			};
	    	
			trajectory.definePath(Direction.FORWARD, 1, path, 0.4f);
			
			sprite.setTrajectory(trajectory);
			
			SpriteNonAdjustAction spriteAction = new SpriteNonAdjustAction();
	        sprite.setCollisionAction(spriteAction);
	        sprite.setCollisionSet(mCollisionSet);
	        
	    	sprite.setOutOfBoundsListener(new VultureOutOfBoundsListener());
	        
	        return sprite;
    	}
    	
        private class VultureOutOfBoundsListener implements ISpriteOutOfBoundsListener
        {
        	@Override
    		public boolean onOutOfBounds(ISprite sprite, OutOfBoundsEvent event)
    		{
    			if (event.limitReached == OutOfBoundsEvent.OutOfBoundsLimit.BOUNDS_LIMIT_RIGHT)
    			{
    				if (event.isOutOfBounds(sprite.getDimensions().getWidth()))
    				{
    					mGameContext.getCollisionManager().remove(sprite.getCollisionObject());
    					mGameContext.getGameEngine().removeComponent(sprite);
    					
    					VultureCache.this.add((ImageSprite) sprite);
    				}
    			}
    			
    			return true;
    		}
        }
    }
    
    private class FenceCache extends ObjectCache<ImageSprite>
    {
    	private IGameContext mGameContext = null;
    	
    	private CollisionSet mCollisionSet = null;
    	
    	public FenceCache(IGameContext gameContext, CollisionSet collisionSet)
    	{
    		mGameContext = gameContext;
    		mCollisionSet = collisionSet;
    	}
    
    	@Override
		protected ImageSprite createObject()
    	{
	        ImageSprite sprite = (ImageSprite) mGameContext.getSpriteFactory().createSprite("Image");
	        
	        ImageDrawer drawer = new ImageDrawer(mGameContext);
	        drawer.setBitmapResource(FENCE_IMAGE_RES, FENCE_WIDTH, FENCE_HEIGHT, ScaleOperator.Tile, BitmapCollisionBounds.RECT_UPPER_LEFT);

	        sprite.addImageDrawer(drawer);
	        
	        SpriteNonAdjustAction spriteAction = new SpriteNonAdjustAction();
	        sprite.setCollisionAction(spriteAction);
	        sprite.setCollisionSet(mCollisionSet);
	        
	        ITrajectory trajectory = mGameContext.getTrajectoryFactory().createTrajectory("ConstantVelocityTrajectory", sprite);
	        trajectory.setPositionLimits(new GraphicsViewLimits(mGameContext));
	
	    	sprite.setOutOfBoundsListener(new FenceOutOfBoundsListener());
	        
	        return sprite;
    	}
    	
        private class FenceOutOfBoundsListener implements ISpriteOutOfBoundsListener
        {
        	@Override
    		public boolean onOutOfBounds(ISprite sprite, OutOfBoundsEvent event)
    		{
    			if (event.limitReached == OutOfBoundsEvent.OutOfBoundsLimit.BOUNDS_LIMIT_TOP)
    			{
    				if (event.isOutOfBounds(sprite.getDimensions().getHeight()))
    				{
    					mGameContext.getCollisionManager().remove(sprite.getCollisionObject());
    					mGameContext.getGameEngine().removeComponent(sprite);
    					
    					FenceCache.this.add((ImageSprite) sprite);
    				}
    			}
    			
    			return true;
    		}
        }
    }
    
    private class LeftBridgeCache extends ObjectCache<ImageSprite>
    {
    	private IGameContext mGameContext = null;
    	
    	public int nofDestructedBridges = 0;
    	
    	private CollisionSet mCollisionSet = null;
    	
    	public LeftBridgeCache(IGameContext gameContext, CollisionSet collisionSet)
    	{
    		mGameContext = gameContext;
    		mCollisionSet = collisionSet;
    	}
    	
    	@Override
		protected ImageSprite createObject()
		{
	        ImageSprite sprite = (ImageSprite) mGameContext.getSpriteFactory().createSprite("Image");
	        
	        ImageDrawer drawer = new ImageDrawer(mGameContext);
	        drawer.setBitmapResource(BRIDGE_IMAGE_RES, BRIDGE_WIDTH, BRIDGE_HEIGHT, ScaleOperator.Tile, BitmapCollisionBounds.RECT_UPPER_LEFT);
	        
	        sprite.addImageDrawer(drawer);
	        
	        SpriteNonAdjustAction spriteAction = new SpriteNonAdjustAction();
	        sprite.setCollisionAction(spriteAction);
	        sprite.setCollisionSet(mCollisionSet);
	        
	        ITrajectory trajectory = mGameContext.getTrajectoryFactory().createTrajectory("ConstantVelocityTrajectory", sprite);
	        trajectory.setPositionLimits(new BoundsLimits(LEFT_BRIDGE_MIN_X, LEFT_BRIDGE_MAX_X, getTop(), getBottom()));
    
        	sprite.setOutOfBoundsListener(new LeftBrickOutOfBoundsListener());
	        
	        return sprite;
		}
    	
        private class LeftBrickOutOfBoundsListener implements ISpriteOutOfBoundsListener
        {
        	public boolean mCreateNewBridge = true;
        	
        	@Override
    		public boolean onOutOfBounds(ISprite sprite, OutOfBoundsEvent event)
    		{
				boolean handled = false;
				
				switch (event.limitReached)
				{
					case BOUNDS_LIMIT_TOP :
						
	    				if (mCreateNewBridge)
	    				{
	    					createBridge();
	    					
	    					mCreateNewBridge = false;
	    				}
	    				
	    				if (event.isOutOfBounds(sprite.getDimensions().getHeight()))
	    				{
	    					mGameContext.getCollisionManager().remove(sprite.getCollisionObject());
	    					
							mGameContext.getGameEngine().removeComponent(sprite);

	    					LeftBridgeCache.this.add((ImageSprite) sprite);

	    					nofDestructedBridges++;
	    					
	    					mCreateNewBridge = true;
	    				}
		    			
						handled = true;
						
						break;
	
					case BOUNDS_LIMIT_BOTTOM :
						handled = true;
						break;
						
					case BOUNDS_LIMIT_LEFT:
	
						if (sprite.getSpeed().X == 0)
						{
							handled = true;
						}
						else
						{
							handled = false;
						}
						
						break;
						
					case BOUNDS_LIMIT_RIGHT :
						
						if (sprite.getSpeed().X == 0)
						{
							handled = true;
						}
						else
						{
							handled = false;
						}
						
						break;
				}
				
				return handled;
    		}
        }
    }
    
    private class RightBridgeCache extends ObjectCache<ImageSprite>
    {
    	private IGameContext mGameContext = null;
    	private CollisionSet mCollisionSet = null;
    	
    	public RightBridgeCache(IGameContext gameContext, CollisionSet collisionSet)
    	{
    		mGameContext = gameContext;
    		mCollisionSet = collisionSet;
    	}
    	
    	@Override
		protected ImageSprite createObject()
		{
	        ImageSprite sprite = (ImageSprite) mGameContext.getSpriteFactory().createSprite("Image");
	        
	        ImageDrawer drawer = new ImageDrawer(mGameContext);
	        drawer.setBitmapResource(BRIDGE_IMAGE_RES, BRIDGE_WIDTH, BRIDGE_HEIGHT, ScaleOperator.Tile, BitmapCollisionBounds.RECT_UPPER_LEFT);

	        sprite.addImageDrawer(drawer);
	        
	        SpriteNonAdjustAction spriteAction = new SpriteNonAdjustAction();
	        sprite.setCollisionAction(spriteAction);
	        sprite.setCollisionSet(mCollisionSet);
	        
	        ITrajectory trajectory = mGameContext.getTrajectoryFactory().createTrajectory("ConstantVelocityTrajectory", sprite);
	        trajectory.setPositionLimits(new BoundsLimits(RIGHT_BRIDGE_MIN_X, RIGHT_BRIDGE_MAX_X, getTop(), getBottom()));

            sprite.setOutOfBoundsListener(new RightBrickOutOfBoundsListener());
	        
	        return sprite;
		}
    	
        private class RightBrickOutOfBoundsListener implements ISpriteOutOfBoundsListener
        {
        	@Override
    		public boolean onOutOfBounds(ISprite sprite, OutOfBoundsEvent event)
    		{
				boolean handled = false;
				
				switch (event.limitReached)
				{
					case BOUNDS_LIMIT_TOP :
						
	    				if (event.isOutOfBounds(sprite.getDimensions().getHeight()))
	    				{
	    					mGameContext.getCollisionManager().remove(sprite.getCollisionObject());
	    					
	    					mGameContext.getGameEngine().removeComponent(sprite);

	    					RightBridgeCache.this.add((ImageSprite) sprite);
	    				}
	    				
						handled = true;
						
						break;
	
					case BOUNDS_LIMIT_BOTTOM :
						handled = true;
						break;
						
					case BOUNDS_LIMIT_LEFT:
						
						if (sprite.getSpeed().X == 0)
						{
							handled = true;
						}
						else
						{
							handled = false;
						}
						
						break;
						
					case BOUNDS_LIMIT_RIGHT :
						
						if (sprite.getSpeed().X == 0)
						{
							handled = true;
						}
						else
						{
							handled = false;
						}
						
						break;
				}
				
				return handled;
			}
        }
    }
}
