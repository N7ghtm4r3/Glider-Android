package com.tecknobit.glider.ui.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.tecknobit.glider.R;
import com.tecknobit.glider.ui.fragments.general.FormFragment;

public class InsertFragment extends FormFragment {

    public InsertFragment() {
        textInputLayouts = new TextInputLayout[2];
        textInputEditTexts = new TextInputEditText[2];
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return viewContainer = inflater.inflate(R.layout.fragment_insert, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadInputMessagesLists();
        instantiateInputs(view, new int[]{R.id.tailLayout, R.id.passwordLayout},
                new int[]{R.id.tailInput, R.id.passwordInput});
        view.findViewById(R.id.insertBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // TODO: 19/12/2022 MAKE REQUEST THEN
                clearViews();
            }
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
                    if (before > 0) {
                        if (start + count > 0)
                            textInputEditTexts[finalJ].setError(null);
                        else
                            textInputLayouts[finalJ].setError(inputsErrors.get(finalJ));
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }
    }

}