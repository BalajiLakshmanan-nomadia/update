package com.synchroteam.fragmenthelper;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.sap.ultralitejni17.ULjException;
import com.synchroteam.TypefaceLibrary.EditText;
import com.synchroteam.beans.Client;
import com.synchroteam.beans.EnableSynchronizationAddJobEvent;
import com.synchroteam.dao.Dao;
import com.synchroteam.listadapters.ClientSectionListAdapterNew;
import com.synchroteam.synchroteam.ClientDetail;
import com.synchroteam.synchroteam.SyncoteamNavigationActivity;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.utils.AsyncTaskCoroutine;
import com.synchroteam.utils.DaoManager;
import com.synchroteam.utils.KEYS;
import com.synchroteam.utils.Logger;
import com.synchroteam.utils.SharedPref;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

// TODO: Auto-generated Javadoc

/**
 * The Class  is to inflate and control all the actions
 * performed in Client listing screen
 *
 * @author $Ideavate Solution
 */
public class ClientListFragmentHelperNew implements HelperInterface {

    private ListView clientListView;
    private ProgressBar progressBar;
    private EditText searchViewEt;
    private LinearLayout searchContainerClientList;
    private LinearLayout bottomProgressView;

    private ClientSectionListAdapterNew clientSectionListAdapter;
    private Dao dataAccessObject;
    private SyncoteamNavigationActivity syncoteamNavigationActivity;
    private ArrayList<Client> clientsList;
    private ArrayList<Client> clientsListOriginal;

    private int clientCount;
    private int searchCount;
    private String searchText;
    private boolean isUserSearching = false;
    private boolean isUserScrolled = false;
    private int listOffset = 1;
    private int searchOffset = 1;

//    private static final String TAG = ClientListFragmentHelperNew.class
//            .getSimpleName();

    /**
     * Instantiates a new ClientListFragmentHelper .
     */
    public ClientListFragmentHelperNew(
            SyncoteamNavigationActivity syncoteamNavigationActivity) {
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
        View view = inflater.inflate(R.layout.layout_section_client_new, null);

        initiateView(view);

        fetchDataFromDataBase();
        SharedPref.setIsClientFetched(syncoteamNavigationActivity, false);

        return view;
    }

    /**
     * Initiates view
     *
     * @param view
     */
    @Override
    public void initiateView(View view) {
        clientListView = (ListView) view.findViewById(R.id.cientListView);
        bottomProgressView = (LinearLayout) view.findViewById(R.id.loadItemsLayout);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        searchContainerClientList = (LinearLayout) view.findViewById(R.id.searchContanerClientList);
        searchViewEt = (EditText) view.findViewById(R.id.searchViewEt);
        clientListView.setOnItemClickListener(mOnItemClickListener);
    }

    /**
     * Fetches value from db
     */
    private void fetchDataFromDataBase() {
        new FetchClientsAsyncTask().execute();
    }

    /**
     * Updates the client list.
     */
    private void updateClientList() {
        bottomProgressView.setVisibility(View.VISIBLE);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    if (!isUserSearching) {
                        listOffset = listOffset + 25;
                        ArrayList<Client> clientList = dataAccessObject.getClientsByOffset(listOffset);
                        clientsList.addAll(clientList);
                        clientsListOriginal.addAll(clientList);
                    } else {
                        searchOffset = searchOffset + 25;
                        clientsList.addAll(dataAccessObject.getClientsSearch(searchOffset, searchText));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                clientSectionListAdapter.notifyDataSetChanged();
                bottomProgressView.setVisibility(View.GONE);
            }
        },900);

