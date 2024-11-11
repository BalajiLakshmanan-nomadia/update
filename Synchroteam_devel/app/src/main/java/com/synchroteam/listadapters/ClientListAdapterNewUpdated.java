package com.synchroteam.listadapters;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.synchroteam.beans.Client;
import com.synchroteam.dao.Dao;
import com.synchroteam.listeners.RvEmptyListener;
import com.synchroteam.synchroteam.ClientListNew;
import com.synchroteam.synchroteam.NewJob;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.utils.KEYS;

import java.util.ArrayList;

public class ClientListAdapterNewUpdated extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    /** The activity. */

    /** The clients. */
    private ArrayList<Client> clients;
    /** The layout inflater. */
    private LayoutInflater layoutInflater;
    private ClientListNew clientList;
    private Activity activity;
    private Context context;

    public ClientListAdapterNewUpdated(Activity activity, ArrayList<Client> clients,
                                       Dao dataAccessObject) {
        // TODO Auto-generated constructor stub

        this.clients = clients;
//		clientList = (ClientListNew) activity;
        this.activity = activity;
        this.context = context;
        layoutInflater = (LayoutInflater) activity
                .getSystemService(Service.LAYOUT_INFLATER_SERVICE);
    }



    /**
     * The Class ViewHolder.
     */
    public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        /** The client name tv. */
        TextView clientNameTv;


        public ItemViewHolder(@NonNull View view) {
            super(view);
            clientNameTv = (TextView) view.findViewById(R.id.dataNameTv);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION){
                Client client = clients.get(position);

                Bundle bundle = new Bundle();
                bundle.putBoolean(KEYS.NewJob.ISNEARESTCLIENTSELECTED,false);
                bundle.putInt(KEYS.NewJob.CLIENT_ID, client.getIdClient());
                bundle.putString(KEYS.NewJob.GLOBAL_ADDRESS, client.getAdresseGlobalClient());
                bundle.putString(KEYS.NewJob.COMPLY_ADDRESS, client.getAdresseComplClient());
                bundle.putString(KEYS.NewJob.RUE, client.getRueClient());
                if(client.getRef_customer().length()>0){
                    bundle.putString(KEYS.NewJob.CLIENT_NAME, client.getNmClient()+" ("+client.getRef_customer()+")");
                }else {
                    bundle.putString(KEYS.NewJob.CLIENT_NAME, client.getNmClient());
                }
                bundle.putString(KEYS.NewJob.VILLE, client.getVilleClient());
                bundle.putString(KEYS.NewJob.GPSX, client.getGpsX());
                bundle.putString(KEYS.NewJob.GPSY, client.getGpsY());
                Log.e("TRIDENT","CHECK_BUNDLE>>>"+bundle);

                Intent clientListIntent = new Intent();
                clientListIntent.putExtras(bundle);
//                activity.startActivity(clientListIntent);
                activity.setResult(Activity.RESULT_OK,clientListIntent);
                activity.finish();
            }

            }

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = layoutInflater.inflate(R.layout.list_item_autocomplete,parent,false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Client client = clients.get(position);
       ItemViewHolder itemViewHolder = (ItemViewHolder)holder ;

        if (client.getRef_customer().length() > 0){
            itemViewHolder.clientNameTv.setText(client.getNmClient() + " (" + client.getRef_customer() + ")");
        }else {
            itemViewHolder.clientNameTv.setText(client.getNmClient());
        }
        Log.e("ClientListAdapterNew", "Binding client at position: " + position);

    }

    @Override
    public int getItemCount() {
        return clients.size();
    }
}
