package com.futurevalley.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.futurevalley.item.ItemMovie;
import com.futurevalley.futurestudio.R;
import com.futurevalley.util.PopUpAds;
import com.futurevalley.util.RvOnClickListener;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class HomeMovieAdapter extends RecyclerView.Adapter<HomeMovieAdapter.ItemRowHolder> {

    private ArrayList<ItemMovie> dataList;
    private Context mContext;
    private RvOnClickListener clickListener;
    private boolean isRTL;

    public HomeMovieAdapter(Context context, ArrayList<ItemMovie> dataList) {
        this.dataList = dataList;
        this.mContext = context;
        isRTL = Boolean.parseBoolean(mContext.getString(R.string.isRTL));
    }

    @NonNull
    @Override
    public ItemRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_row_movie_item, parent, false);
        return new ItemRowHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ItemRowHolder holder, final int position) {
        final ItemMovie singleItem = dataList.get(position);

        holder.text.setText(singleItem.getMovieTitle());
        holder.textLanguage.setText(singleItem.getLanguageName());
        Picasso.get().load(singleItem.getMoviePoster()).placeholder(R.drawable.place_holder_movie).into(holder.image);

        try {
            GradientDrawable gd = new GradientDrawable();
            gd.setShape(GradientDrawable.RECTANGLE);
            gd.setColor(Color.parseColor(singleItem.getLanguageBackground()));
            if (isRTL) {
                gd.setCornerRadii(new float[]{40.0f, 40.0f, 0, 0, 0, 0, 40.0f, 40.0f});
            } else {
                gd.setCornerRadii(new float[]{0, 0, 40.0f, 40.0f, 40.0f, 40.0f, 0, 0});
            }
            holder.textLanguage.setBackground(gd);
        } catch (Exception e) {
            Log.d("error_show", e.toString());
        }

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
        RoundedImageView image;
        TextView text, textLanguage;
        CardView cardView;
        View view;

        ItemRowHolder(View itemView) {
            super(itemView);
            view = itemView.findViewById(R.id.view_movie_adapter);
            image = itemView.findViewById(R.id.image);
            text = itemView.findViewById(R.id.text);
            textLanguage = itemView.findViewById(R.id.textLang);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }
}
