package com.twormobile.mytravelasia.model;

import android.provider.BaseColumns;

/**
 * This class serves as a model that maps both to a db table and webservice data. Exposed members are used by GSON to
 * parse POI data from the webservice.
 *
 * @author avendael
 */
public class Poi {
    /**
     * The name of the table being represented by this class.
     */
    public static final String TABLE_NAME = "poi";

    /**
     * Column: ID of the POI item from the remote resource. This is different from the row id stored in the local database.
     */
    public static final String RESOURCE_ID = "resource_id";

    /**
     * Column: POI's name. Usually a name of a place.
     */
    public static final String NAME = "name";

    /**
     * Column: POI's location in human readable form.
     */
    public static final String ADDRESS = "address";

    /**
     * Column: Facebook profile ID of the user that created the POI.
     */
    public static final String FB_USER_PROFILE_ID = "fb_user_profile_id";

    /**
     * Column: Facebook profile name of the user that created the POI.
     */
    public static final String FB_USER_PROFILE_NAME = "fb_user_profile_name";

    /**
     * Column: The date when the POI was created in the remote resource. Creation date in the local db are not being recorded.
     */
    public static final String CREATED_AT = "created_at";

    /**
     * Column: Content that was applied by the FB user.
     */
    public static final String CONTENT = "content";

    /**
     * Column: Feed type. Can either be "like" or "comment".
     */
    public static final String FEED_TYPE = "feed_type";

    /**
     * Column: Location of the POI in latitude.
     */
    public static final String LATITUDE = "latitude";

    /**
     * Column: Location of the POI in longitude.
     */
    public static final String LONGITUDE = "longitude";

    /**
     * Column: Type of POI. Can either be "attraction", "hotel", etc.
     */
    public static final String POI_TYPE = "poi_type";

    /**
     * Column: Image URL of the image thumbnail.
     */
    public static final String IMAGE_THUMB_URL = "image_thumb_url";

    /**
     * Column: An annotation that indicates where the feed should belong.
     */
    public static final String ANNOTATION_TYPE = "annotation_type";

    /**
     * Column: Total number of likes in the feed.
     */
    public static final String TOTAL_LIKES = "total_likes";

    /**
     * Column: Total number of comments in the feed.
     */
    public static final String TOTAL_COMMENTS = "total_comments";

    /**
     * SQL command to create the POI table.
     */
    public static final String SQL_CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " ("
            + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + RESOURCE_ID + " INTEGER, "
            + NAME + " TEXT, "
            + ADDRESS + " TEXT, "
            + CONTENT + "TEXT, "
            + FB_USER_PROFILE_ID + " TEXT, "
            + FB_USER_PROFILE_NAME + " TEXT, "
            + CREATED_AT + " TEXT, "
            + FEED_TYPE + " TEXT, "
            + LATITUDE + " REAL, "
            + LONGITUDE + " REAL, "
            + POI_TYPE + " TEXT, "
            + IMAGE_THUMB_URL + " TEXT, "
            + ANNOTATION_TYPE + " TEXT, "
            + TOTAL_LIKES + " INTEGER, "
            + TOTAL_COMMENTS + " INTEGER"
            + ");";

    /**
     * SQL command to destroy the table.
     */
    public static final String SQL_DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
}
