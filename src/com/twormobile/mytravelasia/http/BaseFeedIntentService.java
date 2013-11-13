package com.twormobile.mytravelasia.http;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

/**
 * @author avendael
 */
public abstract class BaseFeedIntentService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public BaseFeedIntentService(String name) {
        super(name);
    }


    protected void broadcastFailure(String action, String key, String message) {
        Intent broadcastIntent = new Intent(action);

        broadcastIntent.putExtra(key, message);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(broadcastIntent);
    }
}
