package com.futurevalley.item;

public class ItemMovie {
    private String id;
    private String movieTitle;
    private String movieDesc;
    private String moviePoster;
    private String movieCover;
    private String totalViews;
    private String rateAvg;
    private String languageId;
    private String languageName;
    private String languageBackground;
    private String movieUrl;
    private String movieType;
    private boolean isSubTitle = false;
    private boolean isQuality = false;
    private String quality480;
    private String quality720;
    private String quality1080;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }

    public String getMovieDesc() {
        return movieDesc;
    }

    public void setMovieDesc(String movieDesc) {
        this.movieDesc = movieDesc;
    }

    public String getMoviePoster() {
        return moviePoster;
    }

    public void setMoviePoster(String moviePoster) {
        this.moviePoster = moviePoster;
    }

    public String getMovieCover() {
        return movieCover;
    }

    public void setMovieCover(String movieCover) {
        this.movieCover = movieCover;
    }

    public String getTotalViews() {
        return totalViews;
    }

    public void setTotalViews(String totalViews) {
        this.totalViews = totalViews;
    }

    public String getRateAvg() {
        return rateAvg;
    }

    public void setRateAvg(String rateAvg) {
        this.rateAvg = rateAvg;
    }

    public String getLanguageName() {
        return languageName;
    }

    public void setLanguageName(String languageName) {
        this.languageName = languageName;
    }

    public String getLanguageBackground() {
        return languageBackground;
    }

    public void setLanguageBackground(String languageBackground) {
        this.languageBackground = languageBackground;
    }

    public String getLanguageId() {
        return languageId;
    }

    public void setLanguageId(String languageId) {
        this.languageId = languageId;
    }

    public String getMovieUrl() {
        return movieUrl;
    }

    public void setMovieUrl(String movieUrl) {
        this.movieUrl = movieUrl;
    }

    public String getMovieType() {
        return movieType;
    }

    public void setMovieType(String movieType) {
        this.movieType = movieType;
    }

    public boolean isSubTitle() {
        return isSubTitle;
    }

    public void setSubTitle(boolean subTitle) {
        isSubTitle = subTitle;
    }

    public boolean isQuality() {
        return isQuality;
    }

    public void setQuality(boolean quality) {
        isQuality = quality;
    }

    public String getQuality480() {
        return quality480;
    }

    public void setQuality480(String quality480) {
        this.quality480 = quality480;
    }

    public String getQuality720() {
        return quality720;
    }

    public void setQuality720(String quality720) {
        this.quality720 = quality720;
    }

    public String getQuality1080() {
        return quality1080;
    }

    public void setQuality1080(String quality1080) {
        this.quality1080 = quality1080;
    }
}
