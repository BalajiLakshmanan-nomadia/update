package com.synchroteam.synchroteam;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.TypefaceSpan;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Filter;
import android.widget.ListView;
import android.widget.TextView.OnEditorActionListener;

import com.synchroteam.TypefaceLibrary.EditText;
import com.synchroteam.TypefaceLibrary.TextView;
import com.synchroteam.beans.Equipement;
import com.synchroteam.beans.Site;
import com.synchroteam.dao.Dao;
import com.synchroteam.listadapters.EquipmentsListAdapter;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.utils.AsyncTaskCoroutine;
import com.synchroteam.utils.DaoManager;
import com.synchroteam.utils.DialogUtils;
import com.synchroteam.utils.KEYS;
import com.synchroteam.utils.Logger;

import java.util.ArrayList;

// TODO: Auto-generated Javadoc

/**
 * The Class EquipmentList to show equipments list on new job screen.
 *
 * @author $Ideavate Solution
 */
public class EquipmentListCLientDetail extends AppCompatActivity {

    /**
     * The client list lv.
     */
    private ListView equipmentListLv;

    /**
     * The equipements.
     */
    private ArrayList<Equipement> equipements;

    /**
     * The data accessobject.
     */
    private Dao dataAccessobject;

    /**
     * The equipments list adapter.
     */
    private EquipmentsListAdapter equipmentsListAdapter;

    /**
     * The search view et.
     */
    private EditText searchViewEt;

    /**
     * The site id.
     */
    private int clientId, siteId;

    /**
     * The cancel tv.
     */
    private TextView cancelTv;

    /**
     * The equipment count.
     */
    private int equipmentCount;

    /**
     * The footer view.
     */
    private View footerView;

    /**
     * The index.
     */
    private int index = 1;

    /**
     * The filter.
     */
    private Filter filter;

    private boolean isSelectionEnabled;

    private ActionBar actionBar;

