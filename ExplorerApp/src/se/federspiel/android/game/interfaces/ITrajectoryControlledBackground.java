package se.federspiel.android.game.interfaces;

import se.federspiel.android.backgrounds.trajectories.AbstractTrajectory;
import se.federspiel.android.game.geometry.Point;

public interface ITrajectoryControlledBackground
{
	public IBounds getBounds();
    public Point getPosition();
	public void onPositionChanged(Point position);
	public void setTrajectory(AbstractTrajectory trajectory);
}
