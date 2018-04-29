package com.example.restoreserve.sections.customer.waitinglist;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.util.SortedList;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.restoreserve.R;
import com.example.restoreserve.base.BaseFragment;
import com.example.restoreserve.data.reservations.ReservationsProvider;
import com.example.restoreserve.data.reservations.model.Reservation;
import com.example.restoreserve.data.reservations.model.ReservedTable;
import com.example.restoreserve.data.reservations.model.Table;
import com.example.restoreserve.data.restaurant.RestaurantProvider;
import com.example.restoreserve.data.restaurant.model.Restaurant;
import com.example.restoreserve.data.waitinglist.Waitinglist;
import com.example.restoreserve.data.waitinglist.WaitinglistManager;
import com.example.restoreserve.data.waitinglist.WaitinglistProvider;
import com.example.restoreserve.sections.customer.restaurant.RestaurantActivity;
import com.example.restoreserve.utils.DateHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import rx.SingleSubscriber;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 *
 */
public class WaitinglistFragment extends BaseFragment {

    private RecyclerView rvContent;
    private WaitinglistAdapter adapter;
    private ArrayList<Restaurant> restaurants;

    public static WaitinglistFragment newInstance() {
        return new WaitinglistFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_waitinglist, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvContent = view.findViewById(R.id.rvContent);
        configureListing();
        fetchRestaurants();
        getWaitinglist();
        subscribeToReservations();
    }

    public void setRestaurants(ArrayList<Restaurant> restaurants) {
        this.restaurants = restaurants;
        ArrayList<Waitinglist> arrayWaitinglist = WaitinglistManager.getInstance().getWaitinglist();
        for (Waitinglist waitinglist : arrayWaitinglist) {
            final Restaurant restaurant = getRestaurant(waitinglist.getRestoId());
            if (restaurant != null) {
                fetchReservations(restaurant, waitinglist);
            }
        }
    }

