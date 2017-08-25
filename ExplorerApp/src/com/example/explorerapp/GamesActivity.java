package com.example.explorerapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class GamesActivity extends Activity implements OnItemClickListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_games);
        
        ListView explorerView = (ListView) findViewById(R.id.gamesListView);
        
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
        
		if (name.equals("Ball bounce"))
		{
			selectedActivity = BouncingBallsActivity.class;
		}
		else if (name.equals("Ball glider"))
		{
			selectedActivity = GlidingBallActivity.class;
		}
		else if (name.equals("Slide shooter"))
		{
			selectedActivity = SlideShooterActivity.class;
		}
		else if (name.equals("Breakout"))
		{
			selectedActivity = BreakoutActivity.class;
		}
		else if (name.equals("Sliding puzzle"))
		{
			selectedActivity = SlidingPuzzleActivity.class;
		}
		else if (name.equals("Drop ball"))
		{
			selectedActivity = DropBallActivity.class;
		}
		else if (name.equals("Maze"))
		{
			selectedActivity = MazeActivity.class;
		}
		else if (name.equals("Platform"))
		{
			selectedActivity = PlatformActivity.class;
		}
		else if (name.equals("Unit tests"))
		{
			selectedActivity = CollisionTestActivity.class;
		}

		if (selectedActivity != null)
		{
	    	Intent intent = new Intent(this, selectedActivity);
	
	    	startActivity(intent);
		}
	}
}
