package se.federspiel.android.game.interfaces;

public interface IDrawableComponent extends IGraphicDrawer, IUpdatableComponent
{
	public enum DrawableZOrder
	{
		BACKGROUND_LAYER_1(0),
		BACKGROUND_LAYER_2(1),
		SPRITE_LAYER_1(2),
		SPRITE_LAYER_2(3),
		SPRITE_LAYER_3(4),
		UI_LAYER(5);
			
		private int mLayer = 0;
		
		private DrawableZOrder(int layer)
		{
			mLayer = layer;
		}
		
		public int layer()
		{
			return mLayer;
		}
		
		public static int nofLayers()
		{
			return 6;
		}
	}
	
    public DrawableZOrder getZOrder();
    public void setZOrder(DrawableZOrder level);
}
