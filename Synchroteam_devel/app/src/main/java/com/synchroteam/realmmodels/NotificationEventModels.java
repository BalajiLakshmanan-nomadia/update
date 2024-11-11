package com.synchroteam.realmmodels;
//
//import io.realm.RealmObject;
//import io.realm.annotations.PrimaryKey;

/**
 * Created by Trident on 10/31/2017.
 */

public class NotificationEventModels  {


    private String id;
    private String url;
    private String value;

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
}
