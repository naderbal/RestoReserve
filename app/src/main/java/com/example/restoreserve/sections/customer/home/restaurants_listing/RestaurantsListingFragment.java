package com.example.restoreserve.sections.customer.home.restaurants_listing;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.restoreserve.R;
import com.example.restoreserve.base.BaseFragment;
import com.example.restoreserve.data.event.Event;
import com.example.restoreserve.data.event.EventsProvider;
import com.example.restoreserve.data.restaurant.RestaurantProvider;
import com.example.restoreserve.data.restaurant.model.Restaurant;
import com.example.restoreserve.sections.customer.restaurant.RestaurantActivity;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.jakewharton.rxbinding.widget.TextViewTextChangeEvent;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import rx.SingleSubscriber;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 *
 */
public class RestaurantsListingFragment extends BaseFragment {
    private RecyclerView rvContent;
    private RecyclerView rvEvents;
    private RestaurantsAdapter restaurantsAdapter;
    private CustomerEventsAdapter customerEventsAdapter;
    EditText etSearch;
    SwipeRefreshLayout vSwipe;
    ArrayList<String> bannedRestaurants;
    ArrayList<Restaurant> restaurants;

    public static RestaurantsListingFragment newInstance() {
        return new RestaurantsListingFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_restaurants_listing, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        configureEventsListing();
        configureRestaurantsListing();
        fetchRestaurants();
        fetchEvents();
    }

    private void initViews(View view) {
        etSearch = view.findViewById(R.id.etSearch);
        rvContent = view.findViewById(R.id.rvContent);
        rvEvents = view.findViewById(R.id.rvEvents);
        vSwipe = view.findViewById(R.id.vSwipe);
        vSwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchRestaurants();
            }
        });

        RxTextView.textChangeEvents(etSearch)
                .skip(1)
                .debounce(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<TextViewTextChangeEvent>() {
                    @Override
                    public void onCompleted() {}

                    @Override
                    public void onError(Throwable e) {}

                    @Override
                    public void onNext(TextViewTextChangeEvent textViewTextChangeEvent) {
                        applySearch(etSearch.getText().toString());
                    }
                });
    }

    public void applySearch(String newString) {
        if (restaurants == null) return;
        // create new array
        ArrayList<Restaurant> newRestaurants = new ArrayList<>();
        for (int i = 0; i < restaurants.size(); i++) {
            // name
            String name = restaurants.get(i).getName();

            if (name.toLowerCase().startsWith(newString.toLowerCase()) ) {
                newRestaurants.add(restaurants.get(i));
            }
        }
        restaurantsAdapter.replaceRestaurants(newRestaurants);
    }

    private void configureEventsListing() {
        customerEventsAdapter = new CustomerEventsAdapter(getContext(), new CustomerEventsAdapter.OnEventListener() {
            @Override
            public void onEventClicked(Event event) {
                Restaurant restaurant = getREstaurant(event);
                if (restaurant != null) {
                    openRestaurant(restaurant);
                }
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        rvEvents.setLayoutManager(layoutManager);
        rvEvents.setAdapter(customerEventsAdapter);
    }

    private Restaurant getREstaurant(Event event) {
        if (restaurants != null)
        for (Restaurant restaurant : restaurants) {
            if (restaurant.getId().equals(event.getRestoId())) return restaurant;
        }
        return null;
    }

    private void configureRestaurantsListing() {
        restaurantsAdapter = new RestaurantsAdapter(getContext(), new RestaurantsAdapter.OnRestaurantsListener() {
            @Override
            public void onRestaurantClicked(Restaurant restaurant) {
                openRestaurant(restaurant);
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvContent.setLayoutManager(layoutManager);
        rvContent.setAdapter(restaurantsAdapter);
    }

    private void openRestaurant(Restaurant restaurant) {
        Intent intent = new Intent(getContext(), RestaurantActivity.class);
        intent.putExtra(RestaurantActivity.EXTRA_RESTAURANT, restaurant);
        startActivity(intent);
    }

    private void fetchRestaurants() {
        vSwipe.setRefreshing(true);
        RestaurantProvider.rxGetRestos()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleSubscriber<ArrayList<Restaurant>>() {
                    @Override
                    public void onSuccess(ArrayList<Restaurant> ferchedRestaurants) {
                        vSwipe.setRefreshing(false);
                        restaurants = ferchedRestaurants;
                        restaurantsAdapter.addRestaurants(ferchedRestaurants);
                    }

                    @Override
                    public void onError(Throwable error) {
                        vSwipe.setRefreshing(false);
                    }
                });
    }

    public void fetchEvents() {
        EventsProvider.rxGetEvents()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleSubscriber<ArrayList<Event>>() {
                    @Override
                    public void onSuccess(ArrayList<Event> events) {
                        customerEventsAdapter.replaceEvents(events);
                    }

                    @Override
                    public void onError(Throwable error) {

                    }
                });
    }
}
