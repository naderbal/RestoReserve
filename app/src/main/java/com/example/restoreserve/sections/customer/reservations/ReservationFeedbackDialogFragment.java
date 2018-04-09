package com.example.restoreserve.sections.customer.reservations;

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
import android.widget.RatingBar;
import android.widget.Toast;

import com.example.restoreserve.R;
import com.example.restoreserve.data.feedback.FeedbackProvider;
import com.example.restoreserve.data.reservations.model.Reservation;

import rx.SingleSubscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 *
 */
public class ReservationFeedbackDialogFragment extends DialogFragment {
    RatingBar vRating;
    EditText etFeedback;
    Button btnSubmit;
    OnFeedbackListener listener;
    Reservation reservation;

    public static ReservationFeedbackDialogFragment newInstance(Reservation reservation) {
        Bundle args = new Bundle();
        args.putSerializable("reservation", reservation);
        ReservationFeedbackDialogFragment fragment = new ReservationFeedbackDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_fragment_reservation_feedback, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        reservation = (Reservation) getArguments().getSerializable("reservation");

        initViews(view);
    }

    public void setListener(OnFeedbackListener listener) {
        this.listener = listener;
    }

    private void initViews(View view) {
        vRating = view.findViewById(R.id.rating);
        etFeedback = view.findViewById(R.id.etFeedback);
        btnSubmit = view.findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(v -> handleSubmit());
    }

    private void handleSubmit() {
        showProgressDialog("Submitting feedback");
        final float rating = this.vRating.getRating();
        String message = etFeedback.getText().toString().trim();
        FeedbackProvider.rxAddFeedback(reservation.getId(), rating, message)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleSubscriber<Boolean>() {
                    @Override
                    public void onSuccess(Boolean aBoolean) {
                        dismissProgressDialog();
                        showToast("Review submitted successfully");
                        if (listener != null) {
                            listener.onFeedbackSubmitted();
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

    interface OnFeedbackListener {
        void onFeedbackSubmitted();
    }

}
