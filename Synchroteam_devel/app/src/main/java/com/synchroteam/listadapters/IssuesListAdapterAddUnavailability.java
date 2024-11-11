package com.synchroteam.listadapters;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.synchroteam.TypefaceLibrary.TextView;
import com.synchroteam.beans.UnavailabilityBeans;
import com.synchroteam.synchroteam.AddUnavailability;
import com.synchroteam.synchroteam3.R;

/**
 * @author Trident
 *         <p>
 *         Adapter for issues list in UnavailabilityScheduleFragment and
 *         UnavailabilitStartFragment Class
 */
public class IssuesListAdapterAddUnavailability extends ArrayAdapter<String> {
    private final Context context;
    private ArrayList<UnavailabilityBeans> unavailabilityList;
    private String selectedPos;
    private ViewHolder viewHolder;
    // private static final String TAG = "IssuesListAdapter";

    private boolean isStartFragment;

    public IssuesListAdapterAddUnavailability(Context context,
                                              ArrayList<UnavailabilityBeans> unavailabilityList,
                                              boolean isStartFragment) {
        super(context, R.layout.rowview_unavailability_issue);
        this.context = context;
        this.unavailabilityList = unavailabilityList;
        this.isStartFragment = isStartFragment;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return unavailabilityList.size();
    }

    @SuppressLint("NewApi")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater mInflater = ((Activity) context).getLayoutInflater();
            convertView = mInflater.inflate(
                    R.layout.rowview_unavailability_issue, parent, false);
            viewHolder = new ViewHolder(convertView);

            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.txtIssueName.setText(unavailabilityList.get(position)
                .getUnavailabilityName());

        GradientDrawable gd = (GradientDrawable) viewHolder.viewIssue
                .getBackground();
        gd.setColor(Color
                .parseColor("#"
                        + unavailabilityList.get(position)
                        .getUnavailabilityColorCode()));
        gd.invalidateSelf();

        if (selectedPos != null) {
            int selPos = Integer.parseInt(selectedPos);
            if (selPos == position)
                viewHolder.imgSelectTick.setVisibility(View.VISIBLE);
            else
                viewHolder.imgSelectTick.setVisibility(View.INVISIBLE);
        } else {
            viewHolder.imgSelectTick.setVisibility(View.INVISIBLE);
        }

        viewHolder.viewIssue.setTag(position);
        viewHolder.viewIssue.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                int pos = (Integer) v.getTag();
                viewHolder.imgSelectTick.setVisibility(View.VISIBLE);
                selectedPos = String.valueOf(pos);
                notifyDataSetChanged();

                if (!isStartFragment) {
                    ((AddUnavailability) context).setScheduleFragIssuePos(pos);
                } else {
                    ((AddUnavailability) context).setStartFragIssuePos(pos);
                }

            }
        });

        convertView.setOnClickListener(null);
        return convertView;
    }

    // ...........................VIEWHOLDER...CLASS...STARTS...HERE...........................................
    static class ViewHolder {
        View viewIssue;
        TextView txtIssueName;
        ImageView imgSelectTick;

        // String selectedIssueCode;

        public ViewHolder(View rowView) {
            viewIssue = (View) rowView.findViewById(R.id.viewIssueCircle);
            txtIssueName = (TextView) rowView.findViewById(R.id.txtIssueName);
            imgSelectTick = (ImageView) rowView
                    .findViewById(R.id.imgSelectTick);
        }

    }

    // ****************************VIEWHOLDER...CLASS...ENDS...HERE*********************************************
}
