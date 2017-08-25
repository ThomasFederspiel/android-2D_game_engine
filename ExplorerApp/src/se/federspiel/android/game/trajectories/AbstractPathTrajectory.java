package se.federspiel.android.game.trajectories;

import se.federspiel.android.game.geometry.Point;
import se.federspiel.android.game.geometry.Vector2;
import se.federspiel.android.game.interfaces.IGameContext;
import se.federspiel.android.game.interfaces.ITrajectoryControlledSprite;

public abstract class AbstractPathTrajectory extends AbstractTrajectory
{
	public enum Direction
	{
		FORWARD,
		BACKWARDS,
		BACK_AND_FORTH
	}

	protected int mInitialRepetitions = 1;
	protected Direction mDirection = Direction.FORWARD;
	protected Direction mBackAndForthDirection = Direction.FORWARD;
	protected Point[] mPathPositions = null;
	
    public AbstractPathTrajectory(IGameContext gameContext, ITrajectoryControlledSprite sprite)
    {
    	super(gameContext, sprite);
    }

	@Override
    public void setInitialAcceleration(Vector2 acceleration)
	{
		throw new UnsupportedOperationException();
	}
	
	@Override
    public void setAccelerationMode(AccelerationModeEnum mode)
	{
		throw new UnsupportedOperationException();
	}
	
	public void definePath(Direction direction, int repetitions, Point[] path)
	{
    	assert repetitions > 0;
    	
    	mDirection = direction;
    	mPathPositions = path;
    	mInitialRepetitions = repetitions;
	}
}
