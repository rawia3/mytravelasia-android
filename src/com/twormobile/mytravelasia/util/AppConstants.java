package com.twormobile.mytravelasia.util;

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
}
