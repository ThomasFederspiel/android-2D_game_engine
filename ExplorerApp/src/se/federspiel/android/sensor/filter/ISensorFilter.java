package se.federspiel.android.sensor.filter;

public interface ISensorFilter
{
	void connect(ISensorFilter filter);
	float[] apply(float[] values);
}
