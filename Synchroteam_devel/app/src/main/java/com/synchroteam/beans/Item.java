package com.synchroteam.beans;

import android.os.Parcel;
import android.os.Parcelable;

import com.synchroteam.utils.ReportsItems;

// TODO: Auto-generated Javadoc

/**
 * The Class Item is used to store the data regarding each item of reports screen .
 */
public class Item implements Comparable<Item>, ReportsItems, Parcelable {

    /**
     * The id item.
     */
    private int idItem;

    /**
     * The id famille.
     */
    private int idFamille;

    /**
     * The id type item.
     */
    private int idTypeItem;

    /**
     * The nom item.
     */
    private String nomItem;

    /**
     * The oblig.
     */
    private int oblig;

    /**
     * The cond.
     */
    private int cond;

    /**
     * The val_cond.
     */
    private String val_cond;

    /**
     * The val item.
     */
    private int valItem;

    /**
     * The valeur net.
     */
    private String valeurNet;

    /**
     * The com item.
     */
    private String comItem;

    /**
     * The val trie.
     */
    private int valTrie;

    /**
     * The valeur default.
     */
    private String valeurDefault;

    /**
     * The sub cause.
     */
    private int subCause;

    /**
     * The image.
     */
    private byte[] image;

    /**
     * The fl reserve.
     */
    private int flReserve;

    /**
     * The group position.
     */
    private int groupPosition;

    /**
     * flag private.
     */
    private int flPrivate;


    /**
     * Gets private flag
     *
     * @return
     */
    public int getFlPrivate() {
        return flPrivate;
    }

    /**
     * Gets iteraion
     *
     * @return iteration
     */
    public int getIteration() {
        return iteration;
    }

    /**
     * Sets iteration
     *
     * @param iteration
     */
    public void setIteration(int iteration) {
        this.iteration = iteration;
    }

    /**
     * Iteration value for item
     */
    private int iteration;

    /**
     * Gets the val trie.
     *
     * @return the val trie
     */
    public int getValTrie() {
        return valTrie;
    }


    /**
     * The image.
     */
    private byte[] photoImg;

    public byte[] getPhotoImg() {
        return photoImg;
    }

    public void setPhotoImg(byte[] photoImg) {
        this.photoImg = photoImg;
    }

    /**
     * Sets the val trie.
     *
     * @param valTrie the new val trie
     */
    public void setValTrie(int valTrie) {
        this.valTrie = valTrie;
    }

    /**
     * Instantiates a new item.
     *
     * @param idItem        the id item
     * @param idFamille     the id famille
     * @param idTypeItem    the id type item
     * @param nomItem       the nom item
     * @param oblig         the oblig
     * @param cond          the cond
     * @param valCond       the val cond
     * @param valItem       the val item
     * @param valeur        the valeur
     * @param comItem       the com item
     * @param valTrie       the val trie
     * @param valeurDefault the valeur default
     * @param image         the image
     * @param flReserve     the fl reserve
     */
    public Item(int idItem, int idFamille, int idTypeItem, String nomItem,
                int oblig, int cond, String valCond, int valItem, String valeur, String comItem, int valTrie, String valeurDefault, byte[] image, int flReserve, byte[] photoImg, int iteration) {
        super();
        this.idItem = idItem;
        this.idFamille = idFamille;
        this.idTypeItem = idTypeItem;
        this.nomItem = nomItem;
        this.oblig = oblig;
        this.cond = cond;
        this.val_cond = valCond;
        this.valItem = valItem;
        this.valeurNet = valeur;
        this.comItem = comItem;
        this.valTrie = valTrie;
        this.valeurDefault = valeurDefault;
        this.image = image;
        this.flReserve = flReserve;
        this.photoImg = photoImg;
        this.iteration = iteration;
    }

    public Item(int idItem, int idFamille, int idTypeItem, String nomItem,
                int oblig, int cond, String valCond, int valItem, String valeur, String comItem, int valTrie, String valeurDefault, byte[] image, int flReserve, int iteration) {
        super();
        this.idItem = idItem;
        this.idFamille = idFamille;
        this.idTypeItem = idTypeItem;
        this.nomItem = nomItem;
        this.oblig = oblig;
        this.cond = cond;
        this.val_cond = valCond;
        this.valItem = valItem;
        this.valeurNet = valeur;
        this.comItem = comItem;
        this.valTrie = valTrie;
        this.valeurDefault = valeurDefault;
        this.image = image;
        this.flReserve = flReserve;
        this.iteration = iteration;
    }

