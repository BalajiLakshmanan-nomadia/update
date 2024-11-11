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
import android.widget.Toast;

import com.synchroteam.TypefaceLibrary.EditText;
import com.synchroteam.beans.Client_Site_EquipmnentBean;
import com.synchroteam.beans.EnableSynchronizationAddJobEvent;
import com.synchroteam.beans.InventoryItemBeans;
import com.synchroteam.dao.Dao;
import com.synchroteam.listadapters.EquipmentSectionListAdapterNew;
import com.synchroteam.synchroteam.EquipmentDetials;
import com.synchroteam.synchroteam.SyncoteamNavigationActivity;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.utils.AsyncTaskCoroutine;
import com.synchroteam.utils.DaoManager;
import com.synchroteam.utils.KEYS;
import com.synchroteam.utils.KEYS.EquipmentDetail;
import com.synchroteam.utils.Logger;
import com.synchroteam.utils.SharedPref;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import de.greenrobot.event.EventBus;

// TODO: Auto-generated Javadoc

/**
 * The Class EquipmentsListFragmentHelperNew is to inflate and control all
 * the actions performed in Equipments screen. Created For Future Purpose
 *
 * @author $Ideavate Solution
 */
public class EquipmentsListFragmentHelperNew implements HelperInterface {

    private Dao dataAccessObject;
    private SyncoteamNavigationActivity syncoteamNavigationActivity;
    private EquipmentSectionListAdapterNew equipmentSectionListAdapterNew;
    private ArrayList<Client_Site_EquipmnentBean> equipmentList;

    private ListView equipmentsListView;
    private ProgressBar progressBar;
    private LinearLayout searchContanerClientList;
    private EditText searchViewEt;
    private LinearLayout bottomProgressView;

    private boolean isUserScrolled = false;

    private int equipmentCnt;
    private Dao.EquipmentFilter filter;

    private int mCount = 15;
    public static final String TAG = EquipmentsListFragmentHelperNew.class.getSimpleName();


    /**
     * Instantiates a new description job detail fragment helper.
     */
    public EquipmentsListFragmentHelperNew(
            SyncoteamNavigationActivity syncoteamNavigationActivity) {

        dataAccessObject = DaoManager.getInstance();
        filter = dataAccessObject.new EquipmentFilter();

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
        View view = inflater.inflate(R.layout.layout_section_equipment_new, null);

        initiateView(view);
        fetchDataFromDataBase();
        SharedPref.setIsEquipmentFetched(syncoteamNavigationActivity, false);

        return view;
    }

    @Override
    public void initiateView(View view) {
        equipmentsListView = (ListView) view
                .findViewById(R.id.equipmentsListView);
        bottomProgressView = (LinearLayout) view.findViewById(R.id.loadItemsLayout);
        searchContanerClientList = (LinearLayout) view
                .findViewById(R.id.searchContanerClientList);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        searchViewEt = (EditText) view.findViewById(R.id.searchViewEt);

        equipmentsListView.setOnItemClickListener(onItemClickListener);
        searchViewEt.addTextChangedListener(textWatcher);
        searchViewEt.setNextFocusDownId(searchViewEt.getId()); //keep focus when enter key pressed
    }

    @Override
    public void doOnSyncronize() {
        fetchDataFromDataBase();
    }

    @Override
    public void onReturnToActivity(int requestCode) {

    }

    private void fetchDataFromDataBase() {
        //For fetching the equipment details
        new FetchEquipment().execute();
    }

