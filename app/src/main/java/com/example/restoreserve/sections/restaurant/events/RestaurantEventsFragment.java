package com.example.restoreserve.sections.restaurant.events;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.restoreserve.R;
import com.example.restoreserve.base.BaseFragment;
import com.example.restoreserve.data.event.Event;
import com.example.restoreserve.data.event.EventsProvider;
import com.example.restoreserve.data.restaurant.model.Restaurant;
import com.example.restoreserve.data.session.AppSessionManager;
import com.example.restoreserve.sections.restaurant.reservations.RestaurantReservationsFragment;

import java.util.ArrayList;

import rx.SingleSubscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 *
 */

public class RestaurantEventsFragment extends BaseFragment {
    RecyclerView rvContent;
    Button btnAdd;
    RestaurantEventsAdapter adapter;

    public static RestaurantEventsFragment newInstance() {
        return new RestaurantEventsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_restaurant_events, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        configureListing();
        getEvents();
    }

    private void initViews(View view) {
        rvContent = view.findViewById(R.id.rvContent);
        btnAdd = view.findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(v -> addEvent());
    }

    private void addEvent() {
        final EventDialogFragment dialogFragment = EventDialogFragment.newInstance();
        dialogFragment.setListener(new EventDialogFragment.OnReservationInfoListener() {
            @Override
            public void onAddEventSuccesfull() {
                getEvents();
                dialogFragment.dismiss();
            }
        });
        dialogFragment.show(getActivity().getFragmentManager(), "");
    }

    private void configureListing() {
        adapter = new RestaurantEventsAdapter(getContext(), new RestaurantEventsAdapter.OnEventListener() {

            @Override
            public void onEventClicked(Event event) {
                showAlert("Do you want to delete this event?", () -> deleteEvent(event));
            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvContent.setLayoutManager(linearLayoutManager);
        rvContent.setAdapter(adapter);
    }

    private void deleteEvent(Event event) {
        showProgressDialog("");
        EventsProvider.rxDeleteEvent(event.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleSubscriber<Boolean>() {
                    @Override
                    public void onSuccess(Boolean aBoolean) {
                        dismissProgressDialog();
                        adapter.removeEvent(event);
                    }

                    @Override
                    public void onError(Throwable error) {
                        dismissProgressDialog();
                        showToast("Something went wrong");
                    }
                });
    }

    public void getEvents() {
        Restaurant restaurant = AppSessionManager.getInstance().getRestaurant();
        EventsProvider.rxGetRestaurantEvents(restaurant.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleSubscriber<ArrayList<Event>>() {
                    @Override
                    public void onSuccess(ArrayList<Event> events) {
                        adapter.replaceEvents(events);
                    }

                    @Override
                    public void onError(Throwable error) {
                        dismissProgressDialog();
                    }
                });
    }
}
