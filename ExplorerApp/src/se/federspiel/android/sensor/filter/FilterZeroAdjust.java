package se.federspiel.android.sensor.filter;


public class FilterZeroAdjust extends AbstractSensorFilter
{
	public enum ZeroAdjustFilterMode
	{
		AverageMode
	}

	private ZeroAdjustFilterMode mMode = ZeroAdjustFilterMode.AverageMode;
	private int mParam = 0;

	private float[][] mAverageMatrix = null;
	private float[] mAverage = null;
	private int mAverageCount = 0;
	
	public FilterZeroAdjust(ZeroAdjustFilterMode mode, int param)
	{
		mMode = mode;
		mParam = param;
	}

	public void reset()
	{
		mAverageMatrix = null;
	}
	
	@Override
	protected float[] evaluateFilter(float[] values)
	{
		float[] result = values;
		
		switch (mMode)
		{
			case AverageMode :
				result = averageImpl(values);
				break;
		}
		
		return result;
	}
	
	private float[] averageImpl(float[] values)
	{
		float[] result = values;
		
		if (mAverageMatrix == null)
		{
			mAverageCount = mParam;
			
			mAverageMatrix = new float[mAverageCount][values.length];

			mAverage = null;
			
			mAverageCount--;
			
			mAverageMatrix[mAverageCount] = values;
		}
		else
		{
			if (mAverage == null)
			{
				mAverageCount--;
	
				mAverageMatrix[mAverageCount] = values;
				
				if (mAverageCount == 0)
				{
					mAverage = new float[values.length];
	
					for (int i = 0; i < mAverageMatrix.length; i++)
					{
						for (int j = 0; j < mAverageMatrix[i].length; j++)
						{
							mAverage[j] += mAverageMatrix[i][j];
						}
					}
					
					for (int i = 0; i < mAverage.length; i++)
					{
						mAverage[i] /= mAverageMatrix.length;
					}
				}
			}
			else
			{
				for (int i = 0; i < mAverage.length; i++)
				{
					result[i] -= mAverage[i];
				}
			}
		}
		
		return result;
	}
}
