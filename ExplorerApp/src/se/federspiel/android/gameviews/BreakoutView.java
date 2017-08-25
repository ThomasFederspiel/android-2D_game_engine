package se.federspiel.android.gameviews;

import java.util.ArrayList;

import se.federspiel.android.agraphics.CanvasSurfaceView;
import se.federspiel.android.agraphics.DrawThread.IDrawer;
import se.federspiel.android.backgrounds.ColorBackground;
import se.federspiel.android.game.GameApplication;
import se.federspiel.android.game.GameEngine;
import se.federspiel.android.game.GameRenderer;
import se.federspiel.android.game.collision.CollisionEvaluatorLibrary;
import se.federspiel.android.game.collision.CollisionSelectorLibrary;
import se.federspiel.android.game.collision.CollisionSet;
import se.federspiel.android.game.geometry.Dimensions;
import se.federspiel.android.game.geometry.Point;
import se.federspiel.android.game.geometry.Vector2;
import se.federspiel.android.game.interfaces.ICollisionSprite;
import se.federspiel.android.game.interfaces.IGameContext;
import se.federspiel.android.game.interfaces.IImageSpriteDrawer.BitmapCollisionBounds;
import se.federspiel.android.game.interfaces.ISprite;
import se.federspiel.android.game.interfaces.ISprite.ISpriteOutOfBoundsListener;
import se.federspiel.android.game.interfaces.ITrajectory;
import se.federspiel.android.game.interfaces.IUserInputManager.IOnClickListener;
import se.federspiel.android.game.interfaces.IUserInputManager.TouchClickEvent;
import se.federspiel.android.game.sprites.GraphicSprite;
import se.federspiel.android.game.sprites.ImageSprite;
import se.federspiel.android.game.sprites.PhysicalProperties;
import se.federspiel.android.game.sprites.actions.SpriteCompositeAction;
import se.federspiel.android.game.sprites.actions.SpriteElasticBounceAction;
import se.federspiel.android.game.sprites.actions.SpriteFrictionAction;
import se.federspiel.android.game.sprites.actions.SpriteNonAdjustAction;
import se.federspiel.android.game.sprites.actions.SpriteSoundAction;
import se.federspiel.android.game.sprites.actions.SpriteTerminateAction;
import se.federspiel.android.game.sprites.actions.SpriteTerminateAction.ITerminateSpriteListener;
import se.federspiel.android.game.sprites.drawers.BoxDrawer;
import se.federspiel.android.game.sprites.drawers.ImageDrawer;
import se.federspiel.android.game.trajectories.MovementTrajectory;
import se.federspiel.android.game.trajectories.KeyControlledTrajectory;
import se.federspiel.android.game.trajectories.OrientationControlledTrajectory;
import se.federspiel.android.game.trajectories.OrientationControlledTrajectory.OrientationAxis;
import se.federspiel.android.game.trajectories.OrientationControlledTrajectory.OrientationMode;
import se.federspiel.android.game.trajectories.limits.BoundsLimits;
import se.federspiel.android.game.trajectories.limits.OutOfBoundsEvent;
import se.federspiel.android.game.ui.UIDialog;
import se.federspiel.android.game.ui.UIFactory;
import se.federspiel.android.game.ui.UIFactory.UITapDialog;
import se.federspiel.android.game.ui.UIInputComponent;
import se.federspiel.android.game.ui.UIInputComponent.UIIOnClickListener;
import se.federspiel.android.game.ui.UILabel;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;

import com.example.explorerapp.R;

public class BreakoutView extends CanvasSurfaceView
{
	private static String NOF_BALLS_LABEL_TEXT = "Balls : ";
	private static String LEVELS_LABEL_TEXT = "Level : ";
	
	private static final int BALL_RADIUS = 15;
	private static final int BRICK_WIDTH = 40;
	private static final int BRICK_HEIGHT = 20;

