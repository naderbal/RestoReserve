package com.example.restoreserve.sections.customer.restaurant;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.example.restoreserve.R;
import com.example.restoreserve.base.BaseActivity;
import com.example.restoreserve.data.reservations.ReservationsManager;
import com.example.restoreserve.data.reservations.ReservationsProvider;
import com.example.restoreserve.data.reservations.model.Reservation;
import com.example.restoreserve.data.reservations.model.ReservedTable;
import com.example.restoreserve.data.reservations.model.Table;
import com.example.restoreserve.data.restaurant.model.Restaurant;
import com.example.restoreserve.data.session.AppSessionManager;
import com.example.restoreserve.data.user.User;
import com.example.restoreserve.data.waitinglist.Waitinglist;
import com.example.restoreserve.data.waitinglist.WaitinglistManager;
import com.example.restoreserve.data.waitinglist.WaitinglistProvider;
import com.example.restoreserve.sections.customer.restaurant.tables_listing.TablesAdapter;
import com.example.restoreserve.utils.ui.CustomInputSelectorView;
import com.example.restoreserve.utils.DateHelper;
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
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 *
 */
public class RestaurantActivity extends BaseActivity {
    public static final String EXTRA_RESTAURANT = "restaurant";
    CustomInputSelectorView vDate, vTime;
    Button btnSubmit;
    Button btnWaitinglist;
    Restaurant restaurant;
    RecyclerView rvTables;
    ProgressBar pbTablesLoading;
    TablesAdapter tablesAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);
        // todo checks
        restaurant = (Restaurant) getIntent().getSerializableExtra(EXTRA_RESTAURANT);
        initViews();
        configureListing();
    }

    private void initViews() {
        vDate = findViewById(R.id.vDate);
        vTime = findViewById(R.id.vTime);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnWaitinglist = findViewById(R.id.btnWaitinglist);
        pbTablesLoading = findViewById(R.id.pbTablesLoading);
        // click listeners
        vDate.setOnClickListener(v -> openDate());
        vTime.setOnClickListener(v -> openTime());
        rvTables = findViewById(R.id.rvTables);
        btnWaitinglist.setOnClickListener(v -> handleWaitinglistClicked());
    }

    private void handleWaitinglistClicked() {
        String customerId = AppSessionManager.getInstance().getUser().getId();
        String restoId = restaurant.getId();
        String restoName = restaurant.getName();
        final String date = vDate.getValue();
        final String time = vTime.getValue();
        btnWaitinglist.setVisibility(View.GONE);
        showProgressDialog("Loading");
        Waitinglist waitinglist = new Waitinglist(customerId, restoId, restoName, date, time);
        WaitinglistProvider.rxAddToWaitinglist(waitinglist)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleSubscriber<String>() {

                    @Override
                    public void onError(Throwable e) {
                        dismissProgressDialog();
                        showToast("Something went wrong");
                        btnWaitinglist.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onSuccess(String s) {
                        showToast("Added to waiting list");
                        dismissProgressDialog();
                        Waitinglist waitinglist1 = new Waitinglist(s, waitinglist);
                        WaitinglistManager.getInstance().addWaitinglist(waitinglist);
                    }
                });
    }

    private void configureListing() {
        tablesAdapter = new TablesAdapter(getBaseContext(), new TablesAdapter.OnTableListener() {
            @Override
            public void onTableClicked(ReservedTable table) {
                handleTableClicked(table);
            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getBaseContext(), LinearLayoutManager.VERTICAL, false);
        rvTables.setLayoutManager(linearLayoutManager);
        rvTables.setAdapter(tablesAdapter);
    }

    private void handleTableClicked(ReservedTable table) {
        showAlert("Are you sure you want to reserve this table", ()->{
            reserveTable(table.getTable());
        });
    }

    private void reserveTable(Table table) {
        User user = AppSessionManager.getInstance().getUser();
        final String userId = user.getId();
        final String restId = restaurant.getId();
        final String restName = restaurant.getName();
        final String date = vDate.getValue();
        final String time = vTime.getValue();
        final String tableId = table.getId();
        // show loading
        showProgressDialog("Reserving table");
        Reservation reservation = new Reservation(restId, restName, userId, date, time, tableId, false);
        ReservationsProvider.rxReserveTable(reservation)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleSubscriber<String>() {
                    @Override
                    public void onSuccess(String id) {
                        showToast("Reserved Successfully");
                        dismissProgressDialog();
                        Reservation reservation1 = new Reservation(id, reservation);
                        ReservationsManager.getInstance().addReservation(reservation1);
                        finish();
                    }

                    @Override
                    public void onError(Throwable error) {
                        dismissProgressDialog();
                        showToast("Something went wrong");
                    }
                });
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
                    submitDateOrTimePicked();
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
        dpd.show(getFragmentManager(), "");
    }

    private void openTime() {
        if (vDate.getValue().equals(CustomInputSelectorView.DEFAULT_VALUE)) {
            showToast("Please choose a date");
            return;
        }
        Calendar cal = Calendar.getInstance();
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
                submitDateOrTimePicked();
        },false);

        Calendar currentDate = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, currentDate.get(Calendar.DAY_OF_MONTH));

        try {
            final String openingHour = restaurant.getOpeningHour();
            final String closingHour = restaurant.getClosingHour();
            String chosenDate = vDate.getValue();
            // check if chosen date is today and handle min/max time
            if (!chosenDate.equals(CustomInputSelectorView.DEFAULT_VALUE)) {
                Date parsedChosenDate = DateHelper.parseDate(chosenDate, DateHelper.PATTERN_API_DATE);

                final Date calTime = Calendar.getInstance().getTime();
                String strCurrentTime = DateHelper.formatDate(calTime);
                Date parsedCurrentDate = DateHelper.parseDate(strCurrentTime, DateHelper.PATTERN_API_DATE);

                if (parsedChosenDate != null && parsedChosenDate.equals(parsedCurrentDate)) {
                    strCurrentTime = DateHelper.formatTime(calTime);
                    // check time
                    final Date parsedCurrentTime = DateHelper.parseTime(strCurrentTime);
                    final Date parsedOpeningHourTime = DateHelper.parseTime(openingHour);
                    final Date parsedClosingHourTime = DateHelper.parseTime(closingHour);

                    if (parsedCurrentTime != null && parsedCurrentTime.after(parsedClosingHourTime)) {
                        showToast("Restaurant stopped taking reservations for this day", false);
                        return;
                    } else {
                        // max time
                        final Date maxDate = apiFormat.parse(closingHour);
                        cal.setTime(maxDate);
                        Timepoint maxTime = new Timepoint(cal.get(Calendar.HOUR_OF_DAY));
                        tpd.setMaxTime(maxTime);
                    }
                    if (parsedCurrentTime != null && parsedCurrentTime.after(parsedOpeningHourTime)) {
                        cal = Calendar.getInstance();
                        Timepoint minTime = new Timepoint(cal.get(Calendar.HOUR_OF_DAY ) + 1);
                        tpd.setMinTime(minTime);
                    } else {
                        // min time
                        Date minDate = apiFormat.parse(openingHour);
                        cal.setTime(minDate);
                        Timepoint minTime = new Timepoint(cal.get(Calendar.HOUR_OF_DAY));
                        tpd.setMinTime(minTime);
                    }
                } else {
                    // min time
                    Date minDate = apiFormat.parse(openingHour);
                    cal.setTime(minDate);
                    Timepoint minTime = new Timepoint(cal.get(Calendar.HOUR_OF_DAY));

                    // max time
                    final Date maxDate = apiFormat.parse(closingHour);
                    cal.setTime(maxDate);
                    Timepoint maxTime = new Timepoint(cal.get(Calendar.HOUR_OF_DAY));
                    // add min and  max time restriction to time picker
                    tpd.setMinTime(minTime);
                    tpd.setMaxTime(maxTime);
                }
            }
            tpd.setTimeInterval(1, 30);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        tpd.show(getFragmentManager(), "");
    }

    private void submitDateOrTimePicked() {
        // remove previous query tables
        tablesAdapter.clearPreviousTables();
        // get date and time values
        // check if user have already a reservation set at this date/time combination
        // also checking the 6:00 pm rule
        final String dateValue = vDate.getValue();
        final String timeValue = vTime.getValue();
        if (!dateValue.equals(CustomInputSelectorView.DEFAULT_VALUE) && !timeValue.equals(CustomInputSelectorView.DEFAULT_VALUE)) {
            final ArrayList<Reservation> userReservations = ReservationsManager.getInstance().getReservations();
            if (userReservations != null) {
                for (Reservation userReservation : userReservations) {
                    if (userReservation.getDate().equals(dateValue)) {
                        // parse reservation time to date
                        final String reservationTime = userReservation.getTime();
                        final Date timeReservationDate = DateHelper.parseTime(reservationTime);
                        // parse time to be reserved to date
                        final Date timeToBeReservedDate = DateHelper.parseTime(timeValue);
                        // parse 6 pm date
                        final Date date6pm = DateHelper.parseTime("6:00 PM");
                        // check time to be reserved less than 6:00 pm
                        if (timeToBeReservedDate!= null && timeToBeReservedDate.before(date6pm)) {
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime(timeToBeReservedDate);
                            calendar.add(Calendar.HOUR_OF_DAY, 3);
                            final Date timeToBeReservedAfter3Hours = calendar.getTime();
                            if (timeToBeReservedAfter3Hours.after(timeReservationDate)) {
                                showCantReserveUI();
                                return;
                            }
                        } else { // time to be reserved after 6 pm
                            if (timeReservationDate!= null && timeReservationDate.after(date6pm)) {
                                showCantReserveUI();
                                return;
                            } else {
                                // remove 3 hours and check if time to reserved is still after reserved time
                                Calendar calendar = Calendar.getInstance();
                                calendar.setTime(timeToBeReservedDate);
                                calendar.add(Calendar.HOUR_OF_DAY, -3);
                                final Date timeToBeReservedBefore3Hours = calendar.getTime();
                                if (timeToBeReservedBefore3Hours.before(timeReservationDate)) {
                                    showCantReserveUI();
                                    return;
                                }
                            }
                        }
                    }
                }
            }
            fetchReservations();
        }
    }

    private void showCantReserveUI() {
        showToast("You have a previous reservation at this date/time");
    }

    private void fetchReservations() {
        User user = AppSessionManager.getInstance().getUser();
        String date = vDate.getValue();
        // set tables loading
        pbTablesLoading.setVisibility(View.VISIBLE);
        ReservationsProvider.rxGetReservationsAtDate(restaurant.getId(), date)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new SingleSubscriber<ArrayList<Reservation>>() {
            @Override
            public void onSuccess(ArrayList<Reservation> reservations) {
                pbTablesLoading.setVisibility(View.INVISIBLE);
                handleReservationsReceived(reservations);
            }

            @Override
            public void onError(Throwable error) {
                pbTablesLoading.setVisibility(View.INVISIBLE);
                showToast("Something went wrong");
            }
        });
    }

    private void handleReservationsReceived(ArrayList<Reservation> reservations) {
        final String time = vTime.getValue();
        ArrayList<ReservedTable> reservedTables = getAllTablesAvailableAtTime(restaurant, reservations, time);
        if (reservedTables != null) {
            // add new tables
            tablesAdapter.addTables(reservedTables);
            checkAllReserved(reservedTables);
        }
    }

    private void checkAllReserved(ArrayList<ReservedTable> reservedTables) {
        for (ReservedTable reservedTable : reservedTables) {
            if (!reservedTable.isReserved()) {
                btnWaitinglist.setVisibility(View.GONE);
                return;
            }
        }
        btnWaitinglist.setVisibility(View.VISIBLE);
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
}
