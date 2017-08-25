package se.federspiel.android.glgraphics;

public abstract class AbstractFragmentShader extends AbstractShader
{
    public AbstractFragmentShader(String shaderCode)
	{
    	super(ShaderType.FragmentShader, shaderCode);
	}
}
