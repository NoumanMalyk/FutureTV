package com.futurevalley.util;

import android.app.Activity;
import android.os.Build;
import android.view.View;

import com.bosphere.fadingedgelayout.FadingEdgeLayout;
import com.futurevalley.futurestudio.R;

public class IsRTL {
    public static void ifSupported(Activity mContext) {
        boolean isRTL = Boolean.parseBoolean(mContext.getString(R.string.isRTL));
        if (isRTL) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                mContext.getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }
        }
    }

    public static void changeShadowInRtl(Activity mContext, FadingEdgeLayout fadingEdgeLayout) {
        boolean isRTL = Boolean.parseBoolean(mContext.getString(R.string.isRTL));
        if (isRTL) {
            fadingEdgeLayout.setFadeEdges(false, true, false, false);
            fadingEdgeLayout.setFadeSizes(0, mContext.getResources().getDimensionPixelSize(R.dimen.shadow_size), 0, 0);
        }
    }
}
