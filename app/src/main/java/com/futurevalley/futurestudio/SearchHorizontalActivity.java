package com.futurevalley.futurestudio;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bosphere.fadingedgelayout.FadingEdgeLayout;
import com.futurevalley.adapter.HomeChannelAdapter;
import com.futurevalley.adapter.HomeMovieAdapter;
import com.futurevalley.adapter.HomeSeriesAdapter;
import com.futurevalley.item.ItemChannel;
import com.futurevalley.item.ItemMovie;
import com.futurevalley.item.ItemSeries;
import com.futurevalley.util.API;
import com.futurevalley.util.BannerAds;
import com.futurevalley.util.Constant;
import com.futurevalley.util.IsRTL;
import com.futurevalley.util.NetworkUtils;
import com.futurevalley.util.RvOnClickListener;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class SearchHorizontalActivity extends AppCompatActivity {

    String search;
    ProgressBar mProgressBar;
    LinearLayout lyt_not_found;
    NestedScrollView nestedScrollView;
    TextView movieViewAll, seriesViewAll, channelViewAll;
    RecyclerView rvMovie, rvSeries, rvChannel;
    ArrayList<ItemMovie> movieList;
    ArrayList<ItemSeries> seriesList;
    ArrayList<ItemChannel> channelList;

    HomeMovieAdapter movieAdapter;
    HomeSeriesAdapter seriesAdapter;
    HomeChannelAdapter channelAdapter;

    LinearLayout lytMovie, lytSeries, lytChannel;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_horizontal);
        IsRTL.ifSupported(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.search));
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        LinearLayout mAdViewLayout = findViewById(R.id.adView);
        BannerAds.ShowBannerAds(this, mAdViewLayout);

        Intent intent = getIntent();
        search = intent.getStringExtra("search");

        FadingEdgeLayout feMovie = findViewById(R.id.feMovie);
        FadingEdgeLayout feSeries = findViewById(R.id.feSeries);
        FadingEdgeLayout feChannel = findViewById(R.id.feChannel);

        IsRTL.changeShadowInRtl(this, feMovie);
        IsRTL.changeShadowInRtl(this, feSeries);
        IsRTL.changeShadowInRtl(this, feChannel);

        movieList = new ArrayList<>();
        seriesList = new ArrayList<>();
        channelList = new ArrayList<>();

        mProgressBar = findViewById(R.id.progressBar1);
        lyt_not_found = findViewById(R.id.lyt_not_found);
        nestedScrollView = findViewById(R.id.nestedScrollView);

        movieViewAll = findViewById(R.id.textLatestMovieViewAll);
        seriesViewAll = findViewById(R.id.textTVSeriesViewAll);
        channelViewAll = findViewById(R.id.textLatestChannelViewAll);

        lytMovie = findViewById(R.id.lytMovie);
        lytSeries = findViewById(R.id.lytHomeTVSeries);
        lytChannel = findViewById(R.id.lytHomeLatestChannel);


        rvMovie = findViewById(R.id.rv_latest_movie);
        rvSeries = findViewById(R.id.rv_tv_series);
        rvChannel = findViewById(R.id.rv_latest_channel);


        rvMovie.setHasFixedSize(true);
        rvMovie.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvMovie.setFocusable(false);
        rvMovie.setNestedScrollingEnabled(false);

        rvSeries.setHasFixedSize(true);
        rvSeries.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvSeries.setFocusable(false);
        rvSeries.setNestedScrollingEnabled(false);

        rvChannel.setHasFixedSize(true);
        rvChannel.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvChannel.setFocusable(false);
        rvChannel.setNestedScrollingEnabled(false);

        if (NetworkUtils.isConnected(SearchHorizontalActivity.this)) {
            getSearchAll();
        } else {
            Toast.makeText(SearchHorizontalActivity.this, getString(R.string.conne_msg1), Toast.LENGTH_SHORT).show();
        }

    }

    private void getSearchAll() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
        jsObj.addProperty("method_name", "search_all");
        jsObj.addProperty("search_text", search);
        params.put("data", API.toBase64(jsObj.toString()));

        client.post(Constant.API_URL, params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                mProgressBar.setVisibility(View.VISIBLE);
                nestedScrollView.setVisibility(View.GONE);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                mProgressBar.setVisibility(View.GONE);
                nestedScrollView.setVisibility(View.VISIBLE);

                String result = new String(responseBody);
                try {
                    JSONObject mainJson = new JSONObject(result);
                    JSONObject liveTVJson = mainJson.getJSONObject(Constant.ARRAY_NAME);

                    JSONArray seriesArray = liveTVJson.getJSONArray("search_series");
                    for (int i = 0; i < seriesArray.length(); i++) {
                        JSONObject jsonObject = seriesArray.getJSONObject(i);
                        ItemSeries itemSeries = new ItemSeries();
                        itemSeries.setId(jsonObject.getString(Constant.SERIES_ID));
                        itemSeries.setSeriesName(jsonObject.getString(Constant.SERIES_TITLE));
                        itemSeries.setSeriesPoster(jsonObject.getString(Constant.SERIES_POSTER));
                        seriesList.add(itemSeries);
                    }

                    JSONArray movieArray = liveTVJson.getJSONArray("search_movies");
                    for (int i = 0; i < movieArray.length(); i++) {
                        JSONObject jsonObject = movieArray.getJSONObject(i);
                        ItemMovie itemMovie = new ItemMovie();
                        itemMovie.setId(jsonObject.getString(Constant.MOVIE_ID));
                        itemMovie.setMovieTitle(jsonObject.getString(Constant.MOVIE_TITLE));
                        itemMovie.setMoviePoster(jsonObject.getString(Constant.MOVIE_POSTER));
                        itemMovie.setLanguageName(jsonObject.getString(Constant.MOVIE_LANGUAGE));
                        itemMovie.setLanguageBackground(jsonObject.getString(Constant.MOVIE_LANGUAGE_BACK));
                        movieList.add(itemMovie);
                    }

                    JSONArray channelArray = liveTVJson.getJSONArray("search_channels");
                    for (int i = 0; i < channelArray.length(); i++) {
                        JSONObject jsonObject = channelArray.getJSONObject(i);
                        ItemChannel itemChannel = new ItemChannel();
                        itemChannel.setId(jsonObject.getString(Constant.CHANNEL_ID));
                        itemChannel.setChannelName(jsonObject.getString(Constant.CHANNEL_TITLE));
                        itemChannel.setImage(jsonObject.getString(Constant.CHANNEL_IMAGE));
                        channelList.add(itemChannel);
                    }

                    displayData();

                } catch (JSONException e) {
                    e.printStackTrace();
                    nestedScrollView.setVisibility(View.GONE);
                    lyt_not_found.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                mProgressBar.setVisibility(View.GONE);
                nestedScrollView.setVisibility(View.GONE);
                lyt_not_found.setVisibility(View.VISIBLE);
            }
        });
    }

    private void displayData() {

        if (!movieList.isEmpty()) {
            movieAdapter = new HomeMovieAdapter(SearchHorizontalActivity.this, movieList);
            rvMovie.setAdapter(movieAdapter);

            movieAdapter.setOnItemClickListener(new RvOnClickListener() {
                @Override
                public void onItemClick(int position) {
                    String movieId = movieList.get(position).getId();
                    Intent intent = new Intent(SearchHorizontalActivity.this, MovieDetailsActivity.class);
                    intent.putExtra("Id", movieId);
                    startActivity(intent);
                }
            });

        } else {
            lytMovie.setVisibility(View.GONE);
        }

        if (!seriesList.isEmpty()) {
            seriesAdapter = new HomeSeriesAdapter(SearchHorizontalActivity.this, seriesList);
            rvSeries.setAdapter(seriesAdapter);

            seriesAdapter.setOnItemClickListener(new RvOnClickListener() {
                @Override
                public void onItemClick(int position) {
                    String seriesId = seriesList.get(position).getId();
                    Intent intent = new Intent(SearchHorizontalActivity.this, SeriesDetailsActivity.class);
                    intent.putExtra("Id", seriesId);
                    startActivity(intent);
                }
            });

        } else {
            lytSeries.setVisibility(View.GONE);
        }

        if (!channelList.isEmpty()) {
            channelAdapter = new HomeChannelAdapter(SearchHorizontalActivity.this, channelList);
            rvChannel.setAdapter(channelAdapter);

            channelAdapter.setOnItemClickListener(new RvOnClickListener() {
                @Override
                public void onItemClick(int position) {
                    String tvId = channelList.get(position).getId();
                    Intent intent = new Intent(SearchHorizontalActivity.this, TVDetailsActivity.class);
                    intent.putExtra("Id", tvId);
                    startActivity(intent);
                }
            });


        } else {
            lytChannel.setVisibility(View.GONE);
        }


        movieViewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SearchHorizontalActivity.this, SearchAllMovieActivity.class);
                intent.putExtra("search", search);
                startActivity(intent);
            }
        });

        seriesViewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SearchHorizontalActivity.this, SearchAllSeriesActivity.class);
                intent.putExtra("search", search);
                startActivity(intent);
            }
        });

        channelViewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SearchHorizontalActivity.this, SearchAllChannelActivity.class);
                intent.putExtra("search", search);
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
