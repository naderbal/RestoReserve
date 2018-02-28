package com.example.restoreserve.data.session;

import android.content.Context;
import android.support.annotation.Nullable;

import com.example.restoreserve.RestoReserve;
import com.example.restoreserve.data.caching.SharedPreferencesCache;
import com.example.restoreserve.data.reservations.ReservationsManager;
import com.example.restoreserve.data.restaurant.model.Restaurant;
import com.example.restoreserve.data.user.User;
import com.google.firebase.auth.FirebaseAuth;

import rx.Subscriber;
import rx.subjects.PublishSubject;

/**
 * <p>
 *     Class managing the current logged in session, if any,
 *     with its related data like the user object.
 * </p>
 */
public class AppSessionManager {
    public enum SessionState {
        LOGGED_OUT
    }
    private static AppSessionManager manager;
    // data
    private User user;
    private Restaurant restaurant;
    // tracker
    private PublishSubject<SessionState> sessionTracker;

    private AppSessionManager() {
        sessionTracker = PublishSubject.create();
    }

    public static AppSessionManager getInstance() {
        if (manager == null) {
            manager = new AppSessionManager();
        }
        return manager;
    }

    public void subscribeToTracker(Subscriber<SessionState> subscriber) {
        sessionTracker.subscribe(subscriber);
    }

    /**
     * Returns true if there's a logged in user, false
     * otherwise.
     */
    public boolean isUserLoggedIn() {
        return getCache().getIsCustomer();
    }

    public boolean isRestaurantLoggedIn() {
        return getCache().getIsRestaurant();
    }

    /**
     * Sets the current session user. If the user is not null,
     * the session is active.
     */
    public void setUser(User user) {
        this.user = user;
        getCache().setIsCustomer(true);
        getCache().setIsRestaurant(false);
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
        getCache().setIsRestaurant(true);
        getCache().setIsCustomer(false);
    }

    public void clearCache() {
        getCache().setIsRestaurant(false);
        getCache().setIsCustomer(false);
    }

    /**
     * Returns the current session user, null if there's no
     * logged in user.
     */
    @Nullable
    public User getUser() {
        return user;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void logout() {
        this.user = null;
        this.restaurant = null;
        getCache().setIsRestaurant(false);
        getCache().setIsCustomer(false);
        sessionTracker.onNext(SessionState.LOGGED_OUT);
        FirebaseAuth.getInstance().signOut();
        ReservationsManager.getInstance().clearReservations();
    }

    private Cache getCache() {
        Context context = RestoReserve.getInstance().getApplicationContext();
        return new Cache(context);
    }

    /**
     * Class managing the storage of trellis session data.
     */
    private static class Cache extends SharedPreferencesCache {
        private final String PREF_NAME = "session_manager";
        private final String IS_CUSTOMER = "is_customer";
        private final String IS_RESTAURANT = "is_restaurant";

        private Cache(Context context) {
            super(context);
        }

        @Override
        protected String getName() {
            return PREF_NAME;
        }

        private void setIsCustomer(boolean isCustomer) {
            storeBoolean(IS_CUSTOMER, isCustomer);
        }

        private Boolean getIsCustomer() {
           return getBoolean(IS_CUSTOMER, true);
        }

        private Boolean getIsRestaurant() {
           return getBoolean(IS_RESTAURANT, true);
        }

        private void setIsRestaurant(boolean isRestaurant) {
            storeBoolean(IS_RESTAURANT, isRestaurant);
        }
    }
}
