package com.tecknobit.glider.ui.fragments;

import androidx.fragment.app.Fragment;

/**
 * The {@link GliderFragment} is the super class where a fragment inherit the base methods for the normal
 * {@code Glider}'s fragment workflow
 *
 * @author Tecknobit - N7ghtm4r3
 * @see Fragment
 **/
public abstract class GliderFragment extends Fragment {

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
