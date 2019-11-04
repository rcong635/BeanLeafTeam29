package com.example.beanleafteam29;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
public class GeofenceBroadcastReceiver extends BroadcastReceiver {
    /**
     * Receives incoming intents.
     *
     * @param context the application context.
     * @param intent  sent by Location Services. This Intent is provided to Location
     *                Services (inside a PendingIntent) when addGeofences() is called.
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        // Enqueues a JobIntentService passing the context and intent as parameters
        GeofenceTransitionsJobIntentService.enqueueWork(context, intent);
    }
}
