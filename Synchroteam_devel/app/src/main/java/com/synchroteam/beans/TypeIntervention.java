package com.synchroteam.beans;

// TODO: Auto-generated Javadoc

import android.os.Parcel;
import android.os.Parcelable;

/**
 * The Class TypeIntervention.
 * @author ${Previous Developer}
 */
public class TypeIntervention implements Parcelable
{
	
	/** The id. */
	private int id;
	
	/** The nom. */
	private String nom;
	
	/** The fl default. */
	private int flDefault;
	
	/** The hr duree intervention. */
	private String hrDureeIntervention;
	
	/**
	 * Instantiates a new type intervention.
	 *
	 * @param id the id
	 * @param nom the nom
	 * @param flDefault the fl default
	 * @param hrDureeIntervention the hr duree intervention
	 */
	public TypeIntervention(int id, String nom,int flDefault,String hrDureeIntervention) {
		super();
		this.id = id;
		this.nom = nom;
		this.flDefault = flDefault;
		this.hrDureeIntervention = hrDureeIntervention;
	}
 
	/**
	 * Instantiates a new type intervention.
	 */
	public TypeIntervention() {
		super();
		// TODO Auto-generated constructor stub
	}

	protected TypeIntervention(Parcel in) {
		id = in.readInt();
		nom = in.readString();
		flDefault = in.readInt();
		hrDureeIntervention = in.readString();
	}

	public static final Creator<TypeIntervention> CREATOR = new Creator<TypeIntervention>() {
		@Override
		public TypeIntervention createFromParcel(Parcel in) {
			return new TypeIntervention(in);
		}

		@Override
		public TypeIntervention[] newArray(int size) {
			return new TypeIntervention[size];
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

	/**
	 * Gets the hr duree intervention.
	 *
	 * @return the hr duree intervention
	 */
	public String getHrDureeIntervention() {
		return hrDureeIntervention;
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
		parcel.writeString(hrDureeIntervention);
	}
}
