package se.federspiel.android.glgraphics.mesh;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import se.federspiel.android.game.geometry.Point;
import se.federspiel.android.glgraphics.BaseVertextShader;
import se.federspiel.android.glgraphics.GLES20AbstractProgram;
import se.federspiel.android.glgraphics.GLES20Utils;
import se.federspiel.android.glgraphics.GLES20World;
import android.opengl.GLES20;
import android.opengl.Matrix;

public class GLMesh implements GLES20World.IGLWorldListener
{
    private final float[] mTranslMatrix = new float[16];
    private final float[] mTransfMatrix = new float[16];

    private FloatBuffer mVertexBuffer = null;
    private ShortBuffer mDrawListBuffer = null;

    private int mDrawMode = GLES20.GL_TRIANGLES;
    
    private int mDrawOrderLength = 0;
    private int mVertexCount = 0;

    boolean mTranslationMatrixUpdated = true;
    boolean mCameraAndProjectionMatrixUpdated = false;
    	
	// number of coordinates per vertex in this array
    protected static final int COORDS_PER_VERTEX = 3;
    protected static final int TEXS_PER_VERTEX = 2;

    // 4 bytes per vertex
    protected static final int VERTEX_STRIDE = COORDS_PER_VERTEX * 4; 

	private GLES20AbstractProgram mProgram = null;
    private BaseVertextShader mVertexShader = null;
    
	protected Point mPosition = Point.Zero.clone();
	
    public GLMesh(GLES20AbstractProgram program, BaseVertextShader vertexShader)
    {
    	mProgram = program;
    	mVertexShader = vertexShader;
    	
    	Matrix.setIdentityM(mTranslMatrix, 0);
    }
    
	public void setPosition(Point position)
	{
		setPosition(position.X, position.Y);
	}

	public void setPosition(float x, float y)
	{
		mPosition.set(x, y);
		
		Matrix.translateM(mTranslMatrix, 0, mPosition.X, mPosition.Y, 0);
		
		mTranslationMatrixUpdated = true;
	}

    public void load()
    {
    	mProgram.addGLWorldListener(this);
    }
    
    public void unload()
    {
    	mProgram.removeGLWorldListener(this);
    }
    
	public void onCameraAndProjectionMatrixChange(float[] mvpMatrix)
	{
		mCameraAndProjectionMatrixUpdated = true;
	}
	
    public void draw(GLES20AbstractProgram program) 
    {
     	// get handle to vertex shader's vPosition member
        int positionHandle = mVertexShader.getPositionHandle();
        	
        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(positionHandle);

        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(positionHandle, COORDS_PER_VERTEX,
                                     GLES20.GL_FLOAT, false,
                                     VERTEX_STRIDE, mVertexBuffer);
        
        applyTransformationMatrix(program);

        if (mDrawListBuffer != null)
        {
            GLES20.glDrawElements(mDrawMode, mDrawOrderLength,
                    GLES20.GL_UNSIGNED_SHORT, mDrawListBuffer);
        }
        else
        {
            GLES20.glDrawArrays(mDrawMode, 0, mVertexCount);
        }

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(positionHandle);
    }
    
    protected void setMesh(float[] meshCoords, short[] drawOrder) 
    {
    	setMesh(meshCoords, drawOrder, GLES20.GL_TRIANGLES);
    }
    
    protected void setMesh(float[] meshCoords) 
    {
    	setMesh(meshCoords, GLES20.GL_TRIANGLES);
    }
    
    protected void setMesh(float[] meshCoords, short[] drawOrder, int mode) 
    {
    	mDrawMode = mode;
    	setVertex(meshCoords);
    	setDrawOrder(drawOrder);
    }

    protected void setMesh(float[] meshCoords, int mode) 
    {
    	mDrawMode = mode;
    	setVertex(meshCoords);
    	
    	mDrawListBuffer = null;
    	mDrawOrderLength = 0;
    }

    protected void updateMesh(float[] meshCoords) 
    {
    	setVertex(meshCoords);
    }

    private void setVertex(float[] meshCoords) 
    {
    	if ((mVertexBuffer == null)
    		|| ((meshCoords.length / COORDS_PER_VERTEX) != mVertexCount))
    	{
	        // initialize vertex byte buffer for shape coordinates
	        ByteBuffer bb = ByteBuffer.allocateDirect(
	                // (number of coordinate values * 4 bytes per float)
	        		meshCoords.length * 4);
	        bb.order(ByteOrder.nativeOrder());
	        mVertexBuffer = bb.asFloatBuffer();
	        mVertexBuffer.put(meshCoords);
	        mVertexBuffer.position(0);
	        
	        mVertexCount = meshCoords.length / COORDS_PER_VERTEX;
    	}
    	else
    	{
    		mVertexBuffer.clear();
	        mVertexBuffer.put(meshCoords);
	        mVertexBuffer.position(0);
    	}
    }

    private void setDrawOrder(short[] drawOrder) 
    {
    	if ((mDrawListBuffer == null)
        	|| (drawOrder.length != mDrawOrderLength))
    	{
	        // initialize byte buffer for the draw list
	        ByteBuffer dlb = ByteBuffer.allocateDirect(
	        // (# of coordinate values * 2 bytes per short)
	                drawOrder.length * 2);
	        dlb.order(ByteOrder.nativeOrder());
	        mDrawListBuffer = dlb.asShortBuffer();
	        mDrawListBuffer.put(drawOrder);
	        mDrawListBuffer.position(0);
	        
	        mDrawOrderLength = drawOrder.length;
    	}
    	else
    	{
    		mDrawListBuffer.clear();
    		mDrawListBuffer.put(drawOrder);
    		mDrawListBuffer.position(0);
    	}
    }
    
    private void applyTransformationMatrix(GLES20AbstractProgram program)
    {
		if (mTranslationMatrixUpdated || mCameraAndProjectionMatrixUpdated)
		{
	        Matrix.multiplyMM(mTransfMatrix, 0, program.getCameraAndProjectionMatrix(), 0, mTranslMatrix, 0);
	        
	        mTranslationMatrixUpdated = false;
	        mCameraAndProjectionMatrixUpdated = false;
		}
		
        // get handle to shape's transformation matrix
        int mvpMatrixHandle = mVertexShader.getMVPMatrixHandle();

        // Apply the projection and view transformation
        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mTransfMatrix, 0);
        GLES20Utils.checkGlError("glUniformMatrix4fv");
    }
}