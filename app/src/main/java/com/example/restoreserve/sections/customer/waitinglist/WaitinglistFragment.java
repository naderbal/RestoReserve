package com.example.restoreserve.sections.customer.waitinglist;

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
import com.example.restoreserve.data.reservations.ReservationsManager;
import com.example.restoreserve.data.reservations.ReservationsProvider;
import com.example.restoreserve.data.reservations.model.Reservation;

import java.util.ArrayList;

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

    public static WaitinglistFragment newInstance() {
        return new WaitinglistFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reservations, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvContent = view.findViewById(R.id.rvContent);
        configureListing();
        getReservations();
        subscribeToReservations();
    }

    private void subscribeToReservations() {
        ReservationsManager.getInstance().subscribeToTracker(new Subscriber<Integer>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Integer integer) {
                getReservations();
            }
        });
    }

    private void configureListing() {
        adapter = new WaitinglistAdapter(getContext(), new WaitinglistAdapter.OnReservationsListener() {
            @Override
            public void onReservationClicked(Reservation reservation) {
                showReservationAlert(reservation);
            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvContent.setLayoutManager(linearLayoutManager);
        rvContent.setAdapter(adapter);
    }

    protected void showReservationAlert(Reservation reservation) {
        // create dialog
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getContext());
        // configure
        alertBuilder.setTitle("Confirm Reservation");
        alertBuilder.setMessage("Do you want to confirm this reservation?");
        alertBuilder.setCancelable(true);
        alertBuilder.setPositiveButton(
                "Confirm",
                (dialog, id) -> {
                    confirmReservation(reservation);
                });
        alertBuilder.setNegativeButton("Cancel Reservation", ((dialog, id) -> {
            cancelReservation(reservation);
        }));
        // show dialog
        alertBuilder.show();
    }

    private void cancelReservation(Reservation reservation) {
        showProgressDialog("Canceling reservation");
        ReservationsProvider.rxCancelReservation(reservation.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleSubscriber<Boolean>() {
                    @Override
                    public void onSuccess(Boolean aBoolean) {
                        dismissProgressDialog();
                        reservation.setConfirmed(false);
                        adapter.removeReservation(reservation);
                        showToast("Reservation Canceled");
                        // update cache
                        final SortedList<Reservation> reservations = adapter.getReservations();
                        ArrayList<Reservation> reservationArray = convertReservationsToArray(reservations);
                        ReservationsManager.getInstance().replaceReservations(reservationArray);
                    }

                    @Override
                    public void onError(Throwable error) {
                        dismissProgressDialog();
                    }
                });
    }

    private void confirmReservation(Reservation reservation) {
        ReservationsProvider.rxConfirmReservation(reservation.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleSubscriber<Boolean>() {
                    @Override
                    public void onSuccess(Boolean aBoolean) {
                        dismissProgressDialog();
                        adapter.removeReservation(reservation);
                        showToast("Reservation Confirmed");
                        reservation.setConfirmed(true);
                        adapter.notifyDataSetChanged();
                        // update cache
                        final SortedList<Reservation> reservations = adapter.getReservations();
                        ArrayList<Reservation> reservationArray = convertReservationsToArray(reservations);
                        ReservationsManager.getInstance().replaceReservations(reservationArray);
                    }

                    @Override
                    public void onError(Throwable error) {
                        dismissProgressDialog();
                    }
                });
    }

    @NonNull
    private ArrayList<Reservation> convertReservationsToArray(SortedList<Reservation> reservations) {
        ArrayList<Reservation> reservationArray = new ArrayList<>();
        for (int i=0; i< reservations.size();i++) {
            reservationArray.add(reservations.get(i));
        }
        return reservationArray;
    }

    public void getReservations() {
        ArrayList<Reservation> reservations = ReservationsManager.getInstance().getReservations();
        adapter.replaceReservations(reservations);
    }
}
