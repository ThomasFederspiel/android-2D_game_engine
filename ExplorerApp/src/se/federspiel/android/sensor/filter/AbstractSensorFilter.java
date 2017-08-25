package se.federspiel.android.sensor.filter;

public abstract class AbstractSensorFilter implements ISensorFilter
{
	private ISensorFilter mConnectedFilter = null;
	
	@Override
	public void connect(ISensorFilter filter)
	{
		if (mConnectedFilter == null)
		{
			mConnectedFilter = filter;
		}
		else
		{
			mConnectedFilter.connect(filter);
		}
	}

	@Override
	public float[] apply(float[] values)
	{
		float[] filteredValues = evaluateFilter(values);
		
		if (mConnectedFilter != null)
		{
			filteredValues = mConnectedFilter.apply(filteredValues);
		}
		
		return filteredValues;
	}
	
	protected abstract float[] evaluateFilter(float[] values);
}
