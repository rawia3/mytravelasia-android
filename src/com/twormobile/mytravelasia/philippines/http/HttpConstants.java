package com.twormobile.mytravelasia.philippines.http;

/**
 * Retrieves feeds from the REST webservice.
 *
 * @author avendael
 */
public class HttpConstants {
    public static final int TIMEOUT = 20000; // 20 seconds

    public static final String BASE_URL = "http://www.mytravel-asia.com/";

    public static final String PARAM_COUNTRY_NAME = "country_name";
    public static final String PARAM_PAGE = "page";
    public static final String PARAM_PROFILE_ID = "profile_id";
    public static final String PARAM_POI_ID = "poi_id";
    public static final String PARAM_COMMENT_ID = "comment_id";
    public static final String PARAM_CONTENT = "content";
    public static final String PARAM_FIRST_NAME = "first_name";
    public static final String PARAM_LAST_NAME = "last_name";
    public static final String PARAM_EMAIL = "email";
    public static final String PARAM_TOKEN = "token";

    public static final String NEWS_FEED_RESOURCE = "feed.json";
    public static final String RECENT_FEED_RESOURCE = "recent?format=json";
    public static final String MOST_VIEWED_FEED_RESOURCE = "most_viewed?format=json";
    public static final String FEATURED_FEED_RESOURCE = "featured?format=json";
    public static final String POI_RESOURCE = "pois/";
    public static final String REGISTER_RESOURCE = "mobile/register.json";
    public static final String COMMENTS_RESOURCE = "comments";

    private HttpConstants() {}
}
