package com.tecknobit.glider.helpers.local;

import android.app.Activity;
import android.content.ClipboardManager;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog.Builder;

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
 **/
public class Utils {

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
    public static void hideKeyboard(View view) {
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

    /**
     * Method to open a page link from {@code Glider}
     *
     * @param url: url of the page to open
     */
    public static void openUrlPage(String url) {
        STARTER_ACTIVITY.startActivity(new Intent(Intent.ACTION_VIEW)
                .setData(Uri.parse(url)));
    }

    /**
     * Method to create an {@link Builder}
     *
     * @param title:          title of the alert
     * @param message:        message of the alert
     * @param negativeText:   negative text
     * @param negativeAction: negative action to do
     * @param positiveText:   positive text
     * @param positiveAction: positive action to do
     * @param activity:       activity where is to show
     * @return an alert as {@link Builder}
     */
    @Wrapper
    public static Builder createAlertDialog(int title, int message, int negativeText,
                                            OnClickListener negativeAction, int positiveText,
                                            OnClickListener positiveAction, Activity activity) {
        return createAlertDialog(STARTER_ACTIVITY.getString(title),
                STARTER_ACTIVITY.getString(message), STARTER_ACTIVITY.getString(negativeText),
                negativeAction, STARTER_ACTIVITY.getString(positiveText), positiveAction, activity);
    }

    /**
     * Method to create an {@link Builder}
     *
     * @param title:          title of the alert
     * @param message:        message of the alert
     * @param negativeText:   negative text
     * @param negativeAction: negative action to do
     * @param positiveText:   positive text
     * @param positiveAction: positive action to do
     * @param activity:       activity where is to show
     * @return an alert as {@link Builder}
     */
    public static Builder createAlertDialog(String title, String message, String negativeText,
                                            OnClickListener negativeAction, String positiveText,
                                            OnClickListener positiveAction, Activity activity) {
        Builder alert = new Builder(activity);
        alert.setTitle(title);
        alert.setMessage(message);
        alert.setNegativeButton(negativeText, negativeAction);
        alert.setPositiveButton(positiveText, positiveAction);
        return alert;
    }

}
