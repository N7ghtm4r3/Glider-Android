package com.tecknobit.glider.helpers.local;

import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.tecknobit.apimanager.apis.SocketManager;
import com.tecknobit.glider.helpers.toImport.records.Device;
import com.tecknobit.glider.helpers.toImport.records.Password;
import com.tecknobit.glider.helpers.toImport.records.Session;
import com.tecknobit.glider.ui.fragments.AccountFragment;
import com.tecknobit.glider.ui.fragments.ListFragment;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import static android.content.Context.MODE_PRIVATE;
import static android.content.pm.PackageManager.NameNotFoundException;
import static com.google.firebase.database.FirebaseDatabase.getInstance;
import static com.tecknobit.apimanager.apis.SocketManager.StandardResponseCode.FAILED;
import static com.tecknobit.glider.helpers.local.User.GliderKeys.databasePath;
import static com.tecknobit.glider.helpers.local.User.GliderKeys.statusCode;
import static com.tecknobit.glider.helpers.toImport.records.Session.SessionKeys.qrCodeLogin;
import static com.tecknobit.glider.helpers.toImport.records.Session.SessionKeys.session;
import static com.tecknobit.glider.ui.activities.SplashScreen.STARTER_ACTIVITY;

/**
 * The {@code User} class is a useful to manage the user session during the app workflow
 *
 * @author Tecknobit - N7ghtm4r3
 * @see Session
 **/
public class User extends Session {

    // TODO: 18/01/2023 TO REMOVE WHEN IMPLEMENTED
    /**
     * {@code passwords} list for the {@link ListFragment} with {@link Password.Status} as key and
     * {@link Password} as value.
     **/
    public static final HashMap<Password.Status, ArrayList<Password>> passwords = new HashMap<>();
    /**
     * {@code devices} list of {@link Device} for the {@link AccountFragment}
     **/
    public static final ArrayList<Device> devices = new ArrayList<>();
    /**
     * {@code userShared} instance to manage the user data in app
     */
    private static final SharedPreferences userShared = STARTER_ACTIVITY.getSharedPreferences(databasePath.name(),
            MODE_PRIVATE);
    /**
     * {@code socketManager} instance to manage the user communication with the backend
     */
    public static SocketManager socketManager;

    /**
     * Constructor to init {@link User} object <br>
     * Any params required
     * @apiNote this constructor will be invoked during the normal Glider's workflow
     **/
    public User() {
        super(userShared.getString(SessionKeys.token.name(), null),
                userShared.getString(SessionKeys.ivSpec.name(), null),
                userShared.getString(SessionKeys.secretKey.name(), null), null,
                userShared.getString(SessionKeys.hostAddress.name(), null),
                userShared.getInt(SessionKeys.hostPort.name(), -1),
                userShared.getBoolean(SessionKeys.singleUseMode.name(), true),
                userShared.getBoolean(qrCodeLogin.name(), false),
                userShared.getBoolean(SessionKeys.runInLocalhost.name(), true));
        if (hostAddress != null)
            socketManager = new SocketManager(hostAddress, hostPort);
        checkForUpdates();
    }

    /**
     * {@code user} instance to manage the user session in app
     */
    public static volatile User user;

    /**
     * Method to save a new session data
     * @param sessionData: data of the session to save
     * @throws Exception when an error occurred
     */
    // TODO: 18/01/2023 CLEAR USELESS DATA TO SAVE
    //  PASS CORRECT DEVICE NAME
    //  CREATE A DEFAULT PAYLOAD WITH DEVICE NAME AND OPE TO TO
    public void saveSessionData(JSONObject sessionData) throws Exception {
        if (!sessionData.getString(statusCode.name()).equals(FAILED.name())) {
            JSONObject jSession = sessionData.getJSONObject(session.name());
            for (Iterator<String> data = jSession.keys(); data.hasNext(); ) {
                String key = data.next();
                Object iData = jSession.get(key);
                if (iData instanceof String)
                    userShared.edit().putString(key, (String) iData).apply();
                else if (iData instanceof Integer)
                    userShared.edit().putInt(key, (Integer) iData).apply();
                else
                    userShared.edit().putBoolean(key, (Boolean) iData).apply();
            }
            refreshUser();
        } else
            throw new Exception();
    }

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

    /**
     * Method to refresh the {@link User}' session <br>
     * Any params required
     */
    public void refreshUser() {
        user = new User();
    }

    /**
     * {@code Operation} list of available operations
     */
    public enum Operation {

        /**
         * {@code GET_PUBLIC_KEYS} get public keys operation
         */
        GET_PUBLIC_KEYS,

        /**
         * {@code CONNECT} connect operation
         */
        CONNECT,

        /**
         * {@code REFRESH_DATA} refresh data operation
         */
        REFRESH_DATA,

        /**
         * {@code CREATE_PASSWORD} create password operation
         */
        CREATE_PASSWORD,

        /**
         * {@code INSERT_PASSWORD} insert password operation
         */
        INSERT_PASSWORD,

        /**
         * {@code DELETE_PASSWORD} delete password operation
         */
        DELETE_PASSWORD,

        /**
         * {@code RECOVER_PASSWORD} recovery password operation
         */
        RECOVER_PASSWORD,

        /**
         * {@code ADD_SCOPE} add scope operation
         */
        ADD_SCOPE,

        /**
         * {@code EDIT_SCOPE} edit scope operation
         */
        EDIT_SCOPE,

        /**
         * {@code REMOVE_SCOPE} remove scope operation
         */
        REMOVE_SCOPE,

        /**
         * {@code DISCONNECT} disconnect operation
         */
        DISCONNECT,

        /**
         * {@code MANAGE_DEVICE_AUTHORIZATION} blacklist / unblacklist device operation
         */
        MANAGE_DEVICE_AUTHORIZATION,

        /**
         * {@code DELETE_ACCOUNT} account deletion operation
         */
        DELETE_ACCOUNT,

    }

    /**
     * {@code GliderKeys} list of available keys
     */
    public enum GliderKeys {

        /**
         * {@code "ope"} key
         */
        ope,

        /**
         * {@code "statusCode"} key
         */
        statusCode,

        /**
         * {@code "serverStatus"} key
         */
        serverStatus,

        /**
         * {@code "databasePath"} key
         */
        databasePath

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
