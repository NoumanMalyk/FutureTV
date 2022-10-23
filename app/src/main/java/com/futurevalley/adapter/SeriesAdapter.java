package com.futurevalley.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.futurevalley.item.ItemSeries;
import com.futurevalley.futurestudio.MyApplication;
import com.futurevalley.futurestudio.R;
import com.futurevalley.util.PopUpAds;
import com.futurevalley.util.RvOnClickListener;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SeriesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<ItemSeries> dataList;
    private Context mContext;
    private RvOnClickListener clickListener;
    private int columnWidth;
    private final int VIEW_TYPE_LOADING = 0;
    private final int VIEW_TYPE_ITEM = 1;

    public SeriesAdapter(Context context, ArrayList<ItemSeries> dataList) {
        this.dataList = dataList;
        this.mContext = context;
        columnWidth = MyApplication.getInstance().getScreenWidth();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_tv_series_item, parent, false);
            return new ItemRowHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_loading_item, parent, false);
            return new ProgressViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, final int position) {
        if (viewHolder.getItemViewType() == VIEW_TYPE_ITEM) {
            final ItemRowHolder holder = (ItemRowHolder) viewHolder;
            final ItemSeries singleItem = dataList.get(position);
            holder.image.setLayoutParams(new RelativeLayout.LayoutParams(columnWidth, columnWidth / 3 + 80));
            holder.view.setLayoutParams(new RelativeLayout.LayoutParams(columnWidth, columnWidth / 3 + 80));

            holder.text.setText(singleItem.getSeriesName());
            Picasso.get().load(singleItem.getSeriesPoster()).placeholder(R.drawable.place_holder_movie).into(holder.image);

            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopUpAds.showInterstitialAds(mContext, holder.getAdapterPosition(), clickListener);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return (null != dataList ? dataList.size() + 1 : 0);
    }

    public void hideHeader() {
        ProgressViewHolder.progressBar.setVisibility(View.GONE);
    }

    private boolean isHeader(int position) {
        return position == dataList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return isHeader(position) ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    public void setOnItemClickListener(RvOnClickListener clickListener) {
        this.clickListener = clickListener;
    }

    class ItemRowHolder extends RecyclerView.ViewHolder {
        RoundedImageView image;
        TextView text;
        CardView cardView;
        View view;

        ItemRowHolder(View itemView) {
            super(itemView);
            view = itemView.findViewById(R.id.view_movie_adapter);
            image = itemView.findViewById(R.id.image);
            text = itemView.findViewById(R.id.text);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }

    static class ProgressViewHolder extends RecyclerView.ViewHolder {
        static ProgressBar progressBar;

        ProgressViewHolder(View v) {
            super(v);
            progressBar = v.findViewById(R.id.progressBar);
        }
    }
}
