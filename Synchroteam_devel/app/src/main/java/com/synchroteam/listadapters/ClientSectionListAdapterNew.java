package com.synchroteam.listadapters;

import android.app.Activity;
import android.app.Service;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.synchroteam.beans.Client;
import com.synchroteam.synchroteam3.R;

import java.util.ArrayList;

// TODO: Auto-generated Javadoc

/**
 * The Class ClientListAdapter.
 */
public class ClientSectionListAdapterNew extends BaseAdapter {

    /** The activity. */

    /**
     * The clients.
     */
    private ArrayList<Client> clients;

    /**
     * The layout inflater.
     */
    private LayoutInflater layoutInflater;

    /**
     * Instantiates a new client list adapter.
     *
     * @param activity         the activity
     * @param clients          the clients
     */
    public ClientSectionListAdapterNew(Activity activity,
                                       ArrayList<Client> clients) {
        // TODO Auto-generated constructor stub

        this.clients = clients;
        layoutInflater = (LayoutInflater) activity
                .getSystemService(Service.LAYOUT_INFLATER_SERVICE);
    }

    /**
     * The Class ViewHolder.
     */
    private class ViewHolder {

        /**
         * The client name tv.
         */
        TextView clientNameTv;

        TextView clientPlaceTv;

    }

    /*
     * (non-Javadoc)
     *
     * @see android.widget.Adapter#getCount()
     */
    @Override
    public int getCount() {
        return clients.size();
    }

    /*
     * (non-Javadoc)
     *
     * @see android.widget.Adapter#getItem(int)
     */
    @Override
    public Object getItem(int position) {
        return clients.get(position);
    }

    /*
     * (non-Javadoc)
     *
     * @see android.widget.Adapter#getItemId(int)
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /*
     * (non-Javadoc)
     *
     * @see android.widget.Adapter#getView(int, android.view.View,
     * android.view.ViewGroup)
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        ViewHolder viewHolder;
        Client client = (Client) getItem(position);

        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = layoutInflater.inflate(
                    R.layout.layout_section_client_list, parent,false);

            viewHolder.clientNameTv = (TextView) convertView
                    .findViewById(R.id.clientNameTv);
            viewHolder.clientPlaceTv = (TextView) convertView
                    .findViewById(R.id.clientPlaceTv);

            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (client.getRef_customer().length()>0){
            viewHolder.clientNameTv.setText(client.getNmClient()+" ("+client.getRef_customer()+")");
        }else {
            viewHolder.clientNameTv.setText(client.getNmClient());
        }

        viewHolder.clientPlaceTv.setVisibility(View.VISIBLE);
        if (!TextUtils.isEmpty(client.getVilleClient())) {

            viewHolder.clientPlaceTv.setText(client.getVilleClient());
        } else {
//			viewHolder.clientPlaceTv.setText(notAvaliable);
            viewHolder.clientPlaceTv.setVisibility(View.GONE);
        }

        return convertView;
    }

}
