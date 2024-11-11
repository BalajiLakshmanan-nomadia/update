package com.synchroteam.beans;

public class Client_Site_EquipmnentBean {

	private int idEquipement;
	private String nmEquipement;
	
	private String nmSite;
	private String  nmClient;
	private String clientCity;
	private int idSite,idClient;
	private String publickLink;
	private String refCustomer;

	public String getRefCustomer() {
		return refCustomer;
	}

	public Client_Site_EquipmnentBean(int idEquipement, String nmEquipement, String nmSite, String nmClient, String clientCity, int idClient, int idSite, String publickLink){
		super();
		this.idEquipement = idEquipement;
		this.nmEquipement = nmEquipement;
		this.nmSite=nmSite;
		this.nmClient=nmClient;
		this.clientCity=clientCity;
		this.idClient=idClient;
		this.idSite=idSite;
		this.publickLink = publickLink;
	}

	public Client_Site_EquipmnentBean(int idEquipement,String nmEquipement,String nmSite,String nmClient,String clientCity,int idClient,int idSite,String publickLink,String refCustomer){
		super();
		this.idEquipement = idEquipement;
		this.nmEquipement = nmEquipement;
		this.nmSite=nmSite;
		this.nmClient=nmClient;
		this.clientCity=clientCity;
		this.idClient=idClient;
		this.idSite=idSite;
		this.publickLink = publickLink;
		this.refCustomer = refCustomer;
	}

	public int getIdEquipement() {
		return idEquipement;
	}
	
	public int getIdClient(){
		return idClient;
	}
	public int getIdSite(){
		return idSite;
	}
	public String getNmEquipement() {
		return nmEquipement;
	}
	public String getNmSite() {
		return nmSite;
	}
	public String getNmClient() {
		return nmClient;
	}
	public String getClientCity(){
		return clientCity;
	}

	public String getPublickLink() {
		return publickLink;
	}
}
