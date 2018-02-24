package com.example.restoreserve;

import android.app.Application;

/**
 *
 */

public class RestoReserve extends Application {

    private static RestoReserve instance;

    public static RestoReserve getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }
}
