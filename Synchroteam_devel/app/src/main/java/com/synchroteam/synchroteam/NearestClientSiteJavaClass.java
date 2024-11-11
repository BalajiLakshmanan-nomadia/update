package com.synchroteam.synchroteam;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.synchroteam.TypefaceLibrary.TextView;
import com.synchroteam.dao.Dao;
import com.synchroteam.dialogs.NearestClientNotAvailable;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.utils.AsyncTaskCoroutine;
import com.synchroteam.utils.DaoManager;
import com.synchroteam.utils.DialogUtils;
import com.synchroteam.utils.KEYS;
import com.synchroteam.utils.Logger;

import java.util.ArrayList;
import java.util.HashMap;

public class NearestClientSiteJavaClass {

    /**
     * The liste view.
     */
    private ListView listeView;

    /**
     * The dao.
     */
    private Dao dao;

    /** The search view et. */
    // private EditText searchViewEt;

    /** The cancel tv. */
    // private TextView cancelTv;

    /**
     * The adapter.
     */
    private CustomBaseAdapter adapter;

    /**
     * The nearest clint list.
     */
    private Activity activity;

    /**
     * The nearest clint list.
     */
    private ArrayList<HashMap<String, String>> nearestClintList;

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    public void onCreateView(Activity act, ListView view, String latitude,
                             String longitude) {

        // activity.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dao = DaoManager.getInstance();

        listeView = view;
        // view.findViewById(R.id.belowView).setVisibility(View.GONE);

        // searchViewEt = (EditText) view.findViewById(R.id.searchViewEt);
        // searchViewEt.addTextChangedListener(textWatcher);

        // cancelTv = (TextView) view.findViewById(R.id.cancelTv);

        this.activity = act;

        new FetchNearestClientAsyncTask().execute(latitude, longitude);

        listeView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                Intent intent = new Intent(activity, ClientDetail.class);

                String clientName = nearestClintList.get(position).get(
                        "nmClient");
                int clientId = Integer.parseInt(nearestClintList.get(position)
                                                        .get("IdClient"));

                intent.putExtra(KEYS.ClientDetial.ID_CLIENT, clientId);
                intent.putExtra(KEYS.ClientDetial.CLIENT_NAME, clientName);

                activity.startActivity(intent);

            }
        });

    }

    /**
     * The text watcher.
     */
    TextWatcher textWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
            // TODO Auto-generated method stub

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
            // TODO Auto-generated method stub

        }

        @Override
        public void afterTextChanged(Editable s) {
            // TODO Auto-generated method stub

            adapter.filterData(s.toString());
        }
    };

    /**
     * The Class FetchNearestClientAsyncTask.
     */
    private class FetchNearestClientAsyncTask extends
            AsyncTaskCoroutine<String, Boolean> {

        /**
         * Instantiates a new fetch nearest client async task.
         */
        public FetchNearestClientAsyncTask() {
            // TODO Auto-generated constructor stub
            nearestClintList = new ArrayList<HashMap<String, String>>();
        }

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#onPreExecute()
         */
        @Override
        public void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            DialogUtils.showProgressDialog(activity, "Wait", "Fatching Data",
                                           false);
        }

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#doInBackground(Params[])
         */
        @Override
        public Boolean doInBackground(String... params) {
            // TODO Auto-generated method stub

            try {
                nearestClintList.clear();
                nearestClintList.addAll(dao.getAllSt(params[0], params[1]));
                if (nearestClintList.size() == 0) {

                    return false;
                } else {
                    return true;
                }
            } catch (Exception e) {
                Logger.printException(e);
                return false;
            }

        }

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        public void onPostExecute(Boolean result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            try {
                if (result.booleanValue()) {

                    if (adapter == null) {
                        adapter = new CustomBaseAdapter(activity,
                                                        nearestClintList);
                    }

                    listeView.setAdapter(adapter);

                    adapter.notifyDataSetChanged();

                } else {
                    NearestClientNotAvailable nearestClientNotAvailable = new NearestClientNotAvailable(
                            activity,
                            new NearestClientNotAvailable.NearestClientInterface() {

                                @Override
                                public void doOnOkClick() {
                                    // TODO Auto-generated method stub

                                    activity.finish();
                                }
                            });

                    nearestClientNotAvailable.show();

                }
            } finally {
                DialogUtils.dismissProgressDialog();

            }

        }

    }

    /**
     * The Class CustomBaseAdapter to inflate list of nearest client.
     */
    public class CustomBaseAdapter extends BaseAdapter {

        /**
         * The context.
         */
        Context context;
        // List<RowItem> rowItems;
        /**
         * The list item.
         */
        ArrayList<HashMap<String, String>> listItem;// = new
        // ArrayList<HashMap<String,
        // String>>();
        /**
         * The orignal list.
         */
        ArrayList<HashMap<String, String>> orignalList;

        /**
         * Instantiates a new custom base adapter.
         *
         * @param context  the context
         * @param listItem the list item
         */
        public CustomBaseAdapter(Context context,
                                 ArrayList<HashMap<String, String>> listItem) {
            this.context = context;
            this.listItem = listItem;
            orignalList = new ArrayList<HashMap<String, String>>();
            this.orignalList.addAll(listItem);
        }

		/* private view holder class */

        /**
         * The Class ViewHolder.
         */
        private class ViewHolder {

            /**
             * The txt title.
             */
            TextView txtTitle;

            /**
             * The txt desc.
             */
            TextView txtDesc;

            /**
             * Distance lable
             */
            TextView txtDistanceLable;

            /**
             * The txt distance.
             */
            TextView txtDistance;
        }

        /*
         * (non-Javadoc)
         *
         * @see android.widget.Adapter#getView(int, android.view.View,
         * android.view.ViewGroup)
         */
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;

            LayoutInflater mInflater = (LayoutInflater) context
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            if (convertView == null) {
                convertView = mInflater.inflate(
                        R.layout.layout_clientlist_listitem, null);
                holder = new ViewHolder();
                holder.txtTitle = (TextView) convertView
                        .findViewById(R.id.clientNameTv);
                holder.txtDesc = (TextView) convertView
                        .findViewById(R.id.clientAddressTv);
                holder.txtDistance = (TextView) convertView
                        .findViewById(R.id.clientDistanceTv);
                holder.txtDistanceLable = (TextView) convertView.findViewById(R.id.distanceTv);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            HashMap<String, String> map = extracted(position);

            // holder.txtTitle.setText(map.get("nom")+"--"+map.get("nomClient"));

            String type = map.get("ObjectType");
            String nom = map.get("nom");
            String refCustomer = map.get("RefCustomer");
            if (type.equals("Site")) {
                // nom = map.get("nom") + " - (" + map.get("nmClient") + ")";
                nom = map.get("nmClient") + ":" + map.get("nom");
            }
            if (refCustomer.length()>0){
                holder.txtTitle.setText(nom+" ("+refCustomer+")");
            }else {
                holder.txtTitle.setText(nom);
            }

            holder.txtDesc.setText(map.get("AdrGlobale"));

            double newKB = Math
                    .round(Double.parseDouble(map.get("distanceKm")) * 100.0) / 100.0;
            holder.txtDistanceLable.setText(context.getString(R.string.textDistanceNearestClient) + ":");
            holder.txtDistance.setText(newKB + " Km");

            return convertView;
        }

        /**
         * Extracted.
         *
         * @param position the position
         * @return the hash map
         */
        @SuppressWarnings("unchecked")
        private HashMap<String, String> extracted(int position) {
            return (HashMap<String, String>) getItem(position);
        }

        /*
         * (non-Javadoc)
         *
         * @see android.widget.Adapter#getCount()
         */
        @Override
        public int getCount() {
            return listItem.size();
        }

        /*
         * (non-Javadoc)
         *
         * @see android.widget.Adapter#getItem(int)
         */
        @Override
        public Object getItem(int position) {
            return listItem.get(position);
        }

        /*
         * (non-Javadoc)
         *
         * @see android.widget.Adapter#getItemId(int)
         */
        @Override
        public long getItemId(int position) {
            return listItem.indexOf(getItem(position));
        }

        /**
         * Filter nearest client list.
         *
         * @param query the query
         */
        @SuppressLint("DefaultLocale")
        public void filterData(String query) {

            query = query.toLowerCase();

            listItem.clear();

            if (query.isEmpty()) {
                listItem.addAll(orignalList);
            } else {

                for (HashMap<String, String> client : orignalList) {
                    String type = client.get("ObjectType");
                    String nom = client.get("nom");
                    if (type.equals("Site")) {
                        nom = client.get("nom") + " - ("
                                + client.get("nmClient") + ")";
                    }
                    if (query != null && query.length() > 0)
                        if ((nom.toLowerCase().contains(query))) {

                            listItem.add(client);
                        }

                }
            }

            notifyDataSetChanged();

        }

    }
}
