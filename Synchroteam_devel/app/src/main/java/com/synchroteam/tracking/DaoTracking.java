package com.synchroteam.tracking;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.sap.ultralitejni17.ConfigPersistent;
import com.sap.ultralitejni17.Connection;
import com.sap.ultralitejni17.DatabaseManager;
import com.sap.ultralitejni17.PreparedStatement;
import com.sap.ultralitejni17.ResultSet;
import com.sap.ultralitejni17.StreamHTTPParms;
import com.sap.ultralitejni17.StreamHTTPSParms;
import com.sap.ultralitejni17.SyncParms;
import com.sap.ultralitejni17.ULjException;
import com.synchroteam.dao.Dao;
import com.synchroteam.dialogs.SynchronizationErrorDialog;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.utils.DaoManager;
import com.synchroteam.utils.Logger;
import com.synchroteam.utils.SharedPref;
import com.synchroteam.utils.SynchroteamUitls;
import com.synchroteam.utils.TrackingPrefList;

import java.text.SimpleDateFormat;
import java.util.Date;

// TODO: For production, Use the same host and port used in Dao (normal sync)

/**
 * The Class DaoTracking.
 *
 * @author Previous Developer
 */
public class DaoTracking {

    /**
     * The config.
     */
    public ConfigPersistent config;

    /**
     * The connTracking.
     */
    public Connection connTracking;

    /**
     * The tr.
     */
    private static TablesTracking tr = new TablesTracking();

    private static final String TAG = "DaoTracking";
    // ------------------local-------------------------
    // private static String host="10.0.2.2";
    // private static String host="192.168.1.128";
    // private static int port=8081;
    // -----------------recette------------------------
    // private static String host="83.231.128.170";
    // private static int port=2449;
    // -----------------recette2------------------------
    /****
     * Test URLS
     */
    // private static String host = "sync.suiviintervention.synchroteam.com";
    // private static String host = "cholet04022016.synchroteam.com";
    // private static int port = 2449;
//	 private static String host = "demo.suiviintervention.synchroteam.com";
//	 private static int port = 2449;
    // ------------------prod--------------------------
    /** The host. */
    /****
     * Production URLS
     */
//    private static String host = "mobilink.synchroteam.com";

    /**
     * The port.
     */
//    private static int port = 8000;

    /** Testing port. */
//	private static int port = 8005;

    /****
     * Development URLS
     */
//    private static String host = "demo.suiviintervention.synchroteam.com";

    /**
     * The port.
     */
//    private static int port = 2560;

    /** The port. */
//	 private static int port = 2559;

    /****
     * Testing URLS
     */
//	 private static String host = "demo.suiviintervention.synchroteam.com";

    /**
     * The port.
     */
//	 private static int port = 2449;
//	private static int port = 2560;

    //----------ULTRELITE 17-----------------
//	private static String host = "cholet04022016.synchroteam.com";

//	private static int port = 8001;
    //----------ULTRELITE 17-----------------


//            ************************** This is v44 host and port **************************
    // The new host.
//    private static String host = "demo.suiviintervention.synchroteam.com";

    // The new port.
//    private static int port = 2560;

//            ************************** end of code: v44 host and port **************************

//  **************************** Using host and port from web service prod V45 *******************
    private static String host;
    private static int port;


//    /**
//     * The script.
//     */
//    public static String script = "GPS_TRACKING_1";

    /**
     * The script version v52.
     */
//    public static String script = "GPS_TRACKING_2";

    /**
     * The script version v55.
     */
    public static String script = "GPS_TRACKING_3";

    /**
     * The db name.
     */
    public static String dbName = "STGPS.ulj";


    private Context context;

    private Dao dao;

    /**
     * Instantiates a new dao tracking.
     */
    public DaoTracking() {
        super();
    }

    /**
     * Instantiates a new dao tracking.
     *
     * @param ctx the ctx
     */
    public DaoTracking(Context ctx) {
        connectDatabase(ctx);
        this.context = ctx;
    }

    /**
     * Close database.
     */
    public synchronized void closeDatabase() {
        try {
            if (connTracking != null) {
                connTracking.commit();
                connTracking.release();
            }

            // DatabaseManager.release();

            Log.i(TAG,
                    "DatabaseManager object relaese ");
        } catch (ULjException e) {
            // TODO Auto-generated catch block
            Logger.printException(e);
        }
    }

