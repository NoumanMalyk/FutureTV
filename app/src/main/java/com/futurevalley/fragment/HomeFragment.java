package com.futurevalley.fragment;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bosphere.fadingedgelayout.FadingEdgeLayout;
import com.futurevalley.adapter.HomeCategoryAdapter;
import com.futurevalley.adapter.HomeChannelAdapter;
import com.futurevalley.adapter.HomeMovieAdapter;
import com.futurevalley.adapter.HomeRecentAdapter;
import com.futurevalley.adapter.HomeSeriesAdapter;
import com.futurevalley.adapter.SliderAdapter;
import com.futurevalley.db.DatabaseHelper;
import com.futurevalley.item.ItemCategory;
import com.futurevalley.item.ItemChannel;
import com.futurevalley.item.ItemMovie;
import com.futurevalley.item.ItemRecent;
import com.futurevalley.item.ItemSeries;
import com.futurevalley.item.ItemSlider;
import com.futurevalley.futurestudio.MainActivity;
import com.futurevalley.futurestudio.MovieDetailsActivity;
import com.futurevalley.futurestudio.R;
import com.futurevalley.futurestudio.SeriesDetailsActivity;
import com.futurevalley.futurestudio.TVDetailsActivity;
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
import me.relex.circleindicator.CircleIndicator;

public class HomeFragment extends Fragment {

    ProgressBar mProgressBar;
    LinearLayout lyt_not_found;
    NestedScrollView nestedScrollView;
    ViewPager viewPager;
    TextView categoryViewAll, movieViewAll, seriesViewAll, channelViewAll, recentViewAll;
    RecyclerView rvCategory, rvMovie, rvSeries, rvChannel, rvRecent;
    ArrayList<ItemRecent> recentList;
    ArrayList<ItemCategory> categoryList;
    ArrayList<ItemMovie> movieList;
    ArrayList<ItemSeries> seriesList;
    ArrayList<ItemChannel> channelList;
    ArrayList<ItemSlider> sliderList;

    SliderAdapter sliderAdapter;
    HomeCategoryAdapter categoryAdapter;
    HomeMovieAdapter movieAdapter;
    HomeSeriesAdapter seriesAdapter;
    HomeChannelAdapter channelAdapter;
    HomeRecentAdapter recentAdapter;

    DatabaseHelper databaseHelper;
    CircleIndicator circleIndicator;
    LinearLayout lytRecent, lytCategory, lytMovie, lytSeries, lytChannel;
    RelativeLayout lytSlider;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        FadingEdgeLayout feRecent = rootView.findViewById(R.id.feRecent);
        FadingEdgeLayout feCategory = rootView.findViewById(R.id.feCategory);
        FadingEdgeLayout feMovie = rootView.findViewById(R.id.feMovie);
        FadingEdgeLayout feSeries = rootView.findViewById(R.id.feSeries);
        FadingEdgeLayout feChannel = rootView.findViewById(R.id.feChannel);

        IsRTL.changeShadowInRtl(requireActivity(), feRecent);
        IsRTL.changeShadowInRtl(requireActivity(), feCategory);
        IsRTL.changeShadowInRtl(requireActivity(), feMovie);
        IsRTL.changeShadowInRtl(requireActivity(), feSeries);
        IsRTL.changeShadowInRtl(requireActivity(), feChannel);

        databaseHelper = new DatabaseHelper(getActivity());

        LinearLayout mAdViewLayout = rootView.findViewById(R.id.adView);
        BannerAds.ShowBannerAds(getActivity(), mAdViewLayout);

        recentList = new ArrayList<>();
        movieList = new ArrayList<>();
        categoryList = new ArrayList<>();
        seriesList = new ArrayList<>();
        channelList = new ArrayList<>();
        sliderList = new ArrayList<>();

        mProgressBar = rootView.findViewById(R.id.progressBar1);
        lyt_not_found = rootView.findViewById(R.id.lyt_not_found);
        nestedScrollView = rootView.findViewById(R.id.nestedScrollView);
        viewPager = rootView.findViewById(R.id.viewPager);
        circleIndicator = rootView.findViewById(R.id.indicator_unselected_background);

        categoryViewAll = rootView.findViewById(R.id.textCategoryViewAll);
        movieViewAll = rootView.findViewById(R.id.textLatestMovieViewAll);
        seriesViewAll = rootView.findViewById(R.id.textTVSeriesViewAll);
        channelViewAll = rootView.findViewById(R.id.textLatestChannelViewAll);
        recentViewAll = rootView.findViewById(R.id.textRecentViewAll);
        lytSlider = rootView.findViewById(R.id.lytSlider);

