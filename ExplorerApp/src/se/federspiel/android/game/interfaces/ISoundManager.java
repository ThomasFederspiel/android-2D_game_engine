package se.federspiel.android.game.interfaces;

public interface ISoundManager
{
	public void addSound(int resourceId);
	public void playSound(int resourceId, boolean loopSound);
	public void playSound(int resourceId);
	public void release();
}
