package com.synchroteam.roomDB.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "NotifEventModelsTable")
public class NotifEventModels {
    @PrimaryKey(autoGenerate=true)
    public int uid;

    @ColumnInfo(name = "id")
    public String id;
    @ColumnInfo(name = "url")
    public String url;
    @ColumnInfo(name = "value")
    public String value;

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

    public boolean isDelete() {
        return isDelete;
    }

    public void setDelete(boolean delete) {
        isDelete = delete;
    }

    @ColumnInfo(name = "isDelete")
    public boolean isDelete;
}
