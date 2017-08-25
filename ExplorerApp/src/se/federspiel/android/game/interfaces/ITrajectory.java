package se.federspiel.android.game.interfaces;

import se.federspiel.android.game.GameTime;
import se.federspiel.android.game.geometry.Point;
import se.federspiel.android.game.geometry.Ray;
import se.federspiel.android.game.geometry.Vector2;

public interface ITrajectory
{
	public enum AccelerationModeEnum
	{
		ACCELERATION_ABSOLUTE_MODE,
		ACCELERATION_DIRECTIONAL_MODE;
	}
	
	public enum IntegrationMethod
	{
		INTEGRATION_METHOD_EULER,
		INTEGRATION_METHOD_SYMPLECTIC_EULER,
		INTEGRATION_METHOD_POSITION_VERLET
	}
	
	public void setInitialPosition(Point point);
    public void setInitialPositionX(float x);
    public void setInitialPositionY(float y);
    public void setInitialSpeed(Vector2 vect);
    public void setInitialSpeed(float x, float y);
    public void setInitialAcceleration(Vector2 vect);
    public void setInitialAcceleration(float accX, float accY);
    
    public void setAccelerationMode(AccelerationModeEnum mode);
    public IntegrationMethod getIntegrationMethod();
    public void setIntegrationMethod(IntegrationMethod method);
    
    public Point getPosition();
    public void setPosition(Point point);
    public void setPosition(float x, float y);
    public void setPositionX(float x);
    public void setPositionY(float y);

    public Vector2 getSpeed();
    public float getSpeedX();
    public float getSpeedY();
    public void setSpeed(Vector2 vect);
    public void setSpeed(float x, float y);
    public void setSpeedX(float x);
    public void setSpeedY(float y);

    public Vector2 getAcceleration();
    public void setAcceleration(Vector2 vect);
    public void setAcceleration(float x, float y);
    public void setAccelerationX(float x);
    public void setAccelerationY(float y);

    public void addForce(Vector2 force);
    
    public ITrajectoryLimits getPositionLimits();
    public void setPositionLimits(ITrajectoryLimits limits);

    public Ray getMovementRay();
    public Vector2 getMovementSpeed();
	public boolean isStationary();
	public boolean isMoving();

    public boolean update(GameTime gameTime);
    public void setup();
    public void teardown();
}
