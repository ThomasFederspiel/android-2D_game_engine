package com.example.explorerapp;

import java.util.ArrayList;

import se.federspiel.android.util.ALog;
import se.federspiel.android.util.ALog.ALogRecordDatabase;
import se.federspiel.android.util.ALog.ALogRecordDatabase.ALogRecord;
import android.app.Activity;
import android.os.Bundle;
import android.text.Layout;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class LogViewActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_log_view);

		initView();
	}
	
	private void initView()
	{
		EditText searchField = (EditText) findViewById(R.id.logSearchEditText);
		
		searchField.setText(ALog.getAppName());
		
    	ArrayAdapter<ALogRecordDatabase.LogCatFormatEnum> adapter = new ArrayAdapter<ALogRecordDatabase.LogCatFormatEnum>(this,
  			  android.R.layout.simple_spinner_item, android.R.id.text1, ALogRecordDatabase.LogCatFormatEnum.values());
    	
		Spinner formatSpinner = (Spinner) findViewById(R.id.logFormatSpinner);
		
		formatSpinner.setAdapter(adapter);     

		Button clearButton = (Button) findViewById(R.id.clearLogButton);

		clearButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				ALog.clearApplicationLog();

				fillTextView();
			}
		});

		Button searchButton = (Button) findViewById(R.id.searchLogButton);

		searchButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				fillTextView();
			}
		});
		
		Button endButton = (Button) findViewById(R.id.endButton);

		endButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				toEnd();
			}
		});
	}
	
	private void fillTextView()
	{
		EditText searchField = (EditText) findViewById(R.id.logSearchEditText);
		
		String searchString = searchField.getText().toString();

		CheckBox regExpCheckBox = (CheckBox) findViewById(R.id.logRegExpCheckBox);

		boolean regExpSelected = regExpCheckBox.isChecked();
		
		Spinner formatSpinner = (Spinner) findViewById(R.id.logFormatSpinner);
		
		ALogRecordDatabase.LogCatFormatEnum format = (ALogRecordDatabase.LogCatFormatEnum) formatSpinner.getSelectedItem();
		
		ALogRecordDatabase recordData = ALog.readApplicationLog(format, searchString, regExpSelected);

		StringBuilder log = new StringBuilder();
		
		ArrayList<ALogRecord> records = recordData.getAllRecords();
		
		for (ALogRecord record : records)
		{
			log.append(record.toString());
			log.append("\n");
		}

		TextView tv = (TextView)findViewById(R.id.logTextView);

		tv.setMovementMethod(new ScrollingMovementMethod()); 

		tv.setText(log.toString());
	}
	
	private void toEnd()
	{
		TextView tv = (TextView)findViewById(R.id.logTextView);

	    Layout layout = tv.getLayout();
	    
	    if (layout != null)
	    {
            int scrollDelta = layout.getLineBottom(tv.getLineCount() - 1) 
                - tv.getScrollY() - tv.getHeight();
            
            if (scrollDelta > 0)
            {
            	tv.scrollBy(0, scrollDelta);
            }
	    }
	}
	
}
