package com.synchroteam.fragment;

import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.synchroteam.beans.CustomFieldBean;
import com.synchroteam.beans.CustomFieldsByVal;
import com.synchroteam.dao.Dao;
import com.synchroteam.listadapters.CustomFieldAdapter;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.utils.DaoManager;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Vector;

public class CustomJobPoolDetailsFragment extends Fragment {

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

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.layout_fragment_custom_details,
				container, false);
		dao = DaoManager.getInstance();
		idInterv = getArguments().getString("id_interv");
		idClient = getArguments().getInt("idClient");
		idSite = getArguments().getInt("idSite");
		idEquipement = getArguments().getInt("idEquipement");
		customFieldBeans = new ArrayList<CustomFieldBean>();
		init(view);
		return view;
	}

	/**
	 * Inits the.
	 */
	public void init(View v) {
		maListViewPerso = (ExpandableListView) v
				.findViewById(R.id.listcustomfield);

		customFieldBeans.add(new CustomFieldBean(
				getString(R.string.intervention)));
		customFieldBeans.add(new CustomFieldBean(
				getString(R.string.textJobCustomerLableTv)));
		customFieldBeans.add(new CustomFieldBean(
				getString(R.string.textSiteLable)));
		customFieldBeans.add(new CustomFieldBean(
				getString(R.string.textEquipmentLable)));

		/*
		 * Adding custom field for intervention
		 */
		Vector<CustomFieldsByVal> vectInterv = new Vector<CustomFieldsByVal>();
		// vect = dao.getCustomFields(idInterv, idClient, idSite, idEquipement);
		vectInterv = dao.getCFForIntervention(idInterv);
		Enumeration<CustomFieldsByVal> enInterv = vectInterv.elements();

		while (enInterv.hasMoreElements()) {
			CustomFieldsByVal costumF = enInterv.nextElement();
			customFieldBeans.get(0).getCustomFieldBeans().add(costumF);

			// if (costumF.getNomTable().equals("T_INTERVENTIONS")) {
			// customFieldBeans.get(0).getCustomFieldBeans().add(costumF);
			// }
			//
			// else if (costumF.getNomTable().equals("T_CLIENTS")) {
			// customFieldBeans.get(1).getCustomFieldBeans().add(costumF);
			// }
			//
			// else if (costumF.getNomTable().equals("T_SITES_CLIENTS")) {
			// customFieldBeans.get(2).getCustomFieldBeans().add(costumF);
			// }
			//
			// else {
			// customFieldBeans.get(3).getCustomFieldBeans().add(costumF);
			// }

		}

		/*
		 * Adding custom field for client
		 */
		Vector<CustomFieldsByVal> vectClient = new Vector<CustomFieldsByVal>();
		// vect = dao.getCustomFields(idInterv, idClient, idSite, idEquipement);
		vectClient = dao.getCFForClient(idClient);
		Enumeration<CustomFieldsByVal> enClient = vectClient.elements();

		while (enClient.hasMoreElements()) {
			CustomFieldsByVal costumF = enClient.nextElement();
			customFieldBeans.get(1).getCustomFieldBeans().add(costumF);
		}

		/*
		 * Adding custom field for site
		 */
		Vector<CustomFieldsByVal> vectSite = new Vector<CustomFieldsByVal>();
		// vect = dao.getCustomFields(idInterv, idClient, idSite, idEquipement);
		vectSite = dao.getCFForSite(idSite);
		Enumeration<CustomFieldsByVal> enSite = vectSite.elements();

		while (enSite.hasMoreElements()) {
			CustomFieldsByVal costumF = enSite.nextElement();
			customFieldBeans.get(2).getCustomFieldBeans().add(costumF);
		}

		/*
		 * Adding custom field for equipment
		 */
		Vector<CustomFieldsByVal> vectEquip = new Vector<CustomFieldsByVal>();
		// vect = dao.getCustomFields(idInterv, idClient, idSite, idEquipement);
		vectEquip = dao.getCFForEquip(idEquipement);
		Enumeration<CustomFieldsByVal> enEquip = vectEquip.elements();

		while (enEquip.hasMoreElements()) {
			CustomFieldsByVal costumF = enEquip.nextElement();
			customFieldBeans.get(3).getCustomFieldBeans().add(costumF);
		}

		ArrayList<CustomFieldBean> tempCustomFieldBeans = new ArrayList<CustomFieldBean>();
		for (int i = 0; i < customFieldBeans.size(); i++) {
			if (customFieldBeans.get(i).getCustomFieldBeans().size() == 0) {
				// nothing to do
			} else
				tempCustomFieldBeans.add(customFieldBeans.get(i));
		}

		CustomFieldAdapter customFieldAdapter = new CustomFieldAdapter(
				getActivity(), tempCustomFieldBeans);

		maListViewPerso.setAdapter(customFieldAdapter);

	}
}