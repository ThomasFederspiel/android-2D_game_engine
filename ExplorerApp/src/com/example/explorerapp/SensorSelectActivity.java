package com.example.explorerapp;

import java.util.ArrayList;
import java.util.List;

import se.federspiel.android.data.GlobalDataStorage;
import se.federspiel.android.sensor.AbstractSensor;
import se.federspiel.android.sensor.ISensor;
import se.federspiel.android.util.ALog;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

public class SensorSelectActivity extends Activity {

	private SensorManager mSensorManager = null;
	private ISensor mSensor = null;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        
        initView();
    }

    private void initView()
    {
    	List<Sensor> deviceSensors = mSensorManager.getSensorList(Sensor.TYPE_ALL);

    	List<SensorItem> sensorItems = new ArrayList<SensorItem>();

    	for(Sensor sensor : deviceSensors)
    	{
    		AbstractSensor abstractSensor = AbstractSensor.createSensor(sensor, this);
    		
    		if (abstractSensor != null) 
    		{
    			sensorItems.add(new SensorItem(abstractSensor));
    		}
    	}
    	
    	ArrayAdapter<SensorItem> adapter = new ArrayAdapter<SensorItem>(this,
    			  android.R.layout.simple_spinner_item, android.R.id.text1, sensorItems);

        Spinner spinner = (Spinner) findViewById(R.id.sensorSpinner);
        
		spinner.setAdapter(adapter);     
		
		spinner.setOnItemSelectedListener(new OnItemSelectedListener()
		{
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) 
			{
				ALog.debug(this, "onItemSelected");				
				
				Spinner sensorSpinner = (Spinner) parent;
				
				SensorItem sensorItem = (SensorItem) sensorSpinner.getItemAtPosition(position);
				
				setupSensorView(sensorItem.getSensor());
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) 
			{
				ALog.debug(this, "onNothingSelected");				
			}
		});
		
        Button button = (Button) findViewById(R.id.sensorButton);

        button.setOnClickListener(new OnClickListener()
        {
			@Override
			public void onClick(View parent)
			{
	        	activateSensorView(mSensor);
			}
        });
	}

	private void activateSensorView(ISensor sensor)
	{
		ALog.debug(this, "activateSensorView");				

		Intent intent = new Intent(this, SensorViewActivity.class);

		GlobalDataStorage.instance().addObject(SensorViewActivity.class.getName(), "Sensor", sensor);
		
		intent.putExtra("Key1", "Sensor");

		startActivity(intent);
	}

	private void setupSensorView(ISensor sensor) 
	{
		mSensor = sensor;
		
		TextView textField = (TextView) findViewById(R.id.vendorField);
        textField.setText(sensor.getVendor());
        
        textField = (TextView) findViewById(R.id.versionField);
        textField.setText("" + sensor.getVersion());
        
        textField = (TextView) findViewById(R.id.resolutionField);
        textField.setText("" + sensor.getResolution() + " " + sensor.getUnitString());

        textField = (TextView) findViewById(R.id.rangeField);
        textField.setText("" + sensor.getMaximumRange() + " " + sensor.getUnitString());

        textField = (TextView) findViewById(R.id.powerField);
        textField.setText("" + sensor.getPower() + " [mA]");
	}

	public class SensorItem
	{
		private ISensor mSensor = null;
		
		public SensorItem()
		{
		}
		
		public SensorItem(ISensor sensor)
		{
			mSensor = sensor;
		}

		public ISensor getSensor()
		{
			return mSensor;
		}
		
		public String toString()
		{
			return mSensor.getName();
		}
	}
}
