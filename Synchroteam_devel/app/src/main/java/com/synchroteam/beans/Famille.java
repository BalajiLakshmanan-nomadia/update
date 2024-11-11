package com.synchroteam.beans;

import java.util.Vector;

// TODO: Auto-generated Javadoc
/**
 * The Class Famille which stores the data of category and items of a report Template.
 * @author Previous Developer
 */
public class Famille implements Comparable<Famille>
{
	
	/** The id. */
	private int id;
	
	/** The nom famille. */
	private String nomFamille;
	
	/** The fm trie. */
	
    
    /** The items. */
    private Vector<Item> items;
	
    private int trie_condition_famille ;

	private int isSharedBlock;

	/**
	 * Gets the items.
	 *
	 * @return the items
	 */
	public Vector<Item> getItems() {
		return items;
	}
	
	/**
	 * Instantiates a new famille.
	 *
	 * @param id the id
	 * @param nomFamille the nom famille
	 */
	public Famille(int id, String nomFamille , int trie_condition_famille, int isSharedBlock) {
		super();
		this.id = id;
		this.nomFamille = nomFamille;
		this.trie_condition_famille = trie_condition_famille;
		this.isSharedBlock = isSharedBlock;
//		this.items=items;
		
	}

	
	
	/**
	 * Instantiates a new famille.
	 *
	 * @param id the id
	 */
	public Famille(int id){
		this.id=id;
	}
	/**
	 * Gets the nom famille.
	 *
	 * @return the nom famille
	 */
	public String getNomFamille() {
		return nomFamille;
	}
	
	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public int getID(){
		return id;
	}
	
	/**
	 * Sets the items.
	 *
	 * @param items the new items
	 */
	public void setItems(Vector<Item> items){
		this.items=items;
	}

	public int getTrie_condition_famille() {
		return trie_condition_famille;
	}

	public void setTrie_condition_famille(int trie_condition_famille) {
		this.trie_condition_famille = trie_condition_famille;
	}

	public int isSharedBlock() {
		return isSharedBlock;
	}

	public void setSharedBlock(int sharedBlock) {
		isSharedBlock = sharedBlock;
	}
	
	@Override
	public int compareTo(Famille another) {
		// TODO Auto-generated method stub
		int result = this.getTrie_condition_famille() - another.getTrie_condition_famille();
		if(result == 0){
			result = this.getID() - another.getID();
		}
		
		return result;
	}
	
	
}
