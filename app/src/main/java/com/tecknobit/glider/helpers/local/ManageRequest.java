package com.tecknobit.glider.helpers.local;

import com.tecknobit.glider.helpers.local.User.Operation;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * The {@link ManageRequest} is the interface to manage the communication with the backend
 *
 * @author Tecknobit - N7ghtm4r3
 **/
public interface ManageRequest {

    /**
     * {@code executor} instance to manage the user communication with the backend with background
     * process
     */
    ExecutorService executor = Executors.newSingleThreadExecutor();

    /**
     * Method to create the payload for a request
     *
     * @param operation: operation to create the payload
     * @param parameters : parameters to insert to invoke this method
     */
    <T> void setRequestPayload(Operation operation, T... parameters);

    /**
     * Method to set the base payload for a request <br>
     * No-any params required
     */
    void setBasePayload();

}
