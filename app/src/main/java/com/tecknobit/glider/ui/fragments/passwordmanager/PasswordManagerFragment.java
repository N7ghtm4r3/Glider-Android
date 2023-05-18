package com.tecknobit.glider.ui.fragments.passwordmanager;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.fragment.app.Fragment;

import com.google.android.material.textview.MaterialTextView;
import com.tecknobit.apimanager.annotations.Structure;
import com.tecknobit.apimanager.annotations.android.ResId;
import com.tecknobit.glider.R;
import com.tecknobit.glider.records.Device.DevicePermission;
import com.tecknobit.glider.ui.fragments.parents.FormFragment;
import com.tecknobit.glider.ui.fragments.parents.GliderFragment;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.tecknobit.glider.helpers.local.User.user;

/**
 * The {@link PasswordManagerFragment} is the super class where a fragment inherit the base methods to get data from a
 * form {@link GliderFragment} and manage the {@link DevicePermission#PASSWORD_MANAGER}'s behavior
 *
 * @author Tecknobit - N7ghtm4r3
 * @see GliderFragment
 * @see FormFragment
 */
@Structure
@SuppressLint("NonConstantResourceId")
public abstract class PasswordManagerFragment extends FormFragment {

    /**
     * {@code noAuthorizedText} text view to show when the user is not a {@link DevicePermission#PASSWORD_MANAGER}
     */
    @ResId(id = R.id.noAuthorizedText)
    protected MaterialTextView noAuthorizedText;

    /**
     * {@code relAuthorized} layout to show when the user is at least a {@link DevicePermission#PASSWORD_MANAGER}
     */
    @ResId(id = R.id.relAuthorized)
    protected RelativeLayout relAuthorized;

    /**
     * Method to set the initial layout of the {@link Fragment}
     *
     * @param container: the container view
     */
    protected void initManagerLayout(View container) {
        noAuthorizedText = container.findViewById(R.id.noAuthorizedText);
        relAuthorized = container.findViewById(R.id.relAuthorized);
        setFragmentBehavior();
    }

    /**
     * Method to set the behavior of the {@link Fragment} <br>
     * No-any params required
     */
    protected boolean setFragmentBehavior() {
        if (user.isPasswordManager()) {
            if (relAuthorized.getVisibility() == GONE) {
                noAuthorizedText.setVisibility(GONE);
                relAuthorized.setVisibility(VISIBLE);
            }
            return true;
        } else {
            noAuthorizedText.setVisibility(VISIBLE);
            relAuthorized.setVisibility(GONE);
            return false;
        }
    }

}
