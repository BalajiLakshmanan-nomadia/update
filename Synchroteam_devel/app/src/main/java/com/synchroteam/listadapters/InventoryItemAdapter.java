package com.synchroteam.listadapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.synchroteam.TypefaceLibrary.EditText;
import com.synchroteam.TypefaceLibrary.TextView;
import com.synchroteam.beans.InventoryDialogSerialNumber;
import com.synchroteam.beans.InventorySerialNumbersBeans;
import com.synchroteam.beans.InventorySingleItemBeans;
import com.synchroteam.dao.Dao;
import com.synchroteam.synchroteam.InventoryDetails;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.utils.AccentInsensitive;
import com.synchroteam.scanner.CodeScannerActivity;
import com.synchroteam.utils.AsyncTaskCoroutine;
import com.synchroteam.utils.DaoManager;
import com.synchroteam.utils.DialogUtils;
import com.synchroteam.utils.RequestCode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

public class InventoryItemAdapter extends ArrayAdapter<String> implements
        StickyListHeadersAdapter {

    /* -- Adapter elements -- */
    private static Context context;
    private static ArrayList<InventorySingleItemBeans> inventoryArray;
    private static Dao dao;
    //	private View dialogView;
    private static Typeface typeFace;
    private static ArrayList<String> listSelected;
    private static ArrayList<String> listSelectedSeial;
    public static RelativeLayout relSerialConatainer;
    private ArrayList<InventorySerialNumbersBeans> inventorySerialNos;
    private static List<InventoryDialogSerialNumber> listArrayFrom;
    private static String idPieceDemande;
    private static Calendar calendar;
    private static SimpleDateFormat sdf;
    private static int selectedPos;
    private static int userId;
    private long mLastClickTime = 0;

    // private static final String TAG = "InventoryItemAdapter";

    public InventoryItemAdapter(Context context, int resource,
                                StickyListHeadersListView list,
                                ArrayList<InventorySingleItemBeans> inventoryArray, int userId) {
        super(context, resource);
        this.context = context;
        dao = DaoManager.getInstance();
        this.inventoryArray = inventoryArray;
        this.userId = userId;

        calendar = Calendar.getInstance();
        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    }

    @Override
    public long getHeaderId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ListViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.rowview_single_inventory_item, parent, false);
            holder = new ListViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ListViewHolder) convertView.getTag();
        }

        holder.txtDepot.setText(inventoryArray.get(position).getDepot());
        holder.txtAction.setText(inventoryArray.get(position).getAction());
        holder.txtQty.setText(inventoryArray.get(position).getQty());

        if (Integer.parseInt(inventoryArray.get(position).getFlUrgent()) == 1) {
            holder.linItemContainer.setBackgroundResource(R.drawable.boxframe_textview_layout_red);
        } else {
            holder.linItemContainer.setBackgroundResource(R.drawable.boxframe_textview_layout);
        }

        String action = inventoryArray.get(position).getAction();

        //V49 changes
