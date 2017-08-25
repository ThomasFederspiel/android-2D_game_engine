package com.example.explorerapp;

import se.federspiel.android.util.ALog;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class MainActivity extends Activity implements OnItemClickListener {

	private static final String APP_NAME = "ExplorerApp";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        ALog.setAppName(APP_NAME);
        
        ListView explorerView = (ListView) findViewById(R.id.explorerListView);
        
        explorerView.setOnItemClickListener(this);
    }

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

		ListView explorerView = (ListView) parent;
		
		String name = (String) explorerView.getItemAtPosition(position);
		
		createActivity(name);
	}
	
	private void createActivity(String name)
	{
		@SuppressWarnings("rawtypes")
		Class selectedActivity = null;
		
		if (name.equals("Sensors"))
		{
			selectedActivity = SensorSelectActivity.class;
		}
		else if (name.equals("Games"))
		{
			selectedActivity = GamesActivity.class;
		}
		else if (name.equals("Log"))
		{
			selectedActivity = LogViewActivity.class;
		}
		else if (name.equals("Input"))
		{
			selectedActivity = InputViewActivity.class;
		}
		else if (name.equals("OpenGL ES"))
		{
			selectedActivity = OpenGLESViewActivity.class;
		}
	
		else if (name.equals("Text to speech"))
		{
			selectedActivity = TextToSpeechActivity.class;
		}

		if (selectedActivity != null)
		{
	    	Intent intent = new Intent(this, selectedActivity);
	
	    	startActivity(intent);
		}
	}
}