    /**
     * Updates the client list.
     */
    private void updateEquipmentList() {
        bottomProgressView.setVisibility(View.VISIBLE);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                filter.offset += mCount;

                ArrayList<Client_Site_EquipmnentBean> list = dataAccessObject._equipmentList(filter);
                equipmentList.addAll(list);

                updateAdapter(list);

                bottomProgressView.setVisibility(View.GONE);

            }
        }, 900);
    }


    /**
     * Item click listener for listview.
     */
    private OnItemClickListener onItemClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            Client_Site_EquipmnentBean client_Site_EquipmnentBean = equipmentList
                    .get(position);

            Intent equipmentIntent = new Intent(syncoteamNavigationActivity,
                    EquipmentDetials.class);
            equipmentIntent.putExtra(EquipmentDetail.ID_CLIENT,
                    client_Site_EquipmnentBean.getIdClient());
            equipmentIntent.putExtra(EquipmentDetail.ID_SITE,
                    client_Site_EquipmnentBean.getIdSite());
            equipmentIntent.putExtra(EquipmentDetail.EQUIPMENTS_ID,
                    client_Site_EquipmnentBean.getIdEquipement());
            if (client_Site_EquipmnentBean.getRefCustomer().length()>0){
                equipmentIntent.putExtra(EquipmentDetail.EQUIPMENTS_NAME,
                        client_Site_EquipmnentBean.getNmEquipement()+" ("+client_Site_EquipmnentBean.getRefCustomer()+")");
            }else {
                equipmentIntent.putExtra(EquipmentDetail.EQUIPMENTS_NAME,
                        client_Site_EquipmnentBean.getNmEquipement());
            }
            equipmentIntent.putExtra(EquipmentDetail.CLIENT_NAME,
                    client_Site_EquipmnentBean.getNmClient());
            equipmentIntent.putExtra(EquipmentDetail.SITE_NAME,
                    client_Site_EquipmnentBean.getNmSite());

            if (client_Site_EquipmnentBean.getPublickLink() != null)
                equipmentIntent.putExtra(KEYS.EquipmentDetail.PUBLIC_LINK,
                        client_Site_EquipmnentBean.getPublickLink());
            else {
                equipmentIntent.putExtra(KEYS.EquipmentDetail.PUBLIC_LINK,
                        "");
            }

            syncoteamNavigationActivity.startActivity(equipmentIntent);

        }

    };

    /**
     * The text watcher. to watch test edit in edit text and filter client list
     */
    TextWatcher textWatcher = new TextWatcher() {

        private final long DELAY = 600;//def600
        private java.util.Timer timerSearch;

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
            if (timerSearch != null) {
                timerSearch.cancel();
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {

            String searchText = s.toString().trim();
            filter.searchText = s.toString().trim();

            timerSearch = new Timer();
            timerSearch.schedule(new TimerTask() {
                @Override
                public void run() {
                    performSearch(searchText);
                }
            }, DELAY);

        }
    };

    private void performSearch(String s) {

        ArrayList<Client_Site_EquipmnentBean> list = new ArrayList<>();

        //new changes
        filter.offset = 1;
        filter.searchText = s;
        equipmentList.clear();

        syncoteamNavigationActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.VISIBLE);
            }
        });

        try {

            equipmentCnt = dataAccessObject._equipmentListCount(filter);
            list = dataAccessObject._equipmentList(filter);

        } catch (Exception e) {

        }

        updateAdapter(list);

    }

    public void updateAdapter(final ArrayList<Client_Site_EquipmnentBean> list) {
        syncoteamNavigationActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (list != null) {
                    progressBar.setVisibility(View.GONE);

                    equipmentList.addAll(list);
                    equipmentSectionListAdapterNew.notifyDataSetChanged();

                    progressBar.setVisibility(View.GONE);

                }
            }
        });

    }
    /**
     * Scroll listener for list view
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

                if(totalItemCount<equipmentCnt)
                    updateEquipmentList();

            }
        }
    };


    private class FetchEquipment extends AsyncTaskCoroutine<Void, Void> {

        public FetchEquipment() {

            filter.offset = 1;

            if (equipmentList != null) {
                equipmentList.clear();
            } else {
                equipmentList = new ArrayList<>();
            }

        }

        @Override
        public void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            equipmentsListView.setVisibility(View.GONE);
            searchContanerClientList.setVisibility(View.GONE);
        }

        @Override
        public Void doInBackground(Void... params) {
            equipmentCnt = 0;

            equipmentCnt = dataAccessObject._equipmentListCount(filter);
            ArrayList<Client_Site_EquipmnentBean> listEquipment = dataAccessObject._equipmentList(filter);

            equipmentList.addAll(listEquipment);

            Logger.log(TAG, "Equipment list cnt new ==>" + equipmentCnt);
            Logger.log(TAG, "Equipment list size old ==>" + listEquipment.size());
            return null;
        }

        @Override
        public void onPostExecute(Void result) {
            super.onPostExecute(result);
            progressBar.setVisibility(View.GONE);
            equipmentsListView.setVisibility(View.VISIBLE);
            searchContanerClientList.setVisibility(View.VISIBLE);

            if (equipmentSectionListAdapterNew == null) {
                equipmentSectionListAdapterNew = new EquipmentSectionListAdapterNew(
                        syncoteamNavigationActivity,
                        equipmentList);
                equipmentsListView.setAdapter(equipmentSectionListAdapterNew);

            } else {
                equipmentSectionListAdapterNew.notifyDataSetChanged();
            }

            SharedPref.setIsEquipmentFetched(syncoteamNavigationActivity, true);
            equipmentsListView.setOnScrollListener(mOnScrollListener);
            EventBus.getDefault().post(new EnableSynchronizationAddJobEvent());
        }

    }

}