    /**
     * Instantiates a new item.
     *
     * @param idItem        the id item
     * @param idFamille     the id famille
     * @param idTypeItem    the id type item
     * @param nomItem       the nom item
     * @param oblig         the oblig
     * @param cond          the cond
     * @param valCond       the val cond
     * @param valItem       the val item
     * @param valeur        the valeur
     * @param comItem       the com item
     * @param valTrie       the val trie
     * @param valeurDefault the valeur default
     * @param image         the image
     * @param flReserve     the fl reserve
     */
    public Item(int idItem, int idFamille, int idTypeItem, String nomItem,
                int oblig, int cond, String valCond, int valItem, String valeur, String comItem, int valTrie, String valeurDefault, byte[] image, int flReserve, byte[] photoImg, int iteration, int flPrivate) {
        super();
        this.idItem = idItem;
        this.idFamille = idFamille;
        this.idTypeItem = idTypeItem;
        this.nomItem = nomItem;
        this.oblig = oblig;
        this.cond = cond;
        this.val_cond = valCond;
        this.valItem = valItem;
        this.valeurNet = valeur;
        this.comItem = comItem;
        this.valTrie = valTrie;
        this.valeurDefault = valeurDefault;
        this.image = image;
        this.flReserve = flReserve;
        this.photoImg = photoImg;
        this.iteration = iteration;
        this.flPrivate = flPrivate;
    }

    /**
     * Instantiates a new item.
     *
     * @param cond      the cond
     * @param val_cond  the val_cond
     * @param valeurNet the valeur net
     */
    public Item(int cond, String val_cond, String valeurNet) {
        super();
        this.cond = cond;
        this.val_cond = val_cond;
        this.valeurNet = valeurNet;
    }

    /**
     * Instantiates a new item.
     */
    public Item() {
        // TODO Auto-generated constructor stub
    }

    /**
     * Sets the cond.
     *
     * @param cond the new cond
     */
    public void setCond(int cond) {
        this.cond = cond;
    }

    /**
     * Gets the id famille.
     *
     * @return the id famille
     */
    public int getIdFamille() {
        return idFamille;
    }

    /**
     * Sets the id famille.
     *
     * @param idFamille the new id famille
     */
    public void setIdFamille(int idFamille) {
        this.idFamille = idFamille;
    }

    /**
     * Gets the valeur net.
     *
     * @return the valeur net
     */
    public String getValeurNet() {
        return valeurNet;
    }

    /**
     * Gets the val item.
     *
     * @return the val item
     */
    public int getValItem() {
        return valItem;
    }

    /**
     * Gets the id item.
     *
     * @return the id item
     */
    public int getIdItem() {
        return idItem;
    }

    /**
     * Gets the id type item.
     *
     * @return the id type item
     */
    public int getIdTypeItem() {
        return idTypeItem;
    }

    /**
     * Gets the nom item.
     *
     * @return the nom item
     */
    public String getNomItem() {
        return nomItem;
    }

    /**
     * Gets the oblig.
     *
     * @return the oblig
     */
    public int getOblig() {
        return oblig;
    }

    /**
     * Gets the cond.
     *
     * @return the cond
     */
    public int getCond() {
        return cond;
    }

    /**
     * Gets the val_cond.
     *
     * @return the val_cond
     */
    public String getVal_cond() {
        return val_cond;
    }

    /**
     * Gets the com item.
     *
     * @return the com item
     */
    public String getComItem() {
        return comItem;
    }

    /**
     * Sets the id item.
     *
     * @param idItem the new id item
     */
    public void setIdItem(int idItem) {
        this.idItem = idItem;
    }

    /**
     * Sets the id type item.
     *
     * @param idTypeItem the new id type item
     */
    public void setIdTypeItem(int idTypeItem) {
        this.idTypeItem = idTypeItem;
    }

    /**
     * Sets the nom item.
     *
     * @param nomItem the new nom item
     */
    public void setNomItem(String nomItem) {
        this.nomItem = nomItem;
    }

    /**
     * Sets the oblig.
     *
     * @param oblig the new oblig
     */
    public void setOblig(int oblig) {
        this.oblig = oblig;
    }

    /**
     * Sets the val_cond.
     *
     * @param val_cond the new val_cond
     */
    public void setVal_cond(String val_cond) {
        this.val_cond = val_cond;
    }

