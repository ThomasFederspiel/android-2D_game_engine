package se.federspiel.android.glgraphics.texture;

import se.federspiel.android.glgraphics.ColorFragmentShader;
import se.federspiel.android.glgraphics.GLES20AbstractProgram;
import se.federspiel.android.util.ASystem;

public class ColorTexture extends GLTexture
{
	private ColorFragmentShader mFragmentShader = null;
	
    // Set color with red, green, blue and alpha (opacity) values
    private float mColor[] = { 0.63671875f, 0.76953125f, 0.22265625f, 1.0f };

    public ColorTexture(ColorFragmentShader fragmentShader)
    {
    	super();
    	
    	mFragmentShader = fragmentShader;
    }
    
    public void setColor(int aColor)
    {
    	ASystem.androidColorToGLESColor(aColor, mColor);
    }
    
    public void draw(GLES20AbstractProgram program) 
    {
    	mFragmentShader.setColor(mColor);
    }
 }