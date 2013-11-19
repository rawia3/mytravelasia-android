package com.twormobile.mytravelasia.http;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.twormobile.mytravelasia.util.Log;

/**
 * Calls the get feed details webservice and saves the response to the content provider.
 *
 * @author avendael
 */
public class FeedDetailIntentService extends BaseFeedIntentService {
    private static final String TAG = FeedDetailIntentService.class.getSimpleName();

    /**
     * Name of the broadcast event sent after an attempt to retrieve the feed details from the server.
     */
    public static final String BROADCAST_GET_FEED_DETAIL = "get_feed_detail";

    /**
     * Name of the broadcast message sent after successfully retrieving feed details from the server. The message
     * contains the response from the server (tentative).
     */
    public static final String BROADCAST_GET_FEED_DETAIL_SUCCESS = "get_feed_detail_success";

    /**
     * Name of the broadcast message sent after a failed attempt to retrieve feeds from the server. The message
     * contains whatever failure message is received by the HTTP client.
     */
    public static final String BROADCAST_GET_FEED_DETAIL_FAILED = "get_feed_detail_failed";

    /**
     * Key to use for the intent extra to tell {@link com.twormobile.mytravelasia.http.FeedDetailIntentService} which
     * feed details to fetch.
     */
    public static final String EXTRAS_FEED_ID = "com.twormobile.mytravelasia.extras.feed_id";

    /**
     * Key to use for the intent extra to tell {@link com.twormobile.mytravelasia.http.FeedDetailIntentService} which
     * feed profile id to fetch.
     */
    public static final String EXTRAS_PROFILE_ID = "com.twormobile.mytravelasia.extras.feed_profile_id";

    public FeedDetailIntentService() {
        super(TAG);
    }

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public FeedDetailIntentService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(final Intent intent) {
        Log.d(TAG, "handling feed detail intent");
        final long feedId = intent.getLongExtra(EXTRAS_FEED_ID, -1);

        Log.d(TAG, "feed id to fetch " + feedId);
        Log.d(TAG, "request queue is null " + (mRequestQueue == null));
        if (feedId == -1) {
            broadcastFailure(BROADCAST_GET_FEED_DETAIL, BROADCAST_GET_FEED_DETAIL_FAILED, "Invalid feed id");
            return;
        }

        GsonRequest<FeedDetailResponse> gsonRequest = new GsonRequest<FeedDetailResponse>(
                HttpConstants.BASE_URL + HttpConstants.POI_RESOURCE + feedId + ".json", FeedDetailResponse.class,
                null, null,
                new Response.Listener<FeedDetailResponse>() {
                    @Override
                    public void onResponse(FeedDetailResponse response) {
                        Log.d(TAG, "the response is " + response.toString());
                        Intent broadcastIntent = new Intent(BROADCAST_GET_FEED_DETAIL);

                        broadcastIntent.putExtra(BROADCAST_GET_FEED_DETAIL_SUCCESS, response.toString()); // TODO: IPC might truncate this when the content is too long. Include this in a cache, or pass a Parcelable message
                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(broadcastIntent);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "volley error: " + error.toString());
                        broadcastFailure(BROADCAST_GET_FEED_DETAIL, BROADCAST_GET_FEED_DETAIL_FAILED, error.toString());
                    }
                });

        mRequestQueue.add(gsonRequest);
    }
}
