package com.synchroteam.mvp.view;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.synchroteam.TypefaceLibrary.TextView;
import com.synchroteam.beans.Description;
import com.synchroteam.beans.FamiliesBean;
import com.synchroteam.beans.Item;
import com.synchroteam.beans.UpdateDataBaseEvent;
import com.synchroteam.beans.UpdateUiAfterSync;
import com.synchroteam.beans.User;
import com.synchroteam.dao.Dao;
import com.synchroteam.dialogs.AppUpdateDialog;
import com.synchroteam.dialogs.AuthenticationErrorDialog;
import com.synchroteam.dialogs.ErrorDialog;
import com.synchroteam.dialogs.MultipleDeviceNotSupported;
import com.synchroteam.dialogs.SynchronizationErrorDialog;
import com.synchroteam.events.ReportFamilyUpdateEvent;
import com.synchroteam.events.ReportViewUpdateEvent;
import com.synchroteam.listadapters.ReportListAdapter;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.utils.AsyncTaskCoroutine;
import com.synchroteam.utils.DaoManager;
import com.synchroteam.utils.DialogUtils;
import com.synchroteam.utils.KEYS;
import com.synchroteam.utils.Logger;
import com.synchroteam.utils.SynchroteamUitls;
import com.synchroteam.utils.TouchImageView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import de.greenrobot.event.EventBus;

/**
 * Holds a list of card views which will show the report filled by the user in the ReportJobDetailFragment.
 * By clicking the download button, user can able to download the report.
 * <p>
 * Created by Trident on 11/23/2016.
 */

public class ReportViewFragmentNew extends Fragment {

    private static final String TAG = ReportViewFragmentNew.class.getSimpleName();
    ReportListAdapter reportListAdapter;
    private ListView list_report;
    private TextView txtDownloadReport;
    private ProgressDialog progressDSynch;
    private String idJob;
    private String jobPublicLink;
    private ArrayList<FamiliesBean> listFamilies;
    private Dao dao;
    private View footerViewNew;
    private int siteCount;
    private int index = 1;

    private int listIndex = 1;
    private int listSBCatCount = 0;
    private boolean isUserScrolled = false;
    int previousFamilyId = 0;
    int iterationCount = 1;
    boolean loadMore = true;

    /**
     * The handler.
     */
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            String erreur = (String) msg.obj;
            EventBus.getDefault().post(new UpdateDataBaseEvent());

