package com.twormobile.mytravelasia.philippines.model;

import android.provider.BaseColumns;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * This class serves as a model that maps both to a db table and webservice data. Exposed members are used by GSON to
 * parse POI data from the webservice.
 *
 * @author avendael
 */
public class Poi implements BaseColumns {
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
     * Column: POI's feedName. Usually a name of a place.
     */
    public static final String FEED_NAME = "feed_name";

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
     * Column: Feed Remark. Can either be "Anonymous like this" or "Anynomous makes comment".
     */
    public static final String FEED_REMARK = "feed_remark";

    /**
     * Column: Location of the POI in latitude.
     */
    public static final String LATITUDE = "latitude";

    /**
     * Column: Location of the POI in longitude.
     */
    public static final String LONGITUDE = "longitude";

    /**
     * Column: Distance of the POI from the user.
     */
    public static final String DISTANCE = "distance";

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
            + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + RESOURCE_ID + " INTEGER, "
            + NAME + " TEXT,"
            + FEED_NAME + " TEXT,"
            + ADDRESS + " TEXT,"
            + CONTENT + " TEXT,"
            + FB_USER_PROFILE_ID + " TEXT,"
            + FB_USER_PROFILE_NAME + " TEXT,"
            + CREATED_AT + " INTEGER,"
            + FEED_TYPE + " TEXT,"
            + FEED_REMARK + " TEXT,"
            + LATITUDE + " REAL,"
            + LONGITUDE + " REAL,"
            + DISTANCE + " TEXT,"
            + POI_TYPE + " TEXT,"
            + IMAGE_THUMB_URL + " TEXT,"
            + ANNOTATION_TYPE + " TEXT,"
            + TOTAL_LIKES + " INTEGER,"
            + TOTAL_COMMENTS + " INTEGER"
            + ");";

    /**
     * SQL command to destroy the table.
     */
    public static final String SQL_DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    /**
     * ID of the POI from the local database.
     */
    private long id;

    /**
     * ID of the POI item from the remote resource. This is different from the row id stored in the local database.
     */
    @Expose
    @SerializedName("id")
    private long resourceId;

    /**
     * POI's name. Usually a name of a place.
     */
    @Expose
    @SerializedName("name")
    private String name;

    /**
     * POI's feedName. Usually a name of a place.
     */
    @Expose
    @SerializedName("poi_name")
    private String feedName;

    /**
     * POI's location in human readable form.
     */
    @Expose
    private String address;

    /**
     * Content that was applied by the FB user.
     */
    @Expose
    private String content;

    /**
     * Facebook profile ID of the user that created the POI.
     */
    @Expose
    @SerializedName("profile_id")
    private long fbUserProfileId;

    /**
     * Facebook profile name of the user that created the POI.
     */
    @Expose
    @SerializedName("user")
    private String fbUserProfileName;

    /**
     * The date when the POI was created in the remote resource. Creation date in the local db is not being recorded.
     */
    @Expose
    private Date createdAt;

    /**
     * Location of the POI in longitude.
     */
    @Expose
    private double longitude;

    /**
     * Location of the POI in latitude.
     */
    @Expose
    private double latitude;

    /**
     * Distance of the POI from the user.
     */
    @Expose
    private String distance;

    /**
     * Feed type. Can either be "like" or "comment".
     */
    @Expose
    private String feedType;

    /**
     * Column: Feed Remark. Can either be "Anonymous like this" or "Anynomous makes comment".
     */
    @Expose
    private String feedRemark;

    /**
     * Type of POI. Can either be "attraction", "hotel", etc.
     */
    @Expose
    private String poiType;

    /**
     * Image URL of the image thumbnail.
     */
    @Expose
    @SerializedName("picture_thumb_path")
    private String imageThumbUrl;

    /**
     * An annotation that indicates where the feed should belong.
     */
    @Expose
    private String annotationType;

    /**
     * Total number of likes in the feed.
     */
    @Expose
    private long totalLikes;

    /**
     * Total number of comments in the feed.
     */
    @Expose
    private long totalComments;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getResourceId() {
        return resourceId;
    }

    public void setResourceId(long resourceId) {
        this.resourceId = resourceId;
    }

    public String getFeedName() {
        return feedName;
    }

    public void setFeedName(String feedName) {
        this.feedName = feedName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getFbUserProfileId() {
        return fbUserProfileId;
    }

    public void setFbUserProfileId(long fbUserProfileId) {
        this.fbUserProfileId = fbUserProfileId;
    }

    public String getFbUserProfileName() {
        return fbUserProfileName;
    }

    public void setFbUserProfileName(String fbUserProfileName) {
        this.fbUserProfileName = fbUserProfileName;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) { this.distance = distance; }

    public String getFeedType() {
        return feedType;
    }

    public void setFeedType(String feedType) {
        this.feedType = feedType;
    }

    public String getFeedRemark() { return feedRemark; }

    public void setFeedRemark(String feedRemark) { this.feedRemark = feedRemark; }

    public String getPoiType() {
        return poiType;
    }

    public void setPoiType(String poiType) {
        this.poiType = poiType;
    }

    public String getImageThumbUrl() {
        return imageThumbUrl;
    }

    public void setImageThumbUrl(String imageThumbUrl) {
        this.imageThumbUrl = imageThumbUrl;
    }

    public String getAnnotationType() {
        return annotationType;
    }

    public void setAnnotationType(String annotationType) {
        this.annotationType = annotationType;
    }

    public long getTotalLikes() {
        return totalLikes;
    }

    public void setTotalLikes(long totalLikes) {
        this.totalLikes = totalLikes;
    }

    public long getTotalComments() {
        return totalComments;
    }

    public void setTotalComments(long totalComments) {
        this.totalComments = totalComments;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
