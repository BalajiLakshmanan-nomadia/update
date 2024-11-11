package com.synchroteam.listadapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.synchroteam.TypefaceLibrary.TextView;
import com.synchroteam.beans.UnavailabilityBeans;
import com.synchroteam.synchroteam.CreateUnavailability;
import com.synchroteam.synchroteam3.R;

import java.util.ArrayList;

public class IssuesListAdapterStartActivityRc extends RecyclerView.Adapter<IssuesListAdapterStartActivityRc.ViewHolder> {
    private final Context context;
    private ArrayList<UnavailabilityBeans> unavailabilityList;

    private String selectedPos;

    public IssuesListAdapterStartActivityRc(Context context,
                                            ArrayList<UnavailabilityBeans> unavailabilityList) {

        this.context = context;
        this.unavailabilityList = unavailabilityList;
    }

    @NonNull
    @Override
    public IssuesListAdapterStartActivityRc.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.rowview_unavailability_issue, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final IssuesListAdapterStartActivityRc.ViewHolder viewHolder, int position) {
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
        viewHolder.viewIssue.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int pos = (Integer) v.getTag();
                viewHolder.imgSelectTick.setVisibility(View.VISIBLE);
                selectedPos = String.valueOf(pos);
                notifyDataSetChanged();

                ((CreateUnavailability) context).setIssuePosition(pos);
            }
        });
    }

    @Override
    public int getItemCount() {
        return unavailabilityList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        View viewIssue;
        TextView txtIssueName;
        ImageView imgSelectTick;

        public ViewHolder(@NonNull View rowView) {
            super(rowView);
            viewIssue = (View) rowView.findViewById(R.id.viewIssueCircle);
            txtIssueName = (TextView) rowView.findViewById(R.id.txtIssueName);
            imgSelectTick = (ImageView) rowView
                    .findViewById(R.id.imgSelectTick);
        }
    }
}
