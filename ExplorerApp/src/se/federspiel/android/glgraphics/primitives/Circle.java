package se.federspiel.android.glgraphics.primitives;

import se.federspiel.android.game.interfaces.gprimitives.ICirclePrimitive;
import se.federspiel.android.glgraphics.BaseVertextShader;
import se.federspiel.android.glgraphics.ColorFragmentShader;
import se.federspiel.android.glgraphics.GLES20Program;
import se.federspiel.android.glgraphics.mesh.CircleMesh;
import se.federspiel.android.glgraphics.texture.ColorTexture;
import android.graphics.Color;

public class Circle extends AbstractPrimitive implements ICirclePrimitive 
{
	private CircleMesh mMesh = null;
	private ColorTexture mTexture = null;

	public Circle(GLES20Program<BaseVertextShader, ColorFragmentShader> program) 
    {
		super(program);

		mMesh = new CircleMesh(program, program.getVertexShader());
		setMesh(mMesh);
		
		mTexture = new ColorTexture(program.getFragmentShader());
		setTexture(mTexture);
		
		mTexture.setColor(Color.BLUE);
    }

	public void setRadius(float radius)
	{
		mMesh.setRadius(radius);
	}
}