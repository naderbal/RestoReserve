package com.example.restoreserve.data.reservations.model;

import java.io.Serializable;

/**
 *
 */

public class ReservedTable implements Serializable {

    Table table;
    String time;
    boolean isReserved;
    private String reservedTime;

    public ReservedTable(Table table, String time, boolean isReserved) {
        this.table = table;
        this.time = time;
    }

    public Table getTable() {
        return table;
    }

    public String getTime() {
        return time;
    }

    public boolean isReserved() {
        return isReserved;
    }

    public void setReserved() {
        isReserved = true;
    }

    public void setReservedTime(String reservedTime) {
        this.reservedTime = reservedTime;
    }
}
