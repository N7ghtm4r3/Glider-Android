package com.tecknobit.glider.helpers;

import android.content.ClipboardManager;
import android.graphics.Color;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.tecknobit.glider.R;

import static android.content.ClipData.newPlainText;
import static android.content.Context.CLIPBOARD_SERVICE;
import static android.content.Context.INPUT_METHOD_SERVICE;
import static com.tecknobit.glider.ui.activities.SplashScreen.STARTER_ACTIVITY;

public class Utils {

    // TODO: 17/12/2022 USE FROM THE GLIDER LIBRARY
    public static final String TAIL_KEY = "tail";
    public static final String SCOPES_KEY = "scopes";
    public static final String PASSWORD_LENGTH_KEY = "length";
    public static final int PASSWORD_MAX_LENGTH = 32;
    public static final int PASSWORD_MIN_LENGTH = 8;
    public static final String COLOR_PRIMARY_HEX = "#1E1E8D";
    public static final int COLOR_PRIMARY = Color.parseColor(COLOR_PRIMARY_HEX);


    public static void hideKeyBoard(View view) {
        ((InputMethodManager) STARTER_ACTIVITY.getSystemService(INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void copyText(MaterialTextView textView, View view) {
        ClipboardManager clipboard = (ClipboardManager) STARTER_ACTIVITY.getSystemService(CLIPBOARD_SERVICE);
        clipboard.setPrimaryClip(newPlainText("Glider-Password", textView.getText().toString()));
        showSnackbar(view, R.string.password_successfully_copied);
    }

    public static void showSnackbar(View view, int msg) {
        showSnackbar(view, STARTER_ACTIVITY.getString(msg));
    }

    public static void showSnackbar(View view, String msg) {
        Snackbar.make(view, msg, Snackbar.LENGTH_SHORT)
                .setBackgroundTint(Color.parseColor("#FAEDE1E1"))
                .setTextColor(COLOR_PRIMARY)
                .show();
    }

    public static void loadViews(View container, View[] views, int[] ids) {
        for (int j = 0; j < views.length; j++)
            views[j] = container.findViewById(ids[j]);
    }

    public static String getTextFromEdit(TextInputEditText editText) {
        return editText.getText().toString();
    }

}
