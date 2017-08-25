package se.federspiel.android.game.trajectories.limits;

public class OutOfBoundsEvent
{
	public enum OutOfBoundsLimit
	{
		BOUNDS_LIMIT_LEFT,
		BOUNDS_LIMIT_RIGHT,
		BOUNDS_LIMIT_TOP,
		BOUNDS_LIMIT_BOTTOM
	}

	public OutOfBoundsLimit limitReached = OutOfBoundsLimit.BOUNDS_LIMIT_LEFT;

	public float distance = 0;
	public float position = 0;
	public boolean adjust = false;

	public void setup(float dist, float pos)
	{
		adjust = false;
		distance = dist;
		position = pos;
	}
	
	public boolean isOutOfBounds(float size)
	{
		return (Math.abs(distance) >= size);
	}
}
