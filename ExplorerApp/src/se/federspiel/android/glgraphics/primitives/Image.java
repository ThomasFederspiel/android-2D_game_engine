package se.federspiel.android.glgraphics.primitives;

import se.federspiel.android.game.geometry.Dimensions;
import se.federspiel.android.game.interfaces.gprimitives.IImagePrimitive;
import se.federspiel.android.glgraphics.GLES20Program;
import se.federspiel.android.glgraphics.TextureFragmentShader;
import se.federspiel.android.glgraphics.TextureVertextShader;
import se.federspiel.android.glgraphics.mesh.SquareMesh;
import se.federspiel.android.glgraphics.texture.ImageTexture;
import android.graphics.Bitmap;

public class Image extends AbstractPrimitive implements IImagePrimitive 
{
	private SquareMesh mMesh = null;
	private ImageTexture mTexture = null;

	private GLES20Program<TextureVertextShader, TextureFragmentShader> mProgram = null;
	
    private float textureCoords[] = 
    { 
    		0.0f, 0.0f,   // top left
            0.0f, 1.0f,   // bottom left
            1.0f, 1.0f,   // bottom right
            1.0f, 0.0f    // top right
    };
    
    public Image(GLES20Program<TextureVertextShader, TextureFragmentShader> program) 
    {
    	super(program);
    	
		mMesh = new SquareMesh(program, program.getVertexShader());
		setMesh(mMesh);
		
		mTexture = new ImageTexture(program.getFragmentShader());
		setTexture(mTexture);

		mTexture.setTextureCoordinates(textureCoords);
    }
    
    public void setDimensions(int width, int height)
	{
		mMesh.setDimensions(width, height);
	}
    
    public void setDimensions(Dimensions dims)
	{
		mMesh.setDimensions(dims);
	}
    
    public void setBitmap(Bitmap bitmap, boolean setDimension)
    {
    	mTexture.loadGLTexture(bitmap);
    	
    	if (setDimension)
    	{
    		setDimensions(bitmap.getWidth(), bitmap.getHeight());
    	}
    }
}