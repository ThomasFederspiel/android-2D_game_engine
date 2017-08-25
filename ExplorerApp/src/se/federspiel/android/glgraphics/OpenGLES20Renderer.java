package se.federspiel.android.glgraphics;

import java.util.Vector;

import static junit.framework.Assert.assertTrue;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.example.explorerapp.R;

import se.federspiel.android.game.GameApplication;
import se.federspiel.android.game.interfaces.gprimitives.ICirclePrimitive;
import se.federspiel.android.game.interfaces.gprimitives.IImagePrimitive;
import se.federspiel.android.game.interfaces.gprimitives.ILinePrimitive;
import se.federspiel.android.game.interfaces.gprimitives.ISquarePrimitive;
import se.federspiel.android.game.interfaces.gprimitives.ITrianglePrimitive;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

public class OpenGLES20Renderer implements GLSurfaceView.Renderer 
{
	private GLES20World mGLWorld = new GLES20World();
	
	private GraphicPrimitiveFactory mPrimitiveFactory = null;
	
	private IImagePrimitive mImage = null;
	private ITrianglePrimitive mTriangle = null;
	private ICirclePrimitive mCircle = null;
	private ISquarePrimitive mSquare = null;
	private ILinePrimitive mLine = null;
	
	private Vector<GLES20AbstractProgram> mPrograms = new Vector<GLES20AbstractProgram>();

	public OpenGLES20Renderer()
	{
		mPrimitiveFactory = new GraphicPrimitiveFactory(this, mGLWorld);
	}
	
	public void addGLProgram(GLES20AbstractProgram program)
	{
		assertTrue(!mPrograms.contains(program));
		
		mPrograms.add(program);
	}
	
	public void removeGLProgram(GLES20AbstractProgram program)
	{
		assertTrue(mPrograms.contains(program));
		
		mPrograms.remove(program);
	}
	
    public void onSurfaceCreated(GL10 unused, EGLConfig config) 
    {
        // Set the background frame color
        GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);

        mPrimitiveFactory.init();
        
    	mImage = mPrimitiveFactory.createImagePrimitive();
    	mImage.load(); 
    	mImage.setPosition(200, 200);
	    final BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inScaled = false;
	    
    	Bitmap bitmap = BitmapFactory.decodeResource(GameApplication.getResources(), 
    			R.raw.ghost_0, options);
    	
        mImage.setBitmap(bitmap, true);
        bitmap.recycle();
    	
    	mTriangle = mPrimitiveFactory.createTrianglePrimitive();
    	mTriangle.load();
    	
    	mCircle = mPrimitiveFactory.createCirclePrimitive();
    	mCircle.load(); 
    	mCircle.setRadius(40);
    	mCircle.setPosition(400, 400);
    	
    	mSquare = mPrimitiveFactory.createSquarePrimitive();
    	mSquare.load();
    	mSquare.setPosition(100, 400);
    	mSquare.setDimensions(50, 50);
        
    	mLine = mPrimitiveFactory.createLinePrimitive();
    	mLine.load();
    	mLine.setLine(50, 50, 100, 250);
    }

    public void onDrawFrame(GL10 unused) 
    {
        // Redraw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        for (int i = 0; i < mPrograms.size(); i++)
        {
        	mPrograms.get(i).draw();
        }
    }

    public void onSurfaceChanged(GL10 unused, int width, int height) 
    {
        GLES20.glViewport(0, 0, width, height);

        mGLWorld.viewPortCanvasUpdated(width, height);
    }
}