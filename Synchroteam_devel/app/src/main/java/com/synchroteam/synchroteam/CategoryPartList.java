package com.synchroteam.synchroteam;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.ListView;

import com.synchroteam.TypefaceLibrary.EditText;
import com.synchroteam.beans.PartCategoryNameBeans;
import com.synchroteam.dao.Dao;
import com.synchroteam.listadapters.PartCateListAdapter;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.utils.AsyncTaskCoroutine;
import com.synchroteam.utils.DaoManager;
import com.synchroteam.utils.DialogUtils;
import com.synchroteam.utils.KEYS;
import com.synchroteam.utils.Logger;

import java.util.ArrayList;

// TODO: Auto-generated Javadoc

/**
 * @author Trident
 */
public class CategoryPartList extends Activity {

    /**
     * The client list lv.
     */
    private ListView partCateLV;

    /**
     * The clients.
     */
    private ArrayList<PartCategoryNameBeans> partCateList;

    /**
     * The data accessobject.
     */
    private Dao dataAccessobject;

    /**
     * The client list adapter.
     */
    private PartCateListAdapter adapter;

    /**
     * The search view et.
     */
    private EditText searchViewEt;

    /**
     * The cancel tv.
     */
    private ImageView cancelTv;

    private ImageView clearTv;

    /**
     * The client count.
     */
    private int listcount;

    /**
     * The footer view.
     */
    private View footerView;

    /**
     * The index.
     */
    private int index = 1;

    /**
     * The location manager.
     */
    protected LocationManager locationManager;

    /**
     * The filter.
     */
    private Filter filter;

    private boolean isUserSearching = false;

    private boolean isPart = false;

