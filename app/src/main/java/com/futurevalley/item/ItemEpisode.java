package com.futurevalley.item;

import java.util.ArrayList;

public class ItemEpisode {

    private String id;
    private String episodeTitle;
    private String episodeUrl;
    private String episodePoster;
    private String episodeType;
    private boolean isPlaying = false;
    private boolean isSubTitle = false;
    private boolean isQuality = false;
    private String quality480;
    private String quality720;
    private String quality1080;
    private ArrayList<ItemSubTitle> subTitles;

    public ArrayList<ItemSubTitle> getSubTitles() {
        return subTitles;
    }

    public void setSubTitles(ArrayList<ItemSubTitle> subTitles) {
        this.subTitles = subTitles;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEpisodeTitle() {
        return episodeTitle;
    }

    public void setEpisodeTitle(String episodeTitle) {
        this.episodeTitle = episodeTitle;
    }

    public String getEpisodeUrl() {
        return episodeUrl;
    }

    public void setEpisodeUrl(String episodeUrl) {
        this.episodeUrl = episodeUrl;
    }

    public String getEpisodePoster() {
        return episodePoster;
    }

    public void setEpisodePoster(String episodePoster) {
        this.episodePoster = episodePoster;
    }

    public String getEpisodeType() {
        return episodeType;
    }

    public void setEpisodeType(String episodeType) {
        this.episodeType = episodeType;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
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
