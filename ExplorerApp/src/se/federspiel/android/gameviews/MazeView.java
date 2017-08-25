package se.federspiel.android.gameviews;

import java.util.ArrayList;

import se.federspiel.android.agraphics.CanvasSurfaceView;
import se.federspiel.android.agraphics.DrawThread.IDrawer;
import se.federspiel.android.backgrounds.ImageBackground;
import se.federspiel.android.game.GameApplication;
import se.federspiel.android.game.GameEngine;
import se.federspiel.android.game.collision.CollisionEvaluatorLibrary;
import se.federspiel.android.game.collision.CollisionSet;
import se.federspiel.android.game.geometry.Point;
import se.federspiel.android.game.geometry.Vector2;
import se.federspiel.android.game.interfaces.IDrawableComponent.DrawableZOrder;
import se.federspiel.android.game.interfaces.IGameContext;
import se.federspiel.android.game.interfaces.IGameEngine;
import se.federspiel.android.game.interfaces.IGraphicDrawer;
import se.federspiel.android.game.interfaces.IUserInputManager.TouchClickEvent;
import se.federspiel.android.game.sprites.GraphicSprite;
import se.federspiel.android.game.sprites.actions.SpriteStickAction;
import se.federspiel.android.game.sprites.actions.SpriteStickAction.StickDirection;
import se.federspiel.android.game.sprites.drawers.BallDrawer;
import se.federspiel.android.game.sprites.drawers.LineDrawer;
import se.federspiel.android.game.trajectories.LinearPathTrajectory;
import se.federspiel.android.game.trajectories.OrientationControlledTrajectory;
import se.federspiel.android.game.trajectories.AbstractPathTrajectory.Direction;
import se.federspiel.android.game.trajectories.OrientationControlledTrajectory.OrientationMode;
import se.federspiel.android.game.trajectories.limits.GraphicsViewLimits;
import se.federspiel.android.game.ui.UIButton;
import se.federspiel.android.game.ui.UIInputComponent;
import se.federspiel.android.game.ui.UIInputComponent.UIIOnClickListener;
import se.federspiel.android.game.utils.GameTools;
import se.federspiel.android.game.utils.Maze;
import se.federspiel.android.game.utils.Maze.MazeType;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;

public class MazeView extends CanvasSurfaceView
{
	private static final int BORDER_WIDTH = 30;
	
	private static final int MAZE_ROWS = 25;
	private static final int MAZE_COLUMNS = 15;

	private static final int UI_BUTTON_Y = 1000;
	
	private static final float BALL_SIZE = 0.8f;

	private static final int PATH_POINT_SIZE = 6;
	private static final int PATH_LINE_WIDTH = 4;
	
	private GameApplication mGameApplication = null;
	private GameEngine mGameEngine = null;
	private IGameContext mGameContext = null;

	private CollisionSet mLineCollisionSet = null;
	private CollisionSet mBallCollisionSet = null;
	
	private int mCellSize = 0;

	private int mMazeTop = 0;
	private int mMazeLeft = 0;

	private Maze mMaze = null;

	private GraphicSprite mBallSprite = null;
	
	private ImageBackground mMazeImageBackground = null;
	private ImageBackground mSolvedMazeImageBackground = null;
	private ArrayList<IGraphicDrawer> mDrawers = new ArrayList<IGraphicDrawer>();
	
	public MazeView(Context context)
	{
		super(context);
	}

