package se.federspiel.android.game.interfaces;

import se.federspiel.android.game.geometry.Dimensions;
import se.federspiel.android.game.geometry.Point;
import se.federspiel.android.game.sprites.PhysicalProperties;
import se.federspiel.android.game.trajectories.limits.OutOfBoundsEvent;

public interface ITrajectoryControlledSprite
{
    public Dimensions getDimensions();
    public IBounds getBounds();
	public PhysicalProperties getPhysicalProperties();
    
    public ITrajectory getTrajectory();
    public void setTrajectory(ITrajectory trajectory);
    
    public void onPositionChanged(Point oldPostion, Point newPosition);
    public boolean onOutOfBounds(OutOfBoundsEvent event);
}
