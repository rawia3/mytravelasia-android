package com.twormobile.mytravelasia.philippines.http;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.twormobile.mytravelasia.philippines.db.MtaPhProvider;
import com.twormobile.mytravelasia.philippines.model.Poi;
import com.twormobile.mytravelasia.philippines.util.Log;

import java.util.HashMap;

/**
 * Calls the get feed webservice and saves the response to the content provider.
 *
 * @author avendael
 */
public class FeedListIntentService extends BaseIntentService {
    private static final String TAG = FeedListIntentService.class.getSimpleName();

    /**
     * Name of the broadcast event sent after an attempt to retrieve feeds from the server.
     */
    public static final String BROADCAST_GET_FEED_LIST = "get_feed_list";

    /**
     * Name of the broadcast message sent after successfully retrieving feeds from the server. The message format is an
     * array of longs that contains the following:
     *
     * <ul>
     *     <li>The requested page number</li>
     *     <li>Total number of pages</li>
     * </ul>
     */
    public static final String BROADCAST_GET_FEED_LIST_SUCCESS = "get_feed_list_success";

    /**
     * Name of the broadcast message sent after a failed attempt to retrieve feeds from the server. The message
     * contains whatever failure message is received by the HTTP client.
     */
    public static final String BROADCAST_GET_FEED_LIST_FAILED = "get_feed_list_failed";

    /**
     * Key to use for the intent extra to tell {@link FeedListIntentService} which page to fetch.
     */
    public static final String EXTRAS_FEED_FETCH_PAGE = "com.twormobile.mytravelasia.extras.fetch_page";

    /**
     * Key to use for the intent extra to tell {@link FeedListIntentService} which list of POIs to fetch.
     */
    public static final String EXTRAS_FEED_TYPE = "com.twormobile.mytravelasia.extras.fetch_type";

    /**
     * Key to use for the intent extra to tell {@link FeedListIntentService} the user's current coordinates.
     */
    public static final String EXTRAS_COORDS = "com.twormobile.mytravelasia.extras.coords";

    /**
     * Tell {@link FeedListIntentService} to fetch news.
     */
    public static final int FEED_TYPE_NEWS = 0;

    /**
     * Tell {@link FeedListIntentService} to fetch recents.
     */
    public static final int FEED_TYPE_RECENT = 1;

    /**
     * Tell {@link FeedListIntentService} to fetch most viewed POIs.
     */
    public static final int FEED_TYPE_MOST_VIEWED = 2;

    /**
     * Tell {@link FeedListIntentService} to fetch featured POIs.
     */
    public static final int FEED_TYPE_FEATURED = 3;

    /**
     * Tell {@link FeedListIntentService} to fetch POIs nearby.
     */
    public static final int FEED_TYPE_NEARBY = 4;