	public MazeView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}
	
	public MazeView(Context context, AttributeSet attrs, int defStyle)
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
	
	private void solveMaze()
	{
        mMaze.solveMaze(0, 0, MAZE_COLUMNS - 1, MAZE_ROWS - 1);
        
        ArrayList<Point> cells = mMaze.getPathCoordinates(mMazeLeft, mMazeTop, mCellSize, mCellSize);
		Point[] points = cells.toArray(new Point[0]);

//    	BezierDrawer bezierDrawer = new BezierDrawer(cells.toArray(new Point[0]), 0.1f);
//    	bezierDrawer.setColor(Color.BLACK);
//    	bezierDrawer.setWidth(PATH_LINE_WIDTH);
//    	bezierDrawer.setDrawPathPoints(Color.GREEN, PointIconType.SQUARE, PATH_POINT_SIZE);
    	
        se.federspiel.android.backgrounds.drawers.LineDrawer lineDrawer = 
        		new se.federspiel.android.backgrounds.drawers.LineDrawer(points);
    	lineDrawer.setLineColor(Color.BLACK);
    	lineDrawer.setLineWidth(PATH_LINE_WIDTH);
    	lineDrawer.setDrawPathPoints(Color.RED, se.federspiel.android.backgrounds.drawers.LineDrawer.PointIconType.SQUARE, PATH_POINT_SIZE);

        mDrawers.add(lineDrawer);
    	
        int resourceId = mGameContext.getImageManager().addBitmap(GameTools.createBitmap(mDrawers, Color.BLUE));
        
        mSolvedMazeImageBackground.setBitmapResource(resourceId);
		mGameEngine.addBackground(mSolvedMazeImageBackground);

		mGameEngine.removeBackground(mMazeImageBackground);
		
    	LinearPathTrajectory trajectory = new LinearPathTrajectory(mGameContext, mBallSprite);
		trajectory.definePath(Direction.FORWARD, 1, points);
        trajectory.setInitialSpeed(new Vector2(250, 0));
        mBallSprite.setTrajectory(trajectory);

        mGameContext.getCollisionManager().remove(mBallSprite.getCollisionObject());
        
        mBallSprite.setInitialPosition(new Point(mMazeLeft + mCellSize / 2, mMazeTop + mCellSize / 2));
	}
	
	private void createBackgrounds(IGameEngine gameEngine)
	{
		mMazeImageBackground = new ImageBackground(mGameContext);
		mMazeImageBackground.setZOrder(DrawableZOrder.BACKGROUND_LAYER_2);

		mSolvedMazeImageBackground = new ImageBackground(mGameContext);
		mSolvedMazeImageBackground.setZOrder(DrawableZOrder.BACKGROUND_LAYER_2);
	}

	private void createMaze(IGameEngine gameEngine)
	{
        mMaze = Maze.generateMaze(MAZE_COLUMNS, MAZE_ROWS, MazeType.RecursiveBacktrackingMaze);

        ArrayList<Point> lines = mMaze.getWallCoordinates(mMazeLeft, mMazeTop, mCellSize, mCellSize);

        lines.add(new Point(mMazeLeft, mMazeTop));
        lines.add(new Point(mMazeLeft + mCellSize * MAZE_COLUMNS, mMazeTop));
        
        lines.add(new Point(mMazeLeft + mCellSize * MAZE_COLUMNS, mMazeTop));
        lines.add(new Point(mMazeLeft + mCellSize * MAZE_COLUMNS, mMazeTop + mCellSize * MAZE_ROWS));
        
        lines.add(new Point(mMazeLeft + mCellSize * MAZE_COLUMNS, mMazeTop + mCellSize * MAZE_ROWS));
        lines.add(new Point(mMazeLeft, mMazeTop + mCellSize * MAZE_ROWS));
               
        lines.add(new Point(mMazeLeft, mMazeTop + mCellSize * MAZE_ROWS));
        lines.add(new Point(mMazeLeft, mMazeTop));

        for (int i = 0; i < lines.size(); i += 2)
        {
        	Point start = lines.get(i);
        	Point end = lines.get(i + 1);
        	
        	GraphicSprite grSprite = new GraphicSprite(mGameContext);
            
            LineDrawer lineDrawer = new LineDrawer(mGameContext);
            lineDrawer.setLine((int) start.X, (int) start.Y, (int) end.X, (int) end.Y);
            grSprite.setGraphicDrawer(lineDrawer);

            grSprite.setInitialPosition(start);
                
            grSprite.setCollisionSet(mLineCollisionSet);
            
            mGameContext.getCollisionManager().add(grSprite.getCollisionObject());

            mGameEngine.addUpdateComponent(grSprite);
            
            mDrawers.add(grSprite);
        }

        int resourceId = mGameContext.getImageManager().addBitmap(GameTools.createBitmap(mDrawers, Color.BLUE));
        
        mMazeImageBackground.setBitmapResource(resourceId);
		mGameEngine.addBackground(mMazeImageBackground);
	}

	private void createBall(IGameEngine gameEngine)
	{
		mBallSprite = new GraphicSprite(mGameContext);
        
        BallDrawer drawer = new BallDrawer(mGameContext);
        drawer.setRadius((int) (mCellSize * BALL_SIZE) / 2);
        drawer.setColor(Color.RED);
        mBallSprite.setGraphicDrawer(drawer);

    	OrientationControlledTrajectory trajectory = new OrientationControlledTrajectory(mGameContext, mBallSprite);

        trajectory.setOrientationMode(OrientationMode.SPEED);
        trajectory.setSpeedGain(15, 15);
        trajectory.setPositionLimits(new GraphicsViewLimits(mGameContext));
        mBallSprite.setTrajectory(trajectory);
        
        SpriteStickAction action = new SpriteStickAction();
        action.setStickDirection(StickDirection.NORMAL_DIRECTION);
        mBallSprite.setCollisionAction(action);
        
        mBallSprite.setInitialPosition(new Point(mMazeLeft + mCellSize / 2, mMazeTop + mCellSize / 2));
        
        mBallSprite.setCollisionSet(mBallCollisionSet);
        
        mGameContext.getCollisionManager().add(mBallSprite.getCollisionObject());
        
		mGameEngine.addComponent(mBallSprite);
	}
	
	private void createUI(IGameContext gameContext)
	{
		UIButton button = new UIButton(250, UI_BUTTON_Y, 250, 100);
		button.setText("Solve");
		
		button.setOnClickListener(new UIIOnClickListener()
		{
			@Override
			public boolean onClick(UIInputComponent component,
					TouchClickEvent event)
			{
				solveMaze();
	
				((UIButton) component).setEnable(false);
				
				return true;
			}
		});

		gameContext.getMainWindow().addComponent(button);
	}
	
	private void calculateProperties(IGameContext gameContext)
	{
		int vHeight = gameContext.getGraphicBounds().getHeight();
		int height = vHeight - 2 * BORDER_WIDTH - (vHeight - UI_BUTTON_Y);
		int width = gameContext.getGraphicBounds().getWidth() - 2 * BORDER_WIDTH;

		mCellSize = Math.min((height / MAZE_ROWS), (width / MAZE_COLUMNS));

		mMazeTop = (height - (mCellSize * MAZE_ROWS)) / 2 + BORDER_WIDTH;
		mMazeLeft = (width - (mCellSize * MAZE_COLUMNS)) / 2 + BORDER_WIDTH;
	}
	
	private void createCaches(IGameContext gameContext)
	{
		gameContext.getCollisionManager().enableCollisionSets(true);

		mLineCollisionSet = gameContext.getCollisionManager().createCollisionSet();
		mLineCollisionSet.setCollisionsWithinSet(false);
		
		mBallCollisionSet = gameContext.getCollisionManager().createCollisionSet();
		mBallCollisionSet.setCollisionsWithinSet(false);
		
		mBallCollisionSet.joinSet(mLineCollisionSet);
	}
	
	private void initGame()
	{
    	mGameApplication = new GameApplication(this);

		mGameEngine = GameApplication.getGameEngine();
		mGameContext = mGameEngine.getGameContext();

		mGameContext.getCollisionManager().setCollisionEvaluator(new CollisionEvaluatorLibrary.RepeatNearestFoundCollisionSelectorEvaluator());
		
		mGameEngine.getGameView().setGameViewPosition(100, 200);
		mGameContext.getImageManager().setRecycleUnallocated(true);
		
		calculateProperties(mGameContext);
		
		createCaches(mGameContext);
		createBackgrounds(mGameEngine);
		createMaze(mGameEngine);
		createBall(mGameEngine);
		createUI(mGameContext);
	}
}
