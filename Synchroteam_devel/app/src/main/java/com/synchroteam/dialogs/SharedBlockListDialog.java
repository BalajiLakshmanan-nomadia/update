package com.synchroteam.dialogs;


import android.annotation.TargetApi;
import android.app.Dialog;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;

import com.synchroteam.TypefaceLibrary.EditText;
import com.synchroteam.TypefaceLibrary.TextView;
import com.synchroteam.beans.SharedBlocks;
import com.synchroteam.listadapters.SharedBlockListAdapter;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.utils.KEYS;

import java.util.ArrayList;

/**
 * Dialog fragment for showing list of shared blocks.<p></p>
 * Created by Trident on 7/19/2016.
 */
public class SharedBlockListDialog extends DialogFragment {

    //--------------------------------INSTANCE...VARIABLES...STARTS--------------------------------------

    private ListView lvSharedBlock;

    private ArrayList<SharedBlocks> listSharedBlocks = new ArrayList<>();
    private SharedBlocks sharedBlocks;
    private EditText edSearchSB;
    private SharedBlockListAdapter mSharedBlockAdapter;
//    private static Dao dataAccessObject;

//    private static final String TAG = SharedBlockListDialog.class.getSimpleName();

    //--------------------------------INSTANCE...VARIABLES...STARTS--------------------------------------

    //---------------------------------PUBLIC...METHODS...STARTS----------------------------------------

    public static SharedBlockListDialog getInstance(ArrayList<SharedBlocks> listSharedBlocks) {
//        dataAccessObject = DaoManager.getInstance();
        SharedBlockListDialog dialog = new SharedBlockListDialog();
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEYS.SharedBlock.LIST_SHARED_BLOCK, listSharedBlocks);
        dialog.setArguments(bundle);
        return dialog;
    }

    //---------------------------------PUBLIC...METHODS...ENDS------------------------------------------

    //--------------------------------LIFECYCLE...METHODS...STARTS--------------------------------------

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_shared_blocks_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        lvSharedBlock = (ListView) view.findViewById(R.id.lv_shared_blocks);
        edSearchSB = (EditText) view.findViewById(R.id.edtSearchSharedBlock);
        TextView txtConfirm = (TextView) view.findViewById(R.id.txtConfirm);
        TextView txtClose = (TextView) view.findViewById(R.id.txtClose);
        android.widget.TextView txtSearchIc = (android.widget.TextView) view.findViewById(R.id.txtSearchIcon);

        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), getActivity().getString(R.string.fontName_fontAwesome));
        txtSearchIc.setTypeface(typeface);

        edSearchSB.addTextChangedListener(watcher);
        txtConfirm.setOnClickListener(clickListener);
        txtClose.setOnClickListener(clickListener);

        setCancelable(false);

        getListData();

        setListAdapter();

    }

    @Override
    public void show(FragmentManager manager, String tag) {
        try {
            FragmentTransaction ft = manager.beginTransaction();
            ft.add(this, tag).addToBackStack(null);
            ft.commitAllowingStateLoss();
        } catch (IllegalStateException e) {
            com.synchroteam.utils.Logger.log("TAG", "IllegalStateException ----->" + e);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity(), R.style.Theme_TransparentDialog);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int screenHeight = metrics.heightPixels;
        int dialogHeight = (int) (screenHeight * 0.5);

        WindowManager.LayoutParams wmlp = dialog.getWindow()
                .getAttributes();
        wmlp.gravity = Gravity.CENTER;
        wmlp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        wmlp.height = dialogHeight;

        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        return dialog;
    }

    /**
     * sets the height of the dialog to 1/7th of the total screen height.
     */
//    @Override
//    public void onResume() {
//        super.onResume();
//        DisplayMetrics metrics = getResources().getDisplayMetrics();
//        int screenHeight = metrics.heightPixels;
//        int dialogHeight = (int) (screenHeight * 0.5);
//        getDialog().getWindow().setLayout(
//                ViewGroup.LayoutParams.WRAP_CONTENT, dialogHeight);
//    }
    //--------------------------------LIFECYCLE...METHODS...STARTS--------------------------------------

    //--------------------------------LOCAL...METHODS...STARTS------------------------------------------

    /**
     * Add list of sorting options data to the array list.
     */
    private void getListData() {
//        String[] arrSharedBlocks = new String[]{"Quality control",
//                "Water meter reading",
//                "Machine details",
//                "Installation details",
//        };
        listSharedBlocks = (ArrayList<SharedBlocks>) getArguments().getSerializable(KEYS.SharedBlock.LIST_SHARED_BLOCK);
//        for (int i = 0; i < arrSharedBlocks.length; i++) {
//            SharedBlocks sharedBlocks = new SharedBlocks();
//            sharedBlocks.setBlockName(arrSharedBlocks[i]);
//            listSharedBlocks.add(sharedBlocks);
//        }
    }

    /**
     * Sets the adapter.
     */
    private void setListAdapter() {
        mSharedBlockAdapter = new SharedBlockListAdapter(getActivity(), this, listSharedBlocks);
        lvSharedBlock.setAdapter(mSharedBlockAdapter);
    }

    /**
     * Sends selected sorting option and job number (only if "Sort Nearby job" selected) to All job fragment.
     */
    private void sendSharedBlockName() {
        SelectionListener listener = (SelectionListener) getTargetFragment();
        listener.onSelectSB(sharedBlocks);
        dismiss();
    }

    /**
     * set sorting values from adapter.
     *
     * @param sharedBlocks : shared block family
     */
    public void setSB(SharedBlocks sharedBlocks) {
        this.sharedBlocks = sharedBlocks;
    }

    //--------------------------------LOCAL...METHODS...STARTS------------------------------------------

    //------------------------------------LISTENER...METHODS...STARTS-----------------------------------

    /**
     * Click listener
     */
    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.txtConfirm:
                    sendSharedBlockName();
                    break;
                case R.id.txtClose:
                    dismiss();
                    break;
            }
        }
    };

    /**
     * Text watcher for edit text.
     */
    private TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        mSharedBlockAdapter.getFilter().filter(s.toString());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    //------------------------------------LISTENER...METHODS...ENDS-------------------------------------

    //--------------------------------INTERFACE...STARTS------------------------------------------------

    public interface SelectionListener {
        void onSelectSB(SharedBlocks sharedBlocks);
    }
    //--------------------------------INTERFACE...ENDS--------------------------------------------------


}
