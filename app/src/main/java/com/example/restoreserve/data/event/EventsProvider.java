package com.example.restoreserve.data.event;

import com.example.restoreserve.data.FirestoreManager;
import com.example.restoreserve.data.StorageKeys;
import com.example.restoreserve.data.feedback.Feedback;
import com.example.restoreserve.data.reservations.model.Reservation;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import rx.Single;

/**
 *
 */

public class EventsProvider {

    public static Single<ArrayList<Event>> rxGetRestaurantEvents(String restoId) {
        final FirebaseFirestore instance = FirestoreManager.getInstance().getFirestoreInstance();

        return Single.create(singleSubscriber -> {
            instance
                    .collection(StorageKeys.EVENTS)
                    .whereEqualTo(StorageKeys.RESTO_ID, restoId)
                    .get()
                    .addOnCompleteListener(task -> {
                        // check if profile set
                        if (task.isSuccessful()) {
                            try {
                                ArrayList<Event> events = generateItemsList(task.getResult());
                                singleSubscriber.onSuccess(events);
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

    public static Single<Boolean> rxAddEvent(String restoId, String eventMessage) {
        final FirebaseFirestore instance = FirestoreManager.getInstance().getFirestoreInstance();
        HashMap<String, Object> map = new HashMap<>();
        map.put(StorageKeys.RESTO_ID, restoId);
        map.put(StorageKeys.EVENT_MESSAGE, eventMessage);
        return Single.create(singleSubscriber -> {
            instance
                    .collection(StorageKeys.EVENTS)
                    .add(map)
                    .addOnCompleteListener(task -> {
                        // check if profile set
                        if (task.isSuccessful()) {
                            singleSubscriber.onSuccess(true);
                        } else {
                            // broadcast error
                            Exception exception = task.getException();
                            singleSubscriber.onError(exception);
                        }
                    });
        });
    }

    private synchronized static ArrayList<Event> generateItemsList(QuerySnapshot dataSnapshot) throws Exception {
        // init array
        ArrayList<Event> arrayItems = new ArrayList<>();
        // iterate to parse users
        Iterable<DocumentSnapshot> iterable = dataSnapshot.getDocuments();
        for (DocumentSnapshot snapshot: iterable) {
            // generate user
            Event toast = generateFeedback(snapshot);
            // add it to array
            arrayItems.add(toast);
        }
        return arrayItems;
    }

    private static Event generateFeedback(DocumentSnapshot snapshot) {
        return new Event(snapshot);
    }


}
