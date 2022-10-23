package com.futurevalley.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.futurevalley.item.ItemChannel;
import com.futurevalley.futurestudio.R;
import com.futurevalley.util.PopUpAds;
import com.futurevalley.util.RvOnClickListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FavouriteChannelAdapter extends RecyclerView.Adapter<FavouriteChannelAdapter.ItemRowHolder> {

    private ArrayList<ItemChannel> dataList;
    private Context mContext;
    private RvOnClickListener clickListener;

    public FavouriteChannelAdapter(Context context, ArrayList<ItemChannel> dataList) {
        this.dataList = dataList;
        this.mContext = context;
    }

    @NonNull
    @Override
    public ItemRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_tv_item, parent, false);
        return new ItemRowHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ItemRowHolder holder, final int position) {
        final ItemChannel singleItem = dataList.get(position);

        holder.text.setText(singleItem.getChannelName());
        Picasso.get().load(singleItem.getImage()).placeholder(R.drawable.place_holder_channel).into(holder.image);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopUpAds.showInterstitialAds(mContext, holder.getAdapterPosition(), clickListener);
            }
        });
    }

    @Override
    public int getItemCount() {
        return (null != dataList ? dataList.size() : 0);
    }

    public void setOnItemClickListener(RvOnClickListener clickListener) {
        this.clickListener = clickListener;
    }

    class ItemRowHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView text;
        CardView cardView;

        ItemRowHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            text = itemView.findViewById(R.id.text);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }
}
