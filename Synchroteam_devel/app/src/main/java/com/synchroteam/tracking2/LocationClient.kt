package com.synchroteam.tracking2

import android.location.Location
import kotlinx.coroutines.flow.Flow

interface LocationClient {
    fun getLocationUpdates(interval: Long): Flow<Location>
    fun isGPSLocationAvailable():Boolean
    class LocationException(message: String): Exception()
}