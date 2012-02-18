package be.bedroid.medreminder;

import java.util.HashMap;
import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;

public class NotificationActivity extends Activity implements OnInitListener, TextToSpeech.OnUtteranceCompletedListener {

	private static final String LOG_TAG = NotificationActivity.class.getName();

	private static final int MY_DATA_CHECK_CODE = 1;
	private TextToSpeech mTts;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		Intent checkIntent = new Intent();
		checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
		startActivityForResult(checkIntent, MY_DATA_CHECK_CODE);
	}


	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mTts != null) {
			mTts.shutdown();
		}
	}


	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == MY_DATA_CHECK_CODE) {
			if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
				// success, create the TTS instance
				mTts = new TextToSpeech(this, this);
			} else {
				// missing data, install it
				Intent installIntent = new Intent();
				installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
				startActivity(installIntent);
			}
		}
	}


	@Override
	public void onInit(int status) {
		Locale locale = Locale.getDefault();
		int result = mTts.isLanguageAvailable(locale);
		if (result == TextToSpeech.LANG_AVAILABLE) {
			mTts.setLanguage(locale);
			String myText1 = "This is the first sentence!";
			String myText2 = "Is this the second sentence?";
			HashMap<String, String> myHashAlarm = new HashMap<String, String>();
			mTts.setOnUtteranceCompletedListener(this);
			myHashAlarm.put(TextToSpeech.Engine.KEY_PARAM_STREAM, String.valueOf(AudioManager.STREAM_ALARM));
			mTts.speak(myText1, TextToSpeech.QUEUE_FLUSH, myHashAlarm);
			myHashAlarm.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "end of wakeup message ID");
			// myHashAlarm now contains two optional parameters
			mTts.speak(myText2, TextToSpeech.QUEUE_ADD, myHashAlarm);
		} else {
			Log.i(LOG_TAG, "Language not available");
		}
	}


	@Override
	public void onUtteranceCompleted(String uttId) {
		if (uttId == "end of wakeup message ID") {
			Log.i(LOG_TAG, "End of wakeup message");
		}
	}
}