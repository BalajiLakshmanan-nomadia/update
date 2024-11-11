package com.synchroteam.listadapters;

import android.app.Activity;
import android.app.Service;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.synchroteam.beans.Client_Site_EquipmnentBean;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.utils.AccentInsensitive;

import java.util.ArrayList;
import java.util.List;

// TODO: Auto-generated Javadoc

/**
 * The Class EquipmentSectionListAdapter.
 */
public class EquipmentSectionListAdapterNew extends BaseAdapter {

    /** The activity. */

    /**
     * The clients.
     */
    private ArrayList<Client_Site_EquipmnentBean> clients;

    /**
     * The layout inflater.
     */
    private LayoutInflater layoutInflater;

    private Activity activity;

    /** The is user searching. */

    /**
     * Instantiates a new client list adapter.
     *
     * @param activity the activity
     * @param clients  the clients
     */
    public EquipmentSectionListAdapterNew(Activity activity,
                                          ArrayList<Client_Site_EquipmnentBean> clients) {
        // TODO Auto-generated constructor stub

        this.clients = clients;
        this.activity = activity;
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

        TextView siteNameTv;

        TextView equipmentNameTv;

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
        Client_Site_EquipmnentBean client_Site_Bean = (Client_Site_EquipmnentBean) getItem(position);

        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = layoutInflater.inflate(
                    R.layout.layout_section_equipment_list_item, parent,false);

            viewHolder.clientNameTv = (TextView) convertView
                    .findViewById(R.id.clientNameTv);

            viewHolder.clientPlaceTv = (TextView) convertView
                    .findViewById(R.id.clientPlaceTv);
            viewHolder.siteNameTv = (TextView) convertView
                    .findViewById(R.id.siteNameTv);
            viewHolder.equipmentNameTv = (TextView) convertView
                    .findViewById(R.id.equipmentNameTv);
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.clientNameTv.setText(client_Site_Bean.getNmClient());
        viewHolder.clientPlaceTv.setVisibility(View.VISIBLE);
        if (!TextUtils.isEmpty(client_Site_Bean.getClientCity())) {

            viewHolder.clientPlaceTv.setText(client_Site_Bean.getClientCity());
        } else {

//			viewHolder.clientPlaceTv.setText(activity
//					.getString(R.string.textNotAvalableLable));
            viewHolder.clientPlaceTv.setVisibility(View.GONE);
        }

        viewHolder.siteNameTv.setVisibility(View.VISIBLE);
        if (!TextUtils.isEmpty(client_Site_Bean.getNmSite())) {

            viewHolder.siteNameTv.setText(client_Site_Bean.getNmSite());
        } else {

//			viewHolder.siteNameTv.setText(activity
//					.getString(R.string.textNotAvalableLable));
            viewHolder.siteNameTv.setVisibility(View.GONE);
        }
        if (client_Site_Bean.getRefCustomer().length()>0){
            viewHolder.equipmentNameTv.setText(client_Site_Bean.getNmEquipement()+" ("+client_Site_Bean.getRefCustomer()+")");
        }else {
            viewHolder.equipmentNameTv.setText(client_Site_Bean.getNmEquipement());
        }
        return convertView;
    }

}
