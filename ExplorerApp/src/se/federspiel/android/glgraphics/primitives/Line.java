package se.federspiel.android.glgraphics.primitives;

import se.federspiel.android.game.interfaces.gprimitives.ILinePrimitive;
import se.federspiel.android.glgraphics.BaseVertextShader;
import se.federspiel.android.glgraphics.ColorFragmentShader;
import se.federspiel.android.glgraphics.GLES20Program;
import se.federspiel.android.glgraphics.mesh.LineMesh;
import se.federspiel.android.glgraphics.texture.ColorTexture;
import android.graphics.Color;

public class Line extends AbstractPrimitive implements ILinePrimitive 
{
	private LineMesh mMesh = null;
	private ColorTexture mTexture = null;

	public Line(GLES20Program<BaseVertextShader, ColorFragmentShader> program) 
    {
		super(program);

		mMesh = new LineMesh(program, program.getVertexShader());
		setMesh(mMesh);
		
		mTexture = new ColorTexture(program.getFragmentShader());
		setTexture(mTexture);
		
		mTexture.setColor(Color.BLUE);
    }

    public void setLine(float x1, float y1, float x2, float y2)
	{
		mMesh.setLine(x1, y1, x2, y2);
	}
}