        lytRecent = rootView.findViewById(R.id.lytHomeRecent);
        lytCategory = rootView.findViewById(R.id.lytHomeTVCategory);
        lytMovie = rootView.findViewById(R.id.lytHomeLatestMovie);
        lytSeries = rootView.findViewById(R.id.lytHomeTVSeries);
        lytChannel = rootView.findViewById(R.id.lytHomeLatestChannel);

        rvCategory = rootView.findViewById(R.id.rv_category);
        rvMovie = rootView.findViewById(R.id.rv_latest_movie);
        rvSeries = rootView.findViewById(R.id.rv_tv_series);
        rvChannel = rootView.findViewById(R.id.rv_latest_channel);
        rvRecent = rootView.findViewById(R.id.rv_recent);

        rvCategory.setHasFixedSize(true);
        rvCategory.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        rvCategory.setFocusable(false);
        rvCategory.setNestedScrollingEnabled(false);

        rvMovie.setHasFixedSize(true);
        rvMovie.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        rvMovie.setFocusable(false);
        rvMovie.setNestedScrollingEnabled(false);

        rvSeries.setHasFixedSize(true);
        rvSeries.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        rvSeries.setFocusable(false);
        rvSeries.setNestedScrollingEnabled(false);

        rvChannel.setHasFixedSize(true);
        rvChannel.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        rvChannel.setFocusable(false);
        rvChannel.setNestedScrollingEnabled(false);

        rvRecent.setHasFixedSize(true);
        rvRecent.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        rvRecent.setFocusable(false);
        rvRecent.setNestedScrollingEnabled(false);

        if (NetworkUtils.isConnected(getActivity())) {
            getHome();
        } else {
            Toast.makeText(getActivity(), getString(R.string.conne_msg1), Toast.LENGTH_SHORT).show();
        }

