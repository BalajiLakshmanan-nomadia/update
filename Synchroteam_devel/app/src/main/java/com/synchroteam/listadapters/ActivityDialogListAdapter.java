package com.synchroteam.listadapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.synchroteam.beans.UnavailabilityBeans;
import com.synchroteam.dialogs.ActivityModeDialog;
import com.synchroteam.dialogs.DrivingOrActivityListDialog;
import com.synchroteam.dialogs.StartActivityDialog;
import com.synchroteam.synchroteam.SyncoteamNavigationActivity;
import com.synchroteam.synchroteam3.R;

import java.util.ArrayList;

/**
 * Created by Trident
 */

public class ActivityDialogListAdapter extends ArrayAdapter<String> {

    private final Context context;
    private ArrayList<UnavailabilityBeans> unavailabilityList;
    private ViewHolder viewHolder;
    private SyncoteamNavigationActivity activity;
    private DrivingOrActivityListDialog activityListDialog;
    private SelectActivityListener listener;

    public ActivityDialogListAdapter(Context context,
                                     ArrayList<UnavailabilityBeans> unavailabilityList,
                                     DrivingOrActivityListDialog activityListDialog,
                                     SelectActivityListener listener) {
        super(context, R.layout.row_unavailablity_dialog_listview);
        this.context = context;
        this.activityListDialog = activityListDialog;
        this.listener = listener;
        this.unavailabilityList = unavailabilityList;
        activity = (SyncoteamNavigationActivity) context;
    }

    @Override
    public int getCount() {
        return unavailabilityList.size();
    }

    @SuppressLint("NewApi")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater mInflater = ((Activity) context).getLayoutInflater();
            convertView = mInflater.inflate(
                    R.layout.row_unavailablity_dialog_listview, parent, false);
            viewHolder = new ViewHolder(convertView);

            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.txtIssueName.setText(unavailabilityList.get(position)
                .getUnavailabilityName());

        viewHolder.txtIssueName.setTag(position);
        viewHolder.txtIssueName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = (int) v.getTag();

                if (activityListDialog != null) {
                    ActivityModeDialog.newInstance(unavailabilityList.get(pos).getUnavailabilityName(), unavailabilityList.get(pos).getUnavailabilityID(), null, false, null).show(activity.getSupportFragmentManager(), "");
                    activityListDialog.dismiss();
                }
                if (listener != null){
                    listener.setIssuePosition(pos);
                }
            }
        });

        convertView.setOnClickListener(null);
        return convertView;
    }

    // ...........................VIEWHOLDER...CLASS...STARTS...HERE...........................................
    static class ViewHolder {
        android.widget.TextView txtIssueName;

        public ViewHolder(View rowView) {
            txtIssueName = (android.widget.TextView) rowView.findViewById(R.id.txtIssueName);
        }
    }

    // ****************************VIEWHOLDER...CLASS...ENDS...HERE*********************************************

    public interface SelectActivityListener {
        void setIssuePosition(int position);
    }
}