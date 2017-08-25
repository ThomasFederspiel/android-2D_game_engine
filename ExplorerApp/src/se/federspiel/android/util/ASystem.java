package se.federspiel.android.util;

import static junit.framework.Assert.assertTrue;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.graphics.Color;

public class ASystem
{
	private static final int OPEN_GL_ES_20 = 0x20000;

	public static boolean hasOpenGLES20Support(Context context)
	{
		return (getOpenGLESVersionId(context) >= OPEN_GL_ES_20);
	}
	
	public static int getOpenGLESVersionId(Context context)
	{
		final ActivityManager activityManager = 
		    (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		final ConfigurationInfo configurationInfo = 
		    activityManager.getDeviceConfigurationInfo();
		
		return configurationInfo.reqGlEsVersion;
	}
	
	public static String getOpenGLESVersion(Context context)
	{
		final ActivityManager activityManager = 
		    (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		final ConfigurationInfo configurationInfo = 
		    activityManager.getDeviceConfigurationInfo();
		
		return configurationInfo.getGlEsVersion();
	}
	
	public static float[] androidColorToGLESColor(int aColor, float glColor[])
	{
		assertTrue(glColor.length == 4);
		
		glColor[0] = Color.red(aColor) / 255.0f;	// Red
		glColor[1] = Color.green(aColor) / 255.0f;	// Green
		glColor[2] = Color.blue(aColor) / 255.0f;	// Blue
		glColor[3] = Color.alpha(aColor) / 255.0f;	// Alpha
		
		return glColor;
	}
}