    private int idCategoryPiece;

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_category_part);
        partCateLV = (ListView) findViewById(R.id.clientListLv);


        searchViewEt = (EditText) findViewById(R.id.searchViewEt);
        searchViewEt.addTextChangedListener(textWatcher);
        partCateLV.setOnItemClickListener(onItemClickListener);

        dataAccessobject = DaoManager.getInstance();
        cancelTv = (ImageView) findViewById(R.id.cancelTv);
        clearTv = (ImageView) findViewById(R.id.clearData);

        isPart = getIntent().getExtras().getBoolean(KEYS.Catalouge.IS_PARTS);
        idCategoryPiece = getIntent().getExtras().getInt(KEYS.PartCategoryName.KEY_ID_CATEGORY, -1);
        findViewById(R.id.clearData).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub

                searchViewEt.setText("");

                try {
                    InputMethodManager inputManager = (InputMethodManager) CategoryPartList.this
                            .getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (CategoryPartList.this.getWindow().getCurrentFocus() != null) {
                        inputManager.hideSoftInputFromWindow(
                                CategoryPartList.this.getWindow().getCurrentFocus()
                                        .getWindowToken(), 0);
                    }
                    clearTv.setVisibility(View.GONE);

                } catch (Exception e) {
                    Logger.printException(e);
                }

            }
        });

        if (isPart)
            listcount = dataAccessobject.getPartsCount(idCategoryPiece);
        else
            listcount = dataAccessobject.getCateCount();

        if (listcount > 20) {
            footerView = ((LayoutInflater) this
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                    .inflate(R.layout.layout_footerview_items_list, null, false);
            partCateLV.addFooterView(footerView);
        }
        cancelTv.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                setResult(RESULT_CANCELED);
                finish();

            }
        });

        new FetchPartsCateAsyncTask().execute();

    }

    @Override
    protected void onResume() {
        super.onResume();

    }


    /**
     * Creates the and fill data to list view.
     */
    private void createAndFillDataToListView() {
        // TODO Auto-generated method stub

        // Collections.sort(clients, new Comparator<Client>() {
        // public int compare(Client c1, Client c2) {
        // return c1.getNmClient().compareTo(c2.getNmClient());
        // }
        // });

        if (adapter == null) {
            adapter = new PartCateListAdapter(this, partCateList,
                    dataAccessobject, isPart, idCategoryPiece);
            adapter.setIndexPosition(index);

        } else {
            adapter.setIndexPosition(index);
        }
        partCateLV.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        filter = adapter.getFilter();

        partCateLV.setOnScrollListener(new OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // TODO Auto-generated method stub
                // int ctadapter = adapter.getCount();

                if (footerView != null && footerView.isShown()) {

                    index++;
                    adapter.setIndexPosition(index);

                    try {
                        Thread.sleep(900);
                    } catch (InterruptedException e) {
                        Logger.printException(e);
                    }
                    adapter.notifyDataSetChanged();
                }

                // if (!isUserSearching) {
                // if (ctadapter >= listcount) {
                // if (footerView != null)
                // clientListLv.removeFooterView(footerView);
                // }
                // } else {
                // if (ctadapter >= adapter.getArrayCount()) {
                // hideFooterView();
                // } else {
                // showFooterView();
                // }
                // }

            }

            @Override
            public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {
                int ctadapter = adapter.getCount();

                if (!isUserSearching) {
                    if (ctadapter >= listcount) {
                        if (footerView != null)
                            partCateLV.removeFooterView(footerView);
                    }
                } else {
                    if (ctadapter >= adapter.getArrayCount()) {
                        hideFooterView();
                    } else {
                        showFooterView();
                    }
                }
            }
        });

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

    /**
     * The on item click listener.
     */
    OnItemClickListener onItemClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                long arg3) {

            PartCategoryNameBeans adapterItem = (PartCategoryNameBeans) adapter.getItem(arg2);
            Intent intent = new Intent();
            Bundle bundle = new Bundle();

            if (isPart) {
                bundle.putString(KEYS.PartCategoryName.KEY_PART_NAME, adapterItem.getPartName());
                bundle.putString(KEYS.PartCategoryName.KEY_CD_PRODUIT, adapterItem.getCdProduit());
                bundle.putInt(KEYS.PartCategoryName.KEY_ID_CATEGORY, adapterItem.getIdCategory());
                bundle.putInt(KEYS.PartCategoryName.KEY_ID_PIECE, adapterItem.getIdPiece());
                bundle.putString(KEYS.PartCategoryName.KEY_CATEGORY_NAME, adapterItem.getNameCategory());
            } else {
                bundle.putString(KEYS.PartCategoryName.KEY_CATEGORY_NAME, adapterItem.getNameCategory());
                bundle.putInt(KEYS.PartCategoryName.KEY_ID_CATEGORY, adapterItem.getIdCategory());
            }

            intent.putExtras(bundle);

            CategoryPartList.this.setResult(RESULT_OK, intent);

            finish();

        }

    };


    /**
     * The text watcher. to watch test edit in edit text and filter client list
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

            if (!TextUtils.isEmpty(s.toString())) {
                isUserSearching = true;
                clearTv.setVisibility(View.VISIBLE);
            } else {
                isUserSearching = false;
                clearTv.setVisibility(View.GONE);
            }

            if (filter != null)
                filter.filter(s.toString());

        }
    };

    /**
     * The Class FetchClientsAsyncTask.
     */
    private class FetchPartsCateAsyncTask extends AsyncTaskCoroutine<Void, Boolean> {

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#onPreExecute()
         */
        @Override
        public void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            DialogUtils.showProgressDialog(CategoryPartList.this,
                    CategoryPartList.this.getString(R.string.textWaitLable),
                    CategoryPartList.this.getString(R.string.textClientFetchDialog),
                    false);
        }

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#doInBackground(Params[])
         */
        @Override
        public Boolean doInBackground(Void... params) {
            // TODO Auto-generated method stub
            if (partCateList != null) {

                partCateList.clear();

            } else {

                partCateList = new ArrayList<PartCategoryNameBeans>();

            }
            try {
                if (isPart)
                    partCateList.addAll(dataAccessobject.getPartNameList(idCategoryPiece));
                else
                    partCateList.addAll(dataAccessobject.getCategoryList(idCategoryPiece));

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
                                    CategoryPartList.this,
                                    CategoryPartList.this
                                            .getString(R.string.textAlertLable) + "!",
                                    CategoryPartList.this
                                            .getString(R.string.txt_no_parts) + "!");
                }
            } catch (Exception e) {

                DialogUtils.showInfoDialog(CategoryPartList.this, CategoryPartList.this
                        .getString(R.string.textAlertLable) + "!", CategoryPartList.this
                        .getString(R.string.textClientNotFetchedDialog) + "!");
            } finally {

                DialogUtils.dismissProgressDialog();
            }

        }

    }


    /**
     * Show settings alert.
     */
    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(getString(R.string.textAlertLable) + "!");
        alertDialog.setMessage(getString(R.string.textEnableLocationService));
        alertDialog.setPositiveButton(R.string.textYesLable,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    }
                });

        alertDialog.setNegativeButton(R.string.textNoLable,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        alertDialog.show();
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onDestroy()
     */
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        DialogUtils.dismissProgressDialog();
        try {
            InputMethodManager inputManager = (InputMethodManager) CategoryPartList.this
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            if (CategoryPartList.this.getWindow().getCurrentFocus() != null) {
                inputManager.hideSoftInputFromWindow(CategoryPartList.this
                        .getWindow().getCurrentFocus().getWindowToken(), 0);
            }

        } catch (Exception e) {
            Logger.printException(e);
        }

        super.onDestroy();
    }


}
