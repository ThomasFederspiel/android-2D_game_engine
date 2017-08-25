package se.federspiel.android.glgraphics;

import java.util.Vector;

import android.opengl.Matrix;
import static junit.framework.Assert.assertTrue;


public class GLES20World
{
    private final float[] mMVPMatrix = new float[16];
    private final float[] mProjMatrix = new float[16];
    private final float[] mVMatrix = new float[16];
    
	private int mWidth = 0;
	private int mHeight = 0;

	private Vector<IGLWorldListener> mListeners = new Vector<IGLWorldListener>();
	
    public GLES20World()
	{
	}

    public void addGLWorldListener(IGLWorldListener listener)
    {
    	assertTrue(listener != null);
    	assertTrue(!mListeners.contains(listener));
    	
    	mListeners.add(listener);
    }
    
    public void removeGLWorldListener(IGLWorldListener listener)
    {
    	assertTrue(listener != null);
    	assertTrue(mListeners.contains(listener));
    	
    	mListeners.remove(listener);
    }

    public float[] getCameraAndProjectionMatrix()
	{
		return mMVPMatrix;
	}
	
	public void viewPortCanvasUpdated(int width, int height)
	{
		mWidth = width;
		mHeight = height;
		
        float ratio = (float) width / height;

        float halfHeight = height / 2.0f;
        
		// matrix, ?, left, right, bottom, top, near, far
//        Matrix.frustumM(mProjMatrix, 0, -ratio * halfHeight, ratio * halfHeight, -halfHeight, halfHeight, 1, 10);
        Matrix.orthoM(mProjMatrix, 0, -ratio * halfHeight, ratio * halfHeight, -halfHeight, halfHeight, 1, 10);
        
        setCanvasCameraView(width, height);
        
		// Calculate the projection and view transformation
		calculateMVP();
	}

	public void setCanvasCameraView(int width, int height)
	{
		// Set the camera position (View matrix)
		// matrix, ?, eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ
//		Matrix.setLookAtM(mVMatrix, 0, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, 10.0f, 0.0f, -1.0f, 0.0f);
		Matrix.setLookAtM(mVMatrix, 0, width / 2.0f, height / 2.0f, -1.0f, width / 2.0f, height / 2.0f, 10.0f, 0.0f, -1.0f, 0.0f);
	}

	private void calculateMVP()
    {
		// Calculate the projection and view transformation
		Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mVMatrix, 0);
		
		notifyCameraAndProjectionMatrix(mMVPMatrix);
    }
	
	private void notifyCameraAndProjectionMatrix(float[] mvpMatrix)
	{
		for (int i = 0; i < mListeners.size(); i++)
		{
			mListeners.get(i).onCameraAndProjectionMatrixChange(mvpMatrix);
		}
	}

	public interface IGLWorldListener
	{
		public void onCameraAndProjectionMatrixChange(float[] mvpMatrix);
	}

}
