package se.federspiel.android.glgraphics.mesh;

import se.federspiel.android.game.geometry.Dimensions;
import se.federspiel.android.glgraphics.BaseVertextShader;
import se.federspiel.android.glgraphics.GLES20AbstractProgram;

public class SquareMesh extends GLMesh
{
    private float mSquareCoords[] = 
    { 
		0.0f, 0.0f, 0.0f,   // top left
        0.0f, 0.0f, 0.0f,   // bottom left
        0.0f, 0.0f, 0.0f,   // bottom right
        0.0f, 0.0f, 0.0f    // top right
    };

    private short mDrawOrder[] = { 0, 1, 2, 0, 2, 3 }; // order to draw vertices

    private Dimensions mDims = Dimensions.Zero.clone();
	
    public SquareMesh(GLES20AbstractProgram program, BaseVertextShader vertexShader) 
    {
    	super(program, vertexShader);
    	
    	setMesh(mSquareCoords, mDrawOrder);
    	
    	setDimensions(10, 10);
    }
    
    public void setDimensions(int width, int height)
	{
		if (!mDims.isSame(width, height))
		{
			updateSquareMesh(width - mDims.getWidth(), height - mDims.getHeight());
			
			mDims.set(width, height);
		}
	}
    
    public void setDimensions(Dimensions dims)
	{
		if (!mDims.isSame(dims))
		{
			updateSquareMesh(dims.getWidth() - mDims.getWidth(), dims.getHeight() - mDims.getHeight());
			
			mDims.set(dims);
		}
	}
    
    private void updateSquareMesh(float widthDelta, float heightDelta)
    {
    	// bottom left
    	mSquareCoords[4] += heightDelta;
    	
    	// bottom right
    	mSquareCoords[6] += widthDelta;
    	mSquareCoords[7] += heightDelta;
    	
    	// top left
    	mSquareCoords[9] += widthDelta;
    	
    	updateMesh(mSquareCoords);
    }
}