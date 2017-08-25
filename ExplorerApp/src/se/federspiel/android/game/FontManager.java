package se.federspiel.android.game;

import android.content.Context;
import android.graphics.Typeface;

public class FontManager
{
	private static final int DEFAULT_FONT_SIZE = 20;
	
    private float mDipDensity = 1;
    private float mDipDensityFont = 1;

    private Typeface mCurrentTypeFace = Typeface.DEFAULT;
    
	private Context mContext = null;
	
	private int mFontSizeInPx = DEFAULT_FONT_SIZE;
	
	public FontManager(Context context)
	{
		mContext = context;
        
    	mDipDensity = mContext.getResources().getDisplayMetrics().density;
    	mDipDensityFont = mContext.getResources().getDisplayMetrics().scaledDensity;
    	
    	mFontSizeInPx = dpiFontSizeToPixelSize(DEFAULT_FONT_SIZE);
	}
	
	public void loadFont(String fontName)
	{
		String path = "fonts/" + fontName;
		
		mCurrentTypeFace = Typeface.createFromAsset(mContext.getAssets(), path);
	}
	
    public int dpiSizeToPixelSize(int px)
    {
    	return (int) ((px * mDipDensity) + 0.5f);
    }
    
    public int dpiFontSizeToPixelSize(int px)
    {
    	return (int) ((px * mDipDensityFont) + 0.5f);
    }
    
	public int getDefaultFontSize()
	{
		return mFontSizeInPx;
	}

	public Typeface getDefaultFontTypeFace()
	{
		return mCurrentTypeFace;
	}
}
