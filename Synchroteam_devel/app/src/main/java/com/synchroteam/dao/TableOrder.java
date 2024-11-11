package com.synchroteam.dao;

/**
 * Created by Trident on 9/28/2017.
 */

public class TableOrder {

    private String orderedTables;

    public TableOrder() {
        orderedTables = "T_CUSTOMERS," +
                "T_USERS," +
                "T_MESSAGES_OPERATEUR," +
                "T_ATTRIB_MESSAGES_OPER," +
                "TREF_MODULES," +
                "T_ATTRIB_MODULES," +
                "TREF_TRADUCTIONS_CUST," +
                "TREF_TRADUCTIONS," +
                "C_CONNECTIONS," +
                "TREF_GESTION_ACCES," +
                "TREF_STATUT_INTERVENTION," +
                "TREF_TYPE_INTERVENTION," +
                "TREF_MODELE_RAPPORT," +
                "TREF_MODELE_FAMILLE," +
                "TREF_MODELE_ITEM," +
                "TREF_MODELE_VALUE_ITEM," +
                "TREF_MODELE_BLOC," +
                "TREF_TYPINT_RAPPORT," +
                "T_PHOTOS_PDA," +
                "T_CLIENTS," +
                "T_SITES_CLIENTS," +
                "T_EQUIPEMENTS_CLIENTS," +
                "TREF_CATEGORIE_PIECE," +
                "TREF_PIECES," +
                "TREF_TYPE_CONGE," +
                "T_CONGE," +
                "T_INTERVENTIONS," +
                "T_SAISIE_RAPPORT," +
                "T_SORTIE_PIECE," +
                "T_TEMPS_INTERV," +
                "T_ATTACHMENTS," +
                "T_TAXRATES," +
                "T_FACTURES," +
                "T_LIGNES_FACTURE," +
                "T_STOCKS," +
                "T_PIECE_SERIALS," +
                "T_PIECE_DEMANDE," +
                "T_STOCK_PIECES," +
                "T_SAISIE_BLOC," +
                "T_PAYMENTS," +
                "T_PAYMENTS_LOG";
    }

    public String getOrderedTables() {
        return orderedTables;
    }
}
