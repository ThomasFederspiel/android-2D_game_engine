package se.federspiel.android.game.interfaces;

import se.federspiel.android.game.collision.CollisionSet;

public interface ICollisionObject
{
    public ICollisionBound getCollisionBounds();
    
    public CollisionSet getCollisionSet();
    public void setCollisionSet(CollisionSet set);
}

