package se.federspiel.android.game.trajectories.actions.snap;

import se.federspiel.android.game.interfaces.IGameContext;

public class SnapSoundAction implements ISnapAction
{
    private IGameContext mGameContext = null;
    private int mResourceId = -1;

	public SnapSoundAction(int resourceId, IGameContext gameContext)
	{
		mResourceId = resourceId;
		mGameContext = gameContext;	
	}
	
	@Override
	public void onSnap()
	{
    	mGameContext.getSoundManager().playSound(mResourceId, false);
	}
}
