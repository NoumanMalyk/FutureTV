package com.futurevalley.futurestudio;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.futurevalley.adapter.CommentAdapter;
import com.futurevalley.item.ItemComment;
import com.futurevalley.util.API;
import com.futurevalley.util.Constant;
import com.futurevalley.util.Events;
import com.futurevalley.util.GlobalBus;
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

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class AllCommentActivity extends AppCompatActivity {

    ProgressBar mProgressBar;
    RecyclerView rvComment;
    TextView textNoComment;
    EditText edtComment;
    ImageView imgSent;
    ArrayList<ItemComment> mListItemComment;
    CommentAdapter commentAdapter;
    String postId, postType;
    ProgressDialog pDialog;
    MyApplication myApplication;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_comment);
        IsRTL.ifSupported(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.all_comment));
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        Intent intent = getIntent();
        postId = intent.getStringExtra("postId");
        postType = intent.getStringExtra("postType");

        pDialog = new ProgressDialog(this);
        myApplication = MyApplication.getInstance();

        mProgressBar = findViewById(R.id.progressBar1);
        rvComment = findViewById(R.id.recyclerView);
        textNoComment = findViewById(R.id.textView_noComment_md);
        edtComment = findViewById(R.id.edt_comment);
        imgSent = findViewById(R.id.image_sent);
        mListItemComment = new ArrayList<>();

        rvComment.setHasFixedSize(true);
        rvComment.setLayoutManager(new LinearLayoutManager(AllCommentActivity.this, LinearLayoutManager.VERTICAL, false));
        rvComment.setFocusable(false);
        rvComment.setNestedScrollingEnabled(false);

        if (NetworkUtils.isConnected(AllCommentActivity.this)) {
            getComment();
        } else {
            showToast(getString(R.string.conne_msg1));
        }


        imgSent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comment = edtComment.getText().toString();
                if (!comment.isEmpty()) {
                    if (NetworkUtils.isConnected(AllCommentActivity.this)) {
                        if (myApplication.getIsLogin()) {
                            sentComment(comment);
                        } else {
                            String message = getString(R.string.login_first, getString(R.string.login_first_comment));
                            showToast(message);

                            Intent intentLogin = new Intent(AllCommentActivity.this, SignInActivity.class);
                            intentLogin.putExtra("isOtherScreen", true);
                            startActivity(intentLogin);
                        }
                    } else {
                        showToast(getString(R.string.conne_msg1));
                    }
                }
            }
        });
    }

    private void getComment() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
        jsObj.addProperty("method_name", "get_user_comment");
        jsObj.addProperty("post_id", postId);
        jsObj.addProperty("type", postType);
        params.put("data", API.toBase64(jsObj.toString()));

        client.post(Constant.API_URL, params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                mProgressBar.setVisibility(View.VISIBLE);
                rvComment.setVisibility(View.GONE);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                mProgressBar.setVisibility(View.GONE);
                rvComment.setVisibility(View.VISIBLE);

                String result = new String(responseBody);
                try {
                    JSONObject mainJson = new JSONObject(result);
                    JSONArray jsonArray = mainJson.getJSONArray(Constant.ARRAY_NAME);
                    if (jsonArray.length() != 0) {
                        mListItemComment.clear();
                        for (int j = 0; j < jsonArray.length(); j++) {
                            JSONObject objComment = jsonArray.getJSONObject(j);
                            ItemComment itemComment = new ItemComment();
                            itemComment.setUserName(objComment.getString(Constant.COMMENT_NAME));
                            itemComment.setCommentText(objComment.getString(Constant.COMMENT_DESC));
                            itemComment.setCommentDate(objComment.getString(Constant.COMMENT_DATE));
                            mListItemComment.add(itemComment);
                        }
                    }

                    if (!mListItemComment.isEmpty()) {
                        commentAdapter = new CommentAdapter(AllCommentActivity.this, mListItemComment);
                        rvComment.setAdapter(commentAdapter);
                    } else {
                        textNoComment.setVisibility(View.VISIBLE);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                mProgressBar.setVisibility(View.GONE);
                textNoComment.setVisibility(View.VISIBLE);
            }

        });
    }

    private void sentComment(String comment) {
        edtComment.getText().clear();
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
        jsObj.addProperty("method_name", "user_comment");
        jsObj.addProperty("post_id", postId);
        jsObj.addProperty("user_id", myApplication.getUserId());
        jsObj.addProperty("comment_text", comment);
        jsObj.addProperty("type", postType);
        jsObj.addProperty("is_limit", "false");
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
                    String strMessage = mainJson.getString(Constant.MSG);
                    showToast(strMessage);

                    JSONArray jsonArray = mainJson.getJSONArray(Constant.ARRAY_NAME);
                    if (jsonArray.length() != 0) {
                        mListItemComment.clear();
                        for (int j = 0; j < jsonArray.length(); j++) {
                            JSONObject objComment = jsonArray.getJSONObject(j);
                            ItemComment itemComment = new ItemComment();
                            itemComment.setUserName(objComment.getString(Constant.COMMENT_NAME));
                            itemComment.setCommentText(objComment.getString(Constant.COMMENT_DESC));
                            itemComment.setCommentDate(objComment.getString(Constant.COMMENT_DATE));
                            mListItemComment.add(itemComment);

                        }
                    }

                    if (!mListItemComment.isEmpty()) {
                        commentAdapter = new CommentAdapter(AllCommentActivity.this, mListItemComment);
                        rvComment.setAdapter(commentAdapter);
                        textNoComment.setVisibility(View.GONE);
                        updatePreviousComment();
                    } else {
                        textNoComment.setVisibility(View.VISIBLE);
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

    private void updatePreviousComment() {
        ArrayList<ItemComment> updatedComment = new ArrayList<>();
        if (mListItemComment.size() > 5) {
            for (int i = 0; i < 5; i++) {
                ItemComment itemComment = new ItemComment();
                itemComment.setUserName(mListItemComment.get(i).getUserName());
                itemComment.setCommentText(mListItemComment.get(i).getCommentText());
                itemComment.setCommentDate(mListItemComment.get(i).getCommentDate());
                updatedComment.add(itemComment);
            }
        } else {
            updatedComment = mListItemComment;
        }
        Events.Comment commentNotify = new Events.Comment();
        commentNotify.setItemComments(updatedComment);
        commentNotify.setPostType(postType);
        GlobalBus.getBus().post(commentNotify);
    }

    public void showProgressDialog() {
        pDialog.setMessage(getString(R.string.loading));
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();
    }

    public void dismissProgressDialog() {
        if (pDialog != null && pDialog.isShowing()) {
            pDialog.dismiss();
        }
    }

    public void showToast(String msg) {
        Toast.makeText(AllCommentActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;

            default:
                return super.onOptionsItemSelected(menuItem);
        }
        return true;
    }
}
