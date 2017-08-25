package se.federspiel.android.gameviews;

import java.util.ArrayList;

import se.federspiel.android.agraphics.CanvasSurfaceView;
import se.federspiel.android.agraphics.DrawThread.IDrawer;
import se.federspiel.android.backgrounds.ColorBackground;
import se.federspiel.android.game.GameApplication;
import se.federspiel.android.game.GameEngine;
import se.federspiel.android.game.collision.CollisionEvaluatorLibrary;
import se.federspiel.android.game.collision.CollisionSelectorLibrary;
import se.federspiel.android.game.geometry.Dimensions;
import se.federspiel.android.game.geometry.Point;
import se.federspiel.android.game.interfaces.IBackground;
import se.federspiel.android.game.interfaces.ICollisionSprite;
import se.federspiel.android.game.interfaces.IGameContext;
import se.federspiel.android.game.interfaces.IImageManager;
import se.federspiel.android.game.interfaces.ISprite;
import se.federspiel.android.game.sprites.GraphicSprite;
import se.federspiel.android.game.sprites.ImageSprite;
import se.federspiel.android.game.sprites.actions.SpriteStickAction;
import se.federspiel.android.game.sprites.actions.SpriteStickAction.StickDirection;
import se.federspiel.android.game.sprites.drawers.BoxDrawer;
import se.federspiel.android.game.sprites.drawers.ImageDrawer;
import se.federspiel.android.game.trajectories.CompositeTrajectory;
import se.federspiel.android.game.trajectories.MovementTrajectory;
import se.federspiel.android.game.trajectories.KeyControlledTrajectory;
import se.federspiel.android.game.trajectories.OrientationControlledTrajectory;
import se.federspiel.android.game.trajectories.SnapTrajectory;
import se.federspiel.android.game.trajectories.TouchSlideTrajectory;
import se.federspiel.android.game.trajectories.TouchSlideTrajectory.TouchLock;
import se.federspiel.android.game.trajectories.actions.snap.SnapSoundAction;
import se.federspiel.android.util.ImageTools;
import se.federspiel.android.util.SortTools;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.AttributeSet;

import com.example.explorerapp.R;

public class SlidingPuzzleView extends CanvasSurfaceView
{
	private static final int NOF_BRICKS_HEIGHT = 4;
	private static final int NOF_BRICKS_WIDTH = 4;
	private static final int DEFAULT_BRICK_SIZE = 150;
	private static final int SLACK = 1;
	private static final int SNAP_RADIUS = 5;
	private static final int BORDER_WIDTH = 5;

	private PuzzleStartRunner mPuzzleStartRunner = new PuzzleStartRunner();
	
	private GameApplication mGameApplication = null;
	private GameEngine mGameEngine = null;
	private IGameContext mGameContext = null;
   	private CollisionSelectorLibrary.GridCollisionSelector mCollisionSelector = null;

	private ArrayList<ISprite> mActiveSprites = new ArrayList<ISprite>();
	
	private int mBrickSize = DEFAULT_BRICK_SIZE;
	
	public SlidingPuzzleView(Context context)
	{
		super(context);
	}

