package se.federspiel.android.sensor.meters;

import se.federspiel.android.sensor.PressureSensor;

public class AltitudeMeter
{
	private static final int AIR_DENSITY = 998; // [kg/m^2] 
	private static final double GRAVITY = 9.81; // [m/s^2]
	
	private PressureSensor mSensor = null;
	
	public AltitudeMeter(PressureSensor sensor)
	{
		mSensor = sensor;
	}

	/**
	 * 
	 * @param referencePressure in [mBar]
	 * @param targetPressure in [mBar]
	 * @return in [m]
	 */
	private double calculateAltitudeDelta(int referencePressure, int targetPressure)
	{
		return ((targetPressure - referencePressure) * 100) / AIR_DENSITY / GRAVITY;
	}
	
}
