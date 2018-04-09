package com.example.restoreserve.sections.restaurant.tables;

import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.restoreserve.R;
import com.example.restoreserve.base.BaseFragment;
import com.example.restoreserve.data.reservations.ReservationsManager;
import com.example.restoreserve.data.reservations.ReservationsProvider;
import com.example.restoreserve.data.reservations.model.Reservation;
import com.example.restoreserve.data.reservations.model.Table;
import com.example.restoreserve.data.restaurant.model.Restaurant;
import com.example.restoreserve.data.session.AppSessionManager;
import com.example.restoreserve.data.user.User;
import com.example.restoreserve.utils.DateHelper;
import com.example.restoreserve.utils.ui.CustomInputSelectorView;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import rx.SingleSubscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 *
 */
public class ReserveDialogFragment extends DialogFragment {
    private EditText etName, etPhone;
    private Button btnReserve;
    private Table table;
    OnReservationListener listener;
    String date;
    String time;
    public static ReserveDialogFragment newInstance(Table table, String date, String time) {
        Bundle args = new Bundle();
        args.putSerializable("table", table);
        args.putSerializable("date", date);
        args.putSerializable("time", time);
        ReserveDialogFragment fragment = new ReserveDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_fragment_tables_reserve, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        table = (Table) getArguments().getSerializable("table");
        date = (String) getArguments().getSerializable("date");
        time = (String) getArguments().getSerializable("time");
        initViews(view);
    }

    public void setListener(OnReservationListener listener) {
        this.listener = listener;
    }

    private void initViews(View view) {
        etName = view.findViewById(R.id.etName);
        etPhone = view.findViewById(R.id.etPhone);
        btnReserve = view.findViewById(R.id.btnReservation);
        btnReserve.setOnClickListener(v -> makeReservation());
    }

    private void makeReservation() {
        if (etName.getText().toString().isEmpty()) {
            showToast("Please set customer name");
            return;
        }
        if (etPhone.getText().toString().isEmpty()) {
            showToast("Please set customer phone number");
            return;
        }
        //
        Restaurant restaurant = AppSessionManager.getInstance().getRestaurant();
        String restoId = restaurant.getId();
        String restoName = restaurant.getName();
        String customerId = null;
        String customerName = etName.getText().toString().trim();
        String customerPhonenumber = etPhone.getText().toString().trim();
        String tableId = table.getId();
        Reservation reservation = new Reservation(restoId, restoName,
                customerId,
                customerName,
                customerPhonenumber,
                date, time, tableId, true);
        //
        reserveTable(reservation);
    }

    private void reserveTable(Reservation reservation) {
        // show loading
        showProgressDialog("Reserving table");
        ReservationsProvider.rxReserveTable(reservation)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleSubscriber<String>() {
                    @Override
                    public void onSuccess(String id) {
                        showToast("Reserved Successfully");
                        dismissProgressDialog();
                        if (listener != null) {
                            listener.onReservation();
                        }
                    }

                    @Override
                    public void onError(Throwable error) {
                        dismissProgressDialog();
                        showToast("Something went wrong");
                    }
                });
    }

    protected void showToast(String text) {
        Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
    }

    private ProgressDialog pdLoading;

    protected void showProgressDialog(int resId) {
        String message = getString(resId);
        showProgressDialog(message);
    }

    protected Handler handler = new Handler();


    protected void showProgressDialog(String message) {
        handler.post((() -> {
            if (pdLoading == null) {
                pdLoading = new ProgressDialog(getActivity());
                pdLoading.setCancelable(false);
                pdLoading.setIndeterminate(true);
            }
            pdLoading.setMessage(message != null? message: getString(R.string.loading));
            if (!pdLoading.isShowing()) {
                pdLoading.show();
            }
        }));
    }

    protected void dismissProgressDialog() {
        handler.post((() -> {
            if (pdLoading != null && pdLoading.isShowing()) {
                pdLoading.dismiss();
            }
        }));
    }

    interface OnReservationListener {
        void onReservation();
    }

}
