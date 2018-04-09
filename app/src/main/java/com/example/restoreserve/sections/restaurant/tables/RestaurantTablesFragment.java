package com.example.restoreserve.sections.restaurant.tables;

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
import android.widget.ProgressBar;

import com.example.restoreserve.R;
import com.example.restoreserve.base.BaseFragment;
import com.example.restoreserve.data.reservations.ReservationsManager;
import com.example.restoreserve.data.reservations.ReservationsProvider;
import com.example.restoreserve.data.reservations.model.Reservation;
import com.example.restoreserve.data.reservations.model.Table;
import com.example.restoreserve.data.restaurant.model.Restaurant;
import com.example.restoreserve.data.session.AppSessionManager;
import com.example.restoreserve.sections.restaurant.reservations.RestaurantReservationsAdapter;
import com.example.restoreserve.utils.DateHelper;
import com.example.restoreserve.utils.ui.CustomInputSelectorView;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;
import com.wdullaer.materialdatetimepicker.time.Timepoint;

import java.text.DateFormat;
import java.text.ParseException;
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
public class RestaurantTablesFragment extends BaseFragment {
    CustomInputSelectorView vDate, vTime;
    ProgressBar pbTablesLoading;
    private RecyclerView rvContent;
    private RestaurantTablesAdapter adapter;
    private ArrayList<Table> tables;

    public static RestaurantTablesFragment newInstance() {
        return new RestaurantTablesFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_restaurant_tables, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        configureListing();
    }

    private void initViews(View view) {
        vDate = view.findViewById(R.id.vDate);
        vTime = view.findViewById(R.id.vTime);
        rvContent = view.findViewById(R.id.rvTables);
        pbTablesLoading = view.findViewById(R.id.pbTablesLoading);
        vDate.setOnClickListener(v -> openDate());
        vTime.setOnClickListener(v -> openTime());
    }

    private void configureListing() {
        adapter = new RestaurantTablesAdapter(getContext(), table -> {
            if (table.reservation != null) { // reserved
                openReservationInfo(table);
            } else {
                showCustomReservation(table.table);
            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvContent.setLayoutManager(linearLayoutManager);
        rvContent.setAdapter(adapter);

        tables = AppSessionManager.getInstance().getRestaurant().getTables();
    }

    private void showCustomReservation(Table table) {
        ReserveDialogFragment dialogFragment = ReserveDialogFragment.newInstance(table, vDate.getValue(), vTime.getValue());
        dialogFragment.setListener(new ReserveDialogFragment.OnReservationListener() {
            @Override
            public void onReservation() {
                dialogFragment.dismiss();
                getReservations();
            }
        });
        dialogFragment.show(getActivity().getFragmentManager(), "");
    }

    private void openReservationInfo(RestaurantTablesAdapter.ReservedTable table) {
        final ReservationInfoDialogFragment reservationInfoDialogFragment = ReservationInfoDialogFragment.newInstance(table);
        reservationInfoDialogFragment.setListener(reservation -> {
            reservationInfoDialogFragment.dismiss();
            cancelReservation(reservation);
        });
        reservationInfoDialogFragment.show(getActivity().getFragmentManager(), "");
    }

    private void openDate() {
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
                    vDate.updateValue(formattedDate);
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
        );
        Calendar currentDate = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, currentDate.get(Calendar.DAY_OF_MONTH));
        // add max date restriction to date picker
        dpd.setMinDate(cal);
        cal.set(Calendar.YEAR, currentDate.get(Calendar.YEAR));
        cal.set(Calendar.DAY_OF_MONTH, currentDate.get(Calendar.DAY_OF_MONTH) + 7);
        dpd.setMaxDate(cal);
        dpd.show(getActivity().getFragmentManager(), "");
    }

    private void openTime() {
        if (vDate.getValue().equals(CustomInputSelectorView.DEFAULT_VALUE)) {
            showToast("Please choose a date");
            return;
        }

        DateFormat apiFormat = new SimpleDateFormat(DateHelper.PATTERN_TIME, Locale.ENGLISH);
        TimePickerDialog tpd = TimePickerDialog.newInstance((view, hourOfDay, minute, second) -> {
            // create new calendar instance
            Calendar selectedCalendar = new GregorianCalendar();
            // set date selected to calendar
            selectedCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            selectedCalendar.set(Calendar.MINUTE, minute);
            selectedCalendar.set(Calendar.SECOND, second);

            // get formatted date from selected date
            String formattedTime = apiFormat.format(selectedCalendar.getTime());
            vTime.updateValue(formattedTime);
            getReservations();
        },false);
        tpd.show(getActivity().getFragmentManager(), "");
    }

    protected void showReservationAlert(RestaurantTablesAdapter.ReservedTable reservation) {
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

    protected void showReservationDeletionAlert(RestaurantTablesAdapter.ReservedTable reservedTable) {
        // create dialog
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getContext());
        // configure
        alertBuilder.setTitle("Cancel Reservation");
        alertBuilder.setMessage("Do you want to cancel this reservation?");
        alertBuilder.setCancelable(true);
        alertBuilder.setPositiveButton(
                "Cancel Reservation",
                (dialog, id) -> {
                    cancelReservation(reservedTable);
                });
        // show dialog
        alertBuilder.show();
    }

    private void cancelReservation(RestaurantTablesAdapter.ReservedTable reservedTable) {
        showProgressDialog("Canceling reservation");
        ReservationsProvider.rxCancelReservation(reservedTable.reservation.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleSubscriber<Boolean>() {
                    @Override
                    public void onSuccess(Boolean aBoolean) {
                        dismissProgressDialog();
                        adapter.removeTable(reservedTable);
                        showToast("Reservation Canceled");
                    }

                    @Override
                    public void onError(Throwable error) {
                        dismissProgressDialog();
                    }
                });
    }

    private void confirmReservation(RestaurantTablesAdapter.ReservedTable reservedTable) {
        ReservationsProvider.rxConfirmReservation(reservedTable.reservation.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleSubscriber<Boolean>() {
                    @Override
                    public void onSuccess(Boolean aBoolean) {
                        dismissProgressDialog();
                        showToast("Reservation Confirmed");
                        reservedTable.reservation.setConfirmed(true);
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Throwable error) {
                        dismissProgressDialog();
                    }
                });
    }

    public void getReservations() {
        pbTablesLoading.setVisibility(View.VISIBLE);
        Restaurant restaurant = AppSessionManager.getInstance().getRestaurant();
        ReservationsProvider.rxGetReservationsAtDate(restaurant.getId(), vDate.getValue())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ArrayList<Reservation>>() {
                    @Override
                    public void onCompleted() {}

                    @Override
                    public void onError(Throwable e) {
                        pbTablesLoading.setVisibility(View.GONE);
                        showToast("Something went wrong");
                    }

                    @Override
                    public void onNext(ArrayList<Reservation> reservations) {
                        pbTablesLoading.setVisibility(View.GONE);
                        adapter.clearTables();
                        handleReservations(reservations);
                    }
                });
    }

