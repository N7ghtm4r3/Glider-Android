package com.tecknobit.glider.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tecknobit.glider.R;

public class InsertFragment extends GliderFragment {

    public InsertFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_insert, container, false);
    }

    @Override
    protected void clearViews() {

    }

}