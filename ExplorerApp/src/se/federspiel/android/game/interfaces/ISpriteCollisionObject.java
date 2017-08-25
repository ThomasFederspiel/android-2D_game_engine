package se.federspiel.android.game.interfaces;

import se.federspiel.android.game.geometry.Point;
import se.federspiel.android.game.geometry.Ray;
import se.federspiel.android.game.geometry.Vector2;
import se.federspiel.android.game.sprites.PhysicalProperties;

public interface ISpriteCollisionObject extends ICollisionSprite // ICollisionObject
{
    public IBounds getBounds();
    
	public Ray getLastUpdateMovementRay();
	public Vector2 getLastUpdateSpeed();
	public Vector2 getSpeed();

	public PhysicalProperties getPhysicalProperties();
	public void setPhysicalProperties(PhysicalProperties properties);
	
	public boolean isStationary();
	public boolean isMoving();
	public boolean isYielding();
	
	public ICollisionContext getCollisionContext();
	
	public void setOnSpritePositionChangedListener(ISpritePositionChangedListener listener);
	public void removeOnSpritePositionChangedListener(ISpritePositionChangedListener listener);

    void onSpriteCollision(ICollisionContext context, ISpriteCollisionObject collidingObject);

    public interface ISpritePositionChangedListener
    {
    	public void onSpritePositionChanged(ISpriteCollisionObject collisionObject, Point oldPosition, Point newPosition);
    }
    
    public interface ICollisionContext
    {
		public void setCollisionNormal(Vector2 normal);
		public void setCorrectedMovement(Ray ray);
		
		/**
		 * Method for retrieval of the collision normal for the primary sprite, when colliding with the secondary sprite
		 * 
		 * @return The normal
		 */
    	public Vector2 getCollisionNormal();
    	
    	/**
    	 * Method for retrieval of the primary sprite's corrected movement. If both sprites, primary and secondary, moves 
    	 * according to their corrected movements they will appear touching one another after a collision 
    	 * 
    	 * @return
    	 */
    	public Ray getCorrectedMovement();

    	public void set(ICollisionContext value);
    }
}
