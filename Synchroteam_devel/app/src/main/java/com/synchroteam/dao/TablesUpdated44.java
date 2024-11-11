package com.synchroteam.dao;

/**
 * Created by Trident on 22/3/17.
 * <p>
 * Used for migration from old app (Update)
 */

public class TablesUpdated44 {


    private String t_sites_clients_alter;
    private String t_equipements_clients_alter;
    private String t_temps_interv_alter;

    public TablesUpdated44() {

        t_sites_clients_alter = "ALTER TABLE T_SITES_CLIENTS ADD PUBLIC_LINK VARCHAR(1024) NULL";
        t_equipements_clients_alter = "ALTER TABLE T_EQUIPEMENTS_CLIENTS ADD PUBLIC_LINK VARCHAR(1024) NULL";
        t_temps_interv_alter = "ALTER TABLE T_TEMPS_INTERV ADD DT_FIN_PREV TIMESTAMP NULL";
    }

    public String getT_sites_clients_alter() {
        return t_sites_clients_alter;
    }

    public String getT_equipements_clients_alter() {
        return t_equipements_clients_alter;
    }

    public String getT_temps_interv_alter() {
        return t_temps_interv_alter;
    }
}
