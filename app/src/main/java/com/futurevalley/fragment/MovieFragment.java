package com.futurevalley.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.futurevalley.adapter.MovieAdapter;
import com.futurevalley.item.ItemMovie;
import com.futurevalley.futurestudio.MovieDetailsActivity;
import com.futurevalley.futurestudio.R;
import com.futurevalley.util.API;
import com.futurevalley.util.Constant;
import com.futurevalley.util.EndlessRecyclerViewScrollListener;
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

public class MovieFragment extends Fragment {

    ArrayList<ItemMovie> mListItem;
    public RecyclerView recyclerView;
    MovieAdapter adapter;
    private ProgressBar progressBar;
    private LinearLayout lyt_not_found;
    String Id;
    boolean isLanguage = true;
    boolean isFirst = true, isOver = false;
    private int pageIndex = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.row_recyclerview, container, false);
        if (getArguments() != null) {
            Id = getArguments().getString("Id");
            isLanguage = getArguments().getBoolean("isLanguage", true);
        }
        mListItem = new ArrayList<>();
        lyt_not_found = rootView.findViewById(R.id.lyt_not_found);
        progressBar = rootView.findViewById(R.id.progressBar);
        recyclerView = rootView.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
        recyclerView.setLayoutManager(layoutManager);

        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (adapter.getItemViewType(position)) {
                    case 0:
                        return 3;
                    default:
                        return 1;
                }
            }
        });

        if (NetworkUtils.isConnected(getActivity())) {
            getMovieItem();
        } else {
            Toast.makeText(getActivity(), getString(R.string.conne_msg1), Toast.LENGTH_SHORT).show();
        }

        recyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                if (!isOver) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            pageIndex++;
                            getMovieItem();
                        }
                    }, 1000);
                } else {
                    adapter.hideHeader();
                }
            }
        });

        return rootView;
    }

    private void getMovieItem() {

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
        if (isLanguage) {
            jsObj.addProperty("method_name", "get_movies_by_lang_id");
            jsObj.addProperty("lang_id", Id);
        } else {
            jsObj.addProperty("method_name", "get_movies_by_gen_id");
            jsObj.addProperty("genre_id", Id);
        }
        jsObj.addProperty("page", pageIndex);
        params.put("data", API.toBase64(jsObj.toString()));
        client.post(Constant.API_URL, params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                if (isFirst)
                    showProgress(true);

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (isFirst)
                    showProgress(false);

                String result = new String(responseBody);
                try {
                    JSONObject mainJson = new JSONObject(result);
                    JSONArray jsonArray = mainJson.getJSONArray(Constant.ARRAY_NAME);
                    JSONObject objJson;
                    if (jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            objJson = jsonArray.getJSONObject(i);
                            if (objJson.has(Constant.STATUS)) {
                                lyt_not_found.setVisibility(View.VISIBLE);
                            } else {
                                ItemMovie objItem = new ItemMovie();
                                objItem.setId(objJson.getString(Constant.MOVIE_ID));
                                objItem.setMovieTitle(objJson.getString(Constant.MOVIE_TITLE));
                                objItem.setMoviePoster(objJson.getString(Constant.MOVIE_POSTER));
                                objItem.setLanguageName(objJson.getString(Constant.MOVIE_LANGUAGE));
                                objItem.setLanguageBackground(objJson.getString(Constant.MOVIE_LANGUAGE_BACK));
                                mListItem.add(objItem);
                            }
                        }
                    } else {
                        isOver = true;
                        if (adapter != null) { // when there is no data in first time
                            adapter.hideHeader();
                        }
                    }
                    displayData();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                showProgress(false);
                lyt_not_found.setVisibility(View.VISIBLE);
            }

        });
    }

    private void displayData() {
        if (mListItem.size() == 0) {
            lyt_not_found.setVisibility(View.VISIBLE);
        } else {
            lyt_not_found.setVisibility(View.GONE);

            if (isFirst) {
                isFirst = false;
                adapter = new MovieAdapter(getActivity(), mListItem);
                recyclerView.setAdapter(adapter);
            } else {
                adapter.notifyDataSetChanged();
            }


            adapter.setOnItemClickListener(new RvOnClickListener() {
                @Override
                public void onItemClick(int position) {
                    String movieId = mListItem.get(position).getId();
                    Intent intent = new Intent(getActivity(), MovieDetailsActivity.class);
                    intent.putExtra("Id", movieId);
                    startActivity(intent);
                }
            });
        }

    }


    private void showProgress(boolean show) {
        if (show) {
            progressBar.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            lyt_not_found.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }
}

