package com.tecknobit.glider.ui.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;
import com.tecknobit.apimanager.annotations.android.ResId;
import com.tecknobit.glider.R;
import com.tecknobit.glider.helpers.adapters.PasswordsAdapter;
import com.tecknobit.glider.helpers.local.Utils;
import com.tecknobit.glider.records.Password;
import com.tecknobit.glider.records.Password.Status;
import com.tecknobit.glider.ui.fragments.parents.RealtimeRecyclerFragment;

import java.util.ArrayList;

import static androidx.recyclerview.widget.ItemTouchHelper.LEFT;
import static androidx.recyclerview.widget.ItemTouchHelper.RIGHT;
import static androidx.recyclerview.widget.ItemTouchHelper.SimpleCallback;
import static com.tecknobit.glider.helpers.GliderLauncher.Operation.DELETE_PASSWORD;
import static com.tecknobit.glider.helpers.GliderLauncher.Operation.RECOVER_PASSWORD;
import static com.tecknobit.glider.helpers.adapters.PasswordsAdapter.PasswordView;
import static com.tecknobit.glider.helpers.local.User.passwords;
import static com.tecknobit.glider.helpers.local.User.user;
import static com.tecknobit.glider.helpers.local.Utils.COLOR_PRIMARY;
import static com.tecknobit.glider.helpers.local.Utils.COLOR_RED;
import static com.tecknobit.glider.helpers.local.Utils.hideKeyboard;
import static com.tecknobit.glider.records.Password.Status.ACTIVE;
import static com.tecknobit.glider.records.Password.Status.DELETED;
import static com.tecknobit.glider.ui.activities.MainActivity.MAIN_ACTIVITY;
import static com.tecknobit.glider.ui.activities.MainActivity.navController;

/**
 * The {@link ListFragment} fragment is the section of the app where there is the list of the
 * {@link Password} stored or removed that can be recover or definitely deleted
 *
 * @author Tecknobit - N7ghtm4r3
 * @see RealtimeRecyclerFragment
 */
@SuppressLint("NonConstantResourceId")
public class ListFragment extends RealtimeRecyclerFragment {

    /**
     * {@code relList} view container to show when {@link #list} is filled
     */
    @ResId(id = R.id.relList)
    private RelativeLayout relList;

    /**
     * {@code noPasswordsText} view to show when {@link #list} is empty
     */
    @ResId(id = R.id.noPasswords)
    private MaterialTextView noPasswordsText;

    /**
     * {@code recoveryMode} whether the {@link #list} of the  {@link Password} is in a
     * {@link Status#DELETED} status and can be recovered or is in an {@link Status#ACTIVE} status
     */
    private boolean recoveryMode;

    /**
     * {@code list} list of the {@link Password}
     */
    private ArrayList<Password> list;

    /**
     * {@code searchView} view to contains the {@link #search}
     */
    @ResId(id = R.id.searchView)
    private TextInputLayout searchView;

    /**
     * {@code search} view to create a query and filter the {@link #recyclerManager}
     */
    @ResId(id = R.id.searchQuery)
    private TextInputEditText search;

    /**
     * {@code passwordsAdapter} adapter for the {@link #recyclerManager}
     */
    private PasswordsAdapter passwordsAdapter;

