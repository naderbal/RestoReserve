package com.example.restoreserve.data.waitinglist;

import com.example.restoreserve.data.FirestoreManager;
import com.example.restoreserve.data.StorageKeys;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;

import rx.Single;

/**
 *
 */

public class WaitinglistProvider {

    public static Single<String> rxAddToWaitinglist(Waitinglist watinglist) {
        final FirebaseFirestore instance = FirestoreManager.getInstance().getFirestoreInstance();
        HashMap<String, Object> map = new HashMap<>();
        map.put(StorageKeys.RESTO_ID, watinglist.getRestoId());
        map.put(StorageKeys.CUSTOMER_ID, watinglist.getCustomerId());
        map.put(StorageKeys.RESTO_NAME, watinglist.getRestoName());
        map.put(StorageKeys.TIME, watinglist.getTime());

        return Single.create(singleSubscriber -> {
            instance
                    .collection(StorageKeys.WAITINLIST)
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

    public static Single<Boolean> rxCancelWaitinglist(String id) {
        final FirebaseFirestore instance = FirestoreManager.getInstance().getFirestoreInstance();
        return Single.create(singleSubscriber -> {
            instance
                    .collection(StorageKeys.WAITINLIST)
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

    private synchronized static ArrayList<Waitinglist> generateItemsList(QuerySnapshot dataSnapshot) throws Exception {
        // init array
        ArrayList<Waitinglist> arrayItems = new ArrayList<>();
        // iterate to parse users
        Iterable<DocumentSnapshot> iterable = dataSnapshot.getDocuments();
        for (DocumentSnapshot snapshot: iterable) {
            // generate user
            Waitinglist toast = generateWaitinglist(snapshot);
            // add it to array
            arrayItems.add(toast);
        }
        return arrayItems;
    }

    private static Waitinglist generateWaitinglist(DocumentSnapshot snapshot) {
        return new Waitinglist(snapshot);
    }
}
