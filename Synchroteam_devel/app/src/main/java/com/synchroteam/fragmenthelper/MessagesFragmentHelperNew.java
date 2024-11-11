package com.synchroteam.fragmenthelper;

import android.app.AlertDialog;
import android.content.DialogInterface;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;

import com.synchroteam.baseactivity.SyncroTeamBaseActivity;
import com.synchroteam.beans.EnableSynchronizationAddJobEvent;
import com.synchroteam.beans.Message_oper;
import com.synchroteam.dao.Dao;
import com.synchroteam.fragment.BaseFragment;
import com.synchroteam.listadapters.MessagesAdapter;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.utils.DaoManager;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;
// TODO: Auto-generated Javadoc

/***
 * This class is responsible for functionality of Message Screen.
 *
 * @author Trident
 */
public class MessagesFragmentHelperNew implements HelperInterface {


    SyncroTeamBaseActivity syncroTeamBaseActivity;
    RecyclerView rvMessages;
    private ArrayList<Message_oper> message_opers;
    private MessagesAdapter messagesListAdapter;
    private LinearLayoutManager linearLayoutManager;
    private Dao dataAccessObject;
    private AlertDialog.Builder builder;
    private BaseFragment baseFragment;

    /**
     * Instantiates a new messages fragment helper.
     *
     * @param syncroTeamBaseActivity the syncro team base activity
     * @param baseFragment           the base fragment
     */
    public MessagesFragmentHelperNew(SyncroTeamBaseActivity syncroTeamBaseActivity, BaseFragment baseFragment) {
        this.syncroTeamBaseActivity = syncroTeamBaseActivity;
        dataAccessObject = DaoManager.getInstance();
        builder = new AlertDialog.Builder(syncroTeamBaseActivity);
        this.baseFragment = baseFragment;
    }

    @Override
    public View inflateLayout(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.layout_messages_new, container,
                false);
        initiateView(view);
        return view;
    }

    @Override
    public void initiateView(View v) {
        rvMessages = (RecyclerView) v.findViewById(R.id.rv_messages);
        linearLayoutManager = new LinearLayoutManager(syncroTeamBaseActivity);
        createAndInflateListView();
    }

    /**
     * Creates the and inflate list view.
     */
    private void createAndInflateListView() {
        if (message_opers != null) {
            message_opers.clear();
        } else {
            message_opers = new ArrayList<Message_oper>();
        }

        message_opers.addAll(dataAccessObject.getAllMsg());

        if (messagesListAdapter == null) {
            messagesListAdapter = new MessagesAdapter(syncroTeamBaseActivity, message_opers, dataAccessObject, baseFragment);
            rvMessages.setLayoutManager(linearLayoutManager);
            rvMessages.setAdapter(messagesListAdapter);

        } else {
            messagesListAdapter.notifyDataSetChanged();
        }

        EventBus.getDefault().post(new EnableSynchronizationAddJobEvent());
    }

    @Override
    public void doOnSyncronize() {
        // TODO Auto-generated method stub
        createAndInflateListView();

    }

    @Override
    public void onReturnToActivity(int requestCode) {
        // TODO Auto-generated method stub

    }

    /**
     * The item long click listener.
     */
    OnItemLongClickListener itemLongClickListener = new OnItemLongClickListener() {

        @Override
        public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
                                       long arg3) {
            final int position = arg2;
            builder.setMessage(R.string.txt_confirm);
            builder.setCancelable(false);
            builder.setPositiveButton(R.string.textYesLable, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int idi) {
                    deleteMessage(message_opers.get(position).getId(), position);
                }
            });
            builder.setNegativeButton(R.string.textNoLable, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();


            return false;
        }

    };

    /**
     * removes the message from particular position.
     *
     * @param id       the id
     * @param position the position
     */
    public void deleteMessage(int id, int position) {

        dataAccessObject.deleteMsg(id);
        message_opers.remove(position);
        messagesListAdapter.notifyDataSetChanged();
        baseFragment.listUpdate();

    }
}
