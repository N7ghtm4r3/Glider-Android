package com.tecknobit.glider.ui.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.tecknobit.glider.R;

import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO;
import static androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode;

/**
 * The {@link SplashScreen} activity is the first screen of the {@code Glider} app. <br>
 * This activity is useful to load the base data for the {@code Glider}'s workflow
 *
 * @author Tecknobit - N7ghtm4r3
 * @see AppCompatActivity
 **/
@SuppressLint("CustomSplashScreen")
public class SplashScreen extends AppCompatActivity {

    /**
     * {@code STARTER_ACTIVITY} is the global context for the {@code Glider}'s workflow
     * **/
    @SuppressLint("StaticFieldLeak")
    public static Activity STARTER_ACTIVITY;

    /**
     * {@code start} flag whether start or not with the {@code Glider}'s workflow in those cases
     * when {@link #onBackPressed()} or {@link #onUserLeaveHint()} methods have been called
     * **/
    private static volatile boolean start = true;

    /**
     * {@inheritDoc}
     *
     * Perform initialization of all fragments.
     */
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

    /**
     * {@inheritDoc}
     *
     * @apiNote when called {@link #start} will be set on {@code "false"} and will be called
     * {@link #finishAffinity()} method to exit from this app
     **/
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        start = false;
        finishAffinity();
    }

    /**
     * {@inheritDoc}
     *
     * @apiNote when called {@link #start} will be set on {@code "true"} and will be
     * resumed the normal {@code "Glider"}'s workflow
     **/
    @Override
    protected void onResume() {
        super.onResume();
        start = true;
    }

    /**
     * {@inheritDoc}
     *
     * @apiNote when called {@link #start} will be set on {@code "false"} and will be called
     * {@link #finishAffinity()} method to exit from this app
     **/
    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        start = false;
        finishAffinity();
    }

}