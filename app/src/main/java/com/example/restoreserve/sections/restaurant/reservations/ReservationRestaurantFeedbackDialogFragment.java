package com.example.restoreserve.sections.restaurant.reservations;

import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.restoreserve.R;
import com.example.restoreserve.data.feedback.Feedback;
import com.example.restoreserve.data.feedback.FeedbackProvider;
import com.example.restoreserve.data.reservations.model.Reservation;

import rx.SingleSubscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 *
 */
public class ReservationRestaurantFeedbackDialogFragment extends DialogFragment {
    RatingBar vRating;
    TextView tvFeedback;
    Reservation reservation;

    public static ReservationRestaurantFeedbackDialogFragment newInstance(Reservation reservation) {
        Bundle args = new Bundle();
        args.putSerializable("reservation", reservation);
        ReservationRestaurantFeedbackDialogFragment fragment = new ReservationRestaurantFeedbackDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_fragment_restaraunt_reservation_feedback, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        reservation = (Reservation) getArguments().getSerializable("reservation");

        initViews(view);
        getFeedback();
    }

    private void initViews(View view) {
        vRating = view.findViewById(R.id.rating);
        tvFeedback = view.findViewById(R.id.tvFeedback);
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

    public void getFeedback() {
        showProgressDialog("");
        FeedbackProvider.rxGetFeedback(reservation.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleSubscriber<Feedback>() {
                    @Override
                    public void onSuccess(Feedback feedback) {
                        dismissProgressDialog();
                        bindData(feedback);
                    }

                    @Override
                    public void onError(Throwable error) {
                        dismissProgressDialog();
                        showToast("Something went wrong");
                        dismiss();
                    }
                });
    }

    private void bindData(Feedback feedback) {
        vRating.setRating(feedback.getRating());
        tvFeedback.setText(feedback.getFeedbackMessage());
    }
}
