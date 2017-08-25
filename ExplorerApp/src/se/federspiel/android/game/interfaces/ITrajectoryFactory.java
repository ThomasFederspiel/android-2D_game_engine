package se.federspiel.android.game.interfaces;

public interface ITrajectoryFactory extends IDestroyable
{
    void registerTrajectory(String id, Class<? extends ITrajectory> trajectory);

    ITrajectory createTrajectory(String id);
    ITrajectory createTrajectory(String id, ITrajectoryControlledSprite controlleObject);
}
