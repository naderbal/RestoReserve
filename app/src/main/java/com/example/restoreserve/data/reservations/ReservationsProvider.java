package com.example.restoreserve.data.reservations;

import com.example.restoreserve.data.FirestoreManager;
import com.example.restoreserve.data.StorageKeys;
import com.example.restoreserve.data.reservations.model.Reservation;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;

import rx.Single;

/**
 *
 */

public class ReservationsProvider {

    public static Single<String> rxReserveTable(Reservation reservation) {
        final FirebaseFirestore instance = FirestoreManager.getInstance().getFirestoreInstance();
        HashMap<String, Object> map = new HashMap<>();
        map.put(StorageKeys.RESTO_ID, reservation.getRestoId());
        map.put(StorageKeys.RESTO_NAME, reservation.getRestoName());
        map.put(StorageKeys.TABLE_ID, reservation.getTableId());
        map.put(StorageKeys.DATE, reservation.getDate());
        map.put(StorageKeys.TIME, reservation.getTime());
        map.put(StorageKeys.IS_CONFIRMED, reservation.isConfirmed());
        map.put(StorageKeys.CUSTOMER_ID, reservation.getCustomerId());
        map.put(StorageKeys.CUSTOMER_NAME, reservation.getCustomerName());
        map.put(StorageKeys.CUSTOMER_PHONE_NUMBER, reservation.getCustomerPhonenumber());

        return Single.create(singleSubscriber -> {
            instance
                    .collection(StorageKeys.RESERVATIONS)
                    .add(map)
                    .addOnCompleteListener(task -> {
                        // check if profile set
                        if (task.isSuccessful()) {
                            // check if user exists with checking if document exists
                            try {
                                singleSubscriber.onSuccess(task.getResult().getId());
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

    public static Single<Boolean> rxConfirmReservation(String id) {
        final FirebaseFirestore instance = FirestoreManager.getInstance().getFirestoreInstance();
        HashMap<String, Object> map = new HashMap<>();
        map.put(StorageKeys.IS_CONFIRMED, true);
        return Single.create(singleSubscriber -> {
            instance
                    .collection(StorageKeys.RESERVATIONS)
                    .document(id)
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

    public static Single<Boolean> rxCancelReservation(String id) {
        final FirebaseFirestore instance = FirestoreManager.getInstance().getFirestoreInstance();
        return Single.create(singleSubscriber -> {
            instance
                    .collection(StorageKeys.RESERVATIONS)
                    .document(id)
                    .delete()
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

    public static Single<ArrayList<Reservation>> rxGetReservationsAtDate(String restId, String date) {
        final FirebaseFirestore instance = FirestoreManager.getInstance().getFirestoreInstance();
        return Single.create(singleSubscriber -> {
            instance
                    .collection(StorageKeys.RESERVATIONS)
                    .whereEqualTo(StorageKeys.RESTO_ID, restId)
                    .whereEqualTo(StorageKeys.DATE, date)
                    .get()
                    .addOnCompleteListener(task -> {
                        // check if profile set
                        if (task.isSuccessful()) {
                            // check if user exists with checking if document exists
                            QuerySnapshot result = task.getResult();
                            if (result != null) {
                                try {
                                    singleSubscriber.onSuccess(generateItemsList(result));
                                } catch (Exception e) {
                                    singleSubscriber.onError(new Exception());
                                }
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

    public static Single<ArrayList<Reservation>> rxGetReservationOfUser(String userId) {
        final FirebaseFirestore instance = FirestoreManager.getInstance().getFirestoreInstance();
        return Single.create(singleSubscriber -> {
            instance
                    .collection(StorageKeys.RESERVATIONS)
                    .whereEqualTo(StorageKeys.CUSTOMER_ID, userId)
                    .get()
                    .addOnCompleteListener(task -> {
                        // check if profile set
                        if (task.isSuccessful()) {
                            // check if user exists with checking if document exists
                            QuerySnapshot result = task.getResult();
                            if (result != null) {
                                try {
                                    singleSubscriber.onSuccess(generateItemsList(result));
                                } catch (Exception e) {
                                    singleSubscriber.onError(new Exception());
                                }
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

    public static Single<ArrayList<Reservation>> rxGetReservationOfRestaurant(String restoId) {
        final FirebaseFirestore instance = FirestoreManager.getInstance().getFirestoreInstance();
        return Single.create(singleSubscriber -> {
            instance
                    .collection(StorageKeys.RESERVATIONS)
                    .whereEqualTo(StorageKeys.RESTO_ID, restoId)
                    .get()
                    .addOnCompleteListener(task -> {
                        // check if profile set
                        if (task.isSuccessful()) {
                            // check if user exists with checking if document exists
                            QuerySnapshot result = task.getResult();
                            if (result != null) {
                                try {
                                    singleSubscriber.onSuccess(generateItemsList(result));
                                } catch (Exception e) {
                                    singleSubscriber.onError(new Exception());
                                }
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

    private synchronized static ArrayList<Reservation> generateItemsList(QuerySnapshot dataSnapshot) throws Exception {
        // init array
        ArrayList<Reservation> arrayItems = new ArrayList<>();
        // iterate to parse users
        Iterable<DocumentSnapshot> iterable = dataSnapshot.getDocuments();
        for (DocumentSnapshot snapshot: iterable) {
            // generate user
            Reservation toast = generateResto(snapshot);
            // add it to array
            arrayItems.add(toast);
        }
        return arrayItems;
    }

    private static Reservation generateResto(DocumentSnapshot snapshot) {
        return new Reservation(snapshot);
    }
}
