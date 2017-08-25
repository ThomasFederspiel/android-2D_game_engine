package com.example.explorerapp;

import com.lamerman.fileexplorer.FileDialog;
import com.lamerman.fileexplorer.SelectionMode;

import se.federspiel.android.gameviews.SlidingPuzzleView;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;

public class SlidingPuzzleActivity extends Activity 
{
	private static final int REQUEST_OPEN = 1;

	private SlidingPuzzleView mSlidingPuzzleView = null;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sliding_puzzle);

        mSlidingPuzzleView = (SlidingPuzzleView) findViewById(R.id.slidingPuzzleView);
        
        assert mSlidingPuzzleView != null;
        
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
     	setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }
    
    @Override
    protected void onStart()
    {
    	super.onStart();

    	mSlidingPuzzleView.onStart();
    }
    
    @Override
    protected void onRestart()
    {
    	super.onRestart();

    	mSlidingPuzzleView.onStart();
    }

    @Override
    protected void onResume()
    {
    	super.onResume();
    	
    	mSlidingPuzzleView.onResume();
    }

    @Override
    protected void onPause()
    {
    	super.onPause();
    	
    	mSlidingPuzzleView.onPause();
    }

    @Override
    protected void onStop()
    {
    	super.onStop();
    	
    	mSlidingPuzzleView.onStop();
    }

    @Override
    protected void onDestroy()
    {
    	super.onDestroy();
    	
    	mSlidingPuzzleView.onDestroy();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_sliding_puzzle, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

    	switch (item.getItemId()) 
        {
            case R.id.menuitem_load_image:
            	openImage();
                return true;
                
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    
    private void openImage()
    {
    	Intent intent = new Intent(getBaseContext(), FileDialog.class);                
    	
    	intent.putExtra(FileDialog.START_PATH, Environment.getExternalStorageDirectory().getPath());                                
    	
    	intent.putExtra(FileDialog.SELECTION_MODE, SelectionMode.MODE_OPEN);                               
    	
    	intent.putExtra(FileDialog.SELECT_FILE_DIRECT, true);                                
    	
    	intent.putExtra(FileDialog.FORMAT_FILTER, new String[] { "png", "jpg", "jpeg" });                                
    	
    	startActivityForResult(intent, REQUEST_OPEN);
    }
    
    public synchronized void onActivityResult(final int requestCode, int resultCode, final Intent data) 
    {   
    	switch (resultCode)
    	{
    		case RESULT_OK : 
    		
	    		String imagePath = data.getStringExtra(FileDialog.RESULT_PATH);                

	    		mSlidingPuzzleView.startNewPuzzle(imagePath);
	    		
	    		break;
	    		
    		case RESULT_CANCELED :
    			break;
    			
    		default :
    			assert false;
    	}
    }
}


