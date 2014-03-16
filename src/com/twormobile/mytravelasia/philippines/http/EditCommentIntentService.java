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
 * An intent service for editing comments.
 *
 * @author avendael
 */
public class EditCommentIntentService extends BaseIntentService {
    private static final String TAG = EditCommentIntentService.class.getSimpleName();

    /**
     * Name of the broadcast event sent after a comment is changed.
     */
    public static final String BROADCAST_EDIT_COMMENT = "edit_comment";

    /**
     * Name of the broadcast message sent after a successful edit. This will contain the comment's poi id.
     */
    public static final String BROADCAST_EDIT_COMMENT_SUCCESS = "edit_comment_success";

    /**
     * Name of the broadcast message sent after a failed edit comment.
     */
    public static final String BROADCAST_EDIT_COMMENT_FAILED = "edit_comment_failed";

    /**
     * Key to use for the intent extra to tell {@link EditCommentIntentService} the POI ID to comment to.
     */
    public static final String EXTRAS_POI_ID = "com.twormobile.mytravelasia.extras.poi_id";

    /**
     * Key to use for the intent extra to tell {@link EditCommentIntentService} the profile ID of the user who
     * made the comment.
     */
    public static final String EXTRAS_PROFILE_ID = "com.twormobile.mytravelasia.extras.profile_id";

    /**
     * Key to use for the intent extra to tell {@link EditCommentIntentService} the ID of the comment to be changed.
     */
    public static final String EXTRAS_COMMENT_ID = "com.twormobile.mytravelasia.extras.comment_id";

    /**
     * Key to use for the intent extra to tell {@link EditCommentIntentService} the comment content.
     */
    public static final String EXTRAS_COMMENT_CONTENT = "com.twormobile.mytravelasia.extras.comment_content";

    public EditCommentIntentService() {
        super(TAG);
    }

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public EditCommentIntentService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "handling edit comment intent");

        final long poiId = intent.getLongExtra(EXTRAS_POI_ID, 0);
        final long commentId = intent.getLongExtra(EXTRAS_COMMENT_ID, 0);
        final String profileId = intent.getStringExtra(EXTRAS_PROFILE_ID);
        final String content = intent.getStringExtra(EXTRAS_COMMENT_CONTENT);
        HashMap<String, String> params = new HashMap<String, String>();
        Response.Listener<CommentResponse> successListener = getEditCommentResponseListener(poiId);
        Response.ErrorListener errorListener = getErrorListener();
        String url = HttpConstants.BASE_URL + HttpConstants.COMMENTS_RESOURCE + "/" + commentId + ".json";

        params.put(HttpConstants.PARAM_PROFILE_ID, profileId);
        params.put(HttpConstants.PARAM_POI_ID, Long.toString(poiId));
        params.put(HttpConstants.PARAM_COMMENT_ID, Long.toString(commentId));
        params.put(HttpConstants.PARAM_CONTENT, content);

        GsonRequest<CommentResponse> gsonRequest = new GsonRequest<CommentResponse>(
                url, CommentResponse.class, null, params, successListener, errorListener, Request.Method.PUT);

        gsonRequest.setRetryPolicy(new DefaultRetryPolicy(
                HttpConstants.TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        mRequestQueue.add(gsonRequest);
    }

    private Response.Listener<CommentResponse> getEditCommentResponseListener(final long poiId) {
        return new Response.Listener<CommentResponse>() {
            @Override
            public void onResponse(CommentResponse response) {
                Log.d(TAG, "response is " + response);

                if (null == response || !response.isValid()) {
                    String message = null != response ? response.getMessage() : "No response";
                    broadcastFailure(BROADCAST_EDIT_COMMENT, BROADCAST_EDIT_COMMENT_FAILED, message);

                    return;
                }

                Log.d(TAG, "message is " + response.getMessage());

                Intent broadcastIntent = new Intent(BROADCAST_EDIT_COMMENT);

                broadcastIntent.putExtra(BROADCAST_EDIT_COMMENT_SUCCESS, poiId);
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(broadcastIntent);
            }
        };
    }

    private Response.ErrorListener getErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                broadcastFailure(BROADCAST_EDIT_COMMENT, BROADCAST_EDIT_COMMENT_FAILED, error.toString());
            }
        };
    }
}
