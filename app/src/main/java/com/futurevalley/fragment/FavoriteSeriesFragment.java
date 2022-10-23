package com.futurevalley.fragment;


import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.futurevalley.adapter.FavouriteSeriesAdapter;
import com.futurevalley.db.DatabaseHelper;
import com.futurevalley.item.ItemSeries;
import com.futurevalley.futurestudio.R;
import com.futurevalley.futurestudio.SeriesDetailsActivity;
import com.futurevalley.util.RvOnClickListener;

import java.util.ArrayList;


public class FavoriteSeriesFragment extends Fragment {

    ArrayList<ItemSeries> mListItem;
    public RecyclerView recyclerView;
    FavouriteSeriesAdapter adapter;
    TextView textView;
    DatabaseHelper databaseHelper;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_favourite, container, false);

        databaseHelper = new DatabaseHelper(getActivity());
        mListItem = new ArrayList<>();
        textView = rootView.findViewById(R.id.text_no);
        recyclerView = rootView.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mListItem = databaseHelper.getFavouriteSeries();
        displayData();
    }

    private void displayData() {

        adapter = new FavouriteSeriesAdapter(getActivity(), mListItem);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new RvOnClickListener() {
            @Override
            public void onItemClick(int position) {
                String seriesId = mListItem.get(position).getId();
                Intent intent = new Intent(getActivity(), SeriesDetailsActivity.class);
                intent.putExtra("Id", seriesId);
                startActivity(intent);
            }
        });

        if (adapter.getItemCount() == 0) {
            textView.setVisibility(View.VISIBLE);
        } else {
            textView.setVisibility(View.GONE);
        }
    }
}
