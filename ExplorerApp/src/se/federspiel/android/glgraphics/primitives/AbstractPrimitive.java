package se.federspiel.android.glgraphics.primitives;

import se.federspiel.android.game.geometry.Point;
import se.federspiel.android.game.interfaces.gprimitives.IBasePrimitive;
import se.federspiel.android.glgraphics.GLES20AbstractProgram;
import se.federspiel.android.glgraphics.texture.GLTexture;
import se.federspiel.android.glgraphics.mesh.GLMesh;

public abstract class AbstractPrimitive implements IBasePrimitive
{
	protected GLES20AbstractProgram mProgram = null;
	
	private GLMesh mMesh = null;
	private GLTexture mTexture = null;
	
	public AbstractPrimitive(GLES20AbstractProgram program) 
    {
		mProgram = program;
    }
	
	public void setPosition(float x, float y)
	{
		mMesh.setPosition(x, y);
	}
	
	public void setPosition(Point position)
	{
		mMesh.setPosition(position);
	}

    public void load()
    {
    	mMesh.load();
    	mProgram.addPrimitive(this);
    }
    
    public void unload()
    {
    	mMesh.unload();
    	mProgram.removePrimitive(this);
    }
    
    public void draw(GLES20AbstractProgram program)
    {
    	mTexture.draw(program);
    	mMesh.draw(program);
    }
    
	protected void setMesh(GLMesh mesh)
	{
		mMesh = mesh;
	}
	
	protected void setTexture(GLTexture texture)
	{
		mTexture = texture;
	}
}
