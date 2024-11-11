package com.synchroteam.listadapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.synchroteam.beans.JobDialogTypeListModel;
import com.synchroteam.dialogs.JobListDialog;
import com.synchroteam.events.JobTypeDialogEvent;
import com.synchroteam.synchroteam.SyncoteamNavigationActivity;
import com.synchroteam.synchroteam3.R;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

/**
 * Created by Trident
 */

public class JobDialogListAdapter extends ArrayAdapter<String> {

    private final Context context;
    private ArrayList<JobDialogTypeListModel> jobTypeList;
    private ViewHolder viewHolder;
    private SyncoteamNavigationActivity activity;
    private JobListDialog jobListDialog;


    public JobDialogListAdapter(Context context,
                                ArrayList<JobDialogTypeListModel> jobTypeList,
                                JobListDialog jobListDialog) {
        super(context, R.layout.row_unavailablity_dialog_listview);
        this.context = context;
        this.jobListDialog = jobListDialog;
        this.jobTypeList = jobTypeList;
        activity = (SyncoteamNavigationActivity) context;
    }

    @Override
    public int getCount() {
        return jobTypeList.size();
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

        viewHolder.txtIssueName.setText(jobTypeList.get(position).getJobType());

        viewHolder.txtIssueName.setTag(position);
        viewHolder.txtIssueName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = (int) v.getTag();

                if (jobListDialog != null) {

                    EventBus.getDefault().post(new JobTypeDialogEvent(jobTypeList.get(pos)
                                                                              .getJobTypeName
                                                                                      (),
                                                                      jobTypeList.get(pos).isHasMultipleJobs()));

                    jobListDialog.dismiss();
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