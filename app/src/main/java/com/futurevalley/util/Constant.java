package com.futurevalley.util;
import java.io.Serializable;
public class Constant implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;


    private static String SERVER_URL = "http://pakmasti-tube.com/";

    public static final String IMAGE_PATH = SERVER_URL + "images/";

    public static final String API_URL = SERVER_URL + "api.php";

    public static final String ARRAY_NAME = "LIVETV";

    public static final String CATEGORY_NAME = "category_name";
    public static final String CATEGORY_CID = "cid";
    public static final String CATEGORY_IMAGE = "category_image";

    public static final String LANGUAGE_ID = "id";
    public static final String LANGUAGE_NAME = "language_name";
    public static final String LANGUAGE_COLOR = "language_background";

    public static final String GENRE_ID = "id";
    public static final String GENRE_NAME = "genre_name";
    public static final String GENRE_IMAGE = "genre_image";

    public static final String MOVIE_ID = "id";
    public static final String MOVIE_TITLE = "movie_title";
    public static final String MOVIE_DESC = "movie_desc";
    public static final String MOVIE_POSTER = "movie_poster";
    public static final String MOVIE_COVER = "movie_cover";
    public static final String MOVIE_TOTAL_VIEW = "total_views";
    public static final String MOVIE_RATE = "rate_avg";
    public static final String MOVIE_LANGUAGE_ID = "language_id";
    public static final String MOVIE_LANGUAGE = "language_name";
    public static final String MOVIE_LANGUAGE_BACK = "language_background";
    public static final String MOVIE_URL = "movie_url";
    public static final String MOVIE_TYPE = "video_type";

    public static final String IS_QUALITY = "quality_status";
    public static final String IS_SUBTITLE = "subtitle_status";
    public static final String QUALITY_480 = "quality_480";
    public static final String QUALITY_720 = "quality_720";
    public static final String QUALITY_1080 = "quality_1080";
    public static final String SUBTITLE_ARRAY_NAME = "subtitles";
    public static final String SUBTITLE_ID = "subtitle_id";
    public static final String SUBTITLE_LANGUAGE = "language";
    public static final String SUBTITLE_URL = "subtitle_url";


    public static final String SERIES_ID = "id";
    public static final String SERIES_TITLE = "series_name";
    public static final String SERIES_DESC = "series_desc";
    public static final String SERIES_POSTER = "series_poster";
    public static final String SERIES_COVER = "series_cover";
    public static final String SERIES_RATE = "rate_avg";

    public static final String CHANNEL_ID = "id";
    public static final String CHANNEL_TITLE = "channel_title";
    public static final String CHANNEL_URL = "channel_url";
    public static final String CHANNEL_IMAGE = "channel_thumbnail";
    public static final String CHANNEL_DESC = "channel_desc";
    public static final String CHANNEL_TYPE = "channel_type";
    public static final String CHANNEL_AVG_RATE = "rate_avg";
    public static final String CHANNEL_CATEGORY_ID = "cat_id";
    public static final String CHANNEL_POSTER = "channel_poster";
    public static final String CHANNEL_USER_AGENT_IS = "user_agent";
    public static final String CHANNEL_USER_AGENT_NAME = "user_agent_name";


    public static final String APP_NAME = "app_name";
    public static final String APP_IMAGE = "app_logo";
    public static final String APP_VERSION = "app_version";
    public static final String APP_AUTHOR = "app_author";
    public static final String APP_CONTACT = "app_contact";
    public static final String APP_EMAIL = "app_email";
    public static final String APP_WEBSITE = "app_website";
    public static final String APP_DESC = "app_description";
    public static final String APP_PRIVACY_POLICY = "app_privacy_policy";

    public static final String USER_NAME = "name";
    public static final String USER_ID = "user_id";
    public static final String USER_EMAIL = "email";
    public static final String USER_PHONE = "phone";
    public static final String USER_RATE = "user_rate";

    public static final String RELATED_ITEM_ARRAY_NAME = "related";
    public static final String RELATED_ITEM_CHANNEL_ID = "rel_id";
    public static final String RELATED_ITEM_CHANNEL_NAME = "rel_channel_title";
    public static final String RELATED_ITEM_CHANNEL_THUMB = "rel_channel_thumbnail";

    public static final String COMMENT_ARRAY = "comments";
    public static final String COMMENT_ID = "id";
    public static final String COMMENT_NAME = "user_name";
    public static final String COMMENT_DESC = "comment_text";
    public static final String COMMENT_DATE = "comment_date";

    public static final String SEASON_ARRAY = "seasons";
    public static final String SEASON_ID = "id";
    public static final String SEASON_NAME = "season_name";

    public static final String EPISODE_ID = "id";
    public static final String EPISODE_TITLE = "episode_title";
    public static final String EPISODE_TYPE = "episode_type";
    public static final String EPISODE_URL = "episode_url";
    public static final String EPISODE_POSTER = "episode_poster";


    public static int GET_SUCCESS_MSG;
    public static final String MSG = "msg";
    public static final String SUCCESS = "success";
    public static final String STATUS = "status";
    public static int AD_COUNT = 0;
    public static int AD_COUNT_SHOW;

    public static boolean isBanner = false, isInterstitial = false;
    public static boolean isAdMobBanner = true, isAdMobInterstitial = true;
    public static String bannerId, interstitialId, adMobPublisherId;


}
