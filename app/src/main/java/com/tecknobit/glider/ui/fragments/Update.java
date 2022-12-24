package com.tecknobit.glider.ui.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textview.MaterialTextView;
import com.tecknobit.glider.R;
import com.tecknobit.glider.helpers.local.User;
import com.tecknobit.glider.ui.activities.MainActivity;

import static com.tecknobit.glider.helpers.local.Utils.openUrlPage;
import static com.tecknobit.glider.ui.activities.SplashScreen.STARTER_ACTIVITY;

/**
 * The {@link Update} fragment is the section of the app where the user is warned about a new version
 * or a simple patch update available
 *
 * @author Tecknobit - N7ghtm4r3
 * @see Fragment
 * @see View.OnClickListener
 **/
public class Update extends Fragment implements View.OnClickListener {

    /**
     * Required empty public constructor for the normal Android's workflow
     **/
    public Update() {
        // Required empty public constructor
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
        return inflater.inflate(R.layout.fragment_update, container, false);
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
    @SuppressLint("SetTextI18n")
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        int[] idsBtn;
        if (!User.IS_PATCH) {
            idsBtn = new int[]{R.id.updateBnt, R.id.exitBnt};
            view.findViewById(R.id.keepBnt).setVisibility(View.GONE);
            ((MaterialTextView) view.findViewById(R.id.updateTitle))
                    .setText(getString(R.string.new_version_available));
        } else
            idsBtn = new int[]{R.id.keepBnt, R.id.updateBnt, R.id.exitBnt};
        for (int btn : idsBtn)
            view.findViewById(btn).setOnClickListener(this);
        ((MaterialTextView) view.findViewById(R.id.versionText)).setText("v." + User.UPDATE_VERSION);
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    @SuppressLint("NonConstantResourceId")
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.keepBnt -> startActivity(new Intent(STARTER_ACTIVITY, MainActivity.class));
            case R.id.updateBnt -> openUrlPage("https://play.google.com/store/apps/details?id=com.tecknobit.glider");
            case R.id.exitBnt -> STARTER_ACTIVITY.onBackPressed();
        }
    }

}