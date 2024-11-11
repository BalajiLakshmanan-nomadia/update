package com.synchroteam.fragmenthelper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.synchroteam.TypefaceLibrary.EditText;
import com.synchroteam.beans.EnableSynchronizationAddJobEvent;
import com.synchroteam.beans.InventoryItemBeans;
import com.synchroteam.catalouge.CatalougeSubCategotyUpdated;
import com.synchroteam.dao.Dao;
import com.synchroteam.fragment.BaseFragment;
import com.synchroteam.fragment.InventoryFragment;
import com.synchroteam.listadapters.InventoryListAdapterUpdated;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.scanner.CodeScannerActivity;
import com.synchroteam.utils.AsyncTaskCoroutine;
import com.synchroteam.utils.DaoManager;
import com.synchroteam.utils.KEYS;
import com.synchroteam.utils.RequestCode;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import de.greenrobot.event.EventBus;

//import static com.google.android.gms.internal.zzagr.runOnUiThread;

public class InventoryFragmentHelperNew implements HelperInterface {

    private View view;
    private EditText edtInventory;
    private ListView listViewInventory;
    private TextView txtSearchIcon, txtBarcodeIcon, txtFilterIcon;
    private com.synchroteam.TypefaceLibrary.TextView txtFilter;
    private com.synchroteam.TypefaceLibrary.TextView txtFilterType;
    private TextView txtCloseFilter;
    private RelativeLayout relFilterValue;
    private View footerView;
    private Activity synchroteamActivity;
    private InventoryFragment inventoryFragment;
    private InventoryListAdapterUpdated adapter;
    private Dao dao;

    private ArrayList<InventoryItemBeans> inventoryList;

    private ProgressBar progressBarInventory, progressBarSearch;
    private BaseFragment baseFragment;

    private int index;

    private int listOffset = 1;
    private int inventoryListCount;
    private boolean isUserScrolled = false;
    private Timer timerSearch;
    private int mCount = 50;
    // private static final String TAG = "InventoryFragmentHelper";

    private Dao.InventoryFilter filter;

    public InventoryFragmentHelperNew(Activity synchroteamActivity,
                                      InventoryFragment inventoryFragment, BaseFragment baseFragment) {
        this.synchroteamActivity = synchroteamActivity;
        this.inventoryFragment = inventoryFragment;
        this.baseFragment = baseFragment;
    }

    @Override
    public View inflateLayout(LayoutInflater inflater, ViewGroup container) {
        view = inflater.inflate(R.layout.fragment_inventory, container, false);
        initiateView(view);
        return view;

    }

    public void doOnResume() {

        // getIQList();

    }

    @Override
    public void initiateView(View v) {
        //Init DAO and filter
        dao = DaoManager.getInstance();
        filter = dao.new InventoryFilter();

        edtInventory = (EditText) v.findViewById(R.id.edtInventory);
        listViewInventory = (ListView) v.findViewById(R.id.listInventory);
        // spinnerSort = (Spinner) v.findViewById(R.id.spinnerSort);
        txtBarcodeIcon = (TextView) v.findViewById(R.id.txtBarcodeIcon);
        txtSearchIcon = (TextView) v.findViewById(R.id.txtSearchIcon);
        txtFilterIcon = (TextView) v.findViewById(R.id.txtFilterIcon);
        txtFilter = (com.synchroteam.TypefaceLibrary.TextView) v
                .findViewById(R.id.txt_filter);
        txtFilterType = (com.synchroteam.TypefaceLibrary.TextView) v
                .findViewById(R.id.txt_filter_type);

        txtCloseFilter = (TextView) v.findViewById(R.id.txt_close_filter);
        relFilterValue = (RelativeLayout) v.findViewById(R.id.rel_filter);

        footerView = ((LayoutInflater) synchroteamActivity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
                R.layout.layout_footerview_items_list, null, false);

        progressBarInventory = (ProgressBar) view
                .findViewById(R.id.progressBarInventory);
        progressBarSearch = (ProgressBar) view.findViewById(R.id.progressBarSearch);

        txtBarcodeIcon.setOnClickListener(clickListener);
        txtFilterIcon.setOnClickListener(clickListener);
        txtCloseFilter.setOnClickListener(clickListener);

        setTypeFace();

        index = 1;

        fetchDataFromDataBase();

        edtInventory.addTextChangedListener(textWatcher);
        edtInventory.setNextFocusDownId(edtInventory.getId()); //keep focus when enter key pressed

    }