    /**
     * Sync for Prod Dao Tracking
     *
     * @param user       the user
     * @param pwd        the pwd
     * @param domain     the domain
     * @param scriptGlob the script glob
     * @throws ULjException the u lj exception
     */
    public synchronized boolean sync(String user, String pwd, String domain,
                                     String scriptGlob) {

        Logger.log(TAG, "DURING SYNC NORMAL TRACKING ====>" + user + " ; " + pwd);

        boolean result = false;
        host = SharedPref.getSyncStdServer(context);
        port = SharedPref.getSyncStdPort(context);
        boolean stdSSL = SharedPref.getSyncStdSsl(context);

        if (host != null && port != 0) {

            SyncParms _syncParms;
            try {

                if (stdSSL) {

                    Logger.log(TAG, "Sync started");
                    _syncParms = connTracking.createSyncParms(SyncParms.HTTPS_STREAM, domain + "_" + user + "/#/"
                            + scriptGlob, script);
                    _syncParms.setPassword(pwd);

                    _syncParms.setAcknowledgeDownload(true);

                    StreamHTTPSParms _streamParms = (StreamHTTPSParms) _syncParms
                            .getStreamParms();

                    //For Ultralite17, we need to set the certificate name, company and unit.
                    _streamParms.setCertificateName(context.getString(R.string.certificate_name));
                    _streamParms.setCertificateCompany(context.getString(R.string.certificate_company));
                    _streamParms.setCertificateUnit(context.getString(R.string.certificate_unit));
                    _streamParms.setTrustedCertificates(context.getString(R.string.textPemCertificatePath));

                    _streamParms.setPort(port);
                    _streamParms.setHost(host);


                } else {

                    Logger.log(TAG, "Sync started");
                    _syncParms = connTracking.createSyncParms(domain + "_" + user + "/#/"
                            + scriptGlob, script);
                    _syncParms.setPassword(pwd);

                    _syncParms.setAcknowledgeDownload(true);

                    StreamHTTPParms _streamParms = (StreamHTTPParms) _syncParms.getStreamParms();
                    _streamParms.setPort(port);
                    _streamParms.setHost(host);

                }

                // connTracking.checkpoint();
                connTracking.synchronize(_syncParms);
                result = true;
            } catch (ULjException e) {
                e.printStackTrace();
                Logger.log(TAG, "TRACKING SYNC EXCEPT =====>" + e);
                result = false;
            } catch (Exception e) {
                Logger.log(TAG, "TRACKING SYNC EXCEPT =====>" + e);
                result = false;
            }

            deleteAllTrackingTables("GPS_SUIVI", null);

            SynchroteamUitls.logInFile("Tracking Sync Successfull");

        } else {
//            Toast.makeText(context, context.getString(R.string.msg_synch_error), Toast.LENGTH_SHORT).show();
            showSyncFailureMsgDialog(context.getString(R.string.msg_synch_error_new));
            result = false;
        }
        return result;
    }

    /**
     * For showing the synchronization failure messages
     */
    protected void showSyncFailureMsgDialog(String syncFailureMsg) {

        SynchronizationErrorDialog synchronizationErrorDialog = new SynchronizationErrorDialog
                ((Activity) context, syncFailureMsg, new SynchronizationErrorDialog
                        .SynchronizationErrorInterface() {
                    @Override
                    public void doOnOkayClick() {
                        //perform any action
                    }
                });

        synchronizationErrorDialog.show();
    }

