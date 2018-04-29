package com.example.restoreserve.data.waitinglist;

import com.example.restoreserve.data.StorageKeys;
import com.google.firebase.firestore.DocumentSnapshot;

import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 */

public class Waitinglist {
    private String id;
    private String customerId;
    private String restoId;
    private String restoName;
    private String date;
    private String time;
    private boolean available;

    public Waitinglist(String customerId, String restoId, String restoName, String date, String time) {
        this.customerId = customerId;
        this.restoId = restoId;
        this.restoName = restoName;
        this.date = date;
        this.time = time;
    }

    public Waitinglist(DocumentSnapshot snapshot) {
        id = snapshot.getId();
        if (snapshot.contains(StorageKeys.CUSTOMER_ID)) {
            customerId = snapshot.getString(StorageKeys.CUSTOMER_ID);
        }
        if (snapshot.contains(StorageKeys.RESTO_ID)) {
            restoId = snapshot.getString(StorageKeys.RESTO_ID);
        }
        if (snapshot.contains(StorageKeys.RESTO_NAME)) {
            restoName = snapshot.getString(StorageKeys.RESTO_NAME);
        }
        if (snapshot.contains(StorageKeys.DATE)) {
            date = snapshot.getString(StorageKeys.DATE);
        }
        if (snapshot.contains(StorageKeys.TIME)) {
            time = snapshot.getString(StorageKeys.TIME);
        }
    }

    public Waitinglist(String id, Waitinglist waitinglist) {
        this.id = id;
        this.customerId = waitinglist.customerId;
        this.restoId = waitinglist.restoId;
        this.restoName = waitinglist.restoName;
        this.date = waitinglist.date;
        this.time = waitinglist.time;
    }

    public Waitinglist(JSONObject jsonObject) {
        try {
            if(!jsonObject.isNull(StorageKeys.ID)) {
                id = jsonObject.getString(StorageKeys.ID);
            }
            if(!jsonObject.isNull(StorageKeys.CUSTOMER_ID)) {
                customerId = jsonObject.getString(StorageKeys.CUSTOMER_ID);
            }
            if(!jsonObject.isNull(StorageKeys.RESTO_ID)) {
                restoId = jsonObject.getString(StorageKeys.RESTO_ID);
            }
            if(!jsonObject.isNull(StorageKeys.RESTO_NAME)) {
                restoName = jsonObject.getString(StorageKeys.RESTO_NAME);
            }
            if(!jsonObject.isNull(StorageKeys.DATE)) {
                date = jsonObject.getString(StorageKeys.DATE);
            }
            if(!jsonObject.isNull(StorageKeys.TIME)) {
                time = jsonObject.getString(StorageKeys.TIME);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public JSONObject toJSON() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(StorageKeys.ID, id);
            jsonObject.put(StorageKeys.RESTO_ID, restoId);
            jsonObject.put(StorageKeys.RESTO_NAME, restoName);
            jsonObject.put(StorageKeys.CUSTOMER_ID, customerId);
            jsonObject.put(StorageKeys.DATE, date);
            jsonObject.put(StorageKeys.TIME, time);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public String getId() {
        return id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getRestoId() {
        return restoId;
    }

    public String getRestoName() {
        return restoName;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public boolean isAvailable() {
        return available;
    }
}
