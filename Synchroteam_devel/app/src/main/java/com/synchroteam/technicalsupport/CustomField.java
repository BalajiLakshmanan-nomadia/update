package com.synchroteam.technicalsupport;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Vector;

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

// TODO: Auto-generated Javadoc
/**
 * The Class CustomField to show data of custom fields. 
 */
public class CustomField extends Activity {

	/** The id interv. */
	private String idInterv;

	/** The id equipement. */
	private int idClient, idSite, idEquipement;

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
		idInterv = bundle.getString("id_interv");
		idClient = bundle.getInt("idClient");
		idSite = bundle.getInt("idSite");
		idEquipement = bundle.getInt("idEquipement");
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
				getString(R.string.intervention)));
		customFieldBeans.add(new CustomFieldBean(getString(R.string.textJobCustomerLableTv)));
		customFieldBeans.add(new CustomFieldBean(getString(R.string.textSiteLable)));
		customFieldBeans
				.add(new CustomFieldBean(getString(R.string.textEquipmentLable)));

		Vector<CustomFieldsByVal> vect = new Vector<CustomFieldsByVal>();
		vect = dao.getCustomFields(idInterv, idClient, idSite, idEquipement);
		Enumeration<CustomFieldsByVal> en = vect.elements();

		while (en.hasMoreElements()) {
			CustomFieldsByVal costumF =  en.nextElement();

			if (costumF.getNomTable().equals("T_INTERVENTIONS")) {
				customFieldBeans.get(0).getCustomFieldBeans().add(costumF);
			}

			else if (costumF.getNomTable().equals("T_CLIENTS")) {
				customFieldBeans.get(1).getCustomFieldBeans().add(costumF);
			}

			else if (costumF.getNomTable().equals("T_SITES_CLIENTS")) {
				customFieldBeans.get(2).getCustomFieldBeans().add(costumF);
			}

			else {
				customFieldBeans.get(3).getCustomFieldBeans().add(costumF);
			}

		}

		ArrayList<CustomFieldBean> tempCustomFieldBeans = new ArrayList<CustomFieldBean>();
		for ( int  i = 0 ; i < customFieldBeans.size() ; i++)
		{
			if(customFieldBeans.get(i).getCustomFieldBeans().size() == 0)
			{
				//nothing to do
			}
			else
				tempCustomFieldBeans.add(customFieldBeans.get(i));
		}
	
		CustomFieldAdapter customFieldAdapter = new CustomFieldAdapter(this,
				tempCustomFieldBeans);

		maListViewPerso.setAdapter(customFieldAdapter);

	}

}
