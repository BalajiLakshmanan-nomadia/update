package com.synchroteam.roomDB.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "NotificationEventModelsTable")
public class NotificationEventModels {
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
    @PrimaryKey(autoGenerate=true)
    public int uid;
    @ColumnInfo(name = "id")
    public String id;
    @ColumnInfo(name = "url")
    public String url;
    @ColumnInfo(name = "value")
    public String value;
}
