package com.synchroteam.fragmenthelper;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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

import com.synchroteam.TypefaceLibrary.EditText;
import com.synchroteam.beans.Client_Site_Bean;
import com.synchroteam.beans.EnableSynchronizationAddJobEvent;
import com.synchroteam.dao.Dao;
import com.synchroteam.listadapters.SiteSectionListAdapterNew;
import com.synchroteam.synchroteam.SiteDetail;
import com.synchroteam.synchroteam.SyncoteamNavigationActivity;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.utils.AsyncTaskCoroutine;
import com.synchroteam.utils.DaoManager;
import com.synchroteam.utils.KEYS;
import com.synchroteam.utils.SharedPref;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

/**
 * The Class SiteListFragmentHelper is to inflate and control all the actions
 * performed in Site listing screen
 *
 * @author $Ideavate Solution
 */
public class SiteListFragmentHelperNew implements HelperInterface {

    private ListView siteListView;
    private LinearLayout searchContanerClientList;
    private EditText searchViewEt;
    private LinearLayout bottomProgressView;

    private SyncoteamNavigationActivity syncoteamNavigationActivity;
    private Dao dataAccessObject;
    private ProgressBar progressBar;
    private ArrayList<Client_Site_Bean> siteList;
    private ArrayList<Client_Site_Bean> siteListOriginal;
    private SiteSectionListAdapterNew siteSectionListAdapterNew;

    private int siteCount;
    private int searchCount;
    private String searchText;
    private boolean isUserSearching = false;
    private boolean isUserScrolled = false;
    private int listOffset = 1;
    private int searchOffset = 1;

//    public static final String TAG = "SiteListFragmentHelper";

    /**
     * Instantiates a new SiteListFragmentHelper.
     *
     * @param syncoteamNavigationActivity
     */
    public SiteListFragmentHelperNew(
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
        View view = inflater.inflate(R.layout.layout_section_site_new, null);
        initiateView(view);
        fetchDataFromDataBase();
        SharedPref.setIsSiteFetched(syncoteamNavigationActivity, false);
        return view;
    }

    @Override
    public void initiateView(View view) {
        siteListView = (ListView) view.findViewById(R.id.siteListView);
        bottomProgressView = (LinearLayout) view.findViewById(R.id.loadItemsLayout);
        searchContanerClientList = (LinearLayout) view
                .findViewById(R.id.searchContanerClientList);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        searchViewEt = (EditText) view.findViewById(R.id.searchViewEt);

        searchViewEt.addTextChangedListener(textWatcher);
        siteListView.setOnItemClickListener(onItemClickListener);
    }

    @Override
    public void doOnSyncronize() {
        fetchDataFromDataBase();
    }

    @Override
    public void onReturnToActivity(int requestCode) {
    }

    /**
     * Fetches data from db
     */
    private void fetchDataFromDataBase() {
        new FetchClientSiteAsyncTask().execute();
    }

    /**
     * Updates the client list.
     */
    private void updateSiteList() {
        bottomProgressView.setVisibility(View.VISIBLE);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isUserSearching) {
                    listOffset = listOffset + 15;
                    ArrayList<Client_Site_Bean> listSite = dataAccessObject.getSitesWithOffset(listOffset);
                    siteList.addAll(listSite);
                    siteListOriginal.addAll(listSite);
                } else {
                    searchOffset = searchOffset + 15;
                    siteList.addAll(dataAccessObject.getSitesSearch(searchOffset, searchText));
                }

                if (siteSectionListAdapterNew != null)
                    siteSectionListAdapterNew.notifyDataSetChanged();
                bottomProgressView.setVisibility(View.GONE);
            }
        }, 900);

