package com.example.restoreserve.data;

import com.google.firebase.firestore.FirebaseFirestore;

/**
 *
 */

public class FirestoreManager {

    private FirebaseFirestore firestoreInstance;
    private static FirestoreManager instance;

    private FirestoreManager() {
        firestoreInstance = FirebaseFirestore.getInstance();
    }

    public static FirestoreManager getInstance() {
        if (instance == null) {
            instance = new FirestoreManager();
        }
        return instance;
    }

    public FirebaseFirestore getFirestoreInstance() {
        return firestoreInstance;
    }
}
