package se.federspiel.android.game.sprites;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;

import com.example.explorerapp.AInstrumentation;

import se.federspiel.android.game.GameRenderer;
import se.federspiel.android.game.GameTime;
import se.federspiel.android.game.geometry.Point;
import se.federspiel.android.game.geometry.Ray;
import se.federspiel.android.game.geometry.Vector2;
import se.federspiel.android.game.interfaces.IGameContext;
import se.federspiel.android.game.interfaces.ISprite;
import se.federspiel.android.game.interfaces.ISpriteAction;
import se.federspiel.android.game.interfaces.ISpriteCollisionObject;
import se.federspiel.android.game.interfaces.ITrajectory;
import se.federspiel.android.game.interfaces.ITrajectory.IntegrationMethod;
import se.federspiel.android.game.trajectories.StationaryTrajectory;
import se.federspiel.android.game.trajectories.limits.OutOfBoundsEvent;

public abstract class AbstractSprite extends AbstractSpriteCollisionObject implements ISprite
{
	private static Paint sSpriteDrawCollisionNormalPaint = null; 
	private static Paint sSpriteDrawCollisionPointPaint = null; 
	private static Paint sSpriteDrawBoundsPaint = null; 
	
	// ;+
	protected ICollisionContext mContext = null;
	// ;+
	
	protected IGameContext mGameContext = null;

	private PhysicalProperties mPhysicalProperties = new PhysicalProperties();
	
    private ITrajectory mTrajectory = null;

	private ISpriteAction mSpriteAction = null;

    private ISpriteOutOfBoundsListener mOutOfBoundsListener = null;

    private boolean mIsActive = false;
    
    private DrawableZOrder mZOrder = DrawableZOrder.SPRITE_LAYER_1;

    {
		sSpriteDrawCollisionNormalPaint = new Paint(); 
		sSpriteDrawCollisionNormalPaint.setColor(Color.GREEN); 
		sSpriteDrawCollisionNormalPaint.setStrokeWidth(3); 
		
		sSpriteDrawCollisionPointPaint = new Paint(); 
		sSpriteDrawCollisionPointPaint.setColor(Color.BLACK); 
		sSpriteDrawCollisionPointPaint.setStrokeWidth(1); 
		
		sSpriteDrawBoundsPaint = new Paint(); 
		sSpriteDrawBoundsPaint.setColor(Color.BLACK); 
		sSpriteDrawBoundsPaint.setStrokeWidth(1); 
		sSpriteDrawBoundsPaint.setStyle(Style.STROKE);
    }
	
    public AbstractSprite(IGameContext gameContext)
    {
        mGameContext = gameContext;

        // ;+ must be set via factory
        mTrajectory = new StationaryTrajectory(gameContext, this);
    }

	@Override
	public DrawableZOrder getZOrder()
	{
		return mZOrder;
	}

	@Override
	public void setZOrder(DrawableZOrder level)
	{
		mZOrder = level;
	}

	@Override
	public PhysicalProperties getPhysicalProperties()
	{
		return mPhysicalProperties;
	}

	@Override
	public void setPhysicalProperties(PhysicalProperties properties)
	{
		assert properties != null;
		
		mPhysicalProperties = properties;
	}

	@Override
    public void update(GameTime gameTime)
    {
    	updateTrajectory(gameTime);
    	updateAction(gameTime);
    }

	@Override
    public void loadContent()
    {
    	setupTrajectory();
    	
    	mIsActive = true;
    }

    @Override
    public void unloadContent()
    {
    	teardownTrajectory();
    	
    	mIsActive = false;
    }
    
    @Override
    public void setCollisionAction(ISpriteAction action)
    {
    	assert mSpriteAction == null;
    	
    	mSpriteAction = action;
    }
    
    @Override
    public void setOutOfBoundsListener(ISpriteOutOfBoundsListener listener)
    {
    	assert mOutOfBoundsListener == null;
    	
    	mOutOfBoundsListener = listener;
    }
    
    @Override
    public Point getPosition()
    {
        return mTrajectory.getPosition();
    }

	@Override
	public void setInitialPosition(Point position)
	{
        mTrajectory.setInitialPosition(position);
	}

	@Override
    public void setInitialPositionX(float x)
	{
        mTrajectory.setInitialPositionX(x);
	}

	@Override
	public void setInitialPositionY(float y)
	{
		mTrajectory.setInitialPositionY(y);
	}
    
	@Override
	public void setPosition(Point position)
	{
		mTrajectory.setPosition(position);
	}

	@Override
	public void setPosition(float x, float y)
	{
		mTrajectory.setPosition(x, y);
	}
	
