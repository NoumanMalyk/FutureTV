package com.futurevalley.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.futurevalley.item.ItemEpisode;
import com.futurevalley.futurestudio.R;
import com.futurevalley.util.RvOnClickListener;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class EpisodeAdapter extends RecyclerView.Adapter<EpisodeAdapter.ItemRowHolder> {

    private ArrayList<ItemEpisode> dataList;
    private Context mContext;
    private RvOnClickListener clickListener;

    public EpisodeAdapter(Context context, ArrayList<ItemEpisode> dataList) {
        this.dataList = dataList;
        this.mContext = context;
    }

    @NonNull
    @Override
    public ItemRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_episode_item, parent, false);
        return new ItemRowHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ItemRowHolder holder, final int position) {
        final ItemEpisode singleItem = dataList.get(position);

        holder.text.setText(singleItem.getEpisodeTitle());
        Picasso.get().load(singleItem.getEpisodePoster()).placeholder(R.drawable.place_holder_channel).into(holder.image);

        if (singleItem.isPlaying()) {
            holder.textNowPlay.setVisibility(View.VISIBLE);
        } else {
            holder.textNowPlay.setVisibility(View.GONE);
        }

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.onItemClick(holder.getAdapterPosition());
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
        RoundedImageView image;
        TextView text, textNowPlay;
        CardView cardView;
        View view;

        ItemRowHolder(View itemView) {
            super(itemView);
            view = itemView.findViewById(R.id.view_movie_adapter);
            image = itemView.findViewById(R.id.image);
            text = itemView.findViewById(R.id.text);
            textNowPlay = itemView.findViewById(R.id.textNowPlay);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }
}
