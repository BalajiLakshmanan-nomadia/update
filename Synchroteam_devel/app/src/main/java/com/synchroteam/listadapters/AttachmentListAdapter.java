package com.synchroteam.listadapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.synchroteam.beans.AttachmentsBeans;
import com.synchroteam.beans.Site;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.utils.Logger;

import java.util.ArrayList;

// TODO: Auto-generated Javadoc

/**
 * The Class AttachmentListAdapter. author Ideavate.solution
 */
@SuppressLint("DefaultLocale")
public class AttachmentListAdapter extends BaseAdapter {

    /**
     * The sites.
     */
    private ArrayList<AttachmentsBeans> attachmentsBeans;

    /**
     * The orignal list.
     */
    private ArrayList<AttachmentsBeans> orignalList;

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

    /**
     * The is user searching.
     */
    private boolean isUserSearching = false;

    private Activity activity;

    private int status;

    private boolean fromJobFragment;

    private static final String TAG = "AttachmentListAdapter";

    /**
     * Instantiates a new Attachment List adapter.
     *
     * @param activity         the activity
     * @param sites            the sites
     * @param dataAccessObject the data access object
     */
    public AttachmentListAdapter(Activity activity,
                                 ArrayList<AttachmentsBeans> attachmentsBeans) {
        // TODO Auto-generated constructor stub

        this.attachmentsBeans = attachmentsBeans;
        orignalList = new ArrayList<AttachmentsBeans>();
        orignalList.addAll(attachmentsBeans);

        this.activity = activity;
        layoutInflater = (LayoutInflater) activity
                .getSystemService(Service.LAYOUT_INFLATER_SERVICE);

        status = 0;

    }

    public AttachmentListAdapter(Activity activity,
                                 ArrayList<AttachmentsBeans> attachmentsBeans, int status,
                                 boolean fromJobFragment) {
        // TODO Auto-generated constructor stub

        this.attachmentsBeans = attachmentsBeans;
        orignalList = new ArrayList<AttachmentsBeans>();
        orignalList.addAll(attachmentsBeans);

        this.activity = activity;
        layoutInflater = (LayoutInflater) activity
                .getSystemService(Service.LAYOUT_INFLATER_SERVICE);

        this.status = status;
        this.fromJobFragment = fromJobFragment;

    }

    /**
     * The Class ViewHolder.
     */
    private class ViewHolder {

        LinearLayout linParentView;
        /**
         * The client name tv.
         */
        TextView clientNameLableTv;
        TextView viewAttachmentInBrowser;

    }

