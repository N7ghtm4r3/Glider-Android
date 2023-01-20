package com.tecknobit.glider.helpers.local;

import android.content.SharedPreferences;
import android.os.Build;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.tecknobit.apimanager.apis.SocketManager;
import com.tecknobit.apimanager.formatters.JsonHelper;
import com.tecknobit.glider.helpers.toImport.records.Device;
import com.tecknobit.glider.helpers.toImport.records.Password;
import com.tecknobit.glider.helpers.toImport.records.Password.Status;
import com.tecknobit.glider.helpers.toImport.records.Session;
import com.tecknobit.glider.ui.fragments.AccountFragment;
import com.tecknobit.glider.ui.fragments.ListFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static android.content.Context.MODE_PRIVATE;
import static android.content.pm.PackageManager.NameNotFoundException;
import static com.google.firebase.database.FirebaseDatabase.getInstance;
import static com.tecknobit.apimanager.apis.SocketManager.StandardResponseCode.FAILED;
import static com.tecknobit.apimanager.apis.SocketManager.StandardResponseCode.SUCCESSFUL;
import static com.tecknobit.apimanager.apis.encryption.aes.ClientCipher.Algorithm.CBC_ALGORITHM;
import static com.tecknobit.glider.helpers.local.User.GliderKeys.databasePath;
import static com.tecknobit.glider.helpers.local.User.GliderKeys.ope;
import static com.tecknobit.glider.helpers.local.User.GliderKeys.statusCode;
import static com.tecknobit.glider.helpers.local.User.Operation.REFRESH_DATA;
import static com.tecknobit.glider.helpers.toImport.records.Device.DeviceKeys.name;
import static com.tecknobit.glider.helpers.toImport.records.Device.DeviceKeys.type;
import static com.tecknobit.glider.helpers.toImport.records.Device.Type.MOBILE;
import static com.tecknobit.glider.helpers.toImport.records.Password.PasswordKeys.status;
import static com.tecknobit.glider.helpers.toImport.records.Password.Status.ACTIVE;
import static com.tecknobit.glider.helpers.toImport.records.Password.Status.DELETED;
import static com.tecknobit.glider.helpers.toImport.records.Session.SessionKeys.qrCodeLogin;
import static com.tecknobit.glider.helpers.toImport.records.Session.SessionKeys.session;
import static com.tecknobit.glider.ui.activities.SplashScreen.STARTER_ACTIVITY;
import static java.lang.Thread.sleep;

/**
 * The {@code User} class is a useful to manage the user session during the app workflow
 *
 * @author Tecknobit - N7ghtm4r3
 * @see Session
 **/
public class User extends Session {

    /**
     * {@code DEVICE_NAME} name of the device
     */
    public static final String DEVICE_NAME = Build.MANUFACTURER + "-" + Build.DEVICE;
    /**
     * {@code passwords} list for the {@link ListFragment} with {@link Status} as key and
     * {@link Password} as value.
     **/
    public static final HashMap<Status, ArrayList<Password>> passwords = new HashMap<>();
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
     * {@code user} instance to manage the user session in app
     */
    public static User user;

    /**
     * {@code executor} instance to manage the refresh of the user data
     */
    private static volatile ExecutorService executor;

    /**
     * Constructor to init {@link User} object <br>
     * Any params required
     *
     * @apiNote this constructor will be invoked during the normal Glider's workflow
     **/
    public User() {
        super(userShared.getString(SessionKeys.token.name(), null),
                userShared.getString(SessionKeys.ivSpec.name(), null),
                userShared.getString(SessionKeys.secretKey.name(), null),
                userShared.getString(SessionKeys.sessionPassword.name(), null),
                userShared.getString(SessionKeys.hostAddress.name(), null),
                userShared.getInt(SessionKeys.hostPort.name(), -1),
                userShared.getBoolean(SessionKeys.singleUseMode.name(), true),
                userShared.getBoolean(qrCodeLogin.name(), false),
                userShared.getBoolean(SessionKeys.runInLocalhost.name(), true));
        if (hostAddress != null) {
            if (secretKey != null) {
                try {
                    socketManager = new SocketManager(hostAddress, hostPort, ivSpec, secretKey, CBC_ALGORITHM);
                    refreshData();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } else
                socketManager = new SocketManager(hostAddress, hostPort);
        }
        checkForUpdates();
    }

    /**
     * Method to save a new session data
     * @param sessionData: data of the session to save
     * @throws Exception when an error occurred
     */
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
     * Method to refresh the {@link User}' session <br>
     * Any params required
     */
    public void refreshUser() {
        user = new User();
    }

    // TODO: 18/01/2023 TO REMOVE WHEN IMPLEMENTED

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

    /**
     * Method to refresh the {@code Glider} data <br>
     * Any params required
     */
    private void refreshData() {
        if (executor == null) {
            executor = Executors.newSingleThreadExecutor();
            try {
                final JSONObject payload = new JSONObject()
                        .put(name.name(), DEVICE_NAME)
                        .put(type.name(), MOBILE)
                        .put(SessionKeys.sessionPassword.name(), sessionPassword)
                        .put(ope.name(), REFRESH_DATA);
                final JSONObject[] actualData = {new JSONObject()};
                final JSONArray[] actualList = {new JSONArray(), new JSONArray()};
                final JsonHelper hNewData = new JsonHelper((JSONObject) null);
                final ArrayList<Password> activePasswords = new ArrayList<>();
                final ArrayList<Password> deletedPasswords = new ArrayList<>();
                executor.execute(() -> {
                    while (true) {
                        try {
                            socketManager.writeContent(payload);
                            JSONObject newData = new JSONObject(socketManager.readContent());
                            String sNewData = newData.toString();
                            if (newData.getString(statusCode.name()).equals(SUCCESSFUL.name()) &&
                                    !sNewData.equals(actualData[0].toString())) {
                                hNewData.setJSONObjectSource(newData);
                                JSONArray jDevices = hNewData.getJSONArray("devices", new JSONArray()); // TODO: 20/01/2023 USE THE RIGHT KEY
                                String sDevices = jDevices.toString();
                                if (sDevices.equals(actualList[1].toString())) {
                                    devices.clear();
                                    for (int j = 0; j < jDevices.length(); j++) {
                                        JSONObject jDevice = jDevices.getJSONObject(j);
                                        if (!jDevice.getString(name.name()).equals(DEVICE_NAME))
                                            devices.add(new Device(jDevice));
                                    }
                                    actualList[0] = new JSONArray(sDevices);
                                }
                                JSONArray jPasswords = hNewData.getJSONArray("passwords", new JSONArray()); // TODO: 20/01/2023 USE THE RIGHT KEY
                                String sPasswords = jPasswords.toString();
                                if (!sPasswords.equals(actualList[1].toString())) {
                                    passwords.clear();
                                    activePasswords.clear();
                                    deletedPasswords.clear();
                                    for (int j = 0; j < jPasswords.length(); j++) {
                                        JSONObject jPassword = jPasswords.getJSONObject(j);
                                        Status pStatus = Status.valueOf(jPassword.getString(status.name()));
                                        switch (pStatus) {
                                            case ACTIVE -> activePasswords.add(new Password(jPassword));
                                            case DELETED -> deletedPasswords.add(new Password(jPassword));
                                        }
                                    }
                                    passwords.put(ACTIVE, activePasswords);
                                    passwords.put(DELETED, deletedPasswords);
                                    actualList[1] = new JSONArray(sPasswords);
                                }
                                actualData[0] = new JSONObject(sNewData);
                            }
                            sleep(5000);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
