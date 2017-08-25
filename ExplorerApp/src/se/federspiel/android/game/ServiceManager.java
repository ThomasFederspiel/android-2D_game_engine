package se.federspiel.android.game;

import android.content.Context;
import android.os.Vibrator;
import se.federspiel.android.game.interfaces.IServiceManager;

public class ServiceManager implements IServiceManager
{
	private Context mContext = null;
	private Vibrator mVibrator = null;
	
	public ServiceManager(Context context)
	{
		mContext = context;

		mVibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
	}
	
	@Override
	public boolean hasVibrator()
	{
		return mVibrator.hasVibrator();
	}

	@Override
	public Vibrator getVibrator()
	{
		return mVibrator;
	}
}
