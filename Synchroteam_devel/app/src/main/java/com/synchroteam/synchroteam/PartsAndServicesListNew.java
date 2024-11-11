package com.synchroteam.synchroteam;

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
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.synchroteam.TypefaceLibrary.EditText;
import com.synchroteam.beans.InventoryItemBeans;
import com.synchroteam.catalouge.CatalougeSubCategotyUpdated;
import com.synchroteam.dao.Dao;
import com.synchroteam.events.InventoryFragmentCatEvent;
import com.synchroteam.listadapters.InventoryListAdapterUpdated;
import com.synchroteam.scanner.CodeScannerActivity;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.utils.AsyncTaskCoroutine;
import com.synchroteam.utils.DaoManager;
import com.synchroteam.utils.DateChecker;
import com.synchroteam.utils.KEYS;
import com.synchroteam.utils.Logger;
import com.synchroteam.utils.RequestCode;
import com.synchroteam.utils.SharedPref;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import de.greenrobot.event.EventBus;

import static com.synchroteam.synchroteam3.R.id.txtBarcodeIcon;
import static com.synchroteam.synchroteam3.R.id.txtFilterIcon;

/**
 * This activity holds the UI for parts & services list used in Parts & service
 * and Invoice class.
 *
 * @author Trident
 */

// TODO Need to restrict the filter edit text scrolling.

public class PartsAndServicesListNew extends AppCompatActivity {

    // -------------INSTANCE...VARIABLES-------------------
    private EditText edtSearchParts;

    private ListView listPartsRv;
    /**
     * The progress d synch.
     */

    private ProgressBar progressBarInventory, progressBarSearch;
    private ActionBar actionBar;
    private com.synchroteam.TypefaceLibrary.TextView txtFilter;
    private com.synchroteam.TypefaceLibrary.TextView txtFilterType;
    private TextView txtCloseFilter;
    private RelativeLayout relFilterValue;
    private Typeface typeFace;
    private Dao dao;
    private ArrayList<InventoryItemBeans> partsList;
    private InventoryListAdapterUpdated adapterPartsServices;

    private boolean isInvoice;
    private boolean isPartsAndServices;
    private String id_interv;

    private View footerView;
    private int index;


    private String filteredCategory = "";
    private boolean isStock;

    private boolean isFirstTime = true;

    private static final String TAG = PartsAndServicesListNew.class
            .getSimpleName();


    private boolean isUserScrolled = false;

    private Dao.PartsFilter partsFilter;
    private int partsOffset = 1;
    private int partsListCount;
    private int mCount = 20;

    FetchPartsAndServicesSearchList asyncTask;

    // -------------INSTANCE...VARIABLES-------------------

    // ----------------------------------------------LIFECYCLE...METHODS...STARTS-----------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parts_services);

        initializeUi();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        //New changes
        DateChecker.checkDateAndNavigate(this, dao);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    // ----------------------------------------------LIFECYCLE...METHODS...ENDS-------------------------------------------------