        return rootView;
    }

    private void getHome() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
        jsObj.addProperty("method_name", "get_home");
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

                    JSONArray sliderArray = liveTVJson.getJSONArray("banner");
                    for (int i = 0; i < sliderArray.length(); i++) {
                        JSONObject jsonObject = sliderArray.getJSONObject(i);
                        ItemSlider itemSlider = new ItemSlider();
                        itemSlider.setId(jsonObject.getString("id"));
                        itemSlider.setSliderTitle(jsonObject.getString("title"));
                        itemSlider.setSliderSubTitle(jsonObject.getString("sub_title"));
                        itemSlider.setSliderImage(jsonObject.getString("slide_image"));
                        itemSlider.setSliderType(jsonObject.getString("type"));
                        sliderList.add(itemSlider);
                    }


                    JSONArray categoryArray = liveTVJson.getJSONArray("cat_list");
                    for (int i = 0; i < categoryArray.length(); i++) {
                        JSONObject jsonObject = categoryArray.getJSONObject(i);
                        ItemCategory itemCategory = new ItemCategory();
                        itemCategory.setCategoryId(jsonObject.getString(Constant.CATEGORY_CID));
                        itemCategory.setCategoryName(jsonObject.getString(Constant.CATEGORY_NAME));
                        itemCategory.setCategoryImage(jsonObject.getString(Constant.CATEGORY_IMAGE));
                        categoryList.add(itemCategory);
                    }

                    JSONArray movieArray = liveTVJson.getJSONArray("latest_movies");
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

                    JSONArray seriesArray = liveTVJson.getJSONArray("tv_series");
                    for (int i = 0; i < seriesArray.length(); i++) {
                        JSONObject jsonObject = seriesArray.getJSONObject(i);
                        ItemSeries itemSeries = new ItemSeries();
                        itemSeries.setId(jsonObject.getString(Constant.SERIES_ID));
                        itemSeries.setSeriesName(jsonObject.getString(Constant.SERIES_TITLE));
                        itemSeries.setSeriesPoster(jsonObject.getString(Constant.SERIES_POSTER));
                        seriesList.add(itemSeries);
                    }

                    JSONArray channelArray = liveTVJson.getJSONArray("latest_channels");
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
        if (!sliderList.isEmpty()) {
            sliderAdapter = new SliderAdapter(requireActivity(), sliderList);
            viewPager.setAdapter(sliderAdapter);
            circleIndicator.setViewPager(viewPager);
        } else {
            lytSlider.setVisibility(View.GONE);
        }

        if (!categoryList.isEmpty()) {
            categoryAdapter = new HomeCategoryAdapter(getActivity(), categoryList);
            rvCategory.setAdapter(categoryAdapter);

            categoryAdapter.setOnItemClickListener(new RvOnClickListener() {
                @Override
                public void onItemClick(int position) {
                    String categoryName = categoryList.get(position).getCategoryName();
                    String categoryId = categoryList.get(position).getCategoryId();
                    Bundle bundle = new Bundle();
                    bundle.putString("Id", categoryId);
                    ChannelFragment channelFragment = new ChannelFragment();
                    channelFragment.setArguments(bundle);
                    changeFragment(channelFragment, categoryName);
                }
            });

        } else {
            lytCategory.setVisibility(View.GONE);
        }

        if (!movieList.isEmpty()) {
            movieAdapter = new HomeMovieAdapter(getActivity(), movieList);
            rvMovie.setAdapter(movieAdapter);

            movieAdapter.setOnItemClickListener(new RvOnClickListener() {
                @Override
                public void onItemClick(int position) {
                    String movieId = movieList.get(position).getId();
                    Intent intent = new Intent(getActivity(), MovieDetailsActivity.class);
                    intent.putExtra("Id", movieId);
                    startActivity(intent);
                }
            });

        } else {
            lytMovie.setVisibility(View.GONE);
        }

        if (!seriesList.isEmpty()) {
            seriesAdapter = new HomeSeriesAdapter(getActivity(), seriesList);
            rvSeries.setAdapter(seriesAdapter);

            seriesAdapter.setOnItemClickListener(new RvOnClickListener() {
                @Override
                public void onItemClick(int position) {
                    String seriesId = seriesList.get(position).getId();
                    Intent intent = new Intent(getActivity(), SeriesDetailsActivity.class);
                    intent.putExtra("Id", seriesId);
                    startActivity(intent);
                }
            });

        } else {
            lytSeries.setVisibility(View.GONE);
        }

        if (!channelList.isEmpty()) {
            channelAdapter = new HomeChannelAdapter(getActivity(), channelList);
            rvChannel.setAdapter(channelAdapter);

            channelAdapter.setOnItemClickListener(new RvOnClickListener() {
                @Override
                public void onItemClick(int position) {
                    String tvId = channelList.get(position).getId();
                    Intent intent = new Intent(getActivity(), TVDetailsActivity.class);
                    intent.putExtra("Id", tvId);
                    startActivity(intent);
                }
            });
            
        } else {
            lytChannel.setVisibility(View.GONE);
        }

        recentViewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFragment(new RecentFragment(), getString(R.string.menu_recent));
            }
        });

        categoryViewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFragment(new CategoryFragment(), getString(R.string.home_category));
            }
        });

        movieViewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFragment(new LatestMovieFragment(), getString(R.string.home_latest_movie));
            }
        });

        seriesViewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFragment(new SeriesFragment(), getString(R.string.home_tv_series));
            }
        });

        channelViewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFragment(new LatestChannelFragment(), getString(R.string.home_latest_channel));
            }
        });

    }

    private void changeFragment(Fragment fragment, String Name) {
        FragmentManager fm = getFragmentManager();
        assert fm != null;
        FragmentTransaction ft = fm.beginTransaction();
        ft.hide(HomeFragment.this);
        ft.add(R.id.Container, fragment, Name);
        ft.addToBackStack(Name);
        ft.commit();
        ((MainActivity) requireActivity()).setToolbarTitle(Name);
    }

    private void getRecent() {
        recentList = databaseHelper.getRecent(true);
        if (!recentList.isEmpty()) {
            recentAdapter = new HomeRecentAdapter(getActivity(), recentList);
            rvRecent.setAdapter(recentAdapter);

            recentAdapter.setOnItemClickListener(new RvOnClickListener() {
                @Override
                public void onItemClick(int position) {
                    ItemRecent itemRecent = recentList.get(position);
                    Class<?> aClass;
                    String recentId = itemRecent.getId();
                    String recentType = itemRecent.getRecentType();
                    switch (recentType) {
                        case "movie":
                            aClass = MovieDetailsActivity.class;
                            break;
                        case "series":
                            aClass = SeriesDetailsActivity.class;
                            break;
                        default:
                            aClass = TVDetailsActivity.class;
                            break;
                    }
                    Intent intent = new Intent(getActivity(), aClass);
                    intent.putExtra("Id", recentId);
                    startActivity(intent);
                }
            });

        } else {
            lytRecent.setVisibility(View.GONE);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        getRecent();
    }
}
