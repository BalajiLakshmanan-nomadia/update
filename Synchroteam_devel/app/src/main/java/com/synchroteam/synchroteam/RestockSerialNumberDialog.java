package com.synchroteam.synchroteam;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.SystemClock;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.synchroteam.TypefaceLibrary.Button;
import com.synchroteam.TypefaceLibrary.EditText;
import com.synchroteam.beans.InventoryDialogSerialNumber;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.utils.AccentInsensitive;
import com.synchroteam.scanner.CodeScannerActivity;
import com.synchroteam.utils.RequestCode;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Trident
 *         <p>
 *         Custom class for default button. When the button is clicked, it shows
 *         a dialog fragment. Where the user can select the required number of
 *         serial numbers for <b> Restock </b> and then navigate back to the
 *         previous activity.
 *         </p>
 */
public class RestockSerialNumberDialog extends Button {

    private Context context;
    private static List<InventoryDialogSerialNumber> items;
    private static RestockSerialNumberListener listener;
    static SerialNumberAdapter adapter;
    public static View dialogView;
    public static ScrollView scrollContainer;
    public static RelativeLayout relSerialConatainer;
    private static ArrayList<String> listSelected;
    private static ArrayList<String> listSelectedPiece;
    private static Typeface typeFace;
    private long mLastClickTime = 0;

    public RestockSerialNumberDialog(Context context) {
        super(context);
        this.context = context;
    }

    public RestockSerialNumberDialog(Context arg0, AttributeSet arg1) {
        super(arg0, arg1);
    }

    /*
     * Shows a dialog fragment.
     */
    @Override
    public boolean performClick() {

        // preventing double click.
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return false;
        }

        mLastClickTime = SystemClock.elapsedRealtime();

        if (items == null) {
            Toast.makeText(getContext().getApplicationContext(),
                           getContext().getString(R.string.txt_select_one_depot),
                           Toast.LENGTH_SHORT).show();
        } else {
            if (items.size() == 0) {
                Toast.makeText(getContext().getApplicationContext(),
                               getContext().getString(R.string.txt_no_parts),
                               Toast.LENGTH_SHORT).show();
            } else {
                SerialNumberDialog dialog = SerialNumberDialog.getInstance();
                dialog.show(((AppCompatActivity) getContext())
                                    .getSupportFragmentManager(), "");
                listSelected = new ArrayList<String>();
                listSelectedPiece=new ArrayList<>();
                // adding selected values for the first time
                for (int i = 0; i < items.size(); i++) {
                    if (items.get(i).isSelected()) {
                        listSelected.add(items.get(i).getName());
                        listSelectedPiece.add(items.get(i).getIdPieceSerial());
                    }
                }
            }
        }
        return true;
    }

    public void setItems(List<InventoryDialogSerialNumber> items,
                         String allText, int position, RestockSerialNumberListener listener) {

        this.items = items;
        this.listener = listener;

        if (position != -1) {
            items.get(position).setSelected(true);
        }
    }

    /*
     * listener to invoke the event after a serial number was selected.
     */
    public interface RestockSerialNumberListener {
        public void onItemsSelected(List<String> items,List<String> itemsPieceSerial);

    }

    // *********************************************LIST...VIEW...ADAPTER*******************************************************
    public class SerialNumberAdapter extends BaseAdapter implements Filterable {

        List<InventoryDialogSerialNumber> arrayList;
        List<InventoryDialogSerialNumber> mOriginalValues; // Original Values
        LayoutInflater inflater;

        public SerialNumberAdapter(Context context,
                                   List<InventoryDialogSerialNumber> arrayList) {
            this.arrayList = arrayList;
            inflater = LayoutInflater.from(context);
            // generate the textview for first time
            generateTextView(listSelected);
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
            TextView txtSelectIcon;
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
                holder.txtSelectIcon = (TextView) convertView
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
                                listSelectedPiece.add(data.getIdPieceSerial());
                            } else {
                                listSelected.remove(data.getName());
                                listSelectedPiece.remove(data.getIdPieceSerial());
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

    /**
     * @param listSel <p>
     *                method to generate textview dynamically and add it to the
     *                relative layout. If the textview have the size to fit into
     *                first row, it will added or else will added to the next row.
     *                </p>
     */
    private void generateTextView(final ArrayList<String> listSel) {

        relSerialConatainer = (RelativeLayout) dialogView
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
            tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources()
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

                // scroll down the scroll view after adding the text views.
                scrollContainer.post(new Runnable() {

                    @Override
                    public void run() {
                        scrollContainer.fullScroll(View.FOCUS_DOWN);
                    }
                });
            }
        });

        if (adapter != null)
            adapter.notifyDataSetChanged();
    }

    // *********************************************LIST...VIEW...ADAPTER*******************************************************

    // **********************************************DIALOG...FRAGMENT...CLASS***************************************************
    @SuppressLint("ValidFragment")
    public static class SerialNumberDialog extends DialogFragment {
        EditText edtSearch;

        public static SerialNumberDialog getInstance() {
            return new SerialNumberDialog();
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

            listener.onItemsSelected(listSelected,listSelectedPiece);
        }

        /*
         * sets the height of the dialog to 1/7th of the total screen height.
         */
        @Override
        public void onResume() {
            super.onResume();
            DisplayMetrics metrics = getResources().getDisplayMetrics();
            int screenHeight = metrics.heightPixels;
            int dialogHeight = (int) (screenHeight * 0.7);
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
            dialogView = inflater.inflate(
                    R.layout.dialog_serial_number_spinner, container, false);
            typeFace = Typeface.createFromAsset(getActivity().getAssets(),
                                                getActivity().getString(R.string.fontName_fontAwesome));
            initializeUI();
            setCancelable(false);
            return dialogView;
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

                // listSelected.add(data.getStringExtra("SCAN_RESULT_CODE"));
                // RestockSerialNumberDialog di = new RestockSerialNumberDialog(
                // getActivity());
                // di.generateTextView(listSelected);
            }
        }

        /*
         * Initiates the views
         */
        private void initializeUI() {
            final ListView listView = (ListView) dialogView
                    .findViewById(R.id.listSerialNos);
            listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
            listView.setFastScrollEnabled(false);

            scrollContainer = (ScrollView) dialogView
                    .findViewById(R.id.scrollContainer);

            RestockSerialNumberDialog sn = new RestockSerialNumberDialog(
                    getActivity());
            adapter = sn.new SerialNumberAdapter(getActivity(), items);
            listView.setAdapter(adapter);

            TextView txtSpinnerIcon = (TextView) dialogView
                    .findViewById(R.id.txtSearchIcon);
            TextView txtBarcodeIcon = (TextView) dialogView
                    .findViewById(R.id.txtBarcodeIcon);
            com.synchroteam.TypefaceLibrary.TextView txtClose = (com.synchroteam.TypefaceLibrary.TextView) dialogView
                    .findViewById(R.id.txtClose);

            txtSpinnerIcon.setTypeface(typeFace);
            txtBarcodeIcon.setTypeface(typeFace);

            edtSearch = (EditText) dialogView
                    .findViewById(R.id.edtSearchSerialNos);

            edtSearch.addTextChangedListener(watcher);

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
    }

    // **********************************************DIALOG...FRAGMENT...CLASS***************************************************
}
