package com.neelkanthjdabhi.isight;

import android.content.Intent;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private Vibrator myVib;
    TTSManager ttsManager = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        setContentView(R.layout.activity_main);
        myVib = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);
        final ViewGroup viewGroup = (ViewGroup) ((ViewGroup) this
                .findViewById(android.R.id.content)).getChildAt(0);
        ttsManager = new TTSManager();
        ttsManager.init(this);

        viewGroup.setOnTouchListener(new OnSwipeTouchListener(MainActivity.this) {
            public void onSwipeTop() {
                myVib.vibrate(50);
                Toast.makeText(MainActivity.this, "Image Description", Toast.LENGTH_SHORT).show();
                ttsManager.initQueue("Image Description Opened");
                Intent ImageDesc = new Intent(MainActivity.this,ImageDescription.class);
                startActivity(ImageDesc);
            }
            public void onSwipeRight() {
                myVib.vibrate(50);
                ttsManager.initQueue("You are Currently on Home Activity");
            }
            public void onSwipeLeft() {
                myVib.vibrate(50);
                ttsManager.initQueue("Text Detection Opened");
                Intent textDetect = new Intent(MainActivity.this,TextDetection.class);
                startActivity(textDetect);

            }
            public void onSwipeBottom() {
                myVib.vibrate(50);
                ttsManager.initQueue("Already on Home");
            }
        });
    }
}

