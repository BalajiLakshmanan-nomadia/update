package com.synchroteam.beans;

// TODO: Auto-generated Javadoc

/**
 * Previous developer Code The Class GestionAcces.
 *
 * @author Previous Developer
 */
public class GestionAcces {

    /**
     * The option taracking.
     */
    private int optionTaracking;
//52.0.0
    private int flagSignCust;
    private int flagSignTech;
    private int numDecimals;

    public int getFlagSignCust() {
        return flagSignCust;
    }

    public void setFlagSignCust(int flagSignCust) {
        this.flagSignCust = flagSignCust;
    }

    public int getFlagSignTech() {
        return flagSignTech;
    }

    public void setFlagSignTech(int flagSignTech) {
        this.flagSignTech = flagSignTech;
    }

    public int getNumDecimals() {
        return numDecimals;
    }

    public void setNumDecimals(int numDecimals) {
        this.numDecimals = numDecimals;
    }

    /**
     * The option help surfing.
     */
    private int optionHelpSurfing;

    /**
     * The option replanif.
     */
    private int optionReplanif;

    /**
     * The option reject.
     */
    private int optionReject;

    /**
     * The fl comments rapport.
     */
    private int flCommentsRapport;

    /**
     * The fl mandatory description.
     */
    private int flMandatoryDescription;

    /**
     * The fl force report template.
     */
    private int flForceReportTemplate;

    /**
     * The fl page sites.
     */
    private int flPageSites;

    /* flag to restrict/allow the user to create/update an invoice or quotation */
    private int flCreateUpdateInvoiceQuotation;

    /* flag to show/hide the price of the parts & services */
    private int flMobPrice;

    /* flag to show/hide the clients list */
    private int flListCustomers;

    /* flag to show/hide the inventory section */
    private int flSectionStock;

    /* flag to show/hide the inventory section */
    private int flSectionDelSign;

    /* flag to show/hide the transfer radio button */
    private int flTakeFrom;

    /* flag to show/hide the hide return container */
    private int flSendTo;

    /* flag to show/hide the request radio button */
    private int flRequestFrom;

    /**
     * Flag to show the If Invoice strict is enabled or not
     */
    private boolean flInvoiceStrict;

    public void setOptionTaracking(int optionTaracking) {
        this.optionTaracking = optionTaracking;
    }

    public void setOptionHelpSurfing(int optionHelpSurfing) {
        this.optionHelpSurfing = optionHelpSurfing;
    }

    public void setOptionReplanif(int optionReplanif) {
        this.optionReplanif = optionReplanif;
    }

    public void setOptionReject(int optionReject) {
        this.optionReject = optionReject;
    }

    public void setFlCommentsRapport(int flCommentsRapport) {
        this.flCommentsRapport = flCommentsRapport;
    }

    public void setFlMandatoryDescription(int flMandatoryDescription) {
        this.flMandatoryDescription = flMandatoryDescription;
    }

    public void setFlForceReportTemplate(int flForceReportTemplate) {
        this.flForceReportTemplate = flForceReportTemplate;
    }

    public void setFlPageSites(int flPageSites) {
        this.flPageSites = flPageSites;
    }

    public void setFlCreateUpdateInvoiceQuotation(int flCreateUpdateInvoiceQuotation) {
        this.flCreateUpdateInvoiceQuotation = flCreateUpdateInvoiceQuotation;
    }

    public void setFlMobPrice(int flMobPrice) {
        this.flMobPrice = flMobPrice;
    }

    public void setFlListCustomers(int flListCustomers) {
        this.flListCustomers = flListCustomers;
    }

    public void setFlSectionStock(int flSectionStock) {
        this.flSectionStock = flSectionStock;
    }

    public void setFlSectionDelSign(int flSectionDelSign) {
        this.flSectionDelSign = flSectionDelSign;
    }

    public void setFlTakeFrom(int flTakeFrom) {
        this.flTakeFrom = flTakeFrom;
    }

    public void setFlSendTo(int flSendTo) {
        this.flSendTo = flSendTo;
    }

    public void setFlRequestFrom(int flRequestFrom) {
        this.flRequestFrom = flRequestFrom;
    }

