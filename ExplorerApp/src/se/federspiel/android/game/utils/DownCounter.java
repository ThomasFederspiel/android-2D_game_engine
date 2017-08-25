package se.federspiel.android.game.utils;

public class DownCounter
{
	private int mCounter = 0;
	
	public DownCounter(int count)
	{
		mCounter = count;
	}

	public boolean countDown()
	{
		if (mCounter > 0)
		{
			mCounter--;
		}
		
		return (mCounter == 0);
	}
}
