package se.federspiel.android.game.ui;

public class UILayoutProperties
{
	public static final int LEFT = 0x0001;
	public static final int RIGHT = 0x0002;
	public static final int TOP = 0x0004;
	public static final int BOTTOM = 0x0008;
	public static final int CENTER_X = 0x0010;
	public static final int CENTER_Y = 0x0020;
	
	private static final int CLEAR = 0x0000;
	
	private int mProperty = CLEAR;

	private boolean mIsDefault = false;
	
	public UILayoutProperties(int defaultProp)
	{
		mProperty = defaultProp;
		
		mIsDefault = true;
	}
	
	public void set(int property)
	{
		if (mIsDefault)
		{
			mProperty = CLEAR;
			
			mIsDefault = false;
		}
		
		mProperty |= property;
	}
	
	public void clear(int property)
	{
		mProperty &= ~property;
	}
	
	public void clearAll()
	{
		mProperty = CLEAR;
	}
	
	public boolean isLeft()
	{
		return ((mProperty & LEFT) != 0);
	}
	
	public boolean isRight()
	{
		return ((mProperty & RIGHT) != 0);
	}
	
	public boolean isTop()
	{
		return ((mProperty & TOP) != 0);
	}
	
	public boolean isBottom()
	{
		return ((mProperty & BOTTOM) != 0);
	}
	
	public boolean isCenterX()
	{
		return ((mProperty & CENTER_X) != 0);
	}
	
	public boolean isCenterY()
	{
		return ((mProperty & CENTER_Y) != 0);
	}
}
