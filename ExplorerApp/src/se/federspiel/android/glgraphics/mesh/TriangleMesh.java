package se.federspiel.android.glgraphics.mesh;

import se.federspiel.android.glgraphics.BaseVertextShader;
import se.federspiel.android.glgraphics.GLES20AbstractProgram;
import se.federspiel.android.glgraphics.mesh.GLMesh;

public class TriangleMesh extends GLMesh
{
    private float triangleCoords[] = { // in counterclockwise order:
           160.0f, 203.0f, 0.0f,   // top
           210.0f, 110.0f, 0.0f,   // bottom left
           110.0f, 110.0f, 0.0f    // bottom right
       };

    private short drawOrder[] = { 0, 1, 2 }; // order to draw vertices

    public TriangleMesh(GLES20AbstractProgram program, BaseVertextShader vertexShader) 
    {
    	super(program, vertexShader);
    	
    	setMesh(triangleCoords, drawOrder);
    } 
 }