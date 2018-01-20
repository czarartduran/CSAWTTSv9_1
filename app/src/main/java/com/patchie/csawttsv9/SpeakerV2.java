package com.patchie.csawttsv9;

import android.content.Context;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.Locale;

/**
 * Created by Czar Art Z. Duran on 06/01/2018.
 */

public class SpeakerV2 {
    private TextToSpeech _tts;
    private Context mContext;
    private boolean isready = false;

    public SpeakerV2(Context context){
        mContext = context;
        _tts = new TextToSpeech(mContext, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS){
                    int result = _tts.setLanguage(Locale.US);
                    isready = true;
                    Log.e("Czar", "SeapkerV2");
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED){
                        Log.e("Czar", "Local.US is not supported");
                    }
                }
            }
        });
    }

    public void speak(String TextToRead){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            _tts.speak(TextToRead, TextToSpeech.QUEUE_FLUSH, null, null);
        }else {
            _tts.speak(TextToRead, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    public boolean isReady(){
        return isready;
    }

    public void stop(){
        _tts.stop();
    }
}
