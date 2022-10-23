package com.futurevalley.fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.futurevalley.futurestudio.R;

public class IntroFragment extends Fragment {

    final static String LAYOUT_ID = "layoutid";

    public static IntroFragment newInstance(int layoutId) {
        IntroFragment pane = new IntroFragment();
        Bundle args = new Bundle();
        args.putInt(LAYOUT_ID, layoutId);
        pane.setArguments(args);
        return pane;
    }

    ImageView imageView;
    TextView textTitle, textDesc;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_intro, container, false);
        imageView = rootView.findViewById(R.id.image_intro);
        textTitle = rootView.findViewById(R.id.text);
        textDesc = rootView.findViewById(R.id.textDesc);
        assert getArguments() != null;
        int position = getArguments().getInt(LAYOUT_ID, -1);
        Integer[] Images = {R.drawable.intro_img1, R.drawable.intro_img2, R.drawable.intro_img3};
        String[] Title = {getResources().getString(R.string.intro_1_title),
                getResources().getString(R.string.intro_2_title),
                getResources().getString(R.string.intro_3_title)};
        String[] Desc = {getResources().getString(R.string.intro_1_desc),
                getResources().getString(R.string.intro_2_desc),
                getResources().getString(R.string.intro_3_desc)};
        imageView.setImageResource(Images[position]);
        textTitle.setText(Title[position]);
        textDesc.setText(Desc[position]);
        return rootView;
    }
}
