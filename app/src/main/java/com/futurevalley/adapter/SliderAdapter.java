package com.futurevalley.adapter;

import android.app.Activity;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.cardview.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.futurevalley.item.ItemSlider;
import com.futurevalley.futurestudio.MovieDetailsActivity;
import com.futurevalley.futurestudio.R;
import com.futurevalley.futurestudio.SeriesDetailsActivity;
import com.futurevalley.futurestudio.TVDetailsActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SliderAdapter extends PagerAdapter {

    private LayoutInflater inflater;
    private Activity context;
    private ArrayList<ItemSlider> mList;

    public SliderAdapter(Activity context, ArrayList<ItemSlider> itemChannels) {
        this.context = context;
        this.mList = itemChannels;
        inflater = context.getLayoutInflater();
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        View imageLayout = inflater.inflate(R.layout.row_slider_item, container, false);
        assert imageLayout != null;
        ImageView imageView = imageLayout.findViewById(R.id.image);
        TextView textTitle = imageLayout.findViewById(R.id.text);
        TextView textSubTitle = imageLayout.findViewById(R.id.textSub);
        CardView rootLayout = imageLayout.findViewById(R.id.cardView);

        textTitle.setSelected(true);

        final ItemSlider itemChannel = mList.get(position);
        Picasso.get().load(itemChannel.getSliderImage()).placeholder(R.drawable.place_holder_slider).into(imageView);
        textTitle.setText(itemChannel.getSliderTitle());
        textSubTitle.setText(itemChannel.getSliderSubTitle());

        rootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Class<?> aClass;
                String recentId = itemChannel.getId();
                String recentType = itemChannel.getSliderType();
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
                Intent intent = new Intent(context, aClass);
                intent.putExtra("Id", recentId);
                context.startActivity(intent);
            }
        });

        container.addView(imageLayout, 0);
        return imageLayout;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        (container).removeView((View) object);
    }
}
