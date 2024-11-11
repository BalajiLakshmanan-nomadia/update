package com.synchroteam.utils;

import android.content.Context;
import android.os.Build;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.content.ContextCompat;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.synchroteam.synchroteam3.R;

/**
 * Created by Trident on 7/26/2016.
 */
public class MaterialDesignUtils {
    private static Context mContext;

    public static MaterialDesignUtils getInstance(Context context) {
        mContext = context;
        return new MaterialDesignUtils();
    }

    /**
     * Sets ripple effect for a view. (Only if it is lollipop or higher devices)
     *
     * @param view view to add the effect
     */
    public void setRippleEffect(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            TypedValue outValue = new TypedValue();
            mContext.getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
            view.setBackgroundResource(outValue.resourceId);
        }
    }

    /**
     * Shows snack bar with a message. Text color will be white.
     *
     * @param view   : view to be snackbar shown
     * @param txtMsg : message to be shown
     */
    public void showSnackBar(View view, String txtMsg) {
        Snackbar sbDone = Snackbar.make(view, txtMsg, Snackbar.LENGTH_SHORT);
        View viewSB = sbDone.getView();
        TextView tvMsg = (TextView) viewSB.findViewById(com.google.android.material.R.id.snackbar_text);
        tvMsg.setTextColor(ContextCompat.getColor(mContext, R.color.white));
        tvMsg.setTextSize(TypedValue.COMPLEX_UNIT_PX, mContext.getResources().getDimension(R.dimen.textSizeLastSyncronizationLableTv));
        sbDone.show();
    }
}