	private static final int GAME_AREA_X_MARGIN = 30;
	private static final int GAME_AREA_TOP_MARGIN = 50;
	private static final int GAME_AREA_BOTTOM_MARGIN = 50;
	
	private static final int PADDLE_WIDTH = 100;
	private static final int PADDLE_HEIGHT = 20;
	private static final int PADDLE_MARGIN = 10;

	private static final float PADDLE_FRICTION = 0.45f;
	
	private static final int FRAME_WIDTH = 10;

	private static final int NOF_OF_BALLS = 5;
	
	private static final int BALL_SPEED = -250; // px/s
	private static final int BALL_X_POS = 360;
	private static final int BALL_Y_POS = 900;

	private static char BREAKABLE_BRICK_CHAR = 'b';
	private static char SOLID_BRICK_CHAR = 's';
	
	private GameApplication mGameApplication = null;
	private GameEngine mGameEngine = null;
	private IGameContext mGameContext = null;

	private TerminationListener mTerminationListener = new TerminationListener();
	
	private int mNofCurrentBalls = NOF_OF_BALLS;
	private int mCurrentLevel = 0;
	private int mNofBricksLeft = 0;
	
	private UILabel mNofBallsLabel = null;
	private UILabel mLevelNumberLabel = null;
	
   	private CollisionSelectorLibrary.GridCollisionSelector mCollisionSelector = null;

   	private ArrayList<ISprite> mSolidBricks = new ArrayList<ISprite>();
    private ISprite mPaddle = null;
    private ISprite mBall = null;

    private CollisionSet mBrickCollisionSet = null;
    private CollisionSet mPaddleCollisionSet = null;
    private CollisionSet mBallCollisionSet = null;
    
    private Point mTmpPosition = Point.Zero.clone();
    private Vector2 mTmpVector = Vector2.Zero.clone();
    
   	private static final String[] sBrickLayoutLevel1 = 
	{
   	   	"             ",
   	   	"bsbsbbbbbsbsb"
	};

   	private static final String[] sBrickLayoutLevel2 = 
	{
   	   	"bbbb     bbbb",
   	   	"  sb     bs  ",
   	   	"             ",
   	   	"    sb bs    ",
   	   	"      s      ",
   	   	" bb bbbbb bb "
	};
   	
   	private static final String[] sBrickLayoutLevel3 = 
	{
   	   	"bbbb     bbbb",
   	   	"  bs     sb  ",
   	   	"             ",
   	   	"             ",
   	   	"    bb bb    ",
   	   	"sbbsbbbbbsbbs"
	};
   	
   	private static final BrickDefinition[] sBrickDefinitions = 
	{
   		new BrickDefinition(20, 50, sBrickLayoutLevel1),
   		new BrickDefinition(20, 50, sBrickLayoutLevel2),
   		new BrickDefinition(20, 50, sBrickLayoutLevel3)
	};
   	
	public BreakoutView(Context context)
	{
		super(context);
	}

