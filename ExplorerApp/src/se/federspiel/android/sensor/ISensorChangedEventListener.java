package se.federspiel.android.sensor;

public interface ISensorChangedEventListener 
{
	public void onAccuracyChanged(ISensor sensor, int accuracy);
	public void onSensorChanged(ISensor sensor);
}
