package com.example.restoreserve.data.feedback;

import com.example.restoreserve.data.StorageKeys;
import com.google.firebase.firestore.DocumentSnapshot;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 *
 */
public class Feedback implements Serializable{
    private String id;
    private float rating;
    private String feedbackMessage;
    private String reservationId;


    public Feedback(DocumentSnapshot snapshot) {
        id = snapshot.getId();
        if (snapshot.contains(StorageKeys.RATING)) {
            rating = snapshot.getDouble(StorageKeys.RATING).floatValue();
        }
        if (snapshot.contains(StorageKeys.FEEDBACK_MESSAGE)) {
            feedbackMessage = snapshot.getString(StorageKeys.FEEDBACK_MESSAGE);
        }

        if (snapshot.contains(StorageKeys.RESRVATION_ID)) {
            reservationId = snapshot.getString(StorageKeys.RESRVATION_ID);
        }

    }

    public Feedback(JSONObject jsonObject) {
        try {
            if (!jsonObject.isNull(StorageKeys.ID)) {
                this.id = jsonObject.getString(StorageKeys.ID);
            }
            if (!jsonObject.isNull(StorageKeys.RESRVATION_ID)) {
                this.reservationId = jsonObject.getString(StorageKeys.RESRVATION_ID);
            }
            if (!jsonObject.isNull(StorageKeys.FEEDBACK_MESSAGE)) {
                this.feedbackMessage = jsonObject.getString(StorageKeys.FEEDBACK_MESSAGE);
            }
            if (!jsonObject.isNull(StorageKeys.RATING)) {
                this.rating = (float) jsonObject.getDouble(StorageKeys.RATING);
            }
        } catch (JSONException ignored) {

        }
    }

    public String getId() {
        return id;
    }

    public float getRating() {
        return rating;
    }

    public String getFeedbackMessage() {
        return feedbackMessage;
    }

    public String getReservationId() {
        return reservationId;
    }

    public JSONObject toJSON() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(StorageKeys.ID, id);
            jsonObject.put(StorageKeys.RATING, rating);
            jsonObject.put(StorageKeys.FEEDBACK_MESSAGE, feedbackMessage);
            jsonObject.put(StorageKeys.RESRVATION_ID, reservationId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

}
