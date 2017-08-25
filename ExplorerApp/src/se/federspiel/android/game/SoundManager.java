package se.federspiel.android.game;

import se.federspiel.android.game.interfaces.ISoundManager;
import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.SparseIntArray;

public class SoundManager implements ISoundManager
{
	private SoundPool mSoundPool = null; 
	private AudioManager mAudioManager = null; 

	private GameContext mGameContext = null;
	
	private SparseIntArray mSoundMap = new SparseIntArray(); 
		 
	public SoundManager(GameContext gameContext) 
	{ 
		mGameContext = gameContext;
	    mSoundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 0); 
	    mAudioManager = (AudioManager) mGameContext.getApplContext().getSystemService(Context.AUDIO_SERVICE); 
	} 
		 
	public void addSound(int resourceId) 
	{ 
		if (mSoundMap.get(resourceId) == 0)
		{
			mSoundMap.put(resourceId, mSoundPool.load(mGameContext.getApplContext(), resourceId, 1)); 
		}
	} 
		 
	public void playSound(int resourceId) 
	{ 
		playSound(resourceId, false);
	}
	
	public void playSound(int resourceId, boolean loopSound) 
	{ 
	    float streamVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_RING); 
	    streamVolume = streamVolume / mAudioManager.getStreamMaxVolume(AudioManager.STREAM_RING); 
	 
	    int loop = 0;
	    
	    if (loopSound)
	    {
	    	loop = -1;
	    }
	    
	    int soundId = mSoundMap.get(resourceId);

	    assert soundId != 0;
	    
	    mSoundPool.play(soundId, streamVolume, streamVolume, 1, loop, 1f); 
	} 
	
	public void release()
	{
		mSoundPool.release();
	}
}
