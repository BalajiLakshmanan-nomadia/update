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
import com.synchroteam.synchroteam.UpdateUnavailability;
import com.synchroteam.synchroteam3.R;

import java.util.ArrayList;

public class IssuesListAdapterUpdateUnavailabilityRc extends RecyclerView.Adapter<IssuesListAdapterUpdateUnavailabilityRc.ViewHolder> {
    private final Context context;
    private ArrayList<UnavailabilityBeans> unavailabilityList;
    private boolean isNotFirstTime;
    private String selectedPos;

    private int colorCodePos;
    private String idUser;

    public IssuesListAdapterUpdateUnavailabilityRc(Context context,
                                                   ArrayList<UnavailabilityBeans> unavailabilityList,
                                                   String selectedType, String idUser) {
        this.context = context;
        this.unavailabilityList = unavailabilityList;
        this.idUser = idUser;
        for (int i = 0; i < unavailabilityList.size(); i++) {
            if (selectedType.equalsIgnoreCase(unavailabilityList.get(i)
                    .getUnavailabilityName())) {
                colorCodePos = i;
                ((UpdateUnavailability) context).setIssuePosition(colorCodePos);
            }
        }
    }

    @NonNull
    @Override
    public IssuesListAdapterUpdateUnavailabilityRc.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.rowview_unavailability_issue, viewGroup, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final IssuesListAdapterUpdateUnavailabilityRc.ViewHolder viewHolder, int position) {
        if (colorCodePos == position) {
            viewHolder.imgSelectTick.setVisibility(View.VISIBLE);
        } else {
            viewHolder.imgSelectTick.setVisibility(View.INVISIBLE);
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

        if (isNotFirstTime && selectedPos != null) {
            int selPos = Integer.parseInt(selectedPos);
            if (selPos == position)
                viewHolder.imgSelectTick.setVisibility(View.VISIBLE);
            else
                viewHolder.imgSelectTick.setVisibility(View.INVISIBLE);
        }

        viewHolder.viewIssue.setTag(position);
        viewHolder.viewIssue.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int pos = (Integer) v.getTag();
                viewHolder.imgSelectTick.setVisibility(View.VISIBLE);
                selectedPos = String.valueOf(pos);
                isNotFirstTime = true;
                notifyDataSetChanged();
                ((UpdateUnavailability) context).setIssuePosition(pos);

            }
        });

        if (idUser.equalsIgnoreCase("0")) {
            viewHolder.viewIssue.setEnabled(false);
        } else {
            viewHolder.viewIssue.setEnabled(true);
        }


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
