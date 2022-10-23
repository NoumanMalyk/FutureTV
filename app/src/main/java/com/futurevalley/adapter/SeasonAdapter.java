package com.futurevalley.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.futurevalley.item.ItemSeason;
import com.futurevalley.futurestudio.R;
import com.futurevalley.util.RvOnClickListener;

import java.util.ArrayList;

public class SeasonAdapter extends RecyclerView.Adapter<SeasonAdapter.ItemRowHolder> {

    private ArrayList<ItemSeason> dataList;
    private Context mContext;
    private RvOnClickListener clickListener;
    private int selectSeason;

    public SeasonAdapter(Context context, ArrayList<ItemSeason> dataList, int position) {
        this.dataList = dataList;
        this.mContext = context;
        this.selectSeason = position;
    }

    @NonNull
    @Override
    public ItemRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_season_item, parent, false);
        return new ItemRowHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ItemRowHolder holder, final int position) {
        final ItemSeason singleItem = dataList.get(position);
        holder.text.setText(singleItem.getSeasonName());

        if (selectSeason == position) {
            holder.lytSeason.setBackgroundColor(mContext.getResources().getColor(R.color.yellow));
        } else {
            holder.lytSeason.setBackgroundColor(mContext.getResources().getColor(R.color.transparent));
        }

        holder.lytSeason.setOnClickListener(new View.OnClickListener() {
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
        TextView text;
        LinearLayout lytSeason;

        ItemRowHolder(View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.textSeasonName);
            lytSeason = itemView.findViewById(R.id.rootLytSeason);
        }
    }
}
