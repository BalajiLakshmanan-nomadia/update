package com.synchroteam.beans;

// TODO: Auto-generated Javadoc

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Functionality of this class is not clear
 * The Class ModeleRapport .
 * @author ${Previous Developer}
 */
public class ModeleRapport implements Parcelable {
	
	/** The id. */
	private int id;
	
	/** The nom. */
	private String nom;
	
	/** The fl default. */
	private int flDefault;
	
	/**
	 * Instantiates a new modele rapport.
	 */
	public ModeleRapport() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Instantiates a new modele rapport.
	 *
	 * @param id the id
	 * @param nom the nom
	 * @param flDefault the fl default
	 */
	public ModeleRapport(int id, String nom,int flDefault) {
		super();
		this.id = id;
		this.nom = nom;
		this.flDefault = flDefault;
	}

	protected ModeleRapport(Parcel in) {
		id = in.readInt();
		nom = in.readString();
		flDefault = in.readInt();
	}

	public static final Creator<ModeleRapport> CREATOR = new Creator<ModeleRapport>() {
		@Override
		public ModeleRapport createFromParcel(Parcel in) {
			return new ModeleRapport(in);
		}

		@Override
		public ModeleRapport[] newArray(int size) {
			return new ModeleRapport[size];
		}
	};

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
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		return nom;
	}
	
	/**
	 * Gets the fl default.
	 *
	 * @return the fl default
	 */
	public int getFlDefault() {
		return flDefault;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int i) {
		parcel.writeInt(id);
		parcel.writeString(nom);
		parcel.writeInt(flDefault);
	}
}