    public FeedListIntentService() {
        super(TAG);
    }

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public FeedListIntentService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(final Intent intent) {
        Log.d(TAG, "handling intent");
        final long page = intent.getLongExtra(EXTRAS_FEED_FETCH_PAGE, 1L);
        final int feed = intent.getIntExtra(EXTRAS_FEED_TYPE, FEED_TYPE_NEWS);
        HashMap<String, String> params = new HashMap<String, String>();
        Response.Listener<FeedResponse> successListener = getFeedResponseListener(page);
        Response.ErrorListener errorListener = getErrorListener();
        String url;

        params.put(HttpConstants.PARAM_COUNTRY_NAME, "Philippines");
        params.put(HttpConstants.PARAM_PAGE, Long.toString(page));

        switch (feed) {
            case FEED_TYPE_FEATURED:
                url = String.format(HttpConstants.BASE_URL + HttpConstants.FEATURED_FEED_RESOURCE + "&"
                        + HttpConstants.PARAM_COUNTRY_NAME + "=%1$s&"
                        + HttpConstants.PARAM_PAGE + "=%2$s",
                        "Philippines", page);
                break;
            case FEED_TYPE_MOST_VIEWED:
                url = String.format(HttpConstants.BASE_URL + HttpConstants.MOST_VIEWED_FEED_RESOURCE + "&"
                        + HttpConstants.PARAM_COUNTRY_NAME + "=%1$s&"
                        + HttpConstants.PARAM_PAGE + "=%2$s",
                        "Philippines", page);
                break;
            case FEED_TYPE_RECENT:
                url = String.format(HttpConstants.BASE_URL + HttpConstants.RECENT_FEED_RESOURCE + "&"
                        + HttpConstants.PARAM_COUNTRY_NAME + "=%1$s&"
                        + HttpConstants.PARAM_PAGE + "=%2$s",
                        "Philippines", page);

                break;
            case FEED_TYPE_NEARBY:
                double[] coords = intent.getDoubleArrayExtra(EXTRAS_COORDS);

                if (null == coords) return;

                Log.d(TAG, "coords " + coords[0] + " " + coords[1]);
                url = String.format(HttpConstants.BASE_URL + HttpConstants.NEARBY_FEED_RESOURCE + "&"
                        + HttpConstants.PARAM_COUNTRY_NAME + "=%1$s&"
                        + HttpConstants.PARAM_LATITUDE + "=%2$s&"
                        + HttpConstants.PARAM_LONGITUDE + "=%3$s&"
                        + HttpConstants.PARAM_PAGE + "=%4$s",
                        "Philippines", coords[0], coords[1], page);

                break;
            case FEED_TYPE_NEWS:
            default:
                url = String.format(HttpConstants.BASE_URL + HttpConstants.NEWS_FEED_RESOURCE + "&"
                        + HttpConstants.PARAM_COUNTRY_NAME + "=%1$s&"
                        + HttpConstants.PARAM_PAGE + "=%2$s",
                        "Philippines", page);
        }

        GsonRequest<FeedResponse> gsonRequest = new GsonRequest<FeedResponse>(
                url, FeedResponse.class, null, params, successListener, errorListener);

        mRequestQueue.add(gsonRequest);
    }

    private Response.Listener<FeedResponse> getFeedResponseListener(final long page) {
        return new Response.Listener<FeedResponse>() {
                @Override
                public void onResponse(FeedResponse response) {
                    if (null == response) return;

                    ContentResolver contentResolver = getContentResolver();

                    if (1L == page) {
                        contentResolver.delete(MtaPhProvider.POI_URI, null, null);
                    }

                    Log.d(TAG, "the response is " + response);
                    for (Poi poi : response.getFeeds()) {
                        Log.d(TAG, "poi name: " + poi.getFeedName() + " poi id: " + poi.getResourceId() + " created_at " + poi.getCreatedAt());
                        ContentValues values = new ContentValues();

                        values.put(Poi.RESOURCE_ID, poi.getResourceId());
                        values.put(Poi.FEED_NAME, poi.getFeedName());
                        values.put(Poi.NAME, poi.getName());
                        values.put(Poi.ADDRESS, poi.getAddress());
                        values.put(Poi.CONTENT, poi.getContent());
                        values.put(Poi.FB_USER_PROFILE_ID, poi.getFbUserProfileId());
                        values.put(Poi.FB_USER_PROFILE_NAME, poi.getFbUserProfileName());
                        values.put(Poi.LONGITUDE, poi.getLongitude());
                        values.put(Poi.LATITUDE, poi.getLatitude());
                        values.put(Poi.FEED_TYPE, poi.getFeedType());
                        values.put(Poi.POI_TYPE, poi.getPoiType());
                        values.put(Poi.IMAGE_THUMB_URL, poi.getImageThumbUrl());
                        values.put(Poi.ANNOTATION_TYPE, poi.getAnnotationType());
                        values.put(Poi.TOTAL_COMMENTS, poi.getTotalComments());
                        values.put(Poi.TOTAL_LIKES, poi.getTotalLikes());

                        if (null != poi.getCreatedAt()) {
                            values.put(Poi.CREATED_AT, poi.getCreatedAt().getTime());
                        } else {
                            values.put(Poi.CREATED_AT, System.currentTimeMillis());
                        }

                        contentResolver.insert(MtaPhProvider.POI_URI, values);
                    }

                    Intent broadcastIntent = new Intent(BROADCAST_GET_FEED_LIST);

                    broadcastIntent.putExtra(BROADCAST_GET_FEED_LIST_SUCCESS,
                            new long[]{page, response.getTotalPages()});
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(broadcastIntent);
                }
            };
    }

    private Response.ErrorListener getErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "volley error: " + error.toString());
                broadcastFailure(BROADCAST_GET_FEED_LIST, BROADCAST_GET_FEED_LIST_FAILED, error.toString());
            }
        };
    }
}
