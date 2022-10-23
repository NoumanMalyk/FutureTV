package com.futurevalley.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.futurevalley.futurestudio.MyApplication;
import com.futurevalley.futurestudio.R;
import com.futurevalley.util.API;
import com.futurevalley.util.Constant;
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

public class ReportFragment extends BottomSheetDialogFragment {

    public ReportFragment() {

    }

    EditText edtReport;
    Button btnSubmit;
    String postId, postType;
    MyApplication myApplication;
    ProgressDialog pDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.layout_report, container, false);
        if (getArguments() != null) {
            postId = getArguments().getString("postId");
            postType = getArguments().getString("postType");
        }

        myApplication = MyApplication.getInstance();
        edtReport = rootView.findViewById(R.id.et_report);
        btnSubmit = rootView.findViewById(R.id.button_report_submit);
        pDialog = new ProgressDialog(getActivity());

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comment = edtReport.getText().toString();
                if (!comment.isEmpty()) {
                    if (NetworkUtils.isConnected(requireActivity())) {
                        sentReport(comment);
                    } else {
                        showToast(getString(R.string.conne_msg1));
                    }
                }
            }
        });

        return rootView;
    }

    private void sentReport(String report) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
        jsObj.addProperty("method_name", "user_report");
        jsObj.addProperty("post_id", postId);
        jsObj.addProperty("user_id", myApplication.getUserId());
        jsObj.addProperty("report", report);
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
                        String strMessage = objComment.getString(Constant.MSG);
                        showToast(strMessage);
                        edtReport.getText().clear();
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
        Toast.makeText(requireActivity(), msg, Toast.LENGTH_SHORT).show();
    }
}
