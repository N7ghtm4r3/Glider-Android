package com.tecknobit.glider.ui.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.tecknobit.glider.R;
import com.tecknobit.glider.helpers.toImport.records.Password.PasswordKeys;
import com.tecknobit.glider.ui.fragments.parents.FormFragment;
import com.tecknobit.glider.ui.fragments.parents.GliderFragment;

import org.json.JSONException;
import org.json.JSONObject;

import static com.tecknobit.glider.helpers.local.Utils.getTextFromEdit;
import static com.tecknobit.glider.helpers.local.Utils.hideKeyboard;
import static com.tecknobit.glider.helpers.local.Utils.showSnackbar;

/**
 * The {@link InsertFragment} fragment is the section of the app where the password can be inserted
 * with the custom parameters inserted in the {@code GUI}
 *
 * @author Tecknobit - N7ghtm4r3
 * @see GliderFragment
 * @see FormFragment
 **/
public class InsertFragment extends FormFragment {

    /**
     * Required empty public constructor for the normal Android's workflow
     **/
    public InsertFragment() {
        textInputLayouts = new TextInputLayout[3];
        textInputEditTexts = new TextInputEditText[3];
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return viewContainer = inflater.inflate(R.layout.fragment_insert, container, false);
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
        instantiateInputs(view, new int[]{R.id.tailLayout, R.id.passwordLayout, R.id.scopesLayout},
                new int[]{R.id.tailInput, R.id.passwordInput, R.id.scopesInput});
        view.findViewById(R.id.insertBtn).setOnClickListener(v -> {
            hideKeyboard(v);
            enableEditTexts(false);
            JSONObject payload = getRequestPayload();
            if (payload != null) {
                // TODO: 19/12/2022 MAKE REQUEST THEN
                showSnackbar(v, R.string.password_inserted_successfully);
                clearViews();
            } else
                enableEditTexts(true);
        });
        startInputsListenWorkflow();
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    protected void loadInputMessagesLists() {
        inputsErrors.put(0, getString(R.string.tail_is_required));
        inputsErrors.put(1, getString(R.string.password_is_required));
        inputsHints.put(0, getString(R.string.tail_hint));
        inputsHints.put(1, getString(R.string.password_hint));
        inputsHints.put(2, getString(R.string.scopes_hint));
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    protected void startInputsListenWorkflow() {
        super.startInputsListenWorkflow();
        for (int j = 0; j < textInputLayouts.length; j++) {
            int finalJ = j;
            textInputLayouts[j].setStartIconOnClickListener(v -> {
                if (finalJ != 2) {
                    String required = getString(R.string.required);
                    if (textInputLayouts[finalJ].getHelperText().equals(required))
                        setHelperTextLayout(textInputLayouts[finalJ], inputsHints.get(finalJ));
                    else
                        setHelperTextLayout(textInputLayouts[finalJ], getString(R.string.required));
                } else {
                    String defHint = getString(R.string.scopes_must_be_divided_by);
                    if (textInputLayouts[finalJ].getHelperText().equals(defHint))
                        textInputLayouts[finalJ].setHelperText(inputsHints.get(finalJ));
                    else
                        textInputLayouts[finalJ].setHelperText(defHint);
                }
            });
            if (finalJ != 2) {
                textInputEditTexts[finalJ].addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (before > 0 && start + count == 0)
                            textInputLayouts[finalJ].setError(inputsErrors.get(finalJ));
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if (s.length() > 0)
                            textInputLayouts[finalJ].setError(null);
                    }
                });
            }
        }
    }

    /**
     * Method to create the payload for the password insertion request
     *
     * @param parameters: parameters to insert to invoke this method
     * @return insertion payload with the parameters inserted in the {@code GUI} as {@link JSONObject}
     * or null if an error occurred
     */
    @Override
    @SafeVarargs
    protected final <T> JSONObject getRequestPayload(T... parameters) {
        try {
            String tail = getTextFromEdit(textInputEditTexts[0]);
            if (!tail.isEmpty()) {
                JSONObject payload = new JSONObject().put(PasswordKeys.tail.name(), tail);
                String password = getTextFromEdit(textInputEditTexts[1]);
                if (!password.isEmpty())
                    return payload.put(PasswordKeys.password.name(), password);
                else {
                    showsError(1);
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
        String errorMsg = inputsErrors.get(index);
        showSnackbar(viewContainer, errorMsg);
        textInputLayouts[index].setError(errorMsg);
    }

}