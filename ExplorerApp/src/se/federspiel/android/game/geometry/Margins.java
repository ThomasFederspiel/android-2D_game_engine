package se.federspiel.android.game.geometry;

public class Margins
{
	public static final Margins Zero = new Margins(0, 0, 0, 0);
	
	private int mLeft = 0;
	private int mTop = 0;
	private int mRight = 0;
	private int mBottom = 0;
	
	public Margins(Margins margins)
	{
		this(margins.mLeft, margins.mTop, margins.mRight, margins.mBottom);
	}
	
	public Margins(int left, int top, int right, int bottom)
	{
		mTop = top;
		mRight = right;
		mLeft = left;
		mBottom = bottom;
	}

	public int getTop()
	{
		return mTop;
	}
	
	public int getLeft()
	{
		return mLeft;
	}
	
	public int getRight()
	{
		return mRight;
	}
	
	public int getBottom()
	{
		return mBottom;
	}

    public void setMargins(int left, int top, int right, int bottom)
    {
		mTop = top;
		mRight = right;
		mLeft = left;
		mBottom = bottom;
    }
    
	public void set(Margins margins)
	{
		mTop = margins.mTop;
		mRight = margins.mRight;
		mLeft = margins.mLeft;
		mBottom = margins.mBottom;
	}

	public Margins clone()
	{
		return new Margins(this);
	}
	
	public String toString()
	{
		return "(top = " + mTop + ", left = " + mLeft + ",  bottom = " + mBottom + ", right = " + mRight + ")";
	}
}
