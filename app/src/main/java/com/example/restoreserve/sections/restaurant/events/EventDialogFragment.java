package com.example.restoreserve.sections.restaurant.events;

import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.restoreserve.R;
import com.example.restoreserve.data.event.Event;
import com.example.restoreserve.data.event.EventsProvider;
import com.example.restoreserve.data.restaurant.model.Restaurant;
import com.example.restoreserve.data.session.AppSessionManager;
import com.example.restoreserve.sections.restaurant.tables.RestaurantTablesAdapter;

import rx.SingleSubscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 *
 */
public class EventDialogFragment extends DialogFragment {
    Restaurant restaurant;
    Button btnCancel;
    Button btnAdd;
    EditText etEvent;
    OnReservationInfoListener listener;

    public static EventDialogFragment newInstance() {
        Bundle args = new Bundle();
        EventDialogFragment fragment = new EventDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_fragment_add_event, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
    }

    public void setListener(OnReservationInfoListener listener) {
        this.listener = listener;
    }

    private void initViews(View view) {
        etEvent = view.findViewById(R.id.etEvent);
        btnCancel = view.findViewById(R.id.btnCancel);
        btnAdd = view.findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(v -> addEvent());
        btnCancel.setOnClickListener(v -> dismiss());
    }

    private void addEvent() {
        final String event = etEvent.getText().toString().trim();
        if (event.isEmpty()){
            showToast("Event/offer can't be empty");
            return;
        }
        showProgressDialog("loading");
        Restaurant restaurant = AppSessionManager.getInstance().getRestaurant();
        EventsProvider.rxAddEvent(restaurant.getId(), event)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleSubscriber<Boolean>() {
                    @Override
                    public void onSuccess(Boolean aBoolean) {
                        dismissProgressDialog();
                        if (listener != null) {
                            listener.onAddEventSuccesfull();
                        }
                    }

                    @Override
                    public void onError(Throwable error) {
                        dismissProgressDialog();
                    }
                });
    }

    interface OnReservationInfoListener {
        void onAddEventSuccesfull();
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


}
