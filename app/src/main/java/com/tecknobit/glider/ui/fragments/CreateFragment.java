package com.tecknobit.glider.ui.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import static com.tecknobit.glider.helpers.Utils.PASSWORD_LENGTH_KEY;
import static com.tecknobit.glider.helpers.Utils.PASSWORD_MAX_LENGTH;
import static com.tecknobit.glider.helpers.Utils.PASSWORD_MIN_LENGTH;
import static com.tecknobit.glider.helpers.Utils.SCOPES_KEY;
import static com.tecknobit.glider.helpers.Utils.TAIL_KEY;
import static com.tecknobit.glider.helpers.Utils.getTextFromEdit;
import static com.tecknobit.glider.helpers.Utils.hideKeyBoard;
import static com.tecknobit.glider.helpers.Utils.loadViews;
import static com.tecknobit.glider.helpers.Utils.showSnackbar;

public class CreateFragment extends Fragment implements View.OnClickListener {


    private static final HashMap<Integer, String> inputsErrors = new HashMap<>();
    private static final HashMap<Integer, String> inputsHints = new HashMap<>();
    private final TextInputEditText[] textInputEditTexts = new TextInputEditText[3];
    private final TextInputLayout[] textInputLayouts = new TextInputLayout[3];
    private MaterialCardView passwordCardView;
    private MaterialTextView passwordTextView;
    private MaterialButton createBtn;
    private View createContainer;

    public CreateFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return createContainer = inflater.inflate(R.layout.fragment_create, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadLists();
        loadViews(view, textInputEditTexts, new int[]{R.id.tailInput, R.id.scopesInput, R.id.lengthInput});
        loadViews(view, textInputLayouts, new int[]{R.id.tailLayout, R.id.scopesLayout,
                R.id.lengthLayout});
        createBtn = view.findViewById(R.id.createBtn);
        createBtn.setOnClickListener(this);
        passwordCardView = view.findViewById(R.id.passwordCard);
        passwordCardView.setOnClickListener(this);
        passwordTextView = view.findViewById(R.id.passwordCreated);
        passwordTextView.setOnClickListener(this);
        startInputsListenWorkflow();
    }

    private void loadLists() {
        inputsErrors.put(0, getString(R.string.tail_is_required));
        inputsErrors.put(1, getString(R.string.invalid_length));
        inputsErrors.put(2, getString(R.string.length_is_required));
        inputsHints.put(0, getString(R.string.tail_hint));
        inputsHints.put(1, getString(R.string.scopes_hint));
        inputsHints.put(2, getString(R.string.length_hint));
    }

    private void startInputsListenWorkflow() {
        for (int j = 0; j < textInputEditTexts.length; j++) {
            int finalJ = j;
            if (j != 1) {
                textInputEditTexts[j].addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (start + count > 0)
                            textInputLayouts[finalJ].setError("");
                        else
                            textInputLayouts[finalJ].setError(inputsErrors.get(finalJ));
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
            }
            // TODO: 17/12/2022 TO WORK ON THIS CHANGING HELPER TEXT AND ITS COLOR 
            textInputLayouts[j].setStartIconOnClickListener(v -> {
                if (finalJ != 1) {
                    String required = getString(R.string.required);
                    if (textInputLayouts[finalJ].getHelperText() != required) {
                        textInputLayouts[finalJ].setHelperText(required);
                    } else {
                        textInputLayouts[finalJ].setHelperText(inputsHints.get(finalJ));
                    }
                }
            });
        }
    }

    @Override
    @SuppressLint("NonConstantResourceId")
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.createBtn -> {
                if (createBtn.getText().equals(getString(R.string.create))) {
                    hideKeyBoard(v);
                    enableEditTexts(false);
                    JSONObject payload = getCreatePayload();
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

    private void enableEditTexts(boolean enable) {
        for (TextInputEditText editText : textInputEditTexts)
            editText.setEnabled(enable);
    }

    private JSONObject getCreatePayload() {
        try {
            String tail = getTextFromEdit(textInputEditTexts[0]);
            if (!tail.isEmpty()) {
                JSONObject payload = new JSONObject()
                        .put(TAIL_KEY, tail)
                        .put(SCOPES_KEY, List.of(getTextFromEdit(textInputEditTexts[1]).split(",")));
                try {
                    int length = Integer.parseInt(getTextFromEdit(textInputEditTexts[2]));
                    if (length < PASSWORD_MIN_LENGTH || length > PASSWORD_MAX_LENGTH) {
                        showsError(1);
                        return null;
                    } else
                        payload.put(PASSWORD_LENGTH_KEY, length);
                    return payload;
                } catch (NumberFormatException e) {
                    showsError(2);
                    return null;
                }
            } else
                showsError(0);
        } catch (JSONException e) {
            return null;
        }
        return null;
    }

    private void showsError(int index) {
        String errorMsg = inputsErrors.get(index);
        if (index == 1) {
            index++;
            showSnackbar(createContainer, R.string.password_length_input_error);
        } else
            showSnackbar(createContainer, errorMsg);
        textInputLayouts[index].setError(errorMsg);
    }

    private void clearViews() {
        if (createBtn != null) {
            for (int j = 0; j < textInputEditTexts.length; j++) {
                textInputEditTexts[j].setText("");
                textInputLayouts[j].setError("");
            }
            enableEditTexts(true);
            passwordCardView.setVisibility(View.GONE);
            createBtn.setText(R.string.create);
        }
    }

    @Override
    public void onPrimaryNavigationFragmentChanged(boolean isPrimaryNavigationFragment) {
        super.onPrimaryNavigationFragmentChanged(isPrimaryNavigationFragment);
        if (!isPrimaryNavigationFragment)
            clearViews();
    }

}