	@Override
	public Vector2 getSpeed()
	{
		return mTrajectory.getSpeed();
	}

	@Override
    public IntegrationMethod getIntegrationMethod()
    {
		return mTrajectory.getIntegrationMethod();
    }
    
	@Override
	public void setInitialSpeed(Vector2 speed)
	{
        mTrajectory.setInitialSpeed(speed);
	}

	@Override
	public void setSpeed(float x, float y)
	{
		mTrajectory.setSpeed(x, y);
	}

	@Override
    public ITrajectory getTrajectory()
    {
        return mTrajectory;
    }

	@Override
    public void setTrajectory(ITrajectory trajectory)
    {
		trajectory.setInitialPosition(mTrajectory.getPosition());
		
        mTrajectory = trajectory;
        
        if (mIsActive)
        {
        	setupTrajectory();
        }
    }

    @Override
    public ISpriteCollisionObject getCollisionObject()
    {
        return this; 
    }

    @Override
	public Ray getLastUpdateMovementRay()
	{
 		return mTrajectory.getMovementRay();
	}

	@Override
	public Vector2 getLastUpdateSpeed()
	{
		return mTrajectory.getMovementSpeed();
	}
	
	@Override
	public void addForce(Vector2 force)
	{
		mTrajectory.addForce(force);
	}

	@Override
	public boolean isStationary()
	{
		return mTrajectory.isStationary();
	}
	
	@Override
	public boolean isMoving()
	{
		return mTrajectory.isMoving();
	}

	@Override
	public boolean isYielding()
	{
    	if (mSpriteAction != null)
    	{
    		return mSpriteAction.isYielding(this);
    	}
    	
    	return true;
	}

    @Override
	public void onPositionChanged(Point oldPosition, Point newPosition)
	{
    	onPositionUpdate(newPosition);
    	
    	notifySpritePositionChanged(oldPosition, newPosition);
	}

    @Override
    public void draw(GameRenderer renderer)
    {
    	if (renderer.isWithinBounds(getBounds()))
    	{
	    	paint(renderer);
	    	
	    	if (AInstrumentation.SPRITE_DRAW_COLLISION)
	    	{
	    		if (mContext != null)
	    		{
	    			Vector2 normal = mContext.getCollisionNormal();
	    			
	    			Point point = mContext.getCorrectedMovement().getEndPosition();
	
	    			Canvas canvas = renderer.getCanvas();
	
	    	    	canvas.drawCircle(point.X, point.Y, 3, sSpriteDrawCollisionPointPaint);
	    	    	canvas.drawLine(point.X, point.Y, point.X + normal.X * 40, point.Y + normal.Y * 40, sSpriteDrawCollisionNormalPaint);
	    		}
	    	}
	    	
	    	if (AInstrumentation.SPRITE_DRAW_BOUNDS)
	    	{
    			Canvas canvas = renderer.getCanvas();

    	    	canvas.drawRect(getBounds().getLeft(), getBounds().getTop(), 
    	    		getBounds().getRight(), getBounds().getBottom(), sSpriteDrawBoundsPaint);
	    	}
    	}
    }
    
	@Override
	public void onSpriteCollision(ICollisionContext context, ISpriteCollisionObject collidingObject)
	{
    	if (mSpriteAction != null)
    	{
    		mSpriteAction.onSpriteCollision(this, context, collidingObject);
    	}
    	
    	// ;+
    	if (AInstrumentation.SPRITE_DRAW_COLLISION)
    	{
    		mContext = context;
    		
        	if (AInstrumentation.SPRITE_STOP)
        	{
        		mTrajectory.setSpeed(0, 0);
        	}
    	}
    	// ;+
	}

	@Override
    public boolean onOutOfBounds(OutOfBoundsEvent event)
    {
		boolean handled = false;
		
		if (mOutOfBoundsListener != null)
		{
			handled = mOutOfBoundsListener.onOutOfBounds(this, event);
		}
		
		return handled;
    }

	protected abstract void paint(GameRenderer renderer);
	
    protected void onPositionUpdate(Point position)
    {
    }
    
    private void updateTrajectory(GameTime gameTime)
    {
    	if (mTrajectory.update(gameTime))
        {
        }
    }

    private void updateAction(GameTime gameTime)
    {
    	if (mSpriteAction != null)
    	{
    		mSpriteAction.update(this, gameTime);
    	}
    }
    
    private void setupTrajectory()
    {
    	mTrajectory.setup();
    }
    
    private void teardownTrajectory()
    {
    	mTrajectory.teardown();
    }
}
