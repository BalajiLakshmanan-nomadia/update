//package com.synchroteam.fragmenthelper;
//
//import android.app.Activity;
//import android.content.Context;
//import android.content.Intent;
//import android.graphics.Typeface;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.os.Handler;
//import android.text.Editable;
//import android.text.TextUtils;
//import android.text.TextWatcher;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.ViewGroup;
//import android.widget.AbsListView;
//import android.widget.AbsListView.OnScrollListener;
//import android.widget.Filter;
//import android.widget.ListView;
//import android.widget.ProgressBar;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//
//import com.synchroteam.TypefaceLibrary.EditText;
//import com.synchroteam.beans.EnableSynchronizationAddJobEvent;
//import com.synchroteam.beans.InventoryItemBeans;
//import com.synchroteam.beans.InventoryListBeans;
//import com.synchroteam.beans.InvoicingCatalogCategoryBeans;
//import com.synchroteam.catalouge.CatalougeSubCategory;
//import com.synchroteam.dao.Dao;
//import com.synchroteam.fragment.BaseFragment;
//import com.synchroteam.fragment.InventoryFragment;
//import com.synchroteam.listadapters.InventoryListAdapter;
//import com.synchroteam.synchroteam3.R;
//import com.synchroteam.utils.DaoManager;
//import com.synchroteam.utils.KEYS;
//import com.synchroteam.utils.RequestCode;
//import com.synchroteam.utils.ScannerBar;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Comparator;
//
//import de.greenrobot.event.EventBus;
//
//public class InventoryFragmentHelpertoClientlistFragmentHelper implements HelperInterface {
//
//    private View view;
//    private EditText edtInventory;
//    private ListView listViewInventory;
//    private TextView txtSearchIcon, txtBarcodeIcon, txtFilterIcon;
//    private com.synchroteam.TypefaceLibrary.TextView txtFilter;
//    private com.synchroteam.TypefaceLibrary.TextView txtFilterType;
//    private TextView txtCloseFilter;
//    private RelativeLayout relFilterValue;
//    private View footerView;
//    private Activity synchroteamActivity;
//    private InventoryFragment inventoryFragment;
//    private InventoryListAdapter adapter;
//    private Typeface typeFace;
//    private Dao dao;
//
//    private ArrayList<InventoryListBeans> inventoryList;
//    private InvoicingCatalogCategoryBeans categoryBean;
//    private ArrayList<InventoryItemBeans> inventoryArray;
//
//    private Filter edittextFilter, filterForCategory;
//    private boolean isCategoryFiltered;
//    private boolean isFiltered;
//
//    private ProgressBar progressBarInventory;
//    private BaseFragment baseFragment;
//
//    private int index;
//    private int partsCount;
//    private boolean isUserSearching = false;
//    boolean loadMore = false;
//
//    private String filteredCategory;
//    private boolean isStock;
//    private boolean isRequest;
//
//    // private static final String TAG = "InventoryFragmentHelper";
//
//    public InventoryFragmentHelpertoClientlistFragmentHelper(Activity synchroteamActivity,
//                                                             InventoryFragment inventoryFragment, BaseFragment baseFragment) {
//        this.synchroteamActivity = synchroteamActivity;
//        this.inventoryFragment = inventoryFragment;
//        this.baseFragment = baseFragment;
//    }
//
//    @Override
//    public View inflateLayout(LayoutInflater inflater, ViewGroup container) {
//        view = inflater.inflate(R.layout.fragment_inventory, container, false);
//        initiateView(view);
//        return view;
//
//    }
//
//    public void doOnResume() {
//
//        // getIQList();
//
//    }
//
//    @Override
//    public void initiateView(View v) {
//        dao = DaoManager.getInstance();
//        edtInventory = (EditText) v.findViewById(R.id.edtInventory);
//        listViewInventory = (ListView) v.findViewById(R.id.listInventory);
//        // spinnerSort = (Spinner) v.findViewById(R.id.spinnerSort);
//        txtBarcodeIcon = (TextView) v.findViewById(R.id.txtBarcodeIcon);
//        txtSearchIcon = (TextView) v.findViewById(R.id.txtSearchIcon);
//        txtFilterIcon = (TextView) v.findViewById(R.id.txtFilterIcon);
//        txtFilter = (com.synchroteam.TypefaceLibrary.TextView) v
//                .findViewById(R.id.txt_filter);
//        txtFilterType = (com.synchroteam.TypefaceLibrary.TextView) v
//                .findViewById(R.id.txt_filter_type);
//
//        txtCloseFilter = (TextView) v.findViewById(R.id.txt_close_filter);
//        relFilterValue = (RelativeLayout) v.findViewById(R.id.rel_filter);
//
//        footerView = ((LayoutInflater) synchroteamActivity
//                .getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
//                R.layout.layout_footerview_items_list, null, false);
//
//        progressBarInventory = (ProgressBar) view
//                .findViewById(R.id.progressBarInventory);
//
//        txtBarcodeIcon.setOnClickListener(clickListener);
//        txtFilterIcon.setOnClickListener(clickListener);
//        txtCloseFilter.setOnClickListener(clickListener);
//
//        setTypeFace();
//
//        index = 1;
//
//        new FetchPartsAndServicesList().execute();
//    }
//
//    public void hideFooterView() {
//        if (footerView != null) {
//            if (footerView.getVisibility() == View.VISIBLE) {
//                footerView.setVisibility(View.INVISIBLE);
//            }
//        }
//
//    }
//
//    public void showFooterView() {
//        if (footerView != null) {
//            if (footerView.getVisibility() == View.INVISIBLE) {
//                footerView.setVisibility(View.VISIBLE);
//            }
//        }
//    }
//
//    /**
//     * Gets the parts and services list.
//     *
//     * @return
//     */
//    private ArrayList<InventoryItemBeans> getPartsAndServicesList() {
//
//        return dao.getInventoryList();
//    }
//
//    private void setInventoryListAdapter() {
//        if (adapter == null) {
//        adapter = new InventoryListAdapter(synchroteamActivity,
//                android.R.id.text1, inventoryArray, dao, null, false, false, this);
//        // adapter.sortByCategoryAsc();
//        adapter.setIndexPosition(1);
//        listViewInventory.setAdapter(adapter);
//        } else {
//            adapter.setOriginalList(inventoryArray);
//            adapter.notifyDataSetChanged();
//        }
//
//        if (edittextFilter == null) {
//            edittextFilter = adapter.getEditFilter(false);
//            edtInventory.addTextChangedListener(textWatcher);
//        }
//        if (filterForCategory == null) {
//            filterForCategory = adapter.getCategoryFilter();
//        }
//
//        baseFragment.listUpdate();
//
//        listViewInventory.setOnScrollListener(new OnScrollListener() {
//
//            @Override
//            public void onScrollStateChanged(AbsListView view, int scrollState) {
//
//            }
//
//            @Override
//            public void onScroll(AbsListView view, int firstVisibleItem,
//                                 int visibleItemCount, final int totalItemCount) {
//                int ctadapter = adapter.getCount();
//                if (!loadMore) {
//                    if (footerView != null && footerView.isShown()) {
//                        loadMore = true;
//                        Handler handler = new Handler();
//                        handler.postDelayed(new Runnable() {
//
//                            @Override
//                            public void run() {
//                                index++;
//                                adapter.setIndexPosition(index);
//                                adapter.notifyDataSetChanged();
//
//                                loadMore = false;
//                            }
//                        }, 900);
//
//                    }
//                }
//
//                if (!isUserSearching) {
//                    if (ctadapter >= partsCount) {
//                        hideFooterView();
//                    }
//                } else {
//                    if (ctadapter >= adapter.getArrayCount()) {
//                        hideFooterView();
//                    } else {
//                        showFooterView();
//                    }
//                }
//
//            }
//        });
//    }
//
//    /**
//     * Sorts the array list of parts and services in ASC order.
//     */
//    public void sortByArraylistAsc() {
//        Comparator<InventoryItemBeans> comparator = new Comparator<InventoryItemBeans>() {
//
//            @Override
//            public int compare(InventoryItemBeans lhs, InventoryItemBeans rhs) {
//                if ((lhs.getCategory().compareToIgnoreCase(rhs.getCategory()) == 0)) {
//                    return lhs.getPartsService().compareToIgnoreCase(
//                            rhs.getPartsService());
//                } else {
//                    return lhs.getCategory().compareToIgnoreCase(
//                            rhs.getCategory());
//                }
//            }
//        };
//        Collections.sort(inventoryArray, comparator);
//    }
//
//    private void setTypeFace() {
//        typeFace = Typeface.createFromAsset(synchroteamActivity.getAssets(),
//                synchroteamActivity.getString(R.string.fontName_fontAwesome));
//        txtFilterIcon.setTypeface(typeFace);
//        txtSearchIcon.setTypeface(typeFace);
//        txtBarcodeIcon.setTypeface(typeFace);
//        txtCloseFilter.setTypeface(typeFace);
//    }
//
//    @Override
//    public void doOnSyncronize() {
//        // setListAdapter();
//        new FetchPartsAndServicesList().execute();
//    }
//
//    @Override
//    public void onReturnToActivity(int requestCode) {
//
//    }
//
//    /**
//     * calls after returning to fragment
//     *
//     * @param requestCode
//     * @param data
//     */
//    public void onReturnToFragment(int requestCode, Intent data) {
//
//        if (requestCode == RequestCode.REQUEST_CODE_TEXT_BARCODE) {
//            edtInventory.setText(data.getStringExtra("SCAN_RESULT_CODE"));
//            edtInventory.setSelection(edtInventory.getText().toString()
//                    .length());
//        } else if (requestCode == RequestCode.REQUEST_CODE_OPEN_ITEM) {
//            String filteredString = data.getStringExtra(KEYS.Catalouge.NOM_CAT);
//            isUserSearching = true;
//            index = 1;
//            adapter.setIndexPosition(index);
//            String filterText = filteredString.replace("|", " > ");
//            relFilterValue.setVisibility(View.VISIBLE);
//            txtFilter.setText(filterText);
//
//            boolean isStockSel = data.getBooleanExtra(KEYS.Catalouge.IS_STOCK, false);
//            boolean isRequestSel = data.getBooleanExtra(KEYS.Catalouge.IS_REQUEST, false);
//
//            isStock = isStockSel;
//            isRequest = isRequestSel;
//
//            adapter.isCategoryFiltered = true;
//            isCategoryFiltered = true;
//            isFiltered = true;
//            edtInventory.setText("");
//
//            filteredCategory = filteredString;
//
//            String filterType = "";
//
//            if (!TextUtils.isEmpty(filteredCategory) && (isStock || isRequest)) {
//                filterType = " - ";
//            }
//            if (isStock || isRequest) {
//                if (isStock && isRequest) {
//                    filterType += synchroteamActivity.getString(R.string.txt_stock);
//                    filterType += " & ";
//                    filterType += synchroteamActivity.getString(R.string.txt_request);
//                } else if (isStock) {
//                    filterType += synchroteamActivity.getString(R.string.txt_stock);
//                } else if (isRequest) {
//                    filterType += synchroteamActivity.getString(R.string.txt_request);
//                }
//
//
//            }
////            else {
////                Filter filterFromCat = adapter.getEditFilter(true);
////                filterFromCat.filter(filteredString);
////            }
//            new FetchPartsAndServicesList().execute();
//
//            txtFilterType.setText(filterType);
//
//        }
//
//    }
//
//    TextWatcher textWatcher = new TextWatcher() {
//
//        @Override
//        public void onTextChanged(CharSequence s, int start, int before,
//                                  int count) {
//
//        }
//
//        @Override
//        public void beforeTextChanged(CharSequence s, int start, int count,
//                                      int after) {
//
//        }
//
//        @Override
//        public void afterTextChanged(Editable s) {
//            /*
//             * if the value is filtered by category and if the edit text is set
//			 * to empty, skip the filtering for first time.
//			 */
//
//            if (!TextUtils.isEmpty(s.toString()) || isCategoryFiltered) {
//                isUserSearching = true;
//            } else {
//                isUserSearching = false;
//            }
//
//            if (!isFiltered) {
//                if (!isCategoryFiltered) {
//                    edittextFilter.filter(s.toString());
//                } else {
//                    filterForCategory.filter(s.toString());
//                }
//            } else {
//                isFiltered = false;
//            }
//
//            // if (!TextUtils.isEmpty(s.toString()) || isCategoryFiltered) {
//            // if (footerView != null
//            // && (footerView.getVisibility() == View.VISIBLE))
//            //
//            // footerView.setVisibility(View.INVISIBLE);
//            //
//            // } else {
//            // if (footerView != null
//            // && (footerView.getVisibility() == View.INVISIBLE))
//            // footerView.setVisibility(View.VISIBLE);
//            // }
//
//        }
//    };
//
//    // ........................................LISTENER...STARTS.............................................
//
//    OnClickListener clickListener = new OnClickListener() {
//
//        @Override
//        public void onClick(View v) {
//            switch (v.getId()) {
//                case R.id.txtBarcodeIcon:
//                    Intent intent = new Intent(synchroteamActivity,
//                            ScannerBar.class);
//                    inventoryFragment.startActivityForResult(intent,
//                            RequestCode.REQUEST_CODE_TEXT_BARCODE);
//                    break;
//
//                case R.id.txtFilterIcon:
//                    Intent i = new Intent(synchroteamActivity,
//                            CatalougeSubCategory.class);
//
//                    // i.putExtra(KEYS.Catalouge.ID_INTER, null);
//                    i.putExtra(KEYS.Catalouge.IS_INVENTORY, "true");
//                    i.putExtra(KEYS.Catalouge.NOM_CAT, "");
//                    i.putExtra(KEYS.Catalouge.INDEX, 0);
//                    inventoryFragment.startActivityForResult(i,
//                            RequestCode.REQUEST_CODE_OPEN_ITEM);
//                    break;
//                case R.id.txt_close_filter:
//                    relFilterValue.setVisibility(View.GONE);
//                    index = 1;
//                    adapter.setIndexPosition(index);
//                    isUserSearching = false;
//                    isCategoryFiltered = false;
//                    edittextFilter = adapter.getEditFilter(false);
//                    edtInventory.setText("");
//
//                    filteredCategory = "";
//                    isStock = false;
//                    isRequest = false;
//
//                    new FetchPartsAndServicesList().execute();
//
//                    break;
//                // case R.id.txtSpinnerIcon:
//                // spinnerSort.performClick();
//                // break;
//                // case R.id.txtSortDescIcon:
//                // txtSortDescIcon.setVisibility(View.GONE);
//                // txtSortAscIcon.setVisibility(View.VISIBLE);
//                // mDescendingOrder = true;
//                // sortListView();
//                // break;
//                // case R.id.txtSortAscIcon:
//                // mDescendingOrder = false;
//                // txtSortAscIcon.setVisibility(View.GONE);
//                // txtSortDescIcon.setVisibility(View.VISIBLE);
//                // sortListView();
//                // break;
//            }
//        }
//    };
//
//    public void refreshList() {
//        // TODO Auto-generated method stub
//        if (adapter != null) {
//            adapter.notifyDataSetChanged();
//        }
//        new FetchPartsAndServicesList().execute();
//    }
//
//    /**
//     * Updates the list after datas fetched from database.
//     *
//     * @param listData
//     */
//    private void updateList(ArrayList<InventoryItemBeans> listData) {
//        if (inventoryArray == null) {
//            inventoryArray = new ArrayList<InventoryItemBeans>();
//        } else {
//            inventoryArray.clear();
//        }
//
//        inventoryArray.addAll(listData);
//
//        sortByArraylistAsc();
//    }
//
//    public void updateListPartInventory(Bundle data) {
//
//        String filteredString = data.getString(KEYS.Catalouge.NOM_CAT);
//        isUserSearching = true;
//        index = 1;
//        adapter.setIndexPosition(index);
//        String filterText = filteredString.replace("|", " > ");
//        relFilterValue.setVisibility(View.VISIBLE);
//        txtFilter.setText(filterText);
//
//        boolean isStockSel = data.getBoolean(KEYS.Catalouge.IS_STOCK, false);
//        boolean isRequestSel = data.getBoolean(KEYS.Catalouge.IS_REQUEST, false);
//
//        isStock = isStockSel;
//        isRequest = isRequestSel;
//
//        adapter.isCategoryFiltered = true;
//        isCategoryFiltered = true;
//        isFiltered = true;
//        edtInventory.setText("");
//
//        filteredCategory = filteredString;
//
//        String filterType = "";
//
//        if (!TextUtils.isEmpty(filteredCategory) && (isStock || isRequest)) {
//            filterType = " - ";
//        }
//        if (isStock || isRequest) {
//            if (isStock && isRequest) {
//                filterType += synchroteamActivity.getString(R.string.txt_stock);
//                filterType += " & ";
//                filterType += synchroteamActivity.getString(R.string.txt_request);
//            } else if (isStock) {
//                filterType += synchroteamActivity.getString(R.string.txt_stock);
//            } else if (isRequest) {
//                filterType += synchroteamActivity.getString(R.string.txt_request);
//            }
//
//        }
//        new FetchPartsAndServicesList().execute();
//
//        txtFilterType.setText(filterType);
//    }
//
//    // ****************************************LISTENER...ENDS***********************************************
//
//    /**
//     * Fetch the list of parts and services.
//     */
//    private class FetchPartsAndServicesList extends
//            AsyncTask<Void, Void, ArrayList<InventoryItemBeans>> {
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            progressBarInventory.setVisibility(View.VISIBLE);
//            listViewInventory.setVisibility(View.INVISIBLE);
//        }
//
//        @Override
//        protected ArrayList<InventoryItemBeans> doInBackground(Void... params) {
//            ArrayList<InventoryItemBeans> inventoryList = new ArrayList<>();
//            if (!isStock && !isRequest) {
//                inventoryList.addAll(dao.getInventoryList());
//            } else if (isStock && isRequest) {
//                inventoryList.addAll(dao.getStockRequestInventoryList());
//            } else if (isStock) {
//                inventoryList.addAll(dao.getStockInventoryList());
//            } else if (isRequest) {
//                inventoryList.addAll(dao.getRequestInventoryList());
//            }
//            return inventoryList;
//        }
//
//        @Override
//        protected void onPostExecute(ArrayList<InventoryItemBeans> result) {
//            super.onPostExecute(result);
//            updateList(result);
//
//            listViewInventory.removeFooterView(footerView);
//
//            partsCount = inventoryArray.size();
//
//            progressBarInventory.setVisibility(View.GONE);
//            listViewInventory.setVisibility(View.VISIBLE);
//
//            if (partsCount > 20) {
//                listViewInventory.addFooterView(footerView);
//            }
//
//            setInventoryListAdapter();
//
//            adapter.isCategoryFiltered = isCategoryFiltered;
//
////            if (!TextUtils.isEmpty(filteredCategory)) {
//                    Filter filterFromCat = adapter.getEditFilter(true);
//                    filterFromCat.filter(filteredCategory);
////            }
//            EventBus.getDefault().post(new EnableSynchronizationAddJobEvent());
//        }
//
//    }
//
//}
