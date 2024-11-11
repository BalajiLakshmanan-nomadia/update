package com.synchroteam.beans;



// TODO: Auto-generated Javadoc
/**
 * The Class Categorie to hold data of category.
 * 
 * @author Ideavate.Solution 
 */
public class Categorie implements CategoryAndPartsInterface{
	
	/** The idcat. */
	private int idcat;
	
	/** The nomcat. */
	private String nomcat;
	
	
	
	/** The pices. */
	
	
	
	private int size;
	
	/** The index. */
	private int index;
	
	
	//for test
	/**
	 * Instantiates a new categorie.
	 *
	 * @param name the name
	 */
	public Categorie(String name){
		this.nomcat=name;
		
	}
	
	/**
	 * Instantiates a new categories.
	 *
	 * @param idcat the idcat
	 * @param nomcat the nomcat
	 */
	public Categorie(int idcat, String nomcat) {
		super();
		this.idcat = idcat;
		this.nomcat = nomcat;
		
	}
	
	/**
	 * Instantiates a new categorie.
	 *
	 * @param idCat the id cat
	 * @param nomCat the nom cat
	 * @param size the size
	 * @param index the index
	 */
	public Categorie(int idCat,String nomCat,int size,int index){
		super();
		this.idcat=idCat;
		this.nomcat=nomCat;
		this.size=size;
		this.index=index;
	}
	
	

	/**
	 * Gets the idcat.
	 *
	 * @return the idcat
	 */
	public int getIdcat() {
		return idcat;
	}
	
	/**
	 * Sets the idcat.
	 *
	 * @param idcat the new idcat
	 */
	public void setIdcat(int idcat) {
		this.idcat = idcat;
	}
	
	/**
	 * Gets the nomcat.
	 *
	 * @return the nomcat
	 */
	public String getNomcat() {
		return nomcat;
	}
	
	/**
	 * Sets the nomcat.
	 *
	 * @param nomcat the new nomcat
	 */
	public void setNomcat(String nomcat) {
		this.nomcat = nomcat;
	}
	
	
	
	/**
	 * Gets the size.
	 *
	 * @return the size
	 */
	public int getSize() {
		return size;
	}

	/**
	 * Gets the index.
	 *
	 * @return the index
	 */
	public int getIndex() {
		return index;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		// TODO Auto-generated method stub
	  Categorie categorie=(Categorie)o;
		boolean equal=true;
		if(categorie.getIdcat()!=idcat){
			equal=false;
		}
		else if(!categorie.getNomcat().equals(nomcat)){
			equal=false;
		}
		else if(categorie.getIndex()!=index){
			equal=false;
		}
		
		else if(categorie.getSize()!=size){
			equal=false;
		}
	  
	  
		
		return equal;
	}

	/* (non-Javadoc)
	 * @see com.synchroteam.beans.CategoryAndPartsInterface#isCategory()
	 */
	@Override
	public boolean isCategory() {
		// TODO Auto-generated method stub
		return true;
	}
	
	
}
