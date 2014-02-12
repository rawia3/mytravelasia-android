package com.twormobile.mytravelasia.philippines.http;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.twormobile.mytravelasia.philippines.util.Log;

import java.util.HashMap;

/**
 * An intent service for creating comments.
 *
 * @author avendael
 */
public class CreateCommentIntentService extends BaseIntentService {
    private static final String TAG = CreateCommentIntentService.class.getSimpleName();

    /**
     * Name of the broadcast event sent after a comment is created.
     */
    public static final String BROADCAST_CREATE_COMMENT = "create_comment";

    /**
     * Name of the broadcast message sent after a successful comment. This message contains the total comments so far.
     */
    public static final String BROADCAST_CREATE_COMMENT_SUCCESS = "create_comment_success";

    /**
     * Name of the broadcast message after a failed create comment.
     */
    public static final String BROADCAST_CREATE_COMMENT_FAILED = "create_comment_failed";

    /**
     * Key to use for the intent extra to tell {@link CreateCommentIntentService} the POI ID to comment to.
     */
    public static final String EXTRAS_POI_ID = "com.twormobile.mytravelasia.extras.poi_id";

    /**
     * Key to use for the intent extra to tell {@link CreateCommentIntentService} the profile ID of the user who
     * made the comment.
     */
    public static final String EXTRAS_PROFILE_ID = "com.twormobile.mytravelasia.extras.profile_id";

    /**
     * Key to use for the intent extra to tell {@link CreateCommentIntentService} the comment content.
     */
    public static final String EXTRAS_COMMENT_CONTENT = "com.twormobile.mytravelasia.extras.comment_content";

    public CreateCommentIntentService() {
        super(TAG);
    }

    public CreateCommentIntentService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "handling comment intent");

        final long poiId = intent.getLongExtra(EXTRAS_POI_ID, 0);
        final String profileId = intent.getStringExtra(EXTRAS_PROFILE_ID);
        final String comment = intent.getStringExtra(EXTRAS_COMMENT_CONTENT);
        HashMap<String, String> params = new HashMap<String, String>();
        Response.Listener<CreateCommentResponse> successListener = getCommentResponseListener();
        Response.ErrorListener errorListener = getErrorListener();
        String url = HttpConstants.BASE_URL + HttpConstants.COMMENTS_RESOURCE + ".json";

        params.put(HttpConstants.PARAM_PROFILE_ID, profileId);
        params.put(HttpConstants.PARAM_POI_ID, Long.toString(poiId));
        params.put(HttpConstants.PARAM_CONTENT, comment);

        Log.d(TAG, "comment url " + url);
        GsonRequest<CreateCommentResponse> gsonRequest = new GsonRequest<CreateCommentResponse>(url,
                CreateCommentResponse.class, null, params, successListener, errorListener, Request.Method.POST);

        gsonRequest.setRetryPolicy(new DefaultRetryPolicy(
                HttpConstants.TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        mRequestQueue.add(gsonRequest);
    }

    private Response.Listener<CreateCommentResponse> getCommentResponseListener() {
        return new Response.Listener<CreateCommentResponse>() {
            @Override
            public void onResponse(CreateCommentResponse response) {
                Log.d(TAG, "response is " + response);

                if (null == response || !response.isValid()) {
                    String message = null != response ? response.getMessage() : "No response";
                    broadcastFailure(BROADCAST_CREATE_COMMENT, BROADCAST_CREATE_COMMENT_FAILED, message);

                    return;
                }
                Log.d(TAG, "message is " + response.getMessage());

                Intent broadcastIntent = new Intent(BROADCAST_CREATE_COMMENT);

                broadcastIntent.putExtra(BROADCAST_CREATE_COMMENT_SUCCESS, response.getTotalComments());
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(broadcastIntent);
            }
        };
    }

    private Response.ErrorListener getErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "volley error: " + error.toString());
                broadcastFailure(BROADCAST_CREATE_COMMENT, BROADCAST_CREATE_COMMENT_FAILED, error.toString());
            }
        };
    }
}
