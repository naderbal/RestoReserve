package com.example.restoreserve.data.user;

import android.support.annotation.NonNull;

import com.example.restoreserve.data.StorageKeys;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.HashMap;

/**
 *
 */

public class User {
    private String id;
    private String name;
    private String email;
    private String password;
    private String phoneNumber;

    public User(String name, String email, String phoneNumber) {
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }


    public User(String id, DocumentSnapshot result) {
        this.id = id;
        if (result.contains(StorageKeys.NAME)) {
            name = (String) result.get(StorageKeys.NAME);
        }
        if (result.contains(StorageKeys.EMAIL)) {
            email = (String) result.get(StorageKeys.EMAIL);
        }
        if (result.contains(StorageKeys.PHONE_NUMBER)) {
            phoneNumber = (String) result.get(StorageKeys.PHONE_NUMBER);
        }
    }

    public User(String name, String email, String phone, String password) {
        this.name = name;
        this.email = email;
        this.phoneNumber = phone;
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public HashMap<String, Object> toMap(@NonNull String id) {
        HashMap<String, Object> map = new HashMap<>();
        map.put(StorageKeys.ID, id);
        map.put(StorageKeys.EMAIL, email);
        map.put(StorageKeys.NAME, name);
        map.put(StorageKeys.PHONE_NUMBER, phoneNumber);
        return map;
    }
}
