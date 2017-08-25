package se.federspiel.android.glgraphics;

import se.federspiel.android.game.interfaces.IGraphicPrimitiveFactory;
import se.federspiel.android.game.interfaces.gprimitives.ICirclePrimitive;
import se.federspiel.android.game.interfaces.gprimitives.IImagePrimitive;
import se.federspiel.android.game.interfaces.gprimitives.ILinePrimitive;
import se.federspiel.android.game.interfaces.gprimitives.ISquarePrimitive;
import se.federspiel.android.game.interfaces.gprimitives.ITrianglePrimitive;
import se.federspiel.android.glgraphics.primitives.Circle;
import se.federspiel.android.glgraphics.primitives.Image;
import se.federspiel.android.glgraphics.primitives.Line;
import se.federspiel.android.glgraphics.primitives.Square;
import se.federspiel.android.glgraphics.primitives.Triangle;

public class GraphicPrimitiveFactory implements IGraphicPrimitiveFactory
{
	private OpenGLES20Renderer mGLRender = null;
	private GLES20World mGLWorld = null;

	private GLES20Program<BaseVertextShader, ColorFragmentShader> mColorProgram = null;
	private GLES20Program<TextureVertextShader, TextureFragmentShader> mTextureProgram = null;
	
	public GraphicPrimitiveFactory(OpenGLES20Renderer renderer, GLES20World world)
	{
		mGLRender = renderer;
		mGLWorld = world;
	}

	public void init()
	{
		createPrograms();
	}
	
	public IImagePrimitive createImagePrimitive()
{
		return new Image(mTextureProgram);
	}

	public ICirclePrimitive createCirclePrimitive()
	{
		return new Circle(mColorProgram);
	}

	public ISquarePrimitive createSquarePrimitive()
	{
		return new Square(mColorProgram);
	}

	public ILinePrimitive createLinePrimitive()
	{
		return new Line(mColorProgram);
	}
	
	public ITrianglePrimitive createTrianglePrimitive()
	{
		return new Triangle(mColorProgram);
	}

	private void createPrograms()
	{
		mColorProgram = 
			new GLES20Program<BaseVertextShader, ColorFragmentShader>(mGLRender,
					mGLWorld,
					new BaseVertextShader(),
					new ColorFragmentShader());
		mColorProgram.linkProgram();
		
		mTextureProgram = 
			new GLES20Program<TextureVertextShader, TextureFragmentShader>(mGLRender,
					mGLWorld,
					new TextureVertextShader(),
					new TextureFragmentShader());
		mTextureProgram.linkProgram();
	}
}