    // ----------------------------------------------OVERRIDDEN...METHODS...STARTS----------------------------------------------

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RequestCode.REQUEST_CODE_TEXT_BARCODE) {
            if (resultCode == Activity.RESULT_OK) {
                edtSearchParts.setText(data.getStringExtra("SCAN_RESULT_CODE"));
                edtSearchParts.setSelection(edtSearchParts.getText().toString()
                        .length());
            }
        } else if (requestCode == RequestCode.REQUEST_CODE_OPEN_ITEM) {
            if (resultCode == RESULT_OK) {
                String categoryName = data
                        .getStringExtra(KEYS.Catalouge.NOM_CAT);
                boolean isStockSel = data.getBooleanExtra(KEYS.Catalouge.IS_STOCK, false);
                filterByCategory(categoryName, isStockSel);
                SharedPref.setFilteredCategoryPartsServices(categoryName, this);
                SharedPref.setIsStockSelected(isStockSel, this);
            }
        }

    }

    // ----------------------------------------------OVERRIDDEN...METHODS...ENDS------------------------------------------------

    // ----------------------------------------------LOCAL...METHODS...STARTS---------------------------------------------------

    /**
     * Initialize the UI elements.
     */
    private void initializeUi() {


        dao = DaoManager.getInstance();
        partsFilter = dao.new PartsFilter();

        setActionBar();

        TextView txtSearchIcon = (TextView) findViewById(R.id.txtSearchIcon);
        TextView txtBarcodeIcon = (TextView) findViewById(R.id.txtBarcodeIcon);
        TextView txtFilterIcon = (TextView) findViewById(R.id.txtFilterIcon);
        edtSearchParts = (EditText) findViewById(R.id.edtInventory);

        listPartsRv = (ListView) findViewById(R.id.listInventory);
        txtFilter = (com.synchroteam.TypefaceLibrary.TextView) findViewById(R.id.txt_filter);
        txtFilterType = (com.synchroteam.TypefaceLibrary.TextView)
                findViewById(R.id.txt_filter_type);
        txtCloseFilter = (TextView) findViewById(R.id.txt_close_filter);
        relFilterValue = (RelativeLayout) findViewById(R.id.rel_filter);
        progressBarInventory = (ProgressBar) findViewById(R.id.progressBarInventory);
        progressBarSearch = (ProgressBar) findViewById(R.id.progressBarSearch);

        footerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.layout_footerview_items_list, null, false);


        index = 1;

        // set typeface to search icon
        Typeface typeFace = Typeface.createFromAsset(getAssets(),
                getResources().getString(R.string.fontName_fontAwesome));
        txtSearchIcon.setTypeface(typeFace);
        txtBarcodeIcon.setTypeface(typeFace);
        txtFilterIcon.setTypeface(typeFace);

        getDataFromIntents();

        setTypeFace();

        filteredCategory = SharedPref.getFilteredCategoryPartsServices(this);
        isStock = SharedPref.getIsStockSelected(this);

        fetchDataFromDataBase();

        txtBarcodeIcon.setOnClickListener(clickListner);
        txtFilterIcon.setOnClickListener(clickListner);
        txtCloseFilter.setOnClickListener(clickListner);

        edtSearchParts.addTextChangedListener(textWatcher);
        edtSearchParts.setNextFocusDownId(edtSearchParts.getId()); //keep focus when enter key pressed

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
     *
     */
    public void onEvent(InventoryFragmentCatEvent updateDataBaseEvent) {

//        updateListPartInventory(updateDataBaseEvent.data);
    }

    /**
     * Updates the parts services list.
     */
    private void updatePartsItemsList(final boolean showProgress) {


        if (showProgress) {
            progressBarInventory.setVisibility(View.VISIBLE);
        } else {
            showFooterView();
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {

                    partsOffset = partsOffset + mCount;
                    partsFilter.offset += mCount;

                    final ArrayList<InventoryItemBeans> list = dao._partsList(partsFilter);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            updateAdapter(list, false);

                            if (showProgress) {
                                progressBarInventory.setVisibility(View.GONE);
                            } else {
                                hideFooterView();
                            }

                        }
                    });

                } catch (Exception e) {
                    Logger.printException(e);
                }


            }
        }, 300);//def 980

    }

    /**
     * sets typeface for textview.
     */
    private void setTypeFace() {
        typeFace = Typeface.createFromAsset(getAssets(),
                getString(R.string.fontName_fontAwesome));
        txtCloseFilter.setTypeface(typeFace);
    }


    /**
     * Gets values from intents.
     */
    private void getDataFromIntents() {
        id_interv = getIntent().getExtras().getString(KEYS.Catalouge.ID_INTER);
        isInvoice = getIntent().getExtras().getBoolean(
                KEYS.Catalouge.IS_INVOICE);
        isPartsAndServices = getIntent().getExtras().getBoolean(KEYS.Catalouge.IS_PARTS_AND_SERVICES);
    }

    /**
     * Initiate action bar.
     */
    private void setActionBar() {
        actionBar = getSupportActionBar();
        String mTitle = getResources().getString(R.string.txt_parts_service_label);
        mTitle = mTitle.replace("/", "&");
        actionBar.setTitle(mTitle);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
    }

    /**
     * Sets the adapter for parts & services list.
     */
    private void setListAdapter() {

        if (adapterPartsServices == null) {
            adapterPartsServices = new InventoryListAdapterUpdated(this,
                    android.R.id.text1, partsList, dao, id_interv,
                    isInvoice, isPartsAndServices, null);
            adapterPartsServices.setIndexPosition(1);
            listPartsRv.setAdapter(adapterPartsServices);

        } else {
            adapterPartsServices.notifyDataSetChanged();
        }

        if (isFirstTime) {
            if ((filteredCategory != null && filteredCategory.trim().length() > 0) || isStock) {
                filterInitially(filteredCategory, isStock);
            }
            isFirstTime = false;

        }

        listPartsRv.setOnScrollListener(mOnScrollListener);

    }

    private void filterInitially(String category, boolean isStockSel) {
        if (adapterPartsServices != null) {
            index = 1;

            adapterPartsServices.setIndexPosition(index);

            String filterText = category.replace("|", " > ");

            if (!TextUtils.isEmpty(category) || isStockSel) {
                relFilterValue.setVisibility(View.VISIBLE);
            }

            txtFilter.setText(filterText);
            isStock = isStockSel;


            //new changes parts
            partsFilter.inStock = isStockSel;
            partsFilter.resetCategory(category);


            String filterType = "";

            if (!TextUtils.isEmpty(filteredCategory) && isStock) {
                filterType = " - ";
            }

            if (isStock) {
                filterType += getString(R.string.txt_stock);
            }

            txtFilterType.setText(filterType);

        }
    }


    public void closeList() {
        setResult(Activity.RESULT_OK);
        finish();
    }


    /**
     * Filtered the list by selected category.
     *
     * @param category
     */
    private void filterByCategory(String category, boolean isStockSel) {

        index = 1;
        String filterText = category.replace("|", " > ");
        relFilterValue.setVisibility(View.VISIBLE);
        txtFilter.setText(filterText);

        isStock = isStockSel;

//        edtSearchParts.setText("");

        filteredCategory = category;

        String filterType = "";

        if (!TextUtils.isEmpty(filteredCategory) && isStock) {
            filterType = " - ";
        }

        if (isStock) {
            filterType += getString(R.string.txt_stock);
        }

        //new changes parts
        partsFilter.inStock = isStockSel;
        partsFilter.resetCategory(category);

        fetchDataFromDataBase();

        txtFilterType.setText(filterType);
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

            if (adapterPartsServices != null) {
                int ctadapter = adapterPartsServices.getCount();
                if (isUserScrolled && firstVisibleItem + visibleItemCount == totalItemCount) {
                    isUserScrolled = false;

                    if (ctadapter >= partsListCount) {
                        hideFooterView();
                    } else {
                        if (totalItemCount > 0) {
                            updatePartsItemsList(false);
                            showFooterView();
                        }
                    }

                }
            }

        }
    };

    // ----------------------------------------------LOCAL...METHODS...ENDS-----------------------------------------------------

    // --------------------------------------------------LISTENER...STARTS------------------------------------------------------

    /**
     * Click listener class.
     */
    OnClickListener clickListner = new OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case txtBarcodeIcon:
                    Intent barcodeIntent = new Intent(PartsAndServicesListNew.this,
                            CodeScannerActivity.class);
                    startActivityForResult(barcodeIntent,
                            RequestCode.REQUEST_CODE_TEXT_BARCODE);
                    break;

                case txtFilterIcon:
                    Intent i = new Intent(PartsAndServicesListNew.this,
                            CatalougeSubCategotyUpdated.class);

                    i.putExtra(KEYS.Catalouge.ID_INTER, id_interv);
                    i.putExtra(KEYS.Catalouge.IS_INVENTORY, "false");
                    i.putExtra(KEYS.Catalouge.NOM_CAT, "");
                    i.putExtra(KEYS.Catalouge.INDEX, 0);
                    startActivityForResult(i, RequestCode.REQUEST_CODE_OPEN_ITEM);
                    break;

                case R.id.txt_close_filter:
                    relFilterValue.setVisibility(View.GONE);
                    index = 1;
                    if (adapterPartsServices != null)
                        adapterPartsServices.setIndexPosition(index);

