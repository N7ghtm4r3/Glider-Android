package com.tecknobit.glider.helpers.local;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import static android.content.pm.PackageManager.NameNotFoundException;
import static com.google.firebase.database.FirebaseDatabase.getInstance;
import static com.tecknobit.glider.ui.activities.SplashScreen.STARTER_ACTIVITY;

/**
 * The {@code User} class is a useful to manage the user session during the app workflow
 *
 * @author Tecknobit - N7ghtm4r3
 **/
public class User {

    // TODO: 17/12/2022 SET INSTANCES WITH THE CORRECT VALUE
    /**
     * {@code SECRET_KEY} secret key used during the communication with the backend service
     */
    public static String SECRET_KEY = null;

    /**
     * {@code user} instance to manage the user session in app
     */
    public static volatile User user;

    /**
     * {@code IS_UPDATED} whether the current version of the app is the latest available
     */
    public static volatile boolean IS_UPDATED;

    /**
     * {@code IS_PATCH} whether the update is a simple patch (it can be skipped) or is a mandatory
     * version update
     */
    public static volatile boolean IS_PATCH;

    /**
     * {@code LATEST_VERSION} latest version available for {@code Glider}
     */
    public static volatile String LATEST_VERSION;

    // TODO: 06/01/2023 INSERT WITH SESSION CONSTRUCTOR WHAT THIS CONSTR DO MORE
    public User() {
        checkForUpdates();
    }

    /**
     * Method to check if {@code Glider} have to be updated to the last version <br>
     * Any params required
     *
     * @apiNote the data to do this check are stored in a {@code Firebase}'s database , but no-any more
     * data are stored on that database
     */
    private void checkForUpdates() {
        getInstance().getReference().child("check_node").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                IS_PATCH = snapshot.child("is_patch").getValue(Boolean.class);
                LATEST_VERSION = snapshot.child("latest_version").getValue(String.class);
                try {
                    IS_UPDATED = LATEST_VERSION.equals(STARTER_ACTIVITY.getApplicationContext()
                            .getPackageManager().getPackageInfo(STARTER_ACTIVITY.getApplicationContext()
                                    .getPackageName(), 0).versionName);
                } catch (NameNotFoundException e) {
                    IS_UPDATED = false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
