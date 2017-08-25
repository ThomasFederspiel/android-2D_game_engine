package se.federspiel.android.glgraphics.mesh;

import android.opengl.GLES20;
import se.federspiel.android.glgraphics.BaseVertextShader;
import se.federspiel.android.glgraphics.GLES20AbstractProgram;

public class LineMesh extends GLMesh
{
    private float mLineCoords[] = 
    { 
		0.0f, 0.0f, 0.0f,   // start
        0.0f, 0.0f, 0.0f    // end
    };

    private float mLineDx = 0;
    private float mLineDy = 0;

    public LineMesh(GLES20AbstractProgram program, BaseVertextShader vertexShader) 
    {
    	super(program, vertexShader);
    	
    	setMesh(mLineCoords, GLES20.GL_LINES);
    	
    	setLine(0, 0, 100, 100);
    }
    
    public void setLine(float x1, float y1, float x2, float y2)
    {
    	setPosition(x1, y1);

    	float dx = x2 - x1;
    	float dy = y2 - y1;
    	
    	if ((mLineDx != dx) || (mLineDy != dy))
    	{
    		updateLineMesh(dx, dy);
    		
    		mLineDx = dx;
    		mLineDy = dy;
    	}
    }
    
    private void updateLineMesh(float dx, float dy)
    {
    	mLineCoords[3] = dx;
    	mLineCoords[4] = dy;
    	
    	updateMesh(mLineCoords);
    }
}