//                    edtSearchParts.setText("");
                    SharedPref.setFilteredCategoryPartsServices("",
                            PartsAndServicesListNew.this);
                    SharedPref.setIsStockSelected(false,
                            PartsAndServicesListNew.this);

                    filteredCategory = "";
                    isStock = false;


                    //new changes parts
                    partsFilter.resetCategory("");
                    partsFilter.inStock = false;
                    fetchDataFromDataBase();

                    break;
            }
        }
    };

    /**
     * Watcher class for search edit text.
     */
    TextWatcher textWatcher = new TextWatcher() {
        private Timer timer = new Timer();
        private final long DELAY = 600;//def 600

        @Override
        public void onTextChanged(final CharSequence s, int start, int before,
                                  int count) {

            if (timer != null) {
                timer.cancel();
            }

        }

        @Override
        public void beforeTextChanged(final CharSequence s, int start, int before, int count) {

            if (s.length() == 0 || count == 0) {
                return;
            }
        }

        @Override
        public void afterTextChanged(final Editable s) {

            try {
                partsList.clear();
                partsFilter.searchText = s.toString().trim();
//                partsFilter.searchText="1011456";
                timer = new Timer();

                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
//                        performSearch();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //create new async task
                                asyncTask = new FetchPartsAndServicesSearchList();
                                asyncTask.execute();

                            }
                        });

                    }
                }, DELAY);


            } catch (Exception e) {

                e.printStackTrace();
            }

        }
    };

    /**
     * Filter from DB
     */
    private void performSearch() {
        ArrayList<InventoryItemBeans> list = new ArrayList<>();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBarSearch.setVisibility(View.VISIBLE);
            }
        });

