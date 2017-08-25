package se.federspiel.android.game.interfaces;

import android.os.Vibrator;

public interface IServiceManager
{
	public boolean hasVibrator();

	public Vibrator getVibrator();
}
