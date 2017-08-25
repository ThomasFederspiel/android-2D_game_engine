package se.federspiel.android.game;

import static junit.framework.Assert.assertTrue;
import java.util.ArrayList;
import se.federspiel.android.game.interfaces.IConstraint;
import se.federspiel.android.util.ALog;

public class ConstraintsManager
{
    private GameContext mGameContext = null;

    private ConstraintsGroup mBaseConstraintsGroup = new ConstraintsGroup(1);
    
    private ArrayList<ConstraintsGroup> mConstraints = new ArrayList<ConstraintsGroup>();

    private boolean mIsConstraintPending = false;
    
    private ArrayList<IConstraint> mConstraintsAddQueue = new ArrayList<IConstraint>();
    private ArrayList<IConstraint> mConstraintsRemoveQueue = new ArrayList<IConstraint>();

    private ArrayList<ConstraintsGroup> mConstraintGroupsAddQueue = new ArrayList<ConstraintsGroup>();
    private ArrayList<ConstraintsGroup> mConstraintGroupsRemoveQueue = new ArrayList<ConstraintsGroup>();
        
    
    private boolean mIsUpdating = false;

    public ConstraintsManager(GameContext gameContext)
    {
        mGameContext = gameContext;
        
        mConstraints.add(mBaseConstraintsGroup);
    }

    public void add(IConstraint constraint)
    {
    	if (mIsUpdating)
    	{
    		mConstraintsAddQueue.add(constraint);
    		mIsConstraintPending = true;
    	}
    	else
    	{
    		mBaseConstraintsGroup.add(constraint);
    	}
    }
    
    public void remove(IConstraint constraint)
    {
    	if (mIsUpdating)
    	{
    		mConstraintsRemoveQueue.add(constraint);
    		mIsConstraintPending = true;
    	}
    	else
    	{
    		mBaseConstraintsGroup.remove(constraint);
    	}
    }

    public void add(ConstraintsGroup group)
    {
    	if (mIsUpdating)
    	{
    		mConstraintGroupsAddQueue.add(group);
    		mIsConstraintPending = true;
    	}
    	else
    	{
			assertTrue(mConstraints.contains(group) == false);
			
    		mConstraints.add(group);
    	}
    }
    
    public void remove(ConstraintsGroup group)
    {
    	if (mIsUpdating)
    	{
    		mConstraintGroupsRemoveQueue.add(group);
    		mIsConstraintPending = true;
    	}
    	else
    	{
	  		boolean removed = mConstraints.remove(group);
			
			assertTrue(removed == true);
    	}
    }
    
    public void update()
    {

    	ALog.debug(this, "Constraints check start -------");
    	
    	mIsUpdating = true;
    	for (int i = 0; i < mConstraints.size(); i++)
    	{
    		mConstraints.get(i).update();
    	}
    	mIsUpdating = false;

    	ALog.debug(this, "Constraints check end -------");
    	
    	if (mIsConstraintPending);
    	{	
    		processQueues();
    		mIsConstraintPending = false;
    	}
    }
    
    private void processQueues()
    {
    	if (mConstraintsRemoveQueue.size() > 0)
    	{
        	for (int i = 0; i < mConstraintsRemoveQueue.size(); i++)
        	{
        		mBaseConstraintsGroup.remove(mConstraintsRemoveQueue.get(i));
        	}
        	
        	mConstraintsRemoveQueue.clear();
    	}
    	
    	if (mConstraintsAddQueue.size() > 0)
    	{
        	for (int i = 0; i < mConstraintsAddQueue.size(); i++)
        	{
        		mBaseConstraintsGroup.add(mConstraintsAddQueue.get(i));
        	}
        	
        	mConstraintsAddQueue.clear();
    	}
    	
    	if (mConstraintGroupsRemoveQueue.size() > 0)
    	{
        	for (int i = 0; i < mConstraintGroupsRemoveQueue.size(); i++)
        	{
        		mConstraints.remove(mConstraintGroupsRemoveQueue.get(i));
        	}
        	
        	mConstraintGroupsRemoveQueue.clear();
    	}
    	
    	if (mConstraintGroupsAddQueue.size() > 0)
    	{
        	for (int i = 0; i < mConstraintGroupsAddQueue.size(); i++)
        	{
        		mConstraints.add(mConstraintGroupsAddQueue.get(i));
        	}
        	
        	mConstraintGroupsAddQueue.clear();
    	}
    }
    
    public static class ConstraintsGroup
    {
        private ArrayList<IConstraint> mConstraints = new ArrayList<IConstraint>();
        
    	private int mIterations = 1;

    	public ConstraintsGroup(int iterations)
    	{
    		assert iterations > 0;
    		
    		mIterations = iterations;
    	}
    	
        public void add(IConstraint constraint)
        {
			assertTrue(mConstraints.contains(constraint) == false);
			
    		mConstraints.add(constraint);
        }

        public void remove(IConstraint constraint)
        {
	  		boolean removed = mConstraints.remove(constraint);
			
			assertTrue(removed == true);
        }
        
		public void update()
        {
        	for (int iter = 0; iter < mIterations; iter++)
        	{
	        	for (int i = 0; i < mConstraints.size(); i++)
		    	{
		    		mConstraints.get(i).update();
		    	}
        	}
        }
    }
}
