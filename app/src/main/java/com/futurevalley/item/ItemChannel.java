package com.futurevalley.item;

public class ItemChannel {

    private String id;
    private String channelUrl;
    private String channelImage;
    private String channelName;
    private String channelCategory;
    private String channelCategoryId;
    private String channelDescription;
    private String channelAvgRate;
    private String channelType;
    private String totalViews;
    private String channelPoster;
    private boolean isUserAgent = false;
    private String userAgentName;

    public ItemChannel() {
        // TODO Auto-generated constructor stub
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getChannelUrl() {
        return channelUrl;
    }

    public void setChannelUrl(String url) {
        this.channelUrl = url;
    }


    public String getImage() {
        return channelImage;
    }

    public void setImage(String image) {
        this.channelImage = image;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelname) {
        this.channelName = channelname;
    }

    public String getDescription() {
        return channelDescription;
    }

    public void setDescription(String desc) {
        this.channelDescription = desc;
    }

    public String getChannelType() {
        return channelType;
    }

    public void setChannelType(String channelType) {
        this.channelType = channelType;
    }

    public String getChannelCategory() {
        return channelCategory;
    }

    public void setChannelCategory(String channelCategory) {
        this.channelCategory = channelCategory;
    }

    public String getChannelAvgRate() {
        return channelAvgRate;
    }

    public void setChannelAvgRate(String channelAvgRate) {
        this.channelAvgRate = channelAvgRate;
    }

    public String getChannelCategoryId() {
        return channelCategoryId;
    }

    public void setChannelCategoryId(String channelCategoryId) {
        this.channelCategoryId = channelCategoryId;
    }

    public String getTotalViews() {
        return totalViews;
    }

    public void setTotalViews(String totalViews) {
        this.totalViews = totalViews;
    }

    public String getChannelPoster() {
        return channelPoster;
    }

    public void setChannelPoster(String channelPoster) {
        this.channelPoster = channelPoster;
    }

    public boolean isUserAgent() {
        return isUserAgent;
    }

    public void setUserAgent(boolean userAgent) {
        isUserAgent = userAgent;
    }

    public String getUserAgentName() {
        return userAgentName;
    }

    public void setUserAgentName(String userAgentName) {
        this.userAgentName = userAgentName;
    }
}
