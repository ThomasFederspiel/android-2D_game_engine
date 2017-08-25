package se.federspiel.android.game.geometry;

public class RectangleC extends Rectangle
{
	public static final RectangleC Zero = new RectangleC(0, 0, 0, 0);
	
	public RectangleC(RectangleC rectangle)
	{
		super(rectangle);
	}
	
	public RectangleC(float left, float top, int width, int height)
	{
		super(left, top, width, height);
	}
	
	@Override
    public Point getPosition()
    {
    	return mCenter;
    }

	@Override
    public void setPosition(float x, float y)
    {
		mTopLeft.set(x - mOffset.X, y - mOffset.Y);
		
    	mCenter.X = x;
    	mCenter.Y = y;
    }
    
	@Override
    public void setDimensions(Dimensions dims)
    {
		setDimensions(dims.getWidth(), dims.getHeight());
    }
    
	@Override
    public void setDimensions(int width, int height)
    {
		float hWidth = width / 2;
		float hHeight = height / 2;
		
		mTopLeft.set(mCenter.X - hWidth, mCenter.Y - hHeight);

		super.setDimensions(width, height);
    }
    
	@Override
	public void copy(Rectangle rectangle, float x, float y)
	{
		rectangle.setPosition(x - mOffset.X, y - mOffset.Y);
		rectangle.setDimensions(mDimensions);
	}
	
	public RectangleC clone()
	{
		return new RectangleC(this);
	}
}
