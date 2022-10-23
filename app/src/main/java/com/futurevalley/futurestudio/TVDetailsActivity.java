package com.futurevalley.futurestudio;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bosphere.fadingedgelayout.FadingEdgeLayout;
import com.futurevalley.adapter.CommentAdapter;
import com.futurevalley.adapter.HomeChannelAdapter;
import com.futurevalley.cast.Casty;
import com.futurevalley.cast.MediaData;
import com.futurevalley.db.DatabaseHelper;
import com.futurevalley.dialog.DialogUtil;
import com.futurevalley.dialog.RateDialog;
import com.futurevalley.fragment.ChromecastScreenFragment;
import com.futurevalley.fragment.EmbeddedImageFragment;
import com.futurevalley.fragment.ReportFragment;
import com.futurevalley.fragment.TVExoPlayerFragment;
import com.futurevalley.item.ItemChannel;
import com.futurevalley.item.ItemComment;
import com.futurevalley.util.API;
import com.futurevalley.util.BannerAds;
import com.futurevalley.util.Constant;
import com.futurevalley.util.Events;
import com.futurevalley.util.GlobalBus;
import com.futurevalley.util.IsRTL;
import com.futurevalley.util.NetworkUtils;
import com.futurevalley.util.RvOnClickListener;
import com.github.ornolfr.ratingview.RatingView;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragmentX;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class TVDetailsActivity extends AppCompatActivity implements RateDialog.RateDialogListener {
    ProgressBar mProgressBar;
    LinearLayout lyt_not_found;
    NestedScrollView nestedScrollView;
    RelativeLayout lytParent;
    WebView webView;
    RatingView ratingView;
    TextView textTitle, textCategory, textRate, textReport, textRelViewAll, textComViewAll, textNoComment, textCount;
    ImageView imageEditRate, imageFav;
    RecyclerView rvRelated, rvComment;
    ItemChannel itemChannel;
    ArrayList<ItemChannel> mListItemRelated;
    ArrayList<ItemComment> mListItemComment;
    HomeChannelAdapter homeChannelAdapter;
    CommentAdapter commentAdapter;
    String Id;
    LinearLayout lytRelated;
    EditText editTextComment;

    ProgressDialog pDialog;
    MyApplication myApplication;
    DatabaseHelper databaseHelper;

    private FragmentManager fragmentManager;
    Toolbar toolbar;
    private int playerHeight;
    FrameLayout frameLayout;
    boolean isFullScreen = false;
    boolean isPlayerIsYt = false;
    private YouTubePlayer youTubePlayer;
    public boolean isYouTubePlayerFullScreen = false;
    boolean isFromNotification = false;
    LinearLayout mAdViewLayout;
    private Casty casty;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tv_details);
        IsRTL.ifSupported(this);
        GlobalBus.getBus().register(this);

        FadingEdgeLayout feRecent = findViewById(R.id.feRecent);
        IsRTL.changeShadowInRtl(this, feRecent);
        mAdViewLayout = findViewById(R.id.adView);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        casty = Casty.create(this)
                .withMiniController();

        Intent intent = getIntent();
        Id = intent.getStringExtra("Id");
        if (intent.hasExtra("isNotification")) {
            isFromNotification = true;
        }
        mListItemRelated = new ArrayList<>();
        mListItemComment = new ArrayList<>();
        itemChannel = new ItemChannel();

        pDialog = new ProgressDialog(this);
        myApplication = MyApplication.getInstance();
        databaseHelper = new DatabaseHelper(this);
        fragmentManager = getSupportFragmentManager();

        lytParent = findViewById(R.id.lytParent);
        lytRelated = findViewById(R.id.lytRelated);
        mProgressBar = findViewById(R.id.progressBar1);
        lyt_not_found = findViewById(R.id.lyt_not_found);
        nestedScrollView = findViewById(R.id.nestedScrollView);
        webView = findViewById(R.id.webView);
        ratingView = findViewById(R.id.ratingView);
        editTextComment = findViewById(R.id.editText_comment_md);

        textTitle = findViewById(R.id.textTitle);
        textCategory = findViewById(R.id.textCategory);
        textRate = findViewById(R.id.textRate);
        textReport = findViewById(R.id.textReport);
        textRelViewAll = findViewById(R.id.textRelViewAll);
        textComViewAll = findViewById(R.id.textComViewAll);
        textNoComment = findViewById(R.id.textView_noComment_md);
        textCount = findViewById(R.id.textViews);

        frameLayout = findViewById(R.id.playerSection);
        frameLayout = findViewById(R.id.playerSection);
        int columnWidth = NetworkUtils.getScreenWidth(this);
        frameLayout.setLayoutParams(new RelativeLayout.LayoutParams(columnWidth, columnWidth / 2));
        playerHeight = frameLayout.getLayoutParams().height;

        rvRelated = findViewById(R.id.rv_related);
        rvComment = findViewById(R.id.rv_comment);

        editTextComment.setClickable(true);
        editTextComment.setFocusable(false);
        textTitle.setSelected(true);

        imageEditRate = findViewById(R.id.imageEditRate);
        imageFav = findViewById(R.id.imageFav);
        webView.setBackgroundColor(Color.TRANSPARENT);

        rvRelated.setHasFixedSize(true);
        rvRelated.setLayoutManager(new LinearLayoutManager(TVDetailsActivity.this, LinearLayoutManager.HORIZONTAL, false));
        rvRelated.setFocusable(false);
        rvRelated.setNestedScrollingEnabled(false);

        rvComment.setHasFixedSize(true);
        rvComment.setLayoutManager(new LinearLayoutManager(TVDetailsActivity.this, LinearLayoutManager.VERTICAL, false));
        rvComment.setFocusable(false);
        rvComment.setNestedScrollingEnabled(false);

        BannerAds.ShowBannerAds(this, mAdViewLayout);

        if (NetworkUtils.isConnected(TVDetailsActivity.this)) {
            getDetails();
        } else {
            showToast(getString(R.string.conne_msg1));
        }

    }

    private void getDetails() {

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
        jsObj.addProperty("method_name", "get_single_channel");
        jsObj.addProperty("channel_id", Id);
        params.put("data", API.toBase64(jsObj.toString()));
        client.post(Constant.API_URL, params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                mProgressBar.setVisibility(View.VISIBLE);
                lytParent.setVisibility(View.GONE);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                mProgressBar.setVisibility(View.GONE);
                lytParent.setVisibility(View.VISIBLE);

                String result = new String(responseBody);
                try {
                    JSONObject mainJson = new JSONObject(result);
                    JSONArray jsonArray = mainJson.getJSONArray(Constant.ARRAY_NAME);
                    if (jsonArray.length() > 0) {
                        JSONObject objJson;
                        for (int i = 0; i < jsonArray.length(); i++) {
                            objJson = jsonArray.getJSONObject(i);
                            if (objJson.has(Constant.STATUS)) {
                                lyt_not_found.setVisibility(View.VISIBLE);
                            } else {
                                itemChannel.setId(objJson.getString(Constant.CHANNEL_ID));
                                itemChannel.setChannelName(objJson.getString(Constant.CHANNEL_TITLE));
                                itemChannel.setDescription(objJson.getString(Constant.CHANNEL_DESC));
                                itemChannel.setChannelCategory(objJson.getString(Constant.CATEGORY_NAME));
                                itemChannel.setChannelCategoryId(objJson.getString(Constant.CHANNEL_CATEGORY_ID));
                                itemChannel.setChannelAvgRate(objJson.getString(Constant.CHANNEL_AVG_RATE));
                                itemChannel.setImage(objJson.getString(Constant.CHANNEL_IMAGE));
                                itemChannel.setChannelUrl(objJson.getString(Constant.CHANNEL_URL));
                                itemChannel.setChannelType(objJson.getString(Constant.CHANNEL_TYPE));
                                itemChannel.setTotalViews(objJson.getString(Constant.MOVIE_TOTAL_VIEW));
                                itemChannel.setChannelPoster(objJson.getString(Constant.CHANNEL_POSTER));
                                itemChannel.setUserAgent(objJson.getBoolean(Constant.CHANNEL_USER_AGENT_IS));
                                itemChannel.setUserAgentName(objJson.getString(Constant.CHANNEL_USER_AGENT_NAME));

                                JSONArray jsonArrayChild = objJson.getJSONArray(Constant.RELATED_ITEM_ARRAY_NAME);
                                if (jsonArrayChild.length() != 0) {
                                    for (int j = 0; j < jsonArrayChild.length(); j++) {
                                        JSONObject objChild = jsonArrayChild.getJSONObject(j);
                                        ItemChannel item = new ItemChannel();
                                        item.setId(objChild.getString(Constant.RELATED_ITEM_CHANNEL_ID));
                                        item.setChannelName(objChild.getString(Constant.RELATED_ITEM_CHANNEL_NAME));
                                        item.setImage(objChild.getString(Constant.RELATED_ITEM_CHANNEL_THUMB));
                                        mListItemRelated.add(item);
                                    }
                                }

                                JSONArray jsonArrayComment = objJson.getJSONArray(Constant.COMMENT_ARRAY);
                                if (jsonArrayComment.length() != 0) {
                                    for (int j = 0; j < jsonArrayComment.length(); j++) {
                                        JSONObject objComment = jsonArrayComment.getJSONObject(j);
                                        ItemComment itemComment = new ItemComment();
                                        itemComment.setUserName(objComment.getString(Constant.COMMENT_NAME));
                                        itemComment.setCommentText(objComment.getString(Constant.COMMENT_DESC));
                                        itemComment.setCommentDate(objComment.getString(Constant.COMMENT_DATE));
                                        mListItemComment.add(itemComment);
                                    }
                                }
                            }
                        }
                        displayData();

                    } else {
                        mProgressBar.setVisibility(View.GONE);
                        lytParent.setVisibility(View.GONE);
                        lyt_not_found.setVisibility(View.VISIBLE);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                mProgressBar.setVisibility(View.GONE);
                lytParent.setVisibility(View.GONE);
                lyt_not_found.setVisibility(View.VISIBLE);
            }
        });
    }

    private void displayData() {
        setTitle(itemChannel.getChannelName());
        textTitle.setText(itemChannel.getChannelName());
        textCategory.setText(itemChannel.getChannelCategory());
        textRate.setText(itemChannel.getChannelAvgRate());
        ratingView.setRating(Float.parseFloat(itemChannel.getChannelAvgRate()));
        textCount.setText(getString(R.string.count, NetworkUtils.viewFormat(Integer.parseInt(itemChannel.getTotalViews()))));

        String mimeType = "text/html";
        String encoding = "utf-8";
        String htmlText = itemChannel.getDescription();

        boolean isRTL = Boolean.parseBoolean(getResources().getString(R.string.isRTL));
        String direction = isRTL ? "rtl" : "ltr";

        String text = "<html dir=" + direction + "><head>"
                + "<style type=\"text/css\">@font-face {font-family: MyFont;src: url(\"file:///android_asset/fonts/custom.ttf\")}body{font-family: MyFont;color: #9f9f9f;font-size:14px;margin-left:0px;line-height:1.3}"
                + "</style></head>"
                + "<body>"
                + htmlText
                + "</body></html>";

        webView.loadDataWithBaseURL(null, text, mimeType, encoding, null);

        switch (itemChannel.getChannelType()) {
            case "live_url":
                if (casty.isConnected()) {
                    ChromecastScreenFragment chromecastScreenFragment = new ChromecastScreenFragment();
                    fragmentManager.beginTransaction().replace(R.id.playerSection, chromecastScreenFragment).commitAllowingStateLoss();
                } else {
                    TVExoPlayerFragment exoPlayerFragment = TVExoPlayerFragment.newInstance(itemChannel.getChannelUrl(), itemChannel.getUserAgentName(), itemChannel.isUserAgent());
                    fragmentManager.beginTransaction().replace(R.id.playerSection, exoPlayerFragment).commitAllowingStateLoss();
                }
                break;
            case "youtube":
                isPlayerIsYt = true;
                String videoId = NetworkUtils.getVideoId(itemChannel.getChannelUrl());
                playYoutube(videoId);
                break;
            default:
                EmbeddedImageFragment embeddedImageFragment = EmbeddedImageFragment.newInstance(itemChannel.getChannelUrl(), itemChannel.getImage(), true);
                fragmentManager.beginTransaction().replace(R.id.playerSection, embeddedImageFragment).commitAllowingStateLoss();
                break;
        }

        if (!mListItemRelated.isEmpty()) {
            homeChannelAdapter = new HomeChannelAdapter(TVDetailsActivity.this, mListItemRelated);
            rvRelated.setAdapter(homeChannelAdapter);

            homeChannelAdapter.setOnItemClickListener(new RvOnClickListener() {
                @Override
                public void onItemClick(int position) {
                    String tvId = mListItemRelated.get(position).getId();
                    Intent intent = new Intent(TVDetailsActivity.this, TVDetailsActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("Id", tvId);
                    startActivity(intent);
                }
            });

        } else {
            lytRelated.setVisibility(View.GONE);
        }

        if (!mListItemComment.isEmpty()) {
            commentAdapter = new CommentAdapter(TVDetailsActivity.this, mListItemComment);
            rvComment.setAdapter(commentAdapter);
        } else {
            textNoComment.setVisibility(View.VISIBLE);
        }

        editTextComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myApplication.getIsLogin()) {
                    showCommentBox();
                } else {
                    String message = getString(R.string.login_first, getString(R.string.login_first_comment));
                    showToast(message);

                    Intent intentLogin = new Intent(TVDetailsActivity.this, SignInActivity.class);
                    intentLogin.putExtra("isOtherScreen", true);
                    startActivity(intentLogin);
                }
            }
        });

        textComViewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TVDetailsActivity.this, AllCommentActivity.class);
                intent.putExtra("postId", Id);
                intent.putExtra("postType", "channel");
                startActivity(intent);
            }
        });

        textRelViewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TVDetailsActivity.this, RelatedAllChannelActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("postId", Id);
                intent.putExtra("postCatId", itemChannel.getChannelCategoryId());
                startActivity(intent);
            }
        });

        ratingView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myApplication.getIsLogin()) {
                    DialogUtil.showRateDialog(TVDetailsActivity.this, TVDetailsActivity.this, Id, "channel");
                } else {
                    String message = getString(R.string.login_first, getString(R.string.login_first_rate));
                    showToast(message);

                    Intent intentLogin = new Intent(TVDetailsActivity.this, SignInActivity.class);
                    intentLogin.putExtra("isOtherScreen", true);
                    startActivity(intentLogin);
                }
            }
        });

        textReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myApplication.getIsLogin()) {
                    Bundle bundle = new Bundle();
                    bundle.putString("postId", Id);
                    bundle.putString("postType", "channel");
                    ReportFragment reportFragment = new ReportFragment();
                    reportFragment.setArguments(bundle);
                    reportFragment.show(getSupportFragmentManager(), reportFragment.getTag());
                } else {
                    String message = getString(R.string.login_first, getString(R.string.login_first_report));
                    showToast(message);

                    Intent intentLogin = new Intent(TVDetailsActivity.this, SignInActivity.class);
                    intentLogin.putExtra("isOtherScreen", true);
                    startActivity(intentLogin);
                }
            }
        });

        isFavourite();
        imageFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues fav = new ContentValues();
                if (databaseHelper.getFavouriteById(Id, DatabaseHelper.TABLE_CHANNEL)) {
                    databaseHelper.removeFavouriteById(Id, DatabaseHelper.TABLE_CHANNEL);
                    imageFav.setImageResource(R.drawable.ic_fav);
                    showToast(getString(R.string.favourite_remove));
                } else {
                    fav.put(DatabaseHelper.CHANNEL_ID, Id);
                    fav.put(DatabaseHelper.CHANNEL_TITLE, itemChannel.getChannelName());
                    fav.put(DatabaseHelper.CHANNEL_POSTER, itemChannel.getImage());
                    databaseHelper.addFavourite(DatabaseHelper.TABLE_CHANNEL, fav, null);
                    imageFav.setImageResource(R.drawable.ic_fav_hover);
                    showToast(getString(R.string.favourite_add));
                }
            }
        });

        saveRecent();

        casty.setOnConnectChangeListener(new Casty.OnConnectChangeListener() {
            @Override
            public void onConnected() {

            }

            @Override
            public void onDisconnected() {
                switch (itemChannel.getChannelType()) {
                    case "live_url":
                        TVExoPlayerFragment exoPlayerFragment = TVExoPlayerFragment.newInstance(itemChannel.getChannelUrl(), itemChannel.getUserAgentName(), itemChannel.isUserAgent());
                        fragmentManager.beginTransaction().replace(R.id.playerSection, exoPlayerFragment).commitAllowingStateLoss();
                        break;
                    case "youtube":
                        isPlayerIsYt = true;
                        String videoId = NetworkUtils.getVideoId(itemChannel.getChannelUrl());
                        playYoutube(videoId);
                        break;
                    default:
                        EmbeddedImageFragment embeddedImageFragment = EmbeddedImageFragment.newInstance(itemChannel.getChannelUrl(), itemChannel.getImage(), true);
                        fragmentManager.beginTransaction().replace(R.id.playerSection, embeddedImageFragment).commitAllowingStateLoss();
                        break;
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        casty.addMediaRouteMenuItem(menu);
        getMenuInflater().inflate(R.menu.menu_details, menu);
        if (casty.isConnected()) {
            menu.findItem(R.id.menu_cast_play).setVisible(true);
        } else {
            menu.findItem(R.id.menu_cast_play).setVisible(false);
        }
        return true;
    }


    private void playViaCast() {
        if (itemChannel.getChannelType().equals("live_url")) {
            casty.getPlayer().loadMediaAndPlay(createSampleMediaData(itemChannel.getChannelUrl(), itemChannel.getChannelName(), itemChannel.getImage()));

            ChromecastScreenFragment chromecastScreenFragment = new ChromecastScreenFragment();
            fragmentManager.beginTransaction().replace(R.id.playerSection, chromecastScreenFragment).commitAllowingStateLoss();
        } else {
            showToast(getResources().getString(R.string.cast_youtube));
        }
    }

    private MediaData createSampleMediaData(String videoUrl, String videoTitle, String videoImage) {
        return new MediaData.Builder(videoUrl)
                .setStreamType(MediaData.STREAM_TYPE_BUFFERED)
                .setContentType(getType(videoUrl))
                .setMediaType(MediaData.MEDIA_TYPE_MOVIE)
                .setTitle(videoTitle)
                .setSubtitle(getString(R.string.app_name))
                .addPhotoUrl(videoImage)
                .build();
    }

    private String getType(String videoUrl) {
        if (videoUrl.endsWith(".mp4")) {
            return "videos/mp4";
        } else if (videoUrl.endsWith(".m3u8")) {
            return "application/x-mpegurl";
        } else {
            return "application/x-mpegurl";
        }
    }

    public void showToast(String msg) {
        Toast.makeText(TVDetailsActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.menu_cast_play:
                playViaCast();
                break;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
        return true;
    }

    private void showCommentBox() {
        final Dialog mDialog = new Dialog(TVDetailsActivity.this, R.style.Theme_AppCompat_Translucent);
        mDialog.setContentView(R.layout.dialog_comment);
        final EditText edt_comment = mDialog.findViewById(R.id.edt_comment);
        final ImageView img_sent = mDialog.findViewById(R.id.image_sent);
        mDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        img_sent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comment = edt_comment.getText().toString();
                if (!comment.isEmpty()) {
                    if (NetworkUtils.isConnected(TVDetailsActivity.this)) {
                        sentComment(comment);
                        mDialog.dismiss();
                    } else {
                        showToast(getString(R.string.conne_msg1));
                    }
                }
            }
        });
        mDialog.show();
    }

    private void sentComment(String comment) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
        jsObj.addProperty("method_name", "user_comment");
        jsObj.addProperty("post_id", Id);
        jsObj.addProperty("user_id", myApplication.getUserId());
        jsObj.addProperty("comment_text", comment);
        jsObj.addProperty("type", "channel");
        jsObj.addProperty("is_limit", "true");
        params.put("data", API.toBase64(jsObj.toString()));

        client.post(Constant.API_URL, params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                showProgressDialog();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                dismissProgressDialog();
                String result = new String(responseBody);
                try {
                    JSONObject mainJson = new JSONObject(result);
                    String strMessage = mainJson.getString(Constant.MSG);
                    showToast(strMessage);

                    JSONArray jsonArray = mainJson.getJSONArray(Constant.ARRAY_NAME);
                    if (jsonArray.length() != 0) {
                        mListItemComment.clear();
                        for (int j = 0; j < jsonArray.length(); j++) {
                            JSONObject objComment = jsonArray.getJSONObject(j);
                            ItemComment itemComment = new ItemComment();
                            itemComment.setUserName(objComment.getString(Constant.COMMENT_NAME));
                            itemComment.setCommentText(objComment.getString(Constant.COMMENT_DESC));
                            itemComment.setCommentDate(objComment.getString(Constant.COMMENT_DATE));
                            mListItemComment.add(itemComment);
                        }
                    }

                    if (!mListItemComment.isEmpty()) {
                        commentAdapter = new CommentAdapter(TVDetailsActivity.this, mListItemComment);
                        rvComment.setAdapter(commentAdapter);
                        textNoComment.setVisibility(View.GONE);
                    } else {
                        textNoComment.setVisibility(View.VISIBLE);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                dismissProgressDialog();
            }

        });
    }

    public void showProgressDialog() {
        pDialog.setMessage(getString(R.string.loading));
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();
    }

    public void dismissProgressDialog() {
        if (pDialog != null && pDialog.isShowing()) {
            pDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GlobalBus.getBus().unregister(this);
    }

    @Subscribe
    public void getComment(Events.Comment comment) {
        if (comment.getPostType().equals("channel")) {
            ArrayList<ItemComment> itemComments = comment.getItemComments();
            CommentAdapter commentAdapter = new CommentAdapter(TVDetailsActivity.this, itemComments);
            rvComment.setAdapter(commentAdapter);
            textNoComment.setVisibility(View.GONE);
        }
    }

    @Subscribe
    public void getFullScreen(Events.FullScreen fullScreen) {
        isFullScreen = fullScreen.isFullScreen();
        if (fullScreen.isFullScreen()) {
            gotoFullScreen();
        } else {
            gotoPortraitScreen();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        View v1 = findViewById(R.id.view_fake);
        v1.requestFocus();
    }

    private void gotoPortraitScreen() {
        nestedScrollView.setVisibility(View.VISIBLE);
        toolbar.setVisibility(View.VISIBLE);
        mAdViewLayout.setVisibility(View.VISIBLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        frameLayout.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, playerHeight));
    }

    private void gotoFullScreen() {
        nestedScrollView.setVisibility(View.GONE);
        toolbar.setVisibility(View.GONE);
        mAdViewLayout.setVisibility(View.GONE);
        frameLayout.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    @Override
    public void onBackPressed() {
        if (isPlayerIsYt) {
            if (isYouTubePlayerFullScreen && youTubePlayer != null) {
                youTubePlayer.setFullscreen(false);
            } else {
                if (isFromNotification) {
                    Intent intent = new Intent(TVDetailsActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                } else {
                    super.onBackPressed();
                }
            }
        } else {
            if (isFullScreen) {
                Events.FullScreen fullScreen = new Events.FullScreen();
                fullScreen.setFullScreen(false);
                GlobalBus.getBus().post(fullScreen);
            } else {
                if (isFromNotification) {
                    Intent intent = new Intent(TVDetailsActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                } else {
                    super.onBackPressed();
                }
            }
        }
    }

    private void playYoutube(String videoId) {
        YouTubePlayerSupportFragmentX youTubePlayerFragment = YouTubePlayerSupportFragmentX.newInstance();
        fragmentManager.beginTransaction().replace(R.id.playerSection, youTubePlayerFragment).commitAllowingStateLoss();
        youTubePlayerFragment.initialize(getString(R.string.youtube_api_key), new YouTubePlayer.OnInitializedListener() {

            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {
                if (!wasRestored) {
                    youTubePlayer = player;
                    youTubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);
                    youTubePlayer.loadVideo(videoId);
                    youTubePlayer.play();
                    youTubePlayer.setOnFullscreenListener(new YouTubePlayer.OnFullscreenListener() {
                        @Override
                        public void onFullscreen(boolean _isFullScreen) {
                            isYouTubePlayerFullScreen = _isFullScreen;
                        }
                    });
                }
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
                String errorMessage = youTubeInitializationResult.toString();
                Log.d("errorMessage:", errorMessage);
            }
        });
    }

    @Override
    public void confirm(String rateAvg) {
        ratingView.setRating(Float.parseFloat(rateAvg));
        textRate.setText(rateAvg);
    }

    @Override
    public void cancel() {

    }

    private void isFavourite() {
        if (databaseHelper.getFavouriteById(Id, DatabaseHelper.TABLE_CHANNEL)) {
            imageFav.setImageResource(R.drawable.ic_fav_hover);
        } else {
            imageFav.setImageResource(R.drawable.ic_fav);
        }
    }

    private void saveRecent() {
        if (!databaseHelper.getRecentById(Id, "channel")) {
            ContentValues recent = new ContentValues();
            recent.put(DatabaseHelper.RECENT_ID, Id);
            recent.put(DatabaseHelper.RECENT_TITLE, itemChannel.getChannelName());
            recent.put(DatabaseHelper.RECENT_IMAGE, itemChannel.getChannelPoster());
            recent.put(DatabaseHelper.RECENT_TYPE, "channel");
            databaseHelper.addRecent(DatabaseHelper.TABLE_RECENT, recent, null);
        }
    }
}
