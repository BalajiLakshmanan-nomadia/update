package com.synchroteam.beans;

public class AttachmentsBeans {

	private int idCustomer;
	private int idAttachment;
	private String tokenAttachment;
	private String nameAttachment;
	private int sizeAttachment;

	private String urlAttachment;
	private String dtModif;

	private String dtSupper;
	private String idIntervention;
	private int idClient;
	private int idSite;
	private int idEquipment;

	public AttachmentsBeans(int idCustomer, int idAttachment,
			String tokenAttachment, String nameAttachment, int sizeAttachment,
			String urlAttachment, String dtModif, String dtSupper,
			String idIntervention, int idClient, int idSite, int idEquipment) {
		super();
		this.idCustomer = idCustomer;
		this.idAttachment = idAttachment;
		this.tokenAttachment = tokenAttachment;
		this.nameAttachment = nameAttachment;
		this.sizeAttachment = sizeAttachment;
		this.urlAttachment = urlAttachment;
		this.dtModif = dtModif;
		this.dtSupper = dtSupper;
		this.idIntervention = idIntervention;
		this.idClient = idClient;
		this.idSite = idSite;
		this.idEquipment = idEquipment;
	}

	public int getIdSite() {
		return idSite;
	}

	public int getIdEquipment() {
		return idEquipment;
	}

	public int getIdCustomer() {
		return idCustomer;
	}

	public int getIdAttachment() {
		return idAttachment;
	}

	public String getTokenAttachment() {
		return tokenAttachment;
	}

	public String getNameAttachment() {
		return nameAttachment;
	}

	public int getSizeAttachment() {
		return sizeAttachment;
	}

	public String getUrlAttachment() {
		return urlAttachment;
	}

	public String getDtModif() {
		return dtModif;
	}

	public String getDtSupper() {
		return dtSupper;
	}

	public String getIdIntervention() {
		return idIntervention;
	}

	public int getIdClient() {
		return idClient;
	}

}
