package com.synchroteam.dialogs;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.synchroteam.synchroteam3.R;

public class CallingOptionDialog extends Dialog {

    Activity activity;
    String phoneNo;

    public static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 125;
    public static final int REQUEST_READ_CONTACTS = 1;

    public CallingOptionDialog(Activity activity, String phoneNo) {
        super(activity, android.R.style.Theme_Translucent_NoTitleBar);
        setCancelable(false);
        setContentView(R.layout.dialog_dial_call_option);
        this.getWindow().setGravity(Gravity.CENTER);
        findViewById(R.id.normal_call_option).setOnClickListener(onClickListener);
        findViewById(R.id.watsapp_call_option).setOnClickListener(onClickListener);
        findViewById(R.id.cancelBtn).setOnClickListener(onClickListener);

        this.activity = activity;
        this.phoneNo = phoneNo;
        setCancelable(false);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onClick(View view) {
            int id = view.getId();
            switch (id) {
                case R.id.normal_call_option:
                    doNormalCall();
                    break;
                case R.id.watsapp_call_option:
                    doWatsappCall();
                    break;
                case R.id.cancelBtn:
                    dismiss();
            }
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void doWatsappCall() {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            callingPermissionForWatsapp();
        } else {
            dialPhone(phoneNo);
        }
    }


    @SuppressLint("Range")
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void dialPhone(String phoneNo) {
        String[] projection = new String[]{ContactsContract.Data._ID, ContactsContract.Data.DISPLAY_NAME, ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.CONTACT_ID};

        ContentResolver resolver = getContext().getContentResolver();
        Cursor cursor = resolver.query(
                ContactsContract.Data.CONTENT_URI,
                projection, null, null,
                ContactsContract.Contacts.DISPLAY_NAME);

        Log.e("DIAL", "WHATSAPP METHOD IS EXECUTED");

        String s1= null;
        Boolean whatsApp = false;

        while (cursor.moveToNext()) {
            long _id = cursor.getLong(cursor.getColumnIndex(ContactsContract.Data._ID));
            String displayName = cursor.getString(cursor.getColumnIndex(ContactsContract.Data.DISPLAY_NAME));
            String mimeType = cursor.getString(cursor.getColumnIndex(ContactsContract.Data.MIMETYPE));
            String number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            if (mimeType.equals("vnd.android.cursor.item/vnd.com.whatsapp.voip.call")) {
                s1=number.replaceAll("[^0-9]+", "");
                if (phoneNo.contains(s1)){
                    String voiceCallID = Long.toString(_id);
                    voiceCall(voiceCallID);
                    whatsApp = true;
                }
            }

        }
        if(whatsApp==false){
            Toast.makeText(activity, "Contact is not an Whatsapp number", Toast.LENGTH_SHORT).show();
        }
        cursor.close();
    }



    public void voiceCall(String id) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse("content://com.android.contacts/data/" + id),
                "vnd.android.cursor.item/vnd.com.whatsapp.voip.call");
        intent.setPackage("com.whatsapp");
        activity.startActivity(intent);
    }

    public void doNormalCall(){
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {
            callingPermissionPhone();
        } else {
            callingMethod(phoneNo);
        }
    }

    @SuppressLint("MissingPermission")
    private void callingMethod(String phoneNo) {

        final Intent intent = new Intent();
        intent.setAction(Intent.ACTION_DIAL);
        intent.setData(Uri.fromParts("tel", phoneNo+"", null));
        activity.startActivity(Intent.createChooser(intent, ""));

    }

    private void callingPermissionPhone() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                Manifest.permission.CALL_PHONE)) {


            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(activity);
            alertBuilder.setCancelable(true);
            alertBuilder.setTitle(activity.getString(R.string.app_name));
            alertBuilder.setMessage(activity.getString(R.string.str_phone_permission));
            alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                public void onClick(DialogInterface dialog, int which) {
                    ActivityCompat.requestPermissions(activity,
                            new String[]{Manifest.permission.CALL_PHONE},
                            MY_PERMISSIONS_REQUEST_CALL_PHONE);
                }
            });
            AlertDialog alert = alertBuilder.create();
            alert.show();

        } else {
            // No explanation needed; request the permission
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.CALL_PHONE},
                    MY_PERMISSIONS_REQUEST_CALL_PHONE);
        }
    }



    @RequiresApi(api = Build.VERSION_CODES.M)
    public void callingPermissionForWatsapp(){
        activity.requestPermissions(new String[]{Manifest.permission.READ_CONTACTS,Manifest.permission.CALL_PHONE}, REQUEST_READ_CONTACTS);

    }
}
