package se.federspiel.android.glgraphics;

import static junit.framework.Assert.fail;

import android.opengl.GLES20;

public class GLES20Utils
{
    /**
     * Utility method for debugging OpenGL calls. Provide the name of the call
     * just after making it:
     *
     * <pre>
     * mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
     * MyGLRenderer.checkGlError("glGetUniformLocation");</pre>
     *
     * If the operation is not successful, the check throws an error.
     *
     * @param glOperation - Name of the OpenGL call to check.
     */
    public static void checkGlError(String glOperation) 
    {
        int error;
    
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) 
        {
        	fail(glOperation + ": glError " + error);
            //throw new RuntimeException(glOperation + ": glError " + error);
        }
    }
}