//        new FetchClients().execute();
    }

    @Override
    public void doOnSyncronize() {
        fetchDataFromDataBase();
    }

    @Override
    public void onReturnToActivity(int requestCode) {

    }

    /**
     * Async task for fetching list.
     */
    private class FetchClientsAsyncTask extends AsyncTaskCoroutine<Void, Void> {

        public FetchClientsAsyncTask() {

            if (clientsList != null) {
                clientsList.clear();
            } else {
                clientsList = new ArrayList<>();
            }

            if (clientsListOriginal != null) {
                clientsListOriginal.clear();
            } else {
                clientsListOriginal = new ArrayList<>();
            }
        }

        @Override
        public void onPreExecute() {
            super.onPreExecute();

            searchViewEt.setText("");
            progressBar.setVisibility(View.VISIBLE);
            clientListView.setVisibility(View.GONE);
            searchContainerClientList.setVisibility(View.GONE);

        }

        @Override
        public Void doInBackground(Void... params) {
            try {
                ArrayList<Client> listClients = dataAccessObject.getClientsByOffset(listOffset);
                clientsList.addAll(listClients);
                clientsListOriginal.addAll(listClients);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        public void onPostExecute(Void result) {
            super.onPostExecute(result);

            clientCount = dataAccessObject.getCustomerCount();

            Logger.log("oNpostEceuteClientListing", clientCount + "");
            progressBar.setVisibility(View.GONE);
            clientListView.setVisibility(View.VISIBLE);

            searchContainerClientList.setVisibility(View.VISIBLE);

            if (clientSectionListAdapter == null) {
                clientSectionListAdapter = new ClientSectionListAdapterNew(
                        syncoteamNavigationActivity, clientsList);
                clientListView.setAdapter(clientSectionListAdapter);

            } else {

                clientSectionListAdapter.notifyDataSetChanged();

            }

            searchViewEt.addTextChangedListener(textWatcher);

            clientListView.setOnScrollListener(mOnScrollListener);

            SharedPref.setIsClientFetched(syncoteamNavigationActivity, true);

            EventBus.getDefault().post(new EnableSynchronizationAddJobEvent());
        }

    }

    /**
     * Fetch the site list while scrolling.
     */
    private class FetchClients extends AsyncTaskCoroutine<Void, Void> {

        @Override
        public void onPreExecute() {
            super.onPreExecute();
            bottomProgressView.setVisibility(View.VISIBLE);
        }

        @Override
        public Void doInBackground(Void... params) {
            try {
                if (!isUserSearching) {
                    listOffset = listOffset + 25;
                    ArrayList<Client> clientList = dataAccessObject.getClientsByOffset(listOffset);
                    clientsList.addAll(clientList);
                    clientsListOriginal.addAll(clientList);
                } else {
                    searchOffset = searchOffset + 25;
                    clientsList.addAll(dataAccessObject.getClientsSearch(searchOffset, searchText));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            clientSectionListAdapter.notifyDataSetChanged();
            bottomProgressView.setVisibility(View.GONE);
        }
    }

    /**
     * The text watcher. to watch test edit in edit text and filter client list
     */
    TextWatcher textWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (!TextUtils.isEmpty(s.toString())) {

                isUserSearching = true;
                searchOffset = 1;
                clientsList.clear();
                searchText = s.toString();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            clientsList.clear();
                            searchCount = dataAccessObject.getCustomerSearchCount(searchText);
                            clientsList.addAll(dataAccessObject.getClientsSearch(searchOffset, searchText));
                            clientSectionListAdapter.notifyDataSetChanged();
                            Log.e("afterTextChanged","clientsList size>>" + clientsList.size());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, 100);

            } else {
                isUserSearching = false;
                searchText = "";
                clientsList.clear();
                clientsList.addAll(clientsListOriginal);
                clientSectionListAdapter.notifyDataSetChanged();
            }
        }
    };

    /**
     * Scroll listener for list view.
     */
    private OnScrollListener mOnScrollListener = new OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            if (scrollState == OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isUserScrolled = true;
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            if (isUserScrolled && firstVisibleItem + visibleItemCount == totalItemCount) {
                isUserScrolled = false;

                if (!isUserSearching) {
                    if (totalItemCount < clientCount) {
                        updateClientList();
                    }
                } else if (isUserSearching) {
                    if (totalItemCount < searchCount) {
                        updateClientList();
                    }
                }
            }
        }
    };

    private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(syncoteamNavigationActivity,
                    ClientDetail.class);

            Client client = clientsList.get(position);

            intent.putExtra(KEYS.ClientDetial.ID_CLIENT,
                    client.getIdClient());
            intent.putExtra(KEYS.ClientDetial.CLIENT_NAME,
                    client.getNmClient());
            intent.putExtra(KEYS.ClientDetial.REF_CUSTOMER,
                    client.getRef_customer());

            syncoteamNavigationActivity.startActivity(intent);
        }
    };

}
