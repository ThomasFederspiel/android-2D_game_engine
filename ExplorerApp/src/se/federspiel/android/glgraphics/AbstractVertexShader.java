package se.federspiel.android.glgraphics;

public abstract class AbstractVertexShader extends AbstractShader
{
    public AbstractVertexShader(String shaderCode)
	{
    	super(ShaderType.VertextShader, shaderCode);
	}
}
