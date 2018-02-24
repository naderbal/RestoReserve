package com.example.restoreserve.utils.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TimePicker;

import com.example.restoreserve.R;
import com.example.restoreserve.utils.DateHelper;

import java.util.Calendar;

/**
 *
 */

public class DialogFragmentTimePicker extends DialogFragment {
    // listener
    private OnTimePickerListener listener;
    // views
    private TimePicker timePicker;
    private Button btnSubmit;
    private Button btnCancel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_fragment_time, container, false);
    }

    public void setListener(OnTimePickerListener listener) {
        this.listener = listener;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        timePicker = view.findViewById(R.id.timePicker);
        btnSubmit = view.findViewById(R.id.btnSubmit);
        btnCancel = view.findViewById(R.id.btnCancel);

        btnSubmit.setOnClickListener(v -> submitTime());
        btnCancel.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCancel();
            }
        });
    }

    private void submitTime() {
        int hour = 0;
        int minute = 0;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            hour = timePicker.getHour();
        } else {
            hour = timePicker.getCurrentHour();
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            minute = timePicker.getMinute();
        } else {
            minute = timePicker.getCurrentMinute();
        }

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);

        final String s = DateHelper.formatTime(calendar.getTime());


        if (listener != null) {
            listener.onSubmit(s);
        }

    }

    public interface OnTimePickerListener {
        void onSubmit(String time);
        void onCancel();
    }
}
