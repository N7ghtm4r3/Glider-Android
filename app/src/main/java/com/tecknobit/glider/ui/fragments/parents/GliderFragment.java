package com.tecknobit.glider.ui.fragments.parents;

import android.view.View;

import androidx.fragment.app.Fragment;

import com.tecknobit.glider.helpers.local.ManageRequest;
import com.tecknobit.glider.helpers.local.User.Operation;

import org.json.JSONException;
import org.json.JSONObject;

import static com.tecknobit.glider.helpers.local.User.DEVICE_NAME;
import static com.tecknobit.glider.helpers.local.User.GliderKeys.ope;
import static com.tecknobit.glider.helpers.local.User.user;
import static com.tecknobit.glider.helpers.toImport.records.Device.DeviceKeys.name;
import static com.tecknobit.glider.helpers.toImport.records.Device.DeviceKeys.type;
import static com.tecknobit.glider.helpers.toImport.records.Device.Type.MOBILE;
import static com.tecknobit.glider.helpers.toImport.records.Session.SessionKeys.sessionPassword;

/**
 * The {@link GliderFragment} is the super class where a fragment inherit the base methods for the normal
 * {@code Glider}'s fragment workflow
 *
 * @author Tecknobit - N7ghtm4r3
 * @see Fragment
 * @see ManageRequest
 **/
public abstract class GliderFragment extends Fragment implements ManageRequest {

    /**
     * {@code response} instance to contains the response from the backend
     */
    protected JSONObject response;

    /**
     * {@code viewContainer} the main view container of the {@link Fragment}
     **/
    protected View viewContainer;

    /**
     * {@code payload} the payload to send with the request
     **/
    protected JSONObject payload;

    /**
     * Constructor to init {@link GliderFragment}
     *
     * @apiNote will be instantiated the {@link #payload}
     **/
    protected GliderFragment() {
        setBasePayload();
    }

    /**
     * Method to create the payload for a request
     *
     * @param operation: operation to create the payload
     * @param parameters : parameters to insert to invoke this method
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
     * Any params required
     */
    @Override
    public void setBasePayload() {
        try {
            payload = new JSONObject();
            payload.put(name.name(), DEVICE_NAME)
                    .put(type.name(), MOBILE)
                    .put(sessionPassword.name(), user.getSessionPassword());
        } catch (JSONException e) {
            payload = null;
        }
    }

    /**
     * Method to clear all views and set the {@link GliderFragment} in an initial state <br>
     * Any params required
     */
    protected abstract void clearViews();

    /**
     * Callback for when the primary navigation state of this Fragment has changed. This can be
     * the result of the {@link #getParentFragmentManager()}  containing FragmentManager} having its
     * primary navigation fragment changed via
     * {@link androidx.fragment.app.FragmentTransaction#setPrimaryNavigationFragment} or due to
     * the primary navigation fragment changing in a parent FragmentManager.
     *
     * @param isPrimaryNavigationFragment True if and only if this Fragment and any
     *                                    {@link #getParentFragment() parent fragment} is set as the primary navigation fragment
     *                                    via {@link androidx.fragment.app.FragmentTransaction#setPrimaryNavigationFragment}.
     * @apiNote if {@code "isPrimaryNavigationFragment"} is {@code "true"} will be called the
     * {@link #clearViews()} method to set the view in an initial state
     */
    @Override
    public void onPrimaryNavigationFragmentChanged(boolean isPrimaryNavigationFragment) {
        super.onPrimaryNavigationFragmentChanged(isPrimaryNavigationFragment);
        if (!isPrimaryNavigationFragment)
            clearViews();
    }

}
