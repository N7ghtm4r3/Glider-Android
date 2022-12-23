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
import com.tecknobit.glider.ui.fragments.parents.FormFragment;
import com.tecknobit.glider.ui.fragments.parents.GliderFragment;

import org.json.JSONException;
import org.json.JSONObject;

import static com.tecknobit.glider.helpers.local.Utils.PASSWORD_KEY;
import static com.tecknobit.glider.helpers.local.Utils.TAIL_KEY;
import static com.tecknobit.glider.helpers.local.Utils.getTextFromEdit;
import static com.tecknobit.glider.helpers.local.Utils.hideKeyBoard;
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
        textInputLayouts = new TextInputLayout[2];
        textInputEditTexts = new TextInputEditText[2];
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
        loadInputMessagesLists();
        instantiateInputs(view, new int[]{R.id.tailLayout, R.id.passwordLayout},
                new int[]{R.id.tailInput, R.id.passwordInput});
        view.findViewById(R.id.insertBtn).setOnClickListener(v -> {
            hideKeyBoard(v);
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
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    protected void startInputsListenWorkflow() {
        for (int j = 0; j < textInputLayouts.length; j++) {
            int finalJ = j;
            textInputLayouts[j].setStartIconOnClickListener(v -> {
                String required = getString(R.string.required);
                if (textInputLayouts[finalJ].getHelperText().equals(required))
                    setHelperTextLayout(textInputLayouts[finalJ], inputsHints.get(finalJ));
                else
                    setHelperTextLayout(textInputLayouts[finalJ], getString(R.string.required));
            });
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
        JSONObject payload = null;
        try {
            String tail = getTextFromEdit(textInputEditTexts[0]);
            if (!tail.isEmpty()) {
                payload = new JSONObject()
                        .put(TAIL_KEY, tail);
                String password = getTextFromEdit(textInputEditTexts[1]);
                if (!password.isEmpty())
                    payload.put(PASSWORD_KEY, password);
                else {
                    showsError(1);
                    payload = null;
                }
            } else
                showsError(0);
        } catch (JSONException e) {
            payload = null;
        }
        return payload;
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