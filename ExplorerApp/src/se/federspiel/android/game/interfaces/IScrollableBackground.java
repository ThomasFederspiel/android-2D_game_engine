package se.federspiel.android.game.interfaces;


public interface IScrollableBackground extends IBackground
{
	public enum ScrollDirections
	{
		SCROLL_DIRECTION_NONE,
		SCROLL_DIRECTION_X,
		SCROLL_DIRECTION_Y,
		SCROLL_DIRECTION_XY
	}
	
    public void setScrollDirection(ScrollDirections direction);
}
