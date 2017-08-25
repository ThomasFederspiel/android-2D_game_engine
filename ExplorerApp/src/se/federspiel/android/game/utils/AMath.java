package se.federspiel.android.game.utils;

import java.util.ArrayList;
import java.util.Random;

import se.federspiel.android.game.geometry.Circle;
import se.federspiel.android.game.geometry.Point;
import se.federspiel.android.game.geometry.Rectangle;
import se.federspiel.android.game.geometry.Vector2;
import se.federspiel.android.game.interfaces.IBounds;

public class AMath
{
	public static final float EPS = 0.000000001f;

	public static Random sRandom = new Random();

	private static Vector2 sGCInsideCircleVector = Vector2.Zero.clone();
	
	public static <T> T[] shuffle(T[] array) 
	{
		int n = array.length;
		
		for (int i = 0; i < n; i++) 
		{
			int change = i + randomInt(n - i);
			swap(array, i, change);
		}
		
		return array;
	}

	public static <T> void swap(T[] array, int i, int change) 
	{
		T helper = array[i];
		array[i] = array[change];
		array[change] = helper;
	}
	
	public static int randomInt(int lower, int upper)
	{
		return lower + (int) Math.round(Math.random() * (upper - lower));
	}
	
	public static boolean randomBool()
	{
		return sRandom.nextBoolean();
	}

	/*
	 * Returns a pseudo-random uniformly distributed int in the half-open range [0, n).
	 */
	public static int randomInt(int n)
	{
		return sRandom.nextInt(n);
	}

	public static float distance(Point point1, Point point2)
    {
    	return (float) Math.sqrt(distanceSqr(point1, point2));
    }

    public static float distanceSqr(Point point1, Point point2)
    {
    	float dx = point1.X - point2.X;
    	float dy = point1.Y - point2.Y;
    	
    	return ((dx * dx) + (dy * dy));
    }

    public static boolean insideCircle(Point center, float radius, Point position)
    {
    	Vector2 distance = subtract(position, center, sGCInsideCircleVector);
    	
    	return ((radius * radius) > ((distance.X * distance.X) + (distance.Y * distance.Y)));
    }
    
    public static float magnitude(Vector2 vector)
    {
        return (float) Math.sqrt(magnitudeSqr(vector));
    }

    public static float magnitudeSqr(Vector2 vector)
    {
        return ((vector.X * vector.X) + (vector.Y * vector.Y));
    }

    public static float dot(Vector2 vect1, Vector2 vect2)
	{
		float prod = vect1.X * vect2.X + vect1.Y * vect2.Y;
		
		return prod;
	}

	public static Vector2 subtract(Point point1, Point point2, Vector2 result)
	{
		result.set(point1.X - point2.X, point1.Y - point2.Y);
		
		return result;
	}
	
	public static Point subtract(Point point, Vector2 vector, Point result)
	{
		result.set(point.X - vector.X, point.Y - vector.Y);
		
		return result;
	}

	public static Point add(Point point, Vector2 vector, Point result)
	{
		result.set(point.X + vector.X, point.Y + vector.Y);
		
		return result;
	}

	public static void addToFirst(Point point1, Point point2)
	{
		point1.X += point2.X;
		point1.Y += point2.Y;
	}

	public static void subtractFromFirst(Point point1, Point point2)
	{
		point1.X -= point2.X;
		point1.Y -= point2.Y;
	}
	
	public static Vector2 reflection(Vector2 incident, Vector2 normal, Vector2 result)
	{
		// Rr = Ri - 2 N (Ri . N) 
		
		float dot = dot(incident, normal);
		
		float reflectX = incident.X - 2 * normal.X * dot;
		float reflectY = incident.Y - 2 * normal.Y * dot;
		
		result.set(reflectX, reflectY);
		
		return result;
	}

	public static Vector2 calculateNormal(Vector2 result, Vector2 vector)
	{
		result.X = -vector.Y;
		result.Y = vector.X;	
		
		result.normalize();
	
		return result;
	}
	
	public static Rectangle minkowskiSumTopLeft(IBounds rect1, IBounds rect2, Rectangle sumRect)
	{
		float yTop = rect1.getTop() - rect2.getHeight();
		float xLeft = rect1.getLeft() - rect2.getWidth();
		
		int width = rect1.getWidth() + rect2.getWidth();
		int height = rect1.getHeight() + rect2.getHeight();
		
		sumRect.setPosition(xLeft, yTop);
		sumRect.setDimensions(width, height);
		
		return sumRect;
	}
	
