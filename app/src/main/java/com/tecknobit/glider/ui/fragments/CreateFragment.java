package com.tecknobit.glider.ui.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;
import com.tecknobit.glider.R;
import com.tecknobit.glider.helpers.Utils;
import com.tecknobit.glider.ui.fragments.parents.FormFragment;
import com.tecknobit.glider.ui.fragments.parents.GliderFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import static com.tecknobit.glider.helpers.Utils.PASSWORD_LENGTH_KEY;
import static com.tecknobit.glider.helpers.Utils.PASSWORD_MAX_LENGTH;
import static com.tecknobit.glider.helpers.Utils.PASSWORD_MIN_LENGTH;
import static com.tecknobit.glider.helpers.Utils.SCOPES_KEY;
import static com.tecknobit.glider.helpers.Utils.TAIL_KEY;
import static com.tecknobit.glider.helpers.Utils.getTextFromEdit;
import static com.tecknobit.glider.helpers.Utils.hideKeyBoard;
import static com.tecknobit.glider.helpers.Utils.showSnackbar;

/**
 * The {@link CreateFragment} fragment is the section of the app where the password can be created
 * with the parameters inserted in the {@code GUI}
 *
 * @author Tecknobit - N7ghtm4r3
 * @see GliderFragment
 * @see FormFragment
 * @see OnClickListener
 **/
public class CreateFragment extends FormFragment implements OnClickListener {

    /**
     * {@code passwordCardView} card view where the password created will appear
     *
     * @apiNote clicking on this will be copied the passowrd with the
     * {@link Utils#copyText(TextView, View)}
     **/
    private MaterialCardView passwordCardView;

    /**
     * {@code passwordTextView} text view where the password created will appear
     * @apiNote clicking on this will be copied the passowrd with the
     * {@link Utils#copyText(TextView, View)}
     * **/
    private MaterialTextView passwordTextView;

    /**
     * {@code createBtn} button to make a request for the password creation
     * **/
    private MaterialButton createBtn;

    /**
     * Required empty public constructor for the normal Android's workflow
     * **/
    public CreateFragment() {
        textInputLayouts = new TextInputLayout[3];
        textInputEditTexts = new TextInputEditText[3];
    }

