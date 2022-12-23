package com.tecknobit.glider.ui.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;
import com.tecknobit.glider.R;
import com.tecknobit.glider.helpers.adapters.DevicesAdapter;
import com.tecknobit.glider.helpers.local.Utils;
import com.tecknobit.glider.helpers.toImport.Device;
import com.tecknobit.glider.ui.fragments.parents.RealtimeRecyclerFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

import static androidx.recyclerview.widget.ItemTouchHelper.LEFT;
import static androidx.recyclerview.widget.ItemTouchHelper.RIGHT;
import static com.tecknobit.glider.helpers.local.Utils.COLOR_PRIMARY;
import static com.tecknobit.glider.helpers.local.Utils.COLOR_RED;
import static com.tecknobit.glider.helpers.local.Utils.HOST_ADDRESS_KEY;
import static com.tecknobit.glider.helpers.local.Utils.HOST_PORT_KEY;
import static com.tecknobit.glider.helpers.local.Utils.QR_CODE_LOGIN_KEY;
import static com.tecknobit.glider.helpers.local.Utils.SERVER_STATUS_KEY;
import static com.tecknobit.glider.helpers.local.Utils.SINGLE_USE_MODE_KEY;
import static com.tecknobit.glider.helpers.local.Utils.showSnackbar;
import static com.tecknobit.glider.helpers.toImport.Device.devices;
import static com.tecknobit.glider.ui.activities.MainActivity.MAIN_ACTIVITY;

/**
 * The {@link AccountFragment} fragment is the section of the app where there are the information about:
 * <ul>
 *     <li>Server configuration</li>
 *     <li>Security configuration</li>
 *     <li>Devices connected to the server</li>
 * </ul>
 *
 * @author Tecknobit - N7ghtm4r3
 * @see RealtimeRecyclerFragment
 * @see OnClickListener
 **/
public class AccountFragment extends RealtimeRecyclerFragment implements OnClickListener {

    /**
     * {@code textViews} list of information {@link MaterialTextView} to fill
     **/
    private static final HashMap<String, MaterialTextView> textViews = new HashMap<>();

    /**
     * {@code devicesAdapter} adapter for the {@link #recyclerManager}
     **/
    private DevicesAdapter devicesAdapter;

    /**
     * {@code devicesCardView} view to show the {@link Device}'s list
     **/
    private MaterialCardView devicesCardView;

