package com.synchroteam.beans;

import java.util.ArrayList;

// TODO: Auto-generated Javadoc
/**
 * The Class CustomFieldBean holds the data of Custom Fields in job discription screen.
 */
public class CustomFieldBean {
   
	/** The custom f ield category. */
	String customFIeldCategory;
	
	/** The custom fields by vals. */
	ArrayList<CustomFieldsByVal> customFieldsByVals;
	
	/**
	 * Instantiates a new custom field bean.
	 *
	 * @param customFIeldCategory the custom f ield category
	 */
	public CustomFieldBean(String customFIeldCategory) {
		super();
		this.customFIeldCategory = customFIeldCategory;
		customFieldsByVals=new ArrayList<CustomFieldsByVal>();
	}
	
	
	
	
	/**
	 * Gets the custom f ield category.
	 *
	 * @return the custom f ield category
	 */
	public String getCustomFIeldCategory() {
		return customFIeldCategory;
	}
	
	/**
	 * Sets the custom f ield category.
	 *
	 * @param customFIeldCategory the new custom f ield category
	 */
	public void setCustomFIeldCategory(String customFIeldCategory) {
		this.customFIeldCategory = customFIeldCategory;
	}
	
	/**
	 * Gets the custom field beans.
	 *
	 * @return the custom field beans
	 */
	public ArrayList<CustomFieldsByVal> getCustomFieldBeans() {
		return customFieldsByVals;
	}
	
	/**
	 * Sets the custom field beans.
	 *
	 * @param customFieldsByVals the new custom field beans
	 */
	public void setCustomFieldBeans(ArrayList<CustomFieldsByVal> customFieldsByVals) {
		this.customFieldsByVals = customFieldsByVals;
	}
	
	
	
}
