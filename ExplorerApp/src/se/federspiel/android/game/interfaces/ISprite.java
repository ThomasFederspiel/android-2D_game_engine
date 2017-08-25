package se.federspiel.android.game.interfaces;

import se.federspiel.android.game.geometry.Dimensions;
import se.federspiel.android.game.geometry.Point;
import se.federspiel.android.game.geometry.Vector2;
import se.federspiel.android.game.trajectories.limits.OutOfBoundsEvent;

public interface ISprite extends IDrawableComponent, ITrajectoryControlledSprite
{
    public Dimensions getDimensions();

    public Point getPosition();
    public Vector2 getSpeed();
    public void setInitialPosition(Point position);
    public void setInitialPositionX(float x);
    public void setInitialPositionY(float y);
	public void setInitialSpeed(Vector2 speed);

    public ISpriteCollisionObject getCollisionObject();
    
    public void setOutOfBoundsListener(ISpriteOutOfBoundsListener listener);
    
    public interface ISpriteOutOfBoundsListener
    {
    	public boolean onOutOfBounds(ISprite sprite, OutOfBoundsEvent event);
    }
}
