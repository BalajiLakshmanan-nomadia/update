package com.synchroteam.beans;

import com.synchroteam.utils.ReportsItems;

// TODO: Auto-generated Javadoc
/**
 *
 * The Class ReportsCategoryItem.
 * @author ${Ideavate Solution}
 */
public class ReportsCategoryItem implements ReportsItems {

	/** The category. */
	private String category;
	
	/** The id_category. */
	private int id_category;
	
	/**
	 * Instantiates a new reports category item.
	 *
	 * @param category the category
	 * @param id_category the id_category
	 */
	public ReportsCategoryItem(String category,int id_category) {
		// TODO Auto-generated constructor stub
	
		this.category=category;
		this.id_category=id_category;
	}
	
	
	
	/**
	 * Gets the id category.
	 *
	 * @return the id category
	 */
	public int getIdCategory(){
		
		return id_category;
	}
	
	
	/* (non-Javadoc)
	 * @see com.synchroteam.utils.ReportsItems#isHeader()
	 */
	@Override
	public boolean isHeader() {
		// TODO Auto-generated method stub
		return true;
	}

	/**
	 * Gets the categroy.
	 *
	 * @return the categroy
	 */
	public String getCategroy(){
		return category;
	}
	
	
}
