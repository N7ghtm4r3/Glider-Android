package com.tecknobit.glider.helpers.local;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.tecknobit.apimanager.apis.SocketManager;
import com.tecknobit.apimanager.apis.SocketManager.StandardResponseCode;
import com.tecknobit.apimanager.formatters.JsonHelper;
import com.tecknobit.glider.helpers.DatabaseManager.Table;
import com.tecknobit.glider.records.Device;
import com.tecknobit.glider.records.Device.DeviceKeys;
import com.tecknobit.glider.records.Device.DevicePermission;
import com.tecknobit.glider.records.Password;
import com.tecknobit.glider.records.Password.Status;
import com.tecknobit.glider.records.Session;
import com.tecknobit.glider.ui.activities.SplashScreen;
import com.tecknobit.glider.ui.fragments.AccountFragment;
import com.tecknobit.glider.ui.fragments.ListFragment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import static android.content.Context.MODE_PRIVATE;
import static android.content.pm.PackageManager.NameNotFoundException;
import static android.os.Build.DEVICE;
import static android.os.Build.HOST;
import static android.os.Build.MANUFACTURER;
import static com.google.firebase.database.FirebaseDatabase.getInstance;
import static com.tecknobit.apimanager.apis.SocketManager.StandardResponseCode.FAILED;
import static com.tecknobit.apimanager.apis.SocketManager.StandardResponseCode.GENERIC_RESPONSE;
import static com.tecknobit.apimanager.apis.SocketManager.StandardResponseCode.valueOf;
import static com.tecknobit.apimanager.apis.SocketManager.pingHost;
import static com.tecknobit.apimanager.apis.encryption.aes.ClientCipher.Algorithm.CBC_ALGORITHM;
import static com.tecknobit.glider.helpers.GliderLauncher.GliderKeys.databasePath;
import static com.tecknobit.glider.helpers.GliderLauncher.GliderKeys.ope;
import static com.tecknobit.glider.helpers.GliderLauncher.GliderKeys.statusCode;
import static com.tecknobit.glider.helpers.GliderLauncher.Operation.REFRESH_DATA;
import static com.tecknobit.glider.records.Device.DeviceKeys.name;
import static com.tecknobit.glider.records.Device.DeviceKeys.type;
import static com.tecknobit.glider.records.Device.DevicePermission.ACCOUNT_MANAGER;
import static com.tecknobit.glider.records.Device.DevicePermission.ADMIN;
import static com.tecknobit.glider.records.Device.DevicePermission.PASSWORD_MANAGER;
import static com.tecknobit.glider.records.Device.Type.MOBILE;
import static com.tecknobit.glider.records.Password.PasswordKeys.status;
import static com.tecknobit.glider.records.Password.Status.ACTIVE;
import static com.tecknobit.glider.records.Password.Status.DELETED;
import static com.tecknobit.glider.records.Session.SessionKeys.session;
import static com.tecknobit.glider.ui.activities.SplashScreen.STARTER_ACTIVITY;

/**
 * The {@code User} class is a useful to manage the user session during the app workflow
 *
 * @author Tecknobit - N7ghtm4r3
 * @see Session
 */
public class User extends Session {

    /**
     * {@code DEVICE_NAME} name of the device
     */
    public static final String DEVICE_NAME = MANUFACTURER + "-" + DEVICE + " " + HOST;

    /**
     * {@code passwords} list for the {@link ListFragment} with {@link Status} as key and
     * {@link Password} as value.
     */
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
     * {@code refreshData} whether refresh the data
     */
    public static volatile boolean refreshData = true;

    /**
     * {@code permission} the current user permission
     */
    public static volatile DevicePermission permission;

