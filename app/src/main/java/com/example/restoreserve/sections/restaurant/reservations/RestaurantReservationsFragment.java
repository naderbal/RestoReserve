package com.example.restoreserve.sections.restaurant.reservations;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.util.SortedList;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.restoreserve.R;
import com.example.restoreserve.base.BaseFragment;
import com.example.restoreserve.data.reservations.ReservationsManager;
import com.example.restoreserve.data.reservations.ReservationsProvider;
import com.example.restoreserve.data.reservations.model.Reservation;
import com.example.restoreserve.data.restaurant.model.Restaurant;
import com.example.restoreserve.data.session.AppSessionManager;
import com.example.restoreserve.utils.DateHelper;
import com.example.restoreserve.utils.ui.CustomInputSelectorView;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import rx.SingleSubscriber;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 *
 */
public class RestaurantReservationsFragment extends BaseFragment {
    private SwipeRefreshLayout vSwipe;
    private RecyclerView rvContent;
    Button btnFilter;
    private RestaurantReservationsAdapter adapter;
    CustomInputSelectorView vStartDate, vEndDate;
    ArrayList<Reservation> reservations = new ArrayList<>();

    public static RestaurantReservationsFragment newInstance() {
        return new RestaurantReservationsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_restaurant_reservations, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        configureListing();
        getReservations();
    }

    private void initViews(View view) {
        rvContent = view.findViewById(R.id.rvContent);
        vStartDate = view.findViewById(R.id.vStartDate);
        vEndDate = view.findViewById(R.id.vEndDate);
        btnFilter = view.findViewById(R.id.btnFilter);
        vSwipe = view.findViewById(R.id.vSwipe);
        //
        vSwipe.setOnRefreshListener(() -> vSwipe.setRefreshing(false));
        vStartDate.setOnClickListener(v -> openStartDate());
        vEndDate.setOnClickListener(v -> openEndDate());
        btnFilter.setOnClickListener(v -> filterReservations());
    }

    private void filterReservations() {
        if (reservations.isEmpty()) return;

        final String vStartDateValue = vStartDate.getValue();
        final String vEndDateValue = vEndDate.getValue();

        if (vStartDateValue.equals(CustomInputSelectorView.DEFAULT_VALUE)) {
            showToast("Please set start date");
            return;
        }

        if (vEndDateValue.equals(CustomInputSelectorView.DEFAULT_VALUE)) {
            showToast("Please set end date");
            return;
        }

        final Date startDate = DateHelper.parseApiDate(vStartDateValue);
        final Date endDate = DateHelper.parseApiDate(vEndDateValue);

        ArrayList<Reservation> filtered = new ArrayList<>();

        for (Reservation reservation : reservations) {
            final Date resDate = DateHelper.parseApiDate(reservation.getDate());
            if (resDate == null) continue;
            if (resDate.equals(startDate) || resDate.equals(endDate) || ((resDate.after(startDate) && resDate.before(endDate)))) {
                filtered.add(reservation);
            }
        }
        adapter.replaceReservations(filtered);
    }

    private void openStartDate() {
        Calendar cal = Calendar.getInstance();
        // initialize date picker
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                (view, year, monthOfYear, dayOfMonth) -> {
                    // create new calendar instance
                    Calendar selectedCalendar = new GregorianCalendar();
                    // set date selected to calendar
                    selectedCalendar.set(Calendar.YEAR, year);
                    selectedCalendar.set(Calendar.MONTH, monthOfYear);
                    selectedCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    // create api date formatter
                    DateFormat apiFormat = new SimpleDateFormat(DateHelper.PATTERN_API_DATE, Locale.ENGLISH);
                    // get formatted date from selected date
                    String formattedDate = apiFormat.format(selectedCalendar.getTime());
                    vStartDate.updateValue(formattedDate);
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
        );
        dpd.show(getActivity().getFragmentManager(), "");
    }

    private void openEndDate() {
        if (vStartDate.getValue().equals(CustomInputSelectorView.DEFAULT_VALUE)) {
            showToast("Please set start date");
            return;
        }
        Calendar cal = Calendar.getInstance();
        // initialize date picker
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                (view, year, monthOfYear, dayOfMonth) -> {
                    // create new calendar instance
                    Calendar selectedCalendar = new GregorianCalendar();
                    // set date selected to calendar
                    selectedCalendar.set(Calendar.YEAR, year);
                    selectedCalendar.set(Calendar.MONTH, monthOfYear);
                    selectedCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    // create api date formatter
                    DateFormat apiFormat = new SimpleDateFormat(DateHelper.PATTERN_API_DATE, Locale.ENGLISH);
                    // get formatted date from selected date
                    String formattedDate = apiFormat.format(selectedCalendar.getTime());
                    vEndDate.updateValue(formattedDate);
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
        );
        Calendar calendar = Calendar.getInstance();
        Date date = DateHelper.parseApiDate(vStartDate.getValue());
        calendar.setTime(date);
        dpd.setMinDate(calendar);
        dpd.show(getActivity().getFragmentManager(), "");
    }

    private void configureListing() {
        adapter = new RestaurantReservationsAdapter(getContext(), new RestaurantReservationsAdapter.OnReservationsListener() {
            @Override
            public void onReservationClicked(Reservation reservation) {
                if  (reservation.isConfirmed()) {
                    showReservationDeletionAlert(reservation);
                } else {
                    showReservationAlert(reservation);
                }
            }

            @Override
            public void onFeedbackClicked(Reservation reservation) {
                showFeedback(reservation);
            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvContent.setLayoutManager(linearLayoutManager);
        rvContent.setAdapter(adapter);
    }

    private void showFeedback(Reservation reservation) {
        final ReservationRestaurantFeedbackDialogFragment fragment = ReservationRestaurantFeedbackDialogFragment.newInstance(reservation);
        fragment.show(getActivity().getFragmentManager(), "");
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

    protected void showReservationDeletionAlert(Reservation reservation) {
        // create dialog
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getContext());
        // configure
        alertBuilder.setTitle("Cancel Reservation");
        alertBuilder.setMessage("Do you want to cancel this reservation?");
        alertBuilder.setCancelable(true);
        alertBuilder.setPositiveButton(
                "Cancel Reservation",
                (dialog, id) -> {
                    cancelReservation(reservation);
                });
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
        for (int i = 0; i < reservations.size(); i++) {
            reservationArray.add(reservations.get(i));
        }
        return reservationArray;
    }

    public void getReservations() {
        vSwipe.setRefreshing(true);
        Restaurant restaurant = AppSessionManager.getInstance().getRestaurant();
        ReservationsProvider.rxGetReservationOfRestaurant(restaurant.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ArrayList<Reservation>>() {
                    @Override
                    public void onCompleted() {
                        vSwipe.setRefreshing(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        vSwipe.setRefreshing(false);
                        showToast("Something went wrong");
                    }

                    @Override
                    public void onNext(ArrayList<Reservation> newReservations) {
                        reservations = newReservations;
                        vSwipe.setRefreshing(false);
                        adapter.addReservations(newReservations);
                    }
                });
    }
}
