package se.federspiel.android.glgraphics.primitives;

import android.graphics.Color;
import se.federspiel.android.game.interfaces.gprimitives.ITrianglePrimitive;
import se.federspiel.android.glgraphics.BaseVertextShader;
import se.federspiel.android.glgraphics.ColorFragmentShader;
import se.federspiel.android.glgraphics.GLES20Program;
import se.federspiel.android.glgraphics.mesh.TriangleMesh;
import se.federspiel.android.glgraphics.texture.ColorTexture;

public class Triangle extends AbstractPrimitive implements ITrianglePrimitive
{
	private TriangleMesh mMesh = null;
	private ColorTexture mTexture = null;

	public Triangle(GLES20Program<BaseVertextShader, ColorFragmentShader> program) 
    {
		super(program);

		mMesh = new TriangleMesh(program, program.getVertexShader());
		setMesh(mMesh);
		
		mTexture = new ColorTexture(program.getFragmentShader());
		setTexture(mTexture);
		
		mTexture.setColor(Color.BLUE);
    }
 }