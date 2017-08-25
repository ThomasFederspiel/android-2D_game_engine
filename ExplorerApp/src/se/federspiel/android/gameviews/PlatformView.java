package se.federspiel.android.gameviews;

import se.federspiel.android.agraphics.CanvasSurfaceView;
import se.federspiel.android.agraphics.DrawThread.IDrawer;
import se.federspiel.android.backgrounds.ImageBackground;
import se.federspiel.android.backgrounds.ImageSpriteBackground;
import se.federspiel.android.backgrounds.ParallaxBackground;
import se.federspiel.android.game.GameApplication;
import se.federspiel.android.game.GameEngine;
import se.federspiel.android.game.GameView;
import se.federspiel.android.game.geometry.Point;
import se.federspiel.android.game.interfaces.IDrawableComponent.DrawableZOrder;
import se.federspiel.android.game.interfaces.IGameContext;
import se.federspiel.android.game.interfaces.IGameEngine;
import se.federspiel.android.game.interfaces.IGameEngine.GameViewCoordinateSystem;
import se.federspiel.android.game.interfaces.IScrollableBackground.ScrollDirections;
import se.federspiel.android.game.interfaces.ISprite;
import se.federspiel.android.game.interfaces.ISprite.ISpriteOutOfBoundsListener;
import se.federspiel.android.game.sprites.GraphicSprite;
import se.federspiel.android.game.sprites.drawers.BallDrawer;
import se.federspiel.android.game.trajectories.UITouchTrajectory;
import se.federspiel.android.game.trajectories.limits.BoundsLimits;
import se.federspiel.android.game.trajectories.limits.GameViewLimits;
import se.federspiel.android.game.trajectories.limits.OutOfBoundsEvent;
import se.federspiel.android.game.ui.UITouchButton;
import se.federspiel.android.util.ImageTools.IColorMatcher;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;

import com.example.explorerapp.R;

public class PlatformView extends CanvasSurfaceView
{
	private static final int BUTTON_SIZE = 150;

	private static final float XMIN = 0.0f;
	private static final float XMAX = 1.0f;
	
	private static final float YMIN = 0.35f;
	private static final float YMAX = 0.95f;

	private static final float LAYER2_DRAG_FACTOR = 0.1f;
	private static final float LAYER3_DRAG_FACTOR = 0.2f;
	private static final float LAYER4_DRAG_FACTOR = 0.3f;
	private static final float LAYER5_DRAG_FACTOR = 0.4f;
	private static final float LAYER6_DRAG_FACTOR = 0.5f;
	private static final float LAYER7_DRAG_FACTOR = 0.6f;
	private static final float LAYER8_DRAG_FACTOR = 0.7f;
	private static final float LAYER9_DRAG_FACTOR = 0.8f;
	
	private GameApplication mGameApplication = null;
	private GameEngine mGameEngine = null;
	private GameView mGameView = null;
	private IGameContext mGameContext = null;

	private UITouchButton mYPTouchArea = null;
	private UITouchButton mYNTouchArea = null;

	private ImageBackground mImageBackground = null;
	private ParallaxBackground mParallaxBackground = null;

	public PlatformView(Context context)
	{
		super(context);
	}

