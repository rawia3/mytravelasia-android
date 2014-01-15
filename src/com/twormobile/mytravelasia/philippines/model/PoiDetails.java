package com.twormobile.mytravelasia.philippines.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

/**
 * Model class for a Poi's details.
 *
 * @author avendael
 */
public class PoiDetails implements Parcelable {
    public static final Creator<PoiDetails> CREATOR = new Creator<PoiDetails>() {
        @Override
        public PoiDetails createFromParcel(Parcel parcel) {
            return new PoiDetails(parcel);
        }

        @Override
        public PoiDetails[] newArray(int size) {
            return new PoiDetails[size];
        }
    };

    /**
     * ID of the poi detail from the remote resource. This is different from the row id stored in the local database.
     */
    @Expose
    @SerializedName("id")
    private long resourceId;

    @Expose
    private String name;

    @Expose
    private String address;

    @Expose
    @SerializedName("approved")
    private boolean isApproved;

    @Expose
    @SerializedName("bookable")
    private boolean isBookable;

    @Expose
    private String bookingEmailProviders;

    @Expose
    private List<CommentEntry> commentEntries;

    @Expose
    private long countryId;

    @Expose
    private String countryName;

    @Expose
    private Date createdAt;

    @Expose
    private String currencyCode;

    @Expose
    private String currentStatus;

    @Expose
    private String description;

    @Expose
    private long destinationId;

    @Expose
    private String destinationName;

    @Expose
    private String email;

    @Expose
    @SerializedName("exclusive")
    private boolean isExclusive;

    @Expose
    @SerializedName("featured")
    private boolean isFeatured;

    @Expose
    private String fullAddress;

    @Expose
    @SerializedName("liked")
    private boolean isLiked;

    @Expose
    private double latitude;

    @Expose
    private double longitude;

    @Expose
    private double minRate;

    @Expose
    private String pictureFullPath;

    @Expose
    private String pictureThumbPath;

    @Expose
    private int poiTypeId;

    @Expose
    private String poiTypeName;

    @Expose
    private String telNo;

    @Expose
    private long totalComments;

    @Expose
    private long totalLikes;

    @Expose
    private long totalPictures;

    @Expose
    private long totalRatings;

    @Expose
    private double totalStars;

    @Expose
    private long totalViews;

    @Expose
    private Date updatedAt;

    @Expose
    private Date viewedAt;

    @Expose
    private String webUrl;

    private List<PoiPicture> pictures;

    public PoiDetails() {}

    public PoiDetails(Parcel parcel) {
        long[] longData = new long[10];
        String[] stringData = new String[15];
        double[] doubleData = new double[4];
        boolean[] booleanData = new boolean[5];

        parcel.readLongArray(longData);
        parcel.readStringArray(stringData);
        parcel.readDoubleArray(doubleData);
        parcel.readBooleanArray(booleanData);

        resourceId = longData[0];
        countryId = longData[1];
        destinationId = longData[2];
        totalComments = longData[3];
        totalLikes = longData[4];
        totalPictures = longData[5];
        totalRatings = longData[6];
        totalViews = longData[7];
        updatedAt = new Date(longData[8]);
        viewedAt = new Date(longData[9]);

        name = stringData[0];
        address = stringData[1];
        bookingEmailProviders = stringData[2];
        countryName = stringData[3];
        currencyCode = stringData[4];
        currentStatus = stringData[5];
        description = stringData[6];
        destinationName = stringData[7];
        email = stringData[8];
        fullAddress = stringData[9];
        pictureFullPath = stringData[10];
        pictureThumbPath = stringData[11];
        poiTypeName = stringData[12];
        telNo = stringData[13];
        webUrl = stringData[14];

        latitude = doubleData[0];
        longitude = doubleData[1];
        minRate = doubleData[2];
        totalStars = doubleData[3];

        isApproved = booleanData[0];
        isBookable = booleanData[1];
        isExclusive = booleanData[2];
        isFeatured = booleanData[3];
        isLiked = booleanData[4];

        poiTypeId = parcel.readInt();
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeLongArray(new long[] {resourceId, countryId, destinationId, totalComments, totalLikes,
                totalPictures, totalRatings, totalViews,
                updatedAt != null ? updatedAt.getTime() : 0,
                viewedAt != null ? viewedAt.getTime() : 0});
        parcel.writeStringArray(new String[]{name, address, bookingEmailProviders, countryName, currencyCode,
                currentStatus, description, destinationName, email, fullAddress, pictureFullPath, pictureThumbPath,
                poiTypeName, telNo, webUrl});
        parcel.writeDoubleArray(new double[]{latitude, longitude, minRate, totalStars});
        parcel.writeBooleanArray(new boolean[] {isApproved, isBookable, isExclusive, isFeatured, isLiked});
        parcel.writeInt(poiTypeId);
    }

