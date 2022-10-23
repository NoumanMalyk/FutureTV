package com.futurevalley.dialog;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.futurevalley.futurestudio.MyApplication;
import com.futurevalley.futurestudio.R;
import com.futurevalley.util.API;
import com.futurevalley.util.Constant;
import com.futurevalley.util.NetworkUtils;
import com.github.ornolfr.ratingview.RatingView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class RateDialog extends BaseDialog {

    private Activity activity;
    private ProgressBar progressBar;
    private ImageView imgClose;
    private LinearLayout lytRate;
    private Button btnSubmit;
    private RatingView ratingView;
    private String postId, postType;
    private RateDialogListener rateDialogListener;
    private MyApplication myApplication;
    private ProgressDialog pDialog;

    RateDialog(Activity activity, String postId, String postType) {
        super(activity, R.style.Theme_AppCompat_Translucent);
        this.activity = activity;
        this.postId = postId;
        this.postType = postType;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_rating);
        progressBar = findViewById(R.id.progressBar);
        imgClose = findViewById(R.id.imageView_close_rating);
        lytRate = findViewById(R.id.lytRate);
        btnSubmit = findViewById(R.id.button_rate_dialog);
        ratingView = findViewById(R.id.ratingView);
        pDialog = new ProgressDialog(activity);

        myApplication = MyApplication.getInstance();

        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                rateDialogListener.cancel();
            }
        });

        if (NetworkUtils.isConnected(activity)) {
            getUserRating();
        } else {
            showToast(activity.getString(R.string.conne_msg1));
        }

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String rate = String.valueOf(ratingView.getRating());
                if (!rate.equals("0")) {
                    if (NetworkUtils.isConnected(activity)) {
                        sentRating(rate);
                    } else {
                        showToast(activity.getString(R.string.conne_msg1));
                    }
                }
            }
        });
    }

    private void getUserRating() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
        jsObj.addProperty("method_name", "my_rating");
        jsObj.addProperty("post_id", postId);
        jsObj.addProperty("user_id", myApplication.getUserId());
        jsObj.addProperty("type", postType);
        params.put("data", API.toBase64(jsObj.toString()));

        client.post(Constant.API_URL, params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                lytRate.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                lytRate.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);

                String result = new String(responseBody);
                try {
                    JSONObject mainJson = new JSONObject(result);
                    JSONArray jsonArray = mainJson.getJSONArray(Constant.ARRAY_NAME);
                    JSONObject objComment = jsonArray.getJSONObject(0);
                    if (objComment.getString(Constant.SUCCESS).equals("1")) {
                        ratingView.setRating(Float.parseFloat(objComment.getString(Constant.USER_RATE)));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                lytRate.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }

        });

    }

    private void sentRating(String rating) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
        jsObj.addProperty("method_name", "user_rating");
        jsObj.addProperty("post_id", postId);
        jsObj.addProperty("user_id", myApplication.getUserId());
        jsObj.addProperty("rate", rating);
        jsObj.addProperty("type", postType);
        params.put("data", API.toBase64(jsObj.toString()));

        client.post(Constant.API_URL, params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                showProgressDialog();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                dismissProgressDialog();
                String result = new String(responseBody);
                try {
                    JSONObject mainJson = new JSONObject(result);
                    JSONArray jsonArray = mainJson.getJSONArray(Constant.ARRAY_NAME);
                    JSONObject objComment = jsonArray.getJSONObject(0);
                    if (objComment.getString(Constant.SUCCESS).equals("1")) {
                        showToast(objComment.getString(Constant.MSG));
                        rateDialogListener.confirm(objComment.getString(Constant.CHANNEL_AVG_RATE));
                        dismiss();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                dismissProgressDialog();
            }

        });
    }

    private void showProgressDialog() {
        pDialog.setMessage(activity.getString(R.string.loading));
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();
    }

    private void dismissProgressDialog() {
        if (pDialog != null && pDialog.isShowing()) {
            pDialog.dismiss();
        }
    }

    public void showToast(String msg) {
        Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
    }

    public interface RateDialogListener {
        void confirm(String rateAvg);

        void cancel();
    }

    public void setRateDialogListener(RateDialogListener rateDialogListener) {
        this.rateDialogListener = rateDialogListener;
    }
}
