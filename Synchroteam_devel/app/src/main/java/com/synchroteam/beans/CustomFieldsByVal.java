package com.synchroteam.beans;

import java.util.Comparator;

// TODO: Auto-generated Javadoc
/**
 * The Class to be used for CustomFields previous developer class .
 * @author Ideavate.Solution 
 */
public class CustomFieldsByVal implements Comparable<CustomFieldsByVal>{

	/** The id custom field. */
	private int idCustomField;
	
	/** The nom table. */
	private String nomTable;
	
	/** The nom customs fields. */
	private String nomCustomsFields;
	
	/** The id element concerne. */
	private String idElementConcerne;
	
	/** The val custom field. */
	private String valCustomField;
	
	/** The num order. */
	
	
	
	private  Integer numOrder;
	
	
	
	/**
	 * Instantiates a new custom fields.
	 *
	 * @param idCustomField the id custom field
	 * @param nomTable the nom table
	 * @param nomCustomsFields the nom customs fields
	 * @param idElementConcerne the id element concerne
	 * @param valCustomField the val custom field
	 * @param numOrder the num order
	 */
	public CustomFieldsByVal(int idCustomField, String nomTable,
			String nomCustomsFields, String idElementConcerne,
			String valCustomField, int numOrder) {
		super();
		this.idCustomField = idCustomField;
		this.nomTable = nomTable;
		this.nomCustomsFields = nomCustomsFields;
		this.idElementConcerne = idElementConcerne;
		this.valCustomField = valCustomField;
		this.numOrder = numOrder;
	}
	
	/**
	 * Gets the id custom field.
	 *
	 * @return the id custom field
	 */
	public int getIdCustomField() {
		return idCustomField;
	}
	
	/**
	 * Sets the id custom field.
	 *
	 * @param idCustomField the new id custom field
	 */
	public void setIdCustomField(int idCustomField) {
		this.idCustomField = idCustomField;
	}
	
	/**
	 * Gets the nom table.
	 *
	 * @return the nom table
	 */
	public String getNomTable() {
		return nomTable;
	}
	
	/**
	 * Sets the nom table.
	 *
	 * @param nomTable the new nom table
	 */
	public void setNomTable(String nomTable) {
		this.nomTable = nomTable;
	}
	
	/**
	 * Gets the nom customs fields.
	 *
	 * @return the nom customs fields
	 */
	public String getNomCustomsFields() {
		return nomCustomsFields;
	}
	
	/**
	 * Sets the nom customs fields.
	 *
	 * @param nomCustomsFields the new nom customs fields
	 */
	public void setNomCustomsFields(String nomCustomsFields) {
		this.nomCustomsFields = nomCustomsFields;
	}
	
	/**
	 * Gets the id element concerne.
	 *
	 * @return the id element concerne
	 */
	public String getIdElementConcerne() {
		return idElementConcerne;
	}
	
	/**
	 * Sets the id element concerne.
	 *
	 * @param idElementConcerne the new id element concerne
	 */
	public void setIdElementConcerne(String idElementConcerne) {
		this.idElementConcerne = idElementConcerne;
	}
	
	/**
	 * Gets the val custom field.
	 *
	 * @return the val custom field
	 */
	public String getValCustomField() {
		return valCustomField;
	}
	
	/**
	 * Sets the val custom field.
	 *
	 * @param valCustomField the new val custom field
	 */
	public void setValCustomField(String valCustomField) {
		this.valCustomField = valCustomField;
	}
	
	/**
	 * Gets the num order.
	 *
	 * @return the num order
	 */
	public int getNumOrder() {
		return numOrder;
	}
	
	/**
	 * Sets the num order.
	 *
	 * @param numOrder the new num order
	 */
	public void setNumOrder(int numOrder) {
		this.numOrder = numOrder;
	}


		@Override
		public int compareTo(CustomFieldsByVal another) {
			
			int result = this.getNomTable().compareTo(another.getNomTable());
			if(result == 0){
				result = this.getNumOrder() - another.getNumOrder();
			}
			
			return result;
			
		}
	    
	   
	
 
 
}
