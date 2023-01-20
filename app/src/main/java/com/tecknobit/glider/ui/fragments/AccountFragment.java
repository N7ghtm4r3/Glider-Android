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
import com.tecknobit.apimanager.annotations.android.ResId;
import com.tecknobit.glider.R;
import com.tecknobit.glider.helpers.adapters.DevicesAdapter;
import com.tecknobit.glider.helpers.local.User.GliderKeys;
import com.tecknobit.glider.helpers.local.User.Operation;
import com.tecknobit.glider.helpers.local.Utils;
import com.tecknobit.glider.helpers.toImport.records.Device;
import com.tecknobit.glider.ui.fragments.parents.RealtimeRecyclerFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

import static androidx.recyclerview.widget.ItemTouchHelper.LEFT;
import static androidx.recyclerview.widget.ItemTouchHelper.RIGHT;
import static com.tecknobit.glider.R.id.blackListBtn;
import static com.tecknobit.glider.R.id.deleteBtn;
import static com.tecknobit.glider.R.id.devicesCard;
import static com.tecknobit.glider.R.id.devicesRecycler;
import static com.tecknobit.glider.R.id.disconnectBtn;
import static com.tecknobit.glider.R.id.host_address;
import static com.tecknobit.glider.R.id.host_port;
import static com.tecknobit.glider.R.id.localhostValue;
import static com.tecknobit.glider.R.id.logoutBtn;
import static com.tecknobit.glider.R.id.qr_code_login;
import static com.tecknobit.glider.R.id.server_status;
import static com.tecknobit.glider.R.id.single_use_mode;
import static com.tecknobit.glider.R.id.swipe;
import static com.tecknobit.glider.helpers.local.User.devices;
import static com.tecknobit.glider.helpers.local.Utils.COLOR_PRIMARY;
import static com.tecknobit.glider.helpers.local.Utils.COLOR_RED;
import static com.tecknobit.glider.helpers.local.Utils.showSnackbar;
import static com.tecknobit.glider.helpers.toImport.records.Session.SessionKeys.hostAddress;
import static com.tecknobit.glider.helpers.toImport.records.Session.SessionKeys.hostPort;
import static com.tecknobit.glider.helpers.toImport.records.Session.SessionKeys.qrCodeLogin;
import static com.tecknobit.glider.helpers.toImport.records.Session.SessionKeys.runInLocalhost;
import static com.tecknobit.glider.helpers.toImport.records.Session.SessionKeys.singleUseMode;
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
@SuppressLint("NonConstantResourceId")
public class AccountFragment extends RealtimeRecyclerFragment implements OnClickListener {

    /**
     * {@code textViews} list of information {@link MaterialTextView} to fill
     **/
    @ResId(ids = {host_address, host_port, server_status, single_use_mode, qr_code_login, localhostValue})
    private static final HashMap<String, MaterialTextView> textViews = new HashMap<>();

    /**
     * {@code devicesAdapter} adapter for the {@link #recyclerManager}
     **/
    private DevicesAdapter devicesAdapter;

    /**
     * {@code devicesCardView} view to show the {@link Device}'s list
     **/
    @ResId(id = devicesCard)
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
        for (int btn : new int[]{deleteBtn, logoutBtn})
            view.findViewById(btn).setOnClickListener(this);
        final String[] keysViews = new String[]{hostAddress.name(), hostPort.name(),
                GliderKeys.serverStatus.name(), singleUseMode.name(), qrCodeLogin.name(),
                runInLocalhost.name()};
        final int[] idsViews = new int[]{host_address, host_port, server_status,
                single_use_mode, qr_code_login, localhostValue};
        for (int j = 0; j < idsViews.length; j++)
            textViews.put(keysViews[j], view.findViewById(idsViews[j]));
        devicesCardView = view.findViewById(devicesCard);
        setRecycler(devicesRecycler, MAIN_ACTIVITY);
        swipeRefreshLayout = view.findViewById(swipe);
        swipeRefreshLayout.setOnRefreshListener(() -> {
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
                devicesAdapter.notifyDataSetChanged();
                swipeRefreshLayout.setEnabled(true);
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                                    @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY,
                                    int actionState, boolean isCurrentlyActive) {
                if (viewHolder.itemView.findViewById(R.id.unblacklistBtn).getVisibility() == View.GONE) {
                    swipeRefreshLayout.setEnabled(false);
                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState,
                            isCurrentlyActive);
                    buttons[0] = viewHolder.itemView.findViewById(disconnectBtn);
                    buttons[1] = viewHolder.itemView.findViewById(blackListBtn);
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
                        // TODO: 08/01/2023 CHECK WHEN THE DEVICE IS BLACKLISTED IF ITS LAYOUT CHANGE
                    }
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
                accountPayload.put(hostAddress.name(), "prova" + new Random().nextInt());
                accountPayload.put(hostPort.name(), "prova" + new Random().nextInt());
                accountPayload.put(GliderKeys.serverStatus.name(), "prova" + new Random().nextInt());
                accountPayload.put(singleUseMode.name(), new Random().nextBoolean());
                accountPayload.put(qrCodeLogin.name(), "prova" + new Random().nextInt());
                accountPayload.put("run_in_localhost", new Random().nextBoolean());
                for (Iterator<String> it = accountPayload.keys(); it.hasNext(); ) {
                    String key = it.next();
                    String value = accountPayload.get(key).toString();
                    if (key.equals(singleUseMode.name())) {
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
            if (textViews.get(singleUseMode.name()).getVisibility() == View.VISIBLE) {
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
    public void onClick(View v) {
        switch (v.getId()) {
            case deleteBtn -> {
                Utils.showSnackbar(v, "ACCOUNT DELETED");
            }
            case logoutBtn -> {
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
    public final <T> void setRequestPayload(Operation operation, T... parameters) {
        super.setRequestPayload(operation, parameters);
    }

}