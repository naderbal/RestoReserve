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
import com.example.restoreserve.data.waitinglist.Waitinglist;
import com.example.restoreserve.data.waitinglist.WaitinglistManager;
import com.example.restoreserve.data.waitinglist.WaitinglistProvider;

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
        return inflater.inflate(R.layout.fragment_waitinglist, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvContent = view.findViewById(R.id.rvContent);
        configureListing();
        getWaitinglist();
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
                getWaitinglist();
            }
        });
    }

    private void configureListing() {
        adapter = new WaitinglistAdapter(getContext(), new WaitinglistAdapter.OnWaitingListener() {
            @Override
            public void onReservationClicked(Waitinglist waitinglist) {
                showAlert(waitinglist);
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
                    cancelWaitinglist(waitinglist);
                });
        alertBuilder.setNegativeButton("Cancel", ((dialog, id) -> {
            cancelWaitinglist(waitinglist);
        }));
        // show dialog
        alertBuilder.show();
    }

    private void cancelWaitinglist(Waitinglist waitinglist) {
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
                        ArrayList<Waitinglist> reservationArray = convertReservationsToArray(reservations);
                        WaitinglistManager.getInstance().replaceWaitinglist(reservationArray);
                    }

                    @Override
                    public void onError(Throwable error) {
                        dismissProgressDialog();
                    }
                });
    }

    @NonNull
    private ArrayList<Waitinglist> convertReservationsToArray(SortedList<Waitinglist> waitinglistSortedList) {
        ArrayList<Waitinglist> waitinglistArray = new ArrayList<>();
        for (int i=0; i< waitinglistSortedList.size();i++) {
            waitinglistArray.add(waitinglistSortedList.get(i));
        }
        return waitinglistArray;
    }

    public void getWaitinglist() {
        ArrayList<Waitinglist> waitinglist = WaitinglistManager.getInstance().getWaitinglist();
        adapter.replaceWaitinglist(waitinglist);
    }
}
