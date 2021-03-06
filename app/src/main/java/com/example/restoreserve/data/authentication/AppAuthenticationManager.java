package com.example.restoreserve.data.authentication;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.restoreserve.data.FirestoreManager;
import com.example.restoreserve.data.StorageKeys;
import com.example.restoreserve.data.restaurant.model.Restaurant;
import com.example.restoreserve.data.session.AppSessionManager;
import com.example.restoreserve.data.user.User;
import com.example.restoreserve.utils.DateHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import rx.Single;


/**
 * <p>
 *     Class providing authentication logic, like
 *     login and registration.
 * </p>
 */
public class AppAuthenticationManager {
    /**
     * Returns true if there's a current authenticated user with
     * Firebase, false otherwise.
     */
    public static boolean hasAuthenticatedUser() {
        return FirebaseAuth.getInstance().getCurrentUser() != null;
    }

    /**
     * Returns the id of the current authenticated user
     * with Firebase, false if there's no previous authenticated
     * session.
     */
    public static String getAuthenticatedUserId() {
        // get auth user
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        // return its id if valid
        return firebaseUser != null? firebaseUser.getUid(): null;
    }

    // REGISTRATION //

    /**
     * Returns a {@link Single} that will execute a new registration request.
     * Upon success, it will emit the registered user.
     * @param registrationUser The user data to be registered.
     */
    public static Single<User> rxRegisterUser(@NonNull User registrationUser) {
        // get email/password
        String email = registrationUser.getEmail();
        String password = registrationUser.getPassword();
        // auth request
        return rxFirebaseCheckUserCredentials(null, registrationUser.getPhoneNumber(), registrationUser.getName())
                .flatMap(aBoolean -> {
                    if (aBoolean) {
                        return rxFirebaseRegister(email, password).flatMap(id -> rxStoreUser(id, registrationUser));
                    } else {
                        return Single.error(new PhoneNumberAlreadyExistsException());
                    }
                });
    }

    /**
     * Returns a {@link Single} that will execute a new registration request.
     * Upon success, it will emit the registered user.
     * @param registrationUser The user data to be registered.
     */
    public static Single<Restaurant> rxRegisterRestaurant(@NonNull Restaurant registrationUser, @NonNull String password) {
        // get email/password
        String email = registrationUser.getEmail();
        return rxFirebaseCheckRestaurantCredentials(registrationUser.getId(), registrationUser.getPhoneNumber(), registrationUser.getName())
                .flatMap(aBoolean -> {
                    if (aBoolean) {
                        return rxFirebaseRegister(email, password).flatMap(id -> rxStoreRestaurant(id, registrationUser));
                    } else {
                        return Single.error(new PhoneNumberAlreadyExistsException());
                    }
                });
    }

    public static Single<User> rxUpdateUser(String id, User updatedUser) {
        return rxFirebaseUpdateCheckUserCredentials(id, updatedUser.getPhoneNumber(), updatedUser.getName())
                .flatMap(aBoolean ->  {
                    if (aBoolean) {
                        return Single.create(singleSubscriber -> {
                            // Initialize Firestore
                            FirestoreManager.getInstance().getFirestoreInstance()
                                    .collection("users")
                                    .document(id)
                                    .set(updatedUser.toMap(id), SetOptions.merge())
                                    .addOnCompleteListener(task -> {
                                        // check if profile set
                                        if (task.isSuccessful()) {
                                            singleSubscriber.onSuccess(null);
                                        } else {
                                            // broadcast error
                                            Exception exception = task.getException();
                                            singleSubscriber.onError(exception);
                                        }
                                    });
                        }).flatMap(v -> rxGetUser(id));
                    } else {
                        return Single.error(new PhoneNumberAlreadyExistsException());
                    }
        });
    }

