package com.futurevalley.futurestudio;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.futurevalley.util.API;
import com.futurevalley.util.Constant;
import com.futurevalley.util.IsRTL;
import com.futurevalley.util.NetworkUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;


public class SplashActivity extends AppCompatActivity {

    MyApplication myApplication;
    private boolean mIsBackButtonPressed;
    private static final int SPLASH_DURATION = 2000;
    boolean isLoginDisable = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        IsRTL.ifSupported(this);
        myApplication = MyApplication.getInstance();
        myApplication.setScreenWidth(NetworkUtils.getScreenWidth(SplashActivity.this));
        if (NetworkUtils.isConnected(SplashActivity.this)) {
            checkLicense();
        } else {
            Toast.makeText(SplashActivity.this, getString(R.string.conne_msg1), Toast.LENGTH_SHORT).show();
        }
    }

    private void splashScreen() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!mIsBackButtonPressed) {
                    if (myApplication.getIsIntroduction()) {
                        if (isLoginDisable && myApplication.getIsLogin()) {
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        } else if (!isLoginDisable && myApplication.getIsLogin()) {
                            myApplication.saveIsLogin(false);
                            Toast.makeText(SplashActivity.this, getString(R.string.user_disable), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        } else {
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        }

                    } else {
                        Intent intent = new Intent(getApplicationContext(), IntroActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }

        }, SPLASH_DURATION);
    }

    @Override
    public void onBackPressed() {
        // set the flag to true so the next activity won't start up
        mIsBackButtonPressed = true;
        super.onBackPressed();
    }

    private void checkLicense() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
        jsObj.addProperty("method_name", "get_app_details");
        if (myApplication.getIsLogin()) {
            jsObj.addProperty("user_id", myApplication.getUserId());
        } else {
            jsObj.addProperty("user_id", "");
        }
        params.put("data", API.toBase64(jsObj.toString()));
        client.post(Constant.API_URL, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String result = new String(responseBody);
                try {
                    JSONObject mainJson = new JSONObject(result);
                    isLoginDisable = mainJson.getBoolean("user_status");
                    JSONArray jsonArray = mainJson.getJSONArray(Constant.ARRAY_NAME);
                    JSONObject objJson = jsonArray.getJSONObject(0);
                    if (objJson.has(Constant.MSG)) {
                        Toast.makeText(SplashActivity.this, objJson.getString(Constant.MSG), Toast.LENGTH_SHORT).show();
                    } else {
                        String packageName = objJson.getString("package_name");
                        Constant.isBanner = objJson.getBoolean("banner_ad");
                        Constant.isInterstitial = objJson.getBoolean("interstital_ad");
                        Constant.bannerId = objJson.getString("banner_ad_id");
                        Constant.interstitialId = objJson.getString("interstital_ad_id");
                        Constant.adMobPublisherId = objJson.getString("publisher_id");
                        Constant.AD_COUNT_SHOW = objJson.getInt("interstital_ad_click");
                        Constant.isAdMobBanner = objJson.getString("banner_ad_type").equals("admob");
                        Constant.isAdMobInterstitial = objJson.getString("interstital_ad_type").equals("admob");
                        if (packageName.isEmpty() || !packageName.equals(getPackageName())) {
                            invalidDialog();
                        } else {
                            splashScreen();
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            }

        });
    }

    private void invalidDialog() {
        new AlertDialog.Builder(SplashActivity.this)
                .setTitle(getString(R.string.invalid_license))
                .setMessage(getString(R.string.license_msg))
                .setCancelable(false)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setIcon(R.mipmap.ic_launcher)
                .show();
    }
}
