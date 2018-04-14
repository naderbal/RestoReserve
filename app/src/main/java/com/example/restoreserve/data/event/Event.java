package com.example.restoreserve.data.event;

import com.example.restoreserve.data.StorageKeys;
import com.google.firebase.firestore.DocumentSnapshot;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 *
 */
public class Event implements Serializable{
    private String id;
    private String restoId;
    private String restoName;
    private String eventMessage;

    public Event(String restoId, String restoName, String eventMessage) {
        this.restoId = restoId;
        this.restoName = restoName;
        this.eventMessage = eventMessage;
    }

    public Event(DocumentSnapshot snapshot) {
        id = snapshot.getId();
        if (snapshot.contains(StorageKeys.EVENT_MESSAGE)) {
            eventMessage = snapshot.getString(StorageKeys.EVENT_MESSAGE);
        }
        if (snapshot.contains(StorageKeys.RESTO_ID)) {
            restoId = snapshot.getString(StorageKeys.RESTO_ID);
        }
        if (snapshot.contains(StorageKeys.RESTO_NAME)) {
            restoName = snapshot.getString(StorageKeys.RESTO_NAME);
        }
    }

    public Event(JSONObject jsonObject) {
        try {
            if (!jsonObject.isNull(StorageKeys.ID)) {
                this.id = jsonObject.getString(StorageKeys.ID);
            }
            if (!jsonObject.isNull(StorageKeys.EVENT_MESSAGE)) {
                this.eventMessage = jsonObject.getString(StorageKeys.EVENT_MESSAGE);
            }
        } catch (JSONException ignored) {

        }
    }

    public String getRestoId() {
        return restoId;
    }

    public String getRestoName() {
        return restoName;
    }

    public String getId() {
        return id;
    }

    public String getEventMessage() {
        return eventMessage;
    }

    public JSONObject toJSON() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(StorageKeys.ID, id);
            jsonObject.put(StorageKeys.EVENT_MESSAGE, eventMessage);
            jsonObject.put(StorageKeys.RESTO_ID, restoId);
            jsonObject.put(StorageKeys.RESTO_NAME, restoName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

}
