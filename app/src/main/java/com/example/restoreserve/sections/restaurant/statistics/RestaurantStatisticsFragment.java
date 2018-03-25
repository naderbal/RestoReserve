package com.example.restoreserve.sections.restaurant.statistics;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.restoreserve.R;
import com.example.restoreserve.base.BaseFragment;
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
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 *
 */

public class RestaurantStatisticsFragment extends BaseFragment {
    Button btnSubmit;
    CustomInputSelectorView vStartDate, vEndDate;
    TextView tvReservationsCount;

    public static RestaurantStatisticsFragment newInstance() {
        Bundle args = new Bundle();
        RestaurantStatisticsFragment fragment = new RestaurantStatisticsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_restaurant_statistics, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnSubmit = view.findViewById(R.id.btnSubmit);
        vStartDate = view.findViewById(R.id.vStartDate);
        vEndDate = view.findViewById(R.id.vEndDate);
        tvReservationsCount = view.findViewById(R.id.tvReservationsCount);
        btnSubmit.setOnClickListener(v -> getReservations());
        vStartDate.setOnClickListener(v -> openStartDate());
        vEndDate.setOnClickListener(v -> openEndDate());
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

    void getReservations() {
        final String strStartDate = vStartDate.getValue();
        final String strEndDate = vEndDate.getValue();
        if (strStartDate.equals(CustomInputSelectorView.DEFAULT_VALUE)) {
            showToast("Please set start date");
            return;
        }
        if (strEndDate.equals(CustomInputSelectorView.DEFAULT_VALUE)){
            showToast("Please set end date");
            return;
        }
        showProgressDialog("Loading");
        final Restaurant restaurant = AppSessionManager.getInstance().getRestaurant();
        ReservationsProvider.rxGetReservationOfRestaurant(restaurant.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleSubscriber<ArrayList<Reservation>>() {
                    @Override
                    public void onSuccess(ArrayList<Reservation> reservations) {
                        dismissProgressDialog();
                        getReservationsCount(reservations);
                    }

                    @Override
                    public void onError(Throwable error) {
                        dismissProgressDialog();
                    }
                });
    }

    private void getReservationsCount(ArrayList<Reservation> reservations) {
        final String strStartDate = vStartDate.getValue();
        final String strEndDate = vEndDate.getValue();
        Date startDate = null;
        Date endDate = null;
        if (!strStartDate.equals(CustomInputSelectorView.DEFAULT_VALUE)) {
            startDate = DateHelper.parseApiDate(strStartDate);
        }
        if (!strEndDate.equals(CustomInputSelectorView.DEFAULT_VALUE)) {
            endDate = DateHelper.parseApiDate(strEndDate);
        }
        int count = 0;
        for (Reservation reservation : reservations) {
            String strResDate = reservation.getDate();
            Date resDate = DateHelper.parseApiDate(strResDate);
            if ((startDate.equals(resDate) || endDate.equals(resDate)) || (startDate.before(resDate) && endDate.after(resDate))) {
                count++;
            }
        }
        tvReservationsCount.setText(String.valueOf(count));
    }
}
