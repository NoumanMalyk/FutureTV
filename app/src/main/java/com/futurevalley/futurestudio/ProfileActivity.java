package com.futurevalley.futurestudio;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.annotation.Nullable;
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
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.Length;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cz.msebera.android.httpclient.Header;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class ProfileActivity extends AppCompatActivity implements Validator.ValidationListener {

    ProgressBar mProgressBar;
    LinearLayout lyt_not_found;
    ScrollView scrollView;
    MyApplication myApplication;

    @NotEmpty
    EditText edtName;
    @Email
    EditText edtEmail;
    EditText edtPassword;
    @Length(max = 14, min = 6, message = "Enter valid Phone Number")
    EditText edtPhone;
    String strName, strEmail, strPassword, strMobi, strMessage;
    private Validator validator;
    ProgressDialog pDialog;
    Button btnSubmit;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        IsRTL.ifSupported(this);
        myApplication = MyApplication.getInstance();

        mProgressBar = findViewById(R.id.progressBar1);
        lyt_not_found = findViewById(R.id.lyt_not_found);
        scrollView = findViewById(R.id.scrollView);
        edtName = findViewById(R.id.editText_name_register);
        edtEmail = findViewById(R.id.editText_email_register);
        edtPassword = findViewById(R.id.editText_password_register);
        edtPhone = findViewById(R.id.editText_phoneNo_register);
        btnSubmit = findViewById(R.id.button_submit);

        pDialog = new ProgressDialog(this);

        if (NetworkUtils.isConnected(ProfileActivity.this)) {
            getUserProfile();
        } else {
            showToast(getString(R.string.conne_msg1));
        }

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validator.validate();
            }
        });

        validator = new Validator(this);
        validator.setValidationListener(this);
    }

    @Override
    public void onValidationSucceeded() {
        if (NetworkUtils.isConnected(ProfileActivity.this)) {
            strPassword = edtPassword.getText().toString();
            if (strPassword.length() >= 1 && strPassword.length() <= 5) {
                edtPassword.setError("Invalid Password");
            } else {
                putEditProfile();
            }

        } else {
            showToast(getString(R.string.conne_msg1));
        }
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(this);
            if (view instanceof EditText) {
                ((EditText) view).setError(message);
            } else {
                showToast(message);
            }
        }
    }

    private void getUserProfile() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
        jsObj.addProperty("method_name", "user_profile");
        jsObj.addProperty("user_id", myApplication.getUserId());

        params.put("data", API.toBase64(jsObj.toString()));
        client.post(Constant.API_URL, params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                mProgressBar.setVisibility(View.VISIBLE);
                scrollView.setVisibility(View.GONE);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                mProgressBar.setVisibility(View.GONE);
                scrollView.setVisibility(View.VISIBLE);

                String result = new String(responseBody);
                try {
                    JSONObject mainJson = new JSONObject(result);
                    JSONArray jsonArray = mainJson.getJSONArray(Constant.ARRAY_NAME);
                    if (jsonArray.length() > 0) {
                        JSONObject objJson;
                        for (int i = 0; i < jsonArray.length(); i++) {
                            objJson = jsonArray.getJSONObject(i);
                            edtName.setText(objJson.getString(Constant.USER_NAME));
                            edtEmail.setText(objJson.getString(Constant.USER_EMAIL));
                            edtPhone.setText(objJson.getString(Constant.USER_PHONE));
                        }
                    } else {
                        mProgressBar.setVisibility(View.GONE);
                        scrollView.setVisibility(View.GONE);
                        lyt_not_found.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                mProgressBar.setVisibility(View.GONE);
                scrollView.setVisibility(View.GONE);
                lyt_not_found.setVisibility(View.VISIBLE);
            }
        });
    }


    public void putEditProfile() {
        strName = edtName.getText().toString();
        strEmail = edtEmail.getText().toString();
        strPassword = edtPassword.getText().toString();
        strMobi = edtPhone.getText().toString();

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
        jsObj.addProperty("method_name", "user_profile_update");
        jsObj.addProperty("user_id", myApplication.getUserId());
        jsObj.addProperty("name", strName);
        jsObj.addProperty("email", strEmail);
        if (strPassword.isEmpty()) {
            jsObj.addProperty("password", "");
        } else {
            jsObj.addProperty("password", strPassword);
        }
        jsObj.addProperty("phone", strMobi);
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
                    JSONObject objJson;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        objJson = jsonArray.getJSONObject(i);
                        strMessage = objJson.getString(Constant.MSG);
                        Constant.GET_SUCCESS_MSG = objJson.getInt(Constant.SUCCESS);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                setResult();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                dismissProgressDialog();
            }

        });
    }

    public void setResult() {

        if (Constant.GET_SUCCESS_MSG == 0) {
            showToast(strMessage);
        } else {
            myApplication.saveLogin(myApplication.getUserId(), strName, strEmail);
            showToast(strMessage);
            finish();
        }
    }


    public void showToast(String msg) {
        Toast.makeText(ProfileActivity.this, msg, Toast.LENGTH_SHORT).show();
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
}
