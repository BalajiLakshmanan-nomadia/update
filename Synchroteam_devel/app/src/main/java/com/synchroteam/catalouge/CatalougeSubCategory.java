package com.synchroteam.catalouge;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import androidx.core.view.MenuItemCompat;
import androidx.core.view.MenuItemCompat.OnActionExpandListener;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

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
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Filter;
import android.widget.Toast;

import com.synchroteam.TypefaceLibrary.TextView;
import com.synchroteam.beans.Categorie;
import com.synchroteam.beans.CategoryAndPartsInterface;
import com.synchroteam.beans.Client_Site_EquipmnentBean;
import com.synchroteam.beans.EnableSynchronizationAddJobEvent;
import com.synchroteam.beans.InventoryItemBeans;
import com.synchroteam.dao.Dao;
import com.synchroteam.listadapters.CategoryAdapter;
import com.synchroteam.listadapters.EquipmentSectionListAdapterNew;
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
import com.synchroteam.utils.SharedPref;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

// TODO: Auto-generated Javadoc

/**
 * This Activity shows the category of parts shown by CatalougePices Activty the
 * basic functionality of this activity is to manage Catadory and subcategoy of
 * parts And service module and also to fetch Category or SubCategory from
 * database and fill those parts to ListView. author Ideavate.solution .
 */
