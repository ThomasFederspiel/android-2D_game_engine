package se.federspiel.android.glgraphics;

import android.opengl.GLES20;

public class ColorFragmentShader extends AbstractFragmentShader
{
	private static final String ColorFragmentShaderCode =
	    "precision mediump float;" +
	    "uniform vec4 uColor;" +
	    "void main()" +
	    "{" +
	    "  gl_FragColor = uColor;" +
	    "}";

    public ColorFragmentShader()
	{
    	super(ColorFragmentShaderCode);
	}

    public ColorFragmentShader(String shaderCode)
	{
    	super(shaderCode);
	}

    public void setColor(float[] color)
    {
	    // Get handle to fragment shader's uColor member
	    int colorHandle = GLES20.glGetUniformLocation(mProgram.getHandle(), "uColor");
	    GLES20Utils.checkGlError("glGetUniformLocation");
	
	    // Set color for drawing the triangle
	    GLES20.glUniform4fv(colorHandle, 1, color, 0);
    }
}
