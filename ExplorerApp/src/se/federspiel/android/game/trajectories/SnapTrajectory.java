package se.federspiel.android.game.trajectories;

import se.federspiel.android.game.GameTime;
import se.federspiel.android.game.geometry.Point;
import se.federspiel.android.game.geometry.Vector2;
import se.federspiel.android.game.interfaces.IGameContext;
import se.federspiel.android.game.interfaces.ITrajectoryControlledSprite;
import se.federspiel.android.game.trajectories.actions.snap.ISnapAction;
import se.federspiel.android.game.utils.AMath;

public class SnapTrajectory extends AbstractTrajectory
{
	private Point[] mSnapPoints = null;
	private int mSnapRadius = 0;

	private boolean mSnapActive = true;
	
	private ISnapAction mSnapAction = null;
	
    public SnapTrajectory(IGameContext gameContext)
    {
    	super(gameContext);
    }
    
	public SnapTrajectory(IGameContext gameContext, ITrajectoryControlledSprite sprite)
    {
    	super(gameContext, sprite);
    }

	public void setSnapAction(ISnapAction snapAction)
	{
		mSnapAction = snapAction;
	}
	
	public void setSnapPoints(Point[] snapPoints, int snapRadius)
	{
		mSnapPoints = snapPoints;
		mSnapRadius = snapRadius;
	}
	
	@Override
    public boolean update(GameTime gameTime)
    {
		boolean updated = false;
		
		if (mSnapActive)
		{
			for (int i = 0; i < mSnapPoints.length; i++)
			{
				Point snapPoint = mSnapPoints[i];
				
				if (AMath.insideCircle(snapPoint, mSnapRadius, mPosition))
				{
					if (!snapPoint.isSame(mPosition))
					{
						mPreUpdatePosition.set(mPosition);
// ;+			    	mPreUpdateSpeed.set(mSpeed);
				    	
				    	mPosition.set(snapPoint);
						mSpeed.set(Vector2.Zero);
						mAcceleration.set(Vector2.Zero);
	
						updateControlledObjectsPosition(mPreUpdatePosition, mPosition);

						if (mSnapAction != null)
						{
							mSnapAction.onSnap();
						}
						
						updated = true;
					}
					
					break;
				}
			}
		}
		
		return updated;
    }
}
