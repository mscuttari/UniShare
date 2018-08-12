package it.unishare.client.layout;

import java.util.EventListener;

/**
 * Listener for confirmation dialog
 */
public interface ConfirmationDialogListener extends EventListener {

    /**
     * Called on confirmation dialog response
     *
     * @param   result      true for "Yes" answer; false for "No" answer
     */
    void onResult(boolean result);

}
