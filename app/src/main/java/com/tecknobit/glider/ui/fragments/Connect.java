package com.tecknobit.glider.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tecknobit.glider.R;
import com.tecknobit.glider.ui.activities.MainActivity;
import com.tecknobit.glider.ui.fragments.parents.FormFragment;

import org.json.JSONObject;

import static com.tecknobit.glider.ui.activities.SplashScreen.STARTER_ACTIVITY;

public class Connect extends FormFragment {

    /**
     * Required empty public constructor for the normal Android's workflow
     **/
    public Connect() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_connect, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.connectBtn).setOnClickListener(v ->
                startActivity(new Intent(STARTER_ACTIVITY, MainActivity.class)));
    }


    @Override
    protected void loadInputMessagesLists() {

    }

    @Override
    protected void startInputsListenWorkflow() {

    }

    @Override
    protected void showsError(int index) {

    }

    @Override
    @SafeVarargs
    protected final <T> JSONObject getRequestPayload(T... parameters) {
        return null;
    }

}