    /**
     * Sync for Devel Dao Tracking
     *
     * @param user
     * @param pwd
     * @param domain
     * @param scriptGlob
     */
//    public synchronized void sync(String user, String pwd, String domain,
//                                  String scriptGlob) {
//
//        boolean stdSSL = false;
//
//        if (host != null && port != 0) {
//
//            SyncParms _syncParms;
//            try {
//
//                if (stdSSL) {
//
//                    Logger.log(TAG, "Sync started");
//                    _syncParms = connTracking.createSyncParms(SyncParms.HTTPS_STREAM, domain + "_" + user + "/#/"
//                            + scriptGlob, script);
//                    _syncParms.setPassword(pwd);
//
//                    _syncParms.setAcknowledgeDownload(true);
//
//                    StreamHTTPSParms _streamParms = (StreamHTTPSParms) _syncParms
//                            .getStreamParms();
//
//                    //For Ultralite17, we need to set the certificate name, company and unit.
//                    _streamParms.setCertificateName(context.getString(R.string.certificate_name));
//                    _streamParms.setCertificateCompany(context.getString(R.string.certificate_company));
//                    _streamParms.setCertificateUnit(context.getString(R.string.certificate_unit));
//                    _streamParms.setTrustedCertificates(context.getString(R.string.textPemCertificatePath));
//
//                    _streamParms.setPort(port);
//                    _streamParms.setHost(host);
//
//
//                } else {
//
//                    Logger.log(TAG, "Sync started");
//                    _syncParms = connTracking.createSyncParms(domain + "_" + user + "/#/"
//                            + scriptGlob, script);
//                    _syncParms.setPassword(pwd);
//
//                    _syncParms.setAcknowledgeDownload(true);
//
//                    StreamHTTPParms _streamParms = (StreamHTTPParms) _syncParms.getStreamParms();
//                    _streamParms.setPort(port);
//                    _streamParms.setHost(host);
//
//                }
//
//                // connTracking.checkpoint();
//                connTracking.synchronize(_syncParms);
//            } catch (ULjException e) {
//                e.printStackTrace();
//            }
//
//            deleteAllTrackingTables("GPS_SUIVI", null);
//
//            SynchroteamUitls.logInFile("Tracking Sync Successfull");
//
//        } else {
//            Toast.makeText(context, context.getString(R.string.msg_synch_error_new), Toast.LENGTH_SHORT).show();
//        }
//
//    }


    /**
     * Connect database.
     *
     * @param ctx the ctx
     */
    public synchronized void connectDatabase(Context ctx) {

        try {

            connTracking = null;
            config = DatabaseManager
                    .createConfigurationFileAndroid(dbName, ctx);
            connTracking = DatabaseManager.connect(config);

        } catch (Exception e) {

            try {

//                config.setCreationString("utf8_encoding=true");
                config.setCreationString("page_size=8k;utf8_encoding=true");

                connTracking = DatabaseManager.createDatabase(config);

                Log.e("tracking database ", " tracking createDatabase");

                executeDDL(tr.getT_activite());
                executeDDL(tr.getT_session_gps());
                executeDDL(tr.getT_suivi());
                executeDDL(tr.getGps_steps());
                executeDDL(tr.getGps_steps_index());

                initDb();

                Log.e("tracking database", "tracking createDatabase");

            } catch (Exception ex) {
                Logger.printException(ex);
            }
        }
    }

    /**
     * Gets connection object
     *
     * @return connection
     */
    private synchronized Connection getConnectionObj() {

//        new changes
        if (connTracking == null) {
            try {
                connTracking = DatabaseManager.connect(config);
            } catch (ULjException e) {
                releaseAllConnections();
                e.printStackTrace();
            }
        }

        return connTracking;
    }

    /**
     * After 10th sync we got an exception, not a normal sync.
     * 1. start a job.
     * 2. then came back and start another one .
     * 3. Likewise, we get an exception while starting the 11th job.
     * Exception ::  "Database server connection limit exceeded".
     * Hence release all the connections and reconnect a new one.
     *
     * @return new connection object.
     */
    private synchronized Connection releaseAllConnections() {

        try {
            DatabaseManager.release();
            if (connTracking != null)
                connTracking.release();
            connTracking = DatabaseManager.connect(config);

        } catch (ULjException e) {
            Logger.printException(e);
        }
        return connTracking;
    }

