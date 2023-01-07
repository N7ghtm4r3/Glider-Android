package com.tecknobit.glider.ui.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
import com.tecknobit.glider.R;
import com.tecknobit.glider.ui.activities.MainActivity;
import com.tecknobit.glider.ui.fragments.parents.FormFragment;
import com.tecknobit.glider.ui.fragments.parents.GliderFragment;

import org.json.JSONException;
import org.json.JSONObject;

import static android.graphics.Color.TRANSPARENT;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static com.tecknobit.glider.R.string.host_hint;
import static com.tecknobit.glider.R.string.host_is_required;
import static com.tecknobit.glider.R.string.host_port_hint;
import static com.tecknobit.glider.R.string.host_port_is_required;
import static com.tecknobit.glider.R.string.ope_failed;
import static com.tecknobit.glider.R.string.password_connect_hint;
import static com.tecknobit.glider.R.string.password_is_required;
import static com.tecknobit.glider.R.string.qrcode_reading_error;
import static com.tecknobit.glider.helpers.local.Utils.COLOR_PRIMARY;
import static com.tecknobit.glider.helpers.local.Utils.COLOR_RED;
import static com.tecknobit.glider.helpers.local.Utils.HOST_ADDRESS_KEY;
import static com.tecknobit.glider.helpers.local.Utils.HOST_PORT_KEY;
import static com.tecknobit.glider.helpers.local.Utils.PASSWORD_KEY;
import static com.tecknobit.glider.helpers.local.Utils.getTextFromEdit;
import static com.tecknobit.glider.helpers.local.Utils.hideKeyboard;
import static com.tecknobit.glider.helpers.local.Utils.openUrlPage;
import static com.tecknobit.glider.helpers.local.Utils.showSnackbar;
import static com.tecknobit.glider.ui.activities.SplashScreen.STARTER_ACTIVITY;
import static java.lang.Integer.parseInt;

/**
 * The {@link Connect} fragment is the section of the app where the user can be connect with own
 * backend service with the parameters inserted in the {@code GUI}
 *
 * @author Tecknobit - N7ghtm4r3
 * @see GliderFragment
 * @see FormFragment
 * @see OnClickListener
 **/
public class Connect extends FormFragment implements OnClickListener {

    /**
     * {@code barcodeLauncher} instance to launch a barcode scanning
     **/
    private final ActivityResultLauncher<ScanOptions> barcodeLauncher;

