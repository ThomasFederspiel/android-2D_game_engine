package se.federspiel.android.game.interfaces;

import se.federspiel.android.game.GameTime;

public interface ISpriteAction extends ISpriteCollisionListener
{
	public boolean isYielding(ISprite sprite);
    public void update(ISprite sprite, GameTime gameTime);
    
    public void setSpriteCollsionListener(ISpriteCollisionListener listener);
}
