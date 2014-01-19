package com.twormobile.mytravelasia.philippines.http;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.twormobile.mytravelasia.philippines.util.Log;

import java.util.HashMap;

/**
 * @author avendael
 */
public class RegisterIntentService extends BaseIntentService {
    private static final String TAG = RegisterIntentService.class.getSimpleName();

    /**
     * Name of the broadcast event sent after an attempt to register.
     */
    public static final String BROADCAST_REGISTER_MTA = "register_mta";

    /**
     * Name of the broadcast message sent after a successful registration. This message contains a string that
     * represents the user's Facebook profile id.
     */
    public static final String BROADCAST_REGISTER_SUCCESS = "register_success";

    /**
     * Name of the broadcast message sent after a failed attempt to register.
     */
    public static final String BROADCAST_REGISTER_FAILED = "register_failed";

    /**
     * Key to use for the intent extra to tell {@link RegisterIntentService} the user's first name.
     */
    public static final String EXTRAS_FIRST_NAME = "com.twormobile.mytravelasia.extras.first_name";

    /**
     * Key to use for the intent extra to tell {@link RegisterIntentService} the user's last name.
     */
    public static final String EXTRAS_LAST_NAME = "com.twormobile.mytravelasia.extras.last_name";

    /**
     * Key to use for the intent extra to tell {@link RegisterIntentService} the user's Facebook profile ID.
     */
    public static final String EXTRAS_PROFILE_ID = "com.twormobile.mytravelasia.extras.profile_id";

    /**
     * Key to use for the intent extra to tell {@link RegisterIntentService} the user's email.
     */
    public static final String EXTRAS_EMAIL = "com.twormobile.mytravelasia.extras.email";

    public RegisterIntentService() {
        super(TAG);
    }

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public RegisterIntentService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "handling register intent");
        final String firstName = intent.getStringExtra(EXTRAS_FIRST_NAME);
        final String lastName = intent.getStringExtra(EXTRAS_LAST_NAME);
        final String profileId = intent.getStringExtra(EXTRAS_PROFILE_ID);
        final String email = intent.getStringExtra(EXTRAS_EMAIL);
        HashMap<String, String> params = new HashMap<String, String>();

        params.put(HttpConstants.PARAM_FIRST_NAME, firstName);
        params.put(HttpConstants.PARAM_LAST_NAME, lastName);
        params.put(HttpConstants.PARAM_PROFILE_ID, profileId);
        params.put(HttpConstants.PARAM_EMAIL, email);
        params.put(HttpConstants.PARAM_TOKEN, "aaaaaaaa"); // Not sure what the token is for

        String url = String.format(HttpConstants.BASE_URL + HttpConstants.REGISTER_RESOURCE
                + "?" + HttpConstants.PARAM_FIRST_NAME + "=%1$s"
                + "&" + HttpConstants.PARAM_LAST_NAME + "=%2$s"
                + "&" + HttpConstants.PARAM_PROFILE_ID + "=%3s"
                + "&" + HttpConstants.PARAM_EMAIL + "=%4s"
                + "&" + HttpConstants.PARAM_TOKEN + "=%5s");

        Response.Listener<RegisterResponse> successListener = getRegisterResponseListener(profileId);
        Response.ErrorListener errorListener = getErrorListener();

        GsonRequest<RegisterResponse> gsonRequest = new GsonRequest<RegisterResponse>(
                url, RegisterResponse.class, null, params, successListener, errorListener);

        mRequestQueue.add(gsonRequest);
    }

    private Response.Listener<RegisterResponse> getRegisterResponseListener(final String profileId) {
        return new Response.Listener<RegisterResponse>() {
            @Override
            public void onResponse(RegisterResponse response) {
                Log.d(TAG, "response is " + response);

                if (null != response && null != response.getMessage())
                    broadcastFailure(BROADCAST_REGISTER_MTA, BROADCAST_REGISTER_FAILED, response.getMessage());

                Intent broadcastIntent = new Intent(BROADCAST_REGISTER_MTA);

                broadcastIntent.putExtra(BROADCAST_REGISTER_SUCCESS, profileId);
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(broadcastIntent);

            }
        };
    }

    private Response.ErrorListener getErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "volley error: " + error.toString());
                broadcastFailure(BROADCAST_REGISTER_MTA, BROADCAST_REGISTER_FAILED, error.toString());
            }
        };
    }

}