            if (erreur.equals("ok")) {
                Toast.makeText(getActivity(),
                                getString(R.string.msg_synch_ok), Toast.LENGTH_LONG)
                        .show();
                EventBus.getDefault().post(new UpdateUiAfterSync());


                jobPublicLink = dao.getInterventionById(idJob).getPublicLinkInterv();
                String downloadLink = jobPublicLink + "/PDF";
                openLinkInBrowser(downloadLink);

            } else if (erreur.equals("4001") || erreur.equals("4000")) {
                showAuthErrDialog();
            } else if (erreur.equals("4006")) {
                Toast.makeText(getActivity(),
                        getString(R.string.msg_synch_error_4),
                        Toast.LENGTH_LONG).show();
            } else if (erreur.equals("4101")) {
                showMultipleDeviecDialog();
            } else if (erreur.equals("4005")) {
                showAppUpdatetDialog();
            } else if (erreur.equals("4003")) {
                showErrMsgDialog(getString(R.string.msg_sync_error_4003));
            } else {
//                Toast.makeText(getActivity(),
//                        getString(R.string.msg_synch_error_new), Toast.LENGTH_LONG)
//                        .show();
                showSyncFailureMsgDialog(getString(R.string.msg_synch_error_new));
            }

        }
    };
    /**
     * Click listener
     */
    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.txt_download_report:
                    synch();
                    break;
            }
        }
    };

    public static ReportViewFragmentNew getInstance() {
        return new ReportViewFragmentNew();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_report_view_new, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initiateViews(view);
        dao = DaoManager.getInstance();
        getValues();

        new AttachReportAsyncTask().execute();
        setDownloadButtonVisibility();
        EventBus.getDefault().registerSticky(this);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        setRetainInstance(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    /**
     * Event for getting position of updated family
     *
     * @param reportFamilyUpdateEvent
     */
    public void onEvent(ReportFamilyUpdateEvent reportFamilyUpdateEvent) {
        updateFamilyItems(reportFamilyUpdateEvent.familyPosition);

        //new changes for hiding keyboard v48
        if (getActivity() != null) {
            getActivity().getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            getActivity().getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        }

    }

    /**
     * Event for update the UI after sync.
     *
     * @param updateUiAfterSync the update ui after sync
     */
    public void onEvent(UpdateUiAfterSync updateUiAfterSync) {
//        getFamilies();
//        inflateViews();
        new AttachReportAsyncTask().execute();
    }

    /**
     * Event for updating the whole report view (for the event after adding shared blocks)
     *
     * @param reportViewUpdateEvent
     */
    public void onEvent(ReportViewUpdateEvent reportViewUpdateEvent) {
//        getFamilies();
//        inflateViews();
        new AttachReportAsyncTask().execute();
    }

    /**
     * Initiates all views
     *
     * @param view
     */
    private void initiateViews(View view) {
        list_report = (ListView) view.findViewById(R.id.list_report);
        txtDownloadReport = (TextView) view.findViewById(R.id.txt_download_report);
        footerViewNew = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.layout_footerview_items_list, null, false);

        txtDownloadReport.setOnClickListener(clickListener);
    }

    /**
     * To check if the technician is subcontractor
     *
     * @return result
     */
    private boolean isSubContractor() {
        boolean result = false;
        User user = dao.getUser();

        int idProfil = user.getIdProfil();
        int flSubContractor = user.getFlSubContractor();
        if (idProfil == 4 && flSubContractor == 1) {
            result = true;
        }
        return result;
    }

    /**
     * Sets visibility for download button based
     */
    private void setDownloadButtonVisibility() {

        Description description = dao.getInterventionById(idJob);
        if (description != null) {
            jobPublicLink = description.getPublicLinkInterv();
            boolean isSubContr = isSubContractor();
            Logger.log("TAG", "isSubContr values is===> "
                    + isSubContr);
            if (jobPublicLink != null && !TextUtils.isEmpty(jobPublicLink)) {
                txtDownloadReport.setVisibility(View.VISIBLE);
            } else {
                txtDownloadReport.setVisibility(View.GONE);
            }
        } else {
            txtDownloadReport.setVisibility(View.GONE);
        }
    }

    /**
     * Sets font awesome typeface for textview.
     */
    public void setFATypeFace(android.widget.TextView txtView) {
        Typeface typeFace = Typeface.createFromAsset(getActivity().getAssets(), getActivity()
                .getResources().getString(R.string.fontName_fontAwesome));
        txtView.setTypeface(typeFace);
    }

    /**
     * get bundle values from parent fragment
     */
    private void getValues() {
        Bundle bundle = getArguments();
        idJob = bundle.getString(KEYS.CurrentJobs.ID);
    }

    /**
     * Get list of families from DAO.
     */
    private void getFamilies() {
//        if (listFamilies == null) {
//            listFamilies = new ArrayList<>();
//        } else {
//            listFamilies.clear();
//        }
//
//        dao = DaoManager.getInstance();
//        ArrayList<FamiliesBean> vect = dao.getSBCategory(idJob);
//        Enumeration<FamiliesBean> en = vect.elements();
//
//        while (en.hasMoreElements()) {
//            FamiliesBean fm = en.nextElement();
//            HashMap<Integer, Item> items = dao.getItemsForPreview(
//                    idJob, fm.getIdFamily(), fm.getIteration());
//            ArrayList<Item> vectorItem = cleanListItem(items);
//            fm.setItems(vectorItem);
//            listFamilies.add(fm);
//        }
    }

    public void openMapIntent(String lat, String lon) {
        if (!SynchroteamUitls.isGoogleMapInstalled(getActivity())) {
            SynchroteamUitls.showGoogleMapsDialog(getActivity());
            return;
        } else {
            try {
                if ((!TextUtils.isEmpty(lat)) && (!TextUtils.isEmpty(lon))) {
                    Intent myIntent = new Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("geo:0,0?q=" + lat + "," + lon + "&z=20"));
                    getActivity().startActivity(myIntent);
                }
            } catch (Exception e) {
                Logger.printException(e);
            }
        }
    }

    /**
     * Gets the value format.
     *
     * @param value      the value
     * @param idTypeItem the id type item
     * @return the value format
     */
    public String getValueFormat(String value, int idTypeItem) {

        String locale = getResources().getConfiguration().locale
                .getDisplayName();
        String langue = locale.substring(0, locale.indexOf(" "));
        switch (idTypeItem) {
            case 0:
                if (!TextUtils.isEmpty(value)) {

                    int val = 0;
                    try {
                        val = Integer.parseInt(value);
                    } catch (Exception e) {

                    }

                    if (val == 1)
                        value = getString(R.string.textComplaintLable);
                    else if (val == 2)
                        value = getString(R.string.textNotComplaintLable);

                } else
                    value = getString(R.string.non_renseigne);

                break;

            case 6:
                if (!TextUtils.isEmpty(value) && langue.equalsIgnoreCase("english")) {

                    String[] tab = value.split(" ");
                    String[] tab2 = tab[0].split("/");
                    String jj, mm;

                    if (Integer.parseInt(tab2[0]) < 10 && tab2[0].length() == 1)
                        jj = "0" + tab2[0];
                    else
                        jj = tab2[0];

                    if (Integer.parseInt(tab2[1]) < 10 && tab2[1].length() == 1)
                        mm = "0" + tab2[1];
                    else
                        mm = tab2[1];
                    value = mm + "/" + jj + "/" + tab2[2] + " " + tab[1];

                }
                break;

            case 7:
                if (!TextUtils.isEmpty(value)) {
                    int val = 0;
                    try {
                        val = Integer.parseInt(value);
                    } catch (Exception e) {

                    }

                    if (val == 1)
                        value = getString(R.string.exist);
                    else
                        value = getString(R.string.notExist);

                } else
                    value = getString(R.string.notExist);

        }
        return value;
    }

    /**
     * Clean list item.
     *
     * @param map the map
     * @return the vector
     */
    public ArrayList<Item> cleanListItem(HashMap<Integer, Item> map) {
        ArrayList<Item> vect = new ArrayList<Item>();
        Iterator<Item> iter = map.values().iterator();
        while (iter.hasNext()) {
            Item item1 = (Item) iter.next();
            item1 = listeItems(map, item1);
            if (item1 != null && item1.getFlPrivate() == 0)
                vect.add(item1);
        }
        Collections.sort(vect);
        return vect;
    }

    /**
     * Liste items.
     *
     * @param map  the map
     * @param item the item
     * @return the item
     */
    public Item listeItems(HashMap<Integer, Item> map, Item item) {

        if (item.getCond() == 0) {
            return item;
        } else {
            Item itemP = map.get(item.getCond());
            if (itemP != null) {
                if (RecursiveCompare(item, item, itemP, map)) {
                    return item;
                } else
                    return null;
            } else {
                return null;
            }
        }

    }

    /**
     * Recursive compare.
     *
     * @param itemR the item r
     * @param itemF the item f
     * @param itemP the item p
     * @param map   the map
     * @return the boolean
     */
    public Boolean RecursiveCompare(Item itemR, Item itemF, Item itemP,
                                    HashMap<Integer, Item> map) {

        try {
            if (CompareValues(itemF, itemP)) {

                if (itemP.getCond() != 0) {

                    return RecursiveCompare(itemR, itemP, map.get(itemP.getCond()),
                            map);
                } else
                    return true;
            } else
                return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    /**
     * Compare values.
     *
     * @param itemF the item f
     * @param itemP the item p
     * @return the boolean
     */
    public Boolean CompareValues(Item itemF, Item itemP) throws Exception {
        Boolean drp = false;

        if (itemP.getValItem() == 1 || itemP.getIdTypeItem() == 7
                || itemP.getIdTypeItem() == 8 || itemP.getIdTypeItem() == 9) {
            String operator = "", value = "";
            Date dF, dP;

            switch (itemP.getIdTypeItem()) {
                case 3:
                    operator = itemF.getVal_cond().substring(0, 1);
                    value = itemF.getVal_cond().substring(1);
                    dF = getDateF(value);
                    dP = getDateF(itemP.getValeurNet());

                    if (operator.equals("=")) {
                        if (dF.getTime() == dP.getTime())
                            drp = true;
                    } else if (operator.equals("<")) {
                        if (dF.getTime() > dP.getTime())
                            drp = true;
                    } else if (operator.equals(">")) {
                        if (dF.getTime() < dP.getTime())
                            drp = true;
                    }

                    break;
                case 4:
                    operator = itemF.getVal_cond().substring(0, 1);
                    value = itemF.getVal_cond().substring(1);
                    dF = getTimes(value);
                    dP = getTimes(itemP.getValeurNet());

                    if (operator.equals("=")) {
                        if (dF.getTime() == dP.getTime())
                            drp = true;
                    } else if (operator.equals("<")) {
                        if (dF.getTime() > dP.getTime())
                            drp = true;
                    } else if (operator.equals(">")) {
                        if (dF.getTime() < dP.getTime())
                            drp = true;
                    }

                    break;
                case 5:
                    operator = itemF.getVal_cond().substring(0, 1);
                    Double val = Double.parseDouble(itemF.getVal_cond()
                            .substring(1));

                    Double valp;
                    try {
                        valp = Double.parseDouble(itemP.getValeurNet());
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        valp = 0.00;

                    }

                    if (operator.equals("=")) {

                        if (val.equals(valp))
                            drp = true;
                    } else if (operator.equals("<")) {
                        if (val > valp)
                            drp = true;
                    } else if (operator.equals(">")) {
                        if (val < valp)
                            drp = true;
                    }

                    break;
                case 6:
                    break;
                case 7:
                    if (itemF.getVal_cond().equals("1")) {
                        if (itemP.getValeurNet().equals("1"))
                            drp = true;
                    } else if (itemF.getVal_cond().equals("2")) {
                        if (itemP.getValeurNet().equals("2")
                                || itemP.getValeurNet().equals(""))
                            drp = true;
                    }

                    break;
                case 8:
                    if (itemF.getVal_cond().equals("1")) {
                        if (itemP.getValeurNet().equals("1"))
                            drp = true;
                    } else if (itemF.getVal_cond().equals("2")) {
                        if (itemP.getValeurNet().equals("2")
                                || itemP.getValeurNet().equals(""))
                            drp = true;
                    }

                    break;
                case 9:
                    if (itemF.getVal_cond().equals("1")) {
                        if (!itemP.getValeurNet().equals(""))
                            drp = true;
                    } else if (itemF.getVal_cond().equals("2")) {
                        if (itemP.getValeurNet().equals("2")
                                || itemP.getValeurNet().equals(""))
                            drp = true;
                    }

                    break;

//                default:
//                    if (itemF.getVal_cond().equals(itemP.getValeurNet()))
//                        drp = true;
//                    break;
                case 11:
                    operator = itemF.getVal_cond().substring(0, 1);
                    value = itemF.getVal_cond().substring(1);
                    dF = getTimes(value);
                    dP = getTimes(itemP.getValeurNet());

                    if (operator.equals("=")) {
                        if (dF.getTime() == dP.getTime())
                            drp = true;
                    } else if (operator.equals("<")) {
                        if (dF.getTime() > dP.getTime())
                            drp = true;
                    } else if (operator.equals(">")) {
                        if (dF.getTime() < dP.getTime())
                            drp = true;
                    }

                    break;

                default:
//                    if (itemF.getVal_cond().contains("@@@")) {
//
//                    } else if (itemF.getVal_cond().equals(itemP.getValeurNet()))
//                        drp = true;

                    String[] test = itemF.getVal_cond().split("@@@");
                    if (test != null && test.length > 0) {
                        for (int i = 0; i < test.length; i++) {
//                            if (itemP.getValeurNet().contains("@@@")) {
                            String[] test1 = itemP.getValeurNet().split("@@@");
                            if (test1 != null && test1.length > 0) {
                                for (int j = 0; j < test1.length; j++) {
                                    if (test[i].trim().equals(test1[j].trim())) {
                                        drp = true;
                                        break;
                                    }
                                }
//                                }
                            } else if (test[i].trim().equals(itemP.getValeurNet().trim())) {
                                drp = true;
                                break;
                            }
                        }
                    }

                    break;
            }
        }
        return drp;
    }

    /**
     * Gets the date f.
     *
     * @param mDate the m date
     * @return the date f
     */
    public Date getDateF(String mDate) {
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS");
        Date date;
        try {
            date = df.parse(mDate + " 00:00:00.000");
            return date;
        } catch (ParseException e) {
            Logger.printException(e);
            return new Date();
        }
    }

    /**
     * Gets the times.
     *
     * @param mDate the m date
     * @return the times
     */
    public Date getTimes(String mDate) {
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS");
        Date date;
        try {
            date = df.parse("01/01/0001 " + mDate + ":00.000");
            return date;
        } catch (ParseException e) {
            Logger.printException(e);
            return new Date();
        }
    }

    /**
     * Show img.
     *
     * @param v      the v
     * @param btImag the bt imag
     */
    public void showImg(View v, Bitmap btImag) {

        final Dialog nagDialog = new Dialog(getActivity(),
                android.R.style.Theme_Translucent_NoTitleBar);
        nagDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        nagDialog.setCancelable(true);
        nagDialog.setContentView(R.layout.item_image);

        ImageView btnClose = (ImageView) nagDialog.findViewById(R.id.closeBtn);
        TouchImageView ivPreview = (TouchImageView) nagDialog
                .findViewById(R.id.imageItem);
        //

        // innerFrame.getLayoutParams().height = (deviceWidth * 60) / 100;
        // innerFrame.getLayoutParams().width = (deviceWidth * 60) / 100;
        //
        ivPreview.setImageBitmap(btImag);

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (nagDialog != null)
                    nagDialog.dismiss();
            }
        });
        nagDialog.show();
    }

    /**
     * Updates the items of a family which was changed in report by the user.
     *
     * @param familyPosition : position of the family
     */
    public void updateFamilyItems(int familyPosition) {
        FamiliesBean famille = listFamilies.get(familyPosition);
        HashMap<Integer, Item> items = dao.getItemsForPreview(
                idJob, famille.getIdFamily(), famille.getIteration());

        ArrayList<Item> vectorItem = famille.getItems();
        vectorItem.clear();

        ArrayList<Item> newvectorItem = cleanListItem(items);

        vectorItem.addAll(newvectorItem);

        Logger.log("Size of Items ", items.size() + "");

        listFamilies.set(familyPosition, famille);

        try {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (reportListAdapter != null)
                        reportListAdapter.notifyDataSetChanged();
                }
            });
        } catch (Exception e) {

        }
    }

    /***
     * Create a chooser of browsers to open the link
     *
     * @param link
     */
    protected void openLinkInBrowser(String link) {
        // TODO Auto-generated method stub
//        Intent intent = new Intent(Intent.ACTION_VIEW);
//        intent.setData(Uri.parse("https://drive.google.com/viewerng/viewer?embedded=true&url="+link));

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(link));

        // Always use string resources for UI text. This says something like
        // "Share this photo with"
        String title = getActivity().getString(R.string.titleBrowserSelection);
        // Create and start the chooser
        Intent chooser = Intent.createChooser(intent, title);
        getActivity().startActivity(chooser);

    }

    /**
     * Synch.
     */
    private void synch() {

        if (SynchroteamUitls.isNetworkAvailable(getActivity())) {
            progressDSynch = ProgressDialog.show(getActivity(),
                    getString(R.string.textPleaseWaitLable),
                    getString(R.string.msg_synch), true, false);
            Logger.output(TAG, " thread started");

            Thread syncDbToServer = new Thread((new Runnable() {

                @Override
                public void run() {

                    Message myMessage = new Message();
                    try {
                        User u = dao.getUser();
                        dao.sync(u.getLogin(), u.getPwd());
                        Logger.output(TAG, " finished sync");
                        Thread.sleep(3000);
                        myMessage.obj = "ok";

                        handler.sendMessage(myMessage);

                    } catch (Exception ex) {
                        String exception = ex.getMessage();
                        Logger.printException(ex);
                        if (exception != null) {
                            if (exception.indexOf("4001") != -1) {
                                myMessage.obj = "4001";
                            } else if (exception.indexOf("4000") != -1) {
                                myMessage.obj = "4000";
                            } else if (exception.indexOf("4006") != -1) {
                                myMessage.obj = "4006";
                            } else if (exception.indexOf("4101") != -1) {
                                myMessage.obj = "4101";
                            } else if (exception.indexOf("4005") != -1) {
                                myMessage.obj = "4005";
                            } else if (exception.indexOf("4003") != -1) {
                                myMessage.obj = "4003";
                            } else {
                                myMessage.obj = "Error";
                            }
                        } else {
                            myMessage.obj = "Error";
                        }

                        handler.sendMessage(myMessage);

                    } finally {
                        if (progressDSynch != null
                                && progressDSynch.isShowing())
                            progressDSynch.dismiss();

                    }

                }
            }));
            syncDbToServer.start();
        } else {

            EventBus.getDefault().post(new UpdateDataBaseEvent());
            if (!getActivity().isFinishing()) {
                SynchroteamUitls.showToastMessage(getActivity());
            }
        }

    }

    /**
     * Show error dialog
     */
    protected void showErrMsgDialog(String errMsg) {

        ErrorDialog errDialog = new ErrorDialog(getActivity(), errMsg);

        errDialog.show();
    }

    /**
     * For showing the synchronization failure messages
     */
    protected void showSyncFailureMsgDialog(String syncFailureMsg) {

        SynchronizationErrorDialog synchronizationErrorDialog = new SynchronizationErrorDialog
                (getActivity(), syncFailureMsg, new SynchronizationErrorDialog
                        .SynchronizationErrorInterface() {
                    @Override
                    public void doOnOkayClick() {
                        //perform any action
                    }
                });

        synchronizationErrorDialog.show();
    }

    /**
     * Show authentication error dialog
     */
    protected void showAuthErrDialog() {
        User user = dao.getUser();
        AuthenticationErrorDialog authenticationErrorDialog = new AuthenticationErrorDialog(
                getActivity(), user.getLogin());

        authenticationErrorDialog.show();
    }

    /**
     * Show app update dialog
     */
    protected void showAppUpdatetDialog() {

        AppUpdateDialog appUpdateDialog = new AppUpdateDialog(getActivity());

        appUpdateDialog.show();
    }

    /**
     * Show multiple user dialog
     */
    protected void showMultipleDeviecDialog() {

        MultipleDeviceNotSupported multipleDeviceDialog = new MultipleDeviceNotSupported(
                getActivity(),
                new MultipleDeviceNotSupported.MultipleDeviceInterface() {

                    @Override
                    public void doOnOkClick() {
                    }

                    @Override
                    public void doOnLinkClick() {
                        if (Locale.getDefault().getLanguage()
                                .equalsIgnoreCase("fr")) {
                            openLinkInBrowser(getString(R.string.txtInfoFr));
                        } else if (Locale.getDefault().getLanguage()
                                .equalsIgnoreCase("es")) {
                            openLinkInBrowser(getString(R.string.txtInfoEs));
                        } else {
                            openLinkInBrowser(getString(R.string.txtInfoEn));
                        }
                    }
                });
        multipleDeviceDialog.show();
    }

    private void updateScrollViewList() {
        list_report.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view,
                                             int scrollState) {


                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isUserScrolled = true;
                }


            }


            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                if (reportListAdapter != null) {
                    int ctadapter = reportListAdapter.getCount();

//                    if (ctadapter >= reportItemCount) {
//                        hideFooterViewNew();
//                    } else {
//                        UpdateSharedBlock();
//                        showFooterViewNew();
//                    }

                    if (loadMore && isUserScrolled && firstVisibleItem +
                            visibleItemCount == totalItemCount) {
                        isUserScrolled = false;
                        loadMore = false;

                        if (ctadapter >= siteCount) {
                            // listParts.removeFooterView(footerView);
                            hideFooterViewNew();
//                            isUserScrolled = false;
                        } else {

                            if (totalItemCount > 0) {
                                Logger.log("TAG", "SHARED BLOCK QUERY CALL===>" + ctadapter);
                                Logger.log("TAG", "SHARED BLOCK" +
                                        " BEFORE QUERY ====>");
//                                UpdateSharedBlock();

                                new UpdateReportAsyncTask().execute();

//                                showFooterViewNew();

                            }
                        }


                    }
                }
            }

        });
    }

    /**
     * Updates the parts services list.
     */
    private void UpdateSharedBlock() {
//        DialogUtils
//                .showProgressDialog(jobDetails, jobDetails
//                        .getString(R.string.textWaitLable), jobDetails
//                        .getString(R.string.chargement), false);

        showFooterViewNew();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {

                    listIndex = listIndex + 15;
                    ArrayList<FamiliesBean> list = new ArrayList<>();
                    //todo add the query for the fetching the shared block
                    list = updateListQuery();


                    ArrayList<FamiliesBean> finalList = list;
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            updateAdapter(finalList, listIndex);

                        }
                    });


                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        }, 600);//def 980


    }

    private ArrayList<FamiliesBean> updateListQuery() {

        ArrayList<FamiliesBean> list = new ArrayList<>();

        ArrayList<FamiliesBean> familyList = dao.getSBCategoryUpdated(idJob,
                listIndex, previousFamilyId, iterationCount);

        for (int i = 0; i < familyList.size(); i++) {

            FamiliesBean fm = familyList.get(i);

            HashMap<Integer, Item> items = dao.getItemsForPreview(
                    idJob, fm.getIdFamily(), fm.getIteration());


            ArrayList<Item> vectorItem = cleanListItem(items);
            fm.setItems(vectorItem);


            previousFamilyId = fm.getIdFamily();
            iterationCount = fm.getIterationCount();
            listFamilies.add(fm);
        }

        Logger.log("TAG", "SHARED BLOCK" +
                " AFTER QUERY ====>");
        return list;
    }

    public void updateAdapter(final ArrayList<FamiliesBean> list, int listIndex) {
//        getActivity().runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//
//            }
//        });
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                if (list != null) {

                    listFamilies.addAll(list);

                    if (reportListAdapter != null) {
                        reportListAdapter.notifyDataSetChanged();
                        if (listIndex <= listFamilies.size() - 1)
                            reportListAdapter.setIndexPosition(listIndex);
                        loadMore = true;
                    }
//                    loadMore = true;
                }

            }
        }, 0);


