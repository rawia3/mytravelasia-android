package com.twormobile.mytravelasia.philippines.http;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.twormobile.mytravelasia.philippines.util.Log;

import java.util.HashMap;

/**
 * Calls the like/unlike webservice on a selected POI.
 *
 * @author avendael
 */
public class LikeIntentService extends BaseIntentService {
    private static final String TAG = LikeIntentService.class.getSimpleName();

    /**
     * Name of the broadcast event sent after an attempt to like/unlike a POI.
     */
    public static final String BROADCAST_LIKE_POI = "like_poi";

    /**
     * Name of the broadcast message sent after a successful like.
     */
    public static final String BROADCAST_LIKE_SUCCESS = "like_success";

    /**
     * Name of the broadcast message after a failed like.
     */
    public static final String BROADCAST_LIKE_FAILED = "like_failed";

    /**
     * Key to use for the intent extra to tell {@link LikeIntentService} the POI ID to like.
     */
    public static final String EXTRAS_POI_ID = "com.twormobile.mytravelasia.extras.poi_id";

    /**
     * Key to use for the intent extra to tell {@link LikeIntentService} the user's profile ID.
     */
    public static final String EXTRAS_PROFILE_ID = "com.twormobile.mytravelasia.extras.profile_id";

    /**
     * Key to use for the intent extra to tell {@link LikeIntentService} to like or unlike the POI.
     */
    public static final String EXTRAS_IS_LIKE = "com.twormobile.mytravelasia.extras.is_like";

    public LikeIntentService() {
        super(TAG);
    }

    public LikeIntentService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "handling register intent");
        final String profileId = intent.getStringExtra(EXTRAS_PROFILE_ID);
        final long poiId = intent.getLongExtra(EXTRAS_POI_ID, 0);
        final boolean isLike = intent.getBooleanExtra(EXTRAS_IS_LIKE, true);
        HashMap<String, String> params = new HashMap<String, String>();
        Response.Listener<LikeResponse> successListener = getLikeResponseListener();
        Response.ErrorListener errorListener = getErrorListener();
        String url = String.format(HttpConstants.BASE_URL + HttpConstants.POI_RESOURCE
                + poiId
                + (isLike ? "/unlike" : "/like")
                + "?format=json&" + HttpConstants.PARAM_PROFILE_ID + "=%1$s",
                profileId);

        params.put(HttpConstants.PARAM_PROFILE_ID, profileId);

        Log.d(TAG, "like url " + url);
        GsonRequest<LikeResponse> gsonRequest = new GsonRequest<LikeResponse>(url, LikeResponse.class, null, params,
                successListener, errorListener);

        mRequestQueue.add(gsonRequest);
    }

    private Response.Listener<LikeResponse> getLikeResponseListener() {
        return new Response.Listener<LikeResponse>() {
            @Override
            public void onResponse(LikeResponse response) {
                Log.d(TAG, "response is " + response);

                if (null == response) {
                    broadcastFailure(BROADCAST_LIKE_POI, BROADCAST_LIKE_FAILED, "No response");

                    return;
                }

                Intent broadcastIntent = new Intent(BROADCAST_LIKE_POI);

                broadcastIntent.putExtra(BROADCAST_LIKE_SUCCESS, response.isLiked());
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(broadcastIntent);
            }
        };
    }

    private Response.ErrorListener getErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "volley error: " + error.toString());
                broadcastFailure(BROADCAST_LIKE_POI, BROADCAST_LIKE_FAILED, error.toString());
            }
        };
    }
}
