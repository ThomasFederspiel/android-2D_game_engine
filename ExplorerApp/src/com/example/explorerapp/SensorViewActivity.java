package com.example.explorerapp;

import java.util.Vector;

import se.federspiel.android.data.GlobalDataStorage;
import se.federspiel.android.sensor.AbstractSensor;
import se.federspiel.android.sensor.ISensor;
import se.federspiel.android.sensor.ISensorChangedEventListener;
import se.federspiel.android.sensor.filter.FilterZeroAdjust;
import android.app.Activity;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout.LayoutParams;
import android.widget.TextView;

public class SensorViewActivity extends Activity implements ISensorChangedEventListener {

	private ISensor mRawSensor = null;
	private ISensor mFilteredSensor = null;

	private Vector<TextView> mRawValueFields = new Vector<TextView>();
	private Vector<TextView> mFilteredValueFields = new Vector<TextView>();
	private FilterZeroAdjust mZeroAdjustFilter = null;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String key = getIntent().getStringExtra("Key1"); 

        ISensor sensor = GlobalDataStorage.instance().<ISensor>getObject(SensorViewActivity.class.getName(), key);
        
        initView(sensor);
    }

	@Override
    protected void onResume() {
        super.onResume();
		mRawSensor.setOnSensorChangedListener(this, SensorManager.SENSOR_DELAY_NORMAL);
		mFilteredSensor.setOnSensorChangedListener(this, SensorManager.SENSOR_DELAY_NORMAL);
    }

	@Override
    protected void onPause() {
        super.onPause();
		mRawSensor.removeOnSensorChangedListener(this);
		mFilteredSensor.removeOnSensorChangedListener(this);
    }
    
    private void initView(ISensor sensor)
    {
		mRawSensor = sensor;

		mFilteredSensor = AbstractSensor.cloneSensor(mRawSensor);
		
		mZeroAdjustFilter = new FilterZeroAdjust(FilterZeroAdjust.ZeroAdjustFilterMode.AverageMode, 50);
		
		mFilteredSensor.addFilter(mZeroAdjustFilter);
		
		LinearLayout layout = new LinearLayout(this);
		
		layout.setOrientation(LinearLayout.VERTICAL);
		
		LayoutParams params = new LayoutParams();
		params.height = LayoutParams.MATCH_PARENT;
		params.width = LayoutParams.MATCH_PARENT;
		layout.setLayoutParams(params);

		LayoutParams textLabelParams = new LayoutParams();
		textLabelParams.height = LayoutParams.WRAP_CONTENT;
		textLabelParams.width = LayoutParams.MATCH_PARENT;
		
		TextView textlabel = new TextView(this);
		textlabel.setText(R.string.rawValuesLabel);
		layout.addView(textlabel, textLabelParams);
		
		for (int i = 0; i < mRawSensor.getValueCount(); i++)
		{
			LayoutParams textParams = new LayoutParams();
			textParams.height = LayoutParams.WRAP_CONTENT;
			textParams.width = LayoutParams.MATCH_PARENT;
			
			TextView textview = new TextView(this);
			textview.setText("Not Available");
			layout.addView(textview, textParams);
			
			mRawValueFields.add(textview);
		}
		
		textLabelParams = new LayoutParams();
		textLabelParams.height = LayoutParams.WRAP_CONTENT;
		textLabelParams.width = LayoutParams.MATCH_PARENT;
		
		textlabel = new TextView(this);
		textlabel.setText(R.string.filteredValuesLabel);
		layout.addView(textlabel, textLabelParams);
		
		for (int i = 0; i < mFilteredSensor.getValueCount(); i++)
		{
			LayoutParams textParams = new LayoutParams();
			textParams.height = LayoutParams.WRAP_CONTENT;
			textParams.width = LayoutParams.MATCH_PARENT;
			
			TextView textview = new TextView(this);
			textview.setText("Not Available");
			layout.addView(textview, textParams);
			
			mFilteredValueFields.add(textview);
		}

		LayoutParams buttonParams = new LayoutParams();
		buttonParams.height = LayoutParams.WRAP_CONTENT;
		buttonParams.width = LayoutParams.WRAP_CONTENT;
		
		Button zeroButton = new Button(this);
		zeroButton.setText(R.string.resetZeroFilterText);
		layout.addView(zeroButton, buttonParams);

		zeroButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				mZeroAdjustFilter.reset();
			}
		});
		
		setContentView(layout);
	}

	@Override
	public void onAccuracyChanged(ISensor sensor, int accuracy)
	{
	}

	@Override
	public void onSensorChanged(ISensor sensor)
	{
		Vector<TextView> textFields = null;
		
		if (sensor == mRawSensor)
		{
			textFields = mRawValueFields;
		}
		else if (sensor == mFilteredSensor)
		{
			textFields = mFilteredValueFields;
		}
		
		if (textFields != null)
		{
			float[] rawValues = sensor.getLastRawSensorValues();
			String[] names = sensor.getValueNames();
			String unit = sensor.getUnitString();
			
			for (int i = 0; i < textFields.size(); i++)
			{
				TextView textField = textFields.get(i);
	
				textField.setText(names[i] + " :  " + rawValues[i] + " " + unit);
			}
		}
	}
}
