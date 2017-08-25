package se.federspiel.android.glgraphics;

public class TextureFragmentShader extends AbstractFragmentShader
{
	private static final String TextureFragmentShaderCode =
	    "precision mediump float;" +
		"uniform sampler2D uTextureUnit;" +
		"varying vec2 vTextureCoordinate;" +
	    "void main()" +
	    "{" +
		"  gl_FragColor = texture2D(uTextureUnit, vTextureCoordinate);" +
	    "}";
	
    public TextureFragmentShader()
	{
    	super(TextureFragmentShaderCode);
	}

    public TextureFragmentShader(String shaderCode)
	{
    	super(shaderCode);
	}
    
    
    
}
