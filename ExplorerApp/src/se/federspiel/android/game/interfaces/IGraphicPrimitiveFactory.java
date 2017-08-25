package se.federspiel.android.game.interfaces;

import se.federspiel.android.game.interfaces.gprimitives.ICirclePrimitive;
import se.federspiel.android.game.interfaces.gprimitives.IImagePrimitive;
import se.federspiel.android.game.interfaces.gprimitives.ILinePrimitive;
import se.federspiel.android.game.interfaces.gprimitives.ISquarePrimitive;
import se.federspiel.android.game.interfaces.gprimitives.ITrianglePrimitive;

public interface IGraphicPrimitiveFactory
{
	public IImagePrimitive createImagePrimitive();
	public ICirclePrimitive createCirclePrimitive();
	public ISquarePrimitive createSquarePrimitive();
	public ILinePrimitive createLinePrimitive();
	public ITrianglePrimitive createTrianglePrimitive();
}