    /**
     * Called to have the fragment instantiate its user interface view.
     * This is optional, and non-graphical fragments can return null. This will be called between
     * {@link #onCreate(Bundle)} and {@link #onViewCreated(View, Bundle)}.
     * <p>A default View can be returned by calling {@link Fragment(int)} in your
     * constructor. Otherwise, this method returns null.
     *
     * <p>It is recommended to <strong>only</strong> inflate the layout in this method and move
     * logic that operates on the returned View to {@link #onViewCreated(View, Bundle)}.
     *
     * <p>If you return a View from here, you will later be called in
     * {@link #onDestroyView} when the view is being released.
     *
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return Return the View for the fragment's UI, or null.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return viewContainer = inflater.inflate(R.layout.fragment_create, container, false);
    }

    /**
     * Called immediately after {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}
     * has returned, but before any saved state has been restored in to the view.
     * This gives subclasses a chance to initialize themselves once
     * they know their view hierarchy has been completely created.  The fragment's
     * view hierarchy is not however attached to its parent at this point.
     * @param view The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadInputMessagesLists();
        instantiateInputs(view, new int[]{R.id.tailLayout, R.id.scopesLayout, R.id.lengthLayout},
                new int[]{R.id.tailInput, R.id.scopesInput, R.id.lengthInput});
        createBtn = view.findViewById(R.id.createBtn);
        createBtn.setOnClickListener(this);
        passwordCardView = view.findViewById(R.id.passwordCard);
        passwordCardView.setOnClickListener(this);
        passwordTextView = view.findViewById(R.id.passwordCreated);
        passwordTextView.setOnClickListener(this);
        startInputsListenWorkflow();
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    protected void loadInputMessagesLists() {
        inputsErrors.put(0, getString(R.string.tail_is_required));
        inputsErrors.put(1, getString(R.string.invalid_length));
        inputsErrors.put(2, getString(R.string.length_is_required));
        inputsHints.put(0, getString(R.string.tail_hint));
        inputsHints.put(1, getString(R.string.scopes_hint));
        inputsHints.put(2, getString(R.string.length_hint));
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    protected void startInputsListenWorkflow() {
        for (int j = 0; j < textInputEditTexts.length; j++) {
            int finalJ = j;
            textInputLayouts[j].setStartIconOnClickListener(v -> {
                if (finalJ != 1) {
                    String required = getString(R.string.required);
                    if (textInputLayouts[finalJ].getHelperText() != required)
                        setHelperTextLayout(textInputLayouts[finalJ], required);
                    else
                        setHelperTextLayout(textInputLayouts[finalJ], inputsHints.get(finalJ));
                } else {
                    String defScopes = getString(R.string.scopes_must_be_divided_by);
                    if (!textInputLayouts[finalJ].getHelperText().equals(defScopes))
                        textInputLayouts[finalJ].setHelperText(defScopes);
                    else
                        textInputLayouts[finalJ].setHelperText(inputsHints.get(finalJ));
                }
            });
            if (j != 1) {
                textInputEditTexts[j].addTextChangedListener(new TextWatcher() {
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
     * {@inheritDoc}
     */
    @Override
    @SuppressLint("NonConstantResourceId")
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.createBtn -> {
                if (createBtn.getText().equals(getString(R.string.create))) {
                    hideKeyBoard(v);
                    enableEditTexts(false);
                    JSONObject payload = getRequestPayload();
                    if (payload != null) {
                        // TODO: 17/12/2022 REQUEST THEN
                        showSnackbar(v, R.string.password_successfully_created);
                        passwordCardView.setVisibility(View.VISIBLE);
                        createBtn.setText(R.string.clear);
                    } else
                        enableEditTexts(true);
                } else
                    clearViews();
            }
            case R.id.passwordCard, R.id.passwordCreated -> {
                Utils.copyText(passwordTextView, v);
                clearViews();
            }
        }
    }

    /**
     * Method to create the payload for the password creation request
     *
     * @param parameters: parameters to insert to invoke this method
     * @return creation payload with the parameters inserted in the {@code GUI} as {@link JSONObject}
     * or null if an error occurred
     */
    @SafeVarargs
    protected final <T> JSONObject getRequestPayload(T... parameters) {
        JSONObject payload = null;
        try {
            String tail = getTextFromEdit(textInputEditTexts[0]);
            if (!tail.isEmpty()) {
                payload = new JSONObject()
                        .put(TAIL_KEY, tail)
                        .put(SCOPES_KEY, List.of(getTextFromEdit(textInputEditTexts[1]).split(",")));
                try {
                    int length = Integer.parseInt(getTextFromEdit(textInputEditTexts[2]));
                    if (length < PASSWORD_MIN_LENGTH || length > PASSWORD_MAX_LENGTH) {
                        showsError(1);
                        payload = null;
                    } else
                        payload.put(PASSWORD_LENGTH_KEY, length);
                } catch (NumberFormatException e) {
                    showsError(2);
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
        if (index == 1) {
            index++;
            showSnackbar(viewContainer, R.string.password_length_input_error);
        } else
            showSnackbar(viewContainer, errorMsg);
        textInputLayouts[index].setError(errorMsg);
    }

    /**
     * {@inheritDoc}
     *
     * @apiNote this method is called when:
     * <ul>
     *     <li>this fragment is not more the first showing</li>
     *     <li>an error is occurred during the password creation</li>
     *     <li>the normal conclusion of the password creation's routine</li>
     * </ul>
     */
    @Override
    protected void clearViews() {
        if (viewContainer != null) {
            super.clearViews();
            passwordCardView.setVisibility(View.GONE);
            createBtn.setText(R.string.create);
        }
    }

}