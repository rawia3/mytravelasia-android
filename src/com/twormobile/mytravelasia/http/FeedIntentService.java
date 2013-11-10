package com.twormobile.mytravelasia.http;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.twormobile.mytravelasia.db.MtaPhProvider;
import com.twormobile.mytravelasia.model.Poi;
import com.twormobile.mytravelasia.util.Log;

/**
 * Calls the get feed webservice and saves the response to the content provider.
 *
 * @author avendael
 */
public class FeedIntentService extends IntentService {
    private static final String TAG = FeedIntentService.class.getSimpleName();

    /**
     * Name of the broadcast event sent after an attempt to retrieve feeds from the server.
     */
    public static final String BROADCAST_GET_FEED = "get_feed";

    /**
     * Name of the broadcast message sent after successfully retrieving feeds from the server. The message format is an
     * array of longs that contains the following:
     *
     * <ul>
     *     <li>The requested page number</li>
     *     <li>Total number of pages</li>
     * </ul>
     */
    public static final String BROADCAST_GET_FEED_SUCCESS = "get_feed_success";

    /**
     * Name of the broadcast message sent after a failed attempt to retrieve feeds from the server. The message
     * contains whatever failure message is received by the HTTP client.
     */
    public static final String BROADCAST_GET_FEED_FAILED = "get_feed_failed";

    /**
     * Key to use for the intent extra to tell {@link com.twormobile.mytravelasia.http.FeedIntentService} which page to
     * fetch.
     */
    public static final String EXTRAS_FEED_FETCH_PAGE = "com.twormobile.mytravelasia.extras.fetch_page";

    public FeedIntentService() {
        super(TAG);
    }

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public FeedIntentService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(final Intent intent) {
        Log.d(TAG, "handling intent");
        final long page = intent.getLongExtra(EXTRAS_FEED_FETCH_PAGE, 1);

        FeedHttpClient.getFeeds(new AsyncHttpResponseHandler() {
            @Override
            public void onFailure(Throwable error, String content) {
                Log.d(TAG, "response failed");
                broadcastFailure(content);
            }

            @Override
            public void onSuccess(String content) {
                if (null == content) {
                    broadcastFailure("loopj error: null content"); // TODO: Define error message protocol
                }

                Log.d(TAG, "response is " + content);
                FeedResponse feedResponse = FeedHttpClient.getFeedGsonParser().fromJson(content, FeedResponse.class);

                Intent broadcastIntent = new Intent(BROADCAST_GET_FEED);

                broadcastIntent.putExtra(BROADCAST_GET_FEED_SUCCESS, new long[]{page, feedResponse.getTotalPages()});
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(broadcastIntent);

                for (Poi poi : feedResponse.getFeeds()) {
                    Log.d(TAG, "poi name: " + poi.getName());
                    ContentValues values = new ContentValues();

                    values.put(Poi.RESOURCE_ID, poi.getResourceId());
                    values.put(Poi.NAME, poi.getName());
                    values.put(Poi.ADDRESS, poi.getAddress());
                    values.put(Poi.CONTENT, poi.getContent());
                    values.put(Poi.FB_USER_PROFILE_ID, poi.getFbUserProfileId());
                    values.put(Poi.FB_USER_PROFILE_NAME, poi.getFbUserProfileName());
                    values.put(Poi.CREATED_AT, poi.getCreatedAt());
                    values.put(Poi.LONGITUDE, poi.getLongitude());
                    values.put(Poi.LATITUDE, poi.getLatitude());
                    values.put(Poi.FEED_TYPE, poi.getFeedType());
                    values.put(Poi.POI_TYPE, poi.getPoiType());
                    values.put(Poi.IMAGE_THUMB_URL, poi.getImageThumbUrl());
                    values.put(Poi.ANNOTATION_TYPE, poi.getAnnotationType());
                    values.put(Poi.TOTAL_COMMENTS, poi.getTotalComments());
                    values.put(Poi.TOTAL_LIKES, poi.getTotalLikes());

                    getContentResolver().insert(MtaPhProvider.POI_URI, values);
                }
            }
        }, page);
    }

    private void broadcastFailure(String message) {
        Intent broadcastIntent = new Intent(BROADCAST_GET_FEED);

        broadcastIntent.putExtra(BROADCAST_GET_FEED_FAILED, message);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(broadcastIntent);
    }
}