//        DialogUtils.dismissProgressDialog();
        hideFooterViewNew();

//        if (footerViewNew != null && footerViewNew.isShown()) {
//            index++;
//            if (reportsExpandableListAdapter != null) {
//                reportsExpandableListAdapter.setIndexPosition(index);
//
//                try {
//                    Thread.sleep(900);
//                } catch (InterruptedException e) {
//                    Logger.printException(e);
//                }
//                reportsExpandableListAdapter.notifyDataSetChanged();
//            }
//        }
    }


    public void hideFooterViewNew() {
        if (footerViewNew != null) {
            if (footerViewNew.getVisibility() == View.VISIBLE) {
                footerViewNew.setVisibility(View.INVISIBLE);
            }
        }

    }

    public void showFooterViewNew() {
        if (footerViewNew != null) {
            if (footerViewNew.getVisibility() == View.INVISIBLE) {
                footerViewNew.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * The Class SaveNewJobDataAsyncTask.
     */
    private class AttachReportAsyncTask extends
            AsyncTaskCoroutine<String, Boolean> {


        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#onPreExecute()
         */
        @Override
        public void onPreExecute() {
            super.onPreExecute();
            if (getActivity() != null) {
                Logger.log("TAG", "onPreExecute: getActivity() is not null");
                DialogUtils.showProgressDialog(
                        getActivity(),
                        getActivity().getString(R.string.textWaitLable),
                        getActivity().getString(R.string.chargement), false);
            } else {
                Logger.log("KARAN1", "onPreExecute: getActivity() is null");
            }


            if (listFamilies == null) {
                listFamilies = new ArrayList<>();
            } else {
                listFamilies.clear();
            }

            listIndex = 1;
            previousFamilyId = 0;
            iterationCount = 1;
        }

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#doInBackground(Params[])
         */
        @Override
        public Boolean doInBackground(String... params) {

            dao = DaoManager.getInstance();
//            Vector<FamiliesBean> vect = dao.getSBCategory(idJob);
//            Enumeration<FamiliesBean> en = vect.elements();
//
//            while (en.hasMoreElements()) {
//                FamiliesB fm = en.nextElement();
//                HashMap<Integer, Item> items = dao.getItemsForPreview(
//                        idJob, fm.getIdFamily(), fm.getIteration());
//                ArrayList<Item> vectorItem = cleanListItem(items);
//                fm.setItems(vectorItem);
//                listFamilies.add(fm);
//            }

            listSBCatCount = dao.getSBCategoryCount(idJob);


            ArrayList<FamiliesBean> familyList = dao.getSBCategoryUpdated(idJob,
                    listIndex, previousFamilyId, iterationCount);


            for (int i = 0; i < familyList.size(); i++) {

                FamiliesBean fm = familyList.get(i);

                HashMap<Integer, Item> items = dao.getItemsForPreview(
                        idJob, fm.getIdFamily(), fm.getIteration());


                ArrayList<Item> vectorItem = cleanListItem(items);
                fm.setItems(vectorItem);


                previousFamilyId = fm.getIdFamily();
                iterationCount = fm.getIterationCount();
                listFamilies.add(fm);
            }


            return true;
        }

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        public void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            siteCount = listSBCatCount;
            if (getActivity() != null) {
                Logger.log("karan","onPostExecute: getActivity() is not null");
                if (reportListAdapter == null) {
                    if (listFamilies != null) {
                        reportListAdapter = new ReportListAdapter(
                                getActivity(), listFamilies, dao, idJob,
                                ReportViewFragmentNew.this);
                    }
                    if (reportListAdapter != null) {
                        reportListAdapter.setIndexPosition(1);
                        list_report.setAdapter(reportListAdapter);
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            Logger.printException(e);
                        }
                    }
                } else {
                    try {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (reportListAdapter != null)
                                    reportListAdapter.notifyDataSetChanged();
                            }
                        });
                    } catch (Exception e) {

                    }
                }

                if (siteCount > 20) {
                    loadMore = true;
//                list_report.addFooterView(footerViewNew);
                }
                updateScrollViewList();
                DialogUtils.dismissProgressDialog();
            }else {
                Logger.log("karan","onPostExecute: getActivity() is  null");
            }

        }
    }

