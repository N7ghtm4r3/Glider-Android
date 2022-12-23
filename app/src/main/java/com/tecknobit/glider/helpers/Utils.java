package com.tecknobit.glider.helpers;

import android.content.ClipboardManager;
import android.graphics.Color;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.tecknobit.apimanager.annotations.Wrapper;
import com.tecknobit.glider.R;

import static android.content.ClipData.newPlainText;
import static android.content.Context.CLIPBOARD_SERVICE;
import static android.content.Context.INPUT_METHOD_SERVICE;
import static com.tecknobit.glider.ui.activities.SplashScreen.STARTER_ACTIVITY;

/**
 * The {@code Utils} class is a container of static methods useful for the {@code Glider}'s workflow
 *
 * @author Tecknobit - N7ghtm4r3
 * *
 **/
public class Utils {

    // TODO: 17/12/2022 USE FROM THE GLIDER LIBRARY
    /**
     * {@code TAIL_KEY} tail key
     */
    public static final String TAIL_KEY = "tail";

    /**
     * {@code SCOPES_KEY} scopes key
     */
    public static final String SCOPES_KEY = "scopes";

    /**
     * {@code PASSWORD_LENGTH_KEY} length key
     */
    public static final String PASSWORD_LENGTH_KEY = "length";

    /**
     * {@code PASSWORD_KEY} password key
     */
    public static final String PASSWORD_KEY = "password";

    /**
     * {@code HOST_ADDRESS_KEY} host address key
     */
    public static final String HOST_ADDRESS_KEY = "host_address";

    /**
     * {@code HOST_PORT_KEY} host port key
     */
    public static final String HOST_PORT_KEY = "host_port";

    /**
     * {@code SERVER_STATUS_KEY} server status key
     */
    public static final String SERVER_STATUS_KEY = "server_status";

    /**
     * {@code SINGLE_USE_MODE_KEY} single use mode key
     */
    public static final String SINGLE_USE_MODE_KEY = "single_use_mode";

    /**
     * {@code QR_CODE_LOGIN_KEY} qr code login key
     */
    public static final String QR_CODE_LOGIN_KEY = "qr_code_login";

    /**
     * {@code PASSWORD_MAX_LENGTH} password max length
     */
    public static final int PASSWORD_MAX_LENGTH = 32;

    /**
     * {@code PASSWORD_MIN_LENGTH} password min length
     */
    public static final int PASSWORD_MIN_LENGTH = 8;

    /**
     * {@code COLOR_PRIMARY_HEX} the primary color value as hex {@link String}
     */
    public static final String COLOR_PRIMARY_HEX = "#1E1E8D";

    /**
     * {@code COLOR_PRIMARY} the primary color value as int
     */
    public static final int COLOR_PRIMARY = Color.parseColor(COLOR_PRIMARY_HEX);

    /**
     * {@code COLOR_RED_HEX} the red color value as hex {@link String}
     */
    public static final String COLOR_RED_HEX = "#A81515";

    /**
     * {@code COLOR_RED} the red color value as int
     */
    public static final int COLOR_RED = Color.parseColor(COLOR_RED_HEX);

    /**
     * Constructor to avoid the useless instantiation of {@link Utils} class
     */
    private Utils() {
    }

    /**
     * Method to hide the keyboard
     *
     * @param view: source view from hide the keyboard
     */
    public static void hideKeyBoard(View view) {
        ((InputMethodManager) STARTER_ACTIVITY.getSystemService(INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     * Method to copy a text in the {@link ClipboardManager}
     *
     * @param textView: the text view from copy the text
     * @param view:     source view from show the {@link Snackbar}
     */
    public static void copyText(TextView textView, View view) {
        ClipboardManager clipboard = (ClipboardManager) STARTER_ACTIVITY.getSystemService(CLIPBOARD_SERVICE);
        clipboard.setPrimaryClip(newPlainText("Glider-Password", textView.getText().toString()));
        showSnackbar(view, R.string.password_successfully_copied);
    }

    /**
     * Method to show a {@link Snackbar}
     *
     * @param view: the view from show the {@link Snackbar}
     * @param msg:  resource identifier of the message to show
     */
    @Wrapper
    public static void showSnackbar(View view, int msg) {
        showSnackbar(view, STARTER_ACTIVITY.getString(msg));
    }

    /**
     * Method to show a {@link Snackbar}
     *
     * @param view: the view from show the {@link Snackbar}
     * @param msg:  the message to show
     */
    public static void showSnackbar(View view, String msg) {
        Snackbar.make(view, msg, Snackbar.LENGTH_SHORT)
                .setBackgroundTint(Color.parseColor("#FAEDE1E1"))
                .setTextColor(COLOR_PRIMARY)
                .show();
    }

    /**
     * Method to instantiate an array of {@link View}
     *
     * @param container: the container view where the views are
     * @param views:     the views to instantiate
     * @param ids:       the views identifier
     */
    public static void instantiateViews(View container, View[] views, int[] ids) {
        for (int j = 0; j < views.length; j++)
            views[j] = container.findViewById(ids[j]);
    }

    /**
     * Method to get a text from an {@link TextInputEditText}
     *
     * @param editText: the edit text where get the text
     * @return the text contained in the {@link TextInputEditText} as {@link String}
     */
    public static String getTextFromEdit(TextInputEditText editText) {
        return editText.getText().toString();
    }

}