	public PlatformView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}
	
	public PlatformView(Context context, AttributeSet attrs, int defStyle)
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
	
	private void createBackgrounds(IGameEngine gameEngine)
	{
//        mImageBackground = new ImageBackground(mGameContext);
//        mImageBackground.setBitmapResource(R.raw.city_background_0);
//        mImageBackground.setZOrder(DrawableZOrder.BACKGROUND_LAYER_1);
//        mImageBackground.setScrollDirection(ScrollDirections.SCROLL_DIRECTION_Y);
        
        mImageBackground = new ImageBackground(mGameContext);
        mImageBackground.setBitmapResource(R.raw.background_series1_layer1);
        mImageBackground.setZOrder(DrawableZOrder.BACKGROUND_LAYER_1);
        
        mGameEngine.addBackground(mImageBackground);
        
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
        mParallaxBackground.addBackground(tmpImageBackground, LAYER2_DRAG_FACTOR);
        
		tmpImageBackground = new ImageSpriteBackground(mGameContext);
        tmpImageBackground.setBitmapResource(R.raw.background_series1_layer3);
        tmpImageBackground.setImageSpriteIdentifier(spriteIdentifier);
        mParallaxBackground.addBackground(tmpImageBackground, LAYER3_DRAG_FACTOR);
        
        tmpImageBackground = new ImageSpriteBackground(mGameContext);
        tmpImageBackground.setBitmapResource(R.raw.background_series1_layer5);
        tmpImageBackground.setImageSpriteIdentifier(spriteIdentifier);
        mParallaxBackground.addBackground(tmpImageBackground, LAYER5_DRAG_FACTOR);

//        tmpImageBackground = new ImageSpriteBackground(mGameContext);
//        tmpImageBackground.setBitmapResource(R.raw.background_series1_layer6);
//        mParallaxBackground.addBackground(tmpImageBackground, LAYER6_DRAG_FACTOR);
//
//        tmpImageBackground = new ImageSpriteBackground(mGameContext);
//        tmpImageBackground.setBitmapResource(R.raw.background_series1_layer7);
//        mParallaxBackground.addBackground(tmpImageBackground, LAYER7_DRAG_FACTOR);

        tmpImageBackground = new ImageSpriteBackground(mGameContext);
        tmpImageBackground.setBitmapResource(R.raw.background_series1_layer8);
        tmpImageBackground.setImageSpriteIdentifier(spriteIdentifier);
        mParallaxBackground.addBackground(tmpImageBackground, LAYER8_DRAG_FACTOR);
        
        tmpImageBackground = new ImageSpriteBackground(mGameContext);
        tmpImageBackground.setBitmapResource(R.raw.background_series1_layer9);
        tmpImageBackground.setImageSpriteIdentifier(spriteIdentifier);
        mParallaxBackground.addBackground(tmpImageBackground, LAYER9_DRAG_FACTOR);
        
        mGameEngine.addBackground(mParallaxBackground);
	}

	private void createUI(IGameContext gameContext)
	{
		int height = mGameContext.getGraphicBounds().getHeight();
		int width = mGameContext.getGraphicBounds().getWidth();
		
		mYNTouchArea = new UITouchButton(width - BUTTON_SIZE, 0, BUTTON_SIZE, BUTTON_SIZE);
		mYNTouchArea.setBorder(true);
		mYNTouchArea.setBitmapResource(R.raw.button_arrow_up_0);

		mYPTouchArea = new UITouchButton(width - BUTTON_SIZE, (height - BUTTON_SIZE), BUTTON_SIZE, BUTTON_SIZE);
		mYPTouchArea.setBorder(true);
		mYPTouchArea.setBitmapResource(R.raw.button_arrow_down_0);

		mGameContext.getMainWindow().addComponent(mYPTouchArea);
		mGameContext.getMainWindow().addComponent(mYNTouchArea);
	}

	private void createJumper()
	{
        GraphicSprite sprite = new GraphicSprite(mGameContext);
        
        BallDrawer drawer = new BallDrawer(mGameContext);
        drawer.setRadius(40);
        sprite.setGraphicDrawer(drawer);

        UITouchTrajectory trajectory = new UITouchTrajectory(mGameContext, sprite);

        trajectory.setInitialSpeed(200, 200);
        trajectory.setPositionLimits(createJumperBounds());
        trajectory.setInitialAcceleration(0, 200);

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
					mGameView.moveGameViewPosition(0, -event.distance);
					
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
	}

	private BoundsLimits createJumperBounds()
	{
        BoundsLimits limits = new GameViewLimits(mGameContext, XMIN, XMAX, YMIN, YMAX);
	
		return limits;
	}
	
	private void createCaches(IGameContext gameContext)
	{
	}
	
	private void calculateProperties(IGameContext gameContext)
	{
	}
	
	private void initGame()
	{
    	mGameApplication = new GameApplication(this);

		mGameEngine = GameApplication.getGameEngine();
		mGameContext = mGameEngine.getGameContext();
		mGameView = mGameEngine.getGameView();
		
		int height = mGameContext.getGraphicBounds().getHeight();
		int width = mGameContext.getGraphicBounds().getWidth();
		
		mGameEngine.setLayerCoordinateSystem(DrawableZOrder.SPRITE_LAYER_1, GameViewCoordinateSystem.GAME_VIEW_RELATIVE);
		mGameEngine.enableGameView(true);
		mGameView.setGameViewLimits(0, Float.NaN, width, Float.NaN);

		mGameContext.getImageManager().setRecycleUnallocated(true);
		
		calculateProperties(mGameContext);
		
		createCaches(mGameContext);
		createBackgrounds(mGameEngine);
		createUI(mGameContext);
		createJumper();
	}
}