    /**
     * Required empty public constructor for the normal Android's workflow
     */
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
        return viewContainer = inflater.inflate(R.layout.fragment_list, container, false);
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
        noPasswordsText = view.findViewById(R.id.noPasswords);
        relList = view.findViewById(R.id.relList);
        searchView = view.findViewById(R.id.searchView);
        search = view.findViewById(R.id.searchQuery);
        setRecycler(R.id.passwords, MAIN_ACTIVITY);
        startSearchViewWorkflow();
        swipeRefreshLayout = view.findViewById(R.id.swipe);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            hideKeyboard(viewContainer);
            user.refreshData();
            loadRecycler();
            swipeRefreshLayout.setRefreshing(false);
        });
        loadRecycler();
    }

    /**
     * Method to start the workflow of the {@link #search} view <br>
     * No-any params required
     */
    private void startSearchViewWorkflow() {
        ((TextInputLayout) viewContainer.findViewById(R.id.searchView)).setEndIconOnClickListener(v -> {
            search.setText("");
            hideKeyboard(v);
            viewContainer.clearFocus();
            passwordsAdapter.resetPasswordsList();
        });
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0)
                    passwordsAdapter.getFilter().filter(s.toString());
                else {
                    viewContainer.clearFocus();
                    if (passwordsAdapter != null)
                        passwordsAdapter.resetPasswordsList();
                }
            }
        });
        search.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                hideKeyboard(v);
                viewContainer.clearFocus();
                return true;
            }
            return false;
        });
    }

    /**
     * Method to set the {@link #recyclerManager} and the {@link ItemTouchHelper}'s workflow
     * with the swiping gestures:
     * <ul>
     *     <li>
     *         if {@link #recoveryMode} is set on {@code "false"}:
     *         <ul>
     *             <li>
     *                 Swiping on {@link ItemTouchHelper#LEFT} the {@link Password} will be deleted
     *                 and the status will be set on {@link Status#DELETED}
     *             </li>
     *             <li>
     *                 Swiping on {@link ItemTouchHelper#RIGHT} the {@link Password} will be copied
     *                 with the {@link Utils#copyText(TextView, View)} method
     *             </li>
     *         </ul>
     *     </li>
     *     <li>
     *         if {@link #recoveryMode} is set on {@code "true"}:
     *         <ul>
     *             <li>
     *                 Swiping on {@link ItemTouchHelper#LEFT} the {@link Password}  will be
     *                 <strong>permanently deleted</strong> and <strong>not more recoverable</strong>
     *             </li>
     *             <li>
     *                 Swiping on {@link ItemTouchHelper#RIGHT} the {@link Password} will be recovered
     *                 and the status will be set on {@link Status#ACTIVE}
     *             </li>
     *         </ul>
     *     </li>
     * </ul>
     * @param recyclerId: identifier of the {@link #recyclerManager}
     * @param context: context where the {@link #recyclerManager} is shown
     */
    @Override
    protected void setRecycler(int recyclerId, Context context) {
        super.setRecycler(recyclerId, context);
        final MaterialButton[] buttons = new MaterialButton[2];
        itemTouchHelper = new ItemTouchHelper(new SimpleCallback(0, LEFT | RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return true;
            }

            @Override
            @SuppressLint("NotifyDataSetChanged")
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                swipeRefreshLayout.setEnabled(true);
                for (MaterialButton button : buttons)
                    button.setTextColor(COLOR_PRIMARY);
                PasswordView password = (PasswordView) viewHolder;
                if (direction == RIGHT) {
                    if (!recoveryMode)
                        password.copyPassword();
                    else
                        password.executeOperation(RECOVER_PASSWORD);
                } else
                    password.executeOperation(DELETE_PASSWORD);
                passwordsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                                    @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY,
                                    int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                swipeRefreshLayout.setEnabled(false);
                buttons[0] = viewHolder.itemView.findViewById(R.id.actionBtn);
                buttons[1] = viewHolder.itemView.findViewById(R.id.deleteBtn);
                if (dX > 0) {
                    buttons[0].setTextColor(COLOR_RED);
                    buttons[1].setTextColor(COLOR_PRIMARY);
                } else {
                    buttons[0].setTextColor(COLOR_PRIMARY);
                    buttons[1].setTextColor(COLOR_RED);
                }
            }

            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView,
                                        @NonNull RecyclerView.ViewHolder viewHolder) {
                return makeMovementFlags(0, LEFT | RIGHT);
            }
        });
        itemTouchHelper.attachToRecyclerView(recyclerManager);
    }

    /**
     * {@inheritDoc}
     *
     * @apiNote the data will be automatically fetched and refreshed
     * by the {@link Status} of {@link Password} in base of the {@link #recoveryMode} value
     */
    @Override
    @SuppressLint("NotifyDataSetChanged")
    protected void loadRecycler() {
        super.loadRecycler();
        runnable = () -> {
            if (!recoveryMode)
                list = passwords.get(ACTIVE);
            else
                list = passwords.get(DELETED);
            if (list != null) {
                if (user.isPasswordManager())
                    itemTouchHelper.attachToRecyclerView(recyclerManager);
                else
                    itemTouchHelper.attachToRecyclerView(null);
                int passwordsSize = list.size();
                if (currentRecyclerSize != passwordsSize) {
                    if (passwordsSize > 0) {
                        noPasswordsText.setVisibility(View.GONE);
                        relList.setVisibility(View.VISIBLE);
                        searchView.setVisibility(View.VISIBLE);
                        if (passwordsAdapter == null) {
                            passwordsAdapter = new PasswordsAdapter(list, recoveryMode);
                            recyclerManager.setAdapter(passwordsAdapter);
                        } else
                            passwordsAdapter.refreshPasswordsList(list);
                    } else {
                        noPasswordsText.setVisibility(View.VISIBLE);
                        relList.setVisibility(View.GONE);
                        searchView.setVisibility(View.GONE);
                    }
                    currentRecyclerSize = passwordsSize;
                } else {
                    if (passwordsSize > 0 && !list.equals(passwordsAdapter.getCurrentPasswords()))
                        passwordsAdapter.notifyDataSetChanged();
                }
            }
            handler.postDelayed(runnable, 1000);
        };
        runnable.run();
    }

}