	public SlidingPuzzleView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}
	
	public SlidingPuzzleView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}
	
	@Override
    public void onDestroy()
    {
    	if (mGameContext == null)
    	{
    		mGameContext.destroy();
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

       	mCollisionSelector = new CollisionSelectorLibrary.GridCollisionSelector();
       
		mGameContext.getCollisionManager().setCollisionEvaluator(new CollisionEvaluatorLibrary.NearestFoundCollisionSelectorEvaluator());
		mGameContext.getCollisionManager().setCollisionSelector(mCollisionSelector);
		
		registerObjects(mGameContext);
		loadResources(mGameContext);
		
        IBackground background = mGameContext.getBackgroundFactory().createBackground("Color");
        ((ColorBackground) background).setColor(Color.BLUE);
		mGameEngine.addBackground(background);

		setupDefaultPuzzle();
	}
    
    public void startNewPuzzle(String imagePath)
	{
		mPuzzleStartRunner.start(imagePath);
	}

    private ISprite[] createBricks(int[] resourceIds, IGameContext gameContext)
    {
    	ISprite[] bricks = new ISprite[resourceIds.length];

    	for (int i = 0; i < resourceIds.length; i++)
    	{
        	ISprite sprite = gameContext.getSpriteFactory().createSprite("Image");
        	ImageSprite imageSprite = (ImageSprite) sprite;
        	
            ImageDrawer drawer = new ImageDrawer(mGameContext);
            drawer.setBitmapResource(resourceIds[i], mBrickSize, mBrickSize);
            imageSprite.addImageDrawer(drawer);

            bricks[i] = sprite;
    	}

    	shuffleBricks(bricks);
    	
    	return bricks;
    }
    
    private void shuffleBricks(ISprite[] bricks)
    {
    	SortTools.shuffle(bricks);
    }
    
    private void layoutBricks(ISprite[] bricks, GameEngine gameEngine, IGameContext gameContext)
    {
    	Point[] snapPoints = new Point[NOF_BRICKS_WIDTH * NOF_BRICKS_HEIGHT];

    	Point topLeft = calculateTopLeft();
    	
    	int index = 0;
    	int yPos = (int) topLeft.Y;
    	
    	for (int row = 1; row <= NOF_BRICKS_HEIGHT; row++)
    	{
        	int xPos = (int) topLeft.X;
        	
	        for (int col = 1; col <= NOF_BRICKS_WIDTH; col++)
	        {
	        	snapPoints[index] = new Point(xPos, yPos);
	        			
	        	index++;
	        	
       			xPos += mBrickSize + SLACK;
	        }
	        
	        yPos += mBrickSize + SLACK;
    	}

    	mCollisionSelector.setGridProperties((int) topLeft.X, (int) topLeft.Y, mBrickSize + SLACK, mBrickSize + SLACK, NOF_BRICKS_HEIGHT, NOF_BRICKS_WIDTH);
    	
    	int spriteIndex = 0;
    	
    	yPos = (int) topLeft.Y;
    	
    	for (int row = 1; row <= NOF_BRICKS_HEIGHT; row++)
    	{
        	int xPos = (int) topLeft.X;
        	
	        for (int col = 1; col <= NOF_BRICKS_WIDTH; col++)
	        {
	        	if ((row != NOF_BRICKS_HEIGHT) || (col != NOF_BRICKS_WIDTH))
	        	{
		        	ISprite sprite = bricks[spriteIndex];
		        			
		        	spriteIndex++;
		        	
		            CompositeTrajectory compositeTrajectory = (CompositeTrajectory) gameContext.getTrajectoryFactory().createTrajectory("CompositeTrajectory", sprite);
		            
		            TouchSlideTrajectory touchSlideTrajectory = (TouchSlideTrajectory) gameContext.getTrajectoryFactory().createTrajectory("TouchSlideTrajectory");
		            touchSlideTrajectory.setSlideRelease(true);
		            touchSlideTrajectory.setLock(TouchLock.LOCK_X_OR_Y);

		            SnapTrajectory snapTrajectory = (SnapTrajectory) gameContext.getTrajectoryFactory().createTrajectory("SnapTrajectory");
		            snapTrajectory.setSnapPoints(snapPoints, SNAP_RADIUS);
		            
		            SnapSoundAction snapAction = new SnapSoundAction(R.raw.tick_01, gameContext);
		            snapTrajectory.setSnapAction(snapAction);
		            
		            compositeTrajectory.addServeAllTrajectory(touchSlideTrajectory);
		            compositeTrajectory.addServeAllTrajectory(snapTrajectory);
		            
		            SpriteStickAction action = new SpriteStickAction();
		            action.setStickDirection(StickDirection.NORMAL_DIRECTION);
		            ((ICollisionSprite) sprite).setCollisionAction(action);
		            
		            sprite.setInitialPosition(new Point(xPos, yPos));
	            
		            mActiveSprites.add(sprite);
		            
		            gameContext.getCollisionManager().add(sprite.getCollisionObject());
		            gameEngine.addComponent(sprite);
		            
		            xPos += mBrickSize + SLACK;
	        	}
        	}
        	
            yPos += mBrickSize + SLACK;
        } 
    }
    
	private void calculateMaxBrickSize()
	{
        int viewWidth = getWidth();
        int viewHeight = getHeight();

    	int doubleBorderWidth = 2 * BORDER_WIDTH;
    	int totalWidthSlack = (NOF_BRICKS_WIDTH - 1) * SLACK;
    	int totalHeightSlack = (NOF_BRICKS_HEIGHT - 1) * SLACK;
        int totalBrickWidth = viewWidth - (totalWidthSlack + doubleBorderWidth);
        int totalBrickHeight = viewHeight - (totalHeightSlack + doubleBorderWidth);

		mBrickSize = Math.min(totalBrickWidth / NOF_BRICKS_WIDTH, totalBrickHeight / NOF_BRICKS_HEIGHT);
	}
	
	private Point calculateTopLeft()
	{
        int viewWidth = getWidth();
        int viewHeight = getHeight();

    	Dimensions outerDim = getOuterDimensions();
        
    	int left = viewWidth - outerDim.getWidth();
    	int top = viewHeight - outerDim.getHeight();
		
    	return new Point(left / 2, top / 2);
	}
	
    private Dimensions getInnerDimensions()
    {
    	int totalWidthSlack = (NOF_BRICKS_WIDTH - 1) * SLACK;
    	int totalHeightSlack = (NOF_BRICKS_HEIGHT - 1) * SLACK;
    	int innerWidthSideLength = (NOF_BRICKS_WIDTH * mBrickSize) + totalWidthSlack;
    	int innerHeightSideLength = (NOF_BRICKS_HEIGHT * mBrickSize) + totalHeightSlack;

    	return new Dimensions(innerWidthSideLength, innerHeightSideLength);
    }
    
    private Dimensions getOuterDimensions()
    {
    	int doubleBorderWidth = 2 * BORDER_WIDTH;
    	int totalWidthSlack = (NOF_BRICKS_WIDTH - 1) * SLACK;
    	int totalHeightSlack = (NOF_BRICKS_HEIGHT - 1) * SLACK;
    	int outerWidthSideLength = (NOF_BRICKS_WIDTH * mBrickSize) + totalWidthSlack + doubleBorderWidth;
    	int outerHeightSideLength = (NOF_BRICKS_HEIGHT * mBrickSize) + totalHeightSlack + doubleBorderWidth;

    	return new Dimensions(outerWidthSideLength, outerHeightSideLength);
    }
    
    private void createFrame()
    {
    	// upper
    	ISprite sprite = mGameContext.getSpriteFactory().createSprite("Graphic");

    	Dimensions outerDim = getOuterDimensions();
    	Dimensions innerDim = getInnerDimensions();
    	
    	Point topLeft = calculateTopLeft();
    	
        BoxDrawer drawer = new BoxDrawer(mGameContext);
        drawer.setDimensions(outerDim.getWidth(), BORDER_WIDTH);
        ((GraphicSprite)sprite).setGraphicDrawer(drawer);
        
        sprite.setInitialPosition(new Point(topLeft.X - BORDER_WIDTH, topLeft.Y - BORDER_WIDTH));
            
        mGameContext.getCollisionManager().add(sprite.getCollisionObject());
        mGameEngine.addComponent(sprite);

    	// left
    	sprite = mGameContext.getSpriteFactory().createSprite("Graphic");
        
        drawer = new BoxDrawer(mGameContext);
        drawer.setDimensions(BORDER_WIDTH, outerDim.getHeight() - 2 * BORDER_WIDTH);
        ((GraphicSprite)sprite).setGraphicDrawer(drawer);
        
        sprite.setInitialPosition(new Point(topLeft.X - BORDER_WIDTH, topLeft.Y));
            
        mGameContext.getCollisionManager().add(sprite.getCollisionObject());
        mGameEngine.addComponent(sprite);

        // bottom
    	sprite = mGameContext.getSpriteFactory().createSprite("Graphic");
        
        drawer = new BoxDrawer(mGameContext);
        drawer.setDimensions(outerDim.getWidth(), BORDER_WIDTH);
        ((GraphicSprite)sprite).setGraphicDrawer(drawer);
        
        sprite.setInitialPosition(new Point(topLeft.X - BORDER_WIDTH, topLeft.Y + innerDim.getHeight()));
            
        mGameContext.getCollisionManager().add(sprite.getCollisionObject());
        mGameEngine.addComponent(sprite);

        // right
    	sprite = mGameContext.getSpriteFactory().createSprite("Graphic");
        
        drawer = new BoxDrawer(mGameContext);
        drawer.setDimensions(BORDER_WIDTH, outerDim.getHeight() - 2 * BORDER_WIDTH);
        ((GraphicSprite)sprite).setGraphicDrawer(drawer);
        
        sprite.setInitialPosition(new Point(topLeft.X + innerDim.getWidth(), topLeft.Y));
            
        mGameContext.getCollisionManager().add(sprite.getCollisionObject());
        mGameEngine.addComponent(sprite);
    }
    
    private void registerObjects(IGameContext gameContext)
    {
        gameContext.getSpriteFactory().registerSprite("Graphic", GraphicSprite.class);
        gameContext.getSpriteFactory().registerSprite("Image", ImageSprite.class);

        gameContext.getTrajectoryFactory().registerTrajectory("ConstantVelocityTrajectory", MovementTrajectory.class);
        gameContext.getTrajectoryFactory().registerTrajectory("KeyControlledTrajectory", KeyControlledTrajectory.class);
        gameContext.getTrajectoryFactory().registerTrajectory("OrientationControlledTrajectory", OrientationControlledTrajectory.class);
        gameContext.getTrajectoryFactory().registerTrajectory("TouchSlideTrajectory", TouchSlideTrajectory.class);
        gameContext.getTrajectoryFactory().registerTrajectory("CompositeTrajectory", CompositeTrajectory.class);
        gameContext.getTrajectoryFactory().registerTrajectory("SnapTrajectory", SnapTrajectory.class);
        
        gameContext.getBackgroundFactory().registerBackground("Color", ColorBackground.class);
    }
    
    private void loadResources(IGameContext gameContext)
    {
		gameContext.getSoundManager().addSound(R.raw.tick_01);
    }

    private void setupDefaultPuzzle()
    {
    	int[] resourceIds = new int[NOF_BRICKS_WIDTH * NOF_BRICKS_HEIGHT - 1];
    	
    	for (int i = 0; i < resourceIds.length; i++)
    	{
    		resourceIds[i] = R.raw.button_0;
    	}
    	
    	setupPuzzle(resourceIds);
    }
    
    private void setupPuzzle(int[] resourceIds)
    {
    	calculateMaxBrickSize();
    	
        ISprite[] bricks = createBricks(resourceIds, mGameContext);
        
        layoutBricks(bricks, mGameEngine, mGameContext);
        
		createFrame();
    }
    
	private class PuzzleStartRunner implements Runnable
	{
		private String mImagePath = null;
		
		public PuzzleStartRunner()
		{
		}

		public synchronized void start(String imagePath)
		{
			mImagePath = imagePath;
			
			mGameEngine.invokeOnGameThread(this);
		}
		
		@Override
		public synchronized void run()
		{
			startNewPuzzle(mImagePath);
		}
		
		private void startNewPuzzle(String imagePath)
		{
	    	for (ISprite sprite : mActiveSprites)
	    	{
	    		mGameContext.getCollisionManager().remove(sprite.getCollisionObject());
	    		mGameEngine.removeComponent(sprite);
	    	}
	    	
	    	mActiveSprites.clear();
	    	
			IImageManager imageManager = mGameContext.getImageManager();
			
			Dimensions dim = Dimensions.Zero.clone();
			
			imageManager.getImageSize(imagePath, dim);
			
			calculateMaxBrickSize();

			int puzzleWidth = NOF_BRICKS_WIDTH * mBrickSize;
			int puzzleHeight = NOF_BRICKS_HEIGHT * mBrickSize;

			boolean rotate = false;

			if (dim.getHeight() > dim.getWidth())
			{
				if (puzzleHeight < puzzleWidth)
				{
					rotate = true;
				}
			}
			else
			{
				if (puzzleHeight >= puzzleWidth)
				{
					rotate = true;
				}
			}

			if (rotate)					
			{
				dim.setDimensions(dim.getHeight(), dim.getWidth());
			}
			
			float widthFactor = dim.getWidth() / (float) puzzleWidth;
			float heightFactor = dim.getHeight() / (float) puzzleHeight;

			int reqWidth = 0;
			int reqHeight = 0;
			
			if (widthFactor < heightFactor)
			{
				reqWidth = puzzleWidth;
				reqHeight = Math.round(puzzleHeight * widthFactor);
			}
			else
			{
				reqWidth = Math.round(puzzleWidth * heightFactor);
				reqHeight = puzzleHeight;
			}
			
			if (rotate)					
			{
				int tmp = reqWidth;
				reqWidth = reqHeight;
				reqHeight = tmp;
			}

			int resId = imageManager.loadBitmap(imagePath, reqWidth, reqHeight);

			Bitmap image = imageManager.getBitmap(resId);

			if (rotate)					
			{
				Bitmap rotatedImage = ImageTools.rotateBitmap(image, -90);
				
				image = rotatedImage;
			}

			Bitmap[][] brickBitmaps = ImageTools.splitBitmap(NOF_BRICKS_HEIGHT, NOF_BRICKS_WIDTH, 
					mBrickSize, mBrickSize, image);

			imageManager.unloadBitmap(resId, reqWidth, reqHeight);
			
	    	int[] resourceIds = new int[NOF_BRICKS_WIDTH * NOF_BRICKS_HEIGHT - 1];
	    	
	    	int index = resourceIds.length - 1;
	    	
			for (int i = 0; i < brickBitmaps.length; i++)
			{
				for (int j = 0; j < brickBitmaps[i].length; j++)
				{
					if (index >= 0)
					{
						resourceIds[index] = imageManager.addBitmap(brickBitmaps[i][j]);
						
						index--;
					}
					else
					{
						brickBitmaps[i][j].recycle();
					}
				}
			}
			
	    	setupPuzzle(resourceIds);
		}
	}
}
