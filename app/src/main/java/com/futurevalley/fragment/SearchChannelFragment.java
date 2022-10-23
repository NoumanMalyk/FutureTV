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

import com.futurevalley.adapter.ChannelAdapter;
import com.futurevalley.item.ItemChannel;
import com.futurevalley.futurestudio.R;
import com.futurevalley.futurestudio.TVDetailsActivity;
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

public class SearchChannelFragment extends Fragment {

    ArrayList<ItemChannel> mListItem;
    public RecyclerView recyclerView;
    ChannelAdapter adapter;
    private ProgressBar progressBar;
    private LinearLayout lyt_not_found;

    private static final String Search = "Search";
    String selectedSearch;

    boolean isVisible = false, isLoaded = false;
    boolean isFirst = true, isOver = false;
    private int pageIndex = 1;

    public static SearchChannelFragment newInstance(String SId) {
        SearchChannelFragment f = new SearchChannelFragment();
        Bundle args = new Bundle();
        args.putString(Search, SId);
        f.setArguments(args);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.row_recyclerview, container, false);
        if (getArguments() != null) {
            selectedSearch = getArguments().getString(Search);
        }
        mListItem = new ArrayList<>();
        lyt_not_found = rootView.findViewById(R.id.lyt_not_found);
        progressBar = rootView.findViewById(R.id.progressBar);
        recyclerView = rootView.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(layoutManager);

        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (adapter.getItemViewType(position)) {
                    case 0:
                        return 2;
                    default:
                        return 1;
                }
            }
        });

        if (isVisible && !isLoaded) {
            if (NetworkUtils.isConnected(getActivity())) {
                getChannel();
            } else {
                Toast.makeText(getActivity(), getString(R.string.conne_msg1), Toast.LENGTH_SHORT).show();
            }
            isLoaded = true;
        }

        recyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                if (!isOver) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            pageIndex++;
                            getChannel();
                        }
                    }, 1000);
                } else {
                    adapter.hideHeader();
                }
            }
        });

        return rootView;
    }

    private void getChannel() {

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
        jsObj.addProperty("method_name", "get_search_channels");
        jsObj.addProperty("search_text", selectedSearch);
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
                                if (!objJson.has(Constant.MSG)) {
                                    ItemChannel objItem = new ItemChannel();
                                    objItem.setId(objJson.getString(Constant.CHANNEL_ID));
                                    objItem.setChannelName(objJson.getString(Constant.CHANNEL_TITLE));
                                    objItem.setImage(objJson.getString(Constant.CHANNEL_IMAGE));
                                    mListItem.add(objItem);
                                }
                            }
                        }
                    } else {
                        isOver = true;
                        if (adapter != null) { // when there is no data in first time
                            adapter.hideHeader();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                displayData();
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
                adapter = new ChannelAdapter(getActivity(), mListItem);
                recyclerView.setAdapter(adapter);
            } else {
                adapter.notifyDataSetChanged();
            }

            adapter.setOnItemClickListener(new RvOnClickListener() {
                @Override
                public void onItemClick(int position) {
                    String tvId = mListItem.get(position).getId();
                    Intent intent = new Intent(getActivity(), TVDetailsActivity.class);
                    intent.putExtra("Id", tvId);
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

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        isVisible = isVisibleToUser;
        if (isVisibleToUser && isAdded() && !isLoaded) {
            if (NetworkUtils.isConnected(getActivity())) {
                getChannel();
            } else {
                Toast.makeText(getActivity(), getString(R.string.conne_msg1), Toast.LENGTH_SHORT).show();
            }
            isLoaded = true;
        }
        super.setUserVisibleHint(isVisibleToUser);
    }
}

