package com.fallen.ultra.callbacks;

import com.fallen.ultra.com.fallen.ultra.model.TempAsyncStatus;

/**
 * Created by Nolwe on 16.04.2015.
 */
public interface AsyncToServiceCallback {
    void onStatusChanged (TempAsyncStatus tempAsyncStatus);
}
