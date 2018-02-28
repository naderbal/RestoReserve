package com.example.restoreserve.utils;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Patterns;

import com.example.restoreserve.utils.ui.CustomInputSelectorView;

/**
 *
 */

public class InputValidationUtils {

    /**
     * Returns true if the given value is a valid name.
     */
    public static boolean validateName(String name) {
        return name!= null && name.length() > 2;
    }

    /**
     * Returns true if the given value is a valid email.
     */
    public static boolean validateEmail(@NonNull String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() && !email.contains("+");
    }

    /**
     * Returns true if the given value is an acceptable password.
     */
    public static boolean validatePassword(@NonNull String password) {
        return password.length() > 4;
    }

    public static boolean validaPhone(@NonNull String phone) {
        return phone.length() == 8;
    }

    public static boolean validateInputSelector(@NonNull String phone) {
        return !phone.equals(CustomInputSelectorView.DEFAULT_VALUE);
    }

    public static boolean validateStreet(String street) {
        return street.length() > 3;
    }

    public static boolean validateBuilding(String building) {
        return building.length() > 3;
    }

    public static boolean validateCity(String city) {
        return city.length() > 3;
    }

    public static boolean validateFloor(String floor) {
        return floor.length() >= 1;
    }

    public static boolean validateWebsite(String website) {
        return Patterns.WEB_URL.matcher(website).matches();
    }
}