    private void handleReservations(ArrayList<Reservation> reservations) {
        ArrayList<RestaurantTablesAdapter.ReservedTable> reservedTables = new ArrayList<>();
        for (Table table : tables) {
            Reservation reservationSet = null;
            for (Reservation reservation : reservations) {
                if (reservation.getTableId().equals(table.getId())) {
                    // parse reservation time to date
                    final Date timeReservationDate = DateHelper.parseTime(vTime.getValue());
                    // parse time to be reserved to date
                    final Date timeToBeReservedDate = DateHelper.parseTime(reservation.getTime());
                    // parse 6 pm date
                    final Date date6pm = DateHelper.parseTime("6:00 PM");
                    // check time to be reserved less than 6:00 pm
                    if (timeToBeReservedDate!= null && timeToBeReservedDate.before(date6pm)) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(timeToBeReservedDate);
                        calendar.add(Calendar.HOUR_OF_DAY, 3);
                        final Date timeToBeReservedAfter3Hours = calendar.getTime();
                        if (timeToBeReservedAfter3Hours.after(timeReservationDate)) {
                            reservationSet = reservation;
                        } else {
                            // do nothing already not reserved
                        }
                    } else { // time to be reserved after 6 pm
                        if (timeReservationDate!= null && timeReservationDate.after(date6pm)) {
                            reservationSet = reservation;
                        } else {
                            // remove 3 hours and check if time to reserved is still after reserved time
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime(timeToBeReservedDate);
                            calendar.add(Calendar.HOUR_OF_DAY, -3);
                            final Date timeToBeReservedBefore3Hours = calendar.getTime();
                            if (timeToBeReservedBefore3Hours.before(timeReservationDate)) {
                                reservationSet = reservation;
                            } else {
                                // do nothing already not reserved
                            }
                        }
                    }
                }
            }
            RestaurantTablesAdapter.ReservedTable reservedTable = new RestaurantTablesAdapter.ReservedTable(table, reservationSet);
            reservedTables.add(reservedTable);
        }
        adapter.replaceTables(reservedTables);
    }
}