public class CatalougeSubCategory extends AppCompatActivity implements
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
    private ArrayList<CategoryAndPartsInterface> categories;

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
    private CategoryAdapter categoryAdapter;

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

    //new addition for category offlist
    int categoryOffset = 1;

    private boolean isUserScrolled = false;

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

        // actionBar.setTitle(titleSpannable);
        actionBar.setTitle(isLGDevice() ? title : titleSpannable);

        actionBar.setIcon(android.R.color.transparent);
        actionBar.setHomeButtonEnabled(true);
        Intent intent = getIntent();
        index = intent.getIntExtra(KEYS.Catalouge.INDEX, 0);
        nameCat = intent.getExtras().getString(KEYS.Catalouge.NOM_CAT);

        idIntervention = intent.getExtras().getString(KEYS.Catalouge.ID_INTER);
        isInventory = intent.getExtras().getString(KEYS.Catalouge.IS_INVENTORY);

        footerView = ((LayoutInflater) CatalougeSubCategory.this
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
        MenuItem searchItem = menu.findItem(R.id.action_search);

        SearchView searchView = (SearchView) MenuItemCompat
                .getActionView(searchItem);
        searchView.setOnQueryTextListener(this);
        searchView.setOnCloseListener(this);
        MenuItemCompat.setOnActionExpandListener(searchItem,
                new OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem item) {
                        // Do something when collapsed
                        filter.filter("");
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




//        categoryListView.setOnScrollListener(new OnScrollListener() {
//
//            @Override
//            public void onScrollStateChanged(AbsListView view, int scrollState) {
//                // TODO Auto-generated method stub
//
//                if (footerView != null && footerView.isShown()) {
//                    scrollIndex++;
//                    categoryAdapter.setIndexPosition(scrollIndex);
//                    try {
//                        Thread.sleep(500);
//                    } catch (InterruptedException e) {
//                        Logger.printException(e);
//                    }
//                    categoryAdapter.notifyDataSetChanged();
//                }
//
//            }
//
//            @Override
//            public void onScroll(AbsListView view, int firstVisibleItem,
//                                 int visibleItemCount, int totalItemCount) {
//                if (categoryAdapter != null) {
//                    int categoryCount = dataAccessObject.getAllCategoryCount(nameCat);
//                    Log.e("COUNT","CATEGORY COUNT>>>>>>>"+categoryCount);
//                    int ctadapter = categoryAdapter.getCount();
//                    Log.e("CHECK","CTADAPTER SIZE>>>>>"+ctadapter);
//
//                    if (!isUserSearching) {
////                        if (ctadapter >= nbreCat)
////                        if(categoryCount>= nbreCat)
//                        if(categoryCount >= ctadapter)
//                        {
//
//                            /*
//                            new changes for fetching limited data(20) while scrolling
//                             */
//                            FetchDataOnScroll fetchDataOnScroll = new FetchDataOnScroll();
//                            fetchDataOnScroll.execute();
////                            categoryAdapter.setIndexPosition(scrollIndex);
//
//                            categoryAdapter.notifyDataSetChanged();
//                            categoryListView.removeFooterView(footerView);
//                            Log.e("SIZECHECK","ArrayList size is>>>>>>>>>>"+categories.size());
//                        }
////                        categoryAdapter.notifyDataSetChanged();
//                    } else {
//                        if (ctadapter >= categoryAdapter.getArrayCount()) {
//                            hideFooterView();
//                        } else {
//                            showFooterView();
//                        }
//                    }
//
//                }
//
//
//            }
//        });

        chkStock.setOnCheckedChangeListener(new SmoothCheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SmoothCheckBox checkBox, boolean isChecked) {
                isStockChecked = isChecked;
              new FetchCategoriesAsyncTask().execute();
//                new FetchCategoryStock().execute();
            }
        });

        chkRequest.setOnCheckedChangeListener(new SmoothCheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SmoothCheckBox compoundButton, boolean isChecked) {
                isRequestChecked = isChecked;
                new FetchCategoriesAsyncTask().execute();
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

        new FetchCategoriesAsyncTask().execute();

//        new FetchCategory().execute();

//        new FetchCategoryStock().execute();
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
                int ctadapter = categoryAdapter.getCount();
                if (ctadapter >= nbreCat) {
                    hideFooterView();
                } else {
                    if (totalItemCount > 0) {
                        scrollInventoryList(false);
                        showFooterView();
                    }
                }


//                if(totalItemCount<nbreCat) {
//                    updateCategoryList();
//                }else{
//                    hideFooterView();
//                }

            }
        }
    };

    /**
     * Updates the parts services list.
     */
    private void scrollInventoryList(final boolean showProgress) {
        showFooterView();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {

                    categoryOffset += 20;

//                    ArrayList<Categorie> list = dataAccessObject.getCategoryNameList(
//                            index, nameCat, categoryOffset);

//                    ArrayList<Categorie> list = dataAccessObject.getStockRequestCategoryInventoryNewWithOffset(index,nameCat,categoryOffset);

//                    ArrayList<Categorie> list = dataAccessObject.getStockRequestCategoryInventoryNew(index,nameCat,scrollIndex);

//                    ArrayList<Categorie> list =dataAccessObject.getStockCategorieNiveauNew(index, nameCat, scrollIndex);

                  ArrayList<Categorie> list = dataAccessObject.getRequestCategoryInventory(index, nameCat, scrollIndex);

                    Logger.log(TAG, "Category list offset Scroll ==>" + categoryOffset);
                    Logger.log(TAG, "Category list cnt new Scroll ==>" + nbreCat);
                    Logger.log(TAG, "Category list size old Scroll ==>" + list.size());

                    for(int i=0;i<=list.size();i++){
                        Log.e("NAME","CATEGORY NAME>>>>>>>"+list.get(i).getNomcat());

                    }


                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            updateAdapter(list);

                            hideFooterView();

                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        }, 300);//def 980

    }

    private void updateCategoryList() {

        showFooterView();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                categoryOffset += 20;

                ArrayList<Categorie> list = dataAccessObject.getCategoryNameList(
                        index, nameCat, categoryOffset);

                categoryAdapter.notifyDataSetChanged();
                categoryListView.removeFooterView(footerView);

                Logger.log(TAG, "Category list offset Scroll ==>" + categoryOffset);
                Logger.log(TAG, "Category list cnt new Scroll ==>" + nbreCat);
                Logger.log(TAG, "Category list size old Scroll ==>" + list.size());

                updateAdapter(list);

            }
        }, 900);
    }

    public void updateAdapter(final ArrayList<Categorie> list) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (list != null && list.size()>0) {

                    categories.addAll(list);

                    categoryAdapter.notifyDataSetChanged();

                    hideFooterView();

                }
            }
        });

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
        if (filter != null)
            filter.filter(s);

        if (!TextUtils.isEmpty(s)) {
            isUserSearching = true;
        } else {
            isUserSearching = false;
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
        if (filter != null)
            filter.filter(arg0);
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
                Intent intent = new Intent(CatalougeSubCategory.this,
                        CatalougeSubCategory.class);
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

                CatalougeSubCategory.this.startActivityForResult(
                        intent, RequestCode.REQUEST_CODE_OPEN_PICES);
            } else {

                Intent i = new Intent(CatalougeSubCategory.this,
                        CatalougeSubCategory.class);
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
                CatalougeSubCategory.this.startActivityForResult(i,
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
    public class FetchDataOnScroll extends AsyncTaskCoroutine<Void,Void> {

        @Override
        public Void doInBackground(Void... voids) {
            categoryOffset = categoryOffset+ 20;
            categories.addAll(dataAccessObject.getAllCategorieNiveauOffset(
                    index, nameCat, scrollIndex,categoryOffset));
            Log.e("CHECK","CATEGORIES ARRAYLIST SIZE>>>>"+ categories.size());

            return null;
        }

        @Override
        public void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            Log.e("ITRATIONCHECK","ITRATIONCHECK");
            categoryAdapter.notifyDataSetChanged();
        }
    }


    /**
     * The Async task fetches the data of category of sub category from database
     * in a seprate theread and shows the progress dialog till the data is
     * fetched.
     */
    private class FetchCategoriesAsyncTask extends
            AsyncTaskCoroutine<Void, Boolean> {

        /**
         * Instantiates a new fetch categories async task.
         */
        public FetchCategoriesAsyncTask() {
            // TODO Auto-generated constructor stub


            if (categories != null) {
                categories.clear();
            } else {
                categories = new ArrayList<>();

            }
            Logger.log(TAG,"Category async task executed");
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
            DialogUtils
                    .showProgressDialog(
                            CatalougeSubCategory.this,
                            CatalougeSubCategory.this
                                    .getString(R.string.textWaitLable),
                            CatalougeSubCategory.this
                                    .getString(R.string.textProgressDialogFetchNearestClient),
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
            try {
                if (isInventory == null
                        || isInventory.equalsIgnoreCase("false")) {
                    if (isStockChecked) {
                        categories.addAll(dataAccessObject.getStockCategorieNiveauNew(index, nameCat, scrollIndex));
                    } else {
//                        categoryOffset = categoryOffset+ 15;
                        categories.addAll(dataAccessObject.getAllCategorieNiveauOffset(
                                index, nameCat, scrollIndex,categoryOffset));
                    }
                } else {
                    if (!isStockChecked && !isRequestChecked) {
                        Log.e("QUERY CHECK","AllCategoryInventory Query is called>>>>>>");
                        int inventory = dataAccessObject.getAllCategoryInventoryCount(nameCat);
                        Log.e("COUNTCHECK","ALL CATEGORY INVENTORY COUNT>>>>>>>>>>"+inventory);
                        categories.addAll(dataAccessObject.getAllCategoryInventory(
                                index, nameCat, scrollIndex));
                        Log.e("CHECK","CATEGORIES ARRAYLIST SIZE>>>>>>>"+categories.size());
                    } else if (isStockChecked && isRequestChecked) {
                        // this new query is used for fetch the data faster
                        categories.addAll(dataAccessObject.getStockRequestCategoryInventoryNew(index, nameCat, scrollIndex));
                    } else if (isStockChecked) {
                        // this new query is used for fetch the data faster
                        categories.addAll(dataAccessObject.getStockCategorieNiveauNew(index, nameCat, scrollIndex));
                    } else if (isRequestChecked) {
                        categories.addAll(dataAccessObject.getRequestCategoryInventory(index, nameCat, scrollIndex));
                    }

                }

                Logger.log(TAG,"Category async task list size is ==>"+categories.size());
                return Boolean.valueOf(true);



            } catch (Exception e) {
                // TODO Auto-generated catch block
                Logger.printException(e);
                return Boolean.valueOf(false);
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

                    Logger.output(TAG, "" + categories.size());

                    nbreCat = categories.size();

                    if (nbreCat >= 20)
                        categoryListView.addFooterView(footerView);
                    else
                        categoryListView.removeFooterView(footerView);

                    categoryAdapter = new CategoryAdapter(
                            CatalougeSubCategory.this, categories,
                            dataAccessObject, idIntervention);



                    categoryAdapter.setIndexPosition(scrollIndex);
                    categoryListView.setAdapter(categoryAdapter);
                    filter = categoryAdapter.getFilter();

                }

            } catch (Exception e) {
                Logger.printException(e);
            } finally {
                DialogUtils.dismissProgressDialog();
            }

        }

    }


    private class FetchCategory extends AsyncTaskCoroutine<Void, Void> {

        public FetchCategory() {

            categoryOffset = 1;

            if (categories != null) {
                categories.clear();
            } else {
                categories = new ArrayList<>();

            }

        }

        @Override
        public void onPreExecute() {
            super.onPreExecute();
            DialogUtils
                    .showProgressDialog(
                            CatalougeSubCategory.this,
                            CatalougeSubCategory.this
                                    .getString(R.string.textWaitLable),
                            CatalougeSubCategory.this
                                    .getString(R.string.textProgressDialogFetchNearestClient),
                            false);
        }

        @Override
        public Void doInBackground(Void... params) {
            nbreCat = 0;


            nbreCat = dataAccessObject.getAllCategoryInventoryCount(nameCat);
            categories.addAll(dataAccessObject.getCategoryNameList(
                    index, nameCat, categoryOffset));


            Logger.log(TAG, "Category list cnt new ==>" + nbreCat);
            Logger.log(TAG, "Category list size old ==>" + categories.size());
            return null;
        }

        @Override
        public void onPostExecute(Void result) {
            super.onPostExecute(result);

            DialogUtils.dismissProgressDialog();

            if (categoryAdapter == null) {
                categoryAdapter = new CategoryAdapter(
                        CatalougeSubCategory.this, categories,
                        dataAccessObject, idIntervention);
//                categoryAdapter.setIndexPosition(scrollIndex);
                categoryListView.setAdapter(categoryAdapter);

            } else {
                categoryAdapter.notifyDataSetChanged();
            }

            if (nbreCat >= 20)
                categoryListView.addFooterView(footerView);
            else
                categoryListView.removeFooterView(footerView);


            filter = categoryAdapter.getFilter();

            categoryListView.setOnScrollListener(mOnScrollListener);

        }

    }

    private class FetchCategoryStock extends AsyncTaskCoroutine<Void, Void> {

        public FetchCategoryStock() {

            categoryOffset = 1;

            if (categories != null) {
                categories.clear();
            } else {
                categories = new ArrayList<>();

            }

        }

        @Override
        public void onPreExecute() {
            super.onPreExecute();
            DialogUtils
                    .showProgressDialog(
                            CatalougeSubCategory.this,
                            CatalougeSubCategory.this
                                    .getString(R.string.textWaitLable),
                            CatalougeSubCategory.this
                                    .getString(R.string.textProgressDialogFetchNearestClient),
                            false);
        }

        @Override
        public Void doInBackground(Void... params) {
            nbreCat = 0;


            nbreCat = dataAccessObject.getAllCategoryInventoryCount(nameCat);
//            categories.addAll(dataAccessObject.getStockRequestCategoryInventoryNewWithOffset(index, nameCat, categoryOffset));
//


            categories.addAll(dataAccessObject.getStockCategorieNiveauNewWithOffset(index, nameCat, categoryOffset));

            categories.addAll(dataAccessObject.getRequestCategoryInventory(index, nameCat, scrollIndex));

            Logger.log(TAG, "Category list stock cnt new ==>" + nbreCat);
            Logger.log(TAG, "Category list stock size old ==>" + categories.size());
            return null;
        }

        @Override
        public void onPostExecute(Void result) {
            super.onPostExecute(result);

            DialogUtils.dismissProgressDialog();

            if (categoryAdapter == null) {
                categoryAdapter = new CategoryAdapter(
                        CatalougeSubCategory.this, categories,
                        dataAccessObject, idIntervention);
//                categoryAdapter.setIndexPosition(scrollIndex);
                categoryListView.setAdapter(categoryAdapter);

            } else {
                categoryAdapter.notifyDataSetChanged();
            }

            if (nbreCat >= 20)
                categoryListView.addFooterView(footerView);
            else
                categoryListView.removeFooterView(footerView);


            filter = categoryAdapter.getFilter();

            categoryListView.setOnScrollListener(mOnScrollListener);

        }

    }

}
