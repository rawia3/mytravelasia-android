package com.twormobile.mytravelasia.philippines.http;

/**
 * Retrieves feeds from the REST webservice.
 *
 * @author avendael
 */
public class HttpConstants {
    public static final String BASE_URL = "http://www.mytravel-asia.com/";

    public static final String PARAM_COUNTRY_NAME = "country_name";
    public static final String PARAM_PROFILE_ID = "profile_id";
    public static final String PARAM_PAGE = "page";

    public static final String FEED_RESOURCE = "feed.json";
    public static final String POI_RESOURCE = "pois/";

    private HttpConstants() {}
}
