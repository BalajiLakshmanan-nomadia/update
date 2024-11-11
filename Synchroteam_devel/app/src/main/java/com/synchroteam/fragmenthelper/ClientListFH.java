package com.synchroteam.fragmenthelper;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.synchroteam.TypefaceLibrary.EditText;
import com.synchroteam.beans.Client;
import com.synchroteam.beans.EnableSynchronizationAddJobEvent;
import com.synchroteam.dao.Dao;
import com.synchroteam.listadapters.ClientListWithSiteAdapter;
import com.synchroteam.synchroteam.SyncoteamNavigationActivity;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.utils.AsyncTaskCoroutine;
import com.synchroteam.utils.DaoManager;
import com.synchroteam.utils.Logger;
import com.synchroteam.utils.SharedPref;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

// TODO: Auto-generated Javadoc

/**
 * The Class SiteListFragmentHelper is to inflate and control all the actions
 * performed in Site listing screen
 *
 * @author $Ideavate Solution
 */
public class ClientListFH implements HelperInterface {

    public static final String TAG = "ClientListFragmentHelper";
    private Dao dataAccessObject;

    private ListView siteListView;

    private SyncoteamNavigationActivity syncoteamNavigationActivity;

    private ProgressBar progressBar;
    private View footerView;
    private ArrayList<Client> client_Site_Beans;
    private ClientListWithSiteAdapter clientSectionListAdapter;
    private int siteCount;
    private int index;

    private LinearLayout searchContanerClientList;

    private EditText searchViewEt;

    //   private Filter filter;

    private boolean isUserSearching = false;

    /**
     * Instantiates a new SiteListFragmentHelper.
     *
     * @param syncoteamNavigationActivity
     */
    public ClientListFH(
            SyncoteamNavigationActivity syncoteamNavigationActivity) {
        // TODO Auto-generated constructor stub

        dataAccessObject = DaoManager.getInstance();
        this.syncoteamNavigationActivity = syncoteamNavigationActivity;

    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.synchroteam.fragmenthelper.HelperInterface#inflateLayout(android.
     * view.LayoutInflater, android.view.ViewGroup)
     */
    @Override
    public View inflateLayout(LayoutInflater inflater, ViewGroup container) {
        // TODO Auto-generated method stub
        View view = inflater.inflate(R.layout.layout_section_site_new, null);

        siteListView = (ListView) view.findViewById(R.id.siteListView);

       // siteListView.setOnItemClickListener(onItemClickListener);
        searchContanerClientList = (LinearLayout) view
                .findViewById(R.id.searchContanerClientList);

        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);

        footerView = ((LayoutInflater) syncoteamNavigationActivity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
                R.layout.layout_footerview_items_list, null, false);

        searchViewEt = (EditText) view.findViewById(R.id.searchViewEt);
        index = 1;
        fetchDataFromDataBase();
        SharedPref.setIsSiteFetched(syncoteamNavigationActivity, false);
        return view;
    }

