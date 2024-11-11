package com.synchroteam.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.synchroteam.TypefaceLibrary.EditText;
import com.synchroteam.TypefaceLibrary.TextView;
import com.synchroteam.beans.Item;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.scanner.CodeScannerActivity;
import com.synchroteam.utils.KEYS;
import com.synchroteam.utils.RequestCode;

import static com.synchroteam.utils.KEYS.MultipleTextData.KEY_CATEGORY;
import static com.synchroteam.utils.KEYS.MultipleTextData.KEY_CHILD_INDEX;
import static com.synchroteam.utils.KEYS.MultipleTextData.KEY_CONF;
import static com.synchroteam.utils.KEYS.MultipleTextData.KEY_GROUP_INDEX;
import static com.synchroteam.utils.KEYS.MultipleTextData.KEY_ITEM;
import static com.synchroteam.utils.KEYS.MultipleTextData.KEY_VALUE;

/**
 * Dialog fragment for text field which having multiple items separated by pipe symbol in Reports screen.
 * <p>
 * Author Trident
 */
public class EnterMultipleDataDialogNew extends DialogFragment {

    private LinearLayout containerView;

    private Typeface tfFontAwesome;
    private Typeface tfAvenir;

    private int childPos;
    private Item item;
    private String[] itemName;

    //Empty constructor
    public EnterMultipleDataDialogNew() {

    }

    public static EnterMultipleDataDialogNew newInstance(Item item, int conf, int groupIndex, int childIndex,
                                                         String categoryName) {
        EnterMultipleDataDialogNew multipleDataDF = new EnterMultipleDataDialogNew();
        Bundle args = new Bundle();
        args.putParcelable(KEY_ITEM, item);
        args.putInt(KEYS.MultipleTextData.KEY_CONF, conf);
        args.putInt(KEYS.MultipleTextData.KEY_GROUP_INDEX, groupIndex);
        args.putInt(KEYS.MultipleTextData.KEY_CHILD_INDEX, childIndex);
        args.putString(KEY_CATEGORY, categoryName);
        multipleDataDF.setArguments(args);
        return multipleDataDF;
    }

    /**
     * Interface for saving data
     */
    public interface MultipleDataSaveListener {
        void onDataSaved(Bundle data);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_multiple_data_dialog_new, container, false);
        tfFontAwesome = Typeface.createFromAsset(getActivity().getAssets(), getString(R.string.fontName_fontAwesome));
        tfAvenir = Typeface.createFromAsset(getActivity().getAssets(),getString(R.string.fontName_avenir));
        return view;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView dialogTitleTv = (TextView) view.findViewById(R.id.categoryNameTv);
        Button btnSave = (Button) view.findViewById(R.id.btn_save);
        Button btnCancel = (Button) view.findViewById(R.id.btn_cancel);
        containerView = (LinearLayout) view.findViewById(R.id.containerViews);

        dialogTitleTv.setText(getArguments().getString(KEY_CATEGORY));

        btnSave.setOnClickListener(clickListener);
        btnCancel.setOnClickListener(clickListener);

        item = getArguments().getParcelable(KEY_ITEM);
        inflateViews(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RequestCode.REQUEST_CODE_TEXT_BARCODE) {
            if (resultCode == Activity.RESULT_OK) {
                String code = data.getStringExtra("SCAN_RESULT_CODE");

                RelativeLayout childView = (RelativeLayout) containerView.getChildAt(childPos);
                EditText editText = (EditText) childView.findViewById(R.id.ed_data);

                editText.setText(code);
            }
        }
    }

    /**
     * Add the views to the linear layout.
     *
     * @param item : items
     */
    private void inflateViews(Item item) {
        itemName = item.getNomItem().split("\\|");

        String[] values = null;
        if (!TextUtils.isEmpty(item.getValeurNet())) {
            values = item.getValeurNet().split("\\|");

        }

        for (int i = 0; i < itemName.length; i++) {


            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View childView = inflater.inflate(R.layout.layout_data_with_barcode, null);

//            TextInputLayout txtIpData = (TextInputLayout) childView.findViewById(R.id.txt_input_data);
            TextView txtItemName = (TextView) childView.findViewById(R.id.txt_item_name);
            EditText edData = (EditText) childView.findViewById(R.id.ed_data);
            android.widget.TextView txtBarcode = (android.widget.TextView) childView.findViewById(R.id.txtBarcodeIcon);

//            txtIpData.setTypeface(tfAvenir);
            txtBarcode.setTypeface(tfFontAwesome);

//            txtIpData.setHint(itemName[i]);
            txtItemName.setText(itemName[i]);

            if (values != null) {
                if (i <= (values.length - 1)) {
                    if (!TextUtils.isEmpty(values[i])) {
                        edData.setText(values[i]);
                    }
                }

            }

            edData.setSelection(edData.getText().length());

            interceptScrollEvent(edData);

            txtBarcode.setTag(i);
            txtBarcode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    childPos = (int) view.getTag();
                    Intent it = new Intent(getActivity(), CodeScannerActivity.class);
                    startActivityForResult(it,
                            RequestCode.REQUEST_CODE_TEXT_BARCODE);
                }
            });

            containerView.addView(childView);

        }
    }

    private void interceptScrollEvent (EditText editText){
        editText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    switch (event.getAction() & MotionEvent.ACTION_MASK) {
                        case MotionEvent.ACTION_UP:
                            v.getParent().requestDisallowInterceptTouchEvent(false);
                            break;
                    }
                return false;
            }
        });
    }

    /**
     * Click listener for buttons
     */
    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int id = view.getId();
            switch (id) {
                case R.id.btn_save:
                    StringBuilder values = new StringBuilder();
                    for (int i = 0; i < itemName.length; i++) {

                        RelativeLayout childView = (RelativeLayout) containerView.getChildAt(i);
                        EditText et1 = (EditText) childView.findViewById(R.id.ed_data);

                        if ((i + 1) == itemName.length)
                            values.append(et1.getText().toString());
                        else
                            values.append(et1.getText().toString() + "|");

                    }

                    StringBuilder emptyString = new StringBuilder();
                    for (int i = 0; i < itemName.length; i++) {
                        if ((i + 1) == itemName.length)
                            emptyString.append("");
                        else
                            emptyString.append("|");

                    }

                    MultipleDataSaveListener listener = (MultipleDataSaveListener) getTargetFragment();

                    Bundle bundle = new Bundle();
                    bundle.putParcelable(KEY_ITEM, item);
                    bundle.putInt(KEY_CONF, getArguments().getInt(KEY_CONF));
                    bundle.putInt(KEY_CHILD_INDEX, getArguments().getInt(KEY_CHILD_INDEX));
                    bundle.putInt(KEY_GROUP_INDEX, getArguments().getInt(KEY_GROUP_INDEX));

                    if (values.toString().equals(emptyString.toString())) {
                        bundle.putString(KEY_VALUE, "");
                        listener.onDataSaved(bundle);
                    } else {
                        bundle.putString(KEY_VALUE, values.toString());
                        listener.onDataSaved(bundle);
                    }

                    dismiss();
                    break;

                case R.id.btn_cancel:
                    dismiss();
                    break;
            }
        }
    };


}
