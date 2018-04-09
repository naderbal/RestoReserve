package com.example.restoreserve.sections.restaurant.tables;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.restoreserve.R;

/**
 *
 */
public class ReservationInfoDialogFragment extends DialogFragment {
    RestaurantTablesAdapter.ReservedTable reservation;
    TextView tvName, tvTable, tvDate, tvTime, tvPhoneNumber;
    Button btnCancel;
    OnReservationInfoListener listener;

    public static ReservationInfoDialogFragment newInstance(RestaurantTablesAdapter.ReservedTable reservation) {

        Bundle args = new Bundle();
        args.putSerializable("reservation", reservation);
        ReservationInfoDialogFragment fragment = new ReservationInfoDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_fragment_tables_reservation_info, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        reservation = (RestaurantTablesAdapter.ReservedTable) getArguments().getSerializable("reservation");
        initViews(view);
        bindData(reservation);
    }

    public void setListener(OnReservationInfoListener listener) {
        this.listener = listener;
    }

    private void bindData(RestaurantTablesAdapter.ReservedTable reservation) {
        tvName.setText(reservation.reservation.getCustomerName());
        tvPhoneNumber.setText(reservation.reservation.getCustomerPhonenumber());
        tvTable.setText(reservation.reservation.getTableId());
        tvDate.setText(reservation.reservation.getDate());
        tvTime.setText(reservation.reservation.getTime());
    }

    private void initViews(View view) {
        tvName = view.findViewById(R.id.tvName);
        tvPhoneNumber = view.findViewById(R.id.tvPhoneNumber);
        tvTable = view.findViewById(R.id.tvTable);
        tvDate = view.findViewById(R.id.tvDate);
        tvTime = view.findViewById(R.id.tvTime);
        btnCancel = view.findViewById(R.id.btnCancel);

        btnCancel.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCancelReservation(reservation);
            }
        });
    }

    interface OnReservationInfoListener {
        void onCancelReservation(RestaurantTablesAdapter.ReservedTable reservation);
    }

}
