package com.synchroteam.tracking2

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION
import android.location.Location
import android.os.Build
import android.os.Handler
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.synchroteam.beans.LocationPoints
import com.synchroteam.dao.Dao
import com.synchroteam.synchroteam.SyncroTeamApplication
import com.synchroteam.synchroteam3.R
import com.synchroteam.tracking.DaoTracking
import com.synchroteam.tracking.TrackPoint
import com.synchroteam.utils.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*


class LocationService: Service() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val UPDATE_INTERVAL_IN_MILLISECONDS: Long = 10000
    private val FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS: Long = UPDATE_INTERVAL_IN_MILLISECONDS / 2



    private lateinit var locationClient: LocationClient
    var status = 0
    private val UIhandler = Handler()
    private var currentLat = 0.00
    private var currentLon = 0.00
    private var isFirstEntry = false
    private var isLastEntry = false
    private var numPointInt = 0
    var intent: Intent? = null

    //For calculating the distance
    private var lastLat: Double = 0.00
    private var lastLong: Double = 0.00
    var totDistance = 0.00
    var numberFormat: DecimalFormat? = null
    var oldTp: TrackPoint? = null
    private var locationList: ArrayList<LocationPoints>? = null

    /**
     * The dao.
     */
    private var dao: DaoTracking? = null

    /**
     * The dao2.
     */
    private var dao2: Dao? = null

    private val sendUpdatesToUI: Runnable = object : Runnable {
        override fun run() {
            intent?.putExtra(KEY_IS_FIRST_ENTRY, isFirstEntry)
            intent?.putExtra(KEY_IS_LAST_ENTRY, isLastEntry)
            intent?.putExtra(KEY_LAT, currentLat)
            intent?.putExtra(KEY_LON, currentLon)
            intent?.putExtra(KEY_DIST, totDistance)
            sendBroadcast(intent)
            if (isLastEntry) {
                status = 0
                UIhandler.removeCallbacks(this)
            }
        }
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        intent = Intent(BROADCAST_ACTION)
        locationClient = DefaultLocationClient(
            applicationContext,
            LocationServices.getFusedLocationProviderClient(applicationContext)
        )
        dao = DaoTrackingManager.getInstance()
        dao2 = DaoManager.getInstance()
        initializeDecimalFormat()
        locationList = ArrayList()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.action) {
            ACTION_START -> start()
            ACTION_STOP -> stop()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun start() {

        if (status != 0)
            return

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "location",
                "Location",
                NotificationManager.IMPORTANCE_LOW
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, "location")
            .setContentTitle(getString(R.string.app_name))
            .setContentText(getString(R.string.notification_text_travel))
            .setSmallIcon(R.drawable.icon_notif)
            .setOngoing(true)


        isLastEntry = false

        locationClient
            .getLocationUpdates(1000L)
            .catch { e -> e.printStackTrace() }
            .onEach { location ->
                //lastPoint.postValue(location)
                onLocationChanged(location)

                val unit = LocationUtils.stringKMorMi()
                var calDist = "0.00"
                calDist = numberFormat?.format(totDistance).toString()

                val updatedNotification = notification.setContentText(
                    getString(R.string.txt_trip).toString() + ": " + calDist + " " + unit
                )
                notificationManager.notify(1, updatedNotification.build())

                UIhandler.postDelayed(sendUpdatesToUI, 100)

            }
            .launchIn(serviceScope)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForeground(1, notification.build(),FOREGROUND_SERVICE_TYPE_LOCATION)
        }

        (applicationContext as SyncroTeamApplication)
             .syncroteamActivityInstance.updateUiOnTrakingStatusChange(true)


        numPointInt = 0
    }

    private fun stop() {
        if (numPointInt > 0) {
            dao2?.getUser().let {
                dao!!.sync(
                    it!!.getLogin(), it.getPwd(),
                    dao2!!.userDomain, Dao.script
                )
            }
        }
        isLastEntry = true
        status = 0
        locationList?.clear()
        UIhandler.postDelayed(sendUpdatesToUI, 100)
        stopForeground(true)
        stopSelf()

        (applicationContext as SyncroTeamApplication)
            .syncroteamActivityInstance.updateUiOnTrakingStatusChange(false)

    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    private fun calculatingDist(): Double {
        var dist = 0.00
        dist = if (lastLat !== currentLat && lastLong !== currentLon) {
            LocationUtils.distanceBetweenTwoPointAndroid(
                lastLat, lastLong,
                currentLat, currentLon
            )
            //for kms
            //            dist = dist / 1000;
        } else 0.0
        Logger.log(
            LocationService.TAG,
            "DISTANCE_CALC SERVICE CALCULATION====>$lastLat,$lastLong"
        )
        Logger.log(
            LocationService.TAG,
            "DISTANCE_CALC SERVICE CALCULATION====>$currentLat,$currentLon"
        )
        Logger.log(
            LocationService.TAG,
            "DISTANCE_CALC SERVICE CALCULATION====>$dist"
        )
        return dist
    }

    private fun calculateTotalDistance(): Double {
        var totDistance = 0.00
        if (locationList != null && locationList!!.size > 1) {
            Logger.log(
                LocationService.TAG,
                "DISTANCE_CALC LIST SIZE ====>" + locationList!!.size
            )
            var i = 0
            while (i + 1 < locationList!!.size) {
                val loc1 = locationList!![i]
                val loc2 = locationList!![i + 1]
                //                totDistance += LocationUtils.distanceBetweenTwoPoint(loc1.getLatitude(), loc1.getLongitude(), loc2.getLatitude(), loc2.getLongitude());
                totDistance += LocationUtils.distanceBetweenTwoPointAndroid(
                    loc1.latitude,
                    loc1.longitude,
                    loc2.latitude,
                    loc2.longitude
                )
                i++
            }
            //for kms

//            totDistance = totDistance / 1000;
            Logger.log(
                LocationService.TAG,
                "DISTANCE_CALC TOTAL NEW====>$totDistance"
            )
        }
        return totDistance
    }

    fun onLocationChanged(location: Location) {
        if (status == 0) {
            if (location != null && location.latitude != 0.0 && location.longitude != 0.0) {
                isFirstEntry = true
                status++
            } else {
                isFirstEntry = true
            }
        } else {
            isFirstEntry = false
            status++
        }


        val maxGap = 10.0;
        var gap = 0.0;

        if (oldTp != null) {
            gap = location.distanceTo(oldTp!!.location).toDouble()
            if (gap < maxGap) {
                return
            }
        }

        val newTp = TrackPoint(location, Date().time)

        oldTp = newTp

        val loc1 = LocationPoints(location.latitude, location.longitude)
        locationList!!.add(loc1)
        totDistance = calculateTotalDistance()

       // totDistance +=0.1*numPointInt

        //new logic
        lastLat = currentLat
        lastLong = currentLon
        currentLat = location.latitude
        currentLon = location.longitude
    //    if (lastLat > 0 && lastLong > 0)
    //        totDistance += calculatingDist()

        Logger.log(
            LocationService.TAG,
            "DISTANCE_CALC SERVICE CALCULATION DiST====>$totDistance"
        )

        if (dao!!.getSessiondata("tracking") == "on") {
            saveLocation(location)
        }
    }

    fun saveLocation(location: Location) {
        val latitude = location.latitude
        val longitude = location.longitude
        Logger.log(LocationService.TAG, "<<<<<location points got from Tracking Service class >>>>")

        val vitesse: String

        if (location.hasAccuracy() && location.accuracy > 25.0) {
            return
        }

        if (location.hasSpeed()) {
            val format = DecimalFormat.getInstance()
            format.minimumFractionDigits = 0
            format.maximumFractionDigits = 4
            format.isGroupingUsed = false
            vitesse = format.format(location.speed.toDouble())
            SynchroteamUitls.logInFile("Has Speed $vitesse")
        } else {
            vitesse = "0.0"
            SynchroteamUitls.logInFile("no Speed $vitesse")
        }

        dao2?.getUser()?.let {
            if (it != null) {
                dao?.saveCoordinate(it.getId(), vitesse, latitude, longitude)
                numPointInt++
                if (numPointInt >= 100 && SynchroteamUitls.isNetworkAvailable(this)){
                    numPointInt = 0
                    dao?.sync(
                        it.getLogin(), it.getPwd(),
                        dao2?.userDomain, Dao.script
                    )
                }
            }
        }
    }

    companion object {
        const val BROADCAST_ACTION = "updateUI"
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
        val isTracking = MutableLiveData<Boolean>()
        val isCalcDistance = MutableLiveData<Boolean>()
        val lastPoint = MutableLiveData<Location>()
        const val KEY_IS_FIRST_ENTRY = "is_first"
        const val KEY_IS_LAST_ENTRY = "is_last"
        const val KEY_LAT = "stop_lat"
        const val KEY_LON = "stop_lon"
        const val KEY_DIST = "key_dist"
        val TAG = LocationService::class.java.simpleName
    }

    private fun initializeDecimalFormat() {
        numberFormat = DecimalFormat()

        numberFormat?.setMinimumFractionDigits(2)
        numberFormat?.setMaximumFractionDigits(2)
        val sym = DecimalFormatSymbols.getInstance()
        sym.decimalSeparator = '.'
        numberFormat?.setDecimalFormatSymbols(sym)

    }

    fun isGPSLocationAvailable():Boolean {
        return locationClient.isGPSLocationAvailable()
    }

}