package com.example.restoreserve.sections.customer.restaurant;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.restoreserve.R;
import com.example.restoreserve.base.BaseActivity;
import com.example.restoreserve.data.MyNotificationPublisher;
import com.example.restoreserve.data.reservations.ReservationsManager;
import com.example.restoreserve.data.reservations.ReservationsProvider;
import com.example.restoreserve.data.reservations.model.Reservation;
import com.example.restoreserve.data.reservations.model.ReservedTable;
import com.example.restoreserve.data.reservations.model.Table;
import com.example.restoreserve.data.restaurant.CustomerProvider;
import com.example.restoreserve.data.restaurant.model.Restaurant;
import com.example.restoreserve.data.session.AppSessionManager;
import com.example.restoreserve.data.user.User;
import com.example.restoreserve.data.waitinglist.Waitinglist;
import com.example.restoreserve.data.waitinglist.WaitinglistManager;
import com.example.restoreserve.data.waitinglist.WaitinglistProvider;
import com.example.restoreserve.sections.customer.restaurant.tables_listing.TablesAdapter;
import com.example.restoreserve.sections.splash.SplashActivity;
import com.example.restoreserve.utils.ui.CustomInputSelectorView;
import com.example.restoreserve.utils.DateHelper;
import com.squareup.picasso.Picasso;
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
    TextView tvName, tvBranch, tvPhoneNumber, tvAddress, tvWebsite, tvOpeningHours;
    CustomInputSelectorView vDate, vTime;
    Button btnSubmit;
    Button btnWaitinglist;
    Restaurant restaurant;
    RecyclerView rvTables;
    ProgressBar pbTablesLoading;
    TablesAdapter tablesAdapter;
    ImageView ivTables;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);
        // todo checks
        restaurant = (Restaurant) getIntent().getSerializableExtra(EXTRA_RESTAURANT);
        initViews();
        setupInfo();
        configureListing();
        checkIfBanned();
    }

    private void initViews() {
        tvName = findViewById(R.id.tvName);
        tvOpeningHours = findViewById(R.id.tvOpeningHours);
        tvBranch = findViewById(R.id.tvBranch);
        tvPhoneNumber = findViewById(R.id.tvPhoneNumber);
        tvAddress = findViewById(R.id.tvAddress);
        tvWebsite = findViewById(R.id.tvWebsite);
        tvName = findViewById(R.id.tvName);
        vDate = findViewById(R.id.vDate);
        vTime = findViewById(R.id.vTime);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnWaitinglist = findViewById(R.id.btnWaitinglist);
        pbTablesLoading = findViewById(R.id.pbTablesLoading);
        ivTables = findViewById(R.id.ivTables);
        // click listeners
        vDate.setOnClickListener(v -> openDate());
        vTime.setOnClickListener(v -> openTime());
        rvTables = findViewById(R.id.rvTables);
        btnWaitinglist.setOnClickListener(v -> handleWaitinglistClicked());
    }

    private void setupInfo() {
        tvName.setText(restaurant.getName());
        final String branch = restaurant.getBranch();
        if (branch != null) {
            tvBranch.setText(branch);
        } else {
            tvBranch.setVisibility(View.GONE);
        }

        final String phoneNumber = restaurant.getPhoneNumber();
        if (phoneNumber != null) {
            tvPhoneNumber.setText(phoneNumber);
        } else {
            tvPhoneNumber.setVisibility(View.GONE);
        }
        final String address = restaurant.getAddress();
        if (address != null) {
            tvAddress.setText(address);
        } else {
            tvAddress.setVisibility(View.GONE);
        }
        final String website = restaurant.getWebsite();
        if (website != null) {
            tvWebsite.setText(website);
        } else {
            tvWebsite.setVisibility(View.GONE);
        }
        tvOpeningHours.setText(restaurant.getOpeningHour() +" - " +restaurant.getClosingHour());

        final String tablesPicUrl = restaurant.getTablesPicUrl();
        if (tablesPicUrl != null) {
            Picasso.with(getBaseContext())
                    .load(tablesPicUrl)
                    .placeholder(R.drawable.progress_animation)
                    .into(ivTables);
        } else{
            ivTables.setVisibility(View.GONE);
        }
    }

    private void checkIfBanned() {
        showProgressDialog("Loading");
        final String id = AppSessionManager.getInstance().getUser().getId();
        CustomerProvider.rxGetBannedOfCustomer(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleSubscriber<ArrayList<String>>() {
                    @Override
                    public void onSuccess(ArrayList<String> strings) {
                        dismissProgressDialog();
                        for (String string : strings) {
                            if (restaurant.getId().equals(string)) {
                                showToast("You are banned from this restaurant");
                                finish();
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable error) {

                    }
                });
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
                        WaitinglistManager.getInstance().addWaitinglist(waitinglist1);
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
        final String userName = user.getName();
        final String userPhonenumber = user.getPhoneNumber();
        final String restId = restaurant.getId();
        final String restName = restaurant.getName();
        final String date = vDate.getValue();
        final String time = vTime.getValue();
        final String tableId = table.getId();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.ENGLISH);
        Date resDate = null;
        try {
            resDate = sdf.parse(date + " " + time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        final Calendar instance = Calendar.getInstance();
        instance.setTime(resDate);
        final Date resTime = instance.getTime();

        final Calendar currentCalendar = Calendar.getInstance();
        currentCalendar.add(Calendar.HOUR, 1);
        Date currentDate = currentCalendar.getTime();

        boolean isConfirmed = false;
        if (resTime.before(currentDate)) isConfirmed = true;
        // show loading
        showProgressDialog("Reserving table");
        Reservation reservation = new Reservation(restId, restName, userId, userName, userPhonenumber, date, time, tableId, isConfirmed);
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
                        createNotification(reservation1);
                        finish();
                    }

                    @Override
                    public void onError(Throwable error) {
                        dismissProgressDialog();
                        showToast("Something went wrong");
                    }
                });
    }

    private void createNotification(Reservation reservation) {
        final Calendar instance = Calendar.getInstance();
        String strDate = reservation.getDate() + " " + reservation.getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.ENGLISH);
        Date date;
        try {
            date = simpleDateFormat.parse(strDate);
        } catch (ParseException e) {
            return;
        }
        final Date time = instance.getTime();
        long delay = date.getTime() - time.getTime() - (restaurant.getConfirmationDelayMins() * 1000);
        final String notificationText = getNotificationText(reservation);
        scheduleNotification(delay, notificationText, 1);
    }

    private String getNotificationText(Reservation reservation) {
        return "You should confirm your reservation at " + reservation.getRestoName();
    }

    public void scheduleNotification(long delay, String text, int notificationId) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getBaseContext(), "")
                .setContentTitle(getBaseContext().getString(R.string.app_name))
                .setContentText(text)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.logo)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

        Intent intent = new Intent(getBaseContext(), SplashActivity.class);
        PendingIntent activity = PendingIntent.getActivity(getBaseContext(), notificationId, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        builder.setContentIntent(activity);

        Notification notification = builder.build();

        Intent notificationIntent = new Intent(getBaseContext(), MyNotificationPublisher.class);
        notificationIntent.putExtra(MyNotificationPublisher.NOTIFICATION_ID, notificationId);
        notificationIntent.putExtra(MyNotificationPublisher.NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), notificationId, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        long futureInMillis = SystemClock.elapsedRealtime() + delay;
        AlarmManager alarmManager = (AlarmManager) getBaseContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);
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
                    try {
                        submitDateOrTimePicked();
                    } catch (ParseException ignored) {

                    }
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
            try {
                submitDateOrTimePicked();
            } catch (ParseException ignored) {

            }
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
                    Date maxDate = apiFormat.parse(closingHour);
                    Timepoint minTime;
                    Timepoint maxTime;
                    if (minDate.before(maxDate)) {
                        cal.setTime(minDate);
                        minTime = new Timepoint(cal.get(Calendar.HOUR_OF_DAY));

                        // max time
                        cal.setTime(maxDate);
                        maxTime = new Timepoint(cal.get(Calendar.HOUR_OF_DAY));
                        // add min and  max time restriction to time picker
                        tpd.setMinTime(minTime);
                        tpd.setMaxTime(maxTime);
                    }
                }
            }
            tpd.setTimeInterval(1, 30);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        tpd.show(getFragmentManager(), "");
    }

    private void submitDateOrTimePicked() throws ParseException {
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
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
                        final Date timeReservationDate = DateHelper.parseTime(reservationTime);
                        // parse time to be reserved to date
                        final Date timeToBeReservedDate = simpleDateFormat.parse(timeValue);
                        // parse 6 pm date
                        final Date date6pm = simpleDateFormat.parse("6:00 PM");
                        // check time to be reserved less than 6:00 pm
                        if (timeToBeReservedDate!= null && timeToBeReservedDate.before(date6pm)) {
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime(timeToBeReservedDate);
                            calendar.add(Calendar.HOUR_OF_DAY, 3);
                            final Date timeToBeReservedAfter3Hours = calendar.getTime();
                            if (timeToBeReservedAfter3Hours.before(timeReservationDate)) {
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
