package com.tecknobit.glider.helpers.local;

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
    public static String SECRET_KEY = "null";

    /**
     * {@code IS_UPDATED} whether the current version of the app is the latest available
     */
    public static boolean IS_UPDATED = false;

    /**
     * {@code IS_PATCH} whether the update is a simple patch (it can be skipped) or is a mandatory
     * version update
     */
    public static boolean IS_PATCH = false;

    /**
     * {@code UPDATE_VERSION} latest version available for {@code Glider}
     */
    public static String UPDATE_VERSION = "1.0.2";

}
