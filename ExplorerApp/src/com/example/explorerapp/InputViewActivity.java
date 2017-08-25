package com.example.explorerapp;

import se.federspiel.android.agraphics.CanvasSurfaceView;
import android.app.Activity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.TextView;

public class InputViewActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);
        
        
        initView();
    }

    @Override 
    public boolean onKeyDown(int keyCode, KeyEvent event) 
    { 
    	TextView textView = (TextView) findViewById(R.id.inputTextView);
    	
    	textView.append("A-OnKeyDown(key = " + keyCode + ", act = " + event.getAction() + ")\n");
    	
        return false; // super.onKeyDown(keyCode, event); 
    } 
    
    @Override 
    public boolean onKeyUp(int keyCode, KeyEvent event) 
    {
    	TextView textView = (TextView) findViewById(R.id.inputTextView);
    	
    	textView.append("A-OnKeyUp(key = " + keyCode + ", act = " + event.getAction() + ")\n");
    	
        return false; // super.onKeyDown(keyCode, event); 
    }
    
    public void initView()
    {
    	CanvasSurfaceView surface = (CanvasSurfaceView) findViewById(R.id.inputSurfaceView);

    	surface.setFocusable(true);
    	surface.setFocusableInTouchMode(true);
    	surface.requestFocus();
    	
    	surface.setOnClickListener(new OnClickListener() 
    	{
			@Override
			public void onClick(View view)
			{
		    	TextView textView = (TextView) findViewById(R.id.inputTextView);
		    	
		    	textView.append("OnClick\n");
			}
    		
    	});
    	
    	surface.setOnKeyListener(new OnKeyListener() 
    	{
			@Override
			public boolean onKey(View view, int keyCode, KeyEvent event)
			{
		    	TextView textView = (TextView) findViewById(R.id.inputTextView);
		    	
		    	textView.append("OnKey(key = " + keyCode + ", act = " + event.getAction() + ")\n");
		    	
				return false;
			}
    		
    	});
    	
    	surface.setOnLongClickListener(new OnLongClickListener() 
    	{

			@Override
			public boolean onLongClick(View view)
			{
				TextView textView = (TextView) findViewById(R.id.inputTextView);
		    	
		    	textView.append("OnLongKlick\n");
		    	
				return false;
			}
    		
    	});

    	surface.setOnTouchListener(new OnTouchListener() 
    	{

			@Override
			public boolean onTouch(View view, MotionEvent event)
			{
				TextView textView = (TextView) findViewById(R.id.inputTextView);
		    	
		    	textView.append("onTouch(x = " + event.getRawX() + ", y = " + event.getRawY() + ")\n" );

		    	textView.append("onTouch(a = " + event.getAction() + ", am = " + event.getActionMasked() + ")\n" );
		    	
		    	textView.append("onTouch(ai = " + event.getActionIndex() + ", pi = " + event.getPointerId(event.getActionIndex()) + ")\n" );
		    	
		    	textView.append("onTouch(hs = " + event.getHistorySize() + ", pc = " + event.getPointerCount() + ")\n" );
		    	
		    	textView.append("onTouch(s = " + event.getSize() + ", p = " + event.getPressure() + ")\n" );
		    	
				return false;
			}
    		
    	});
    	
    	Button clearButton = (Button) findViewById(R.id.clearInputButton);
    	
    	clearButton.setOnClickListener(new OnClickListener() 
    	{
			@Override
			public void onClick(View view)
			{
		    	TextView textView = (TextView) findViewById(R.id.inputTextView);
		    	
		    	textView.setText("");
			}
    		
    	});
    	
		TextView tv = (TextView) findViewById(R.id.inputTextView);

		tv.setMovementMethod(new ScrollingMovementMethod()); 
    }
}