//    @Override
//    public void onDetach() {
//        super.onDetach();
//    }

        /**
         * The Class SaveNewJobDataAsyncTask.
         */
        private class UpdateReportAsyncTask extends AsyncTaskCoroutine<String, Boolean> {

            ArrayList<FamiliesBean> list;


            public UpdateReportAsyncTask() {
                list = new ArrayList<>();
                listIndex = listIndex + 15;
            }

            @Override
            public void onPreExecute() {
                super.onPreExecute();

                DialogUtils
                        .showProgressDialog(getActivity(), getActivity()
                                .getString(R.string.textWaitLable), getActivity()
                                .getString(R.string.chargement), false);

                hideFooterViewNew();
//            showFooterViewNew();

            }


            @Override
            public Boolean doInBackground(String... params) {

                ArrayList<FamiliesBean> list = new ArrayList<>();

                ArrayList<FamiliesBean> familyList = dao.getSBCategoryUpdated(idJob,
                        listIndex, previousFamilyId, iterationCount);

                for (int i = 0; i < familyList.size(); i++) {

                    FamiliesBean fm = familyList.get(i);

                    HashMap<Integer, Item> items = dao.getItemsForPreview(
                            idJob, fm.getIdFamily(), fm.getIteration());


                    ArrayList<Item> vectorItem = cleanListItem(items);
                    fm.setItems(vectorItem);


                    previousFamilyId = fm.getIdFamily();
                    iterationCount = fm.getIterationCount();
                    listFamilies.add(fm);
                }

                return true;

            }

            /*
             * (non-Javadoc)
             *
             * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
             */
            @Override
            public void onPostExecute(Boolean result) {
                super.onPostExecute(result);

                DialogUtils.dismissProgressDialog();


                Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        try {

                            if (list != null) {

                                listFamilies.addAll(list);

                                if (reportListAdapter != null) {
                                    if (listIndex <= listFamilies.size() - 1)
                                        reportListAdapter.setIndexPosition(listIndex);
                                    else
                                        reportListAdapter.setIndexPosition(listFamilies.size() - 1);

                                    reportListAdapter.notifyDataSetChanged();
                                    loadMore = true;
                                }

                            }

                            Thread.sleep(1000);

                        } catch (Exception ex) {
                            Logger.printException(ex);

                        }
                    }
                }, 0);

//            hideFooterViewNew();

            }
        }

    }

