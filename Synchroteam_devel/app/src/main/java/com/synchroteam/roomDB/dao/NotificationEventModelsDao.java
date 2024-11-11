package com.synchroteam.roomDB.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.synchroteam.roomDB.entity.NotificationEventModels;

import java.util.List;

@Dao
public interface NotificationEventModelsDao {
    @Insert
    void insertAll(NotificationEventModels... notificationEventModels);

    @Query("SELECT * FROM NotificationEventModelsTable")
    List<NotificationEventModels> getAllNotificationEventModels();
    @Query("DELETE FROM NotificationEventModelsTable")
    void deteteAllNotificationEventModels();

    @Query("DELETE FROM NotificationEventModelsTable where id = :userId")
    void deteteIDNotificationEventModels(String userId);

}