//    OnItemClickListener onItemClickListener = new OnItemClickListener() {
//
//        @Override
//        public void onItemClick(AdapterView<?> parent, View view, int position,
//                                long id) {
//            // TODO Auto-generated method stub
//
//            Client client_Site_Bean = client_Site_Beans.get(position);
//
//            Intent intent = new Intent(syncoteamNavigationActivity,
//                    ClientDetail.class);
//            intent.putExtra(KEYS.ClientDetial.ID_CLIENT,
//                    client_Site_Bean.getIdClient());
//            intent.putExtra(KEYS.ClientDetial.CLIENT_NAME,
//                    client_Site_Bean.getNmClient());
//
//            syncoteamNavigationActivity.startActivity(intent);
//
//
//        }
//
//    };

    /**
     * The text watcher. to watch test edit in edit text and filter client list
     */
    TextWatcher textWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
            if (!TextUtils.isEmpty(s.toString())) {
                clientSectionListAdapter.getFilter().filter(s.toString());
                isUserSearching = true;
            } else {
                if (before >= 1)
                    clientSectionListAdapter.getFilter().filter(s.toString());
                else
                    isUserSearching = false;
            }


        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {


        }

        @Override
        public void afterTextChanged(Editable s) {
            // TODO Auto-generated method stub
            // if (!TextUtils.isEmpty(s.toString())) {
            // if (footerView != null
            // && (footerView.getVisibility() == View.VISIBLE))
            //
            // footerView.setVisibility(View.GONE);
            //
            // } else {
            // if (footerView != null
            // && (footerView.getVisibility() == View.GONE))
            // footerView.setVisibility(View.VISIBLE);
            // }


//            filter.filter(s.toString());

        }
    };

    private void fetchDataFromDataBase() {
        // TODO Auto-generated method stub
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            new FetchClientSiteAsyncTask().execute();
        else
            new FetchClientSiteAsyncTask().execute();
        // new FetchEquipment().execute();

    }

    private class FetchClientSiteAsyncTask extends AsyncTaskCoroutine<Void, Void> {

        public FetchClientSiteAsyncTask() {
            searchViewEt.setText("");

            if (client_Site_Beans != null) {
                client_Site_Beans.clear();
            } else {
                client_Site_Beans = new ArrayList<Client>();
            }

        }

        @Override
        public void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();

            progressBar.setVisibility(View.VISIBLE);
            siteListView.setVisibility(View.GONE);
            searchContanerClientList.setVisibility(View.GONE);

        }

        @Override
        public Void doInBackground(Void... params) {
            // TODO Auto-generated method stub
            client_Site_Beans.addAll(dataAccessObject.getClients());
            Logger.output(TAG, " client client " + client_Site_Beans.size());
            return null;
        }

        @Override
        public void onPostExecute(Void result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);

            progressBar.setVisibility(View.GONE);
            siteListView.setVisibility(View.VISIBLE);
            searchContanerClientList.setVisibility(View.VISIBLE);

            siteCount = client_Site_Beans.size();

            Logger.log("oNpostEceuteSiteListing", siteCount + "");

            if (siteCount > 20) {

                siteListView.addFooterView(footerView);
            }

            if (true) {
                if (searchViewEt.getText().toString().trim().length() >= 1) {
                    searchViewEt.setText("");
                } else {
                    clientSectionListAdapter = new ClientListWithSiteAdapter(
                            syncoteamNavigationActivity, client_Site_Beans,
                            dataAccessObject, ClientListFH.this);
                    clientSectionListAdapter.setIndexPosition(1);

                    siteListView.setAdapter(clientSectionListAdapter);
                  //  clientSectionListAdapter.notifyDataSetChanged();
                    searchViewEt.addTextChangedListener(textWatcher);
                }


            } else {

                clientSectionListAdapter.notifyDataSetChanged();

            }

            // if (filter == null) {

            // filter = clientSectionListAdapter.getFilter();
            //   searchViewEt.addTextChangedListener(textWatcher);
            // }

            SharedPref.setIsSiteFetched(syncoteamNavigationActivity, true);

            siteListView.setOnScrollListener(new OnScrollListener() {

                @Override
                public void onScrollStateChanged(AbsListView view,
                                                 int scrollState) {
                    // TODO Auto-generated method stub

                    // int ctadapter = clientSectionListAdapter.getCount();

                    if (footerView != null && footerView.isShown()) {

                        index++;
                        clientSectionListAdapter.setIndexPosition(index);

                        try {
                            Thread.sleep(900);
                        } catch (InterruptedException e) {
                            Logger.printException(e);
                        }
                        syncoteamNavigationActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                clientSectionListAdapter.notifyDataSetChanged();
                            }
                        });


                    }


                }

                @Override
                public void onScroll(AbsListView arg0, int arg1, int arg2,
                                     int arg3) {
                    int ctadapter = clientSectionListAdapter.getCount();

                    if (!isUserSearching) {
                        if (ctadapter >= siteCount) {
                            if (siteListView.getAdapter() != null) {
                                siteListView.removeFooterView(footerView);
                                clientSectionListAdapter.notifyDataSetChanged();
                            }
                        }
                    } else {
                        if (ctadapter >= clientSectionListAdapter
                                .getArrayCount()) {
                            hideFooterView();
                        } else {
                            showFooterView();
                        }
                    }
                }
            });

            EventBus.getDefault().post(new EnableSynchronizationAddJobEvent());

        }

    }

    @Override
    public void initiateView(View v) {
        // TODO Auto-generated method stub

    }

    @Override
    public void doOnSyncronize() {
        searchViewEt.removeTextChangedListener(textWatcher);
        searchViewEt.setText("");

        fetchDataFromDataBase();
    }

    @Override
    public void onReturnToActivity(int requestCode) {
        // TODO Auto-generated method stub

    }

    public void hideFooterView() {
        if (footerView != null) {
            if (footerView.getVisibility() == View.VISIBLE) {
                footerView.setVisibility(View.INVISIBLE);
            }
        }

    }

    public void showFooterView() {
        if (footerView != null) {
            if (footerView.getVisibility() == View.INVISIBLE) {
                footerView.setVisibility(View.VISIBLE);
            }
        }
    }

}
