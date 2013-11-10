package com.twormobile.mytravelasia.http;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;

/**
 * Retrieves feeds from the REST webservice.
 *
 * @author avendael
 */
public class FeedHttpClient {
    private static final String BASE_URL = "http://www.mytravel-asia.com/";
    public static final String PARAM_COUNTRY_NAME = "country_name";
    public static final String PARAM_PAGE = "page";

    private static SyncHttpClient sHttpClient = new SyncHttpClient() {
        @Override
        public String onRequestFailed(Throwable error, String content) {
            return content;
        }
    };

    public static void getFeeds(AsyncHttpResponseHandler responseHandler, long page) {
        RequestParams params = new RequestParams();

        params.put(PARAM_COUNTRY_NAME, "Philippines");
        params.put(PARAM_PAGE, page > 0 ? Long.toString(page) : "1");

        sHttpClient.get(BASE_URL + "feed.json", params, responseHandler);
    }

    public static Gson getFeedGsonParser() {
        return new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .excludeFieldsWithoutExposeAnnotation().create();
    }
}