    /**
     * Required empty public constructor for the normal Android's workflow
     **/
    public AccountFragment() {
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
        return viewContainer = inflater.inflate(R.layout.fragment_account, container, false);
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
        for (int btn : new int[]{R.id.deleteBtn, R.id.logoutBtn})
            view.findViewById(btn).setOnClickListener(this);
        final String[] keysViews = new String[]{HOST_ADDRESS_KEY, HOST_PORT_KEY, SERVER_STATUS_KEY,
                SINGLE_USE_MODE_KEY, QR_CODE_LOGIN_KEY};
        final int[] idsViews = new int[]{R.id.host_address, R.id.host_port, R.id.server_status,
                R.id.single_use_mode, R.id.qr_code_login};
        for (int j = 0; j < idsViews.length; j++)
            textViews.put(keysViews[j], view.findViewById(idsViews[j]));
        devicesCardView = view.findViewById(R.id.devicesCard);
        setRecycler(R.id.devicesRecycler, MAIN_ACTIVITY);
        swipeRefreshLayout = view.findViewById(R.id.swipe);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            // TODO: 23/12/2022 REMOVE THIS SNIPPET
            devices.add(new Device("agaga", "111.112.33.11", "24/11/22", "24/11/22", Device.Type.values()[new Random().nextInt(2)]));
            loadRecycler();
            swipeRefreshLayout.setRefreshing(false);
        });
        loadRecycler();
    }

    /**
     * Method to set the {@link #recyclerManager} and the {@link ItemTouchHelper}'s workflow
     * with the swiping gestures:
     * <ul>
     *     <li>
     *         Swiping on {@link ItemTouchHelper#LEFT} the {@link Device} will be
     *         <strong>blacklisted</strong> and the {@link Device#blacklist()} will be invoked
     *     </li>
     *      <li>
     *         Swiping on {@link ItemTouchHelper#RIGHT} the {@link Device} will be disconnected
     *       </li>
     * </ul>
     *
     * @param recyclerId: identifier of the {@link #recyclerManager}
     * @param context:    context where the {@link #recyclerManager} is shown
     **/
    @Override
    protected void setRecycler(int recyclerId, Context context) {
        super.setRecycler(recyclerId, context);
        final MaterialButton[] buttons = new MaterialButton[2];
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, LEFT | RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return true;
            }

            @Override
            @SuppressLint("NotifyDataSetChanged")
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                for (MaterialButton button : buttons)
                    button.setTextColor(COLOR_PRIMARY);
                swipeRefreshLayout.setEnabled(true);
                devicesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                                    @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY,
                                    int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                swipeRefreshLayout.setEnabled(false);
                buttons[0] = viewHolder.itemView.findViewById(R.id.disconnectBtn);
                buttons[1] = viewHolder.itemView.findViewById(R.id.blackListBtn);
                if (dX > 0) {
                    buttons[0].setTextColor(COLOR_RED);
                    buttons[1].setTextColor(COLOR_PRIMARY);
                    // TODO: 23/12/2022 REQUEST THEN
                    showSnackbar(viewContainer, "DISCONNECTED");
                } else {
                    buttons[1].setTextColor(COLOR_RED);
                    buttons[0].setTextColor(COLOR_PRIMARY);
                    // TODO: 23/12/2022 REQUEST THEN
                    showSnackbar(viewContainer, "BLACKLISTED");
                }
            }

            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView,
                                        @NonNull RecyclerView.ViewHolder viewHolder) {
                return makeMovementFlags(0, LEFT | RIGHT);
            }

        }).attachToRecyclerView(recyclerManager);
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    @SuppressLint("NotifyDataSetChanged")
    protected void loadRecycler() {
        super.loadRecycler();
        runnable = () -> {
            // TODO: 23/12/2022 MODIFY THIS SNIPPET TO REFRESH ONLY THE CORRECT TEXTVIEW
            try {
                JSONObject accountPayload = new JSONObject(); // TODO: 23/12/2022 obtained by request
                accountPayload.put(HOST_ADDRESS_KEY, "prova" + new Random().nextInt());
                accountPayload.put(HOST_PORT_KEY, "prova" + new Random().nextInt());
                accountPayload.put(SERVER_STATUS_KEY, "prova" + new Random().nextInt());
                accountPayload.put(SINGLE_USE_MODE_KEY, new Random().nextBoolean());
                accountPayload.put(QR_CODE_LOGIN_KEY, "prova" + new Random().nextInt());
                for (Iterator<String> it = accountPayload.keys(); it.hasNext(); ) {
                    String key = it.next();
                    String value = accountPayload.get(key).toString();
                    if (key.equals(SINGLE_USE_MODE_KEY)) {
                        if (Boolean.parseBoolean(value))
                            devicesCardView.setVisibility(View.GONE);
                        else
                            devicesCardView.setVisibility(View.VISIBLE);
                        value = value.toUpperCase();
                    }
                    textViews.get(key).setText(value);
                }
            } catch (JSONException ignored) {
            }
            // TODO: 23/12/2022 END MODIFY THIS SNIPPET
            if (textViews.get(SINGLE_USE_MODE_KEY).getVisibility() == View.VISIBLE) {
                int currentSize = devices.size();
                if (currentRecyclerSize != currentSize) {
                    if (devicesAdapter == null) {
                        devicesAdapter = new DevicesAdapter(devices);
                        recyclerManager.setAdapter(devicesAdapter);
                    } else
                        devicesAdapter.refreshDevicesList(devices);
                    currentRecyclerSize = currentSize;
                } else {
                    if (!devices.equals(devicesAdapter.getCurrentDevicesList()))
                        devicesAdapter.notifyDataSetChanged();
                }
            }
            handler.postDelayed(runnable, 10000);
        };
        runnable.run();
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    @SuppressLint("NonConstantResourceId")
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.deleteBtn -> {
                Utils.showSnackbar(v, "ACCOUNT DELETED");
            }
            case R.id.logoutBtn -> {
                Utils.showSnackbar(v, "LOGOUT");
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    // TODO: 21/12/2022 LIST THE BEHAVIOURS OF THIS METHOD IN THE DOCU STRING IN BASE OF THE OPE PASSED AS ARGUMENT
    @Override
    @SafeVarargs
    protected final <T> JSONObject getRequestPayload(T... parameters) {
        return null;
    }

}