    /**
     * {@code devices} list of {@link Device} for the {@link AccountFragment}
     */
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
     * {@code handler} allows to manage the {@link #runnable}'s workflow
     */
    private static final Handler handler = new Handler();

    /**
     * {@code runnable} allows to manage the data refreshing workflow
     */
    private Runnable runnable;

    /**
     * {@code language} user language selected by the user
     */
    private String language;

    /**
     * Constructor to init {@link User} object <br>
     * No-any params required
     *
     * @apiNote this constructor will be invoked during the normal Glider's workflow
     */
    public User() {
        super(userShared.getString(SessionKeys.token.name(), null),
                userShared.getString(SessionKeys.ivSpec.name(), null),
                userShared.getString(SessionKeys.secretKey.name(), null),
                userShared.getString(SessionKeys.sessionPassword.name(), null),
                userShared.getString(SessionKeys.hostAddress.name(), null),
                userShared.getInt(SessionKeys.hostPort.name(), -1),
                userShared.getBoolean(SessionKeys.singleUseMode.name(), true),
                userShared.getBoolean(SessionKeys.QRCodeLoginEnabled.name(), false),
                userShared.getBoolean(SessionKeys.runInLocalhost.name(), true));
        language = userShared.getString("language", "en");
        if (hostAddress != null) {
            if (secretKey != null) {
                try {
                    if (pingHost(hostAddress, hostPort, 2000)) {
                        socketManager = new SocketManager(hostAddress, hostPort, ivSpec, secretKey,
                                CBC_ALGORITHM);
                        refreshData();
                    } else
                        resetSession(FAILED);
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
     * No-any params required
     */
    public void refreshUser() {
        user = new User();
    }

    /**
     * Method to clear the {@link User}' session <br>
     * No-any params required
     */
    public void clearUserData() {
        userShared.edit().clear().apply();
        passwords.clear();
        devices.clear();
        refreshData = false;
        if (runnable != null) {
            handler.removeCallbacks(runnable);
            runnable = null;
        }
        refreshUser();
    }

    /**
     * Method to check if {@code Glider} have to be updated to the last version <br>
     * No-any params required
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
     * No-any params required
     */
    public void refreshData() {
        if (runnable != null)
            handler.removeCallbacks(runnable);
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
            runnable = () -> {
                try {
                    if (refreshData) {
                        if (socketManager.pingHost(2000)) {
                            socketManager.writeContent(payload);
                            JSONObject newData = new JSONObject(socketManager.readContent());
                            permission = DevicePermission.valueOf(newData.getString(DeviceKeys.permission.name()));
                            String sNewData = newData.toString();
                            switch (valueOf(newData.getString(statusCode.name()))) {
                                case SUCCESSFUL -> {
                                    if (!sNewData.equals(actualData[0].toString())) {
                                        hNewData.setJSONObjectSource(newData);
                                        JSONArray jDevices = hNewData.getJSONArray(Table.devices.name(),
                                                new JSONArray());
                                        String sDevices = jDevices.toString();
                                        if (!sDevices.equals(actualList[0].toString())) {
                                            devices.clear();
                                            for (int j = 0; j < jDevices.length(); j++) {
                                                JSONObject jDevice = jDevices.getJSONObject(j);
                                                if (!jDevice.getString(name.name()).equals(DEVICE_NAME))
                                                    devices.add(new Device(jDevice));
                                            }
                                            actualList[0] = new JSONArray(sDevices);
                                        }
                                        JSONArray jPasswords = hNewData.getJSONArray(Table.passwords.name(),
                                                new JSONArray());
                                        String sPasswords = jPasswords.toString();
                                        if (!sPasswords.equals(actualList[1].toString())) {
                                            passwords.clear();
                                            activePasswords.clear();
                                            deletedPasswords.clear();
                                            for (int j = 0; j < jPasswords.length(); j++) {
                                                JSONObject jPassword = jPasswords.getJSONObject(j);
                                                Status pStatus = Status.valueOf(jPassword.getString(status.name()));
                                                switch (pStatus) {
                                                    case ACTIVE ->
                                                            activePasswords.add(new Password(jPassword));
                                                    case DELETED ->
                                                            deletedPasswords.add(new Password(jPassword));
                                                }
                                            }
                                            passwords.put(ACTIVE, activePasswords);
                                            passwords.put(DELETED, deletedPasswords);
                                            actualList[1] = new JSONArray(sPasswords);
                                        }
                                        actualData[0] = new JSONObject(sNewData);
                                    }
                                }
                                case GENERIC_RESPONSE -> resetSession(GENERIC_RESPONSE);
                                default -> resetSession(FAILED);
                            }
                            handler.postDelayed(runnable, 5000);
                        } else
                            resetSession(FAILED);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            };
            runnable.run();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Method to reset the {@link User}'s session
     *
     * @param code: code of termination
     */
    private void resetSession(StandardResponseCode code) {
        clearUserData();
        STARTER_ACTIVITY.startActivity(new Intent(STARTER_ACTIVITY, SplashScreen.class)
                .putExtra(statusCode.name(), code.name()));
    }

    /**
     * Method to get {@link #language} instance <br>
     * No-any params required
     *
     * @return {@link #language} instance as {@link String}
     */
    public String getLanguage() {
        return language;
    }

    /**
     * Method to get {@link #language} instance in ISO format <br>
     * No-any params required
     *
     * @return {@link #language} in ISO format instance as {@link String}
     */
    public String getISOLanguage() {
        switch (language) {
            case "ITA" -> {
                return "it";
            }
            case "FRA" -> {
                return "fr";
            }
            case "ESP" -> {
                return "es";
            }
            default -> {
                return "en";
            }
        }
    }

    /**
     * Method to set and save in the {@link SharedPreferences} the {@link #language} instance
     *
     * @param language: the language to set and save
     */
    public void setLanguage(String language) {
        userShared.edit().putString("language", language).apply();
        this.language = language;
    }

    /**
     * Method to check whether a device has the {@link DevicePermission#ADMIN} or
     * {@link DevicePermission#PASSWORD_MANAGER} permissions <br>
     * No-any params required
     *
     * @return whether a device has the right permission
     */
    public boolean isPasswordManager() {
        return permission == PASSWORD_MANAGER || isAdmin();
    }

    /**
     * Method to check whether a device has the {@link DevicePermission#ADMIN} or
     * {@link DevicePermission#ACCOUNT_MANAGER} permissions <br>
     * No-any params required
     *
     * @return whether a device has the right permission
     */
    public boolean isAccountManager() {
        return permission == ACCOUNT_MANAGER || isAdmin();
    }

    /**
     * Method to check whether a device has the {@link DevicePermission#ADMIN} permission <br>
     * No-any params required
     *
     * @return whether a device has the right permission
     */
    public boolean isAdmin() {
        return permission == ADMIN;
    }

}
