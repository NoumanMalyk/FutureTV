package com.futurevalley.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;

public class DialogUtil {

    @SuppressLint("StaticFieldLeak")
    private static RateDialog rateDialog;

    public static void showRateDialog(Activity activity,
                                      RateDialog.RateDialogListener rateDialogListener,
                                      String postId, String postType) {
        if (activity != null) {
            rateDialog = new RateDialog(activity, postId, postType);
            rateDialog.setRateDialogListener(rateDialogListener);
            rateDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    DialogUtil.rateDialog = null;
                }
            });
            rateDialog.show();
            rateDialog.setCancelable(false);
        }

    }
}
