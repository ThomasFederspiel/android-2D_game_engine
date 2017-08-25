package se.federspiel.android.glgraphics.mesh;

import se.federspiel.android.glgraphics.BaseVertextShader;
import se.federspiel.android.glgraphics.GLES20AbstractProgram;
import se.federspiel.android.glgraphics.mesh.GLMesh;
import android.opengl.GLES20;

public class CircleMesh extends GLMesh
{
    private float circleCoords[] = null;

    private float mRadius = 10;
    private int mSegments = 40;
    
    public CircleMesh(GLES20AbstractProgram program, BaseVertextShader vertexShader) 
    {
    	super(program, vertexShader);
    	
    	circleCoords = generateCircle(0, 0, mRadius, mSegments);
    	setMesh(circleCoords, GLES20.GL_TRIANGLE_FAN);
    }
    
    public void setRadius(float radius)
	{
    	if (mRadius != radius)
    	{
    		mRadius = radius;
    		
        	circleCoords = generateCircle(0, 0, mRadius, mSegments);
        	updateMesh(circleCoords);
    	}
	}
    
    private float[] generateCircle(float cx, float cy, float radius, int nofSegments) 
    { 
    	float[] coordinates = new float[(nofSegments + 2) * COORDS_PER_VERTEX]; 
    	
    	float theta = (float) (2 * Math.PI / nofSegments); 
    	float c = (float) Math.cos(theta);
    	float s = (float) Math.sin(theta);
    	float t;

    	float x = radius;
    	float y = 0; 
        
    	int offset = 0;
    	
    	coordinates[offset] = cx;
    	coordinates[offset + 1] = cy;
    	coordinates[offset + 2] = 0;
    	
    	offset += COORDS_PER_VERTEX;
    	
    	for (int i = 0; i < nofSegments; i++) 
    	{ 
        	coordinates[offset] = cx + x;
        	coordinates[offset + 1] = cy + y;
        	coordinates[offset + 2] = 0;
        	
        	offset += COORDS_PER_VERTEX;
        	
    		t = x;
    		x = c * x - s * y;
    		y = s * t + c * y;
    	} 
    	
    	coordinates[offset] = coordinates[3];
    	coordinates[offset + 1] = coordinates[4];
    	coordinates[offset + 2] = coordinates[5];
    	
    	return coordinates;
    }    
}