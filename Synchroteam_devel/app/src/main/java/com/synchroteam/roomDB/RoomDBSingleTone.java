package com.synchroteam.roomDB;


import androidx.room.Room;

import android.content.Context;


public class RoomDBSingleTone {
    public static RoomDB db = null;

    public static RoomDB instance(Context context) {
        if (db == null) {
            db = Room.databaseBuilder(context, RoomDB.class, "synchroteam.realm").build();
            return db;
        }
        return db;
    }

}
