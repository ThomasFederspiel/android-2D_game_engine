package se.federspiel.android.game;

public class GameTime
{
	public static final int FPS = 25;
    public static final long FPS_TIME_NS = (long) (1E9 / FPS);
    public static final long FPS_TIME_MS = (long) (1E3 / FPS);
    public static final float FPS_TIME_S = (1.0f / FPS);

	private long mElapsedTimeNs = 0;
	private int mFps = 1;

	public long getElapsedTime()
	{
		return mElapsedTimeNs;
	}

	public void setElapsedTime(long elapsedTimeNs)
	{
		mElapsedTimeNs = elapsedTimeNs;
	}

	public int getFPS()
	{
		return mFps;
	}

	public void setFPS(int fps)
	{
		mFps = fps;
	}
}