    /**
     * Instantiates a new gestion acces.
     *
     * @param optionTaracking                the option taracking
     * @param optionHelpSurfing              the option help surfing
     * @param optionReplanif                 the option replanif
     * @param optionReject                   the option reject
     * @param flCommentsRapport              the fl comments rapport
     * @param flMandatoryDescription         the fl mandatory description
     * @param flForceReportTemplate          the fl force report template
     * @param flPageSites                    the fl page sites
     * @param flCreateUpdateInvoiceQuotation flag to create or update invoice quotation
     * @param flMobPrice                     flag to show/hide price in parts & services
     * @param flListCustomers                flag to show/hide clients
     * @param flSectionStock                 flag to show/hide inventory
     * @param flSectionDelSign               flag to delete signature in report, if any changes made
     */
    public GestionAcces()
    {

    }
    public GestionAcces(int optionTaracking, int optionHelpSurfing,
                        int optionReplanif, int optionReject, int flCommentsRapport,
                        int flMandatoryDescription, int flForceReportTemplate,
                        int flPageSites, int flCreateUpdateInvoiceQuotation,
                        int flMobPrice, int flListCustomers, int flSectionStock,
                        int flSectionDelSign,int flTakeFrom, int flSendTo, int flRequestFrom,int numDecimals) {
        super();
        this.optionTaracking = optionTaracking;
        this.optionHelpSurfing = optionHelpSurfing;
        this.optionReplanif = optionReplanif;
        this.optionReject = optionReject;
        this.flCommentsRapport = flCommentsRapport;
        this.flMandatoryDescription = flMandatoryDescription;
        this.flForceReportTemplate = flForceReportTemplate;
        this.flPageSites = flPageSites;
        this.flCreateUpdateInvoiceQuotation = flCreateUpdateInvoiceQuotation;
        this.flMobPrice = flMobPrice;
        this.flListCustomers = flListCustomers;
        this.flSectionStock = flSectionStock;
        this.flSectionDelSign = flSectionDelSign;
        this.flTakeFrom = flTakeFrom;
        this.flSendTo = flSendTo;
        this.flRequestFrom = flRequestFrom;
        this.numDecimals=numDecimals;
    }
    public GestionAcces(int optionTaracking,
                        int optionHelpSurfing,
                        int optionReplanif,
                        int optionReject,
                        int flCommentsRapport,
                        int flMandatoryDescription,
                        int flForceReportTemplate,
                        int flPageSites,
                        int flCreateUpdateInvoiceQuotation,
                        int flMobPrice,
                        int flListCustomers,
                        int flSectionStock,
                        int flSectionDelSign,
                        int flTakeFrom,
                        int flSendTo,
                        int flRequestFrom,
                        int flagSignTech,
                        int flagSignCust,int numDecimals) {
        super();
        this.optionTaracking = optionTaracking;
        this.optionHelpSurfing = optionHelpSurfing;
        this.optionReplanif = optionReplanif;
        this.optionReject = optionReject;
        this.flCommentsRapport = flCommentsRapport;
        this.flMandatoryDescription = flMandatoryDescription;
        this.flForceReportTemplate = flForceReportTemplate;
        this.flPageSites = flPageSites;
        this.flCreateUpdateInvoiceQuotation = flCreateUpdateInvoiceQuotation;
        this.flMobPrice = flMobPrice;
        this.flListCustomers = flListCustomers;
        this.flSectionStock = flSectionStock;
        this.flSectionDelSign = flSectionDelSign;
        this.flTakeFrom = flTakeFrom;
        this.flSendTo = flSendTo;
        this.flRequestFrom = flRequestFrom;
        this.flagSignCust=flagSignCust;
        this.flagSignTech=flagSignTech;
        this.numDecimals=numDecimals;
    }
        /**
         * Gets the option taracking.
         *
         * @return the option taracking
         */
    public int getOptionTaracking() {
        return optionTaracking;
    }

    /**
     * Gets the option help surfing.
     *
     * @return the option help surfing
     */
    public int getOptionHelpSurfing() {
        return optionHelpSurfing;
    }

    /**
     * Gets the option replanif.
     *
     * @return the option replanif
     */
    public int getOptionReplanif() {
        return optionReplanif;
    }

    /**
     * Gets the option reject.
     *
     * @return the option reject
     */
    public int getOptionReject() {
        return optionReject;
    }

    /**
     * Gets the fl comments rapport.
     *
     * @return the fl comments rapport
     */
    public int getFlCommentsRapport() {
        return flCommentsRapport;
    }

    /**
     * Gets the fl mandatory description.
     *
     * @return the fl mandatory description
     */
    public int getFlMandatoryDescription() {
        return flMandatoryDescription;
    }

    /**
     * Gets the fl force report template.
     *
     * @return the fl force report template
     */
    public int getFlForceReportTemplate() {
        return flForceReportTemplate;
    }

    /**
     * Gets the fl page sites.
     *
     * @return the fl page sites
     */
    public int getFlPageSites() {
        return flPageSites;
    }

    /**
     * Gets the flag for create update invoice quotation
     *
     * @return flag value for flCreateUpdateInvoiceQuotation
     */
    public int getFlCreateUpdateInvoiceQuotation() {
        return flCreateUpdateInvoiceQuotation;
    }

    /**
     * Gets the flag for show/hide the price of the parts & services
     *
     * @return flag value for flMobPrice
     */
    public int getFlMobPrice() {
        return flMobPrice;
    }

    /**
     * Gets the flag for show/hide client list.
     *
     * @return flListCustomers
     */
    public int getFlListCustomers() {
        return flListCustomers;
    }

    /**
     * Gets the flag for show/hide inventory list.
     *
     * @return flSectionStock
     */
    public int getFlSectionStock() {
        return flSectionStock;
    }

    /**
     * Gets the flag to delete the customer & client signature if any changes made on report or parts & services
     *
     * @return flSectionDelSign
     */
    public int getFlSectionDelSign() {
        return flSectionDelSign;
    }

    public int getFlTakeFrom() {
        return flTakeFrom;
    }

    public int getFlSendTo() {
        return flSendTo;
    }

    public int getFlRequestFrom() {
        return flRequestFrom;
    }


    public boolean isFlInvoiceStrict() {
        return flInvoiceStrict;
    }

    public void setFlInvoiceStrict(boolean flInvoiceStrict) {
        this.flInvoiceStrict = flInvoiceStrict;
    }
}
