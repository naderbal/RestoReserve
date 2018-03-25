package com.example.restoreserve.data.reservations.model;

import com.example.restoreserve.data.StorageKeys;
import com.google.firebase.firestore.DocumentSnapshot;

import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 */
public class Reservation {
    private String id;
    private String restoId;
    private String restoName;
    private String customerId;
    private String customerName;
    private String customerPhonenumber;
    private String date;
    private String time;
    private String tableId;
    private boolean isConfirmed;

    public Reservation(String restoId,
                       String restoName,
                       String customerId,
                       String customerName,
                       String customerPhonenumber,
                       String date, String time, String tableId, boolean isConfirmed) {
        this.restoId = restoId;
        this.restoName = restoName;
        this.customerId = customerId;
        this.customerName = customerName;
        this.customerPhonenumber = customerPhonenumber;
        this.date = date;
        this.time = time;
        this.tableId = tableId;
        this.isConfirmed = isConfirmed;
    }

    public Reservation(String id, Reservation reservation) {
        this.id = id;
        this.restoId = reservation.restoId;
        this.restoName = reservation.restoName;
        this.customerId = reservation.customerId;
        this.customerName = reservation.customerName;
        this.customerPhonenumber = reservation.customerPhonenumber;
        this.date = reservation.date;
        this.time = reservation.time;
        this.tableId = reservation.tableId;
    }

    public Reservation(DocumentSnapshot snapshot) {
        id = snapshot.getId();
        if (snapshot.contains(StorageKeys.RESTO_ID)) {
            restoId = snapshot.getString(StorageKeys.RESTO_ID);
        }
        if (snapshot.contains(StorageKeys.RESTO_NAME)) {
            restoName = snapshot.getString(StorageKeys.RESTO_NAME);
        }
        if (snapshot.contains(StorageKeys.CUSTOMER_ID)) {
            customerId = snapshot.getString(StorageKeys.CUSTOMER_ID);
        }
        if (snapshot.contains(StorageKeys.CUSTOMER_NAME)) {
            customerName = snapshot.getString(StorageKeys.CUSTOMER_NAME);
        }
        if (snapshot.contains(StorageKeys.CUSTOMER_PHONE_NUMBER)) {
            customerPhonenumber = snapshot.getString(StorageKeys.CUSTOMER_PHONE_NUMBER);
        }
        if (snapshot.contains(StorageKeys.DATE)) {
            date = snapshot.getString(StorageKeys.DATE);
        }
        if (snapshot.contains(StorageKeys.TIME)) {
            time = snapshot.getString(StorageKeys.TIME);
        }
        if (snapshot.contains(StorageKeys.TABLE_ID)) {
            tableId = snapshot.getString(StorageKeys.TABLE_ID);
        }
        if (snapshot.contains(StorageKeys.IS_CONFIRMED)) {
            isConfirmed = snapshot.getBoolean(StorageKeys.IS_CONFIRMED);
        }
    }

    public Reservation(JSONObject jsonObject) {
        try {
            if (!jsonObject.isNull(StorageKeys.ID)) {
                this.id = jsonObject.getString(StorageKeys.ID);
            }
            if (!jsonObject.isNull(StorageKeys.RESTO_ID)) {
                this.restoId = jsonObject.getString(StorageKeys.RESTO_ID);
            }
            if (!jsonObject.isNull(StorageKeys.RESTO_NAME)) {
                this.restoName = jsonObject.getString(StorageKeys.RESTO_NAME);
            }
            if (!jsonObject.isNull(StorageKeys.CUSTOMER_ID)) {
                this.customerId = jsonObject.getString(StorageKeys.CUSTOMER_ID);
            }
            if (!jsonObject.isNull(StorageKeys.CUSTOMER_NAME)) {
                this.customerName = jsonObject.getString(StorageKeys.CUSTOMER_NAME);
            }
            if (!jsonObject.isNull(StorageKeys.CUSTOMER_PHONE_NUMBER)) {
                this.customerPhonenumber = jsonObject.getString(StorageKeys.CUSTOMER_PHONE_NUMBER);
            }
            if (!jsonObject.isNull(StorageKeys.DATE)) {
                this.date = jsonObject.getString(StorageKeys.DATE);
            }
            if (!jsonObject.isNull(StorageKeys.TIME)) {
                this.time = jsonObject.getString(StorageKeys.TIME);
            }
            if (!jsonObject.isNull(StorageKeys.TABLE_ID)) {
                this.tableId = jsonObject.getString(StorageKeys.TABLE_ID);
            }
            if (!jsonObject.isNull(StorageKeys.IS_CONFIRMED)) {
                this.isConfirmed = jsonObject.getBoolean(StorageKeys.IS_CONFIRMED);
            }
        } catch (JSONException ignored) {

        }
    }

    public String getId() {
        return id;
    }

    public String getRestoId() {
        return restoId;
    }

    public String getRestoName() {
        return restoName;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getTableId() {
        return tableId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getCustomerPhonenumber() {
        return customerPhonenumber;
    }

    public boolean isConfirmed() {
        return isConfirmed;
    }

    public void setConfirmed(boolean confirmed) {
        isConfirmed = confirmed;
    }

    public JSONObject toJSON() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(StorageKeys.ID, id);
            jsonObject.put(StorageKeys.RESTO_ID, restoId);
            jsonObject.put(StorageKeys.RESTO_NAME, restoName);
            jsonObject.put(StorageKeys.CUSTOMER_ID, customerId);
            jsonObject.put(StorageKeys.CUSTOMER_NAME, customerName);
            jsonObject.put(StorageKeys.CUSTOMER_PHONE_NUMBER, customerPhonenumber);
            jsonObject.put(StorageKeys.DATE, date);
            jsonObject.put(StorageKeys.TIME, time);
            jsonObject.put(StorageKeys.TABLE_ID, tableId);
            jsonObject.put(StorageKeys.IS_CONFIRMED, isConfirmed);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
}
