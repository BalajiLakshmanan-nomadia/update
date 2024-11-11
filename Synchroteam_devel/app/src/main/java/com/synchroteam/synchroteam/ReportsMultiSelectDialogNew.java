package com.synchroteam.synchroteam;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.synchroteam.synchroteam3.R;

import java.util.ArrayList;

/**
 * Created by user on 28/3/17.
 */

@SuppressLint("ValidFragment")
public class ReportsMultiSelectDialogNew extends DialogFragment {


    private Context context;

    private static ArrayList<String> listSelectedAlready;
    ArrayList<String> fullListValues;
    String[] itemValeurNetSelected;

    private String itemName;
    static MultiSelectValuesAdapter adapter;
    Dialog dialog;

    public static ScrollView scrollContainer;
    public static RelativeLayout relSerialConatainer;
    private static Typeface typeFace;
    private long mLastClickTime = 0;
    Activity activity;

    SaveButtonClickListener saveButtonClickListener;

    public interface SaveButtonClickListener {
        void saveButtonClicked(ArrayList<String> arrayList);
    }


    public ReportsMultiSelectDialogNew(Activity activity, String itemName, ArrayList<String> fullListValues, String[] itemValeurNetSelected, SaveButtonClickListener saveButtonClickListener) {

        this.itemName = itemName;
        this.fullListValues = fullListValues;
        this.itemValeurNetSelected = itemValeurNetSelected;
        this.activity = activity;
        this.saveButtonClickListener = saveButtonClickListener;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        dialog = new Dialog(getActivity(), android.R.style.Theme_Light_NoTitleBar);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        dialog.setContentView(R.layout.dialog_reports_multi_select_layout);

        typeFace = Typeface.createFromAsset(getActivity().getAssets(),
                getActivity().getString(R.string.fontName_fontAwesome));

        initializeUI();

        dialog.setCancelable(true);

        dialog.show();

        return dialog;
    }


    private void initializeUI() {

        final ListView listView = (ListView) dialog.findViewById(R.id.listSerialNos);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listView.setFastScrollEnabled(false);

        TextView txtViewExit = (TextView) dialog
                .findViewById(R.id.txtViewExit);
        txtViewExit.setTypeface(typeFace);

        com.synchroteam.TypefaceLibrary.TextView txtItemName = (com.synchroteam.TypefaceLibrary.TextView) dialog
                .findViewById(R.id.txt_item_name);
        txtItemName.setText(itemName);

        scrollContainer = (ScrollView) dialog
                .findViewById(R.id.scrollContainer);

        listSelectedAlready = new ArrayList<String>();

        for (int i = 0; i < itemValeurNetSelected.length; i++) {
            listSelectedAlready.add(itemValeurNetSelected[i]);
        }

        MultiSelectValuesAdapter customListOfValuesAdapter = new MultiSelectValuesAdapter(
                activity, fullListValues, listSelectedAlready);
        listView.setAdapter(customListOfValuesAdapter);


        com.synchroteam.TypefaceLibrary.TextView txtViewSave = (com.synchroteam.TypefaceLibrary.TextView) dialog
                .findViewById(R.id.txtViewSave);

        txtViewSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                saveButtonClickListener.saveButtonClicked(listSelectedAlready);
                dismiss();

            }
        });

        txtViewExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

    }


    // *********************************************LIST...VIEW...ADAPTER*******************************************************
    public class MultiSelectValuesAdapter extends BaseAdapter {

        ArrayList<String> fullListValues;
        LayoutInflater inflater;
        ArrayList<String> listSelectedAlready;

        public MultiSelectValuesAdapter(Context context,
                                        ArrayList<String> fullListValues, ArrayList<String> listSelectedAlready) {

            this.fullListValues = fullListValues;
            this.listSelectedAlready = listSelectedAlready;
            inflater = LayoutInflater.from(context);


            // generate the textview for first time
            generateTextView(listSelectedAlready);

        }

        @Override
        public int getCount() {
            return fullListValues.size();
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
                        R.layout.row_dialog_reports_multi_select_list, null);
                holder.textView = (com.synchroteam.TypefaceLibrary.TextView) convertView
                        .findViewById(R.id.alertTextView);
                holder.txtSelectIcon = (android.widget.TextView) convertView
                        .findViewById(R.id.txtItemSelect);

                holder.txtSelectIcon.setTypeface(typeFace);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final String data = fullListValues.get(position);

            holder.textView.setText(data);

            outerloop:
            for (int i = 0; i < listSelectedAlready.size(); i++) {

                if (listSelectedAlready.get(i).equalsIgnoreCase(fullListValues.get(position))) {
                    holder.txtSelectIcon.setVisibility(View.VISIBLE);
                    break outerloop;
                } else {
                    holder.txtSelectIcon.setVisibility(View.INVISIBLE);
                }
            }

            final ViewHolder finalHolder = holder;
            convertView.setOnClickListener(new View.OnClickListener() {
                @SuppressWarnings("deprecation")
                public void onClick(View v) {

                    String strSelectedNow = null;

                    if (listSelectedAlready.size() == 0) {
                        strSelectedNow = data;
                        finalHolder.txtSelectIcon.setVisibility(View.VISIBLE);
                    } else {

                        loopExitWhenRemovedList:
                        for (int i = 0; i < listSelectedAlready.size(); i++) {

                            strSelectedNow = null;

                            if (listSelectedAlready.get(i).equalsIgnoreCase(fullListValues.get(position))) {
                                listSelectedAlready.remove(i);
                                finalHolder.txtSelectIcon.setVisibility(View.INVISIBLE);
                                break loopExitWhenRemovedList;

                            } else {
                                finalHolder.txtSelectIcon.setVisibility(View.VISIBLE);
                                strSelectedNow = data;
                            }
                        }
                    }

                    if (strSelectedNow != null) {
                        listSelectedAlready.add(strSelectedNow);
                    }

                    // generate the textview after selected
                    generateTextView(listSelectedAlready);

                }
            });

            holder.txtSelectIcon.setTag(holder);

            return convertView;
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

        relSerialConatainer = (RelativeLayout)
                dialog.findViewById(R.id.relSerialContainer);
        relSerialConatainer.removeAllViews();

        for (int i = 0; i < listSel.size(); i++) {

            android.widget.TextView tv = new android.widget.TextView(getContext());
            Typeface typeFaceAvenir = Typeface.createFromAsset(
                    getContext().getAssets(),
                    getContext().getResources().getString(
                            R.string.fontName_avenir));
            tv.setText(listSel.get(i));
            tv.setBackgroundDrawable(getContext().getResources().getDrawable(
                    R.drawable.boxframe_serial_not_text));

            tv.setId(i + 1);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, getContext().getResources()
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


}