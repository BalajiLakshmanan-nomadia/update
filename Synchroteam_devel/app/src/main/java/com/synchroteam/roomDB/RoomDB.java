package com.synchroteam.roomDB;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.synchroteam.roomDB.dao.LocationCoordinatesDao;
import com.synchroteam.roomDB.dao.NotificationEventModelsDao;
import com.synchroteam.roomDB.entity.LocationCoordinates;
import com.synchroteam.roomDB.entity.NotifEventModels;
import com.synchroteam.roomDB.entity.NotificationEventModels;

@Database(entities = {LocationCoordinates.class,
        NotifEventModels.class,
        NotificationEventModels.class
}, version = 1,exportSchema = false)
public abstract class RoomDB extends RoomDatabase {
    public abstract NotificationEventModelsDao roomDao();
    public abstract LocationCoordinatesDao LocationCoordinatesDao();

}