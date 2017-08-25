package se.federspiel.android.game.interfaces;

import se.federspiel.android.game.GameRenderer;

public interface IUIWindow
{
	public void setEnable(boolean enable);
	public boolean isEnabled();
	
	public void loadContent();
	public void unloadContent();
	public void draw(GameRenderer renderer);
}
