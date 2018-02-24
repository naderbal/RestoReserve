package com.example.restoreserve.data.reservations.model;

import com.example.restoreserve.data.StorageKeys;

import java.io.Serializable;
import java.util.HashMap;

/**
 *
 */

public class Table implements Serializable {
    String id;
    long seatsCount;

    public Table() {
    }

    public Table(String id, long seatsCount) {
        this.id = id;
        this.seatsCount = seatsCount;
    }

    public Table(HashMap<String, Object> tableMap) {
        if (tableMap.containsKey(StorageKeys.ID)) {
            this.id = (String) tableMap.get(StorageKeys.ID);
        }
        if (tableMap.containsKey(StorageKeys.COUNT)) {
            this.seatsCount = (long) tableMap.get(StorageKeys.COUNT);
        }
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getSeatsCount() {
        return seatsCount;
    }

    public String getId() {
        return id;
    }
}
