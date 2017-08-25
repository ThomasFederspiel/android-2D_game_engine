package se.federspiel.android.game.interfaces;

import se.federspiel.android.game.GameView;
import se.federspiel.android.game.interfaces.IDrawableComponent.DrawableZOrder;

public interface IGameEngine extends IDestroyable
{
	public enum GameViewCoordinateSystem
	{
		GAME_VIEW_ABSOLUTE,
		GAME_VIEW_RELATIVE
	}
	
	public void invokeOnGameThread(Runnable runnable);
	
	public GameView getGameView();
	
	public void enableGameView(boolean enable);
	
	public void resetLayerCoordinateSystem();
	public void setLayerCoordinateSystem(DrawableZOrder zOrder, GameViewCoordinateSystem coord);
	
	public void togglePause();
	public void pause();
	public void unPause();
	public IGameContext getGameContext();
	
	public void addComponent(IDrawableComponent component);
	public void addBackground(IDrawableComponent component);
	public void addUpdateComponent(IUpdatableComponent component);
	
	public void removeComponent(IDrawableComponent component);
	public void removeUpdateComponent(IUpdatableComponent component);
	public void removeBackground(IDrawableComponent component);
}
