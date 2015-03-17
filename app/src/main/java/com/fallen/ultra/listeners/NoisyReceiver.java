package com.fallen.ultra.listeners;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.fallen.ultra.services.UltraPlayerService;
import com.fallen.ultra.utils.Params;
import com.fallen.ultra.utils.UtilsUltra;

/**
 * Created by Nolwe on 17.03.2015.
 */
public class NoisyReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null && intent.getAction().equals(android.media.AudioManager.ACTION_AUDIO_BECOMING_NOISY))
        {
            UtilsUltra.printLog("Noisy received");
            Intent serviceIntent = new Intent(context, UltraPlayerService.class);
            serviceIntent.putExtra(Params.ACTION_FROM_BROADCAST_NOISY, Params.ACTION_NOISY);
            if (context !=null)
                context.startService(serviceIntent);
        }


    }
}
