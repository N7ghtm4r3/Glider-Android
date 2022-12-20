package com.tecknobit.glider.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tecknobit.glider.R;
import com.tecknobit.glider.ui.fragments.general.PasswordsListFragment;

import org.json.JSONObject;

public class ListFragment extends PasswordsListFragment {

    public ListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return viewContainer = inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setPasswordsRecycler(R.id.passwords);
    }

    @SafeVarargs
    @Override
    protected final <T> JSONObject getRequestPayload(T... parameters) {
        return null;
    }

    @Override
    protected void clearViews() {

    }

}