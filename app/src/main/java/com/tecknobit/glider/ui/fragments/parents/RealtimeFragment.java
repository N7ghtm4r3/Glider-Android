package com.tecknobit.glider.ui.fragments.parents;

import android.os.Handler;

/**
 * The {@link RealtimeFragment} is the super class where a fragment inherit the base methods to refresh
 * data {@link GliderFragment} in realtime mode
 *
 * @author Tecknobit - N7ghtm4r3
 * @see GliderFragment
 **/
public abstract class RealtimeFragment extends GliderFragment {

    /**
     * {@code handler} allows to manage the {@link #runnable}'s workflow
     **/
    protected static final Handler handler = new Handler();

    /**
     * {@code runnable} allows to manage the data refreshing workflow
     **/
    protected Runnable runnable;

    /**
     * {@inheritDoc}
     *
     * @apiNote when called the {@link #stopRunnable()} method will be called to stop
     * the current {@link #runnable}'s workflow
     **/
    @Override
    protected void clearViews() {
        stopRunnable();
    }

    /**
     * Method to stop the current {@link #runnable}'s workflow
     **/
    protected void stopRunnable() {
        if (runnable != null)
            handler.removeCallbacks(runnable);
    }

}
