package se.federspiel.android.game.sprites;

import se.federspiel.android.game.geometry.Point;
import se.federspiel.android.game.geometry.Vector2;
import se.federspiel.android.game.interfaces.ITrajectory.IntegrationMethod;

public abstract class AbstractConstraintObject
{
	public abstract Point getPosition();
	public abstract Point getMassCenter();
    public abstract void setPosition(Point position);
    public abstract void setPosition(float x, float y);
	public abstract Vector2 getSpeed();
	public abstract void setSpeed(float x, float y);
    public abstract void addForce(Vector2 force);
    public abstract IntegrationMethod getIntegrationMethod();
	public abstract PhysicalProperties getPhysicalProperties();
}
