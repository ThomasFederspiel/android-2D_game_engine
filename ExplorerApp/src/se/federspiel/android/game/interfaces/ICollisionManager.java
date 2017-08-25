package se.federspiel.android.game.interfaces;

import java.util.ArrayList;

import se.federspiel.android.game.collision.CollisionSet;
import se.federspiel.android.game.interfaces.ISpriteCollisionObject.ICollisionContext;

public interface ICollisionManager extends IDestroyable
{
	public CollisionSet createCollisionSet();
	public void enableCollisionSets(boolean enable);
	
    public void add(ISpriteCollisionObject collisionObject);
    public void remove(ISpriteCollisionObject collisionObject);

    public void update();
    
    public void setCollisionEvaluator(ICollisionEvaluator evaluator);
    public void setCollisionSelector(ICollisionSelector selector);
        
    public interface ICollisionLibrary
    {
    	public boolean isCollisionSetEnabled();
        public boolean isColliding(ISpriteCollisionObject collisionObjectOne, ISpriteCollisionObject collisionObjectTwo,
        		ICollisionContext contextOne, ICollisionContext contextTwo);
        public void evaluateRigidity(ISpriteCollisionObject collisionObjectOne,
    			ICollisionContext contextOne,
    			ISpriteCollisionObject collisionObjectTwo,
    			ICollisionContext contextTwo);
    }
    
    public interface ICollisionEvaluator
    {
    	public void evaluate(ICollisionLibrary library, ArrayList<ISpriteCollisionObject> objects, 
    			ICollisionSelector selector);
    }
    
    public interface ICollisionSelector
    {
    	public ArrayList<ISpriteCollisionObject> select(ArrayList<ISpriteCollisionObject> objects, ISpriteCollisionObject collisioner);
        public void add(ISpriteCollisionObject collisionObject);
        public void remove(ISpriteCollisionObject collisionObject);
        public void clear();
    }
}
