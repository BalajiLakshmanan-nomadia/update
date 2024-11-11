package com.synchroteam.catalouge;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.TypefaceSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Filter;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;

import com.synchroteam.TypefaceLibrary.TextView;
import com.synchroteam.beans.Categorie;
import com.synchroteam.beans.CategoryAndPartsInterface;
import com.synchroteam.dao.Dao;
import com.synchroteam.smoothcheckbox.SmoothCheckBox;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.utils.AsyncTaskCoroutine;
import com.synchroteam.utils.DaoManager;
import com.synchroteam.utils.DateChecker;
import com.synchroteam.utils.DialogUtils;
import com.synchroteam.utils.KEYS;
import com.synchroteam.utils.Logger;
import com.synchroteam.utils.MyFixedListView;
import com.synchroteam.utils.RequestCode;

import java.util.ArrayList;


// TODO: Auto-generated Javadoc

/**
 * This Activity shows the category of parts shown by CatalougePices Activty the
 * basic functionality of this activity is to manage Catadory and subcategoy of
 * parts And service module and also to fetch Category or SubCategory from
 * database and fill those parts to ListView. author Ideavate.solution .
 */

public class CatalougeSubCategotyUpdated extends AppCompatActivity implements
        SearchView.OnQueryTextListener, SearchView.OnCloseListener {

    /**
     * The action bar.
     */
    private ActionBar actionBar;

    /**
     * The item list view.
     */
    private MyFixedListView categoryListView;

    private TextView txtOkBtn;

    /**
     * The categories.
     */
    private ArrayList<CategoryAndPartsInterface> categoryList;

    /** The items. */

    /**
     * The data access object.
     */
    private Dao dataAccessObject;

    /**
     * The nbre cat.
     */
    private int nbreCat = 0;

    /** The item holders. */

    /**
     * The id intervention.
     */
    private String idIntervention;

    /**
     * The name cat.
     */
    private String nameCat = null;

    /**
     * The category adapter.
     */
    private CategoryItemsAdapter categoryAdapter;

    /**
     * The footer view.
     */
    private View footerView;

    /**
     * The index.
     */
    private int index;

    /**
     * The scroll index.
     */
    private int scrollIndex = 1;

    private String isInventory;

    /**
     * The filter.
     */
    private Filter filter;

    private boolean isUserSearching = false;

    private boolean isStockChecked;
    private boolean isRequestChecked;

    private static final String TAG = "CatalougeSubCategory";


    private boolean isUserScrolled = false;
    private int categoryOffset = 1;
    private int categoryListCount;
    private int mCount = 20;

    //changes for search
    FetchCategoryListValueWithSearch asyncTask;
    MenuItem searchItem;
    /**
     * The searched items list adapter.
     *
     * @param savedInstanceState the saved instance state
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout_items_list);
        actionBar = getSupportActionBar();

        String title = this.getResources().getString(R.string.itemsTopBarLable).toUpperCase();
        SpannableString titleSpannable = new SpannableString(title);
        titleSpannable.setSpan(
                new TypefaceSpan(this.getResources().getString(
                        R.string.fontName_hevelicaLight)), 0,
                titleSpannable.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        actionBar.setTitle(isLGDevice() ? title : titleSpannable);

        actionBar.setIcon(android.R.color.transparent);
        actionBar.setHomeButtonEnabled(true);
        Intent intent = getIntent();
        index = intent.getIntExtra(KEYS.Catalouge.INDEX, 0);
        nameCat = intent.getExtras().getString(KEYS.Catalouge.NOM_CAT);

        idIntervention = intent.getExtras().getString(KEYS.Catalouge.ID_INTER);
        isInventory = intent.getExtras().getString(KEYS.Catalouge.IS_INVENTORY);

        footerView = ((LayoutInflater) CatalougeSubCategotyUpdated.this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
                R.layout.layout_footerview_items_list, null, false);

        //new changes
        isStockChecked = intent.getExtras().getBoolean(KEYS.Catalouge.IS_STOCK, false);
        isRequestChecked = intent.getExtras().getBoolean(KEYS.Catalouge.IS_REQUEST, false);

        init();

    }

    /*
     * pass the value to onActivityResult method
     */
    @Override
    protected void onResume() {
        super.onResume();

        //New changes
        DateChecker.checkDateAndNavigate(this, dataAccessObject);

        Intent intent = getIntent();
        String filterText = intent.getStringExtra(KEYS.Catalouge.NOM_CAT);
        String isParts = intent.getStringExtra(KEYS.Catalouge.IS_PARTS);
        boolean isStockChecked = intent.getBooleanExtra(KEYS.Catalouge.IS_STOCK, false);
        boolean isRequestChecked = intent.getBooleanExtra(KEYS.Catalouge.IS_REQUEST, false);

        Intent intent1 = new Intent();
        if (!TextUtils.isEmpty(filterText) && !TextUtils.isEmpty(isParts)) {
            intent1.putExtra(KEYS.Catalouge.NOM_CAT, filterText);
            intent1.putExtra(KEYS.Catalouge.IS_PARTS, isParts);
            intent1.putExtra(KEYS.Catalouge.IS_STOCK, isStockChecked);
            intent1.putExtra(KEYS.Catalouge.IS_REQUEST, isRequestChecked);
            setResult(RESULT_OK, intent1);
            if (isParts.equalsIgnoreCase("true")) {
                finish();
            }
        }
    }

    public boolean isLGDevice() {
        return (Build.MANUFACTURER.contains("LG") || Build.MODEL.contains("LG"));
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub

        switch (item.getItemId()) {
            case android.R.id.home:
                setResult(RESULT_CANCELED);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }

    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // TODO Auto-generated method stub

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.items_menu, menu);
        searchItem = menu.findItem(R.id.action_search);

        SearchView searchView = (SearchView) MenuItemCompat
                .getActionView(searchItem);
        searchView.setOnQueryTextListener(this);
        searchView.setOnCloseListener(this);



        MenuItemCompat.setOnActionExpandListener(searchItem,
                new MenuItemCompat.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem item) {
                        // Do something when collapsed
//                        filter.filter("");
                        return true; // Return true to collapse action view
                    }

                    @Override
                    public boolean onMenuItemActionExpand(MenuItem item) {
                        // Do something when expanded
                        return true; // Return true to expand action view
                    }
                });

        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Initializes the Dao object and extract the data required for fetching
     * subcategories from database from the intent pass to It from the calling
     * activity.
     */
    private void init() {
        // TODO Auto-generated method stub
        categoryListView = (MyFixedListView) findViewById(R.id.itemListLv);

        final SmoothCheckBox chkStock = (SmoothCheckBox) findViewById(R.id.chk_stock);
        final SmoothCheckBox chkRequest = (SmoothCheckBox) findViewById(R.id.chk_request);

        TextView txtStock = (TextView) findViewById(R.id.txt_stock);
        TextView txtRequest = (TextView) findViewById(R.id.txt_request);

        txtOkBtn = (TextView) findViewById(R.id.txtOk);

        dataAccessObject = DaoManager.getInstance();

        if (isInventory == null
                || isInventory.equalsIgnoreCase("false")) {
            chkRequest.setVisibility(View.GONE);
            txtRequest.setVisibility(View.GONE);
        }

        if (isStockChecked) {
            chkStock.performClick();
        }

        if (isRequestChecked) {
            chkRequest.performClick();
        }

        txtOkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isStockChecked || isRequestChecked) {
                    String cat = "";
                    if (!TextUtils.isEmpty(nameCat)) {
                        cat += nameCat;
                    }

                    Intent intent1 = new Intent();
                    intent1.putExtra(KEYS.Catalouge.NOM_CAT, cat);
                    intent1.putExtra(KEYS.Catalouge.IS_PARTS, "true");
                    intent1.putExtra(KEYS.Catalouge.IS_STOCK, isStockChecked);
                    intent1.putExtra(KEYS.Catalouge.IS_REQUEST, isRequestChecked);
                    setResult(RESULT_OK, intent1);
                    finish();
                }
            }
        });

        chkStock.setOnCheckedChangeListener(new SmoothCheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SmoothCheckBox checkBox, boolean isChecked) {
                isStockChecked = isChecked;
                searchItem.collapseActionView();
                new FetchCategoryList().execute();
            }
        });

        chkRequest.setOnCheckedChangeListener(new SmoothCheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SmoothCheckBox compoundButton, boolean isChecked) {
                isRequestChecked = isChecked;
                searchItem.collapseActionView();
                new FetchCategoryList().execute();
            }
        });

        txtStock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chkStock.setChecked(!isStockChecked, true);
            }
        });

        txtRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chkRequest.setChecked(!isRequestChecked, true);
            }
        });

        new FetchCategoryList().execute();
    }

    /*
     * (non-Javadoc)
     *
     * @see android.support.v4.app.FragmentActivity#onActivityResult(int, int,
     * android.content.Intent)
     */
    @SuppressLint("NewApi")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        /*
         * new changes.... pass the values back to parts & services list.
         */
        if (requestCode == RequestCode.REQUEST_CODE_OPEN_PICES) {

            if (resultCode == RESULT_OK) {

                String filterText = data.getStringExtra(KEYS.Catalouge.NOM_CAT);
                String isParts = data.getStringExtra(KEYS.Catalouge.IS_PARTS);
                boolean isStockChecked = data.getBooleanExtra(KEYS.Catalouge.IS_STOCK, false);
                boolean isRequestChecked = data.getBooleanExtra(KEYS.Catalouge.IS_REQUEST, false);
                if (!TextUtils.isEmpty(isParts) && isParts.equalsIgnoreCase("true")) {

                    //new changes
//                    Bundle bundle = new Bundle();
//                    bundle.putString(KEYS.Catalouge.NOM_CAT, filterText);
//                    bundle.putString(KEYS.Catalouge.IS_PARTS, isParts);
//                    bundle.putBoolean(KEYS.Catalouge.IS_STOCK, isStockChecked);
//                    bundle.putBoolean(KEYS.Catalouge.IS_REQUEST, isRequestChecked);
//                    EventBus.getDefault().post(new InventoryFragmentCatEvent(bundle));

                    //old code
                    Intent intent1 = new Intent();
                    intent1.putExtra(KEYS.Catalouge.NOM_CAT, filterText);
                    intent1.putExtra(KEYS.Catalouge.IS_PARTS, isParts);
                    intent1.putExtra(KEYS.Catalouge.IS_STOCK, isStockChecked);
                    intent1.putExtra(KEYS.Catalouge.IS_REQUEST, isRequestChecked);
                    setResult(RESULT_OK, intent1);
                    finish();

                }

            }

        } else if (requestCode == RequestCode.REQUEST_CODE_OPEN_ITEM) {
            finish();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see android.support.v7.widget.SearchView.OnCloseListener#onClose()
     */
    @Override
    public boolean onClose() {
        // TODO Auto-generated method stub
        return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * android.support.v7.widget.SearchView.OnQueryTextListener#onQueryTextChange
     * (java.lang.String)
     */
    @Override
    public boolean onQueryTextChange(String s) {
        // TODO Auto-generated method stub

        if (!TextUtils.isEmpty(s)) {
            isUserSearching = true;

            ArrayList<CategoryAndPartsInterface> list = new FetchCategoryListValueWithSearch(s).doInBackground();
            updateAdapter(list, true);
        } else {
            isUserSearching = false;
        }

        if (TextUtils.isEmpty(s)) {
            Log.e("SEARCH", "SEARCH IS EMPTY>>>>>>>>>");
//            ArrayList<CategoryAndPartsInterface> list= new FetchCategoryListValue().doInBackground();
//            updateAdapter(list,true);

            new FetchCategoryList().execute();

        }

        return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * android.support.v7.widget.SearchView.OnQueryTextListener#onQueryTextSubmit
     * (java.lang.String)
     */
    @Override
    public boolean onQueryTextSubmit(String arg0) {
        // TODO Auto-generated method stub
//        if (filter != null)
//            filter.filter(arg0);

        new FetchCategoryList().doInBackground();
        return false;
    }

    public void setClickAdapterCheck(CategoryAndPartsInterface categoryAndPartsInterface) {
        if (categoryAndPartsInterface.isCategory()) {
            Categorie map = (Categorie) categoryAndPartsInterface;

            Intent in = getIntent();

            if (map.getIdcat() != 0) {

                /*
                 * new changes
                 */
                Intent intent = new Intent(CatalougeSubCategotyUpdated.this,
                        CatalougeSubCategotyUpdated.class);
                if (!TextUtils.isEmpty(nameCat)) {
                    intent.putExtra(KEYS.Catalouge.NOM_CAT, nameCat
                            + map.getNomcat());
                    intent.putExtra(KEYS.Catalouge.IS_PARTS, "true");
                } else {
                    intent.putExtra(KEYS.Catalouge.NOM_CAT,
                            map.getNomcat());
                    intent.putExtra(KEYS.Catalouge.IS_PARTS, "true");
                }

                //new changes
                intent.putExtra(KEYS.Catalouge.IS_STOCK, isStockChecked);
                intent.putExtra(KEYS.Catalouge.IS_REQUEST, isRequestChecked);
                intent.putExtra(KEYS.Catalouge.IS_INVENTORY, isInventory);

                CatalougeSubCategotyUpdated.this.startActivityForResult(
                        intent, RequestCode.REQUEST_CODE_OPEN_PICES);
            } else {

                Intent i = new Intent(CatalougeSubCategotyUpdated.this,
                        CatalougeSubCategotyUpdated.class);
                i.putExtra(KEYS.Catalouge.ID_CAT, map.getIdcat());
                // i.putExtra("cd_statut", statut);
                if (!TextUtils.isEmpty(nameCat))
                    i.putExtra(KEYS.Catalouge.NOM_CAT,
                            nameCat + map.getNomcat() + "|");
                else
                    i.putExtra(KEYS.Catalouge.NOM_CAT, map.getNomcat()
                            + "|");
                i.putExtra("size", map.getSize());
                i.putExtra("index", map.getIndex());
                i.putExtra("idInter",
                        in.getExtras().getString("idInter"));
                i.putExtra(KEYS.Catalouge.IS_PARTS, "false");

                //new changes
                i.putExtra(KEYS.Catalouge.IS_STOCK, isStockChecked);
                i.putExtra(KEYS.Catalouge.IS_REQUEST, isRequestChecked);
                i.putExtra(KEYS.Catalouge.IS_INVENTORY, isInventory);

                // closeDb();
                CatalougeSubCategotyUpdated.this.startActivityForResult(i,
                        RequestCode.REQUEST_CODE_OPEN_PICES);
            }
        }
    }


    /*
     * (non-Javadoc)
     *
     * @see android.support.v7.app.ActionBarActivity#onBackPressed()
     */
    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        setResult(RESULT_CANCELED);
        // Intent intent = new Intent(CatalougeSubCategory.this,
        // PartsAndServicesList.class);
        // startActivity(intent);
        super.onBackPressed();
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

    // ---------------------------------------------------ASYNC...CLASS...STARTS-------------------------------------------------

    /**
     * Fetch the list of parts and services.
     */
    private class FetchCategoryList extends
            AsyncTaskCoroutine<Void, ArrayList<CategoryAndPartsInterface>> {

        public ArrayList<CategoryAndPartsInterface> list = new ArrayList<>();

        public FetchCategoryList() {

            categoryOffset = 1;

            if (categoryList != null) {
                categoryList.clear();
            } else {
                categoryList = new ArrayList<>();
            }

        }

        @Override
        public void onPreExecute() {
            super.onPreExecute();

            DialogUtils
                    .showProgressDialog(
                            CatalougeSubCategotyUpdated.this,
                            CatalougeSubCategotyUpdated.this
                                    .getString(R.string.textWaitLable),
                            CatalougeSubCategotyUpdated.this
                                    .getString(R.string.textProgressDialogFetchNearestClient),
                            false);
        }

        @Override
        public ArrayList<CategoryAndPartsInterface> doInBackground(Void... params) {

            list = new ArrayList<>();

            categoryListCount = 0;
            categoryOffset = 1;

//            categoryListCount = dao._partsListCount(partsFilter);
//            list.addAll(dao._partsList(partsFilter));


            if (isInventory == null
                    || isInventory.equalsIgnoreCase("false")) {

                if (isStockChecked) {

                    categoryListCount = dataAccessObject.getStockAllCategoryCount(nameCat);
                    list.addAll(dataAccessObject.getStockAllCategory(
                            index, nameCat, scrollIndex, categoryOffset));

                } else {
                    categoryListCount = dataAccessObject.getAllCategoryPartsCount(nameCat);
                    list.addAll(dataAccessObject.getAllCategoryParts(
                            index, nameCat, scrollIndex, categoryOffset));

                    Logger.log(TAG, "Category Parts List check categoryList OLD is ===>" + categoryList.size());

                }

            } else {

                if (!isStockChecked && !isRequestChecked) {

                    categoryListCount = dataAccessObject.getAllCategoryCount(nameCat);
                    list.addAll(dataAccessObject.getAllCategory(
                            index, nameCat, scrollIndex, categoryOffset));
                    Logger.log(TAG, "Category Parts List check categoryList is ===>" + categoryList.size());

                } else if (isStockChecked && isRequestChecked) {
                    categoryListCount = dataAccessObject.getStockRequestAllCategoryCount(nameCat);
                    list.addAll(dataAccessObject.getStockRequestAllCategory(
                            index, nameCat, scrollIndex, categoryOffset));
                    Logger.log(TAG, "Category Parts List check categoryList is ===>" + categoryList.size());
                } else if (isStockChecked) {
                    categoryListCount = dataAccessObject.getStockAllCategoryCount(nameCat);
                    list.addAll(dataAccessObject.getStockAllCategory(
                            index, nameCat, scrollIndex, categoryOffset));
                    Logger.log(TAG, "Category Parts List check categoryList OLD is ===>" + categoryList.size());

                } else if (isRequestChecked) {
                    categoryListCount = dataAccessObject.getRequestAllCategoryCount(nameCat);
                    list.addAll(dataAccessObject.getRequestAllCategory(
                            index, nameCat, scrollIndex, categoryOffset));
                    Logger.log(TAG, "Category Parts List check categoryList OLD is ===>" + categoryList.size());

                }
            }
            Logger.log(TAG, "Category Parts List check count is ===>" + categoryListCount);
            Logger.log(TAG, "Category Parts List check List size async is ===>" + list.size());

            return list;
        }

        @Override
        public void onPostExecute(ArrayList<CategoryAndPartsInterface> result) {
            super.onPostExecute(result);

            DialogUtils.dismissProgressDialog();

            if (categoryListCount > 20) {
                categoryListView.addFooterView(footerView);
            } else {
                categoryListView.removeFooterView(footerView);
            }


            if (result != null) {
                categoryList.addAll(result);
                setListAdapter();
            }

        }

    }

    // ---------------------------------------------------ASYNC...CLASS...STARTS-------------------------------------------------

    /**
     * Sets the adapter for parts & services list.
     */
    private void setListAdapter() {

        if (categoryAdapter == null) {
            categoryAdapter = new CategoryItemsAdapter(
                    CatalougeSubCategotyUpdated.this,android.R.id.text1, categoryList,
                    dataAccessObject, idIntervention);

            categoryAdapter.setIndexPosition(scrollIndex);
            categoryListView.setAdapter(categoryAdapter);

        } else {
            categoryAdapter.notifyDataSetChanged();
        }


        categoryListView.setOnScrollListener(mOnScrollListener);

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

            if (categoryAdapter != null) {
                int ctadapter = categoryAdapter.getCount();
                if (isUserScrolled && (firstVisibleItem + visibleItemCount) == totalItemCount) {
                    isUserScrolled = false;

                    if (ctadapter >= categoryListCount) {
                        hideFooterView();
                    } else {
                        if (totalItemCount > 0) {
                            updateCategoryItemsList();
//                            showFooterView();
                        }
                    }

                }
            }

        }
    };

    /**
     * Updates the parts services list.
     */
    private void updateCategoryItemsList() {
        new FetchCategoryListValue().execute();

    }
    public void updateAdapter(final ArrayList<CategoryAndPartsInterface> list,
                              boolean clearList) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (list != null) {

                    if (clearList)
                        categoryList.clear();

                    hideFooterView();

                    categoryList.addAll(list);

                    if (categoryAdapter != null)
                        categoryAdapter.notifyDataSetChanged();

                    categoryListView.removeFooterView(footerView);
                    if (categoryListCount > 20) {
                        categoryListView.addFooterView(footerView);
                    }

                }
            }
        });
    }


    /**
     * Fetch the list of parts and services.
     */
    private class FetchCategoryListValueWithSearch extends
            AsyncTaskCoroutine<Void, ArrayList<CategoryAndPartsInterface>> {

        public ArrayList<CategoryAndPartsInterface> list;
        String searchText;

        public FetchCategoryListValueWithSearch(String searchText) {
            this.searchText = searchText;
        }

        @Override
        public ArrayList<CategoryAndPartsInterface> doInBackground(Void... params) {

            list = new ArrayList<>();

            categoryListCount = 0;
            categoryOffset = 1;

            if (isInventory == null
                    || isInventory.equalsIgnoreCase("false")) {

                if (isStockChecked) {

                    categoryListCount = dataAccessObject.getStockAllCategoryCountWithSearch(nameCat, searchText);
                    list.addAll(dataAccessObject.getStockAllCategoryWithSearch(
                            index, nameCat, scrollIndex, categoryOffset, searchText));

                } else {
                    categoryListCount = dataAccessObject.getAllCategoryPartsCountWithSearch(nameCat, searchText);
                    list.addAll(dataAccessObject.getAllCategoryPartsWithSearch(
                            index, nameCat, scrollIndex, categoryOffset, searchText));

                    Logger.log(TAG, "Category Parts List check categoryList OLD is ===>" + categoryList.size());

                }

            } else {

                if (!isStockChecked && !isRequestChecked) {
                    categoryListCount = dataAccessObject.getAllCategoryCountWithSearch(nameCat, searchText);
                    list.addAll(dataAccessObject.getAllCategoryWithSearch(
                            index, nameCat, scrollIndex, categoryOffset, searchText));
                } else if (isStockChecked && isRequestChecked) {
                    categoryListCount = dataAccessObject.getStockRequestAllCategoryCountWithSearch(nameCat, searchText);
                    list.addAll(dataAccessObject.getStockRequestAllCategoryWithSearch(
                            index, nameCat, scrollIndex, categoryOffset, searchText));
                    Logger.log(TAG, "Category Parts List check categoryList is ===>" + categoryList.size());
                } else if (isStockChecked) {
                    categoryListCount = dataAccessObject.getStockAllCategoryCountWithSearch(nameCat, searchText);
                    list.addAll(dataAccessObject.getStockAllCategoryWithSearch(
                            index, nameCat, scrollIndex, categoryOffset, searchText));
                    Logger.log(TAG, "Category Parts List check categoryList OLD is ===>" + categoryList.size());

                } else if (isRequestChecked) {
                    categoryListCount = dataAccessObject.getRequestAllCategoryCountWithSearch(nameCat, searchText);
                    list.addAll(dataAccessObject.getRequestAllCategoryWithSearch(
                            index, nameCat, scrollIndex, categoryOffset, searchText));
                    Logger.log(TAG, "Category Parts List check categoryList OLD is ===>" + categoryList.size());

                }
            }
            Logger.log(TAG, "Category Parts List check count is ===>" + categoryListCount);
            Logger.log(TAG, "Category Parts List check List size async is ===>" + list.size());

            return list;
        }

        @Override
        public void onPostExecute(ArrayList<CategoryAndPartsInterface> categoryAndPartsInterfaces) {
            super.onPostExecute(categoryAndPartsInterfaces);
            updateAdapter(list, true);
        }

    }

    /**
     * Fetch the list of parts and services.
     */
    private class FetchCategoryListValue extends
            AsyncTaskCoroutine<Void, ArrayList<CategoryAndPartsInterface>> {

        public ArrayList<CategoryAndPartsInterface> list;

        @Override
        public void onPreExecute() {
            super.onPreExecute();
            categoryOffset = categoryOffset + mCount;
//            if(categoryListCount> mCount){
                footerView.setVisibility(View.VISIBLE);

//                showFooterView();
//            }
        }

        @Override
        public ArrayList<CategoryAndPartsInterface> doInBackground(Void... params) {

            list = new ArrayList<>();

            Logger.log(TAG, "loading more data===>" + categoryList.size());


            categoryListCount = 0;

            if (isInventory == null
                    || isInventory.equalsIgnoreCase("false")) {

                if (isStockChecked) {

                    categoryListCount = dataAccessObject.getStockAllCategoryCount(nameCat);
                    list.addAll(dataAccessObject.getStockAllCategory(
                            index, nameCat, scrollIndex, categoryOffset));

                } else {
                    categoryListCount = dataAccessObject.getAllCategoryPartsCount(nameCat);
                    list.addAll(dataAccessObject.getAllCategoryParts(
                            index, nameCat, scrollIndex, categoryOffset));

                    Logger.log(TAG, "Category Parts List check categoryList OLD is ===>" + categoryList.size());

                }

            } else {

                if (!isStockChecked && !isRequestChecked) {
                    categoryListCount = dataAccessObject.getAllCategoryCount(nameCat);
                    list.addAll(dataAccessObject.getAllCategory(
                            index, nameCat, scrollIndex, categoryOffset));
                } else if (isStockChecked && isRequestChecked) {
                    categoryListCount = dataAccessObject.getStockRequestAllCategoryCount(nameCat);
                    list.addAll(dataAccessObject.getStockRequestAllCategory(
                            index, nameCat, scrollIndex, categoryOffset));
                    Logger.log(TAG, "Category Parts List check categoryList is ===>" + categoryList.size());
                } else if (isStockChecked) {
                    categoryListCount = dataAccessObject.getStockAllCategoryCount(nameCat);
                    list.addAll(dataAccessObject.getStockAllCategory(
                            index, nameCat, scrollIndex, categoryOffset));
                    Logger.log(TAG, "Category Parts List check categoryList OLD is ===>" + categoryList.size());

                } else if (isRequestChecked) {
                    categoryListCount = dataAccessObject.getRequestAllCategoryCount(nameCat);
                    list.addAll(dataAccessObject.getRequestAllCategory(
                            index, nameCat, scrollIndex, categoryOffset));
                    Logger.log(TAG, "Category Parts List check categoryList OLD is ===>" + categoryList.size());

                }
            }
            Logger.log(TAG, "Category Parts List check count is ===>" + categoryListCount);
            Logger.log(TAG, "Category Parts List check List size async is ===>" + list.size());

            return list;
        }


        @Override
        public void onPostExecute(ArrayList<CategoryAndPartsInterface> categoryAndPartsInterfaces) {
            super.onPostExecute(categoryAndPartsInterfaces);

//            updateAdapter(categoryAndPartsInterfaces, false);

            categoryList.addAll(list);

            footerView.setVisibility(View.INVISIBLE);

            if (categoryAdapter != null)
                categoryAdapter.notifyDataSetChanged();

//            hideFooterView();

        }

    }


}





