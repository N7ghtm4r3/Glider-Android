package com.tecknobit.glider.ui.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.tecknobit.glider.R;
import com.tecknobit.glider.helpers.local.User;
import com.tecknobit.glider.ui.fragments.AccountFragment;
import com.tecknobit.glider.ui.fragments.ListFragment;
import com.tecknobit.glider.ui.fragments.passwordmanager.CreateFragment;
import com.tecknobit.glider.ui.fragments.passwordmanager.InsertFragment;

import static com.tecknobit.glider.R.id.nav_host_fragment_activity_main;
import static com.tecknobit.glider.R.id.navigation_account;
import static com.tecknobit.glider.R.id.navigation_create;
import static com.tecknobit.glider.R.id.navigation_insert;
import static com.tecknobit.glider.R.id.navigation_list;
import static com.tecknobit.glider.R.id.navigation_removed;
import static com.tecknobit.glider.helpers.local.User.refreshData;
import static com.tecknobit.glider.helpers.local.Utils.setLanguageLocale;

/**
 * The {@link MainActivity} activity is the container menu for all the fragments of the {@link BottomNavigationView}. <br>
 * From this activity is possible reach:
 * <ul>
 *     <li>{@link CreateFragment}</li>
 *     <li>{@link InsertFragment}</li>
 *     <li>
 *         The {@link ListFragment}:
 *         <ul>
 *             <li>
 *                 {@link Fragment} for the current password stored
 *             </li>
 *             <li>
 *                 {@link Fragment} for the deleted password that can be definitely deleted or recovered
 *             </li>
 *         </ul>
 *     </li>
 *     <li>{@link AccountFragment}</li>
 * </ul>
 *
 * @author Tecknobit - N7ghtm4r3
 * @see AppCompatActivity
 */
public class MainActivity extends AppCompatActivity {

    /**
     * {@code MAIN_ACTIVITY} is the {@link MainActivity} context for the {@code Glider}'s workflow
     */
    @SuppressLint("StaticFieldLeak")
    public static Activity MAIN_ACTIVITY;

    /**
     * {@code navController} is the navigation controller of the {@link BottomNavigationView}
     */
    @SuppressLint("StaticFieldLeak")
    public static NavController navController;

    /**
     * {@inheritDoc}
     * <p>
     * Perform initialization of all fragments.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setLanguageLocale(this);
        super.onCreate(savedInstanceState);
        MAIN_ACTIVITY = this;
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(navigation_create,
                navigation_insert, navigation_list, navigation_removed, navigation_account).build();
        navController = Navigation.findNavController(this, nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
    }

    /**
     * {@inheritDoc}
     *
     * @apiNote when called {@link User#refreshData} on {@code "true"} and will be
     * resumed the normal {@code "Glider"}'s workflow
     */
    @Override
    protected void onResume() {
        super.onResume();
        refreshData = true;
    }

    /**
     * {@inheritDoc}
     *
     * @apiNote when called will be invoked the {@link #finishAffinity()} method to exit from this app
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        refreshData = false;
        finishAffinity();
    }

    /**
     * {@inheritDoc}
     *
     * @apiNote when called will be invoked the {@link #finishAffinity()} method to exit from this app
     */
    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        refreshData = false;
        finishAffinity();
    }

}