    public long getResourceId() {
        return resourceId;
    }

    public void setResourceId(long resourceId) {
        this.resourceId = resourceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isApproved() {
        return isApproved;
    }

    public void setApproved(boolean approved) {
        isApproved = approved;
    }

    public boolean isBookable() {
        return isBookable;
    }

    public void setBookable(boolean bookable) {
        isBookable = bookable;
    }

    public String getBookingEmailProviders() {
        return bookingEmailProviders;
    }

    public void setBookingEmailProviders(String bookingEmailProviders) {
        this.bookingEmailProviders = bookingEmailProviders;
    }

    public List<CommentEntry> getCommentEntries() {
        return commentEntries;
    }

    public void setCommentEntries(List<CommentEntry> commentEntries) {
        this.commentEntries = commentEntries;
    }

    public long getCountryId() {
        return countryId;
    }

    public void setCountryId(long countryId) {
        this.countryId = countryId;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(String currentStatus) {
        this.currentStatus = currentStatus;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getDestinationId() {
        return destinationId;
    }

    public void setDestinationId(long destinationId) {
        this.destinationId = destinationId;
    }

    public String getDestinationName() {
        return destinationName;
    }

    public void setDestinationName(String destinationName) {
        this.destinationName = destinationName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isExclusive() {
        return isExclusive;
    }

    public void setExclusive(boolean exclusive) {
        isExclusive = exclusive;
    }

    public boolean isFeatured() {
        return isFeatured;
    }

    public void setFeatured(boolean featured) {
        isFeatured = featured;
    }

    public String getFullAddress() {
        return fullAddress;
    }

    public void setFullAddress(String fullAddress) {
        this.fullAddress = fullAddress;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getMinRate() {
        return minRate;
    }

    public void setMinRate(double minRate) {
        this.minRate = minRate;
    }

    public String getPictureFullPath() {
        return pictureFullPath;
    }

    public void setPictureFullPath(String pictureFullPath) {
        this.pictureFullPath = pictureFullPath;
    }

    public String getPictureThumbPath() {
        return pictureThumbPath;
    }

    public void setPictureThumbPath(String pictureThumbPath) {
        this.pictureThumbPath = pictureThumbPath;
    }

    public int getPoiTypeId() {
        return poiTypeId;
    }

    public void setPoiTypeId(int poiTypeId) {
        this.poiTypeId = poiTypeId;
    }

    public String getPoiTypeName() {
        return poiTypeName;
    }

    public void setPoiTypeName(String poiTypeName) {
        this.poiTypeName = poiTypeName;
    }

    public String getTelNo() {
        return telNo;
    }

    public void setTelNo(String telNo) {
        this.telNo = telNo;
    }

    public long getTotalComments() {
        return totalComments;
    }

    public void setTotalComments(long totalComments) {
        this.totalComments = totalComments;
    }

    public long getTotalLikes() {
        return totalLikes;
    }

    public void setTotalLikes(long totalLikes) {
        this.totalLikes = totalLikes;
    }

    public long getTotalPictures() {
        return totalPictures;
    }

    public void setTotalPictures(long totalPictures) {
        this.totalPictures = totalPictures;
    }

    public long getTotalRatings() {
        return totalRatings;
    }

    public void setTotalRatings(long totalRatings) {
        this.totalRatings = totalRatings;
    }

    public double getTotalStars() {
        return totalStars;
    }

    public void setTotalStars(double totalStars) {
        this.totalStars = totalStars;
    }

    public long getTotalViews() {
        return totalViews;
    }

    public void setTotalViews(long totalViews) {
        this.totalViews = totalViews;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Date getViewedAt() {
        return viewedAt;
    }

    public void setViewedAt(Date viewedAt) {
        this.viewedAt = viewedAt;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }

    public List<PoiPicture> getPictures() {
        return pictures;
    }

    public void setPictures(List<PoiPicture> pictures) {
        this.pictures = pictures;
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
