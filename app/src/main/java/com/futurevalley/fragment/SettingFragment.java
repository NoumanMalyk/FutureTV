package com.futurevalley.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.futurevalley.futurestudio.MainActivity;
import com.futurevalley.futurestudio.MyApplication;
import com.futurevalley.futurestudio.R;
import com.onesignal.OneSignal;

public class SettingFragment extends Fragment {

    LinearLayout lytRate, lytMore, lytShare, lytPrivacy, lytAbout;
    Switch notificationSwitch;
    MyApplication myApplication;
    ProgressDialog pDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_setting, container, false);
        pDialog = new ProgressDialog(getActivity());
        myApplication = MyApplication.getInstance();

        lytRate = rootView.findViewById(R.id.lytRateApp);
        lytMore = rootView.findViewById(R.id.lytMoreApp);
        lytShare = rootView.findViewById(R.id.lytShareApp);
        lytPrivacy = rootView.findViewById(R.id.lytPrivacy);
        lytAbout = rootView.findViewById(R.id.lytAbout);

        notificationSwitch = rootView.findViewById(R.id.switch_notification);

        notificationSwitch.setChecked(myApplication.getNotification());

        notificationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                myApplication.saveIsNotification(isChecked);
                OneSignal.unsubscribeWhenNotificationsAreDisabled(isChecked);
            }
        });

        lytRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rateApp();
            }
        });

        lytMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(getString(R.string.play_more_apps))));
            }
        });

        lytShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareApp();
            }
        });

        lytAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String about = getString(R.string.about);
                AboutFragment aboutFragment = new AboutFragment();
                changeFragment(aboutFragment, about);
            }
        });

        lytPrivacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String privacy = getString(R.string.privacy_policy);
                PrivacyFragment privacyFragment = new PrivacyFragment();
                changeFragment(privacyFragment, privacy);
            }
        });

        return rootView;
    }

    private void changeFragment(Fragment fragment, String Name) {
        FragmentManager fm = getFragmentManager();
        assert fm != null;
        FragmentTransaction ft = fm.beginTransaction();
        ft.hide(SettingFragment.this);
        ft.add(R.id.Container, fragment, Name);
        ft.addToBackStack(Name);
        ft.commit();
        ((MainActivity) requireActivity()).setToolbarTitle(Name);
    }

    private void shareApp() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.share_msg) + requireActivity().getPackageName());
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    private void rateApp() {
        final String appName = requireActivity().getPackageName();
        try {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("market://details?id="
                            + appName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id="
                            + appName)));
        }
    }
}