    public static Single<Restaurant> rxUpdateRestaurant(String id, Restaurant updatedRestaurant) {
        return rxFirebaseUpdateCheckRestaurantCredentials(id, updatedRestaurant.getPhoneNumber(), updatedRestaurant.getName())
                .flatMap(aBoolean -> {
                    if (aBoolean) {
                        return Single.create(singleSubscriber -> {
                            // Initialize Firestore
                            FirestoreManager.getInstance().getFirestoreInstance()
                                    .collection("restaurants")
                                    .document(id)
                                    .set(updatedRestaurant.toEditMap(id), SetOptions.merge())
                                    .addOnCompleteListener(task -> {
                                        // check if profile set
                                        if (task.isSuccessful()) {
                                            singleSubscriber.onSuccess(null);
                                        } else {
                                            // broadcast error
                                            Exception exception = task.getException();
                                            singleSubscriber.onError(exception);
                                        }
                                    });
                        }).flatMap(v -> rxGetRestaurant(id));
                    } else {
                        return Single.error(new PhoneNumberAlreadyExistsException());
                    }
                });
    }

    /**
     * Returns a {@link Single} that will execute a {@link FirebaseAuth} registration
     * request. Upon success, it will emit the registered {@link FirebaseUser} uid.
     * @param email New user's account email.
     * @param password New user's account password.
     */
    private static Single<String> rxFirebaseRegister(@NonNull String email, @NonNull String password) {
        return Single.create(singleSubscriber ->
                // trigger Firebase registration request
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            // check if resulting task was successful
                            if (task.isSuccessful()) {
                                // get authenticated user
                                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                                if (firebaseUser != null) {
                                    // get user id
                                    String id = firebaseUser.getUid();
                                    // broadcast result
                                    singleSubscriber.onSuccess(id);
                                } else {
                                    singleSubscriber.onError(new Exception());
                                }
                            } else {
                                Exception exception = task.getException();
                                singleSubscriber.onError(exception);
                            }
                        })
        );
    }

    /**
     * Returns a {@link Single} that will execute a {@link FirebaseAuth} registration
     * request. Upon success, it will emit the registered {@link FirebaseUser} uid.
     */
    private static Single<Boolean> rxFirebaseCheckUserCredentials(@Nullable String id, @NonNull String phoneNumber, String name) {
        return Single.create(singleSubscriber ->
                // trigger Firebase registration request
                FirestoreManager.getInstance()
                        .getFirestoreInstance()
                        .collection("users")
                        .get()
                        .addOnCompleteListener(task -> {
                            // check if resulting task was successful
                            if (task.isSuccessful()) {
                                Iterable<DocumentSnapshot> iterable = task.getResult().getDocuments();
                                for (DocumentSnapshot snapshot: iterable) {
                                    // generate user
                                    User user = new User(snapshot.getId(), snapshot);
                                    if (user.getPhoneNumber().equals(phoneNumber)) {
                                        singleSubscriber.onError(new PhoneNumberAlreadyExistsException());
                                        return;
                                    }
                                    if ((id !=null && !id.equals(user.getId())) && user.getName().equals(name)) {
                                        singleSubscriber.onError(new NameAlreadyExistsException());
                                        return;
                                    }
                                }
                                singleSubscriber.onSuccess(true);
                            } else {
                                singleSubscriber.onSuccess(false);
                            }
                        })
        );
    }

    /**
     * Returns a {@link Single} that will execute a {@link FirebaseAuth} registration
     * request. Upon success, it will emit the registered {@link FirebaseUser} uid.
     */
    private static Single<Boolean> rxFirebaseUpdateCheckUserCredentials(@NonNull String id, @NonNull String phoneNumber, String name) {
        return Single.create(singleSubscriber ->
                // trigger Firebase registration request
                FirestoreManager.getInstance()
                        .getFirestoreInstance()
                        .collection("users")
                        .get()
                        .addOnCompleteListener(task -> {
                            // check if resulting task was successful
                            if (task.isSuccessful()) {
                                Iterable<DocumentSnapshot> iterable = task.getResult().getDocuments();
                                for (DocumentSnapshot snapshot: iterable) {
                                    // generate user
                                    User user = new User(snapshot.getId(), snapshot);
                                    if (!id.equals(user.getId()) && user.getPhoneNumber().equals(phoneNumber)) {
                                        singleSubscriber.onError(new PhoneNumberAlreadyExistsException());
                                        return;
                                    }
                                    if (!id.equals(user.getId()) && user.getName().equals(name)) {
                                        singleSubscriber.onError(new NameAlreadyExistsException());
                                        return;
                                    }
                                }
                                singleSubscriber.onSuccess(true);
                            } else {
                                singleSubscriber.onSuccess(false);
                            }
                        })
        );
    }

    /**
     * Returns a {@link Single} that will execute a {@link FirebaseAuth} registration
     * request. Upon success, it will emit the registered {@link FirebaseUser} uid.
     */
    private static Single<Boolean> rxFirebaseCheckRestaurantCredentials(@Nullable String id, @NonNull String phoneNumber, String name) {
        return Single.create(singleSubscriber ->
                // trigger Firebase registration request
                FirestoreManager.getInstance()
                        .getFirestoreInstance()
                        .collection("restaurants")
                        .get()
                        .addOnCompleteListener(task -> {
                            // check if resulting task was successful
                            if (task.isSuccessful()) {
                                Iterable<DocumentSnapshot> iterable = task.getResult().getDocuments();
                                for (DocumentSnapshot snapshot: iterable) {
                                    // generate user
                                    Restaurant restaurant = new Restaurant(snapshot);
                                    final String phoneNumber1 = restaurant.getPhoneNumber();
                                    final String name1 = restaurant.getName();
                                    if ((id !=null && !id.equals(restaurant.getId())) && phoneNumber1 !=null && phoneNumber1.equals(phoneNumber)) {
                                        singleSubscriber.onError(new PhoneNumberAlreadyExistsException());
                                        return;
                                    }
                                    if ((id != null && !id.equals(restaurant.getId())) && name1 != null && name1.equals(name)) {
                                        singleSubscriber.onError(new NameAlreadyExistsException());
                                        return;
                                    }
                                }
                                singleSubscriber.onSuccess(true);
                            } else {
                                singleSubscriber.onSuccess(false);
                            }
                        })
        );
    }

    /**
     * Returns a {@link Single} that will execute a {@link FirebaseAuth} registration
     * request. Upon success, it will emit the registered {@link FirebaseUser} uid.
     */
    private static Single<Boolean> rxFirebaseUpdateCheckRestaurantCredentials(@NonNull String id, @NonNull String phoneNumber, String name) {
        return Single.create(singleSubscriber ->
                // trigger Firebase registration request
                FirestoreManager.getInstance()
                        .getFirestoreInstance()
                        .collection("restaurants")
                        .get()
                        .addOnCompleteListener(task -> {
                            // check if resulting task was successful
                            if (task.isSuccessful()) {
                                Iterable<DocumentSnapshot> iterable = task.getResult().getDocuments();
                                for (DocumentSnapshot snapshot: iterable) {
                                    // generate user
                                    Restaurant restaurant = new Restaurant(snapshot);
                                    final String phoneNumber1 = restaurant.getPhoneNumber();
                                    final String name1 = restaurant.getName();
                                    if (!id.equals(restaurant.getId()) && phoneNumber1 !=null && phoneNumber1.equals(phoneNumber)) {
                                        singleSubscriber.onError(new PhoneNumberAlreadyExistsException());
                                        return;
                                    }
                                    if (!id.equals(restaurant.getId()) && name1 != null && name1.equals(name)) {
                                        singleSubscriber.onError(new NameAlreadyExistsException());
                                        return;
                                    }
                                }
                                singleSubscriber.onSuccess(true);
                            } else {
                                singleSubscriber.onSuccess(false);
                            }
                        })
        );
    }

    /**
     * Returns a {@link Single} that will store a new user in the {@link FirebaseFirestore}.
     * Upon success, it will emit the stored user. Note that any previous user with the
     * same id will be overridden.
     * @param id The id of the user.
     * @param user The user data to be stored.
     */
    private static Single<User> rxStoreUser(String id, User user) {
        return Single.create(singleSubscriber -> {
            // Initialize Firestore
            FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(id)
                    .set(user.toMap(id))
                    .addOnCompleteListener(task -> {
                        // check if profile set
                        if (task.isSuccessful()) {
                            // inform session manager
                            AppSessionManager.getInstance().setUser(user);
                            // broadcast user
                            singleSubscriber.onSuccess(user);
                        } else {
                            // broadcast error
                            Exception exception = task.getException();
                            singleSubscriber.onError(exception);
                        }
                    });
        });
    }

    private static Single<Restaurant> rxStoreRestaurant(String id, Restaurant restaurant) {
        return Single.create(singleSubscriber -> {
            // Initialize Firestore
            final HashMap<String, Object> map = restaurant.toMap(id);
            map.put(StorageKeys.IS_APPROVED, false);
            map.put(StorageKeys.IS_PAID, false);
//            final Date time = Calendar.getInstance().getTime();
//            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss", Locale.ENGLISH);
//            sdf.format(time);
            map.put("expire_date", FieldValue.serverTimestamp());
            FirebaseFirestore.getInstance()
                    .collection("restaurants")
                    .document(id)
                    .set(map)
                    .addOnCompleteListener(task -> {
                        // check if profile set
                        if (task.isSuccessful()) {
                            // inform session manager
                            AppSessionManager.getInstance().setRestaurant(restaurant);
                            // broadcast user
                            singleSubscriber.onSuccess(restaurant);
                        } else {
                            // broadcast error
                            Exception exception = task.getException();
                            singleSubscriber.onError(exception);
                        }
                    });
        });
    }

    // LOGIN //

    /**
     * Returns a {@link Single} that will execute a login request.
     * Upon success, it will emit the logged in user.
     * @param email The user account's email.
     * @param password The user account's password.
     */
    public static Single<User> rxUserLogin(@NonNull String email, @NonNull String password) {
        // auth request
        Single<String> singleAuthLogin = rxFirebaseLogin(email, password);
        // flat
        return singleAuthLogin.flatMap(id -> rxGetUser(id));
    }

    public static Single<Restaurant> rxRestaurantLogin(@NonNull String email, @NonNull String password) {
        // auth request
        Single<String> singleAuthLogin = rxFirebaseLogin(email, password);
        // flat
        return singleAuthLogin.flatMap(id -> rxGetRestaurant(id));
    }

    /**
     * Returns a {@link Single} that will execute a {@link FirebaseAuth} credentials
     * login request. Upon success, it will emit the registered {@link FirebaseUser} uid.
     * @param email The user account's email.
     * @param password The user account's password.
     */
    private static Single<String> rxFirebaseLogin(@NonNull String email, @NonNull String password) {
        return Single.create(singleSubscriber -> {
            FirebaseAuth.getInstance()
                    .signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        // check if resulting task was successful
                        if (task.isSuccessful()) {
                            // get registered user
                            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                            if (firebaseUser != null) {
                                // get user id
                                String id = firebaseUser.getUid();
                                // broadcast result
                                singleSubscriber.onSuccess(id);
                            } else {
                                singleSubscriber.onError(new Exception());
                            }
                        } else {
                            Exception exception = task.getException();
                            singleSubscriber.onError(exception);
                        }
                    });
        });
    }

    /**
     * Returns a {@link Single} that will fetch a user profile from
     * {@link FirebaseFirestore}. Upon success, it will emit the request
     * user object.
     * @param id The user's id.
     */
    private static Single<User> rxGetUser(@NonNull String id) {
        return Single.create(singleSubscriber -> {
            FirestoreManager.getInstance().getFirestoreInstance()
                    .collection("users")
                    .document(id)
                    .get().addOnCompleteListener(task -> {
                            // check if profile set
                            if (task.isSuccessful()) {
                                // check if user exists with checking if document exists
                                if (task.getResult().exists()) {
                                    // generate stored user (to avoid fetching it again)
                                    User user = new User(id, task.getResult());
                                    // inform session manager
                                    AppSessionManager.getInstance().setUser(user);
                                    // broadcast user
                                    singleSubscriber.onSuccess(user);
                                } else {
                                    singleSubscriber.onError(new AccountNotFoundException());
                                }
                            } else {
                                // broadcast error
                                Exception exception = task.getException();
                                singleSubscriber.onError(exception);
                            }
                        });
        });
    }

    private static Single<Restaurant> rxGetRestaurant(@NonNull String id) {
        return Single.create(singleSubscriber -> {
            FirestoreManager.getInstance().getFirestoreInstance()
                    .collection("restaurants")
                    .document(id)
                    .get().addOnCompleteListener(task -> {
                            // check if profile set
                            if (task.isSuccessful()) {
                                // check if user exists with checking if document exists
                                if (task.getResult().exists()) {
                                    // generate stored user (to avoid fetching it again)
                                    Restaurant restaurant = new Restaurant(task.getResult());
                                    // inform session manager
                                    AppSessionManager.getInstance().setRestaurant(restaurant);
                                    // broadcast user
                                    singleSubscriber.onSuccess(restaurant);
                                } else {
                                    singleSubscriber.onError(new AccountNotFoundException());
                                }
                            } else {
                                // broadcast error
                                Exception exception = task.getException();
                                singleSubscriber.onError(exception);
                            }
                        });
        });
    }

    public static Single<Boolean> deactivateUser(String userId) {
        return Single.create(singleSubscriber -> {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user == null) {
                singleSubscriber.onError(new Exception());
                return;
            }
            user.delete()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                singleSubscriber.onSuccess(true);
                            } else {
                                singleSubscriber.onError(new Exception());
                            }
                        }
                    });
        }).flatMap(a -> rxDeleteUser(userId));
    }

    public static Single<Boolean> deactivateRestaurant(String restId) {
        return Single.create(singleSubscriber -> {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user == null) {
                singleSubscriber.onError(new Exception());
                return;
            }
            user.delete()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                singleSubscriber.onSuccess(true);
                            } else {
                                singleSubscriber.onError(new Exception());
                            }
                        }
                    });
        }).flatMap(a -> rxDeleteRestaurant(restId));
    }

    @NonNull
    private static Single<Boolean> rxDeleteUser(String userId) {
        return Single.create(singleSubscriber -> {
            FirestoreManager.getInstance().getFirestoreInstance()
                    .collection("users")
                    .document(userId)
                    .delete()
                    .addOnCompleteListener(task -> {
                        singleSubscriber.onSuccess(true);
                    });
                });
    }

    @NonNull
    private static Single<Boolean> rxDeleteRestaurant(String restId) {
        return Single.create(singleSubscriber -> {
            FirestoreManager.getInstance().getFirestoreInstance()
                    .collection("restaurants")
                    .document(restId)
                    .delete()
                    .addOnCompleteListener(task -> {
                        singleSubscriber.onSuccess(true);
                    });
                });
    }


    public static class AccountNotFoundException extends Exception {

    }

    // LOGOUT //

    /**
     * Logs out the user and clears any active session.
     */
    public static void logout() {
        // request auth logout
        FirebaseAuth.getInstance().signOut();
        // inform session manager
        AppSessionManager.getInstance().logout();
    }

    // SYNC //

    /**
     * Returns a {@link Single} that fetches the currently authenticated
     * user object from {@link FirebaseFirestore}. Upon success, it will
     * emit the fetched user.
     * @throws Exception if there's no authenticated session.
     */
    public static Single<User> rxSyncUser() throws Exception {
        // get user id
        String uid = getAuthenticatedUserId();
        // validate
        if (uid == null) {
            throw new Exception();
        }
        // fetch user
        return rxGetUser(uid);
    }
    public static Single<Restaurant> rxSyncRestaurant() throws Exception {
        // get user id
        String uid = getAuthenticatedUserId();
        // validate
        if (uid == null) {
            throw new Exception();
        }
        // fetch user
        return rxGetRestaurant(uid);
    }

    // EXCEPTIONS //

    public static class UserNotFoundException extends Exception {
        public UserNotFoundException() {
            super("User not found");
        }
    }

    public static class PhoneNumberAlreadyExistsException extends Exception {
        public PhoneNumberAlreadyExistsException() {
            super("phone number already exists");
        }
    }

    public static class NameAlreadyExistsException extends Exception {
        public NameAlreadyExistsException() {
            super("Name already exists");
        }
    }
}
