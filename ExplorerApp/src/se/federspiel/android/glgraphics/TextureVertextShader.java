package se.federspiel.android.glgraphics;

public class TextureVertextShader extends BaseVertextShader
{
	private static final String TextureVertexShaderCode =
        "uniform mat4 uMVPMatrix;" +
        "attribute vec4 aPosition;" +
    	"attribute vec2 aTextureCoordinate;" +
        "varying vec2 vTextureCoordinate;" +
        "void main()" +
	    "{" +
  	  	"  vTextureCoordinate = aTextureCoordinate;" +
        "  gl_Position = uMVPMatrix * aPosition;" +
        "}";

    public TextureVertextShader()
	{
    	super(TextureVertexShaderCode);
	}

    public TextureVertextShader(String shaderCode)
	{
    	super(shaderCode);
	}
    
    
    
}
