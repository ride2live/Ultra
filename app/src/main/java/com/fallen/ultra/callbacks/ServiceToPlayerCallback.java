package com.fallen.ultra.callbacks;

/**
 * Created by Nolwe on 15.04.2015.
 */
public interface ServiceToPlayerCallback {
    boolean onSocketCreated();
    void onBuffered();

    void onPhoneAction(int intExtra);

    void onNoisy();

    boolean setCanceled();

    boolean stopAndReleasePlayer();
}