	public static Rectangle minkowskiSum(IBounds rect1, Circle circle, Rectangle sumRect)
	{
		float yTop = rect1.getTop() - circle.getRadius();
		float xLeft = rect1.getLeft() - circle.getRadius();
		
		int width = rect1.getWidth() + circle.getRadius() * 2;
		int height = rect1.getHeight() + circle.getRadius() * 2;

		sumRect.setPosition(xLeft, yTop);
		sumRect.setDimensions(width, height);
		
		return sumRect;
	}
	
    public static BezierFactors bezierCalculateFactors(Point point1, Point point2, Point point3, Point point4, int steps, BezierFactors factors)
    {
		float subdiv_step  = 1.0f / (steps + 1);
		float subdiv_step2 = subdiv_step * subdiv_step;
		float subdiv_step3 = subdiv_step * subdiv_step * subdiv_step;

		float pre1 = 3.0f * subdiv_step;
		float pre2 = 3.0f * subdiv_step2;
		float pre4 = 6.0f * subdiv_step2;
		float pre5 = 6.0f * subdiv_step3;

		float tmp1x = point1.X - point2.X * 2.0f + point3.X;
		float tmp1y = point1.Y - point2.Y * 2.0f + point3.Y;

		float tmp2x = (point2.X - point3.X) * 3.0f - point1.X + point4.X;
		float tmp2y = (point2.Y - point3.Y) * 3.0f - point1.Y + point4.Y;

		factors.mSteps = steps;
		
		factors.mFirstPoint = point1;
		factors.mLastPoint = point4;
		
		factors.mDfx = (point2.X - point1.X) * pre1 + tmp1x * pre2 + tmp2x * subdiv_step3;
		factors.mDfy = (point2.Y - point1.Y) * pre1 + tmp1y * pre2 + tmp2y * subdiv_step3;

		factors.mDdfx = tmp1x * pre4 + tmp2x * pre5;
		factors.mDdfy = tmp1y * pre4 + tmp2y * pre5;

		factors.mDddfx = tmp2x * pre5;
		factors.mDddfy = tmp2y * pre5;

		return factors;
    }
    
	public static Point[] bezierInterpolateControlPoints(Point[] points, float scale)
	{
	  	Point[] controlPoints = points;
	  	
	  	if (points.length >= 2)
	  	{
	    	controlPoints = new Point[points.length * 3 - 2];

	    	boolean cyclic = false;
	    	
	    	if (points[0].isSame(points[points.length - 1]))
	    	{
	    		cyclic = true;
	    	}
	    	
	    	for (int i = 0; i < (points.length - 1); i++)
	    	{
	    		Point p0;
	    		
	    		if (i > 0)
	    		{
		    		p0 = points[i - 1];
	    		}
	    		else
	    		{
		    		if (cyclic)
		    		{
			    		p0 = points[points.length - 2];
		    		}
		    		else
		    		{
			    		p0 = points[i];
		    		}
	    		}
	    		
	    		Point p1 = points[i];
	    		Point p2 = points[i + 1];
	    		
	    		Point p3;

	    		if (i < (points.length - 2))
	    		{
		    		p3 = points[i + 2];
	    		}
	    		else
	    		{
		    		if (cyclic)
		    		{
			    		p3 = points[1];
		    		}
		    		else
		    		{
			    		p3 = points[i + 1];
		    		}
	    		}
	    		
			    float xc1 = (p0.X + p1.X) / 2;
			    float yc1 = (p0.Y + p1.Y) / 2;
			    float xc2 = (p1.X + p2.X) / 2;
			    float yc2 = (p1.Y + p2.Y) / 2;
			    float xc3 = (p2.X + p3.X) / 2;
			    float yc3 = (p2.Y + p3.Y) / 2;
	
			    float len1 = (float) Math.sqrt((p1.X - p0.X) * (p1.X - p0.X) + (p1.Y - p0.Y) * (p1.Y - p0.Y));
			    float len2 = (float) Math.sqrt((p2.X - p1.X) * (p2.X - p1.X) + (p2.Y - p1.Y) * (p2.Y - p1.Y));
			    float len3 = (float) Math.sqrt((p3.X - p2.X) * (p3.X - p2.X) + (p2.Y - p2.Y) * (p3.Y - p2.Y));
		
			    float k1 = len1 / (len1 + len2);
			    float k2 = len2 / (len2 + len3);
		
			    float xm1 = xc1 + (xc2 - xc1) * k1;
			    float ym1 = yc1 + (yc2 - yc1) * k1;
		
			    float xm2 = xc2 + (xc3 - xc2) * k2;
			    float ym2 = yc2 + (yc3 - yc2) * k2;
		
			    // Resulting control points. Here smooth_value is mentioned
				// above coefficient K whose value should be in range [0...1].
			    float ctrl1_x = xm1 + (xc2 - xm1) * scale + p1.X - xm1;
			    float ctrl1_y = ym1 + (yc2 - ym1) * scale + p1.Y - ym1;
			
			    float ctrl2_x = xm2 + (xc2 - xm2) * scale + p2.X - xm2;
			    float ctrl2_y = ym2 + (yc2 - ym2) * scale + p2.Y - ym2;
			    
			    int ctrlIndex = i * 3;
			    controlPoints[ctrlIndex] = p1;
			    controlPoints[ctrlIndex + 1] = new Point(ctrl1_x, ctrl1_y);
			    controlPoints[ctrlIndex + 2] = new Point(ctrl2_x, ctrl2_y);
			    controlPoints[ctrlIndex + 3] = p2;
	    	}  
	  	}
	  	
	  	return controlPoints;
  	}

