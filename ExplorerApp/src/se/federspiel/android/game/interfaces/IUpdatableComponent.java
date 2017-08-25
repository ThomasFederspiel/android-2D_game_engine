package se.federspiel.android.game.interfaces;

import se.federspiel.android.game.GameTime;

public interface IUpdatableComponent
{
    public void update(GameTime gameTime);
    public void loadContent();
    public void unloadContent();	
}