    public synchronized void deleteAllTrackingTables(String tablesname, Context ctx) {
        PreparedStatement ps = null;
        try {

            Log.e(TAG, "DAO TRACKING TABLE NAME " + tablesname);


            ps = getConnectionObj().prepareStatement("DELETE FROM " + tablesname);
            ps.execute();
//            ps.close();
//            connTracking.commit();

            SynchroteamUitls
                    .logInFile("DaoTracking " + tablesname + " deleted");

        } catch (Exception e) {
            Logger.printException(e);

        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            try {
                connTracking.commit();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public synchronized void deleteTrackingTables(Context ctx) {

        deleteAllTrackingTables("GPS_ACTIVITE", ctx);

        deleteAllTrackingTables("GPS_SUIVI", ctx);

        deleteAllTrackingTables("T_SESSION_GPS", ctx);

    }

    /**
     * Inits the db.
     */
    private synchronized void initDb() {
        setSessiondata("heur1", "08:00");
        setSessiondata("heur2", "18:00");
        setSessiondata("jours", "2-3-4-5-6-0-0");
        setSessiondata("periode", "5");
        setSessiondata("frequence", "50");
        setSessiondata("tracking", "off");
    }

    /**
     * Sets the sessiondata.
     *
     * @param nom  the nom
     * @param data the data
     */
    public synchronized void setSessiondata(String nom, String data) {
        ResultSet cursor = null;
        try {
            cursor = getConnectionObj().prepareStatement(
                    "SELECT NOM FROM t_session_gps WHERE NOM='" + nom + "'")
                    .executeQuery();
            if (cursor.next()) {
                executeDDL("UPDATE t_session_gps SET VALEUR='" + data
                        + "' where NOM='" + nom + "'");
//                cursor.close();
//                connTracking.commit();
            } else {
                executeDDL("INSERT INTO t_session_gps(nom,valeur) values ('"
                        + nom + "','" + data + "')");
//                cursor.close();
//                connTracking.commit();
            }
        } catch (ULjException e) {
            Logger.printException(e);
        } finally {
            if (cursor != null) {
                try {
                    cursor.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            try {
                connTracking.commit();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * Gets the sessiondata.
     *
     * @param nom the nom
     * @return the sessiondata
     */
    public synchronized String getSessiondata(String nom) {
        String val = "";
        ResultSet cursor = null;
        try {

//            if (connTracking == null) {
//                return "";
//            }

            cursor = getConnectionObj().prepareStatement(
                    "SELECT VALEUR FROM t_session_gps WHERE NOM='" + nom + "'")
                    .executeQuery();
            if (cursor.next()) {
                val = cursor.getString(1);
//                cursor.close();
//                connTracking.commit();

            } else {
//                cursor.close();
//                connTracking.commit();
                val = "";
            }
        } catch (ULjException e) {
            Logger.printException(e);
            val = "";
        } finally {
            if (cursor != null) {
                try {
                    cursor.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            try {
                connTracking.commit();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        return val;
    }

    /**
     * Execute ddl.
     *
     * @param sql the sql
     */
    public synchronized boolean executeDDL(String sql) {

        boolean result = false;
        PreparedStatement ps = null;

        try {
            ps = getConnectionObj().prepareStatement(sql);
            ps.execute();
            result = true;
//            ps.close();
//            connTracking.commit();
        } catch (ULjException e) {
            result = false;
            Logger.printException(e);
        } finally {

            if (ps != null) {
                try {
                    ps.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            try {
                connTracking.commit();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return result;

    }

    /**
     * Save coordinate.
     *
     * @param idUser    the id user
     * @param vitesse   the vitesse
     * @param latitude  the latitude
     * @param longitude the longitude
     */
    public synchronized boolean saveCoordinate(int idUser, String vitesse, Double latitude,
                                               Double longitude) {

        Logger.log(TAG, "location points while saving ---->>>>" + latitude + " , " + longitude);
        String currentDateandTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        TrackingPrefList.TrackingModel model = new TrackingPrefList.TrackingModel();
        model.setTrackingUserId(idUser);
        model.setTrackingDateTime(currentDateandTime);
        model.setTrackingLatitude(latitude);
        model.setTrackingLongitude(longitude);

        TrackingPrefList trackingPrefList = new TrackingPrefList();
        trackingPrefList.addModel(context, model);

        if (vitesse.contains(",")) {
            vitesse = vitesse.replace(",", ".");
        }

        Double lat = formatingDouble(latitude);
        Double longi = formatingDouble(longitude);

        try {
            Logger.log(TAG, "values saved");

            return executeDDL("INSERT INTO GPS_SUIVI (USER_ID,VITESSE,LATITUDE,LONGITUDE,HORODATAGE) VALUES ("
                    + idUser
                    + ","
                    + vitesse
                    + ",'"
                    + lat
                    + "','"
                    + longi + "',CURRENT TIMESTAMP)");

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        // try {
        // File file = new File(Environment.getExternalStorageDirectory()
        // + "/" + "SynchroteanLogs.txt");
        // // FileOutputStream outputStream = new FileOutputStream(file);
        // Calendar calendar = Calendar.getInstance();
        // String temp = "Time : " + calendar.getTime().toString() + " "
        // + "Location:" + longitude + " " + latitude + " "
        // + "Vitesse: " + vitesse + " UserId: " + idUser + " \n ";
        // FileWriter fileWritter = new FileWriter(file, true);
        // BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
        // bufferWritter.append(temp);
        // bufferWritter.close();
        // } catch (IOException e) {
        // e.printStackTrace();
        // }
    }

    private Double formatingDouble(Double value) {

        Double result;
        String valDec = "" + value;

        try {
            if (valDec.contains(",")) {
                valDec = valDec.replace(",", ".");
            }
            result = Double.parseDouble(valDec);

        } catch (Exception e) {
            result = value;
        }

        return result;
    }


    /**
     * Save activite tracking.
     *
     * @param idUser   the id user
     * @param activite the activite
     */
    public synchronized void saveActiviteTracking(int idUser, String activite) {
        executeDDL("INSERT INTO GPS_ACTIVITE (USER_ID,TYPE_ACTIVITE,HORODATAGE,SAISIE) VALUES ("
                + idUser + ",'" + activite + "',CURRENT TIMESTAMP,NULL)");
    }

    public synchronized int getTrackingLocationCount(int userId) {
        String locationCount = "SELECT COUNT(*) FROM GPS_SUIVI WHERE USER_ID="
                + userId;
        int status = -1;
        PreparedStatement stmt = null;
        ResultSet cursor = null;
        try {
            stmt = getConnectionObj().prepareStatement(locationCount);
            cursor = stmt.executeQuery();
            while (cursor.next()) {

                status = cursor.getInt(1);
            }
//            cursor.close();
//            connTracking.commit();

        } catch (Exception e) {
            Logger.printException(e);
        } finally {
            if (cursor != null) {
                try {
                    cursor.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            try {
                connTracking.commit();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return status;
    }

    public synchronized void printLatLong() {
        String query = "SELECT * FROM GPS_SUIVI";
        PreparedStatement stmt;
        try {
            stmt = getConnectionObj().prepareStatement(query);
            ResultSet cursor = stmt.executeQuery();
            while (cursor.next()) {

                Logger.log("LAT", cursor.getString(2));
                Logger.log("LONG", cursor.getString(3));
                SynchroteamUitls.logInFile("Versite " + cursor.getString(4));

            }
            cursor.close();
            connTracking.commit();
        } catch (ULjException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }


    public synchronized void insertActivityInGpsSteps(String lat, String lng, String event, int dateIndex, String idActivityRemote){
        try {
            dao = DaoManager.getInstance();
            int idUser = dao.getUser().getId();
            int idCustomer = dao.getIdCustomer();

            boolean success = executeDDL("INSERT INTO GPS_STEPS (ID_CUSTOMER, ID_ACTIVITY, ID_JOB, ID_USER, HORODATAGE, LAT, LNG, TYPE, EVENT, DATE_INDEX,ID_ACTIVITY_REMOTE) " +
                    "VALUES ("+idCustomer+",NULL,NULL,"+idUser+",CURRENT TIMESTAMP,"+lat+","+lng+",'activity','"+event+"',"+dateIndex+",'"+idActivityRemote+"')");

            if (success) {

                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        sync(dao.getUser().getLogin(), dao.getUser().getPwd(),
                                dao.getUserDomain(), Dao.script);
                    }
                });


                Log.e("INSERT","THE INSERT QUERY IS WORKING");
            }

        } catch (Exception exc) {
            Logger.printException(exc);
        }
    }

    public synchronized void insertJobInGpsSteps(String jobId, String lat, String lng, String event, int dateIndex){
        try {
            dao = DaoManager.getInstance();
            int idUser = dao.getUser().getId();
            int idCustomer = dao.getIdCustomer();

            boolean success = executeDDL("INSERT INTO GPS_STEPS (ID_CUSTOMER, ID_ACTIVITY, ID_JOB, ID_USER, HORODATAGE, LAT, LNG, TYPE, EVENT, DATE_INDEX,ID_ACTIVITY_REMOTE) " +
                    "VALUES ("+idCustomer+",NULL,"+ "'"+jobId+ "'"+","+idUser+",CURRENT TIMESTAMP,"+lat+","+lng+","+ "'job'"+","+ "'"+event+ "'"+","+dateIndex+",NULL)");

            if (success) {

                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        sync(dao.getUser().getLogin(), dao.getUser().getPwd(),
                                dao.getUserDomain(), Dao.script);
                    }
                });
                Log.e("INSERT","THE INSERT QUERY IS WORKING");
            }

        } catch (Exception exc) {
            Logger.printException(exc);
        }
    }

}


