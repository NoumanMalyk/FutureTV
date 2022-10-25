package com.futurevalley.futurestudio;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatTextView;

import com.futurevalley.util.API;
import com.futurevalley.util.Constant;
import com.futurevalley.util.IsRTL;
import com.futurevalley.util.NetworkUtils;
import com.futurevalley.util.SharedPrefManager;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Password;
import com.tuyenmonkey.textdecorator.TextDecorator;
import com.tuyenmonkey.textdecorator.callback.OnTextClickListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cn.refactor.library.SmoothCheckBox;
import cz.msebera.android.httpclient.Header;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class SignInActivity extends AppCompatActivity implements Validator.ValidationListener {
    @NotEmpty
    @Email
    EditText edtEmail;
    @NotEmpty
    @Password
    EditText edtPassword;
    String strEmail, strPassword, strMessage, strName, strUserId;
    Button btnLogin, btnSkip;
    TextView btnForgotPass, btnRegister;
    private Validator validator;
    MyApplication MyApp;
    ProgressDialog pDialog;
    SmoothCheckBox checkBox;
    boolean isFromOtherScreen = false;
    AppCompatTextView tvSignInAccept;
    AppCompatCheckBox checkBoxAgree;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        IsRTL.ifSupported(this);

        Intent intent = getIntent();
        if (intent.hasExtra("isOtherScreen")) {
            isFromOtherScreen = true;
        }

        pDialog = new ProgressDialog(this);
        MyApp = MyApplication.getInstance();
        edtEmail = findViewById(R.id.editText_email_login_activity);
        edtPassword = findViewById(R.id.editText_password_login_activity);
        btnLogin = findViewById(R.id.button_login_activity);
        btnSkip = findViewById(R.id.button_skip_login_activity);
        btnForgotPass = findViewById(R.id.textView_forget_password_login);
        btnRegister = findViewById(R.id.textView_signup_login);
        checkBox = findViewById(R.id.checkbox_login_activity);
        tvSignInAccept = findViewById(R.id.textSignUpAccept);
        checkBoxAgree = findViewById(R.id.checkbox);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPrefManager.getInstance(SignInActivity.this).setLoginResult("1");
                Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

        btnForgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignInActivity.this, ForgotPasswordActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validator.validate();
            }
        });


        if (MyApp.getIsRemember()) {
            checkBox.setChecked(true);
            edtEmail.setText(MyApp.getRememberEmail());
            edtPassword.setText(MyApp.getRememberPassword());
        }
        validator = new Validator(this);
        validator.setValidationListener(this);

        setAcceptText();
    }

    @Override
    public void onValidationSucceeded() {
        if (NetworkUtils.isConnected(SignInActivity.this)) {
            if (checkBoxAgree.isChecked()) {
                putSignIn();
            } else {
                showToast(getString(R.string.please_accept));
            }
        } else {
            showToast(getString(R.string.conne_msg1));
        }
    }

    public void putSignIn() {
        strEmail = edtEmail.getText().toString();
        strPassword = edtPassword.getText().toString();

        if (checkBox.isChecked()) {
            MyApp.saveIsRemember(true);
            MyApp.saveRemember(strEmail, strPassword);
        } else {
            MyApp.saveIsRemember(false);
        }

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
        jsObj.addProperty("method_name", "user_login");
        jsObj.addProperty("email", strEmail);
        jsObj.addProperty("password", strPassword);
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
                        if (objJson.has(Constant.MSG)) {
                            strMessage = objJson.getString(Constant.MSG);
                            Constant.GET_SUCCESS_MSG = objJson.getInt(Constant.SUCCESS);
                        } else {
                            Constant.GET_SUCCESS_MSG = objJson.getInt(Constant.SUCCESS);
                            strName = objJson.getString(Constant.USER_NAME);
                            strUserId = objJson.getString(Constant.USER_ID);
                        }
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


    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(this);
            if (view instanceof EditText) {
                ((EditText) view).setError(message);
            } else {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        }
    }

    public void setResult() {

        if (Constant.GET_SUCCESS_MSG == 0) {
            showToast("Opps. \n" + strMessage);

        } else {
            SharedPrefManager.getInstance(SignInActivity.this).setLoginResult("1");
            MyApp.saveIsLogin(true);
            MyApp.saveLogin(strUserId, strName, strEmail);
            if (isFromOtherScreen) {
                onBackPressed();
            } else {
                Intent i = new Intent(SignInActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }

        }
    }

    public void showToast(String msg) {
        Toast.makeText(SignInActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

    public void showProgressDialog() {
        pDialog.setMessage(getString(R.string.loading));
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();
    }

    public void dismissProgressDialog() {
        pDialog.dismiss();
    }

    private void setAcceptText() {
        TextDecorator
                .decorate(tvSignInAccept, getString(R.string.sign_up_accept, getString(R.string.privacy_policy)))
                .setTextColor(R.color.colorAccent, getString(R.string.privacy_policy))
                .makeTextClickable(new OnTextClickListener() {
                    @Override
                    public void onClick(View view, String text) {
                        Intent intent = new Intent(SignInActivity.this, AcceptActivity.class);
                        startActivity(intent);
                    }
                }, true, getString(R.string.privacy_policy))
                .build();
    }
}
