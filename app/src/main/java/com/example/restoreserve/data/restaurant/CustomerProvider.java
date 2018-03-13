package com.example.restoreserve.data.restaurant;

import com.example.restoreserve.data.FirestoreManager;
import com.example.restoreserve.data.StorageKeys;
import com.example.restoreserve.data.user.User;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;

import rx.Single;

/**
 *
 */

public class CustomerProvider  {

    public static Single<ArrayList<User>> rxGetCustomers() {
        final FirebaseFirestore instance = FirestoreManager.getInstance().getFirestoreInstance();
        return Single.create(singleSubscriber -> {
            instance
                    .collection(StorageKeys.USERS)
                    .get()
                    .addOnCompleteListener(task -> {
                        // check if profile set
                        if (task.isSuccessful()) {
                            // check if user exists with checking if document exists
                            QuerySnapshot result = task.getResult();
                            if (result != null) {
                                try {
                                    singleSubscriber.onSuccess(generateCustomersList(result, false));
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

    public static Single<ArrayList<User>> rxGetBannedCustomers(String restoId) {
        final FirebaseFirestore instance = FirestoreManager.getInstance().getFirestoreInstance();
        return Single.create(singleSubscriber -> {
            instance
                    .collection(StorageKeys.BANNED_USERS)
                    .whereEqualTo(StorageKeys.RESTO_ID, restoId)
                    .get()
                    .addOnCompleteListener(task -> {
                        // check if profile set
                        if (task.isSuccessful()) {
                            // check if user exists with checking if document exists
                            QuerySnapshot result = task.getResult();
                            if (result != null) {
                                try {
                                    singleSubscriber.onSuccess(generateCustomersList(result, true));
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

    public static Single<ArrayList<String>> rxGetBannedOfCustomer(String userId) {
        final FirebaseFirestore instance = FirestoreManager.getInstance().getFirestoreInstance();
        return Single.create(singleSubscriber -> {
            instance
                    .collection(StorageKeys.BANNED_USERS)
                    .whereEqualTo(StorageKeys.ID, userId)
                    .get()
                    .addOnCompleteListener(task -> {
                        // check if profile set
                        if (task.isSuccessful()) {
                            // check if user exists with checking if document exists
                            QuerySnapshot result = task.getResult();
                            if (result != null) {
                                try {
                                    singleSubscriber.onSuccess(generateList(result));
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

    private static ArrayList<String> generateList(QuerySnapshot result) {
        ArrayList<String> list = new ArrayList<>();
        for (DocumentSnapshot documentSnapshot : result.getDocuments()) {
            list.add(documentSnapshot.getString(StorageKeys.RESTO_ID));
        }
        return list;
    }


    public static Single<Boolean> rxBanCustomer(User user, String restoId) {
        final HashMap<String, Object> userMap = user.toMap(user.getId());
        userMap.put(StorageKeys.RESTO_ID, restoId);
        final FirebaseFirestore instance = FirestoreManager.getInstance().getFirestoreInstance();
        return Single.create(singleSubscriber -> {
            instance
                    .collection(StorageKeys.BANNED_USERS)
                    .add(userMap)
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



    private synchronized static ArrayList<User> generateCustomersList(QuerySnapshot dataSnapshot, boolean innerId) throws Exception {
        // init array
        ArrayList<User> arrayItems = new ArrayList<>();
        // iterate to parse users
        Iterable<DocumentSnapshot> iterable = dataSnapshot.getDocuments();
        for (DocumentSnapshot snapshot: iterable) {
            // generate user
            User toast = generateCustomer(snapshot, innerId);
            // add it to array
            arrayItems.add(toast);
        }
        return arrayItems;
    }

    private static User generateCustomer(DocumentSnapshot snapshot, boolean innerId) {
        String userId;
        if (innerId) {
            userId = snapshot.getString(StorageKeys.ID);
        } else {
            userId = snapshot.getId();
        }
        return new User(userId, snapshot);
    }
}
