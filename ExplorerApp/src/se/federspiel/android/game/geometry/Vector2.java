package se.federspiel.android.game.geometry;

import se.federspiel.android.game.utils.AMath;

public class Vector2
{
	public static final Vector2 Zero = new Vector2(0);
	
	public float X = 0;
	public float Y = 0;
	
	public Vector2(Vector2 vect)
	{
		this(vect.X, vect.Y);
	}
	
	public Vector2(float v)
	{
		this(v, v);
	}
	
	public Vector2(float x, float y)
	{
		X = x;
		Y = y;
	}

	public boolean isZero()
	{
		return ((-AMath.EPS < X) && (X < AMath.EPS) && (-AMath.EPS < Y) && (Y < AMath.EPS));
	}
	
	public float getMagnitude()
	{
		return calcMagnitude();
	}
	
	public Vector2 getNormal(Vector2 normal)
	{
		return AMath.calculateNormal(normal, this);
	}

	public Vector2 set(Vector2 vect)
	{
		X = vect.X;
		Y = vect.Y;
		
		return this;
	}
	
	public Vector2 set(float x, float y)
	{
		X = x;
		Y = y;
		
		return this;
	}

	public Vector2 normalize()
	{
		float mag = calcMagnitude();

		if (mag > 0)
		{
			X /= mag;
			Y /= mag;
		}
		
		return this;
	}

	public void addMagnitude(float magnitude)
	{
		float oldMag = calcMagnitude();
		float newMag = oldMag + magnitude;
		
		scale(newMag / oldMag);
	}
	
    public void clampMagnitude(float magnitude)
    {
    	assert magnitude > 0;
    	
		float oldMag = calcMagnitude();
		
		if (oldMag > magnitude)
		{
			scale(magnitude / oldMag);
		}
    }
    
    public Vector2 scale(float scale)
    {
    	X *= scale;
    	Y *= scale;
		
		return this;
    }
    
	public Vector2 clone()
	{
		return new Vector2(this);
	}
	
	public float dot(Vector2 vector)
	{
		return AMath.dot(this, vector);
	}
	
	public void reverse()
	{
		X = -X;
		Y = -Y;
	}

    public void subtract(Vector2 vector)
    {
		X -= vector.X;
		Y -= vector.Y;
    }
	
    public void add(Vector2 vector)
    {
		X += vector.X;
		Y += vector.Y;
    }

    public String toString()
    {
    	return "(x = " + X + ", y = " + Y + ")";
    }
    
    private float calcMagnitude()
	{
		return AMath.magnitude(this);
	}
}
