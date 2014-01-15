package com.twormobile.mytravelasia.philippines.http;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * @author avendael
 */
public abstract class BaseFeedIntentService extends IntentService {
    protected RequestQueue mRequestQueue;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public BaseFeedIntentService(String name) {
        super(name);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mRequestQueue = Volley.newRequestQueue(this);
    }

    protected void broadcastFailure(String action, String key, String message) {
        Intent broadcastIntent = new Intent(action);

        broadcastIntent.putExtra(key, message);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(broadcastIntent);
    }
}