//        new FetchSites().execute();
    }

    /**
     * Async task to get client details
     */
    private class FetchClientSiteAsyncTask extends AsyncTaskCoroutine<Void, Void> {

        public FetchClientSiteAsyncTask() {
            if (siteList != null) {
                siteList.clear();
            } else {
                siteList = new ArrayList<>();
            }

            if (siteListOriginal != null) {
                siteListOriginal.clear();
            } else {
                siteListOriginal = new ArrayList<>();
            }

        }

        @Override
        public void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            siteListView.setVisibility(View.GONE);
            searchContanerClientList.setVisibility(View.GONE);

        }

        @Override
        public Void doInBackground(Void... params) {
            ArrayList<Client_Site_Bean> listSites = dataAccessObject.getSitesWithOffset(listOffset);
            siteList.addAll(listSites);
            siteListOriginal.addAll(listSites);
            return null;
        }

        @Override
        public void onPostExecute(Void result) {
            super.onPostExecute(result);

            progressBar.setVisibility(View.GONE);
            siteListView.setVisibility(View.VISIBLE);
            searchContanerClientList.setVisibility(View.VISIBLE);

            siteCount = dataAccessObject.getSitesCount();

            if (siteSectionListAdapterNew == null) {
                siteSectionListAdapterNew = new SiteSectionListAdapterNew(
                        syncoteamNavigationActivity, siteList);
                siteListView.setAdapter(siteSectionListAdapterNew);
            } else {
                siteSectionListAdapterNew.notifyDataSetChanged();
            }

            SharedPref.setIsSiteFetched(syncoteamNavigationActivity, true);

            siteListView.setOnScrollListener(mOnScrollListener);

            EventBus.getDefault().post(new EnableSynchronizationAddJobEvent());

        }

    }

    /**
     * Fetch the site list while scrolling.
     */
    private class FetchSites extends AsyncTaskCoroutine<Void, Void> {

        @Override
        public void onPreExecute() {
            super.onPreExecute();
            bottomProgressView.setVisibility(View.VISIBLE);
        }

        @Override
        public Void doInBackground(Void... params) {
            if (!isUserSearching) {
                listOffset = listOffset + 50;
                ArrayList<Client_Site_Bean> listSite = dataAccessObject.getSitesWithOffset(listOffset);
                siteList.addAll(listSite);
                siteListOriginal.addAll(listSite);
            } else {
                searchOffset = searchOffset + 50;
                siteList.addAll(dataAccessObject.getSitesSearch(searchOffset, searchText));
            }
            return null;
        }

        @Override
        public void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (siteSectionListAdapterNew != null)
                siteSectionListAdapterNew.notifyDataSetChanged();
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
            } else {
                isUserSearching = false;
            }

            if (!TextUtils.isEmpty(s.toString())) {

                isUserSearching = true;
                searchOffset = 1;
                siteList.clear();
                searchText = s.toString();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        siteList.clear();
                        searchCount = dataAccessObject.getSiteSearchCount(searchText);
                        siteList.addAll(dataAccessObject.getSitesSearch(searchOffset, searchText));
                        if (siteSectionListAdapterNew != null)
                            siteSectionListAdapterNew.notifyDataSetChanged();
                    }
                }, 200);

            } else {
                isUserSearching = false;
                searchText = "";
                siteList.clear();
                siteList.addAll(siteListOriginal);
                if (siteSectionListAdapterNew != null)
                    siteSectionListAdapterNew.notifyDataSetChanged();
            }
        }
    };

    /**
     * Item click listener for list view
     */
    OnItemClickListener onItemClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            Client_Site_Bean client_Site_Bean = siteList.get(position);

            Intent intent = new Intent(syncoteamNavigationActivity,
                    SiteDetail.class);
            intent.putExtra(KEYS.SiteDetails.ID_SITE,
                    client_Site_Bean.getIdSite());
            intent.putExtra(KEYS.SiteDetails.ID_CLIENT,
                    client_Site_Bean.getIdClient());
            intent.putExtra(KEYS.SiteDetails.NAME_SITE,
                    client_Site_Bean.getNmSite());
            intent.putExtra(KEYS.SiteDetails.CLIENT_NAME,
                    client_Site_Bean.getClientName());
            intent.putExtra(KEYS.SiteDetails.REF_CUSTOMER,
                    client_Site_Bean.getRefCustomer());
            syncoteamNavigationActivity.startActivity(intent);

        }

    };

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
                    if (totalItemCount < siteCount) {
                        updateSiteList();
                    }
                } else if (isUserSearching) {
                    if (totalItemCount < searchCount) {
                        updateSiteList();
                    }
                }
            }
        }
    };
}
