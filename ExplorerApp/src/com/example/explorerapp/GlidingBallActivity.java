package com.example.explorerapp;

import android.os.Bundle;
import android.view.WindowManager;
import android.app.Activity;
import android.content.pm.ActivityInfo;

public class GlidingBallActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gliding_ball);
        
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    	setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }
}
