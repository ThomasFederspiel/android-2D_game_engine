package se.federspiel.android.agraphics;

import se.federspiel.android.game.interfaces.IGraphicPrimitiveFactory;
import se.federspiel.android.game.interfaces.gprimitives.ICirclePrimitive;
import se.federspiel.android.game.interfaces.gprimitives.IImagePrimitive;
import se.federspiel.android.game.interfaces.gprimitives.ILinePrimitive;
import se.federspiel.android.game.interfaces.gprimitives.ISquarePrimitive;
import se.federspiel.android.game.interfaces.gprimitives.ITrianglePrimitive;
import se.federspiel.android.agraphics.primitives.Circle;
import se.federspiel.android.agraphics.primitives.Image;
import se.federspiel.android.agraphics.primitives.Line;
import se.federspiel.android.agraphics.primitives.Square;
import se.federspiel.android.agraphics.primitives.Triangle;

public class GraphicPrimitiveFactory implements IGraphicPrimitiveFactory
{
	public GraphicPrimitiveFactory()
	{
	}

	public IImagePrimitive createImagePrimitive()
{
		return new Image();
	}

	public ICirclePrimitive createCirclePrimitive()
	{
		return new Circle();
	}

	public ISquarePrimitive createSquarePrimitive()
	{
		return new Square();
	}

	public ILinePrimitive createLinePrimitive()
	{
		return new Line();
	}
	
	public ITrianglePrimitive createTrianglePrimitive()
	{
		return new Triangle();
	}
}
