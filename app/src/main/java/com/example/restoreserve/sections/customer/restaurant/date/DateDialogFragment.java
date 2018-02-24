package com.example.restoreserve.sections.customer.restaurant.date;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;

import com.example.restoreserve.R;

/**
 *
 */

public class DateDialogFragment extends DialogFragment {
    OnDateListener listener;
    DatePicker datePicker;
    Button btnCancel, btnSubmit;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_fragment_date, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
    }

    public void setListener(OnDateListener listener) {
        this.listener = listener;
    }

    private void initViews(View view) {
        datePicker = view.findViewById(R.id.datePicker);
        btnCancel = view.findViewById(R.id.btnCancel);
        btnSubmit = view.findViewById(R.id.btnSubmit);

        // click listeners
        btnCancel.setOnClickListener(v -> cancelClicked());
        btnSubmit.setOnClickListener(v -> submitClicked());
    }

    private void submitClicked() {
        final int dayOfMonth = datePicker.getDayOfMonth();
        final int month = datePicker.getMonth();
        final int year = datePicker.getYear();

        if (listener != null) {
            listener.onSubmitClicked(dayOfMonth, month, year);
        }
    }

    private void cancelClicked() {
        dismiss();
    }

    interface OnDateListener {
        void onSubmitClicked(int day, int month, int year);
    }

}
