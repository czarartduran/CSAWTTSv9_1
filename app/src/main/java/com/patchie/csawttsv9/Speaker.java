package com.patchie.csawttsv9;

import android.content.Context;
import android.media.AudioManager;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.HashMap;
import java.util.Locale;

/**
 * Created by Czar Art Z. Duran on 24/12/2017.
 */

public class Speaker implements TextToSpeech.OnInitListener {
    private TextToSpeech _tts;
    private boolean _ready = false;
    private boolean _allowed = true;
    private float _speachRate = 1;

    public Speaker(Context context){
        Log.e("Czar", "Speaker Context");
        _tts = new TextToSpeech(context, this);
        _tts.setSpeechRate(_speachRate);
        Log.e("Speaker speach rate: ", Float.toString(_speachRate));
    }

    public void setSpeedRate(float speechrate) {
        _tts.setSpeechRate(speechrate);
    }

    public boolean isAllowed(){
        return _allowed;
    }

    public void allow(boolean allowed){
        _allowed = allowed;
    }

    @Override
    public void onInit(int status) {
        Log.e("Czar","Speaker.java had been initialized");

        if(status == TextToSpeech.SUCCESS){
            _tts.setLanguage(Locale.US);
            //_tts.setVoice();
            _ready = true;
            Log.e("Czar", "Speaker.java onInit value: true");
        } else{
            _ready = false;
            Log.e("Czar", "Speaker.java onInit value: false");
        }
    }

    public void speak(String text){
        if(_ready && _allowed) {
            if (isSpeaking()) {
                _tts.stop();
            }
            HashMap<String, String> hash = new HashMap<String,String>();
            hash.put(TextToSpeech.Engine.KEY_PARAM_STREAM, String.valueOf(AudioManager.STREAM_NOTIFICATION));
            _tts.speak(text, TextToSpeech.QUEUE_ADD, hash);
            Log.e("Czar", "Speaking: " + text);
        }
    }

    public boolean isSpeaking() {
        return _tts.isSpeaking();
    }

    public void pause(int duration){
        _tts.playSilence(duration, TextToSpeech.QUEUE_ADD, null);
    }

    public void stop() {
        _tts.stop();
    }

    public void destroy(){
        _tts.shutdown();
    }
}
