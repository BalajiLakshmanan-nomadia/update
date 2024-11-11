package com.synchroteam.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Trident on 2/5/17.
 */

public class TrackingPrefList {

    public static final String PREFS_NAME = "DEBUGTRACKING";
    public static final String FAVORITES = "tracking";

    public TrackingPrefList() {
        super();
    }

    /**
     * used for store arrayList in json format
     *
     * @param context
     * @param tracking
     */
    public void storeTrackingList(Context context, List tracking) {
        SharedPreferences settings;
        SharedPreferences.Editor editor;
        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = settings.edit();
        Gson gson = new Gson();
        String jsonFavorites = gson.toJson(tracking);
        editor.putString(FAVORITES, jsonFavorites);
        editor.commit();
    }

    /**
     * used for retrieving array list from json formatted string
     *
     * @param context
     * @return
     */
    public ArrayList loadTrackingList(Context context) {
        SharedPreferences settings;
        List trackingList;
        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        if (settings.contains(FAVORITES)) {
            String jsonFavorites = settings.getString(FAVORITES, null);
            Gson gson = new Gson();
            TrackingModel[] favoriteItems = gson.fromJson(jsonFavorites, TrackingModel[].class);
            trackingList = Arrays.asList(favoriteItems);
            trackingList = new ArrayList(trackingList);
        } else
            return null;
        return (ArrayList) trackingList;
    }

    /**
     * Adding single data to the list
     *
     * @param context
     * @param model
     */
    public void addModel(Context context, TrackingModel model) {
        List trackingList = loadTrackingList(context);
        if (trackingList == null)
            trackingList = new ArrayList();
        trackingList.add(model);
        storeTrackingList(context, trackingList);
    }

    public void removeModel(Context context, TrackingModel model) {
        ArrayList favorites = loadTrackingList(context);
        if (favorites != null) {
            favorites.remove(model);
            storeTrackingList(context, favorites);
        }
    }

    /**
     * Clear all data in list
     *
     * @param context
     */
    public void clearTrackingList(Context context) {
        ArrayList favorites = loadTrackingList(context);
        if (favorites != null) {
            favorites.clear();
            storeTrackingList(context, favorites);
        }
    }


    public static class TrackingModel {
        private String trackingDateTime;
        private int trackingUserId;
        private Double trackingLatitude;
        private Double trackingLongitude;

        public String getTrackingDateTime() {
            return trackingDateTime;
        }

        public void setTrackingDateTime(String trackingDateTime) {
            this.trackingDateTime = trackingDateTime;
        }

        public int getTrackingUserId() {
            return trackingUserId;
        }

        public void setTrackingUserId(int trackingUserId) {
            this.trackingUserId = trackingUserId;
        }

        public Double getTrackingLatitude() {
            return trackingLatitude;
        }

        public void setTrackingLatitude(Double trackingLatitude) {
            this.trackingLatitude = trackingLatitude;
        }

        public Double getTrackingLongitude() {
            return trackingLongitude;
        }

        public void setTrackingLongitude(Double trackingLongitude) {
            this.trackingLongitude = trackingLongitude;
        }
    }

}