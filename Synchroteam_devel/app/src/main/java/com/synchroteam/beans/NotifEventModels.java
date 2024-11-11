package com.synchroteam.beans;

import java.io.Serializable;

//import io.realm.RealmObject;
//import io.realm.annotations.PrimaryKey;

/**
 * Created by Trident on 10/31/2017.
 */

public class NotifEventModels implements Serializable {


    private String id;
    private String url;
    private String value;
    private boolean isDelete;

    public boolean isDelete() {
        return isDelete;
    }

    public void setDelete(boolean delete) {
        isDelete = delete;
    }

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

    public NotifEventModels(String id, String url, String value, boolean isDelete) {
        this.id = id;
        this.url = url;
        this.value = value;
        this.isDelete = isDelete;
    }
}
