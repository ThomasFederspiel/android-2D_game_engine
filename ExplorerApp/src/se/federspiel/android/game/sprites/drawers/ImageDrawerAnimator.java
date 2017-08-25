package se.federspiel.android.game.sprites.drawers;

import java.util.ArrayList;

import se.federspiel.android.game.GameTime;

public class ImageDrawerAnimator
{
	private ArrayList<IAnimationAction[]> mAnimations = new ArrayList<IAnimationAction[]>();
	
	private IAnimationAction[] mCurrentAnimation = null;
	private IAnimationAction mCurrentStep = null;

	private int mAnimationStep = 0;
	
	private IAnimationContext mAnimationContext = null;

	public void setContext(IAnimationContext animationContext)
	{
		mAnimationContext = animationContext;		
	}
	
	public void addAnimation(IAnimationAction[] animation)
	{
		assert animation != null;
		
		mAnimations.add(animation);
	}

	public void activateAnimation(int id)
	{
		assert id < mAnimations.size();
		
		mCurrentAnimation = mAnimations.get(id);
		
		mAnimationStep = 0;
		
		mCurrentStep = mCurrentAnimation[mAnimationStep];
		
		mCurrentStep.prepare();
	}
	
    public void update(GameTime gameTime)
    {
    	if (mCurrentStep != null)
    	{
    		int move  = mCurrentStep.execute(mAnimationContext);
    		
    		switch (move)
    		{
	    		case IAnimationAction.STAY :
	    			// Stay put
	    			break;
	    			
	    		case IAnimationAction.CONTINUE :
	    			mAnimationStep++;
	    			
	    			if (mAnimationStep < mCurrentAnimation.length)
	    			{
	    				mCurrentStep = mCurrentAnimation[mAnimationStep];
	    				
	    				mCurrentStep.prepare();
	    			}
	    			else
	    			{
	    				mCurrentStep = null;
	    				mCurrentAnimation = null;
	    			}
	    			
	    			break;
	    			
	    		default :
	    			assert move < mCurrentAnimation.length;
	    			
	    			mAnimationStep = move;
	    			
    				mCurrentStep = mCurrentAnimation[mAnimationStep];
    				
    				mCurrentStep.prepare();
    				
	    			break;
    		}
    	}
    }

    public interface IAnimationContext
    {
    	public void selectImageDrawer(int id);
    }
    
    public static interface IAnimationAction
	{
		public static int CONTINUE = -2;
		public static int STAY = -1;
		
		public void prepare();
		public int execute(IAnimationContext context);
	}
	
	public static class DrawAction implements IAnimationAction
	{
		private int mImageId = 0;
		private int mFrameWait = 0;
		
		private int mFrameCount = 0;

		public DrawAction(int imageId, int waitInMs)
		{
			assert waitInMs >= 0;
			
			mImageId = imageId;
			mFrameWait = waitInMs / (int) GameTime.FPS_TIME_MS;
		}
		
		@Override
		public void prepare()
		{
			mFrameCount = mFrameWait;
		}

		@Override
		public int execute(IAnimationContext context)
		{
			int move = STAY;
			
			if (mFrameCount == mFrameWait)
			{
				context.selectImageDrawer(mImageId);
			}

			if (mFrameCount > 0)
			{
				mFrameCount--;
			}
			
			if (mFrameCount <= 0)
			{
				move = CONTINUE;
			}
			
			return move;
		}
	}
	
	
	public static class RepeatAction implements IAnimationAction
	{
		private int mRepeatValue = 0;
		private int mGotoStep = 0;
		
		private int mRepeateCount = 0;
		
		public RepeatAction(int repeatValue, int gotoStep)
		{
			assert mRepeatValue >= 0;
			assert mGotoStep >= 0;
			
			mRepeatValue = repeatValue;
			mGotoStep = gotoStep;
		}
		@Override
		public void prepare()
		{
			mRepeateCount = mRepeatValue;
		}

		@Override
		public int execute(IAnimationContext context)
		{
			int move = CONTINUE;
			
			if (mRepeateCount > 0)
			{
				move = mGotoStep;
				
				mRepeateCount--;
			}

			return move;
		}
	}
}
