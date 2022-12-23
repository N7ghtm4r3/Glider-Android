package com.tecknobit.glider.ui.fragments.parents;

import android.content.Context;
import android.os.Handler;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

/**
 * The {@link RealtimeRecyclerFragment} is the super class where a fragment inherit the base methods to refresh
 * data in realtime mode by a {@link RecyclerView}
 *
 * @author Tecknobit - N7ghtm4r3
 * @see GliderFragment
 **/
public abstract class RealtimeRecyclerFragment extends GliderFragment {

    /**
     * {@code handler} allows to manage the {@link #runnable}'s workflow
     **/
    protected static final Handler handler = new Handler();

    /**
     * {@code runnable} allows to manage the data refreshing workflow
     **/
    protected Runnable runnable;

    /**
     * {@code swipeRefreshLayout} view to manually refresh the {@link #recyclerManager}'s view, reloading
     * the data
     **/
    protected SwipeRefreshLayout swipeRefreshLayout;

    /**
     * {@code recyclerManager} allows to manage the list of the data to show
     **/
    protected RecyclerView recyclerManager;

    /**
     * {@code currentRecyclerSize} the current {@link #recyclerManager} size to avoid the useless
     * refreshing of the view
     **/
    protected int currentRecyclerSize;

    /**
     * Method to set the {@link #recyclerManager}
     *
     * @param recyclerId: identifier of the {@link #recyclerManager}
     * @param context:    context where the {@link #recyclerManager} is shown
     **/
    protected void setRecycler(int recyclerId, Context context) {
        recyclerManager = viewContainer.findViewById(recyclerId);
        recyclerManager.setLayoutManager(new LinearLayoutManager(context));
    }

    /**
     * Method to load the {@link #recyclerManager} with the data to show <br>
     * Any params required
     **/
    protected void loadRecycler() {
        stopRunnable();
        currentRecyclerSize = -1;
    }

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