	public static ArrayList<Point> generateBezierLines(Point[] path, int steps, float scale)
	{
		ArrayList<Point> bezierPath = new ArrayList<Point>();
		
		Point[] controlPoints = bezierInterpolateControlPoints(path, scale);

		int size = 1 + (controlPoints.length - 4) / 3;
		
		BezierFactors[] factors = new BezierFactors[size];

		for (int i = 1; i < controlPoints.length; i += 3)
		{
			factors[i / 3] = new BezierFactors();
			 
			Point point1 = controlPoints[i - 1];
			Point point2 = controlPoints[i];
			Point point3 = controlPoints[i + 1];
			Point point4 = controlPoints[i + 2];
			
			AMath.bezierCalculateFactors(point1, point2, point3, point4, steps, factors[i / 3]);
		}

		BezierFactors tmpFactor = new BezierFactors();
		
		if (factors.length > 0)
		{
			Point previousPoint = null;
			
			bezierPath.add(factors[0].mFirstPoint);
			
			for (int i = 0; i < factors.length; i++)
			{
				tmpFactor.set(factors[i]);

				previousPoint = factors[i].mFirstPoint;
				
				for (int j = 0; j <= tmpFactor.mSteps; j++)
				{
					float newX = previousPoint.X + tmpFactor.mDfx;
					float newY = previousPoint.Y + tmpFactor.mDfy;
	
					previousPoint = new Point(newX, newY);
					
					bezierPath.add(previousPoint);
					
					tmpFactor.takeAStep();
				}
			}
		}
		
		return bezierPath;
	}

    public static class BezierFactors
    {
    	public float mDfx = 0;
    	public float mDfy = 0;

    	public float mDdfx = 0;
    	public float mDdfy = 0;

    	public float mDddfx = 0;
    	public float mDddfy = 0;

    	public int mSteps = 0;
    	
    	public Point mFirstPoint = null;
    	public Point mLastPoint = null;

    	private static BezierFactors sTmpFactors = new BezierFactors();
    	
    	public void set(BezierFactors factors)
    	{
	    	mDfx = factors.mDfx;
	    	mDfy = factors.mDfy;
	
	    	mDdfx = factors.mDdfx;
	    	mDdfy = factors.mDdfy;
	
	    	mDddfx = factors.mDddfx;
	    	mDddfy = factors.mDddfy;
	    	
	    	mSteps = factors.mSteps;
	    	
	    	mFirstPoint = factors.mFirstPoint;
	    	mLastPoint = factors.mLastPoint;
    	}
    	
		public void takeAStep()
		{
	        mDfx  += mDdfx;
	        mDfy  += mDdfy;
	        mDdfx += mDddfx;
	        mDdfy += mDddfy;
		}
		
		public float pathLength()
		{
			float length = 0;

			sTmpFactors.set(this);
			
			for (int i = 0; i < mSteps; i++)
			{
				length += Math.sqrt((sTmpFactors.mDfx * sTmpFactors.mDfx) + (sTmpFactors.mDfy * sTmpFactors.mDfy));
			
				sTmpFactors.takeAStep();
			}
			
			return length;
		}
    } 
}
