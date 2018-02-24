package com.example.restoreserve.data.reservations;

/**
 *
 */

public class ReserveTime {
    String startTime;
    boolean isReserved;

    public ReserveTime(String startTime, boolean isReserved) {
        this.startTime = startTime;
        this.isReserved = isReserved;
    }
}
