package com.example.restoreserve.data.feedback;

import com.example.restoreserve.data.FirestoreManager;
import com.example.restoreserve.data.StorageKeys;
import com.example.restoreserve.data.reservations.model.Reservation;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.List;

import rx.Single;

/**
 *
 */

public class FeedbackProvider {

    public static Single<Boolean> rxAddFeedback(String reservationId, float rating, String message) {
        final FirebaseFirestore instance = FirestoreManager.getInstance().getFirestoreInstance();
        HashMap<String, Object> map = new HashMap<>();
        map.put(StorageKeys.RESRVATION_ID, reservationId);
        map.put(StorageKeys.RATING, rating);
        map.put(StorageKeys.FEEDBACK_MESSAGE, message);

        return Single.create(singleSubscriber -> {
            instance
                    .collection(StorageKeys.FEEDBACKS)
                    .add(map)
                    .addOnCompleteListener(task -> {
                        // check if profile set
                        if (task.isSuccessful()) {
                            // check if user exists with checking if document exists
                            try {
                                singleSubscriber.onSuccess(true);
                            } catch (Exception e) {
                                singleSubscriber.onError(new Exception());
                            }
                        } else {
                            // broadcast error
                            Exception exception = task.getException();
                            singleSubscriber.onError(exception);
                        }
                    });
        }).flatMap(a -> rxReservationHasFeedback(reservationId));
    }

    private static Single<Boolean> rxReservationHasFeedback(String reservationId) {
        final FirebaseFirestore instance = FirestoreManager.getInstance().getFirestoreInstance();
        HashMap<String, Object> map = new HashMap<>();
        map.put(StorageKeys.HAS_FEEDBACK, true);

        return Single.create(singleSubscriber -> {
            instance
                    .collection(StorageKeys.RESERVATIONS)
                    .document(reservationId)
                    .set(map, SetOptions.merge())
                    .addOnCompleteListener(task -> {
                        // check if profile set
                        if (task.isSuccessful()) {
                            // check if user exists with checking if document exists
                            try {
                                singleSubscriber.onSuccess(true);
                            } catch (Exception e) {
                                singleSubscriber.onError(new Exception());
                            }
                        } else {
                            // broadcast error
                            Exception exception = task.getException();
                            singleSubscriber.onError(exception);
                        }
                    });
        });
    }

    public static Single<Feedback> rxGetFeedback(String reservationId) {
        final FirebaseFirestore instance = FirestoreManager.getInstance().getFirestoreInstance();

        return Single.create(singleSubscriber -> {
            instance
                    .collection(StorageKeys.FEEDBACKS)
                    .whereEqualTo(StorageKeys.RESRVATION_ID, reservationId)
                    .get()
                    .addOnCompleteListener(task -> {
                        // check if profile set
                        if (task.isSuccessful()) {
                            final List<DocumentSnapshot> documents = task.getResult().getDocuments();
                            if (documents.size() > 0) {
                                Feedback feedback = generateFeedback(documents.get(0));
                                singleSubscriber.onSuccess(feedback);
                            } else {
                                singleSubscriber.onError(new Exception());
                            }
                        } else {
                            // broadcast error
                            Exception exception = task.getException();
                            singleSubscriber.onError(exception);
                        }
                    });
        });
    }

    private static Feedback generateFeedback(DocumentSnapshot snapshot) {
        return new Feedback(snapshot);
    }


}
