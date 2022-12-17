package com.tecknobit.glider.ui.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.tecknobit.glider.R;

import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO;
import static androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode;

@SuppressLint("CustomSplashScreen")
public class SplashScreen extends AppCompatActivity {

    public static Activity STARTER_ACTIVITY;
    private static volatile boolean start = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        setDefaultNightMode(MODE_NIGHT_NO);
        STARTER_ACTIVITY = this;
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    sleep(2500);
                } catch (InterruptedException ignored) {
                } finally {
                    if (start)
                        startActivity(new Intent(SplashScreen.this, MainActivity.class));
                }
            }
        }.start();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        start = false;
        finishAffinity();
    }

    @Override
    protected void onResume() {
        super.onResume();
        start = true;
    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        start = false;
        finishAffinity();
    }

}