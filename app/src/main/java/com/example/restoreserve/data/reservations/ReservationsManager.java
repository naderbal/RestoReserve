package com.example.restoreserve.data.reservations;

import android.content.Context;
import android.support.v7.util.SortedList;

import com.example.restoreserve.RestoReserve;
import com.example.restoreserve.data.caching.SharedPreferencesCache;
import com.example.restoreserve.data.reservations.model.Reservation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import rx.Subscriber;
import rx.subjects.PublishSubject;
import rx.subjects.ReplaySubject;

/**
 * Created by naderbaltaji on 12/5/17.
 */

public class ReservationsManager {

    private static ReservationsManager cartManager;
    private ArrayList<Reservation> items;
    private PublishSubject<Integer> reservationsTracker;

    private ReservationsManager() {
        init();
    }

    public static ReservationsManager getInstance() {
        if (cartManager == null) {
            cartManager = new ReservationsManager();
        }
        return cartManager;
    }

    private void init() {
        Cache cache  = getCache();
        items = cache.getItems();
        reservationsTracker = PublishSubject.create();
        // emit items size
        reservationsTracker.onNext(items.size());
    }

    public void addReservation(Reservation reservation) {
        Cache cache  = getCache();
        this.items.add(reservation);
        cache.storeItems(items);
        // emit items size
        reservationsTracker.onNext(items.size());
    }

    public void addReservations(ArrayList<Reservation> items) {
        Cache cache  = getCache();
        this.items.clear();
        this.items.addAll(items);
        cache.storeItems(items);
        // emit items size
        reservationsTracker.onNext(items.size());
    }

    public ArrayList<Reservation> getReservations() {
        return items;
    }

    public void subscribeToTracker(Subscriber<Integer> subscriber) {
        reservationsTracker.subscribe(subscriber);
    }

    /**
     * Returns a new instance of the cache.
     */
    private static Cache getCache() {
        Context context = RestoReserve.getInstance().getApplicationContext();
        return new Cache(context);
    }

    public void clearReservations() {
        items.clear();
        getCache().storeItems(items);
        reservationsTracker.onNext(items.size());
    }

    public void replaceReservations(ArrayList<Reservation> reservations) {
        Cache cache  = getCache();
        this.items.clear();
        this.items.addAll(reservations);
        cache.storeItems(items);
        // emit items size
        reservationsTracker.onNext(items.size());
    }

    /**
     * Class managing the storage of trellis session data.
     */
    private static class Cache extends SharedPreferencesCache {
        private final String PREF_NAME = "reservations_manager";
        private final String RESERVATIONS = "reservations";

        private Cache(Context context) {
            super(context);
        }

        @Override
        protected String getName() {
            return PREF_NAME;
        }

        private void storeItems(ArrayList<Reservation> items) {
            JSONArray jsonArray = new JSONArray();
            for (Reservation item : items) {
                JSONObject jsonObject = item.toJSON();
                jsonArray.put(jsonObject);
            }
            storeString(RESERVATIONS, jsonArray.toString());
        }

        private ArrayList<Reservation> getItems() {
            ArrayList<Reservation> items = new ArrayList<>();
            try {
                // get session json from cache
                String strJson = getString(RESERVATIONS, null);
                if (strJson != null) {
                    JSONArray array = new JSONArray(strJson);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject jsonObject = array.getJSONObject(i);
                        Reservation item = new Reservation(jsonObject);
                        items.add(item);
                    }
                    // create session object

                }
            } catch (JSONException e) {

            }
            return items;
        }
    }
}