//        //new changes hiding settings icon for request inventory
//        if (action.equalsIgnoreCase(context.getString(R.string.txt_take_from_label))) {
//            holder.relEditStock.setVisibility(View.GONE);
//        } else {
//            holder.relEditStock.setVisibility(View.VISIBLE);
//        }


        holder.txtEditIcon.setTag(position);
        holder.txtEditIcon.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                // preventing double click.
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }

                mLastClickTime = SystemClock.elapsedRealtime();

                int pos = (int) v.getTag();

                selectedPos = pos;

                idPieceDemande = inventoryArray.get(pos).getIdPieceDemande();
                inventorySerialNos = dao.getDepotSerialNumbers(inventoryArray
                        .get(pos).getIdStock(), Integer.parseInt(inventoryArray
                        .get(pos).getIdPiece()));

                String action = inventoryArray.get(pos).getAction();
                int isRestock;
                if (action.equalsIgnoreCase(context
                        .getString(R.string.txt_take_from_label))) {
                    isRestock = 1;
                } else {
                    isRestock = 0;
                }

                listArrayFrom = new ArrayList<InventoryDialogSerialNumber>();

                for (int i = 0; i < inventorySerialNos.size(); i++) {
                    InventoryDialogSerialNumber h = new InventoryDialogSerialNumber();
                    h.setId(i + 1);
                    h.setName(inventorySerialNos.get(i).getSerialNumber());
                    h.setSelected(false);

                    //new changes
                    h.setIdPieceSerial(inventorySerialNos.get(i).getIdSerialNumber());
                    h.setStockId(inventorySerialNos.get(i).getIdStock());
                    h.setIdPiece(inventorySerialNos.get(i).getIdPiece());

                    if (TextUtils.isEmpty(inventorySerialNos.get(i)
                            .getIdInterv()) || inventorySerialNos.get(i).getDateUsed() == null) {
                        listArrayFrom.add(h);
                    }
                }

                if (inventoryArray.get(pos).getIsSerializable() == 0) {

                    SerialNumberNonSerializableDialog dialog = SerialNumberNonSerializableDialog.getInstance(
                            isRestock, inventoryArray.get(pos).getQty(),
                            inventoryArray.get(pos).getDepot());
                    dialog.show(((AppCompatActivity) getContext())
                            .getSupportFragmentManager(), "");
                } else {
                    if (listArrayFrom.size() == 0) {
                        Toast.makeText(getContext().getApplicationContext(),
                                getContext().getString(R.string.txt_no_parts),
                                Toast.LENGTH_SHORT).show();
                    } else {
                        SerialNumberDialog dialog = SerialNumberDialog.getInstance(
                                isRestock, inventoryArray.get(pos).getQty(),
                                inventoryArray.get(pos).getDepot());
                        dialog.show(((AppCompatActivity) getContext())
                                .getSupportFragmentManager(), "");
                        listSelected = new ArrayList<String>();
                        listSelectedSeial=new ArrayList<>();
                    }
                }
            }

        });
        /*
         * if the value is not serializable hide the serial number.
         */
        // if (inventoryArray.get(position).getIsSerializable() == 0) {
        // holder.linSerialNumberView.setVisibility(View.GONE);
        // holder.dividerLine.setVisibility(View.GONE);
        // }
        return convertView;
    }

    @Override
    public int getCount() {
        return inventoryArray.size();
    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        final HeaderViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.header,
                    parent, false);
            holder = new HeaderViewHolder(convertView);
            convertView.setTag(holder);

        } else {
            holder = (HeaderViewHolder) convertView.getTag();
        }
        holder.txtHeader.setText(context.getResources().getString(
                R.string.header_text));

        return convertView;
    }

    class ListViewHolder {
        TextView txtQty, txtDepot, txtAction, txtSerialNumbers;
        android.widget.TextView txtEditIcon;
        LinearLayout linItemContainer;
        RelativeLayout relEditStock;

        // LinearLayout linSerialNumberView;
        // View dividerLine;

        public ListViewHolder(View view) {
            txtQty = (TextView) view.findViewById(R.id.txt_qty_item);
            txtDepot = (TextView) view.findViewById(R.id.txt_depot_item);
            txtAction = (TextView) view.findViewById(R.id.txt_action_item);
            txtSerialNumbers = (TextView) view
                    .findViewById(R.id.txtSerialNumbers);
            txtEditIcon = (android.widget.TextView) view
                    .findViewById(R.id.txtEditItem);
            relEditStock = (RelativeLayout) view.findViewById(R.id.relEditStock);
            linItemContainer = (LinearLayout) view.findViewById(R.id.lin_item_container);
            // linSerialNumberView = (LinearLayout) view
            // .findViewById(R.id.linSerialNoView);
            // dividerLine = (View) view.findViewById(R.id.viewSerialNoDivider);

            Typeface typeFace = Typeface.createFromAsset(
                    context.getAssets(),
                    context.getResources().getString(
                            R.string.fontName_fontAwesome));
            txtEditIcon.setTypeface(typeFace);
        }
    }

    class HeaderViewHolder {
        android.widget.TextView txtHeader;

        public HeaderViewHolder(View view) {

            initiateUi(view);

        }

        private void initiateUi(View view) {
            txtHeader = (android.widget.TextView) view
                    .findViewById(R.id.txtHeader);

        }

    }

    // **********************************************DIALOG...FRAGMENT...CLASS***************************************************

    // -------------------------------------------------SERIALIZABLE---DIALOG-------------------------------------------
    @SuppressLint("ValidFragment")
    public static class SerialNumberDialog extends DialogFragment {

        private static final String KEY_QTY = "qty";
        private static final String KEY_IS_RESTOCK = "is_restock";
        private static final String KEY_DEPOT_NAME = "depot_name";

        EditText edtSearch;
        View dView;
        private static SerialNumberAdapter adapter;

        public static SerialNumberDialog getInstance(int isRestock, String qty, String depotName) {
            Bundle bundle = new Bundle();
            bundle.putInt(KEY_IS_RESTOCK, isRestock);
            bundle.putString(KEY_QTY, qty);
            bundle.putString(KEY_DEPOT_NAME, depotName);
            SerialNumberDialog dialog = new SerialNumberDialog();
            dialog.setArguments(bundle);
            return dialog;
        }

        /*
         * when dismissed invoke the listener to notify that some items are
         * selected.
         */
        @Override
        public void onDismiss(DialogInterface dialog) {
            super.onDismiss(dialog);
            if (adapter != null)
                adapter.notifyDataSetChanged();

            // listener.onItemsSelected(listSelected);
        }

        /*
         * sets the height of the dialog to 1/7th of the total screen height.
         */
        @Override
        public void onResume() {
            super.onResume();
            DisplayMetrics metrics = getResources().getDisplayMetrics();
            int screenHeight = metrics.heightPixels;
            int dialogHeight = (int) (screenHeight * 0.8);
            getDialog().getWindow().setLayout(
                    ViewGroup.LayoutParams.WRAP_CONTENT, dialogHeight);
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Dialog dialog = super.onCreateDialog(savedInstanceState);
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setBackgroundDrawable(
                    new ColorDrawable(Color.TRANSPARENT));
            WindowManager.LayoutParams wmlp = dialog.getWindow()
                    .getAttributes();
            wmlp.gravity = Gravity.CENTER;
            return dialog;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View dialogView = inflater.inflate(
                    R.layout.dialog_serial_number_serializable, container,
                    false);
            typeFace = Typeface.createFromAsset(getActivity().getAssets(),
                    getActivity().getString(R.string.fontName_fontAwesome));

            setCancelable(false);
            return dialogView;
        }

        @Override
        public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            dView = view;
            initializeUI(view);
        }

        /**
         * To fetch the values from barcode scanner class.
         *
         * @param requestCode
         * @param resultCode
         * @param data
         */
        @Override
        public void onActivityResult(int requestCode, int resultCode,
                                     Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if (resultCode == Activity.RESULT_OK) {

                edtSearch.setText(data.getStringExtra("SCAN_RESULT_CODE"));
                edtSearch.setSelection(edtSearch.getText().length());

                // InventoryDialogSerialNumber serialModel = new
                // InventoryDialogSerialNumber();
                // serialModel.setId(listArrayFrom.size() + 1);
                // serialModel.setName(data.getStringExtra("SCAN_RESULT_CODE"));
                // serialModel.setSelected(true);
                // listArrayFrom.add(serialModel);
                // listSelected.add(data.getStringExtra("SCAN_RESULT_CODE"));
                // adapter.notifyDataSetChanged();
                //
                // generateTextView(listSelected);

            }
        }

        private void initializeUI(View dialogView) {
            final ListView listView = (ListView) dialogView
                    .findViewById(R.id.listSerialNos);
            listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
            listView.setFastScrollEnabled(false);
            // listView.setVisibility(View.GONE);
            adapter = new SerialNumberAdapter(getActivity(), listArrayFrom);
            listView.setAdapter(adapter);

            android.widget.TextView txtRestockIcon = (android.widget.TextView) dialogView
                    .findViewById(R.id.txtRestockIcon);
            android.widget.TextView txtReturnIcon = (android.widget.TextView) dialogView
                    .findViewById(R.id.txtReturnIcon);
            RelativeLayout relRestock = (RelativeLayout) dialogView
                    .findViewById(R.id.relRestockLabel);
            RelativeLayout relReturn = (RelativeLayout) dialogView
                    .findViewById(R.id.relReturnLabel);
            android.widget.TextView txtSpinnerIcon = (android.widget.TextView) dialogView
                    .findViewById(R.id.txtSearchIcon);
            android.widget.TextView txtBarcodeIcon = (android.widget.TextView) dialogView
                    .findViewById(R.id.txtBarcodeIcon);
            TextView txtConfirm = (TextView) dialogView
                    .findViewById(R.id.txtConfirm);
            TextView txtClose = (TextView) dialogView
                    .findViewById(R.id.txtClose);
            TextView txtQty = (TextView) dialogView
                    .findViewById(R.id.txt_qty_item);
            TextView txtDepot = (TextView) dialogView
                    .findViewById(R.id.txt_depot_item);
            edtSearch = (EditText) dialogView
                    .findViewById(R.id.edtSearchSerialNos);

            txtRestockIcon.setTypeface(typeFace);
            txtReturnIcon.setTypeface(typeFace);
            txtSpinnerIcon.setTypeface(typeFace);
            txtBarcodeIcon.setTypeface(typeFace);

            int isRestock = getArguments().getInt(KEY_IS_RESTOCK);
            String qty = getArguments().getString(KEY_QTY);
            String depotName = getArguments().getString(KEY_DEPOT_NAME);

            //new changes hide confirm for request restock
            if (isRestock == 1) {
                relRestock.setVisibility(View.VISIBLE);
                relReturn.setVisibility(View.INVISIBLE);
//                txtConfirm.setVisibility(View.GONE);
            } else {
                relRestock.setVisibility(View.INVISIBLE);
                relReturn.setVisibility(View.VISIBLE);
//                txtConfirm.setVisibility(View.VISIBLE);
            }

            txtQty.setText(qty);
            txtDepot.setText(depotName);

            edtSearch.addTextChangedListener(watcher);

            txtConfirm.setOnClickListener(clickListener);

            txtClose.setOnClickListener(clickListener);

            txtBarcodeIcon.setOnClickListener(clickListener);
        }

        /*
         * Text watcher for edit text
         */
        TextWatcher watcher = new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                adapter.getFilter().filter(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        /*
         * click listener
         */
        OnClickListener clickListener = new OnClickListener() {

            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.txtConfirm:
                        if (Integer.parseInt(inventoryArray.get(selectedPos)
                                .getQty()) == listSelected.size()) {
                            UpdateCompletedDate updateAsyn = new UpdateCompletedDate();
                            updateAsyn.execute();
                            dismiss();
                        } else {
                            Toast.makeText(
                                    getActivity(),
                                    context.getResources().getString(
                                            R.string.txt_validate_qty),
                                    Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case R.id.txtClose:
                        dismiss();
                        break;
                    case R.id.txtBarcodeIcon:
                        Intent intent = new Intent(getActivity(), CodeScannerActivity.class);
                        startActivityForResult(intent,
                                RequestCode.REQUEST_CODE_TEXT_BARCODE);
                        break;
                    default:
                        break;
                }
            }
        };

        public class SerialNumberAdapter extends BaseAdapter implements Filterable {

            List<InventoryDialogSerialNumber> arrayList;
            List<InventoryDialogSerialNumber> mOriginalValues; // Original Values
            LayoutInflater inflater;

            public SerialNumberAdapter(Context context,
                                       List<InventoryDialogSerialNumber> arrayList) {
                this.arrayList = arrayList;
                inflater = LayoutInflater.from(context);
                // generate the textview for first time
                // generateTextView(listSelected);
            }

            @Override
            public int getCount() {
                return arrayList.size();
            }

            @Override
            public Object getItem(int position) {
                return position;
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            private class ViewHolder {
                com.synchroteam.TypefaceLibrary.TextView textView;
                android.widget.TextView txtSelectIcon;
            }

            @Override
            public View getView(final int position, View convertView,
                                ViewGroup parent) {

                ViewHolder holder = null;

                if (convertView == null) {

                    holder = new ViewHolder();
                    convertView = inflater.inflate(
                            R.layout.alert_dialog_listview_search_subview, null);
                    holder.textView = (com.synchroteam.TypefaceLibrary.TextView) convertView
                            .findViewById(R.id.alertTextView);
                    holder.txtSelectIcon = (android.widget.TextView) convertView
                            .findViewById(R.id.txtItemSelect);

                    holder.txtSelectIcon.setTypeface(typeFace);

                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }

                final InventoryDialogSerialNumber data = arrayList.get(position);

                holder.textView.setText(data.getName());
                if (data.isSelected()) {
                    holder.txtSelectIcon.setVisibility(View.VISIBLE);
                } else {
                    holder.txtSelectIcon.setVisibility(View.INVISIBLE);
                }
                convertView.setOnClickListener(new View.OnClickListener() {
                    @SuppressWarnings("deprecation")
                    public void onClick(View v) {

                        int len = arrayList.size();

                        for (int i = 0; i < len; i++) {
                            if (i == position) {
                                data.setSelected(!data.isSelected());
                                if (data.isSelected()) {
                                    listSelected.add(data.getName());
                                    listSelectedSeial.add(data.getIdPieceSerial());
                                } else {
                                    listSelected.remove(data.getName());
                                    listSelectedSeial.remove(data.getIdPieceSerial());
                                }
                                break;
                            }
                        }

                        // generate the textview after selected
                        generateTextView(listSelected);

                        ViewHolder temp = (ViewHolder) v.getTag();
                        if (data.isSelected()) {
                            temp.txtSelectIcon.setVisibility(View.INVISIBLE);
                        } else {
                            temp.txtSelectIcon.setVisibility(View.VISIBLE);
                        }

                    }
                });

                holder.txtSelectIcon.setTag(holder);

                return convertView;
            }

            @SuppressLint("DefaultLocale")
            @Override
            public Filter getFilter() {
                Filter filter = new Filter() {

                    @SuppressWarnings("unchecked")
                    @Override
                    protected void publishResults(CharSequence constraint,
                                                  FilterResults results) {

                        arrayList = (List<InventoryDialogSerialNumber>) results.values; // has
                        // the
                        // filtered
                        // values
                        notifyDataSetChanged(); // notifies the data with new
                        // filtered values
                    }

                    @Override
                    protected FilterResults performFiltering(CharSequence constraint) {
                        FilterResults results = new FilterResults(); // Holds the
                        // results
                        // of a
                        // filtering
                        // operation
                        // in values
                        List<InventoryDialogSerialNumber> FilteredArrList = new ArrayList<InventoryDialogSerialNumber>();

                        if (mOriginalValues == null) {
                            mOriginalValues = new ArrayList<InventoryDialogSerialNumber>(
                                    arrayList); // saves the original data in
                            // mOriginalValues
                        }

                        /********
                         *
                         * If constraint(CharSequence that is received) is null
                         * returns the mOriginalValues(Original) values else does
                         * the Filtering and returns FilteredArrList(Filtered)
                         *
                         ********/
                        if (constraint == null || constraint.length() == 0) {

                            // set the Original result to return
                            results.count = mOriginalValues.size();
                            results.values = mOriginalValues;
                        } else {
                            constraint = constraint.toString().toLowerCase();
                            for (int i = 0; i < mOriginalValues.size(); i++) {
                                String data = mOriginalValues.get(i).getName();

                                // remove all the accented characters before search.
                                String asciiSearchString = AccentInsensitive
                                        .removeAccentCase(data);
                                String asciiConstraint = AccentInsensitive
                                        .removeAccentCase(constraint.toString());
                                if (asciiConstraint != null && asciiConstraint.length() > 0)
                                    if (asciiSearchString.toLowerCase().contains(
                                            asciiConstraint.toString())) {
                                        FilteredArrList.add(mOriginalValues.get(i));
                                    }
                            }
                            // set the Filtered result to return
                            results.count = FilteredArrList.size();
                            results.values = FilteredArrList;
                        }
                        return results;
                    }
                };
                return filter;
            }
        }

        /*
         * Method to generate textview dynamically and add it to the relative
         * layout. If the textview have the size to fit into first row, it will
         * added or else will added to the next row.
         */
        private void generateTextView(final ArrayList<String> listSel) {

            relSerialConatainer = (RelativeLayout) dView
                    .findViewById(R.id.relSerialContainer);
            relSerialConatainer.removeAllViews();

            for (int i = 0; i < listSel.size(); i++) {

                TextView tv = new TextView(getContext());
                Typeface typeFaceAvenir = Typeface.createFromAsset(
                        getContext().getAssets(),
                        getContext().getResources().getString(
                                R.string.fontName_avenir));
                tv.setText(listSel.get(i));
                tv.setBackgroundDrawable(getContext().getResources().getDrawable(
                        R.drawable.boxframe_serial_not_text));

                tv.setId(i + 1);
                tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, getActivity().getResources()
                        .getDimension(R.dimen.textSizeDaysTv));
                tv.setSingleLine(true);
                tv.setPadding(10, 5, 10, 5);
                tv.setTypeface(typeFaceAvenir);
                relSerialConatainer.setVisibility(View.INVISIBLE);
                relSerialConatainer.addView(tv);
            }

            relSerialConatainer.post(new Runnable() {
                @Override
                public void run() {
                    int totalWidth = relSerialConatainer.getWidth();

                    // loop through each text view, and set its layout
                    // params
                    for (int i = 0; i < listSel.size(); i++) {
                        View child = relSerialConatainer.getChildAt(i);
                        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                                ViewGroup.LayoutParams.WRAP_CONTENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT);
                        params.setMargins(10, 10, 0, 0);
                        // this text view can fit in the same row so
                        // lets place it relative to the previous one.
                        if (child.getWidth() < totalWidth) {

                            if (i > 0) { // i == 0 is in correct
                                // position
                                params.addRule(RelativeLayout.RIGHT_OF,
                                        relSerialConatainer.getChildAt(i - 1)
                                                .getId());
                                params.addRule(RelativeLayout.ALIGN_BOTTOM,
                                        relSerialConatainer.getChildAt(i - 1)
                                                .getId());
                            }
                        } else {
                            // place it in the next row.
                            totalWidth = relSerialConatainer.getWidth();
                            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                            params.addRule(RelativeLayout.BELOW,
                                    relSerialConatainer.getChildAt(i - 1).getId());
                        }
                        child.setLayoutParams(params);
                        totalWidth = totalWidth - child.getWidth() - 10;
                    }
                    relSerialConatainer.setVisibility(View.VISIBLE);
                    relSerialConatainer.requestLayout();
                }
            });
            if (adapter != null)
                adapter.notifyDataSetChanged();
        }

        // ****************************************************ASYN...CLASS...STARTS...HERE********************************************

        /**
         * Async task to update the completed date of a pending request.
         */
        private class UpdateCompletedDate extends AsyncTaskCoroutine<String, Boolean> {

            @Override
            public void onPreExecute() {
                super.onPreExecute();
            }

            @SuppressLint("SimpleDateFormat")
            @Override
            public Boolean doInBackground(String... params) {
                String currentTime = sdf.format(calendar.getTime());
                boolean drp = dao.updateCompletedDate(idPieceDemande, currentTime);
                return drp;
            }

            @Override
            public void onPostExecute(Boolean result) {
                super.onPostExecute(result);
                DialogUtils.dismissProgressDialog();
                boolean drp = result.booleanValue();
                if (drp) {
                    AddInventoryPiece addPiece = new AddInventoryPiece();
                    addPiece.execute();
                } else
                    Toast.makeText(context.getApplicationContext(),
                            context.getString(R.string.msg_error),
                            Toast.LENGTH_SHORT).show();
            }
        }

        private class AddInventoryPiece extends AsyncTaskCoroutine<String, Boolean> {

            @Override
            public void onPreExecute() {
                super.onPreExecute();
            }

            @SuppressLint("SimpleDateFormat")
            @Override
            public Boolean doInBackground(String... params) {

                String currentTime = sdf.format(calendar.getTime());
                boolean drp = dao.addInventoryTransfer(
                        inventoryArray.get(selectedPos).getIdStock(),
                        inventoryArray.get(selectedPos).getIdStockDestination(),
                        Integer.parseInt(inventoryArray.get(selectedPos)
                                .getIdPiece()), userId,
                        Integer.parseInt(inventoryArray.get(selectedPos).getQty()),
                        1, Integer.parseInt(inventoryArray.get(selectedPos).getFlUrgent()), currentTime);
                return drp;
            }

            @Override
            public void onPostExecute(Boolean result) {
                super.onPostExecute(result);
                DialogUtils.dismissProgressDialog();
                boolean drp = result.booleanValue();
                if (drp) {
                    if (inventoryArray.get(selectedPos).getIsSerializable() == 0) {
                        Toast.makeText(context.getApplicationContext(),
                                context.getString(R.string.txt_ack_msg),
                                Toast.LENGTH_SHORT).show();
                        ((InventoryDetails) context).setListAdapter();
                    } else {
                        UpdateSerialNumberId updateSerial = new UpdateSerialNumberId();
                        updateSerial.execute();
                    }
                } else
                    Toast.makeText(context.getApplicationContext(),
                            context.getString(R.string.msg_error),
                            Toast.LENGTH_SHORT).show();
            }
        }

        /**
         * Async task to update the serial number's stock id before an item is
         * transfered/returned.
         */
        private class UpdateSerialNumberId extends AsyncTaskCoroutine<String, Boolean> {

            @Override
            public void onPreExecute() {
                super.onPreExecute();
            }

            @SuppressLint("SimpleDateFormat")
            @Override
            public Boolean doInBackground(String... params) {
                boolean isChanged = true;
                List<String> serialNoList;
                List<String> serialPieceNoList;
                String idStock;
                serialNoList = listSelected;
                serialPieceNoList=listSelectedSeial;
                idStock = inventoryArray.get(selectedPos).getIdStockDestination();
                int idPiece=Integer.parseInt(inventoryArray.get(selectedPos).getIdPiece());
                for (int i = 0; i < serialNoList.size(); i++) {
                    boolean drp = dao
                            .updateSerialNoId(idStock, serialNoList.get(i),
                                    idPiece,serialPieceNoList.get(i));
                    if (!drp)
                        isChanged = false;
                }
                return isChanged;
            }

            @Override
            public void onPostExecute(Boolean result) {
                super.onPostExecute(result);
                DialogUtils.dismissProgressDialog();
                boolean drp = result.booleanValue();
                if (drp) {
                    ((InventoryDetails) context).setListAdapter();
                    ((InventoryDetails) context).setSpinnerAdapter();
                    Toast.makeText(context.getApplicationContext(),
                            context.getString(R.string.txt_ack_msg),
                            Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(context.getApplicationContext(),
                            context.getString(R.string.msg_error),
                            Toast.LENGTH_SHORT).show();
            }
        }

        // ****************************************************ASYN...CLASS...ENDS...HERE********************************************
    }

    // -------------------------------------------------SERIALIZABLE---DIALOG-------------------------------------------

    // -------------------------------------------------NON--SERIALIZABLE---DIALOG-------------------------------------------

    @SuppressLint("ValidFragment")
    public static class SerialNumberNonSerializableDialog extends DialogFragment {

        private static final String KEY_QTY = "qty";
        private static final String KEY_IS_RESTOCK = "is_restock";
        private static final String KEY_DEPOT_NAME = "depot_name";

        public static SerialNumberNonSerializableDialog getInstance(int isRestock, String qty,
                                                                    String depot) {
            Bundle bundle = new Bundle();
            bundle.putInt(KEY_IS_RESTOCK, isRestock);
            bundle.putString(KEY_QTY, qty);
            bundle.putString(KEY_DEPOT_NAME, depot);
            SerialNumberNonSerializableDialog dialog = new SerialNumberNonSerializableDialog();
            dialog.setArguments(bundle);
            return dialog;
        }

        /*
         * sets the width of the dialog to 1/7th of the total screen height.
         */
        @Override
        public void onResume() {
            super.onResume();
            DisplayMetrics metrics = getResources().getDisplayMetrics();
            int screenHeight = metrics.widthPixels;
            int dialogHeight = (int) (screenHeight * 0.8);
            getDialog().getWindow().setLayout(dialogHeight,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Dialog dialog = super.onCreateDialog(savedInstanceState);
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setBackgroundDrawable(
                    new ColorDrawable(Color.TRANSPARENT));
            WindowManager.LayoutParams wmlp = dialog.getWindow()
                    .getAttributes();
            wmlp.gravity = Gravity.CENTER;
            return dialog;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View dialogView = inflater.inflate(
                    R.layout.dialog_serial_number_nonserializable, container,
                    false);
            typeFace = Typeface.createFromAsset(getActivity().getAssets(),
                    getActivity().getString(R.string.fontName_fontAwesome));
            setCancelable(false);
            return dialogView;
        }

        @Override
        public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            initializeUI(view);
        }

        private void initializeUI(View dialogView) {

            android.widget.TextView txtRestockIcon = (android.widget.TextView) dialogView
                    .findViewById(R.id.txtRestockIcon);
            android.widget.TextView txtReturnIcon = (android.widget.TextView) dialogView
                    .findViewById(R.id.txtReturnIcon);
            RelativeLayout relRestock = (RelativeLayout) dialogView
                    .findViewById(R.id.relRestockLabel);
            RelativeLayout relReturn = (RelativeLayout) dialogView
                    .findViewById(R.id.relReturnLabel);
            TextView txtQty = (TextView) dialogView
                    .findViewById(R.id.txt_qty_item);
            TextView txtDepot = (TextView) dialogView
                    .findViewById(R.id.txt_depot_item);
            TextView txtConfirm = (TextView) dialogView
                    .findViewById(R.id.txtConfirm);
            TextView txtClose = (TextView) dialogView
                    .findViewById(R.id.txtClose);

            txtRestockIcon.setTypeface(typeFace);
            txtReturnIcon.setTypeface(typeFace);

            int isRestock = getArguments().getInt(KEY_IS_RESTOCK);
            String qty = getArguments().getString(KEY_QTY);
            String depot = getArguments().getString(KEY_DEPOT_NAME);
            //new changes hide confirm for request restock
            if (isRestock == 1) {
                relRestock.setVisibility(View.VISIBLE);
                relReturn.setVisibility(View.INVISIBLE);
//                txtConfirm.setVisibility(View.GONE);
            } else {
                relRestock.setVisibility(View.INVISIBLE);
                relReturn.setVisibility(View.VISIBLE);
//                txtConfirm.setVisibility(View.VISIBLE);
            }

            txtQty.setText(qty);
            txtDepot.setText(depot);

            txtConfirm.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    UpdateCompletedDate updateAsyn = new UpdateCompletedDate();
                    updateAsyn.execute();
                    dismiss();

                }
            });

            txtClose.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
        }

        // ****************************************************ASYN...CLASS...STARTS...HERE********************************************

        /**
         * Async task to update the completed date of a pending request.
         */
        private class UpdateCompletedDate extends AsyncTaskCoroutine<String, Boolean> {

            @Override
            public void onPreExecute() {
                super.onPreExecute();
            }

            @SuppressLint("SimpleDateFormat")
            @Override
            public Boolean doInBackground(String... params) {
                String currentTime = sdf.format(calendar.getTime());
                boolean drp = dao.updateCompletedDate(idPieceDemande, currentTime);
                return drp;
            }

            @Override
            public void onPostExecute(Boolean result) {
                super.onPostExecute(result);
                DialogUtils.dismissProgressDialog();
                boolean drp = result.booleanValue();
                if (drp) {
                    AddInventoryPiece addPiece = new AddInventoryPiece();
                    addPiece.execute();
                } else
                    Toast.makeText(context.getApplicationContext(),
                            context.getString(R.string.msg_error),
                            Toast.LENGTH_SHORT).show();
            }
        }

        private class AddInventoryPiece extends AsyncTaskCoroutine<String, Boolean> {

            @Override
            public void onPreExecute() {
                super.onPreExecute();
            }

            @SuppressLint("SimpleDateFormat")
            @Override
            public Boolean doInBackground(String... params) {

                String currentTime = sdf.format(calendar.getTime());
                boolean drp = dao.addInventoryTransfer(
                        inventoryArray.get(selectedPos).getIdStock(),
                        inventoryArray.get(selectedPos).getIdStockDestination(),
                        Integer.parseInt(inventoryArray.get(selectedPos)
                                .getIdPiece()), userId,
                        Integer.parseInt(inventoryArray.get(selectedPos).getQty()),
                        1, Integer.parseInt(inventoryArray.get(selectedPos).getFlUrgent()), currentTime);
                return drp;
            }

            @Override
            public void onPostExecute(Boolean result) {
                super.onPostExecute(result);
                DialogUtils.dismissProgressDialog();
                boolean drp = result.booleanValue();
                if (drp) {
                    if (inventoryArray.get(selectedPos).getIsSerializable() == 0) {
                        Toast.makeText(context.getApplicationContext(),
                                context.getString(R.string.txt_ack_msg),
                                Toast.LENGTH_SHORT).show();
                        ((InventoryDetails) context).setListAdapter();
                    } else {
                        UpdateSerialNumberId updateSerial = new UpdateSerialNumberId();
                        updateSerial.execute();
                    }
                } else
                    Toast.makeText(context.getApplicationContext(),
                            context.getString(R.string.msg_error),
                            Toast.LENGTH_SHORT).show();
            }
        }

        /**
         * Async task to update the serial number's stock id before an item is
         * transfered/returned.
         */
        private class UpdateSerialNumberId extends AsyncTaskCoroutine<String, Boolean> {

            @Override
            public void onPreExecute() {
                super.onPreExecute();
            }

            @SuppressLint("SimpleDateFormat")
            @Override
            public Boolean doInBackground(String... params) {
                boolean isChanged = true;
                List<String> serialNoList;
                List<String> serialPieceNoList;
                String idStock;
                serialNoList = listSelected;
                serialPieceNoList=listSelectedSeial;
                idStock = inventoryArray.get(selectedPos).getIdStockDestination();
                int idPiece=Integer.parseInt(inventoryArray.get(selectedPos).getIdPiece());
                for (int i = 0; i < serialNoList.size(); i++) {
                    boolean drp = dao
                            .updateSerialNoId(idStock, serialNoList.get(i),
                                    idPiece,serialPieceNoList.get(i));
                    if (!drp)
                        isChanged = false;
                }
                return isChanged;
            }

            @Override
            public void onPostExecute(Boolean result) {
                super.onPostExecute(result);
                DialogUtils.dismissProgressDialog();
                boolean drp = result.booleanValue();
                if (drp) {
                    ((InventoryDetails) context).setListAdapter();
                    ((InventoryDetails) context).setSpinnerAdapter();
                    Toast.makeText(context.getApplicationContext(),
                            context.getString(R.string.txt_ack_msg),
                            Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(context.getApplicationContext(),
                            context.getString(R.string.msg_error),
                            Toast.LENGTH_SHORT).show();
            }
        }

        // ****************************************************ASYN...CLASS...ENDS...HERE********************************************
    }

    // -------------------------------------------------NON--SERIALIZABLE---DIALOG-------------------------------------------

    // **********************************************DIALOG...FRAGMENT...CLASS***************************************************

    // *********************************************LIST...VIEW...ADAPTER*******************************************************


    // *********************************************LIST...VIEW...ADAPTER*******************************************************


}
