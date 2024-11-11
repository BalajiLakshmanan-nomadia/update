package com.synchroteam.listadapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.synchroteam.TypefaceLibrary.TextView;
import com.synchroteam.beans.ModeleRapport;
import com.synchroteam.beans.TypeIntervention;
import com.synchroteam.synchroteam.ClientJobReport;
import com.synchroteam.synchroteam.ClientJobType;

import com.synchroteam.synchroteam3.R;
import com.synchroteam.utils.AccentInsensitive;

import java.util.ArrayList;

public class ClientJobTypeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    ClientJobType clientJobType;
    ClientJobReport clientJobReport;
    ArrayList<TypeIntervention> clientJobTypeList;
    ArrayList<TypeIntervention> originalClientJobTypeList;

    //for Job Report
    ArrayList<ModeleRapport> clientJobReportList;
    ArrayList<ModeleRapport> originalClientReportTypeList;
    Boolean isClientJobReport = false;

    Filter filter;

    public ClientJobTypeAdapter(Activity activity, ArrayList<TypeIntervention> clientJobTypeList) {
        this.clientJobType = (ClientJobType) activity;
        this.clientJobTypeList = clientJobTypeList;
        originalClientJobTypeList = new ArrayList<TypeIntervention>();
        originalClientJobTypeList.addAll(clientJobTypeList);
        Log.e("CHECK","ClientJobTypeAdapter>>>>>>>>>called");
    }

    public ClientJobTypeAdapter(Activity activity, ArrayList<ModeleRapport> clientJobTypeReport, Boolean isClientJobReport){
        this.clientJobReport = (ClientJobReport) activity;
        this.clientJobReportList = clientJobTypeReport;
        originalClientReportTypeList = new ArrayList<ModeleRapport>();
        originalClientReportTypeList.addAll(clientJobReportList);
        this.isClientJobReport = isClientJobReport;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_layout_client_job_type,parent,false);
        return new ClientJobTypeViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

                    ClientJobTypeViewHolder jobTypeViewHolderholder = (ClientJobTypeViewHolder) holder;
        if(isClientJobReport){
//            ClientJobReportHolder clientJobReportHolder = (ClientJobReportHolder) holder;
            jobTypeViewHolderholder.clientJobTypeTextView.setText(clientJobReportList.get(position).getNom());
        }else {

            jobTypeViewHolderholder.clientJobTypeTextView.setText(clientJobTypeList.get(position).getNom());
        }
    }


    @Override
    public int getItemCount() {
        if(isClientJobReport){
            Log.e("COUNT","clientReportList>>>>>>>>>>>>>"+clientJobReportList.size());
            return clientJobReportList.size();
        }else {
            Log.e("COUNT","clientJobTypeList>>>>>>>>>>>>>"+clientJobTypeList.size());
            return clientJobTypeList.size();
        }
    }

    public class ClientJobTypeViewHolder extends RecyclerView.ViewHolder{

        TextView clientJobTypeTextView;

        public ClientJobTypeViewHolder(@NonNull View itemView) {
            super(itemView);
            clientJobTypeTextView = itemView.findViewById(R.id.dataClientJobType);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(isClientJobReport){
                        clientJobReport.passOnValues(clientJobReportList.get(getAdapterPosition()));
                    }else{
                        clientJobType.passOnValues(clientJobTypeList.get(getAdapterPosition()));
                    }
                }
            });
        }
    }


    public  Filter getFilter(){
        filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                FilterResults results = new FilterResults();
                ArrayList<TypeIntervention> arrayList = new ArrayList<>();

                if (charSequence != null && charSequence.toString().length() > 0) {
                    for (TypeIntervention typeIntervention : originalClientJobTypeList) {

                        TypeIntervention piece = typeIntervention;
                        String nom = piece.getNom();

                        // remove all the accented characters before search.
                        String asciiSearchString = AccentInsensitive
                                .removeAccentCase(nom);
                        String asciiConstraint = AccentInsensitive
                                .removeAccentCase(charSequence.toString());

                        if ((asciiSearchString.toLowerCase()
                                .contains(asciiConstraint))) {

                            arrayList.add(piece);

                        }

                    }

                    results.values = arrayList;
                    results.count = arrayList.size();

                } else {
                    synchronized (clientJobTypeList) {
                        results.values = originalClientJobTypeList;
                        results.count = originalClientJobTypeList.size();
                    }
                }
                return results;

            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {

                clientJobTypeList.clear();
                clientJobTypeList.addAll((ArrayList<TypeIntervention>) filterResults.values);

                notifyDataSetChanged();
            }
        };
        return filter;
    }

    public Filter getFilterForJobReport(){
        filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                FilterResults filterResults = new FilterResults();
                ArrayList<ModeleRapport> arrayList = new ArrayList<>();

                if (charSequence != null && charSequence.toString().length() > 0) {
                    for (ModeleRapport modeleRapport : originalClientReportTypeList) {

                        ModeleRapport piece = modeleRapport;
                        String nom = piece.getNom();

                        // remove all the accented characters before search.
                        String asciiSearchString = AccentInsensitive
                                .removeAccentCase(nom);
                        String asciiConstraint = AccentInsensitive
                                .removeAccentCase(charSequence.toString());

                        if ((asciiSearchString.toLowerCase()
                                .contains(asciiConstraint))) {

                            arrayList.add(piece);

                        }

                    }

                    filterResults.values = arrayList;
                    filterResults.count = arrayList.size();

                } else {
                    synchronized (clientJobReportList) {
                        filterResults.values = originalClientReportTypeList;
                        filterResults.count = originalClientReportTypeList.size();
                    }
                }
                return filterResults;


            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                clientJobReportList.clear();
                clientJobReportList.addAll((ArrayList<ModeleRapport>) filterResults.values);

                notifyDataSetChanged();
            }
        };
        return  filter;
    }
    }

