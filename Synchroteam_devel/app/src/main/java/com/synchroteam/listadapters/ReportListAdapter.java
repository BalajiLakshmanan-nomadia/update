package com.synchroteam.listadapters;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.synchroteam.beans.Client;
import com.synchroteam.beans.Families;
import com.synchroteam.beans.FamiliesBean;
import com.synchroteam.beans.Item;
import com.synchroteam.dao.Dao;
import com.synchroteam.fragmenthelper.ClientListFH;
import com.synchroteam.mvp.view.ReportViewFragmentNew;
import com.synchroteam.synchroteam.ClientDetail;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.utils.AccentInsensitive;
import com.synchroteam.utils.KEYS;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import androidx.fragment.app.FragmentActivity;

// TODO: Auto-generated Javadoc

/**
 * The Class ClientListAdapter.
 */
public class ReportListAdapter extends BaseAdapter {


    /**
     * The clients.
     */
    private ArrayList<FamiliesBean> listFamilies;


    /**
     * The layout inflater.
     */
    private LayoutInflater layoutInflater;

    /**
     * The index.
     */
    private int index;

    /**
     * The base count.
     */
    private int baseCount = 20;
    Dao dao;

    private ReportViewFragmentNew fragment;
    private Activity activity;
    String idJob;

    /**
     * Instantiates a new report list adapter.
     */
    public ReportListAdapter(Activity activity, ArrayList<FamiliesBean> listFamilies,
                             Dao dao, String idJob,
                             ReportViewFragmentNew fragment) {

        this.activity = activity;
        this.listFamilies = listFamilies;
        this.idJob = idJob;
        this.dao = dao;
        this.fragment = fragment;

        if (this.activity != null)
            layoutInflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    /*
     * (non-Javadoc)
     *
     * @see android.widget.Adapter#getCount()
     */
    @Override
    public int getCount() {
        // TODO Auto-generated method stub

        // if (!isUserSearching) {
        int count = index * baseCount;

        if (count < listFamilies.size()) {
            return count;
        } else {
            return listFamilies.size();
        }


    }

    public int getArrayCount() {
        return listFamilies.size();
    }

    /*
     * (non-Javadoc)
     *
     * @see android.widget.Adapter#getItem(int)
     */
    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return listFamilies.get(position);
    }

    /*
     * (non-Javadoc)
     *
     * @see android.widget.Adapter#getItemId(int)
     */
    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    /*
     * (non-Javadoc)
     *
     * @see android.widget.Adapter#getView(int, android.view.View,
     * android.view.ViewGroup)
     */
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View parentView = null;

        FamiliesBean family = listFamilies.get(position);
        parentView = inflater.inflate(R.layout.card_report_view_family, null);

        TextView txtFamilyName = (com.synchroteam.TypefaceLibrary.TextView) parentView.findViewById(R.id.txt_family_name);
        LinearLayout linItems = (LinearLayout) parentView.findViewById(R.id.lin_items);
        txtFamilyName.setText(family.getNameFamily());

        ArrayList<Item> items = family.getItems();

