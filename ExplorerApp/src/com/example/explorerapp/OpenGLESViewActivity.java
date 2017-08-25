package com.example.explorerapp;

import static junit.framework.Assert.assertTrue;
import se.federspiel.android.gameviews.OpenGLESView;
import se.federspiel.android.util.ALog;
import se.federspiel.android.util.ASystem;
import android.os.Bundle;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.view.WindowManager;

public class OpenGLESViewActivity extends Activity
{
	private OpenGLESView mView = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		mView = new OpenGLESView(this);
		
		ALog.debug(this, "GL = " + ASystem.getOpenGLESVersion(this));
		
		if (ASystem.hasOpenGLES20Support(this))
		{
			setContentView(mView);
			
	//		setContentView(R.layout.activity_open_gles);
			
	        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	     	setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
		else
		{
			assertTrue(false);
		}
	}
	
	@Override
    protected void onPause() 
   	{
        super.onPause();

        mView.onPause();
//        ((OpenGLESView) findViewById(R.id.openGLESView)).onPause();
    }
    
    @Override
    protected void onResume() 
    {
        super.onResume();
   
        mView.onResume();
//        ((OpenGLESView) findViewById(R.id.openGLESView)).onResume();
    }
}
