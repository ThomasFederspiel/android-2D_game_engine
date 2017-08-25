package se.federspiel.android.game.interfaces;

public interface ITrajectoryLimits
{
	public void moveLimits(float dx, float dy);
	public float limitX(IBounds bounds);
	public float limitY(IBounds bounds);
}
