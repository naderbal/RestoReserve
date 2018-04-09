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
    private String eventMessage;

    public Event(DocumentSnapshot snapshot) {
        id = snapshot.getId();
        if (snapshot.contains(StorageKeys.EVENT_MESSAGE)) {
            eventMessage = snapshot.getString(StorageKeys.EVENT_MESSAGE);
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
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

}
