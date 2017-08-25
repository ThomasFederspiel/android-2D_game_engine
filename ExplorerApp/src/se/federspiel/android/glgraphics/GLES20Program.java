package se.federspiel.android.glgraphics;

import static junit.framework.Assert.assertTrue;

import se.federspiel.android.util.ALog;
import android.opengl.GLES20;

public class GLES20Program<V extends AbstractVertexShader, F extends AbstractFragmentShader> extends GLES20AbstractProgram
{
	private V mVertexShader = null;
	private F mFragmentShader = null;
	
	public GLES20Program(OpenGLES20Renderer renderer, GLES20World world, V vertexShader, F fragmentShader)
	{
		super(renderer, world);
		
		mVertexShader = vertexShader;
		mFragmentShader = fragmentShader;
	}
	
	public V getVertexShader()
	{
		return mVertexShader;
	}
	
	public F getFragmentShader()
	{
		return mFragmentShader;
	}

	public void linkProgram() 
	{
	    int vertexShaderHandle = mVertexShader.loadShader();
	    int fragmentShaderHandle = mFragmentShader.loadShader();

	    int programHandle = GLES20.glCreateProgram();             // create empty OpenGL ES Program
	    assertTrue(programHandle != 0);
		GLES20Utils.checkGlError("glCreateProgram");
		
	    GLES20.glAttachShader(programHandle, vertexShaderHandle);   // add the vertex shader to program
		GLES20Utils.checkGlError("glCreateProgram");
	    GLES20.glAttachShader(programHandle, fragmentShaderHandle); // add the fragment shader to program
		GLES20Utils.checkGlError("glCreateProgram");
	    GLES20.glLinkProgram(programHandle);                  // creates OpenGL ES program executables
		GLES20Utils.checkGlError("glCreateProgram");

	    final int[] linkStatus = new int[1];
	    GLES20.glGetProgramiv(programHandle, GLES20.GL_LINK_STATUS, linkStatus, 0);
	 
	    // If the link failed, delete the program.
	    if (linkStatus[0] == 0)
	    {
		    ALog.error(this, GLES20.glGetProgramInfoLog(programHandle));
		    
	        GLES20.glDeleteProgram(programHandle);
	        programHandle = 0;
	    }
	    
	    assertTrue(linkStatus[0] != 0);
	    
	    setHandle(programHandle);
	    
		mVertexShader.setProgram(this);
		mFragmentShader.setProgram(this);
	}
}
