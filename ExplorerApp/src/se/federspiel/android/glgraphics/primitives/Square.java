package se.federspiel.android.glgraphics.primitives;

import se.federspiel.android.game.geometry.Dimensions;
import se.federspiel.android.game.interfaces.gprimitives.ISquarePrimitive;
import se.federspiel.android.glgraphics.BaseVertextShader;
import se.federspiel.android.glgraphics.ColorFragmentShader;
import se.federspiel.android.glgraphics.GLES20Program;
import se.federspiel.android.glgraphics.mesh.SquareMesh;
import se.federspiel.android.glgraphics.texture.ColorTexture;
import android.graphics.Color;

public class Square extends AbstractPrimitive implements ISquarePrimitive
{
	private SquareMesh mMesh = null;
	private ColorTexture mTexture = null;

	public Square(GLES20Program<BaseVertextShader, ColorFragmentShader> program) 
    {
		super(program);

		mMesh = new SquareMesh(program, program.getVertexShader());
		setMesh(mMesh);
		
		mTexture = new ColorTexture(program.getFragmentShader());
		setTexture(mTexture);
		
		mTexture.setColor(Color.BLUE);
    }

    public void setDimensions(int width, int height)
	{
		mMesh.setDimensions(width, height);
	}
    
    public void setDimensions(Dimensions dims)
	{
		mMesh.setDimensions(dims);
	}
}