    /**
     * Fetches value from db
     */
    private void fetchDataFromDataBase() {
        new FetchPartsAndServicesList().execute();
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
     * Gets the parts and services list.
     *
     * @return
     */
    private ArrayList<InventoryItemBeans> getPartsAndServicesList() {

        return dao.getInventoryList();
    }

    /**
     * Updates the parts services list.
     */
    private void scrollInventoryList(final boolean showProgress) {
        if (showProgress) {
            progressBarInventory.setVisibility(View.VISIBLE);
        } else {
            showFooterView();
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {

                    listOffset = listOffset + mCount;
                    filter.offset += mCount;

                    final ArrayList<InventoryItemBeans> list = dao._inventoryList(filter);

                    synchroteamActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            updateAdapter(list);

                            if (showProgress) {
                                progressBarInventory.setVisibility(View.GONE);
                            } else {
                                hideFooterView();
                            }

                        }
                    });

                } catch (Exception e) {
                    Toast.makeText(synchroteamActivity, "updateInventoryList " + e.getMessage(), Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }


            }
        }, 300);//def 980

    }

    private void setInventoryListAdapter() {
        if (adapter == null && inventoryList.size() > 0) {

            adapter = new InventoryListAdapterUpdated(synchroteamActivity,
                    android.R.id.text1, inventoryList, dao, null,
                    false, false, this);
            listViewInventory.setAdapter(adapter);

        } else {
            if (adapter != null)
                adapter.notifyDataSetChanged();
        }

        if(inventoryList!=null && inventoryList.size()<=0){
            hideFooterView();
        }

        listViewInventory.setOnScrollListener(mOnScrollListener);
        //baseFragment.listUpdate(); //This updates the main menu it seems???


    }


    private void setTypeFace() {
        Typeface typeFace = Typeface.createFromAsset(synchroteamActivity.getAssets(), synchroteamActivity.getString(R.string.fontName_fontAwesome));
        txtFilterIcon.setTypeface(typeFace);
        txtSearchIcon.setTypeface(typeFace);
        txtBarcodeIcon.setTypeface(typeFace);
        txtCloseFilter.setTypeface(typeFace);
    }

    @Override
    public void doOnSyncronize() {
        fetchDataFromDataBase();
    }

    @Override
    public void onReturnToActivity(int requestCode) {

    }

    /**
     * calls after returning to fragment
     *
     * @param requestCode
     * @param data
     */
    public void onReturnToFragment(int requestCode, Intent data) {

        if (requestCode == RequestCode.REQUEST_CODE_TEXT_BARCODE) {
            edtInventory.setText(data.getStringExtra("SCAN_RESULT_CODE"));
            edtInventory.setSelection(edtInventory.getText().toString()
                    .length());
        } else if (requestCode == RequestCode.REQUEST_CODE_OPEN_ITEM) {
            String filteredString = data.getStringExtra(KEYS.Catalouge.NOM_CAT);

            String filterText = filteredString.replace("|", " > ");
            relFilterValue.setVisibility(View.VISIBLE);
            txtFilter.setText(filterText);

            filter.inStock = data.getBooleanExtra(KEYS.Catalouge.IS_STOCK, false);
            filter.isRequested = data.getBooleanExtra(KEYS.Catalouge.IS_REQUEST, false);

            if (adapter != null)
                adapter.isCategoryFiltered = true;

            filter.resetCategory(filteredString);

            String filterType = "";

            if (!TextUtils.isEmpty(filter.category) && (filter.inStock || filter.isRequested)) {
                filterType = " - ";
            }
            if (filter.inStock || filter.isRequested) {
                if (filter.inStock && filter.isRequested) {
                    filterType += synchroteamActivity.getString(R.string.txt_stock);
                    filterType += " & ";
                    filterType += synchroteamActivity.getString(R.string.txt_request);
                } else if (filter.inStock) {
                    filterType += synchroteamActivity.getString(R.string.txt_stock);
                } else if (filter.isRequested) {
                    filterType += synchroteamActivity.getString(R.string.txt_request);
                }


            }
            txtFilterType.setText(filterType);

            fetchDataFromDataBase();

        }

    }

    /**
     * Watcher class for search edit text.
     */
    private TextWatcher textWatcher = new TextWatcher() {

        private final long DELAY = 600;//def600

        @Override public void onTextChanged(final CharSequence s, int start, int before, int count) {

            if (timerSearch != null) {
                timerSearch.cancel();
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }


        @Override
        public void afterTextChanged(final Editable s) {

            filter.searchText = s.toString().trim();
            timerSearch = new Timer();
            timerSearch.schedule(new TimerTask() {
                @Override
                public void run() {
                    performSearch();
                }
            }, DELAY);

        }

    };

    /**
     * Filter from DB
     */
    private void performSearch() {
        ArrayList<InventoryItemBeans> list = new ArrayList<>();
        synchroteamActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBarSearch.setVisibility(View.VISIBLE);
            }
        });

        inventoryList.clear();
        filter.offset = 1;
        /*if (TextUtils.isEmpty(filteredCategory)) {
            filteredCategory = "";
            filter.category = "";
        }*/

        try {

            inventoryListCount = dao._inventoryListCount(filter);
            list.addAll(dao._inventoryList(filter));

        } catch (Exception e) {

            Toast.makeText(synchroteamActivity, "performSearch " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();

        }

        updateAdapter(list);

    }


    public void updateAdapter(final ArrayList<InventoryItemBeans> list) {
        synchroteamActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (list != null) {
                    progressBarSearch.setVisibility(View.GONE);

                    inventoryList.addAll(list);

                    setInventoryListAdapter();

                    /*if (adapter != null)
                        adapter.notifyDataSetChanged(); //adapter null when searching + filter by category
                     */

                    hideFooterView();

                }
            }
        });

    }


    /**
     * Scroll listener for list view.
     */
    private AbsListView.OnScrollListener mOnScrollListener = new AbsListView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isUserScrolled = true;
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            if (adapter != null) {
                int ctadapter = adapter.getCount();
                if (isUserScrolled && firstVisibleItem + visibleItemCount == totalItemCount) {
                    isUserScrolled = false;

                    if (ctadapter >= inventoryListCount) {
                        hideFooterView();
                    } else {
                        if (totalItemCount > 0) {
                            scrollInventoryList(false);
                            showFooterView();
                        }
                    }

                }
            }
        }
    };

    // ........................................LISTENER...STARTS.............................................

    OnClickListener clickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.txtBarcodeIcon:
                    Intent intent = new Intent(synchroteamActivity,
                            CodeScannerActivity.class);
                    inventoryFragment.startActivityForResult(intent,
                            RequestCode.REQUEST_CODE_TEXT_BARCODE);
                    break;

                case R.id.txtFilterIcon:
                    Intent i = new Intent(synchroteamActivity,
                            CatalougeSubCategotyUpdated.class);

                    // i.putExtra(KEYS.Catalouge.ID_INTER, null);
                    i.putExtra(KEYS.Catalouge.IS_INVENTORY, "true");
                    i.putExtra(KEYS.Catalouge.NOM_CAT, "");
                    i.putExtra(KEYS.Catalouge.INDEX, 0);
                    inventoryFragment.startActivityForResult(i,
                            RequestCode.REQUEST_CODE_OPEN_ITEM);
                    break;
                case R.id.txt_close_filter:
                    relFilterValue.setVisibility(View.GONE);

                    filter.resetCategory("");
                    filter.inStock = false;
                    filter.isRequested = false;

                    fetchDataFromDataBase();

                    break;

            }
        }
    };

    public void refreshList() {
        new FetchPartsAndServicesList().execute();
    }


    public void updateListPartInventory(Bundle data) {

        String filteredString = data.getString(KEYS.Catalouge.NOM_CAT);
        String filterText = filteredString.replace("|", " > ");
        relFilterValue.setVisibility(View.VISIBLE);
        txtFilter.setText(filterText);

        filter.inStock = data.getBoolean(KEYS.Catalouge.IS_STOCK, false);
        filter.isRequested = data.getBoolean(KEYS.Catalouge.IS_REQUEST, false);

        if (adapter != null)
            adapter.isCategoryFiltered = true;

        filter.resetCategory(filteredString);

        String filterType = "";

        if (!TextUtils.isEmpty(filter.category) && (filter.inStock || filter.isRequested)) {
            filterType = " - ";
        }
        if (filter.inStock || filter.isRequested) {
            if (filter.inStock && filter.isRequested) {
                filterType += synchroteamActivity.getString(R.string.txt_stock);
                filterType += " & ";
                filterType += synchroteamActivity.getString(R.string.txt_request);
            } else if (filter.inStock) {
                filterType += synchroteamActivity.getString(R.string.txt_stock);
            } else if (filter.isRequested) {
                filterType += synchroteamActivity.getString(R.string.txt_request);
            }

        }
        txtFilterType.setText(filterType);

        new FetchPartsAndServicesList().execute();


    }

    // ****************************************LISTENER...ENDS***********************************************

    /**
     * Fetch the list of parts and services.
     */
    private class FetchPartsAndServicesList extends
            AsyncTaskCoroutine<Void, ArrayList<InventoryItemBeans>> {

        public ArrayList<InventoryItemBeans> list = new ArrayList<>();

        public FetchPartsAndServicesList() {
            listOffset = 1;
            filter.offset = 1;
            /*if (adapter != null)
                adapter = null;*/
            if (inventoryList != null) {
                inventoryList.clear();
            } else {
                inventoryList = new ArrayList<>();
            }

        }

        @Override
        public void onPreExecute() {
            super.onPreExecute();
            progressBarInventory.setVisibility(View.VISIBLE);
            listViewInventory.setVisibility(View.INVISIBLE);
        }

        @Override
        public ArrayList<InventoryItemBeans> doInBackground(Void... params) {
//            inventoryList = new ArrayList<>();
            inventoryListCount = 0;

            inventoryListCount = dao._inventoryListCount(filter);
            list.addAll(dao._inventoryList(filter));

            return list;
        }

        @Override
        public void onPostExecute(ArrayList<InventoryItemBeans> result) {
            super.onPostExecute(result);


            listViewInventory.removeFooterView(footerView);
            progressBarInventory.setVisibility(View.GONE);
            listViewInventory.setVisibility(View.VISIBLE);

            if (inventoryListCount > mCount) {
                listViewInventory.addFooterView(footerView);
            }

            if (result != null) {
                inventoryList.addAll(result);
            }
            setInventoryListAdapter();

            if (adapter != null) {
                adapter.isCategoryFiltered = false;
            }
            EventBus.getDefault().post(new EnableSynchronizationAddJobEvent());
        }

    }

}
