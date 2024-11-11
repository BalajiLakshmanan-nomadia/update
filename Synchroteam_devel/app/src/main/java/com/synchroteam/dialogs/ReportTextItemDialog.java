package com.synchroteam.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AlertDialog;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.synchroteam.TypefaceLibrary.Button;
import com.synchroteam.TypefaceLibrary.EditText;
import com.synchroteam.TypefaceLibrary.TextView;
import com.synchroteam.events.CloseTextDialogEvent;
import com.synchroteam.events.SaveTextDialogEvent;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.technicalsupport.JobDetails;
import com.synchroteam.utils.CommonUtils;

import de.greenrobot.event.EventBus;

/**
 * Full screen dialog for text item in a family in Report screen.
 * Created by Trident on 1/18/2017.
 */

public class ReportTextItemDialog extends DialogFragment {

    private EditText edItemValue;
    private Button btnSave;
    private static TextItemListener listener;

    private boolean isChangesMade;

    private static final String KEY_FAMILY_NAME = "family_name";
    private static final String KEY_ITEM_TITLE = "item_title";
    private static final String KEY_ITEM_VALUE = "item_value";

    private static final String TAG = ReportTextItemDialog.class.getSimpleName();

    private Dialog dialog;

    public static ReportTextItemDialog getInstance(String familyName, String itemTitle, String itemValue, TextItemListener mListener) {
        listener = mListener;

        ReportTextItemDialog dialog = new ReportTextItemDialog();
        Bundle args = new Bundle();
        args.putString(KEY_FAMILY_NAME, familyName);
        args.putString(KEY_ITEM_TITLE, itemTitle);
        args.putString(KEY_ITEM_VALUE, itemValue);
        dialog.setArguments(args);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_text_item_report, container, false);
//        initToolbar(view);
        initiateView(view);
        EventBus.getDefault().unregister(this);
        EventBus.getDefault().registerSticky(this);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        getActivity().getMenuInflater().inflate(R.menu.menu_text_item_dialog, menu);
    }

    /**
     * Initiates toolbar from activity context
     *
     * @param view : layout parent view
     */
    private void initToolbar(View view) {
//        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
//
//        toolbar.setTitle(getArguments().getString(KEY_FAMILY_NAME));
//
//        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
//
//        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
//        if (actionBar != null) {
//            actionBar.setDisplayHomeAsUpEnabled(true);
//            actionBar.setHomeButtonEnabled(true);
//            actionBar.setHomeAsUpIndicator(android.R.drawable.ic_menu_close_clear_cancel);
//        }
//        setHasOptionsMenu(true);
    }

    /**
     * Initiates all UI elements.
     *
     * @param view
     */
    private void initiateView(View view) {

        Typeface typeFace = Typeface.createFromAsset(getActivity().getAssets(),
                getActivity().getString(R.string.fontName_fontAwesome));

        android.widget.TextView txtViewExit = (android.widget.TextView) view.findViewById(R.id.txtViewExit);
        txtViewExit.setTypeface(typeFace);

        com.synchroteam.TypefaceLibrary.TextView txtItemName = (com.synchroteam.TypefaceLibrary.TextView) view
                .findViewById(R.id.toolbar_name);
        txtItemName.setText(getArguments().getString(KEY_FAMILY_NAME));

        TextView txtItemTitle = (TextView) view.findViewById(R.id.txt_item_name);
        edItemValue = (EditText) view.findViewById(R.id.ed_data);
        btnSave = (Button) view.findViewById(R.id.btnSave);
        btnSave.setText(getResources().getString(R.string.modifier));

        InputFilter[] FilterArray = new InputFilter[1];
        FilterArray[0] = new InputFilter.LengthFilter(512);
        edItemValue.setFilters(FilterArray);

        txtItemTitle.setText(getArguments().getString(KEY_ITEM_TITLE));
        edItemValue.setText(getArguments().getString(KEY_ITEM_VALUE));

        isChangesMade = false;

        edItemValue.requestFocus();
        CommonUtils.showKeyboard(getActivity(), edItemValue);

        edItemValue.addTextChangedListener(mWatcher);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(new SaveTextDialogEvent());
            }
        });

        txtViewExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(new CloseTextDialogEvent());
            }
        });

    }


    /**
     * Event for closing the dialog.
     *
     * @param closeTextDialogEvent
     */
    public void onEvent(CloseTextDialogEvent closeTextDialogEvent) {
        doDialogCloseEvent();
    }

    private void doDialogCloseEvent() {
        if (edItemValue != null) {
            CommonUtils.hideKeyboard(getActivity(), edItemValue);
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

                                    removeAdjsViewForActivity();

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
                                    ((JobDetails) getActivity()).isTextDialogOpened = true;
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
        if (edItemValue != null) {
            CommonUtils.hideKeyboard(getActivity(), edItemValue);
        }
        ((JobDetails) getActivity()).isTextDialogOpened = false;
        listener.onSave(edItemValue.getText().toString());
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
     * Interface for saving data.
     */
    public interface TextItemListener {
        void onSave(String data);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        removeAdjsViewForActivity();
    }

    private void removeAdjsViewForActivity() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        } else {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_UNSPECIFIED);
        }
    }

}
