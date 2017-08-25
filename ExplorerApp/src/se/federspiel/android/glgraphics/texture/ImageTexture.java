package se.federspiel.android.glgraphics.texture;

import static junit.framework.Assert.assertTrue;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import se.federspiel.android.glgraphics.GLES20AbstractProgram;
import se.federspiel.android.glgraphics.GLES20Utils;
import se.federspiel.android.glgraphics.TextureFragmentShader;

public class ImageTexture extends GLTexture
{
	private TextureFragmentShader mFragmentShader = null;
	
    private FloatBuffer mTextureBuffer = null;
    
    private int mTextureHandle = -1;
    
    private static final int TEXS_PER_VERTEX = 2;
    
    public ImageTexture(TextureFragmentShader fragmentShader)
    {
    	super();
    	
    	mFragmentShader = fragmentShader;
    }
    
    public void setTextureCoordinates(float[] textureCoords) 
    { 
        // function.
		// float is 4 bytes, therefore we multiply the number if
		// vertices with 4.
		ByteBuffer byteBuf = ByteBuffer
		.allocateDirect(textureCoords.length * 4);
		byteBuf.order(ByteOrder.nativeOrder());
		
		mTextureBuffer = byteBuf.asFloatBuffer();
		mTextureBuffer.put(textureCoords);
		mTextureBuffer.position(0);
    } 
    
    public void loadGLTexture(Bitmap bitmap)
    {
		// Generate one texture pointer...
		int[] textures = new int[1];
		GLES20.glGenTextures(1, textures, 0);
		mTextureHandle = textures[0];
 
		assertTrue(mTextureHandle != 0);
			
		// ...and bind it to our array
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureHandle);

		// Create Nearest Filtered Texture
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,
				GLES20.GL_LINEAR);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER,
				GLES20.GL_LINEAR);
		
		// Different possible texture parameters, e.g. GL10.GL_CLAMP_TO_EDGE
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,
				GLES20.GL_REPEAT);
		GLES20.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T,
				GLES20.GL_REPEAT);
		
		// Use the Android GLUtils to specify a two-dimensional texture image
		// from our bitmap
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
      }
    
    public void draw(GLES20AbstractProgram program) 
    {
    	GLES20.glEnable(GLES20.GL_BLEND);
    	GLES20Utils.checkGlError("glEnable");
    	
//        	GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ZERO);
    	GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

//    	GLES20.glEnable(GLES20.GL_TEXTURE_2D);
//      program.checkGlError("glEnable");

		// Enable the texture state
//     	GLES20.glEnableClientState(GLES20.GL_TEXTURE_COORD_ARRAY);
//
    	// Point to our buffers
//      GLES20.glTexCoordPointer(2, GLES20.GL_FLOAT, 0, mTextureBuffer);
			
//      GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureHandle);

        int textureUnitHandle = GLES20.glGetUniformLocation(program.getHandle(), "uTextureUnit");
        GLES20Utils.checkGlError("glGetUniformLocation");

        int textureCoordinateHandle = GLES20.glGetAttribLocation(program.getHandle(), "aTextureCoordinate");
        GLES20Utils.checkGlError("glGetAttribLocation");
 
                // Set the active texture unit to texture unit 0.
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20Utils.checkGlError("glActiveTexture");
        
        // Bind the texture to this unit.
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureHandle);
        GLES20Utils.checkGlError("glBindTexture");
        
        // Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0.
        GLES20.glUniform1i(textureUnitHandle, 0);       
        GLES20Utils.checkGlError("glUniform1i");

        GLES20.glEnableVertexAttribArray(textureCoordinateHandle);
        GLES20Utils.checkGlError("glEnableVertexAttribArray");
        
        // Pass in the texture coordinate information
        GLES20.glVertexAttribPointer(textureCoordinateHandle, TEXS_PER_VERTEX, GLES20.GL_FLOAT, false, 
          0, mTextureBuffer);
        GLES20Utils.checkGlError("glVertexAttribPointer");

//      GLES20.glDisableVertexAttribArray(textureCoordinateHandle);
    }
 }