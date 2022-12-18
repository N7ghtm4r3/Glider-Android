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
import com.tecknobit.glider.ui.fragments.AccountFragment;
import com.tecknobit.glider.ui.fragments.CreateFragment;
import com.tecknobit.glider.ui.fragments.InsertFragment;
import com.tecknobit.glider.ui.fragments.ListFragment;
import com.tecknobit.glider.ui.fragments.RemovedFragment;

import static com.tecknobit.glider.R.id.nav_host_fragment_activity_main;
import static com.tecknobit.glider.R.id.navigation_account;
import static com.tecknobit.glider.R.id.navigation_create;
import static com.tecknobit.glider.R.id.navigation_insert;
import static com.tecknobit.glider.R.id.navigation_list;
import static com.tecknobit.glider.R.id.navigation_removed;

/**
 * The {@link MainActivity} activity is the container menu for all the fragments of the {@link BottomNavigationView}. <br>
 * From this activity is possible reach:
 * <ul>
 *     <li>{@link CreateFragment}</li>
 *     <li>{@link InsertFragment}</li>
 *     <li>{@link ListFragment}</li>
 *     <li>{@link RemovedFragment}</li>
 *     <li>{@link AccountFragment}</li>
 * </ul>
 *
 * @author Tecknobit - N7ghtm4r3
 * @see AppCompatActivity
 **/
public class MainActivity extends AppCompatActivity {

    /**
     * {@code MAIN_ACTIVITY} is the {@link MainActivity} context for the {@code Glider}'s workflow
     **/
    @SuppressLint("StaticFieldLeak")
    public static Activity MAIN_ACTIVITY;

    /**
     * {@inheritDoc}
     * <p>
     * Perform initialization of all fragments.
     */
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

    /**
     * {@inheritDoc}
     *
     * @apiNote when called will be invoked the {@link #finishAffinity()} method to exit from this app
     **/
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }

    /**
     * {@inheritDoc}
     *
     * @apiNote when called will be invoked the {@link #finishAffinity()} method to exit from this app
     **/
    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        finishAffinity();
    }

}