package com.example.explorerapp;

import java.util.ArrayList;
import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class TextToSpeechActivity extends Activity implements TextToSpeech.OnInitListener {

	private TextToSpeech m_TTS = null;

	private int TTS_INITIAL_CHECK = 0;
	private int TTS_FINAL_CHECK = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_text_to_speech);

		initView();
	}

	private void initView() {
		
		Button speakButton = (Button) findViewById(R.id.speakButton);

		speakButton.setEnabled(false);
		
		speakButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				EditText textField = (EditText) findViewById(R.id.speechTextEditText);
				
				speak(textField.getText().toString());
			}
		});

		checkTTS(TTS_INITIAL_CHECK);
	}

	private void checkTTS(int requestCode) {
		Intent checkTTSIntent = new Intent();
        checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkTTSIntent, requestCode);
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		     
        if (requestCode == TTS_INITIAL_CHECK) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
            	m_TTS = new TextToSpeech(this, this);
            	
        		Spinner languageSpinner = (Spinner) findViewById(R.id.languageSpinner);
        		
            	ArrayList<String> languages = data.getStringArrayListExtra(TextToSpeech.Engine.EXTRA_AVAILABLE_VOICES);

            	ArrayAdapter<String> langArrayAdapter = new ArrayAdapter<String>(
                        this, android.R.layout.simple_spinner_item, languages);
            	
            	languageSpinner.setAdapter(langArrayAdapter);     
            }
            else {
                Intent installTTSIntent = new Intent();
                installTTSIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installTTSIntent);
                
        		checkTTS(TTS_FINAL_CHECK);
            }
        }
        else if (requestCode == TTS_FINAL_CHECK) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
            	m_TTS = new TextToSpeech(this, this);
            }
            else {
    			setTextField("No text engine present");
            }
        }
	}

	private void setTextField(String text) {
		EditText textField = (EditText) findViewById(R.id.speechTextEditText);
		
		textField.setText(text);
	}
	
	public void onInit(int initStatus) {
		
	    if (initStatus == TextToSpeech.SUCCESS) {
	    	
	        if (m_TTS.isLanguageAvailable(Locale.US) == TextToSpeech.LANG_AVAILABLE) {
	            m_TTS.setLanguage(Locale.US);
	        }
	        
			Button speakButton = (Button) findViewById(R.id.speakButton);

			speakButton.setEnabled(true);
	    }
	    else if (initStatus == TextToSpeech.ERROR) {
			setTextField("Text engine initiation failed");
	    }
	}
	
	private void speak(String text) {
		m_TTS.speak(text, TextToSpeech.QUEUE_FLUSH, null);
	}
}
