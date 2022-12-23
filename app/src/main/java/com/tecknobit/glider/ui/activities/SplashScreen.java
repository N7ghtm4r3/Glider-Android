package com.tecknobit.glider.ui.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.tecknobit.glider.R;
import com.tecknobit.glider.helpers.local.User;
import com.tecknobit.glider.ui.fragments.Connect;

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
        Animation animation = AnimationUtils.loadAnimation(SplashScreen.this, R.anim.fadein);
        for (int id : new int[]{R.id.appName, R.id.byTecknobit})
            findViewById(id).startAnimation(animation);
        new CountDownTimer(2000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                if (start) {
                    // TODO: 23/12/2022 CHANGE WITH THE REAL WORKFLOW
                    if (User.SECRET_KEY != null)
                        startActivity(new Intent(SplashScreen.this, MainActivity.class));
                    else {
                        runOnUiThread(() -> {
                            findViewById(R.id.container).setVisibility(View.VISIBLE);
                            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                            transaction.add(R.id.container, new Connect());
                            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_MATCH_ACTIVITY_OPEN);
                            transaction.addToBackStack(null);
                            transaction.commitAllowingStateLoss();
                            findViewById(R.id.relContainer).setVisibility(View.GONE);
                        });
                    }
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