    /**
     * Required empty public constructor for the normal Android's workflow
     **/
    public Connect() {
        textInputLayouts = new TextInputLayout[3];
        textInputEditTexts = new TextInputEditText[3];
        barcodeLauncher = registerForActivityResult(new ScanContract(), result -> {
            String contents = result.getContents();
            if (contents == null)
                showSnackbar(viewContainer, getString(qrcode_reading_error));
            else {
                Dialog dialog = new Dialog(STARTER_ACTIVITY);
                dialog.setContentView(R.layout.connect_dialog);
                Window window = dialog.getWindow();
                window.setBackgroundDrawable(new ColorDrawable(TRANSPARENT));
                window.setLayout(MATCH_PARENT, WRAP_CONTENT);
                TextInputLayout lPassword = dialog.findViewById(R.id.passwordLayout);
                TextInputEditText password = dialog.findViewById(R.id.passwordInput);
                setStartIconActions(lPassword, 1);
                password.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (before > 0 && start + count == 0)
                            lPassword.setError(inputsErrors.get(1));
                        else
                            lPassword.setError(null);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if (s.length() > 0)
                            lPassword.setError(null);
                    }
                });
                dialog.findViewById(R.id.dismiss).setOnClickListener(v -> dialog.dismiss());
                dialog.findViewById(R.id.connect).setOnClickListener(v -> {
                    try {
                        JSONObject credentials = new JSONObject(contents);
                        textInputEditTexts[0].setText(credentials.getString(HOST_ADDRESS_KEY));
                        textInputEditTexts[1].setText(getTextFromEdit(password));
                        textInputEditTexts[2].setText(credentials.getString(HOST_PORT_KEY));
                        JSONObject payload = getRequestPayload();
                        if (payload != null) {
                            // TODO: 07/01/2023 REQUEST THEN
                            startActivity(new Intent(STARTER_ACTIVITY, MainActivity.class));
                        } else
                            dialog.dismiss();
                    } catch (JSONException e) {
                        dialog.dismiss();
                        showSnackbar(viewContainer, ope_failed);
                    }
                });
                dialog.show();
            }
        });
    }

    /**
     * Called to have the fragment instantiate its user interface view.
     * This is optional, and non-graphical fragments can return null. This will be called between
     * {@link #onCreate(Bundle)} and {@link #onViewCreated(View, Bundle)}.
     * <p>A default View can be returned by calling {@link Fragment (int)} in your
     * constructor. Otherwise, this method returns null.
     *
     * <p>It is recommended to <strong>only</strong> inflate the layout in this method and move
     * logic that operates on the returned View to {@link #onViewCreated(View, Bundle)}.
     *
     * <p>If you return a View from here, you will later be called in
     * {@link #onDestroyView} when the view is being released.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate
     *                           any views in the fragment,
     * @param container          If non-null, this is the parent view that the fragment's
     *                           UI should be attached to.  The fragment should not add the view itself,
     *                           but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     *                           from a previous saved state as given here.
     * @return Return the View for the fragment's UI, or null.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return viewContainer = inflater.inflate(R.layout.fragment_connect, container, false);
    }

    /**
     * Called immediately after {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}
     * has returned, but before any saved state has been restored in to the view.
     * This gives subclasses a chance to initialize themselves once
     * they know their view hierarchy has been completely created.  The fragment's
     * view hierarchy is not however attached to its parent at this point.
     *
     * @param view               The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     *                           from a previous saved state as given here.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        instantiateInputs(view, new int[]{R.id.hostLayout, R.id.passwordLayout, R.id.portLayout},
                new int[]{R.id.hostInput, R.id.passwordInput, R.id.portInput});
        startInputsListenWorkflow();
        for (int id : new int[]{R.id.scanner, R.id.connectBtn, R.id.gitLink, R.id.gitIcon})
            view.findViewById(id).setOnClickListener(this);
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    protected void loadInputMessagesLists() {
        inputsErrors.put(0, getString(host_is_required));
        inputsErrors.put(1, getString(password_is_required));
        inputsErrors.put(2, getString(host_port_is_required));
        inputsHints.put(0, getString(host_hint));
        inputsHints.put(1, getString(password_connect_hint));
        inputsHints.put(2, getString(host_port_hint));
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    protected void startInputsListenWorkflow() {
        super.startInputsListenWorkflow();
        for (int j = 0; j < textInputLayouts.length; j++) {
            int finalJ = j;
            setStartIconActions(textInputLayouts[j], j);
            textInputEditTexts[finalJ].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    String host = getTextFromEdit(textInputEditTexts[0]);
                    String port = getTextFromEdit(textInputEditTexts[2]);
                    if (before > 0 && start + count == 0)
                        textInputLayouts[finalJ].setError(inputsErrors.get(finalJ));
                    if (finalJ == 0) {
                        if (!host.isEmpty()) {
                            int k = 0;
                            for (int j = 0; j < host.length(); j++)
                                if (host.charAt(j) == ':')
                                    k++;
                            if (k == 2) {
                                host = host.replace("://", "-");
                                if (!host.endsWith(":")) {
                                    String aPort = host.split(":")[1];
                                    if (!port.equals(aPort))
                                        textInputEditTexts[2].setText(aPort);
                                }
                            }
                        }
                    } else if (finalJ == 2 && !host.isEmpty()) {
                        if (!port.isEmpty() && !host.endsWith(port)) {
                            host = host.replace("://", "_");
                            if (host.contains(":"))
                                host = host.replace(host.split(":")[1], port);
                            else
                                host = host + ":" + port;
                            textInputEditTexts[0].setText(host.replace("_", "://"));
                        }
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.length() > 0)
                        textInputLayouts[finalJ].setError(null);
                }
            });
        }
    }

    /**
     * Method to set the start icon actions of {@link TextInputLayout} when pressed
     *
     * @param layout: layout where set the start icon actions
     * @param index:  index of the hint to show
     */
    private void setStartIconActions(TextInputLayout layout, int index) {
        layout.setStartIconOnClickListener(v -> {
            String required = getString(R.string.required);
            hideKeyboard(viewContainer);
            if (layout.getHelperText().equals(required)) {
                layout.setHelperText(inputsHints.get(index));
                layout.setHelperTextColor(ColorStateList.valueOf(COLOR_PRIMARY));
            } else {
                layout.setHelperText(required);
                layout.setHelperTextColor(ColorStateList.valueOf(COLOR_RED));
            }
        });
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    @SuppressLint("NonConstantResourceId")
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.scanner -> {
                ScanOptions scanOptions = new ScanOptions();
                scanOptions.setBeepEnabled(false);
                scanOptions.setOrientationLocked(false);
                scanOptions.setPrompt(getString(R.string.qrcode_prompt));
                barcodeLauncher.launch(scanOptions);
            }
            case R.id.connectBtn -> {
                hideKeyboard(v);
                JSONObject payload = getRequestPayload();
                if (payload != null) {
                    // TODO: 24/12/2022 REQUEST THEN
                    startActivity(new Intent(STARTER_ACTIVITY, MainActivity.class));
                }
            }
            case R.id.gitLink, R.id.gitIcon -> // TODO: 24/12/2022 CHECK IF LINK IS GLIDER OR GLIDER-ANDROID
                    openUrlPage("https://github.com/N7ghtm4r3/Glider-Android");
        }
    }

    /**
     * Method to create the payload for the connection request
     *
     * @param parameters: parameters to insert to invoke this method
     * @return connection payload with the parameters inserted in the {@code GUI} as {@link JSONObject}
     * or null if an error occurred
     */
    @Override
    @SafeVarargs
    protected final <T> JSONObject getRequestPayload(T... parameters) {
        String host = getTextFromEdit(textInputEditTexts[0]);
        try {
            if (!host.isEmpty()) {
                JSONObject payload = new JSONObject().put(HOST_ADDRESS_KEY, host);
                String password = getTextFromEdit(textInputEditTexts[1]);
                if (!password.isEmpty())
                    payload.put(PASSWORD_KEY, password);
                else {
                    showsError(1);
                    return null;
                }
                try {
                    return payload.put(HOST_PORT_KEY, parseInt(getTextFromEdit(textInputEditTexts[2])));
                } catch (NumberFormatException e) {
                    showsError(2);
                    return null;
                }
            } else {
                showsError(0);
                return null;
            }
        } catch (JSONException e) {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    protected void showsError(int index) {
        String error = inputsErrors.get(index);
        textInputLayouts[index].setError(error);
        showSnackbar(viewContainer, error);
    }

}