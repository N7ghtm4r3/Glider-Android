package com.tecknobit.glider.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.tecknobit.glider.R;
import com.tecknobit.glider.helpers.PasswordsAdapter;

import java.util.ArrayList;

import static com.tecknobit.glider.ui.activities.MainActivity.MAIN_ACTIVITY;
import static com.tecknobit.glider.ui.activities.MainActivity.navController;

/**
 * The {@link ListFragment} fragment is the section of the app where there is the list of the
 * passwords stored or removed that can be recover or definitely deleted
 *
 * @author Tecknobit - N7ghtm4r3
 * @see Fragment
 **/
public class ListFragment extends Fragment {

    private View listContainer;
    private RecyclerView passwords;
    private boolean recoveryMode;
    private SearchView searchView;

    /**
     * Required empty public constructor for the normal Android's workflow
     **/
    public ListFragment() {
        // Required empty public constructor
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
        return listContainer = inflater.inflate(R.layout.fragment_list, container, false);
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
        recoveryMode = navController.getCurrentDestination().getLabel().equals(getString(R.string.removed));
        SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.swipe);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            loadRecycler();
            swipeRefreshLayout.setRefreshing(false);
        });
        passwords = view.findViewById(R.id.passwords);
        searchView = view.findViewById(R.id.searchView);
        passwords.setLayoutManager(new LinearLayoutManager(MAIN_ACTIVITY));
        loadRecycler();
    }

    private void loadRecycler() {
        passwords.setAdapter(new PasswordsAdapter(new ArrayList<>(), recoveryMode));
    }

}