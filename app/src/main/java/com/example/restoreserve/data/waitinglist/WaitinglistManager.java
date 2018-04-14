package com.example.restoreserve.data.waitinglist;

import android.content.Context;

import com.example.restoreserve.RestoReserve;
import com.example.restoreserve.data.caching.SharedPreferencesCache;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import rx.Subscriber;
import rx.subjects.PublishSubject;

/**
 */

public class WaitinglistManager {

    private static WaitinglistManager waitinglistManager;
    private ArrayList<Waitinglist> items;
    private PublishSubject<Integer> waitinglistTracker;

    private WaitinglistManager() {
        init();
    }

    public static WaitinglistManager getInstance() {
        if (waitinglistManager == null) {
            waitinglistManager = new WaitinglistManager();
        }
        return waitinglistManager;
    }

    private void init() {
        Cache cache  = getCache();
        items = cache.getItems();
        waitinglistTracker = PublishSubject.create();
    }

    public int getSize() {
        return items.size();
    }

    public void addWaitinglist(Waitinglist waitinglist) {
        Cache cache  = getCache();
        this.items.add(waitinglist);
        cache.storeItems(items);
        // emit items size
        waitinglistTracker.onNext(getSize());
    }

    public void addWaitinglist(ArrayList<Waitinglist> items) {
        Cache cache  = getCache();
        this.items.clear();
        this.items.addAll(items);
        cache.storeItems(items);
        // emit items size
        waitinglistTracker.onNext(items.size());
    }

    public ArrayList<Waitinglist> getWaitinglist() {
        return items;
    }

    public void subscribeToTracker(Subscriber<Integer> subscriber) {
        waitinglistTracker.subscribe(subscriber);
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
        waitinglistTracker.onNext(getSize());
    }

    public void replaceWaitinglist(ArrayList<Waitinglist> waitinglists) {
        Cache cache  = getCache();
        this.items.clear();
        this.items.addAll(waitinglists);
        cache.storeItems(items);
        // emit items size
        waitinglistTracker.onNext(getSize());
    }

    /**
     */
    private static class Cache extends SharedPreferencesCache {
        private final String PREF_NAME = "waitinglist_manager";
        private final String WAITING_LISTS = "waitinglists";

        private Cache(Context context) {
            super(context);
        }

        @Override
        protected String getName() {
            return PREF_NAME;
        }

        private void storeItems(ArrayList<Waitinglist> items) {
            JSONArray jsonArray = new JSONArray();
            for (Waitinglist item : items) {
                JSONObject jsonObject = item.toJSON();
                jsonArray.put(jsonObject);
            }
            storeString(WAITING_LISTS, jsonArray.toString());
        }

        private ArrayList<Waitinglist> getItems() {
            ArrayList<Waitinglist> items = new ArrayList<>();
            try {
                // get session json from cache
                String strJson = getString(WAITING_LISTS, null);
                if (strJson != null) {
                    JSONArray array = new JSONArray(strJson);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject jsonObject = array.getJSONObject(i);
                        Waitinglist item = new Waitinglist(jsonObject);
                        items.add(item);
                    }

                }
            } catch (JSONException e) {

            }
            return items;
        }
    }
}