	public BreakoutView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}
	
	public BreakoutView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}

    private void createBricks(BrickDefinition brickDefinition, GameEngine gameEngine, IGameContext gameContext)
    {
    	int width = getWidth() - 2 * GAME_AREA_X_MARGIN;
    	
        createBricks(GAME_AREA_X_MARGIN, GAME_AREA_TOP_MARGIN, width, brickDefinition, gameEngine, gameContext);
    }
    
    private void createBricks(int x, int y, int width, BrickDefinition brickDefinition, GameEngine gameEngine, IGameContext gameContext)
    {
    	int nofColumns = brickDefinition.getNofColumns();
    	int nofRows = brickDefinition.getNofRows();
    	int xMargin = brickDefinition.mXMargin;
    	int yMargin = brickDefinition.mYMargin;
    	
    	int brickSeparation = (width - 2 * xMargin - (nofColumns * BRICK_WIDTH)) / (nofColumns - 1);
    	
    	mCollisionSelector.setGridProperties(x + xMargin - brickSeparation / 2,  y + yMargin - brickSeparation / 2,
    			BRICK_WIDTH + brickSeparation, BRICK_HEIGHT + brickSeparation, 
    			nofRows, nofColumns);
    	
    	mNofBricksLeft = 0;
    	
    	int yPos = y + yMargin;
    	
    	for (int row = 0; row < nofRows; row++)
    	{
        	int xPos = x + xMargin;
        	
	        for (int col = 0; col < nofColumns; col++)
	        {
	            if (brickDefinition.isBrick(row, col))
	            {
	            	boolean isSolid = brickDefinition.isSolid(row, col);
	            	
		            ISprite sprite = createBrick(isSolid, gameContext);
		            
		            if (isSolid)
		            {
		            	mSolidBricks.add(sprite);
		            }
		            else
		            {
			            SpriteTerminateAction terminateAction = new SpriteTerminateAction(gameContext);
			            terminateAction.setTerminationListener(mTerminationListener);
			            
			            ((ICollisionSprite) sprite).setCollisionAction(terminateAction);
			            
			            mNofBricksLeft++;
		            }
		            
		            sprite.setInitialPosition(new Point(xPos, yPos));
	            
		            gameContext.getCollisionManager().add(sprite.getCollisionObject());
		            gameEngine.addComponent(sprite);
	            }
	            
	            xPos += (BRICK_WIDTH + brickSeparation);
        	}
        	
            yPos += (BRICK_HEIGHT + brickSeparation);
        }
    }

    private ISprite createBrick(boolean solid, IGameContext gameContext)
    {
    	GraphicSprite sprite = (GraphicSprite) gameContext.getSpriteFactory().createSprite("Graphic");
    
	    BoxDrawer drawer = new BoxDrawer(mGameContext);
	    drawer.setDimensions(BRICK_WIDTH, BRICK_HEIGHT);
	    sprite.setGraphicDrawer(drawer);

	    sprite.setCollisionSet(mBrickCollisionSet);
	    
	    int color = Color.WHITE;
	    
	    if (solid)
	    {
	    	color = Color.RED;
	    }
	    
	    drawer.setColor(color);
	    
	    return sprite;
    }
    
    private Vector2 getBallCreateSpeed(Vector2 speed)
    {
    	speed.X = BALL_SPEED;    	// px/s
    	speed.Y = BALL_SPEED;    	// px/s

    	double xFactor = Math.random();
    	
    	if (xFactor < 0.5)
    	{
    		speed.X *= -1;
    		speed.X *= (xFactor + 0.75);
    	}
    	else
    	{
    		speed.X *= (xFactor + 0.25);
    	}
    	
    	return speed;
    }
    
    private Point getBallCreateLocation(Point position)
    {
        position.X = BALL_X_POS;
        position.Y = BALL_Y_POS;
        
        if (mPaddle != null)
        {
        	Point paddlePosition = mPaddle.getPosition();
        	
        	position.Y = ((int) paddlePosition.Y) - BALL_RADIUS - 5;
        	
        	position.X = ((int) paddlePosition.X) + PADDLE_WIDTH / 2;
        }
        
    	return position;
    }

    private void setupBallSpeedAndLocation()
    {
        Vector2 speed = getBallCreateSpeed(mTmpVector);
        
        Point position = getBallCreateLocation(mTmpPosition);

        mBall.setInitialPosition(position);
        mBall.setInitialSpeed(speed);
    }

    private ISprite createBallSprite()
    {
    	ImageSprite imageSprite = (ImageSprite) mGameContext.getSpriteFactory().createSprite("Image");
        
        ImageDrawer drawer = new ImageDrawer(mGameContext);
        drawer.setBitmapResource(R.raw.ball_0, 2 * BALL_RADIUS, 2 * BALL_RADIUS, BitmapCollisionBounds.CIRCLE);
        imageSprite.addImageDrawer(drawer);
        
        imageSprite.getPhysicalProperties().setMass(1);
        
        imageSprite.setCollisionSet(mBallCollisionSet);
        
        return imageSprite;
    }

    private void createBall(GameEngine gameEngine, IGameContext gameContext)
    {
        ISprite sprite = createBallSprite();
        
        mBall = sprite;
        
        ITrajectory trajectory = gameContext.getTrajectoryFactory().createTrajectory("ConstantVelocityTrajectory", sprite);
        trajectory.setPositionLimits(createBoundsLimits());
   
        SpriteCompositeAction compositeAction = new SpriteCompositeAction();
        
        SpriteSoundAction soundAction = new SpriteSoundAction(R.raw.boing_rebound_01, false, gameContext);
        compositeAction.addAction(soundAction);

        SpriteElasticBounceAction elasticAction = new SpriteElasticBounceAction();
        compositeAction.addAction(elasticAction);
        
        SpriteFrictionAction frictionAction = new SpriteFrictionAction();
        compositeAction.addAction(frictionAction);
        
        ((ICollisionSprite) sprite).setCollisionAction(compositeAction);

        sprite.setOutOfBoundsListener(new ISpriteOutOfBoundsListener()
        {
			@Override
			public boolean onOutOfBounds(ISprite sprite, OutOfBoundsEvent event)
			{
				boolean handled = false;
				
				if (event.limitReached == OutOfBoundsEvent.OutOfBoundsLimit.BOUNDS_LIMIT_BOTTOM)
				{
					if (event.isOutOfBounds(sprite.getDimensions().getHeight()))
					{
						mGameContext.getCollisionManager().remove(sprite.getCollisionObject());
						mGameContext.getGameEngine().removeComponent(sprite);

						mNofCurrentBalls--;

						updateUI();
						
						if (mNofCurrentBalls > 0)
						{
							createBall(mGameEngine, mGameContext);
						}
					}

					handled = true;
				}
				
				return handled;
			}
        });
        
        Vector2 speed = getBallCreateSpeed(mTmpVector);
        
        Point position = getBallCreateLocation(mTmpPosition);
        
        sprite.setInitialSpeed(speed);
        sprite.setInitialPosition(position);
            
        gameContext.getCollisionManager().add(sprite.getCollisionObject());
        gameEngine.addComponent(sprite);
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
	
    private void createPaddle(GameEngine gameEngine, IGameContext gameContext)
    {
    	GraphicSprite sprite = (GraphicSprite) mGameContext.getSpriteFactory().createSprite("Graphic");

	    mPaddle = sprite;
	    
	    BoxDrawer drawer = new BoxDrawer(mGameContext);
	    drawer.setDimensions(new Dimensions(PADDLE_WIDTH, PADDLE_HEIGHT));
	    sprite.setGraphicDrawer(drawer);

	    sprite.setCollisionSet(mPaddleCollisionSet);
	
	    PhysicalProperties properties = new PhysicalProperties();
	    
	    properties.mFriction = PADDLE_FRICTION;
	    
	    sprite.setPhysicalProperties(properties);
	    
	    int speed = 250; // px/s
	    int acceleration = 40; // px/s^2
	
/*	    ITrajectory trajectory = mGameContext.getTrajectoryFactory().createTrajectory("KeyControlledTrajectory", sprite);
	    trajectory.setInitialSpeed(new Vector2(speed, 0));
	    trajectory.setInitialAcceleration(new Vector2(acceleration, 0));
	    trajectory.setPositionLimits(new ViewportLimits(mGameContext));
*/    
	    
        OrientationControlledTrajectory trajectory = (OrientationControlledTrajectory) mGameContext.getTrajectoryFactory().createTrajectory("OrientationControlledTrajectory", sprite);

        trajectory.setOrientationMode(OrientationMode.SPEED);
        
        mTmpVector.X = 0;
        mTmpVector.Y = 0;
        trajectory.setInitialSpeed(mTmpVector);
        
        trajectory.setSpeedGain(30, 30);
        trajectory.setAxis(OrientationAxis.X_ONLY);
        trajectory.setPositionLimits(createBoundsLimits());
        
        SpriteNonAdjustAction spriteAction = new SpriteNonAdjustAction();
        ((ICollisionSprite) sprite).setCollisionAction(spriteAction);

        int xPos = (getWidth() - 2 * GAME_AREA_X_MARGIN - PADDLE_WIDTH) / 2;
        
	    sprite.setInitialPosition(new Point(xPos, getHeight() - GAME_AREA_BOTTOM_MARGIN - PADDLE_HEIGHT - PADDLE_MARGIN));
	    
	    mGameContext.getCollisionManager().add(sprite.getCollisionObject());
	    mGameEngine.addComponent(sprite);
    }

    private BoundsLimits createBoundsLimits()
    {
    	return new BoundsLimits(GAME_AREA_X_MARGIN, getWidth() - GAME_AREA_X_MARGIN,
    			GAME_AREA_TOP_MARGIN, getHeight() - GAME_AREA_BOTTOM_MARGIN);
    }
    
    private void registerObjects(IGameContext gameContext)
    {
        gameContext.getSpriteFactory().registerSprite("Graphic", GraphicSprite.class);
        gameContext.getSpriteFactory().registerSprite("Image", ImageSprite.class);

        gameContext.getTrajectoryFactory().registerTrajectory("ConstantVelocityTrajectory", MovementTrajectory.class);
        gameContext.getTrajectoryFactory().registerTrajectory("KeyControlledTrajectory", KeyControlledTrajectory.class);
        gameContext.getTrajectoryFactory().registerTrajectory("OrientationControlledTrajectory", OrientationControlledTrajectory.class);
        
        gameContext.getBackgroundFactory().registerBackground("Color", ColorBackground.class);
    }
    
    private void loadResources(IGameContext gameContext)
    {
		gameContext.getSoundManager().addSound(com.example.explorerapp.R.raw.boing_rebound_01);
    }

	private void updateUI()
	{
		mNofBallsLabel.setText(NOF_BALLS_LABEL_TEXT + mNofCurrentBalls);
		mLevelNumberLabel.setText(LEVELS_LABEL_TEXT + mCurrentLevel);
	}

	private void createUI(IGameContext gameContext)
	{
		mNofBallsLabel = new UILabel(GAME_AREA_X_MARGIN + 30, 5, 150, 10);
		gameContext.getMainWindow().addComponent(mNofBallsLabel);
		
		mLevelNumberLabel = new UILabel(getWidth() - 2 * GAME_AREA_X_MARGIN - 150, 5, 150, 10);
		gameContext.getMainWindow().addComponent(mLevelNumberLabel);
	}
	
	private void createBackground(GameEngine gameEngine)
	{
		FrameBackground background = new FrameBackground(GAME_AREA_X_MARGIN, GAME_AREA_TOP_MARGIN,
				getWidth() - 2 * GAME_AREA_X_MARGIN,
				getHeight() - GAME_AREA_TOP_MARGIN - GAME_AREA_BOTTOM_MARGIN, FRAME_WIDTH, mGameContext);
        background.setColor(Color.BLUE);
        
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

		mBrickCollisionSet = gameContext.getCollisionManager().createCollisionSet();
		mBrickCollisionSet.setCollisionsWithinSet(false);
		
		mPaddleCollisionSet = gameContext.getCollisionManager().createCollisionSet();
		
		mBallCollisionSet = gameContext.getCollisionManager().createCollisionSet();
		
		mBallCollisionSet.joinSet(mBrickCollisionSet);
		mBallCollisionSet.joinSet(mPaddleCollisionSet);
	}
	
    private void initGame()
	{
    	mGameApplication = new GameApplication(this);

		mGameEngine = GameApplication.getGameEngine();
		mGameContext = mGameEngine.getGameContext();
		
       	mCollisionSelector = new CollisionSelectorLibrary.GridCollisionSelector();
       	
		mGameContext.getCollisionManager().setCollisionEvaluator(new CollisionEvaluatorLibrary.NearestFoundCollisionSelectorEvaluator());
		mGameContext.getCollisionManager().setCollisionSelector(mCollisionSelector);
		
		registerObjects(mGameContext);
		loadResources(mGameContext);
		
		setupListeners(mGameContext);
		
        createPaddle(mGameEngine, mGameContext);
        createBall(mGameEngine, mGameContext);
        createUI(mGameContext);
        createBackground(mGameEngine);
        
	    setupNextLevel();
	}

    protected void setupNextLevel()
    {
    	if (mCurrentLevel >= sBrickDefinitions.length)
    	{
    		mCurrentLevel = 0;
    	}

    	if (mSolidBricks.size() > 0)
    	{
    		for (int i = 0; i < mSolidBricks.size(); i++)
    		{
    			mGameEngine.removeComponent(mSolidBricks.get(i));
    		}
    	}
    	
    	mSolidBricks.clear();
    	
		createBricks(sBrickDefinitions[mCurrentLevel], mGameEngine, mGameContext);
		
		mCurrentLevel++;

		updateUI();
		
		setupBallSpeedAndLocation();		
    }

    private class TerminationListener implements ITerminateSpriteListener
    {
		@Override
		public void onSpriteTermination(ISprite sprite)
		{
			BreakoutView.this.mNofBricksLeft--;
			
			if (BreakoutView.this.mNofBricksLeft <= 0)
			{
				BreakoutView.this.setupNextLevel();
			}
		}
    }
    
    private static class FrameBackground extends ColorBackground
    {
    	private int mTop = 0;
    	private int mLeft = 0;
    	private int mRight = 0;
    	private int mBottom = 0;

    	private Paint mPaint = null;
    	
    	public FrameBackground(int x, int y, int width, int height, int thickness, IGameContext gameContext)
    	{
    		super(gameContext);
    		
    		mLeft = x - thickness / 2;
    		mTop = y - thickness / 2;
    		mRight = x + width + thickness / 2;
    		mBottom = y + height + thickness / 2;
    		
    		mPaint = new Paint();
    		mPaint.setColor(Color.WHITE);
    		mPaint.setStrokeWidth(thickness);
    		mPaint.setStyle(Style.STROKE);
    	}

    	@Override
    	public void draw(GameRenderer renderer)
    	{
    		super.draw(renderer);

    		renderer.getCanvas().drawRect(mLeft, mTop, mRight, mBottom, mPaint);
    	}
    }
    
    private static class BrickDefinition
    {
    	public int mXMargin = 0;
    	public int mYMargin = 0;
    	public String[] mLayout = null;
    	
    	public BrickDefinition(int xMargin, int yMargin, String[] layout)
    	{
    		mXMargin = xMargin;
    		mYMargin = yMargin;
    		mLayout = layout;
    	}

    	public int getNofRows()
    	{
    		return mLayout.length;
    	}
    	
    	public int getNofColumns()
    	{
    		return mLayout[0].length();
    	}
    	
    	public boolean isBrick(int row, int col)
    	{
    		assert mLayout.length > row;
    		assert mLayout[row].length() > col;
    		
    		char ch = mLayout[row].charAt(col);
    		
    		return ((ch == BREAKABLE_BRICK_CHAR) || (ch == SOLID_BRICK_CHAR));
    	}
    	
    	public boolean isSolid(int row, int col)
    	{
    		assert mLayout.length > row;
    		assert mLayout[row].length() > col;
    		
    		char ch = mLayout[row].charAt(col);
    		
    		return (ch == SOLID_BRICK_CHAR);
    	}
    }
}
