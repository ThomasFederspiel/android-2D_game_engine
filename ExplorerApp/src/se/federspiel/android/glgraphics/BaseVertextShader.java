package se.federspiel.android.glgraphics;

import android.opengl.GLES20;

public class BaseVertextShader extends AbstractVertexShader
{
	private static final String PositionMVPMatrixVertexShaderCode =
        "uniform mat4 uMVPMatrix;" +
        "attribute vec4 aPosition;" +
        "void main()" +
	    "{" +
        "  gl_Position = uMVPMatrix * aPosition;" +
        "}";

    public BaseVertextShader()
	{
    	super(PositionMVPMatrixVertexShaderCode);
	}

    public BaseVertextShader(String shaderCode)
	{
    	super(shaderCode);
	}

    public int getPositionHandle()
    {
	 	// get handle to vertex shader's vPosition member
	    int positionHandle = GLES20.glGetAttribLocation(mProgram.getHandle(), "aPosition");
	    GLES20Utils.checkGlError("glGetAttribLocation");
	    
	    return positionHandle;
    }
    
    public int getMVPMatrixHandle()
    {
	    // get handle to shape's transformation matrix
	    int mvpMatrixHandle = GLES20.glGetUniformLocation(mProgram.getHandle(), "uMVPMatrix");
	    GLES20Utils.checkGlError("glGetUniformLocation");
	    
	    return mvpMatrixHandle;
    }
}