    /*
     * (non-Javadoc)
     *
     * @see android.widget.Adapter#getCount()
     */
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        if (!isUserSearching) {
            int count = index * baseCount;

            if (count < attachmentsBeans.size()) {

                return count;
            } else {
                return attachmentsBeans.size();
            }
        } else {
            return attachmentsBeans.size();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see android.widget.Adapter#getItem(int)
     */
    @Override
    public AttachmentsBeans getItem(int position) {
        // TODO Auto-generated method stub
        return attachmentsBeans.get(position);
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
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        final ViewHolder viewHolder;
        AttachmentsBeans attachmentsBeans = (AttachmentsBeans) getItem(position);

        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = layoutInflater.inflate(
                    R.layout.layout_attachment_list_item, parent, false);

            viewHolder.linParentView = (LinearLayout) convertView.findViewById(R.id.lin_attachment_container);
            viewHolder.clientNameLableTv = (TextView) convertView
                    .findViewById(R.id.clientNameLableTv);
            viewHolder.viewAttachmentInBrowser = (TextView) convertView
                    .findViewById(R.id.viewAttachmentInBrowser);

            Typeface fontAwesome = Typeface.createFromAsset(
                    activity.getAssets(),
                    activity.getString(R.string.fontName_fontAwesome));
            viewHolder.viewAttachmentInBrowser.setTypeface(fontAwesome);
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

		/* New change */
        /*
		 * To differentiate the attachment of the job and customer, a suffix is
		 * added with the name of the attachment. If the id is not null, it is a
		 * job attachment else it is a customer attachment. (There is no id for
		 * customer attachment).
		 */

        if (fromJobFragment) {
            if (attachmentsBeans.getIdIntervention() != null) {
                viewHolder.clientNameLableTv.setText(attachmentsBeans
                        .getNameAttachment()
                        + " ("
                        + activity.getString(R.string.intervention) + ")");
            }
            if (attachmentsBeans.getIdClient() != 0) {
                viewHolder.clientNameLableTv.setText(attachmentsBeans
                        .getNameAttachment()
                        + " ("
                        + activity.getString(R.string.textJobCustomerLableTv) + ")");
            }
            if (attachmentsBeans.getIdEquipment() != 0) {
                viewHolder.clientNameLableTv
                        .setText(attachmentsBeans.getNameAttachment()
                                + " ("
                                + activity
                                .getString(R.string.textEquipmentLable)
                                + ")");
            }
            if (attachmentsBeans.getIdSite() != 0) {
                viewHolder.clientNameLableTv.setText(attachmentsBeans
                        .getNameAttachment()
                        + " ("
                        + activity.getString(R.string.textSiteLable) + ")");
            }
        } else {
            viewHolder.clientNameLableTv.setText(attachmentsBeans
                    .getNameAttachment());
        }
        viewHolder.viewAttachmentInBrowser.setTag(attachmentsBeans
                .getUrlAttachment());
        viewHolder.viewAttachmentInBrowser
                .setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        openLinkInBrowser((String) (v.getTag()));

                    }
                });
        Logger.log(TAG, attachmentsBeans.getNameAttachment());

        /** New changes */
        viewHolder.linParentView.setTag(attachmentsBeans
                .getUrlAttachment());
        viewHolder.linParentView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                openLinkInBrowser((String) (view.getTag()));
            }
        });

        return convertView;
    }

    /**
     * Adds the site to orignal list.
     *
     * @param sites the sites
     */
    public void addSiteToOrignalList(ArrayList<Site> sites) {

        orignalList.clear();
        orignalList.addAll(attachmentsBeans);

    }

    /**
     * Sets the index position.
     *
     * @param index the new index position
     */
    public void setIndexPosition(int index) {
        // TODO Auto-generated method stub
        this.index = index;
    }

    /**
     * Gets the filter.
     *
     * @return the filter
     */
    // public Filter getFilter(){
    //
    // Filter filter=new Filter() {
    //
    // @SuppressWarnings("unchecked")
    // @Override
    // protected void publishResults(CharSequence constraint, FilterResults
    // results) {
    // // TODO Auto-generated method stub
    // attachmentsBeans.clear();
    // attachmentsBeans.addAll((ArrayList<Site>)results.values);
    //
    // notifyDataSetChanged();
    //
    //
    // }
    //
    // // @Override
    // // protected FilterResults performFiltering(CharSequence constraint) {
    // // // TODO Auto-generated method stub
    // // FilterResults results = new FilterResults();
    // // ArrayList<Site> aux = new ArrayList<Site>();
    // //
    // // // El prefijo tiene que ser mayor que 0 y existir
    // // if (constraint != null && constraint.toString().length() > 0) {
    // // isUserSearching=true;
    // // for (AttachmentsBeans site : orignalList) {
    // //
    // // String nom = site.getNmSite();
    // //
    // //
    // //
    // // if ((nom.toLowerCase().contains(constraint))) {
    // //
    // // aux.add(site);
    // //
    // // }
    // //
    // // }
    // // results.values = aux;
    // // results.count = aux.size();
    // //
    // //
    // // }
    // // else {
    // // synchronized (sites) {
    // // isUserSearching=false;
    // // results.values = orignalList;
    // // results.count = orignalList.size();
    // // }
    // // }
    // // return results;
    // //
    // // }
    // };
    //
    // return filter;
    //
    // }

    /***
     * Create a chooser of browsers to open the link
     *
     * @param link
     */
    protected void openLinkInBrowser(String link) {
        // TODO Auto-generated method stub
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(link));

        // Always use string resources for UI text. This says something like
        // "Share this photo with"
        String title = activity.getString(R.string.titleBrowserSelection);
        // Create and start the chooser
        Intent chooser = Intent.createChooser(intent, title);
        activity.startActivity(chooser);
    }

}
