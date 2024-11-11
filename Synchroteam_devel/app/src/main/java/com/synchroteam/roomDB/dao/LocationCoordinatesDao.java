package com.synchroteam.roomDB.dao;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.synchroteam.roomDB.entity.LocationCoordinates;

import java.util.List;

@Dao
public interface LocationCoordinatesDao {
    @Insert
    void insertAll(LocationCoordinates... locationCoordinates);

    @Query("SELECT * FROM LocationCoordinatesTable")
    List<LocationCoordinates> getAllLocationCoordinatesModels();

    @Query("DELETE FROM LocationCoordinatesTable")
    void deteteAllLocationCoordinatesModels();
}
