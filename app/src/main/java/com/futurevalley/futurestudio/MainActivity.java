package com.futurevalley.futurestudio;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.futurevalley.fragment.CategoryFragment;
import com.futurevalley.fragment.FavouriteTabFragment;
import com.futurevalley.fragment.HomeFragment;
import com.futurevalley.fragment.MovieTabFragment;
import com.futurevalley.fragment.SeriesFragment;
import com.futurevalley.fragment.SettingFragment;
import com.futurevalley.util.BannerAds;
import com.futurevalley.util.Constant;
import com.futurevalley.util.IsRTL;
import com.google.android.material.navigation.NavigationView;
import com.ixidev.gdpr.GDPRChecker;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    private FragmentManager fragmentManager;
    boolean doubleBackToExitPressedOnce = false;
    MyApplication myApplication;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        IsRTL.ifSupported(this);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        navigationView = findViewById(R.id.navigation_view);
        drawerLayout = findViewById(R.id.drawer_layout);
        fragmentManager = getSupportFragmentManager();
        myApplication = MyApplication.getInstance();

        new GDPRChecker()
                .withContext(MainActivity.this)
                .withPrivacyUrl(getString(R.string.privacy_url)) // your privacy url
                .withPublisherIds(Constant.adMobPublisherId) // your admob account Publisher id
                //.withTestMode("9424DF76F06983D1392E609FC074596C") // remove this on real project
                .check();

        LinearLayout mAdViewLayout = findViewById(R.id.adView);
        BannerAds.ShowBannerAds(this, mAdViewLayout);

        HomeFragment homeFragment = new HomeFragment();
        loadFrag(homeFragment, getString(R.string.menu_home), fragmentManager);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                drawerLayout.closeDrawers();
                switch (menuItem.getItemId()) {
                    case R.id.menu_go_home:
                        HomeFragment homeFragment = new HomeFragment();
                        loadFrag(homeFragment, getString(R.string.menu_home), fragmentManager);
                        return true;
                    case R.id.menu_go_movie:
                        MovieTabFragment movieTabFragment = new MovieTabFragment();
                        loadFrag(movieTabFragment, getString(R.string.menu_movie), fragmentManager);
                        return true;
                    case R.id.menu_go_tv_series:
                        SeriesFragment seriesFragment = new SeriesFragment();
                        loadFrag(seriesFragment, getString(R.string.menu_tv_series), fragmentManager);
                        return true;
                    case R.id.menu_go_tv_channel:
                        CategoryFragment categoryFragment = new CategoryFragment();
                        loadFrag(categoryFragment, getString(R.string.menu_tv_channel), fragmentManager);
                        return true;
                    case R.id.menu_go_favourite:
                        FavouriteTabFragment favouriteTabFragment = new FavouriteTabFragment();
                        loadFrag(favouriteTabFragment, getString(R.string.menu_favourite), fragmentManager);
                        return true;
                    case R.id.menu_go_profile:
                        Intent intentProfile = new Intent(MainActivity.this, ProfileActivity.class);
                        startActivity(intentProfile);
                        return true;
                    case R.id.menu_go_setting:
                        SettingFragment settingFragment = new SettingFragment();
                        loadFrag(settingFragment, getString(R.string.menu_setting), fragmentManager);
                        return true;
                    case R.id.menu_go_logout:
                        logOut();
                        return true;
                    case R.id.menu_go_log_in:
                        Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                        return true;
                    default:
                        return true;
                }
            }
        });

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        toolbar.setNavigationIcon(R.drawable.ic_side_nav);

    }

    public void loadFrag(Fragment f1, String name, FragmentManager fm) {
        for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.Container, f1, name);
        ft.commit();
        setToolbarTitle(name);
    }

    public void setToolbarTitle(String Title) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(Title);
        }
    }

    public void setHeader() {
        if (myApplication.getIsLogin() && navigationView != null) {
            View header = navigationView.getHeaderView(0);
            TextView txtHeaderName = header.findViewById(R.id.nav_name);
            TextView txtHeaderEmail = header.findViewById(R.id.nav_email);

            txtHeaderName.setText(myApplication.getUserName());
            txtHeaderEmail.setText(myApplication.getUserEmail());
        }
        if (myApplication.getIsLogin()) {
            navigationView.getMenu().findItem(R.id.menu_go_log_in).setVisible(false);
            navigationView.getMenu().findItem(R.id.menu_go_profile).setVisible(true);
            navigationView.getMenu().findItem(R.id.menu_go_logout).setVisible(true);
        } else {
            navigationView.getMenu().findItem(R.id.menu_go_log_in).setVisible(true);
            navigationView.getMenu().findItem(R.id.menu_go_profile).setVisible(false);
            navigationView.getMenu().findItem(R.id.menu_go_logout).setVisible(false);
        }
    }

    private void logOut() {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle(getString(R.string.menu_log_out))
                .setMessage(getString(R.string.logout_msg))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        myApplication.saveIsLogin(false);
                        Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(R.drawable.ic_logout)
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setHeader();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);
        final MenuItem searchMenuItem = menu.findItem(R.id.search);
        final SearchView searchView = (SearchView) searchMenuItem.getActionView();

        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // TODO Auto-generated method stub
                if (!hasFocus) {
                    searchMenuItem.collapseActionView();
                    searchView.setQuery("", false);
                }
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String arg0) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(MainActivity.this, SearchHorizontalActivity.class);
                intent.putExtra("search", arg0);
                startActivity(intent);
                searchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String arg0) {
                // TODO Auto-generated method stub
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if (fragmentManager.getBackStackEntryCount() != 0) {
            String tag = fragmentManager.getFragments().get(fragmentManager.getBackStackEntryCount() - 1).getTag();
            setToolbarTitle(tag);
            super.onBackPressed();
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, getString(R.string.back_key), Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
    }
}
