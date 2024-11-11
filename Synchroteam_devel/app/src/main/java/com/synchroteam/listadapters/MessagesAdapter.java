package com.synchroteam.listadapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.synchroteam.TypefaceLibrary.TextView;
import com.synchroteam.beans.Message_oper;
import com.synchroteam.dao.Dao;
import com.synchroteam.fragment.BaseFragment;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.utils.KEYS;
import com.synchroteam.utils.MaterialDesignUtils;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Adapter class for recycler view in messages screen.
 * Created by Trident on 2/14/2017.
 */

public class MessagesAdapter extends RecyclerView.Adapter {

    private Activity activity;
    private List<Message_oper> messagesBeans;
    private LayoutInflater layoutInflater;
    private Dao dataAccessObject;
    private BaseFragment baseFragment;
    private Typeface tfAvenir;

    public class VHMessages extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private LinearLayout linMsg;
        private TextView txtMsgTitle;
        private TextView txtMsgContent;
        private AlertDialog.Builder builder;

        public VHMessages(View itemView) {
            super(itemView);
            linMsg = (LinearLayout) itemView.findViewById(R.id.lin_msg);
            txtMsgTitle = (TextView) itemView.findViewById(R.id.tv_msg_title);
            txtMsgContent = (TextView) itemView.findViewById(R.id.tv_msg_content);
            builder = new AlertDialog.Builder(activity);

            MaterialDesignUtils.getInstance(activity).setRippleEffect(linMsg);

            itemView.setTag(false);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Boolean isArrowUp = (Boolean) view.getTag();
            boolean a = isArrowUp;
            if (a) {
                txtMsgContent.setMaxLines(1);
                view.setTag(false);
            } else {
                if (messagesBeans.get(getLayoutPosition()).getEtat() == 0) {
                    dataAccessObject.majMsgLu(messagesBeans.get(getLayoutPosition()).getId());
                    txtMsgTitle.setTypeface(tfAvenir, Typeface.NORMAL);
                    txtMsgContent.setTypeface(tfAvenir, Typeface.NORMAL);
                    messagesBeans.get(getLayoutPosition()).setEtat(1);
//                    notifyItemChanged(getLayoutPosition());
                    baseFragment.listUpdate();
                }
                txtMsgContent.setMaxLines(Integer.MAX_VALUE);
                view.setTag(true);
            }
        }

        @Override
        public boolean onLongClick(View view) {
            builder.setMessage(R.string.txt_confirm);
            builder.setCancelable(false);
            builder.setPositiveButton(R.string.textYesLable, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int idi) {
                    deleteMessage(messagesBeans.get(getLayoutPosition()).getId(), getLayoutPosition());
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

        public void deleteMessage(int id, int position) {
            dataAccessObject.deleteMsg(id);
            messagesBeans.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, messagesBeans.size()); //getLayoutPosition() is changed into position
            baseFragment.listUpdate();
            notifyDataSetChanged(); // added new line for View.java
        }
    }

    public MessagesAdapter(Activity activity,
                           List<Message_oper> messagesBeans, Dao dataAccessObject, BaseFragment baseFragment) {
        this.activity = activity;
        this.messagesBeans = messagesBeans;
        this.dataAccessObject = dataAccessObject;
        this.baseFragment = baseFragment;
        tfAvenir = Typeface.createFromAsset(activity.getAssets(), activity.getString(R.string.fontName_avenir));
    }

    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.rowview_messages_item, parent, false);
        return new VHMessages(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        VHMessages holderMsg = (VHMessages) holder;
        Message_oper message = messagesBeans.get(position);

        holderMsg.txtMsgTitle.setText(message.getTitre());
        holderMsg.txtMsgContent.setText(message.getCorp());

        if (message.getPriorite() == KEYS.Messages.PRIORITY_HIGH) {
            holderMsg.txtMsgTitle.setTextColor(ContextCompat.getColor(activity, R.color.textMessageHIghPriority));
        } else if (message.getPriorite() == KEYS.Messages.PRIORITY_LOW) {
            holderMsg.txtMsgTitle.setTextColor(ContextCompat.getColor(activity, R.color.textMessageLowPriority));
        } else {
            holderMsg.txtMsgTitle.setTextColor(ContextCompat.getColor(activity, R.color.textMessageAveragePriority));
        }

        if (message.getEtat() == 0) {
            holderMsg.txtMsgTitle.setTypeface(tfAvenir, Typeface.BOLD);
            holderMsg.txtMsgContent.setTypeface(tfAvenir, Typeface.BOLD);
        } else {
            holderMsg.txtMsgTitle.setTypeface(tfAvenir, Typeface.NORMAL);
            holderMsg.txtMsgContent.setTypeface(tfAvenir, Typeface.NORMAL);
        }
    }

    @Override
    public int getItemCount() {
        return messagesBeans.size();
    }
}