    private void subscribeToReservations() {
        WaitinglistManager.getInstance().subscribeToTracker(new Subscriber<Integer>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Integer integer) {
                getWaitinglist();
            }
        });
    }

    private void configureListing() {
        adapter = new WaitinglistAdapter(getContext(), new WaitinglistAdapter.OnWaitingListener() {
            @Override
            public void onWaitinglistClicked(Waitinglist waitinglist, boolean available) {
                if (available) {
                    final Restaurant restaurant = getRestaurant(waitinglist.getRestoId());
                    if (restaurant != null) {
                        Intent intent = new Intent(getActivity(), RestaurantActivity.class);
                        intent.putExtra(RestaurantActivity.EXTRA_RESTAURANT, restaurant);
                        startActivity(intent);
                    }
                } else {
                    showAlert(waitinglist);
                }
            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvContent.setLayoutManager(linearLayoutManager);
        rvContent.setAdapter(adapter);
    }

    protected void showAlert(Waitinglist waitinglist) {
        // create dialog
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getContext());
        // configure
        alertBuilder.setTitle("Remove from waiting list");
        alertBuilder.setCancelable(true);
        alertBuilder.setPositiveButton(
                "Confirm",
                (dialog, id) -> {
                    cancelWatchlist(waitinglist);
                });
        alertBuilder.setNegativeButton("Cancel", ((dialog, id) -> {
            // cancel
        }));
        // show dialog
        alertBuilder.show();
    }

    private void cancelWatchlist(Waitinglist waitinglist) {
        showProgressDialog("Canceling");
        WaitinglistProvider.rxCancelWaitinglist(waitinglist.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleSubscriber<Boolean>() {
                    @Override
                    public void onSuccess(Boolean aBoolean) {
                        dismissProgressDialog();
                        adapter.removeReservation(waitinglist);
                        showToast("Canceled");
                        // update cache
                        final SortedList<Waitinglist> reservations = adapter.getWaitlists();
                        ArrayList<Waitinglist> reservationArray = convertWaitinglistToArray(reservations);
                        WaitinglistManager.getInstance().replaceWaitinglist(reservationArray);
                    }

                    @Override
                    public void onError(Throwable error) {
                        dismissProgressDialog();
                    }
                });
    }

    @NonNull
    private ArrayList<Waitinglist> convertWaitinglistToArray(SortedList<Waitinglist> waitinglistSortedList) {
        ArrayList<Waitinglist> waitinglistArray = new ArrayList<>();
        for (int i = 0; i< waitinglistSortedList.size(); i++) {
            waitinglistArray.add(waitinglistSortedList.get(i));
        }
        return waitinglistArray;
    }

    public void getWaitinglist() {
        ArrayList<Waitinglist> arrayWaitinglist = WaitinglistManager.getInstance().getWaitinglist();
        adapter.replaceWaitinglist(arrayWaitinglist);
    }

    private Restaurant getRestaurant(String restoId) {
        if (restaurants != null) {
            for (Restaurant restaurant : restaurants) {
                if (restaurant.getId().equals(restoId)) return restaurant;
            }
        }
        return null;
    }

    private void fetchReservations(Restaurant restaurant, Waitinglist waitinglist) {
        ReservationsProvider.rxGetReservationsAtDate(restaurant.getId(), waitinglist.getDate())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleSubscriber<ArrayList<Reservation>>() {
                    @Override
                    public void onSuccess(ArrayList<Reservation> reservations) {
                        handleReservationsReceived(restaurant, reservations, waitinglist);
                    }

                    @Override
                    public void onError(Throwable error) {
                    }
                });
    }

    private void handleReservationsReceived(Restaurant restaurant, ArrayList<Reservation> reservations, Waitinglist waitinglist) {
        ArrayList<ReservedTable> reservedTables = getAllTablesAvailableAtTime(restaurant, reservations, waitinglist.getTime());
        if (reservedTables != null) {
            checkAllReserved(reservedTables, waitinglist);
        }
    }

    private void checkAllReserved(ArrayList<ReservedTable> reservedTables, Waitinglist waitinglist) {
        for (ReservedTable reservedTable : reservedTables) {
            if (!reservedTable.isReserved()) {
                waitinglist.setAvailable(true);
                adapter.updateWaitingList(waitinglist);
                return;
            }
        }
    }

    private ArrayList<ReservedTable> getAllTablesAvailableAtTime(Restaurant restaurant, ArrayList<Reservation> reservations, String timeToBeReserved) {
        // generate of list of reserved tables of restaurant's tables initially not reserved
        ArrayList<ReservedTable> reservedTables = generateReservedTables(restaurant.getTables());
        if (reservedTables != null) {
            // loop over all reservation
            for (ReservedTable reservedTable : reservedTables) {
                // get id of current table
                final String id = reservedTable.getTable().getId();
                // loop over all restaurant reservedTables
                for (Reservation reservation: reservations) {
                    String reservationTime = reservation.getTime();
                    String reservedTableId= reservation.getTableId();
                    // check if it is equal to current reservations table id
                    if (id.equals(reservedTableId)) {
                        // parse reservation time to date
                        final Date timeReservationDate = DateHelper.parseTime(reservationTime);
                        // parse time to be reserved to date
                        final Date timeToBeReservedDate = DateHelper.parseTime(timeToBeReserved);
                        // parse 6 pm date
                        final Date date6pm = DateHelper.parseTime("6:00 PM");
                        // check time to be reserved less than 6:00 pm
                        if (timeToBeReservedDate!= null && timeToBeReservedDate.before(date6pm)) {
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime(timeToBeReservedDate);
                            calendar.add(Calendar.HOUR_OF_DAY, 3);
                            final Date timeToBeReservedAfter3Hours = calendar.getTime();
                            if (timeToBeReservedAfter3Hours.after(timeReservationDate)) {
                                reservedTable.setReserved();
                                reservedTable.setReservedTime(reservationTime);
                            } else {
                                // do nothing already not reserved
                            }
                        } else { // time to be reserved after 6 pm
                            if (timeReservationDate!= null && timeReservationDate.after(date6pm)) {
                                reservedTable.setReserved();
                                reservedTable.setReservedTime(reservationTime);
                            } else {
                                // remove 3 hours and check if time to reserved is still after reserved time
                                Calendar calendar = Calendar.getInstance();
                                calendar.setTime(timeToBeReservedDate);
                                calendar.add(Calendar.HOUR_OF_DAY, -3);
                                final Date timeToBeReservedBefore3Hours = calendar.getTime();
                                if (timeToBeReservedBefore3Hours.before(timeReservationDate)) {
                                    reservedTable.setReserved();
                                    reservedTable.setReservedTime(reservationTime);
                                } else {
                                    // do nothing already not reserved
                                }
                            }
                        }
                    }
                }
            }
        }
        return reservedTables;
    }

    private ArrayList<ReservedTable> generateReservedTables(ArrayList<Table> tables) {
        ArrayList<ReservedTable> reservedTables = new ArrayList<>();
        for (Table table : tables) {
            ReservedTable reservedTable = new ReservedTable(table, "", true);
            reservedTables.add(reservedTable);
        }
        return reservedTables;
    }

    private void fetchRestaurants() {
        RestaurantProvider.rxGetRestos()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleSubscriber<ArrayList<Restaurant>>() {
                    @Override
                    public void onSuccess(ArrayList<Restaurant> ferchedRestaurants) {
                        setRestaurants(ferchedRestaurants);
                    }

                    @Override
                    public void onError(Throwable error) {
                    }
                });
    }

}
