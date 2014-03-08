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
 * An intent service for deleting comments.
 *
 * @author avendael
 */
public class DeleteCommentIntentService extends BaseIntentService {
    private static final String TAG = DeleteCommentIntentService.class.getSimpleName();

    /**
     * Name of the broadcast event sent after a comment is deleted.
     */
    public static final String BROADCAST_DELETE_COMMENT = "delete_comment";

    /**
     * Name of the broadcast message sent after a successful delete. This will contain the comment's poi id.
     */
    public static final String BROADCAST_DELETE_COMMENT_SUCCESS = "delete_comment_success";

    /**
     * Name of the broadcast message sent after a failed delete comment.
     */
    public static final String BROADCAST_DELETE_COMMENT_FAILED = "delete_comment_failed";

    /**
     * Key to use for the intent extra to tell {@link DeleteCommentIntentService} the POI ID to comment to.
     */
    public static final String EXTRAS_POI_ID = "com.twormobile.mytravelasia.extras.poi_id";

    /**
     * Key to use for the intent extra to tell {@link DeleteCommentIntentService} the profile ID of the user who
     * made the comment.
     */
    public static final String EXTRAS_PROFILE_ID = "com.twormobile.mytravelasia.extras.profile_id";

    /**
     * Key to use for the intent extra to tell {@link DeleteCommentIntentService} the ID of the comment to be deleted.
     */
    public static final String EXTRAS_COMMENT_ID = "com.twormobile.mytravelasia.extras.comment_id";

    public DeleteCommentIntentService() {
        super(TAG);
    }

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public DeleteCommentIntentService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "handling delete comment intent");

        final long poiId = intent.getLongExtra(EXTRAS_POI_ID, 0);
        final long commentId = intent.getLongExtra(EXTRAS_COMMENT_ID, 0);
        final String profileId = intent.getStringExtra(EXTRAS_PROFILE_ID);
        HashMap<String, String> params = new HashMap<String, String>();
        Response.Listener<DeleteCommentResponse> successListener = getDeleteCommentResponseListener(poiId);
        Response.ErrorListener errorListener = getErrorListener();
        String url = HttpConstants.BASE_URL + HttpConstants.COMMENTS_RESOURCE + "/" + commentId + ".json";

        params.put(HttpConstants.PARAM_PROFILE_ID, profileId);
        params.put(HttpConstants.PARAM_POI_ID, Long.toString(poiId));
        params.put(HttpConstants.PARAM_COMMENT_ID, Long.toString(commentId));

        GsonRequest<DeleteCommentResponse> gsonRequest = new GsonRequest<DeleteCommentResponse>(
                url, DeleteCommentResponse.class, null, params, successListener, errorListener, Request.Method.POST);

        gsonRequest.setRetryPolicy(new DefaultRetryPolicy(
                HttpConstants.TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        mRequestQueue.add(gsonRequest);
    }

    private Response.Listener<DeleteCommentResponse> getDeleteCommentResponseListener(final long poiId) {
        return new Response.Listener<DeleteCommentResponse>() {
            @Override
            public void onResponse(DeleteCommentResponse response) {
                Log.d(TAG, "response is " + response);

                if (null == response || !response.isValid()) {
                    String message = null != response ? response.getMessage() : "No response";
                    broadcastFailure(BROADCAST_DELETE_COMMENT, BROADCAST_DELETE_COMMENT_FAILED, message);

                    return;
                }
                Log.d(TAG, "message is " + response.getMessage());

                Intent broadcastIntent = new Intent(BROADCAST_DELETE_COMMENT);

                broadcastIntent.putExtra(BROADCAST_DELETE_COMMENT_SUCCESS, poiId);
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(broadcastIntent);
            }
        };
    }

    private Response.ErrorListener getErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                broadcastFailure(BROADCAST_DELETE_COMMENT, BROADCAST_DELETE_COMMENT_FAILED, error.toString());
            }
        };
    }
}