//
        partsFilter.offset = 1;

        try {

            partsListCount = dao._partsListCount(partsFilter);
            list.addAll(dao._partsList(partsFilter));

        } catch (Exception e) {
            Logger.printException(e);
        }

        updateAdapter(list, true);


    }

    public void updateAdapter(final ArrayList<InventoryItemBeans> list, boolean clearList) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (list != null) {

                    if (clearList)
                        partsList.clear();

                    progressBarSearch.setVisibility(View.GONE);
                    partsList.addAll(list);

                    if (adapterPartsServices != null)
                        adapterPartsServices.notifyDataSetChanged();

                    listPartsRv.removeFooterView(footerView);
                    if (partsListCount > 20) {
                        listPartsRv.addFooterView(footerView);
                    }

                }
            }
        });
    }

    // --------------------------------------------------LISTENER...ENDS--------------------------------------------------------

    // ---------------------------------------------------ASYNC...CLASS...STARTS-------------------------------------------------

    /**
     * Fetch the list of parts and services.
     */
    private class FetchPartsAndServicesList extends
            AsyncTaskCoroutine<Void, ArrayList<InventoryItemBeans>> {

        public ArrayList<InventoryItemBeans> list = new ArrayList<>();

        public FetchPartsAndServicesList() {

            partsOffset = 1;

            if (partsList != null) {
                partsList.clear();
            } else {
                partsList = new ArrayList<>();
            }

        }

        @Override
        public void onPreExecute() {
            super.onPreExecute();

            progressBarInventory.setVisibility(View.VISIBLE);
            listPartsRv.setVisibility(View.INVISIBLE);
        }

        @Override
        public ArrayList<InventoryItemBeans> doInBackground(Void... params) {

            list = new ArrayList<>();

            partsListCount = 0;
            partsFilter.offset = 1;
            partsFilter.inStock = isStock;

            if (filteredCategory != null && filteredCategory.length() > 0) {
                partsFilter.filterCategory = filteredCategory;
//                partsFilter.searchText="";
            }

            partsListCount = dao._partsListCount(partsFilter);
            list.addAll(dao._partsList(partsFilter));

            Logger.log(TAG, "Parts List check count is ===>" + partsListCount);
            Logger.log(TAG, "Parts List check partsList size async is ===>" + list.size());

            return list;
        }

        @Override
        public void onPostExecute(ArrayList<InventoryItemBeans> result) {
            super.onPostExecute(result);

            listPartsRv.removeFooterView(footerView);
            progressBarInventory.setVisibility(View.GONE);
            listPartsRv.setVisibility(View.VISIBLE);
            if (partsListCount > 20) {
                listPartsRv.addFooterView(footerView);
            }


            if (result != null) {
                partsList.addAll(result);
                setListAdapter();
            }

        }

    }


    /**
     * Fetch the list of parts and services.
     */
    private class FetchPartsAndServicesSearchList extends
            AsyncTaskCoroutine<Void, ArrayList<InventoryItemBeans>> {

        public ArrayList<InventoryItemBeans> list = new ArrayList<>();

        public FetchPartsAndServicesSearchList() {

            partsOffset = 1;
            partsFilter.offset = 1;

            partsList.clear();

            progressBarSearch.setVisibility(View.VISIBLE);

            Logger.log(TAG, "Before executing query");

        }

//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//
//            progressBarSearch.setVisibility(View.VISIBLE);
//        }

        @Override
        public ArrayList<InventoryItemBeans> doInBackground(Void... params) {

            ArrayList<InventoryItemBeans> list = new ArrayList<>();

            partsListCount = dao._partsListCount(partsFilter);
            list.addAll(dao._partsList(partsFilter));


            return list;
        }



        @Override
        public void onPostExecute(ArrayList<InventoryItemBeans> result) {
            super.onPostExecute(result);

            Logger.log(TAG, "After executing query");

            progressBarSearch.setVisibility(View.GONE);

            if (partsListCount > 20) {
                listPartsRv.addFooterView(footerView);
            }


            updateAdapter(result, true);

        }

    }

    // ---------------------------------------------------ASYNC...CLASS...STARTS-------------------------------------------------
}
