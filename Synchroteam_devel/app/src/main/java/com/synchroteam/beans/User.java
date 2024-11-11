package com.synchroteam.beans;

// TODO: Auto-generated Javadoc
/**
 * The Class User to hold data of current user which is holding session
 * 
 * Previous Developer Class.
 */
public class User {

	/** The id. */
	private int id;

	/** The login. */
	private String login;

	/** The pwd. */
	private String pwd;

	/** The nom. */
	private String nom;// name

	/** The prenom. */
	private String prenom;// LastName

	/** Stock id of the user */
	private String idStock;

	/**
	 * profile id of the user
	 */
	private int idProfil;

	/**
	 * profile id of the user
	 */
	private int flSubContractor;


	/**
	 * Instantiates a new user.
	 */
	public User() {
		super();
	}

	/**
	 * Instantiates a new user.
	 *
	 * @param login
	 *            the login
	 * @param pwd
	 *            the pwd
	 * @param nom
	 *            the nom
	 * @param prenom
	 *            the prenom
	 * @param id
	 *            the id
	 */
	public User(String login, String pwd, String nom, String prenom,
			String idStock, int id,int idProfil,int flSubContractor) {
		super();
		this.login = login;
		this.pwd = pwd;
		this.nom = nom;
		this.prenom = prenom;
		this.idStock = idStock;
		this.id = id;
		this.idProfil = idProfil;
		this.flSubContractor=flSubContractor;

	}

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * Gets the nom.
	 *
	 * @return the nom
	 */
	public String getNom() {
		return nom;
	}

	/**
	 * Gets the prenom.
	 *
	 * @return the prenom
	 */
	public String getPrenom() {
		return prenom;
	}

	/**
	 * Gets the login.
	 *
	 * @return the login
	 */
	public String getLogin() {
		return login;
	}

	/**
	 * Sets the login.
	 *
	 * @param login
	 *            the new login
	 */
	public void setLogin(String login) {
		this.login = login;
	}

	/**
	 * Gets the pwd.
	 *
	 * @return the pwd
	 */
	public String getPwd() {
		return pwd;
	}

	/**
	 * Sets the pwd.
	 *
	 * @param pwd
	 *            the new pwd
	 */
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	/**
	 * get the stock id
	 * 
	 * 
	 * @return stock id
	 */
	public String getIdStock() {
		return idStock;
	}

	/**
	 * set the stock id
	 * 
	 * 
	 * @param idStock
	 */
	public void setIdStock(String idStock) {
		this.idStock = idStock;
	}

	public int getIdProfil() {
		return idProfil;
	}

	public int getFlSubContractor() {
		return flSubContractor;
	}
}
