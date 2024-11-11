package com.synchroteam.catalouge;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.core.view.MenuItemCompat;
import androidx.core.view.MenuItemCompat.OnActionExpandListener;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.TypefaceSpan;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Filter;
import android.widget.ListView;

import com.synchroteam.beans.CatalougePieces;
import com.synchroteam.dao.Dao;
import com.synchroteam.listadapters.PiecesListAdapter;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.utils.DaoManager;
import com.synchroteam.utils.KEYS;
import com.synchroteam.utils.MyFixedListView;

// TODO: Auto-generated Javadoc
/**
 * The Activity holds the Ui of parts list in Parts and services modules and
 * also manages infaltion of Parts List in listview and to initiates fetching of
 * parts list from database using Dao object and instantiating list view. .
 * author Ideavate.solution
 */
public class CatalougePices extends AppCompatActivity implements
		SearchView.OnQueryTextListener, SearchView.OnCloseListener {

	/** The action bar. */
	private ActionBar actionBar;

	/** The item list view. */

	private int idCat;

	/** The id inter. */
	private String idInter;

	/** The data access object. */
	private Dao dataAccessObject;

	/** The nbre piece. */
	private int nbrePiece;

	/** The pieces lv. */
	private ListView piecesLv;

	/** The scroll index. */
	private int scrollIndex = 1;

	/** The pieces. */
	private ArrayList<CatalougePieces> pieces;

	/** The pieces list adapter. */
	private PiecesListAdapter piecesListAdapter;

	/** The filter. */
	private Filter filter;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v7.app.AppCompatActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_items_list);
		actionBar = getSupportActionBar();
		actionBar.setIcon(android.R.color.transparent);

		String title = this.getResources().getString(R.string.itemsTopBarLable).toUpperCase();
		SpannableString titleSpannable = new SpannableString(title);
		titleSpannable.setSpan(
				new TypefaceSpan(this.getResources().getString(
						R.string.fontName_hevelicaLight)), 0,
				titleSpannable.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

		// actionBar.setTitle(titleSpannable);
		actionBar.setTitle(isLGDevice() ? title : titleSpannable);

		init();

		fillPiecesToListView();
	}

	public boolean isLGDevice() {
		return (Build.MANUFACTURER.contains("LG") || Build.MODEL.contains("LG"));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub

		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.items_menu, menu);
		MenuItem searchItem = menu.findItem(R.id.action_search);
		SearchView searchView = (SearchView) MenuItemCompat
				.getActionView(searchItem);
		searchView.setOnQueryTextListener(this);
		searchView.setOnCloseListener(this);

		MenuItemCompat.setOnActionExpandListener(searchItem,
				new OnActionExpandListener() {
					@Override
					public boolean onMenuItemActionCollapse(MenuItem item) {
						// Do something when collapsed
						filter.filter("");
						return true; // Return true to collapse action view
					}

					@Override
					public boolean onMenuItemActionExpand(MenuItem item) {
						// Do something when expanded
						return true; // Return true to expand action view
					}
				});

		return super.onCreateOptionsMenu(menu);
	}

	/**
	 * Fetches the parts of particular category which is identified by idCat(Id
	 * of category) and initalise the adapter from this list and assign the
	 * adapter to listview.
	 */
	private void fillPiecesToListView() {
		// TODO Auto-generated method stub

		if (pieces != null) {
			pieces.clear();
		} else {
			pieces = new ArrayList<CatalougePieces>();
		}

		pieces.addAll(dataAccessObject.getPieceByCategorie(idCat + "", idInter,
				scrollIndex, nbrePiece));

		piecesListAdapter = new PiecesListAdapter(this, pieces,
				dataAccessObject, idInter + "");

		piecesLv.setAdapter(piecesListAdapter);

		filter = piecesListAdapter.getFilter();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub

		switch (item.getItemId()) {
		case android.R.id.home:
			setResult(RESULT_OK);
			finish();
			return true;

		default:
			return super.onOptionsItemSelected(item);

		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.support.v7.widget.SearchView.OnQueryTextListener#onQueryTextChange
	 * (java.lang.String)
	 */
	@Override
	public boolean onQueryTextChange(String arg0) {
		// TODO Auto-generated method stub

		filter.filter(arg0);

		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.support.v7.widget.SearchView.OnQueryTextListener#onQueryTextSubmit
	 * (java.lang.String)
	 */
	@Override
	public boolean onQueryTextSubmit(String arg0) {
		// TODO Auto-generated method stub

		filter.filter(arg0);

		return false;
	}

	/**
	 * Initialised the Dao object to access data from Data base and also
	 * extracts data from intent passed to it from other activity which will be
	 * needed for performing operation assigned to this activity..
	 */
	private void init() {
		// TODO Auto-generated method stub

		Intent intent = getIntent();
		dataAccessObject = DaoManager.getInstance();
		idCat = intent.getExtras().getInt(KEYS.Catalouge.ID_CAT);
		idInter = intent.getExtras().getString(KEYS.Catalouge.ID_INTER);

		piecesLv =  (MyFixedListView)findViewById(R.id.itemListLv);

		piecesLv.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub

			}
		});

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v7.app.AppCompatActivity#onBackPressed()
	 */
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		setResult(RESULT_OK);
		super.onBackPressed();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v7.widget.SearchView.OnCloseListener#onClose()
	 */
	@Override
	public boolean onClose() {
		// TODO Auto-generated method stub
		return false;
	}

}
