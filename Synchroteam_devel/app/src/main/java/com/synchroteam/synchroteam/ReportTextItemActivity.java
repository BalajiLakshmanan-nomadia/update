package com.synchroteam.synchroteam;

import android.app.Dialog;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;

import com.synchroteam.TypefaceLibrary.Button;
import com.synchroteam.TypefaceLibrary.EditText;
import com.synchroteam.TypefaceLibrary.TextView;
import com.synchroteam.dialogs.ReportTextItemDialog;
import com.synchroteam.events.CloseTextDialogEvent;
import com.synchroteam.events.SaveTextDialogEvent;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.utils.CommonUtils;

import de.greenrobot.event.EventBus;

/**
 * Created by dharma on 17/04/18.
 */

public class ReportTextItemActivity extends AppCompatActivity implements View.OnClickListener {
    
    private EditText edItemValue;
    private Button btnSave;
    private static ReportTextItemDialog.TextItemListener listener;

    private boolean isChangesMade;

    private static final String KEY_FAMILY_NAME = "family_name";
    private static final String KEY_ITEM_TITLE = "item_title";
    private static final String KEY_ITEM_VALUE = "item_value";

    private static final String TAG = ReportTextItemDialog.class.getSimpleName();

    private Dialog dialog;

    private final int minDelta = 300;           // threshold in ms
    private long focusTime = 0;                 // time of last touch
    private View focusTarget = null;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_text_item);
        getValuesFromBundle();
        initializeView();
    }
    
    
    private void initializeView() {
        Typeface typeFace = Typeface.createFromAsset(getAssets(),
                getString(R.string.fontName_fontAwesome));

        android.widget.TextView txtViewExit = (android.widget.TextView) findViewById(R.id.txtViewExit);
        txtViewExit.setTypeface(typeFace);

        com.synchroteam.TypefaceLibrary.TextView txtItemName = (com.synchroteam.TypefaceLibrary.TextView) findViewById(R.id.toolbar_name);
        txtItemName.setText(getIntent().getExtras().getString(KEY_FAMILY_NAME));

        TextView txtItemTitle = (TextView) findViewById(R.id.txt_item_name);
        edItemValue = (EditText) findViewById(R.id.ed_data);
        btnSave = (Button) findViewById(R.id.btnSave);
        btnSave.setText(getResources().getString(R.string.modifier));

        InputFilter[] FilterArray = new InputFilter[1];
        FilterArray[0] = new InputFilter.LengthFilter(512);
        edItemValue.setFilters(FilterArray);

        txtItemTitle.setText(getIntent().getExtras().getString(KEY_ITEM_TITLE));
        edItemValue.setText(getIntent().getExtras().getString(KEY_ITEM_VALUE));

        isChangesMade = false;

        edItemValue.requestFocus();
        CommonUtils.showKeyboard(ReportTextItemActivity.this, edItemValue);

        edItemValue.addTextChangedListener(mWatcher);


        edItemValue.setOnFocusChangeListener(onFocusChangeListener);

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

        edItemValue.requestFocus();

        edItemValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!edItemValue.hasFocus())
                    edItemValue.requestFocus();
            }
        });

    }

    View.OnFocusChangeListener onFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View view, boolean hasFocus) {
            long t = System.currentTimeMillis();
            long delta = t - focusTime;
            if (hasFocus) {     // gained focus
                if (delta > minDelta) {
                    focusTime = t;
                    focusTarget = view;
                }
            } else {              // lost focus
                if (delta <= minDelta && view == focusTarget) {
                    focusTarget.post(new Runnable() {   // reset focus to target
                        public void run() {
                            focusTarget.requestFocus();
                        }
                    });
                }
            }
        }
    };

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


    private void getValuesFromBundle() {
    }

    @Override
    public void onClick(View v) {
        
    }

    /**
     * Interface for saving data.
     */
    public interface TextItemListener {
        void onSave(String data);
    }


    @Override
    protected void onDestroy() {
        removeAdjsViewForActivity();
        super.onDestroy();
        
    }

    private void removeAdjsViewForActivity() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        } else {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_UNSPECIFIED);
        }
    }
}
