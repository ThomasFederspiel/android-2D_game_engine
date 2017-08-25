package se.federspiel.android.game.interfaces;

import se.federspiel.android.game.geometry.Vector2;

public interface ICollisionSprite extends /* ISprite, */ ICollisionObject
{
    public Vector2 getSpeed();
    public void setCollisionAction(ISpriteAction action);
    public ITrajectory getTrajectory();
    public ISpriteCollisionObject getCollisionObject();
}
