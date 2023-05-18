package com.tecknobit.glider.helpers.adapters;

import android.annotation.SuppressLint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filterable;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;
import com.tecknobit.apimanager.annotations.Wrapper;
import com.tecknobit.apimanager.annotations.android.ResId;
import com.tecknobit.glider.R;
import com.tecknobit.glider.helpers.GliderLauncher.Operation;
import com.tecknobit.glider.helpers.local.ManageRequest;
import com.tecknobit.glider.records.Device;
import com.tecknobit.glider.records.Device.DeviceKeys;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;

import static android.view.LayoutInflater.from;
import static androidx.core.content.ContextCompat.getDrawable;
import static com.tecknobit.apimanager.apis.SocketManager.StandardResponseCode.SUCCESSFUL;
import static com.tecknobit.apimanager.formatters.TimeFormatter.getStringDate;
import static com.tecknobit.glider.R.drawable.ic_baseline_desktop_24;
import static com.tecknobit.glider.R.string.device_blacklisted_successfully;
import static com.tecknobit.glider.R.string.device_disconnected_successfully;
import static com.tecknobit.glider.R.string.device_unblacklisted_successfully;
import static com.tecknobit.glider.R.string.ope_failed;
import static com.tecknobit.glider.helpers.GliderLauncher.GliderKeys.ope;
import static com.tecknobit.glider.helpers.GliderLauncher.GliderKeys.statusCode;
import static com.tecknobit.glider.helpers.GliderLauncher.Operation.DISCONNECT;
import static com.tecknobit.glider.helpers.GliderLauncher.Operation.MANAGE_DEVICE_AUTHORIZATION;
import static com.tecknobit.glider.helpers.local.User.DEVICE_NAME;
import static com.tecknobit.glider.helpers.local.User.socketManager;
import static com.tecknobit.glider.helpers.local.User.user;
import static com.tecknobit.glider.helpers.local.Utils.showSnackbar;
import static com.tecknobit.glider.records.Device.DeviceKeys.ipAddress;
import static com.tecknobit.glider.records.Device.DeviceKeys.targetDevice;
import static com.tecknobit.glider.records.Device.DeviceKeys.type;
import static com.tecknobit.glider.records.Device.Type.MOBILE;
import static com.tecknobit.glider.records.Session.SessionKeys.sessionPassword;
import static com.tecknobit.glider.ui.activities.MainActivity.MAIN_ACTIVITY;
import static java.text.DateFormat.DATE_FIELD;
import static java.text.DateFormat.getDateInstance;
import static java.util.Locale.getDefault;

/**
 * The {@link DevicesAdapter} is the adapter for the devices list
 *
 * @author Tecknobit - N7ghtm4r3
 * @see RecyclerView.Adapter
 * @see Filterable
 */
public class DevicesAdapter extends RecyclerView.Adapter<DevicesAdapter.DeviceView> {

    /**
     * {@code devices} list of {@link Device} to manage
     */
    private ArrayList<Device> devices;

    /**
     * Constructor to init {@link DevicesAdapter} object
     *
     * @param devices: list of {@link Device} to manage
     */
    public DevicesAdapter(ArrayList<Device> devices) {
        this.devices = devices;
    }

    /**
     * {@inheritDoc}
     */
    @NonNull
    @Override
    public DeviceView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DeviceView(from(MAIN_ACTIVITY).inflate(R.layout.device_item, parent, false));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressLint("SetTextI18n")
    public void onBindViewHolder(@NonNull DeviceView holder, int position) {
        Device device = devices.get(position);
        holder.name.setText(device.getName());
        Device.Type type = device.getType();
        if (type.equals(Device.Type.DESKTOP))
            holder.deviceIconType.setImageDrawable(getDrawable(MAIN_ACTIVITY, ic_baseline_desktop_24));
        holder.ip.setText(MAIN_ACTIVITY.getString(R.string.ip_address) + " " + device.getIpAddress());
        holder.login.setText(MAIN_ACTIVITY.getString(R.string.login_date) + " " +
                getLocaleDate(device.getLoginDateTimestamp()));
        holder.lastActivity.setText(MAIN_ACTIVITY.getString(R.string.last_activity) + " " + getLocaleDate(device.getLastActivityTimestamp()));
        if (user.isAccountManager()) {
            if (device.isBlacklisted()) {
                holder.relActions.setVisibility(View.GONE);
                holder.unblacklistBtn.setVisibility(View.VISIBLE);
            }
        } else {
            holder.relActions.setVisibility(View.GONE);
            holder.unblacklistBtn.setVisibility(View.GONE);
        }
    }