        for (int i = 0; i < items.size(); i++) {
            //adding child views
            Item item = items.get(i);
            View childView;
            int itemType = item.getIdTypeItem();

            if (itemType != 7 && itemType != 8 && itemType != 9) {
                childView = inflater.inflate(R.layout.item_normal_report_view, null);
                com.synchroteam.TypefaceLibrary.TextView txtFieldName = (com.synchroteam.TypefaceLibrary.TextView) childView.findViewById(R.id.txt_field_name);
                com.synchroteam.TypefaceLibrary.TextView txtFieldValue = (com.synchroteam.TypefaceLibrary.TextView) childView.findViewById(R.id.txt_field_value);
                View dividerView = childView.findViewById(R.id.divider_item);

                String fieldName = item.getNomItem();
                String fieldValue = fragment.getValueFormat(item.getValeurNet(),
                        item.getIdTypeItem());
                if (itemType == 10) {
                    fieldValue = fieldValue.replace("@@@", ", ");
                }
                txtFieldName.setText(fieldName);
                txtFieldValue.setText(fieldValue);

                if (i == items.size() - 1) {
                    dividerView.setVisibility(View.GONE);
                }
            } else if (itemType == 9) {

                childView = inflater.inflate(R.layout.item_location_report_view, null);
                com.synchroteam.TypefaceLibrary.TextView txtFieldName = (com.synchroteam.TypefaceLibrary.TextView) childView.findViewById(R.id.txt_field_name);
                com.synchroteam.TypefaceLibrary.TextView txtFieldValue = (com.synchroteam.TypefaceLibrary.TextView) childView.findViewById(R.id.txt_field_value);
                android.widget.TextView txtLocationIcon = (android.widget.TextView) childView.findViewById(R.id.txt_map_pin_icon);
                fragment.setFATypeFace(txtLocationIcon);

                View dividerView = childView.findViewById(R.id.divider_item);

                String fieldName = item.getNomItem();
                String fieldValue = fragment.getValueFormat(item.getValeurNet(),
                        item.getIdTypeItem());
                txtFieldName.setText(fieldName);
                txtFieldValue.setText(fieldValue);

                if (i == items.size() - 1) {
                    dividerView.setVisibility(View.GONE);
                }

                if (!TextUtils.isEmpty(fieldValue)) {
                    if (fieldValue.contains("|")) {
                        txtLocationIcon.setVisibility(View.VISIBLE);

                        final String lat = fieldValue.substring(0, fieldValue.indexOf("|"));
                        final String lon = fieldValue.substring(fieldValue.indexOf("|") + 1, fieldValue.lastIndexOf("|"));

                        txtLocationIcon.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                fragment.openMapIntent(lat, lon);
                            }
                        });
                    } else {
                        txtLocationIcon.setVisibility(View.GONE);
                    }
                } else {
                    txtLocationIcon.setVisibility(View.GONE);
                }

            } else {
                childView = inflater.inflate(R.layout.item_image_report_view, null);
                ImageView imgValue = (ImageView) childView.findViewById(R.id.img_field);
                com.synchroteam.TypefaceLibrary.TextView txtTitle = (com.synchroteam.TypefaceLibrary.TextView) childView.findViewById(R.id.txt_field_title);
                View dividerView = childView.findViewById(R.id.divider_item);

                String fieldName = item.getNomItem();
                txtTitle.setText(fieldName);

                final BitmapFactory.Options opt = new BitmapFactory.Options();
                opt.inDither = true;
                opt.inPreferredConfig = Bitmap.Config.RGB_565;

                //signature field
                if (itemType == 7) {
                    final byte[] retour = dao.getPhotoById(idJob, "SIGN_"
                            + item.getIdItem(), item.getIteration());
                    if (retour != null && retour.length != 0d) {

                        Glide.with(activity)
                                .load(retour)
                                .asBitmap()
                                .override(200, 200)
                                .fitCenter()
                                .placeholder(R.drawable.library_iicon)
                                .into(imgValue);
                    }

                    imgValue.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(final View v) {
                            if ((retour != null) && (retour.length != 0)) {


                                Glide.with(activity)
                                        .load(retour)
                                        .asBitmap()
                                        .override(200, 200)
                                        .fitCenter()
                                        .placeholder(R.drawable.library_iicon)
                                        .into(imgValue);
                            }

                        }
                    });
                }
                //photo field
                else {
                    final byte[] photoImg = item.getPhotoImg();
                    if ((photoImg != null) && (photoImg.length != 0)) {

                        Glide.with(activity)
                                .load(photoImg)
                                .asBitmap()
                                .override(200, 200)
                                .fitCenter()
                                .placeholder(R.drawable.library_iicon)
                                .into(imgValue);

                    }

                    imgValue.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(final View v) {
                            if ((photoImg != null) && (photoImg.length != 0)) {

                                //new changes
                                Glide.with(activity)
                                        .load(photoImg)
                                        .asBitmap()
                                        .fitCenter()
                                        .placeholder(R.drawable.library_iicon)
                                        .into(new SimpleTarget<Bitmap>() {
                                            @Override
                                            public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                                                fragment.showImg(v, resource);
                                            }
                                        });
                            }

                        }
                    });

                }
                if (i == items.size() - 1) {
                    dividerView.setVisibility(View.GONE);
                }
            }
            linItems.addView(childView);
        }
        return parentView;
    }


    /**
     * Sets the index position.
     *
     * @param index the new index position
     */
    public void setIndexPosition(int index) {

        if(index<=0)
            this.index=1;
        else
        this.index = index;
    }

}
