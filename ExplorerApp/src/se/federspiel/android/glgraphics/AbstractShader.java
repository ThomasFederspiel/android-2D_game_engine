package se.federspiel.android.glgraphics;

import static junit.framework.Assert.assertTrue;

import se.federspiel.android.util.ALog;
import android.opengl.GLES20;

public abstract class AbstractShader
{
	protected enum ShaderType
	{
		VertextShader(GLES20.GL_VERTEX_SHADER),		
		FragmentShader(GLES20.GL_FRAGMENT_SHADER);

		private int mType = -1;
		
		private ShaderType(int type)
		{
			mType = type;
		}
		
		int getType()
		{
			return mType;
		}
	};
	
	private ShaderType mShaderType = null;
	
	private String mShaderCode = null;
	
	private int mShaderHandle = -1;

	GLES20AbstractProgram mProgram = null;
	
    public AbstractShader(ShaderType type, String shaderCode)
	{
		assertTrue(shaderCode != null);
		
    	mShaderType = type;
		mShaderCode = shaderCode;
	}

	public int getShaderHandle()
	{
		return loadShader();
	}

	public int loadShader()
	{
		if (mShaderHandle == -1)
		{
			assertTrue(mShaderCode != null);
			
			mShaderHandle = loadShader(mShaderType.getType(), mShaderCode);
		}
		
		return mShaderHandle;
	}
	
	protected int loadShader(int type, String shaderCode)
	{
	    // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
	    // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
	    int shaderHandle = GLES20.glCreateShader(type);

		assertTrue(shaderHandle != 0);
	    
	    // add the source code to the shader and compile it
	    GLES20.glShaderSource(shaderHandle, shaderCode);
	    GLES20.glCompileShader(shaderHandle);

	    final int[] compileStatus = new int[1];
	    GLES20.glGetShaderiv(shaderHandle, GLES20.GL_COMPILE_STATUS, compileStatus, 0);
	 
	    // If the compilation failed, delete the shader.
	    if (compileStatus[0] == 0)
	    {
		    ALog.error(this, GLES20.glGetShaderInfoLog(shaderHandle));
		    
	        GLES20.glDeleteShader(shaderHandle);
	        shaderHandle = 0;
	    }
	    
		assertTrue(compileStatus[0] != 0);
		
	    return shaderHandle;
	}
	
	void setProgram(GLES20AbstractProgram program)
	{
		mProgram = program;		
	}
}
