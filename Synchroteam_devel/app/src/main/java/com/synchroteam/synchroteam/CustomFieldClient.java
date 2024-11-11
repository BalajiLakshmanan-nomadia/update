package com.synchroteam.synchroteam;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ExpandableListView;

import com.synchroteam.beans.CustomFieldBean;
import com.synchroteam.beans.CustomFieldsByVal;
import com.synchroteam.dao.Dao;
import com.synchroteam.listadapters.CustomFieldAdapter;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.utils.DaoManager;
import com.synchroteam.utils.KEYS;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Vector;

// TODO: Auto-generated Javadoc
/**
 * The Class CustomField to show data of custom fields.
 */
public class CustomFieldClient extends Activity {

	/** The id client. */
	private int idClient;

	/** The dao. */
	private Dao dao;

	/** The progress d synch. */
	protected ProgressDialog progressDSynch;

	/** The pi. */
	PendingIntent pi;

	/** The ma list view perso. */
	private ExpandableListView maListViewPerso;

	/** The action bar. */

	/** The custom field beans. */
	private ArrayList<CustomFieldBean> customFieldBeans;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		dao = DaoManager.getInstance();

		setContentView(R.layout.list_customs_field);
		// setTitle(R.string.Additionalinformation);

		Bundle bundle = this.getIntent().getExtras();

		idClient = bundle.getInt(KEYS.ClientDetial.ID_CLIENT);

		customFieldBeans = new ArrayList<CustomFieldBean>();
		init();

	}

	/**
	 * Inits the.
	 */
	public void init() {
		maListViewPerso = (ExpandableListView) findViewById(R.id.listcustomfield);
		findViewById(R.id.titleTextTv).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						finish();

					}
				});

		customFieldBeans.add(new CustomFieldBean(
				getString(R.string.textJobCustomerLableTv)));
		Vector<CustomFieldsByVal> vect = new Vector<CustomFieldsByVal>();
		// vect = dao.getCustomFieldsClients(idClient);
		vect = dao.getCFForClient(idClient);
		Enumeration<CustomFieldsByVal> en = vect.elements();

		while (en.hasMoreElements()) {
			CustomFieldsByVal costumF = en.nextElement();

			// if (costumF.getNomTable().equals("T_CLIENTS")) {
			customFieldBeans.get(0).getCustomFieldBeans().add(costumF);
			// }

		}

		CustomFieldAdapter customFieldAdapter = new CustomFieldAdapter(this,
				customFieldBeans);

		maListViewPerso.setAdapter(customFieldAdapter);

	}

}
