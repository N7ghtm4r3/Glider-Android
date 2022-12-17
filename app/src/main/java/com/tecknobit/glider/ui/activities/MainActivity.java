package com.tecknobit.glider.ui.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.tecknobit.glider.R;

import static com.tecknobit.glider.R.id.nav_host_fragment_activity_main;
import static com.tecknobit.glider.R.id.navigation_account;
import static com.tecknobit.glider.R.id.navigation_create;
import static com.tecknobit.glider.R.id.navigation_insert;
import static com.tecknobit.glider.R.id.navigation_list;
import static com.tecknobit.glider.R.id.navigation_removed;

public class MainActivity extends AppCompatActivity {

    @SuppressLint("StaticFieldLeak")
    public static Activity MAIN_ACTIVITY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MAIN_ACTIVITY = this;
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(navigation_create,
                navigation_insert, navigation_list, navigation_removed, navigation_account).build();
        NavController navController = Navigation.findNavController(this, nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        finishAffinity();
    }

}