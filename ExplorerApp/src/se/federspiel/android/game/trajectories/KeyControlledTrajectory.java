package se.federspiel.android.game.trajectories;

import se.federspiel.android.game.GameTime;
import se.federspiel.android.game.geometry.Vector2;
import se.federspiel.android.game.interfaces.IGameContext;
import se.federspiel.android.game.interfaces.ITrajectoryControlledSprite;
import se.federspiel.android.game.interfaces.IUserInputManager.IOnKeyDownListener;
import se.federspiel.android.game.interfaces.IUserInputManager.IOnKeyUpListener;
import se.federspiel.android.game.interfaces.IUserInputManager.KeyCodeEnum;
import se.federspiel.android.game.trajectories.limits.OutOfBoundsEvent;

public class KeyControlledTrajectory extends AbstractTrajectory implements IOnKeyDownListener, IOnKeyUpListener
{
	private Vector2 mDefaultSpeed = Vector2.Zero.clone();
	private Vector2 mDefaultAcceleration = Vector2.Zero.clone();
	
    public KeyControlledTrajectory(IGameContext gameContext)
    {
    	super(gameContext);
    }
    
    public KeyControlledTrajectory(IGameContext gameContext, ITrajectoryControlledSprite sprite)
    {
    	super(gameContext, sprite);
    }

	@Override
	public void setup()
	{
		setAccelerationMode(AccelerationModeEnum.ACCELERATION_ABSOLUTE_MODE);
		
		mGameContext.getUserInputManager().setOnKeyDownListener(this, KeyCodeEnum.KeyLeft);
		mGameContext.getUserInputManager().setOnKeyDownListener(this, KeyCodeEnum.KeyRight);
		mGameContext.getUserInputManager().setOnKeyUpListener(this, KeyCodeEnum.KeyLeft);
		mGameContext.getUserInputManager().setOnKeyUpListener(this, KeyCodeEnum.KeyRight);
		
		setZeroSpeed();
	}

	@Override
	public void teardown()
	{
		mGameContext.getUserInputManager().removeOnKeyDownListener(this, KeyCodeEnum.KeyLeft);
		mGameContext.getUserInputManager().removeOnKeyDownListener(this, KeyCodeEnum.KeyRight);
		mGameContext.getUserInputManager().removeOnKeyUpListener(this, KeyCodeEnum.KeyLeft);
		mGameContext.getUserInputManager().removeOnKeyUpListener(this, KeyCodeEnum.KeyRight);
	}
	
	@Override
    public boolean update(GameTime gameTime)
    {
        return updatePosition(gameTime);
    }

	@Override
	public boolean onKeyUp(KeyCodeEnum keyCode)
	{
		switch (keyCode)
		{
			case KeyLeft:
			case KeyRight:
				setZeroSpeed();
				setZeroAcceleration();
				break;
				
			default :
				assert false;
				break;
		}
		
		return true;
	}

	@Override
	public boolean onKeyDown(KeyCodeEnum keyCode)
	{
		switch (keyCode)
		{
			case KeyLeft:
				setSpeed(reverse(mDefaultSpeed));
				setAcceleration();
				break;
				
			case KeyRight:
				setSpeed(mDefaultSpeed);
				setAcceleration();
				break;
				
			default :
				assert false;
				break;
		}
		
		return true;
	}

	@Override
	public void setInitialSpeed(Vector2 speed)
	{
		mDefaultSpeed.set(speed);
		
		super.setInitialSpeed(0, 0);
	}
	
	@Override
	public void setInitialAcceleration(Vector2 acceleration)
	{
		mDefaultAcceleration.set(acceleration);
		
		super.setInitialAcceleration(0, 0);
	}

	@Override
    protected void onOutOfBoundsX(OutOfBoundsEvent event)
    {
		setZeroSpeed();
		
		event.adjust = true;
    }
    
	@Override
    protected void onOutOfBoundsY(OutOfBoundsEvent event)
    {
		setZeroSpeed();
		
		event.adjust = true;
    }
    
	private void setZeroSpeed()
	{
		setSpeed(0, 0);
	}
	
	private void setZeroAcceleration()
	{
		setAcceleration(0, 0);
	}

	private void setAcceleration()
	{
		setAcceleration(mDefaultAcceleration);
	}

	private Vector2 reverse(Vector2 vect)
	{
		return new Vector2(-vect.X, -vect.Y);
	}
}