    /**
     * Method to format a date input with the correct {@link Locale}
     *
     * @param date: date as long timestamp to format
     * @return date input with the correct {@link Locale} as {@link String}
     */
    private String getLocaleDate(long date) {
        if ((System.currentTimeMillis() - date) >= (86400 * 1000) / 2)
            return getDateInstance(DATE_FIELD, getDefault()).format(new Date(date));
        return getStringDate(date);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getItemCount() {
        return devices.size();
    }

    /**
     * Method to refresh the current {@link #devices} list with a new list <br>
     * No-any params required
     */
    @SuppressLint("NotifyDataSetChanged")
    public void refreshDevicesList(ArrayList<Device> devices) {
        this.devices = devices;
        notifyDataSetChanged();
    }

    /**
     * Method to get {@link #devices} instance <br>
     * No-any params required
     *
     * @return {@link #devices} instance as {@link Collection} of {@link Device}
     */
    public Collection<Device> getCurrentDevicesList() {
        return devices;
    }

    /**
     * The {@link DeviceView} is the view adapter for the {@link DevicesAdapter}
     *
     * @author Tecknobit - N7ghtm4r3
     * @see RecyclerView.ViewHolder
     * @see View.OnClickListener
     */
    @SuppressLint("NonConstantResourceId")
    public static class DeviceView extends RecyclerView.ViewHolder implements View.OnClickListener,
            ManageRequest {

        /**
         * {@code payload} the payload to send with the request
         */
        protected JSONObject payload;

        /**
         * {@code response} instance to contains the response from the backend
         */
        protected JSONObject response;

        /**
         * {@code name} of the device -> as view instance type
         */
        @ResId(id = R.id.deviceName)
        private final MaterialTextView name;

        /**
         * {@code deviceIconType} icon type of the device -> as view instance type
         */
        @ResId(id = R.id.deviceIconType)
        private final ShapeableImageView deviceIconType;

        /**
         * {@code ip} of the device -> as view instance type
         */
        @ResId(id = R.id.ipAddress)
        private final MaterialTextView ip;

        /**
         * {@code login} of the device -> as view instance type
         */
        @ResId(id = R.id.loginDate)
        private final MaterialTextView login;

        /**
         * {@code lastActivity} last activity of the device -> as view instance type
         */
        @ResId(id = R.id.lastActivity)
        private final MaterialTextView lastActivity;

        /**
         * {@code relActions} view where are contained actions buttons
         */
        @ResId(id = R.id.relActions)
        private final RelativeLayout relActions;

        /**
         * {@code unblacklistBtn} button to unblacklist a {@link Device}
         */
        @ResId(id = R.id.unblacklistBtn)
        private final MaterialButton unblacklistBtn;

        /**
         * {@inheritDoc}
         */
        public DeviceView(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.deviceName);
            deviceIconType = itemView.findViewById(R.id.deviceIconType);
            ip = itemView.findViewById(R.id.ipAddress);
            login = itemView.findViewById(R.id.loginDate);
            lastActivity = itemView.findViewById(R.id.lastActivity);
            relActions = itemView.findViewById(R.id.relActions);
            unblacklistBtn = itemView.findViewById(R.id.unblacklistBtn);
            unblacklistBtn.setOnClickListener(this);
            for (int btn : new int[]{R.id.blackListBtn, R.id.disconnectBtn})
                itemView.findViewById(btn).setOnClickListener(this);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.blackListBtn -> manageDeviceAuthorization(true);
                case R.id.disconnectBtn -> disconnectDevice();
                case R.id.unblacklistBtn -> manageDeviceAuthorization(false);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public <T> void setRequestPayload(Operation operation, T... parameters) {
            try {
                if (payload == null)
                    setBasePayload();
                payload.put(ope.name(), operation);
            } catch (JSONException e) {
                payload = null;
            }
        }

        /**
         * Method to set the base payload for a request <br>
         * No-any params required
         */
        @Override
        public void setBasePayload() {
            try {
                payload = new JSONObject();
                payload.put(DeviceKeys.name.name(), DEVICE_NAME)
                        .put(type.name(), MOBILE)
                        .put(sessionPassword.name(), user.getSessionPassword());
            } catch (JSONException e) {
                payload = null;
            }
        }

        /**
         * Method to disconnect a device <br>
         * No-any params required
         */
        @Wrapper
        public void disconnectDevice() {
            executeRequest(DISCONNECT, false);
        }

        /**
         * Method to manage the device authorization
         *
         * @param blacklist: whether is to blacklist or unblacklist
         */
        @Wrapper
        public void manageDeviceAuthorization(boolean blacklist) {
            executeRequest(MANAGE_DEVICE_AUTHORIZATION, blacklist);
        }

        /**
         * Method to execute a request on a {@link Device}
         *
         * @param ope:       ope to execute, {@link Operation#DISCONNECT} of {@link Operation#MANAGE_DEVICE_AUTHORIZATION}
         * @param blacklist: whether is to blacklist or unblacklist
         */
        private void executeRequest(Operation ope, boolean blacklist) {
            setRequestPayload(ope);
            try {
                payload.put(targetDevice.name(), new JSONObject()
                        .put(DeviceKeys.name.name(), name.getText().toString())
                        .put(ipAddress.name(), ip.getText().toString().split(" ")[2]));
                executor.execute(() -> {
                    try {
                        socketManager.writeContent(payload);
                        response = new JSONObject(socketManager.readContent());
                        MAIN_ACTIVITY.runOnUiThread(() -> {
                            try {
                                if (response.getString(statusCode.name()).equals(SUCCESSFUL.name())) {
                                    if (ope.equals(MANAGE_DEVICE_AUTHORIZATION)) {
                                        if (blacklist) {
                                            relActions.setVisibility(View.GONE);
                                            unblacklistBtn.setVisibility(View.VISIBLE);
                                            showSnackbar(ip, device_blacklisted_successfully);
                                        } else {
                                            relActions.setVisibility(View.VISIBLE);
                                            unblacklistBtn.setVisibility(View.GONE);
                                            showSnackbar(ip, device_unblacklisted_successfully);
                                        }
                                    } else {
                                        showSnackbar(ip, device_disconnected_successfully);
                                    }
                                } else
                                    showSnackbar(ip, ope_failed);
                            } catch (JSONException e) {
                                showSnackbar(ip, ope_failed);
                            }
                        });
                    } catch (Exception e) {
                        showSnackbar(ip, ope_failed);
                    }
                });
            } catch (JSONException e) {
                showSnackbar(ip, ope_failed);
            }
        }

    }

}
