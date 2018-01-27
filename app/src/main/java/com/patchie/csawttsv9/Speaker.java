package com.patchie.csawttsv9;

import android.content.Context;
import android.media.AudioManager;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.HashMap;
import java.util.Locale;

/**
 * Created by Czar Art Z. Duran on 24/12/2017.
 */

public class Speaker {
    private TextToSpeech _tts;
    private boolean _ready = false;
    private boolean _allowed = true;
    private float _speechRate = 1;

    public Speaker(Context context) {
        Log.e("Czar", "Initializing Speaker");
        _tts = new TextToSpeech(context, new TextToSpeech.OnInitListener() {

            @Override
            public void onInit(int status) {
                Log.e("Czar", "Speaker.java had been initialized");

                if (status == TextToSpeech.SUCCESS) {
                    _tts.setLanguage(Locale.US);
                    _ready = true;
                    Log.e("Czar", "Speaker.java onInit value: true");
                } else {
                    _ready = false;
                    Log.e("Czar", "Speaker.java onInit value: false");
                }
            }
        });
    }

    public Speaker(Context context, final String WelcomeMessage) {
        Log.e("Czar", "Initializing Speaker");
        _tts = new TextToSpeech(context, new TextToSpeech.OnInitListener() {

            @Override
            public void onInit(int status) {
                Log.e("Czar", "Speaker.java had been initialized");

                if (status == TextToSpeech.SUCCESS) {
                    _tts.setLanguage(Locale.US);
                    _ready = true;
                    SpeakWelcomeMessage(WelcomeMessage);
                    Log.e("Czar", "Speaker.java onInit value: true");
                } else {
                    _ready = false;
                    Log.e("Czar", "Speaker.java onInit value: false");
                }
            }
        });
    }

    private void SpeakWelcomeMessage(String WelcomeMessage) {
        Log.e("Czar", "MessageLength: " + WelcomeMessage.length());
        Log.e("Speaker", "Is Speaking: " + isSpeaking());
        if (WelcomeMessage.length() > 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Log.e("Speaker", "New: Speaking: " + WelcomeMessage);
                _tts.speak(WelcomeMessage, TextToSpeech.QUEUE_FLUSH, null, null);
            } else {
                Log.e("Speaker", "OLD: Speaking: " + WelcomeMessage);
                HashMap<String, String> hash = new HashMap<String, String>();
                hash.put(TextToSpeech.Engine.KEY_PARAM_STREAM, String.valueOf(AudioManager.STREAM_NOTIFICATION));
                _tts.speak(WelcomeMessage, TextToSpeech.QUEUE_FLUSH, hash);
            }

        }
    }

    public void setSpeedRate(float speechrate) {
        _tts.setSpeechRate(speechrate);
    }

    public boolean isAllowed() {
        return _allowed;
    }

    public void allow(boolean allowed) {
        this._allowed = allowed;
    }

    /*@Override
    public void onInit(int status) {
        Log.e("Czar","Speaker.java had been initialized");

        if(status == TextToSpeech.SUCCESS){
            _tts.setLanguage(Locale.US);
            _ready = true;
            Log.e("Czar", "Speaker.java onInit value: true");
        } else{
            _ready = false;
            Log.e("Czar", "Speaker.java onInit value: false");
        }
    }*/

    public boolean isReady() {
        return _ready;
    }

    public void speak(String text) {
        _tts.stop();
        Log.e("Speaker", "Is Speaking: " + isSpeaking());
        if (_tts.isSpeaking()) {
            _tts.stop();
        }

        Log.e("Speaker", "Is Speaking: " + isSpeaking());
        if (_ready && _allowed) {
            HashMap<String, String> hash = new HashMap<String, String>();
            hash.put(TextToSpeech.Engine.KEY_PARAM_STREAM, String.valueOf(AudioManager.STREAM_NOTIFICATION));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                _tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
                Log.e("Speaker", "New: Speaking: " + text);
            } else {
                int tts = _tts.speak(text, TextToSpeech.QUEUE_FLUSH, hash);
                Log.e("Speaker", "OLD Speaking: " + text + " : " + tts);
            }
        }

    }

    public void speakAdd(String text) {
        Log.e("Czar", "Speaking: " + isSpeaking());
        if (_ready && _allowed) {
            HashMap<String, String> hash = new HashMap<String, String>();
            hash.put(TextToSpeech.Engine.KEY_PARAM_STREAM, String.valueOf(AudioManager.STREAM_NOTIFICATION));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                _tts.speak(text, TextToSpeech.QUEUE_ADD, null, null);
                Log.e("Speaker", "New SpeakAdd: " + text);
            } else {
                _tts.speak(text, TextToSpeech.QUEUE_ADD, hash);
                Log.e("Speaker", "OLD SpeakAdd: " + text + " : " + text);
            }
        }
    }

    public boolean isSpeaking() {
        return _tts.isSpeaking();
    }

    public void pause(int duration) {
        //_tts.playSilence(duration, TextToSpeech.QUEUE_ADD, null);
        _tts.playSilentUtterance(duration, TextToSpeech.QUEUE_ADD, null);
    }

    public void stop() {
        _tts.stop();
    }

    public void destroy() {
        if (_tts != null) {
            _tts.stop();
            _tts.shutdown();
            Log.e("Speaker", "Speaker destroyed");
        }
    }
}
