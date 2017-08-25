package se.federspiel.android.glgraphics;

import static junit.framework.Assert.assertTrue;

import java.util.Vector;

import se.federspiel.android.glgraphics.GLES20World.IGLWorldListener;
import se.federspiel.android.glgraphics.primitives.AbstractPrimitive;
import android.opengl.GLES20;

public abstract class GLES20AbstractProgram
{
	protected int mProgramHandle = -1;

	private OpenGLES20Renderer mGLRender = null;
	private GLES20World mGLWorld = null;
	
	private Vector<AbstractPrimitive> mPrimities = new Vector<AbstractPrimitive>();
	
	public GLES20AbstractProgram(OpenGLES20Renderer renderer, GLES20World world)
	{
		mGLRender = renderer;
		mGLWorld = world;
	}

    public void addGLWorldListener(IGLWorldListener listener)
    {
    	mGLWorld.addGLWorldListener(listener);
    }
    
    public void removeGLWorldListener(IGLWorldListener listener)
    {
    	mGLWorld.removeGLWorldListener(listener);
    }

    public void addPrimitive(AbstractPrimitive primitive)
	{
		assertTrue(!mPrimities.contains(primitive));

		if (mPrimities.size() == 0)
		{
			mGLRender.addGLProgram(this);
		}
		
		mPrimities.add(primitive);
	}
	
	public void removePrimitive(AbstractPrimitive primitive)
	{
		assertTrue(mPrimities.contains(primitive));
		
		mPrimities.remove(primitive);
		
		if (mPrimities.size() == 0)
		{
			mGLRender.removeGLProgram(this);
		}
	}

	public void draw()
	{
		useProgram();
		
        for (int i = 0; i < mPrimities.size(); i++)
        {
        	mPrimities.get(i).draw(this);
        }
	}
	
	public int getHandle()
	{
		return mProgramHandle;
	}
	
	public float[] getCameraAndProjectionMatrix()
	{
		return mGLWorld.getCameraAndProjectionMatrix();
	}
	
	public void setHandle(int handle)
	{
		mProgramHandle = handle;
	}
	
	private void useProgram() 
	{
		GLES20.glUseProgram(mProgramHandle);
		GLES20Utils.checkGlError("glUseProgram");
	}
}
