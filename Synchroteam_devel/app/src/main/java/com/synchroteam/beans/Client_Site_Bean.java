package com.synchroteam.beans;

public class Client_Site_Bean extends Site {

	/** The id site. */
	private int idSite;
	
	/** The nm site. */
	private String nmSite;
	
	/** The rue site. */
	private String rueSite;
	
	/** The ville site. */
	private String villeSite;
	
	/** The adresse. */
	private String adresse;
	
	/** The adresse compl. */
	private String adresseCompl;
	
	private String clientCity;
	
	
	private int idClient;
	
	
	/** The gps x. */
	private String gpsX;
	
	private String clientName;
	/** The gps y. */
	private String gpsY;

	public String getRefCustomer() {
		return refCustomer;
	}

	/** The  reference customer. */
	private String refCustomer;

	public Client_Site_Bean(int idSite, String nmSite, String rueSite, String villeSite,String adresse,String adresseCompl,String gpsX,String gpsY,String clientName,String clientCity,int idClient) {
		super();
		this.idSite = idSite;
		this.nmSite = nmSite;
		this.rueSite = rueSite;
		this.villeSite = villeSite;
		this.adresse = adresse;
		this.adresseCompl = adresseCompl;
		this.gpsX = gpsX;
		this.gpsY = gpsY;
		this.clientName=clientName;
		this.clientCity=clientCity;
		this.idClient=idClient;
	}

	public Client_Site_Bean(int idSite, String nmSite, String rueSite, String villeSite,String adresse,String adresseCompl,String gpsX,String gpsY,String clientName,String clientCity,int idClient,String refCustomer) {
		super();
		this.idSite = idSite;
		this.nmSite = nmSite;
		this.rueSite = rueSite;
		this.villeSite = villeSite;
		this.adresse = adresse;
		this.adresseCompl = adresseCompl;
		this.gpsX = gpsX;
		this.gpsY = gpsY;
		this.clientName=clientName;
		this.clientCity=clientCity;
		this.idClient=idClient;
		this.refCustomer=refCustomer;
	}


	public int getIdClient(){
		
		return idClient;
	}
	
	
	public int getIdSite() {
		return idSite;
	}
	public String getNmSite() {
		return nmSite;
	}
	public String getRueSite() {
		return rueSite;
	}
	public String getVilleSite() {
		return villeSite;
	}
	public String getAdresse() {
		return adresse;
	}
	public String getAdresseCompl() {
		return adresseCompl;
	}
	public String getGpsX() {
		return gpsX;
	}
	public String getClientName() {
		return clientName;
	}
	public String getGpsY() {
		return gpsY;
	}
	
	public String getClientCity(){
		return clientCity;
		
	}
}
