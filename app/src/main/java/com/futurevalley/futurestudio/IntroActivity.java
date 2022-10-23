package com.futurevalley.futurestudio;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.futurevalley.fragment.IntroFragment;
import com.futurevalley.util.IsRTL;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;


public class IntroActivity extends AppCompatActivity {

    View circle1, circle2, circle3;
    ViewPager pager;
    PagerAdapter pagerAdapter;
    Button btnNext, btnSkip;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        IsRTL.ifSupported(this);
        circle1 = findViewById(R.id.circle1);
        circle2 = findViewById(R.id.circle2);
        circle3 = findViewById(R.id.circle3);

        btnNext = findViewById(R.id.button_next);
        btnSkip = findViewById(R.id.button_skip);

        MyApplication.getInstance().saveIsIntroduction(true);

        pager = findViewById(R.id.view_pager);
        pagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        pager.setAdapter(pagerAdapter);
        setIndicator(0);

        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setIndicator(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(IntroActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(IntroActivity.this, SignInActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

    }

    private void setIndicator(int index) {
        switch (index) {
            case 0:
                circle1.setBackgroundResource(R.drawable.intro_circle_select);
                circle2.setBackgroundResource(R.drawable.intro_circle_unselect);
                circle3.setBackgroundResource(R.drawable.intro_circle_unselect);
                break;
            case 1:
                circle2.setBackgroundResource(R.drawable.intro_circle_select);
                circle1.setBackgroundResource(R.drawable.intro_circle_unselect);
                circle3.setBackgroundResource(R.drawable.intro_circle_unselect);
                break;
            case 2:
                circle3.setBackgroundResource(R.drawable.intro_circle_select);
                circle1.setBackgroundResource(R.drawable.intro_circle_unselect);
                circle2.setBackgroundResource(R.drawable.intro_circle_unselect);
                break;
        }
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

        ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            IntroFragment tp;
            tp = IntroFragment.newInstance(position);
            return tp;
        }

        @Override
        public int getCount() {
            return 3;
        }
    }
}