    /**
     * Sets the val item.
     *
     * @param valItem the new val item
     */
    public void setValItem(int valItem) {
        this.valItem = valItem;
    }

    /**
     * Sets the valeur net.
     *
     * @param valeurNet the new valeur net
     */
    public void setValeurNet(String valeurNet) {
        this.valeurNet = valeurNet;
    }

    /**
     * Sets the com item.
     *
     * @param comItem the new com item
     */
    public void setComItem(String comItem) {
        this.comItem = comItem;
    }

    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(Item item) {

        int val1 = item.getValTrie();
        int val2 = this.getValTrie();
        if (val1 > val2) return -1;
        else if (val1 == val2) return 0;
        else return 1;
    }

    /**
     * Gets the valeur default.
     *
     * @return the valeur default
     */
    public String getValeurDefault() {
        return valeurDefault;
    }

    /**
     * Sets the valeur default.
     *
     * @param valeurDefault the new valeur default
     */
    public void setValeurDefault(String valeurDefault) {
        this.valeurDefault = valeurDefault;
    }

    /**
     * Gets the sub cause.
     *
     * @return the sub cause
     */
    public int getSubCause() {
        return subCause;
    }

    /**
     * Sets the sub cause.
     *
     * @param subCause the new sub cause
     */
    public void setSubCause(int subCause) {
        this.subCause = subCause;
    }

    /**
     * Gets the image.
     *
     * @return the image
     */
    public byte[] getImage() {
        return image;
    }

    /**
     * Sets the image.
     *
     * @param image the new image
     */
    public void setImage(byte[] image) {
        this.image = image;
    }

    /**
     * Gets the fl reserve.
     *
     * @return the fl reserve
     */
    public int getFlReserve() {
        return flReserve;
    }

    /**
     * Sets the fl reserve.
     *
     * @param flReserve the new fl reserve
     */
    public void setFlReserve(int flReserve) {
        this.flReserve = flReserve;
    }

    /* (non-Javadoc)
     * @see com.synchroteam.utils.ReportsItems#isHeader()
     */
    @Override
    public boolean isHeader() {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * Gets the group position.
     *
     * @return the groupPosition
     */
    public int getGroupPosition() {
        return groupPosition;
    }

    /**
     * Sets the group position.
     *
     * @param groupPosition the groupPosition to set
     */
    public void setGroupPosition(int groupPosition) {
        this.groupPosition = groupPosition;
    }

    //---------------------------PARCELABLE CODE----------------------------------------------------

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.idItem);
        dest.writeInt(this.idFamille);
        dest.writeInt(this.idTypeItem);
        dest.writeString(this.nomItem);
        dest.writeInt(this.oblig);
        dest.writeInt(this.cond);
        dest.writeString(this.val_cond);
        dest.writeInt(this.valItem);
        dest.writeString(this.valeurNet);
        dest.writeString(this.comItem);
        dest.writeInt(this.valTrie);
        dest.writeString(this.valeurDefault);
        dest.writeInt(this.subCause);
        dest.writeByteArray(this.image);
        dest.writeInt(this.flReserve);
        dest.writeInt(this.groupPosition);
        dest.writeInt(this.iteration);
        dest.writeByteArray(this.photoImg);
        dest.writeInt(this.flPrivate);
    }

    protected Item(Parcel in) {
        this.idItem = in.readInt();
        this.idFamille = in.readInt();
        this.idTypeItem = in.readInt();
        this.nomItem = in.readString();
        this.oblig = in.readInt();
        this.cond = in.readInt();
        this.val_cond = in.readString();
        this.valItem = in.readInt();
        this.valeurNet = in.readString();
        this.comItem = in.readString();
        this.valTrie = in.readInt();
        this.valeurDefault = in.readString();
        this.subCause = in.readInt();
        this.image = in.createByteArray();
        this.flReserve = in.readInt();
        this.groupPosition = in.readInt();
        this.iteration = in.readInt();
        this.photoImg = in.createByteArray();
        this.flPrivate = in.readInt();
    }

    public static final Parcelable.Creator<Item> CREATOR = new Parcelable.Creator<Item>() {
        @Override
        public Item createFromParcel(Parcel source) {
            return new Item(source);
        }

        @Override
        public Item[] newArray(int size) {
            return new Item[size];
        }
    };
}
