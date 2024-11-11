package com.synchroteam.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.synchroteam.TypefaceLibrary.EditText;
import com.synchroteam.TypefaceLibrary.TextView;
import com.synchroteam.beans.Item;
import com.synchroteam.events.CloseTextDialogEvent;
import com.synchroteam.events.SaveTextDialogEvent;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.technicalsupport.JobDetails;
import com.synchroteam.scanner.CodeScannerActivity;
import com.synchroteam.utils.CommonUtils;
import com.synchroteam.utils.KEYS;
import com.synchroteam.utils.RequestCode;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

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
public class ReportMultipleTextiItemDialog extends DialogFragment {

    private LinearLayout containerView;

    private Typeface tfFontAwesome;

    private int childPos;
    private Item item;
    private String[] itemName;

    private boolean isChangesMade;

    private ArrayList<EditText> listEdtItem;


    //Empty constructor
    public ReportMultipleTextiItemDialog() {

    }

    public static ReportMultipleTextiItemDialog newInstance(Item item, int conf, int groupIndex, int childIndex,
                                                            String categoryName) {
        ReportMultipleTextiItemDialog multipleDataDF = new ReportMultipleTextiItemDialog();
        Bundle args = new Bundle();
        args.putParcelable(KEY_ITEM, item);
        args.putInt(KEYS.MultipleTextData.KEY_CONF, conf);
        args.putInt(KEYS.MultipleTextData.KEY_GROUP_INDEX, groupIndex);
        args.putInt(KEYS.MultipleTextData.KEY_CHILD_INDEX, childIndex);
        args.putString(KEY_CATEGORY, categoryName);
        multipleDataDF.setArguments(args);
        return multipleDataDF;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_multiple_text_report, container, false);
        tfFontAwesome = Typeface.createFromAsset(getActivity().getAssets(), getString(R.string.fontName_fontAwesome));
        initToolbar(view);
        EventBus.getDefault().unregister(this);
        EventBus.getDefault().registerSticky(this);
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        getActivity().getMenuInflater().inflate(R.menu.menu_text_item_dialog, menu);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        containerView = (LinearLayout) view.findViewById(R.id.containerViews);
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
                editText.setSelection(code.length());
            }
        }
    }

    /**
     * Initiates toolbar from activity context
     *
     * @param view : layout parent view
     */
    private void initToolbar(View view) {
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        toolbar.setTitle(getArguments().getString(KEY_CATEGORY));

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setHomeAsUpIndicator(android.R.drawable.ic_menu_close_clear_cancel);
        }
        setHasOptionsMenu(true);
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

        listEdtItem = new ArrayList<>();

        for (int i = 0; i < itemName.length; i++) {


            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View childView = inflater.inflate(R.layout.layout_data_with_barcode, null);

            TextView txtItemName = (TextView) childView.findViewById(R.id.txt_item_name);
            EditText edData = (EditText) childView.findViewById(R.id.ed_data);

            //changes done now
            if (itemName.length > 1) {
                if (i == itemName.length - 1) {
                    edData.setImeOptions(EditorInfo.IME_ACTION_DONE);
                } else {
                    edData.setImeOptions(EditorInfo.IME_ACTION_NEXT);
                }
                edData.setHorizontallyScrolling(false);
                edData.setRawInputType(InputType.TYPE_CLASS_TEXT);
            }

            android.widget.TextView txtBarcode = (android.widget.TextView) childView.findViewById(R.id.txtBarcodeIcon);

            txtBarcode.setTypeface(tfFontAwesome);

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

            edData.addTextChangedListener(mWatcher);

            edData.setOnKeyListener(new View.OnKeyListener() {
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (keyCode == EditorInfo.IME_ACTION_DONE) {
                        if (getActivity() != null)
                            CommonUtils.hideKeyboardNew(getActivity());
                        return true;
                    }

                    return false;
                }
            });

            listEdtItem.add(edData);

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

        listEdtItem.get(0).requestFocus();
        CommonUtils.showKeyboard(getActivity(), listEdtItem.get(0));

    }

    private void interceptScrollEvent(EditText editText) {
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
     * Event for closing the dialog.
     *
     * @param closeTextDialogEvent
     */
    public void onEvent(CloseTextDialogEvent closeTextDialogEvent) {
        if (listEdtItem.get(0) != null) {
            CommonUtils.hideKeyboard(getActivity(), listEdtItem.get(0));
        }

        if (isChangesMade) {
            AlertDialog.Builder builder = new AlertDialog.Builder(
                    getActivity());
            builder.setMessage(R.string.textCancelLable)
                    .setCancelable(false)
                    .setPositiveButton(R.string.textYesLable,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    ((JobDetails) getActivity()).isTextDialogOpened = false;
                                    isChangesMade = false;
                                    EventBus.getDefault().unregister(this);
                                    dismiss();
                                }
                            })
                    .setNegativeButton(R.string.textNoLable,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    dialog.cancel();
                                }
                            });
            AlertDialog alert = builder.create();
            alert.show();
        } else {
            ((JobDetails) getActivity()).isTextDialogOpened = false;
            EventBus.getDefault().unregister(this);
            dismiss();
        }
    }


    /**
     * Event for saving the data enter.
     *
     * @param saveTextDialogEvent
     */
    public void onEvent(SaveTextDialogEvent saveTextDialogEvent) {
        if (listEdtItem.get(0) != null) {
            CommonUtils.hideKeyboard(getActivity(), listEdtItem.get(0));
        }
        ((JobDetails) getActivity()).isTextDialogOpened = false;

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

        EventBus.getDefault().unregister(this);
        dismiss();
    }

    /**
     * Watcher for edittext.
     */
    TextWatcher mWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            isChangesMade = true;
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    /**
     * Interface for saving data
     */
    public interface MultipleDataSaveListener {
        void onDataSaved(Bundle data);
    }

}