    private String clientName, siteName;

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout_newjob_equipment_dialog);
        equipmentListLv = (ListView) findViewById(R.id.equipmentListLv);
        findViewById(R.id.searchContanerEquipmentList).setVisibility(View.GONE);

        searchViewEt = (EditText) findViewById(R.id.searchViewEt);
        searchViewEt.addTextChangedListener(textWatcher);

        actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);

        String title = this.getResources().getString(
                R.string.textEquipmentListingLable);
        SpannableString titleSpannable = new SpannableString(title);
        titleSpannable.setSpan(
                new TypefaceSpan(this.getResources().getString(
                        R.string.fontName_hevelicaLight)), 0,
                titleSpannable.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // actionBar.setTitle(titleSpannable);
        actionBar.setTitle(isLGDevice() ? title : titleSpannable);

        dataAccessobject = DaoManager.getInstance();

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            clientId = bundle.getInt(KEYS.NewJob.CLIENT_ID);
            siteId = bundle.getInt(KEYS.NewJob.SITE_ID);

            clientName = bundle.getString(KEYS.NewJob.CLIENT_NAME);
            siteName = bundle.getString(KEYS.NewJob.SITE_NAME);
        }

        isSelectionEnabled = getIntent().getBooleanExtra(
                KEYS.ClientDetial.ITEM_SELECTION_ENABLED, true);
        if (isSelectionEnabled) {
            equipmentListLv.setOnItemClickListener(onItemClickListener);
        }
        cancelTv = (TextView) findViewById(R.id.cancelTv);
        equipmentCount = dataAccessobject.getEquipementsForSiteCount(clientId,
                siteId);
        if (equipmentCount > 20) {
            footerView = ((LayoutInflater) this
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                    .inflate(R.layout.layout_footerview_items_list, null, false);
            equipmentListLv.addFooterView(footerView);
        }

        cancelTv.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                try {
                    InputMethodManager inputManager = (InputMethodManager) EquipmentListCLientDetail.this
                            .getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (EquipmentListCLientDetail.this.getWindow()
                            .getCurrentFocus() != null) {
                        inputManager.hideSoftInputFromWindow(
                                EquipmentListCLientDetail.this.getWindow()
                                        .getCurrentFocus().getWindowToken(), 0);
                    }

                } catch (Exception e) {
                    Logger.printException(e);
                }

                setResult(RESULT_CANCELED);
                finish();

            }
        });

        searchViewEt.setOnEditorActionListener(new OnEditorActionListener() {

            @Override
            public boolean onEditorAction(android.widget.TextView v,
                                          int actionId, KeyEvent event) {
                // TODO Auto-generated method stub
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    // bundle.putSerializable(KEYS.NewJob.CLIENT_DETAILS, clt);
                    bundle.putInt(KEYS.NewJob.EQUIPMENTS_ID, -1);
                    bundle.putString(KEYS.NewJob.EQUIPMENTS_NAME, v.getText()
                            .toString());

                    intent.putExtras(bundle);
                    // intent.putExtra(KEYS.NewJob.CLIENT_INFORMATION, bundle);
                    EquipmentListCLientDetail.this.setResult(RESULT_OK, intent);
                    finish();

                }

                return false;
            }
        });

        new FetchEquipmentAsyncTask().execute();

        equipmentListLv.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                Equipement equipment = equipements.get(position);
                siteId = equipment.getmEquipementSiteId();


                if (siteId != -1 && siteId>0) {
                    Site site = dataAccessobject.getSiteDetail(siteId);
                    siteName = site.getNmSite();
                }
                else
                {
                    siteName="";
                }

                Intent equipmentIntent = new Intent(
                        EquipmentListCLientDetail.this, EquipmentDetials.class);
                equipmentIntent.putExtra(KEYS.EquipmentDetail.ID_CLIENT,
                        clientId);
                equipmentIntent.putExtra(KEYS.EquipmentDetail.ID_SITE, siteId);
                equipmentIntent.putExtra(KEYS.EquipmentDetail.EQUIPMENTS_ID,
                        equipment.getIdEquipement());
                if (equipment.getRefCustomer().length()>0){
                    equipmentIntent.putExtra(KEYS.EquipmentDetail.EQUIPMENTS_NAME,
                            equipment.getNmEquipement()+" ("+equipment.getRefCustomer()+")");
                }else {
                    equipmentIntent.putExtra(KEYS.EquipmentDetail.EQUIPMENTS_NAME,
                            equipment.getNmEquipement());
                }
                equipmentIntent.putExtra(KEYS.EquipmentDetail.CLIENT_NAME,
                        clientName);
                equipmentIntent.putExtra(KEYS.EquipmentDetail.SITE_NAME,
                        siteName);



                startActivity(equipmentIntent);

            }
        });
    }

    @Override
    protected void onDestroy() {
        DialogUtils.dismissProgressDialog();
        super.onDestroy();
    }

    public boolean isLGDevice() {
        return (Build.MANUFACTURER.contains("LG") || Build.MODEL.contains("LG"));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();

                return true;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Creates the and fill data to list view.
     */
    private void createAndFillDataToListView() {
        // TODO Auto-generated method stub

        if (equipmentsListAdapter == null) {
            equipmentsListAdapter = new EquipmentsListAdapter(this,
                    equipements, dataAccessobject);
            equipmentsListAdapter.setIndexPosition(index);
            equipmentListLv.setAdapter(equipmentsListAdapter);
        } else {
            equipmentsListAdapter.notifyDataSetChanged();
        }

        filter = equipmentsListAdapter.getFilter();

        equipmentListLv.setOnScrollListener(new OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // TODO Auto-generated method stub
                int ctadapter = equipmentsListAdapter.getCount();

                if (footerView != null && footerView.isShown()) {

                    index++;
                    equipmentsListAdapter.setIndexPosition(index);

                    try {
                        Thread.sleep(900);
                    } catch (InterruptedException e) {
                        Logger.printException(e);
                    }
                    equipmentsListAdapter.notifyDataSetChanged();
                }
                if (ctadapter >= equipmentCount) {
                    if (footerView != null) {
                        equipmentListLv.removeFooterView(footerView);
                        equipmentsListAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                // TODO Auto-generated method stub

            }
        });

    }

    /**
     * The on item click listener.
     */
    OnItemClickListener onItemClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                long arg3) {
            Equipement clt = (Equipement) equipmentsListAdapter.getItem(arg2);
            // int idClient = clt.getIdClient();

            // // EditText et = (EditText) findViewById(R.id.addressEt);
            // if (clt.getAdresseGlobalClient().equals(""))
            // et.setText(" ");
            // else
            // et.setText(clt.getAdresseGlobalClient());
            //
            // // et = (EditText) findViewById(R.id.additionalAddressEt);
            //
            // if (.equals(""))
            // et.setText(" ");
            // else
            // et.setText(clt.getAdresseComplClient());
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            // bundle.putSerializable(KEYS.NewJob.CLIENT_DETAILS, clt);
            bundle.putInt(KEYS.NewJob.EQUIPMENTS_ID, clt.getIdEquipement());
            bundle.putString(KEYS.NewJob.EQUIPMENTS_NAME, clt.getNmEquipement());
            bundle.putInt(KEYS.NewJob.SITE_ID, clt.getmEquipementSiteId());
            intent.putExtras(bundle);
            // intent.putExtra(KEYS.NewJob.CLIENT_INFORMATION, bundle);
            EquipmentListCLientDetail.this.setResult(RESULT_OK, intent);
            finish();

            // if (gt.getFlPageSites() == 1)
            // initAutoCompleteSites();
            // initAutoCompleteEquipements();
        }

    };
    /**
     * The text watcher.which watches text changes on edit text and perform
     * equipment list filtering
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
            if (!TextUtils.isEmpty(s.toString())) {
                if (footerView != null
                        && (footerView.getVisibility() == View.VISIBLE))

                    footerView.setVisibility(View.GONE);

            } else {
                if (footerView != null
                        && (footerView.getVisibility() == View.GONE))
                    footerView.setVisibility(View.VISIBLE);
            }

            if (filter != null)
                filter.filter(s.toString());
        }
    };

    /**
     * The Class FetchEquipmentAsyncTask.
     */
    private class FetchEquipmentAsyncTask extends
            AsyncTaskCoroutine<Void, Boolean> {

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#onPreExecute()
         */
        @Override
        public void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            DialogUtils.showProgressDialog(EquipmentListCLientDetail.this,
                    EquipmentListCLientDetail.this
                            .getString(R.string.textWaitLable),
                    EquipmentListCLientDetail.this
                            .getString(R.string.textClientFetchDialog), false);
        }

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#doInBackground(Params[])
         */
        @Override
        public Boolean doInBackground(Void... params) {
            // TODO Auto-generated method stub
            if (equipements != null) {

                equipements.clear();

            } else {

                equipements = new ArrayList<Equipement>();

            }
            try {
                equipements.addAll(dataAccessobject.getEquipementsForSite(
                        clientId, index, siteId, equipmentCount));
                return true;
            } catch (Exception e) {
                // TODO Auto-generated catch block
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

                    createAndFillDataToListView();

                } else {
                    DialogUtils
                            .showInfoDialog(
                                    EquipmentListCLientDetail.this,
                                    EquipmentListCLientDetail.this
                                            .getString(R.string.textAlertLable) + "!",
                                    EquipmentListCLientDetail.this
                                            .getString(R.string.textEquipmentNotFetchedDialog));
                }
            } catch (Exception e) {

                DialogUtils
                        .showInfoDialog(
                                EquipmentListCLientDetail.this,
                                EquipmentListCLientDetail.this
                                        .getString(R.string.textAlertLable) + "!",
                                EquipmentListCLientDetail.this
                                        .getString(R.string.textEquipmentNotFetchedDialog));
            } finally {

                DialogUtils.dismissProgressDialog();
            }

        }

    }

}
