package com.synchroteam.synchroteam;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.synchroteam.TypefaceLibrary.EditText;
import com.synchroteam.TypefaceLibrary.TextView;
import com.synchroteam.beans.Conge;
import com.synchroteam.beans.UnavailabilityBeans;
import com.synchroteam.beans.UpdateDataBaseEvent;
import com.synchroteam.dao.Dao;
import com.synchroteam.listadapters.IssuesListAdapterStartActivityRc;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.utils.AsyncTaskCoroutine;
import com.synchroteam.utils.DaoManager;
import com.synchroteam.utils.DateChecker;
import com.synchroteam.utils.DialogUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Vector;

import de.greenrobot.event.EventBus;


/**
 * @author Trident
 *         <p>
 *         This class is used to create an unavailability type and send it to
 *         the portal.
 *         </p>
 */
public class CreateUnavailability extends AppCompatActivity implements
        OnClickListener {

    // .............................UI...ELEMENTS...STARTS...HERE................................................

    private ActionBar actionBar;

    private RecyclerView unavailabilityListView;
    private EditText edtIssueDescription;
    private TextView txtCreate;
    private String notesDescription;
    private Calendar cal;

    private Dao dataAccessObject;
    private ArrayList<UnavailabilityBeans> unavailabilitiesList;
    // private SharedPreferences issuesPositionPref;
    private String pos;
    // private Editor edit;

    SimpleDateFormat sdf;

    private String idInterv, idUser;

    // private static final String TAG = "CreateUnavailability";

    // *****************************UI...ELEMENTS...ENDS...HERE**************************************************

    // .............................LIFECYCLE...METHODS...STARTS...HERE...........................................
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_unavailability);
        initializeUI();
    }

    @Override
    protected void onResume() {
        super.onResume();

        //New changes
        DateChecker.checkDateAndNavigate(this, dataAccessObject);

    }

    @Override
    protected void onDestroy() {
        DialogUtils.dismissProgressDialog();
        super.onDestroy();
    }

    // ............................OVERRIDDEN...METHOD...STARTS...HERE...........................................
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intentBack = new Intent(this, SyncoteamNavigationActivity.class);
        startActivity(intentBack);
        finish();
    }

    // @Override
    // protected void onStop() {
    // super.onStop();
    // edit.putString("pos", null);
    // edit.commit();
    //
    // }

    // ****************************OVERRIDDEN...METHOD...ENDS...HERE******************************************

    // *****************************LIFECYCLE...METHODS...ENDS...HERE**********************************************

    // .............................LOCAL...METHODS...STARTS...HERE................................................
    @SuppressLint("SimpleDateFormat")
    private void initializeUI() {
        actionBar = getSupportActionBar();
        dataAccessObject = DaoManager.getInstance();

        unavailabilitiesList = new ArrayList<UnavailabilityBeans>();
        unavailabilitiesList = (ArrayList<UnavailabilityBeans>) dataAccessObject
                .getUnavailabilities();

        unavailabilityListView = (RecyclerView) findViewById(R.id.unavailabilityListView);
        LinearLayoutManager llm=new LinearLayoutManager(CreateUnavailability.this,LinearLayoutManager.HORIZONTAL,false);
        unavailabilityListView.setLayoutManager(llm);
        edtIssueDescription = (EditText) findViewById(R.id.edtIssueDescription);
        txtCreate = (TextView) findViewById(R.id.txtCreate);
        // issuesPositionPref = getSharedPreferences("issues_pref",
        // Context.MODE_PRIVATE);
        // edit = issuesPositionPref.edit();
        cal = Calendar.getInstance();
        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle(getString(R.string.txt_start_activity));

        idInterv = dataAccessObject.getStartedJobId();
        idUser = " ";

        unavailabilityListView.setAdapter(new IssuesListAdapterStartActivityRc(
                this, unavailabilitiesList));
        txtCreate.setOnClickListener(this);

    }

    /**
     * Check for the unavailability which does not have an end date (i.e already
     * started) and update the end date of that unavailability to current date.
     */
    private void updateEndDateOfPreviousActivity() {

        Vector<Conge> vectConge = new Vector<Conge>();
        vectConge = dataAccessObject.getConge();
        Enumeration<Conge> enindisp = vectConge.elements();
        String currentDate = sdf.format(cal.getTime());
        while (enindisp.hasMoreElements()) {
            Conge indisp = enindisp.nextElement();
            if (indisp.getDtFin() == null) {
                dataAccessObject.updateUnavailabilityEndDate(
                        indisp.getIdConge(), currentDate);
            }
        }
    }

    public void setIssuePosition(int position) {
        pos = String.valueOf(position);
    }

    // *****************************LOCAL...METHODS...ENDS...HERE************************************************

    // .............................LISTENER...STARTS...HERE.......................................................
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txtCreate:
                // pos = issuesPositionPref.getString("pos", null);

                notesDescription = edtIssueDescription.getText().toString();

                if (pos == null) {
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.select_type), Toast.LENGTH_SHORT)
                            .show();
                } else {
                    new addUnavailabilityAsync().execute();
                }
                break;

        }
    }

    // *****************************LISTENER...ENDS...HERE*********************************************************

    /**
     * The Class SaveNewJobDataAsyncTask.
     */
    private class addUnavailabilityAsync extends
            AsyncTaskCoroutine<String, Boolean> {

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#onPreExecute()
         */
        @Override
        public void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();

            DialogUtils.showProgressDialog(CreateUnavailability.this,
                    getString(R.string.textWaitLable),
                    getString(R.string.textSavingAddJobData), false);
        }

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#doInBackground(Params[])
         */
        @SuppressLint("SimpleDateFormat")
        @Override
        public Boolean doInBackground(String... params) {
            // TODO Auto-generated method stub
            String currentDateStr = sdf.format(cal.getTime());
            updateEndDateOfPreviousActivity();

            boolean drp = dataAccessObject.addUnavailability(null,
                    unavailabilitiesList.get(Integer.parseInt(pos))
                            .getUnavailabilityID(), 0, currentDateStr, null,
                    notesDescription);
            // edit.putString("pos", null);
            // edit.commit();
            return drp;
        }

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        public void onPostExecute(Boolean result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);

            DialogUtils.dismissProgressDialog();

            boolean drp = result.booleanValue();
            if (drp) {
                Toast.makeText(CreateUnavailability.this,
                        getString(R.string.unavailability_added),
                        Toast.LENGTH_SHORT).show();
                /*
				 * change the status of the currently running job to
				 * pause/suspend it
				 */
                if (dataAccessObject.updateStatutInterv(4, idInterv))
                    dataAccessObject.setTimeClotIntervention(idInterv, idUser
                            + "","");
                EventBus.getDefault().post(new UpdateDataBaseEvent());
                finish();
            } else
                Toast.makeText(CreateUnavailability.this, R.string.msg_error,
                        Toast.LENGTH_SHORT).show();
        }

    }

}
