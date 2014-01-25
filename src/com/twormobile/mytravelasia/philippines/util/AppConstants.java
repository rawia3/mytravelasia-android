package com.twormobile.mytravelasia.philippines.util;

/**
 * Application specific constants which are not specific to a particular app component.
 *
 * @author avendael
 */
public class AppConstants {
    private AppConstants() {}

    /**
     * Primarily used to hide debug logs in production.
     */
    public static final boolean DEBUG = true;

    /**
     * Error message to be displayed when a fragment attaches to an activity that does not implement the
     * fragment's callbacks.
     */
    public static final String EXC_ACTIVITY_CALLBACK = "Activity must implement fragment's callbacks";

    /**
     * The usual density dpi of the smallest tablet.
     */
    public static final int TABLET_MIN_SW = 530;

    /**
     * Extras key for the user's current location.
     */
    public static final String ARG_CURRENT_LOCATION = "com.twormobile.mytravelasia.location.current";

    /**
     * Extras key for a POI's photo URL.
     */
    public static final String ARG_PHOTO_URL = "com.twormobile.mytravelasia.philippines.ui.photo_url";

    /**
     * Extras key for a user's Facebook profile ID.
     */
    public static final String ARG_FB_PROFILE_ID = "com.twormobile.mytravelasia.philippines.extras.profile_id";
}
