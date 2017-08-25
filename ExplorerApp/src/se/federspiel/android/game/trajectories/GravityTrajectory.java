package se.federspiel.android.game.trajectories;

import se.federspiel.android.game.GameTime;
import se.federspiel.android.game.geometry.Vector2;
import se.federspiel.android.game.interfaces.IGameContext;
import se.federspiel.android.game.interfaces.ITrajectoryControlledSprite;

public class GravityTrajectory extends AbstractTrajectory
{
	private Vector2 mGravity = Vector2.Zero.clone();

	public GravityTrajectory(IGameContext gameContext)
    {
    	super(gameContext);
    }
    
	public GravityTrajectory(IGameContext gameContext, ITrajectoryControlledSprite sprite)
    {
    	super(gameContext, sprite);
    }

	public void setGravity(float x, float y)
	{
		mGravity.set(x, y);
		
		setAcceleration(mGravity);
	}
	
	@Override
	public void setup()
	{
		super.setup();
		
		setAcceleration(mGravity);
	}

	@Override
    public boolean update(GameTime gameTime)
    {
		return super.updatePosition(gameTime);
    }
}
