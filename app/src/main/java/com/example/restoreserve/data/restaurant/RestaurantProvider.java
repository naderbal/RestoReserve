package com.example.restoreserve.data.restaurant;

import com.example.restoreserve.data.FirestoreManager;
import com.example.restoreserve.data.StorageKeys;
import com.example.restoreserve.data.restaurant.model.Restaurant;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import rx.Single;

/**
 *
 */
public class RestaurantProvider {

    public static Single<ArrayList<Restaurant>> rxGetRestos() {
        final FirebaseFirestore instance = FirestoreManager.getInstance().getFirestoreInstance();
        return Single.create(singleSubscriber -> {
            instance
                    .collection(StorageKeys.RESTAURANTS)
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

    private synchronized static ArrayList<Restaurant> generateItemsList(QuerySnapshot dataSnapshot) throws Exception {
        // init array
        ArrayList<Restaurant> arrayItems = new ArrayList<>();
        // iterate to parse users
        Iterable<DocumentSnapshot> iterable = dataSnapshot.getDocuments();
        for (DocumentSnapshot snapshot: iterable) {
            // generate user
            Restaurant toast = generateResto(snapshot);
            // add it to array
            arrayItems.add(toast);
        }
        return arrayItems;
    }

    private static Restaurant generateResto(DocumentSnapshot snapshot) {
        return new Restaurant(snapshot);
    }

}
