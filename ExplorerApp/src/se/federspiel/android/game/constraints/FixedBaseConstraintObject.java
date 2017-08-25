package se.federspiel.android.game.constraints;

import se.federspiel.android.game.geometry.Point;
import se.federspiel.android.game.geometry.Vector2;
import se.federspiel.android.game.interfaces.ITrajectory.IntegrationMethod;
import se.federspiel.android.game.sprites.AbstractConstraintObject;
import se.federspiel.android.game.sprites.PhysicalProperties;

public class FixedBaseConstraintObject extends AbstractConstraintObject
{
	private Point mPosition = Point.Zero.clone();
	private Vector2 mSpeed = Vector2.Zero.clone();

	private PhysicalProperties mPhysicalProperties = new PhysicalProperties();
	
	public FixedBaseConstraintObject(Point position)
	{
		mPosition.set(position);
	}
	
	@Override
	public Point getPosition()
	{
		return mPosition;
	}

	@Override
	public Point getMassCenter()
	{
		return mPosition;
	}

	@Override
	public Vector2 getSpeed()
	{
		return mSpeed;
	}

	@Override
	public void addForce(Vector2 force)
	{
	}

	@Override
	public void setPosition(Point position)
	{
	}
	
	@Override
	public void setPosition(float x, float y)
	{
	}

	@Override
	public void setSpeed(float x, float y)
	{
	}

	@Override
	public IntegrationMethod getIntegrationMethod()
	{
		return IntegrationMethod.INTEGRATION_METHOD_EULER;
	}

	@Override
	public PhysicalProperties getPhysicalProperties()
	{
		return mPhysicalProperties;
	}
}
