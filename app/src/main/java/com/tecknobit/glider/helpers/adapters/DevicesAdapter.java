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
import com.tecknobit.apimanager.annotations.android.ResId;
import com.tecknobit.glider.R;
import com.tecknobit.glider.helpers.local.Utils;
import com.tecknobit.glider.helpers.toImport.records.Device;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;

import static android.view.LayoutInflater.from;
import static androidx.core.content.ContextCompat.getDrawable;
import static com.tecknobit.glider.ui.activities.MainActivity.MAIN_ACTIVITY;

/**
 * The {@link DevicesAdapter} is the adapter for the devices list
 *
 * @author Tecknobit - N7ghtm4r3
 * @see RecyclerView.Adapter
 * @see Filterable
 **/
public class DevicesAdapter extends RecyclerView.Adapter<DevicesAdapter.DeviceView> {

    /**
     * {@code devices} list of {@link Device} to manage
     **/
    private ArrayList<Device> devices;

    /**
     * Constructor to init {@link DevicesAdapter} object
     *
     * @param devices: list of {@link Device} to manage
     **/
    public DevicesAdapter(ArrayList<Device> devices) {
        this.devices = devices;
    }

    /**
     * {@inheritDoc}
     **/
    @NonNull
    @Override
    public DeviceView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DeviceView(from(MAIN_ACTIVITY).inflate(R.layout.device_item, parent, false));
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    @SuppressLint("SetTextI18n")
    public void onBindViewHolder(@NonNull DeviceView holder, int position) {
        Device device = devices.get(position);
        holder.name.setText(MAIN_ACTIVITY.getString(R.string.name) + " " + device.getName());
        Device.Type type = device.getType();
        if (type.equals(Device.Type.DESKTOP))
            holder.deviceIconType.setImageDrawable(getDrawable(MAIN_ACTIVITY, R.drawable.ic_baseline_desktop_24));
        holder.ip.setText(MAIN_ACTIVITY.getString(R.string.ip_address) + " " + device.getIpAddress());
        holder.login.setText(MAIN_ACTIVITY.getString(R.string.login_date) + " " +
                getLocaleDate(device.getLoginDateTimestamp()));
        holder.lastActivity.setText(MAIN_ACTIVITY.getString(R.string.last_activity) + " " +
                getLocaleDate(device.getLastActivityTimestamp()));
        if (device.isBlacklisted()) {
            holder.relActions.setVisibility(View.GONE);
            holder.unblacklistBtn.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Method to format a date input with the correct {@link Locale}
     *
     * @param date: date as long timestamp to format
     * @return date input with the correct {@link Locale} as {@link String}
     **/
    // TODO: 23/12/2022 SET ALSO THE SECONDS AND THE MINUTES TO THE TIME AND IF IS BETWEEN 24h PRINT
    //  FULL DATE IF NOT PRINT ONLY THE DATE VALUE, SEE TRADERBOT IN LAST ACTIVITY SECTION TO DO THIS
    private String getLocaleDate(long date) {
        return DateFormat.getDateInstance(DateFormat.DATE_FIELD, Locale.getDefault()).format(new Date(date));
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    public int getItemCount() {
        return devices.size();
    }

    /**
     * Method to refresh the current {@link #devices} list with a new list <br>
     * Any params required
     */
    @SuppressLint("NotifyDataSetChanged")
    public void refreshDevicesList(ArrayList<Device> devices) {
        this.devices = devices;
        notifyDataSetChanged();
    }

    /**
     * Method to get {@link #devices} instance <br>
     * Any params required
     *
     * @return {@link #devices} instance as {@link Collection} of {@link Device}
     **/
    public Collection<Device> getCurrentDevicesList() {
        return devices;
    }

    /**
     * The {@link DeviceView} is the view adapter for the {@link DevicesAdapter}
     *
     * @author Tecknobit - N7ghtm4r3
     * @see RecyclerView.ViewHolder
     * @see View.OnClickListener
     **/
    @SuppressLint("NonConstantResourceId")
    public static class DeviceView extends RecyclerView.ViewHolder implements View.OnClickListener {

        /**
         * {@code name} of the device -> as view instance type
         **/
        @ResId(id = R.id.deviceName)
        private final MaterialTextView name;

        /**
         * {@code deviceIconType} icon type of the device -> as view instance type
         **/
        @ResId(id = R.id.deviceIconType)
        private final ShapeableImageView deviceIconType;

        /**
         * {@code ip} of the device -> as view instance type
         **/
        @ResId(id = R.id.ipAddress)
        private final MaterialTextView ip;

        /**
         * {@code login} of the device -> as view instance type
         **/
        @ResId(id = R.id.loginDate)
        private final MaterialTextView login;

        /**
         * {@code lastActivity} last activity of the device -> as view instance type
         **/
        @ResId(id = R.id.lastActivity)
        private final MaterialTextView lastActivity;

        /**
         * {@code relActions} view where are contained actions buttons
         **/
        @ResId(id = R.id.relActions)
        private final RelativeLayout relActions;

        /**
         * {@code unblacklistBtn} button to unblacklist a {@link Device}
         **/
        @ResId(id = R.id.unblacklistBtn)
        private final MaterialButton unblacklistBtn;

        /**
         * {@inheritDoc}
         **/
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
                case R.id.blackListBtn -> {
                    // TODO: 23/12/2022 REQUEST THEN
                    Utils.showSnackbar(v, "BLACKLISTED");
                    relActions.setVisibility(View.GONE);
                    unblacklistBtn.setVisibility(View.VISIBLE);
                }
                case R.id.disconnectBtn -> {
                    // TODO: 23/12/2022 REQUEST THEN
                    Utils.showSnackbar(v, "DISCONNECTED");
                }
                case R.id.unblacklistBtn -> {
                    Utils.showSnackbar(v, "UNBLACKLISTED");
                    relActions.setVisibility(View.VISIBLE);
                    unblacklistBtn.setVisibility(View.GONE);
                }
            }
        }

    }

}
