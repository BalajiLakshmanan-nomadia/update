package com.synchroteam.listadapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.synchroteam.beans.TravelActivity;
import com.synchroteam.dialogs.DrivingModeDialogUpdated;
import com.synchroteam.dialogs.DrivingOrActivityListDialog;
import com.synchroteam.synchroteam.SyncoteamNavigationActivity;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.utils.LocationUtils;

import java.util.ArrayList;

/**
 * Created by Trident
 */

public class DrivingDialogListAdapter extends ArrayAdapter<String> {

    private final Context context;
    private ArrayList<TravelActivity> unavailabilityList;
    private ViewHolder viewHolder;
    private SyncoteamNavigationActivity activity;
    private DrivingOrActivityListDialog activityListDialog;

    public DrivingDialogListAdapter(Context context,
                                    ArrayList<TravelActivity> unavailabilityList,
                                    DrivingOrActivityListDialog activityListDialog) {
        super(context, R.layout.row_unavailablity_dialog_listview);
        this.context = context;
        this.activityListDialog = activityListDialog;
        this.unavailabilityList = unavailabilityList;
        activity = (SyncoteamNavigationActivity) context;
    }

    @Override
    public int getCount() {
        return unavailabilityList.size();
    }

    @SuppressLint("NewApi")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

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
                .getTravelName());

        viewHolder.txtIssueName.setTag(position);
        viewHolder.txtIssueName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (LocationUtils.checkLocationServices((Activity) context)) {
                    int pos = (int) v.getTag();
                    DrivingModeDialogUpdated.newInstance(null, unavailabilityList.get(pos).getIdType(), unavailabilityList.get(pos).getTravelName()).show(activity.getSupportFragmentManager(), "");

//                    todo remove later Travel
//                    TravelModeDialog.newInstance(null, unavailabilityList.get(pos).getIdType(), unavailabilityList.get(pos).getTravelName()).show(activity.getSupportFragmentManager(), "");

                    activityListDialog.dismiss();
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
}