package se.federspiel.android.game.trajectories;

import se.federspiel.android.game.GameTime;
import se.federspiel.android.game.interfaces.IGameContext;
import se.federspiel.android.game.interfaces.ITrajectoryControlledSprite;
import se.federspiel.android.game.trajectories.limits.OutOfBoundsEvent;

public class MovementTrajectory extends AbstractTrajectory
{
    public MovementTrajectory(IGameContext gameContext)
    {
    	super(gameContext);
    }

    public MovementTrajectory(IGameContext gameContext, ITrajectoryControlledSprite sprite)
    {
    	super(gameContext, sprite);
    }

	@Override
    public boolean update(GameTime gameTime)
    {
		return updatePosition(gameTime);
    }

	@Override
    protected void onOutOfBoundsX(OutOfBoundsEvent event)
    {
		reverseX();
		
		event.adjust = true;
    }
    
	@Override
    protected void onOutOfBoundsY(OutOfBoundsEvent event)
    {
		reverseY();
		
		event.adjust = true;
    }
}
