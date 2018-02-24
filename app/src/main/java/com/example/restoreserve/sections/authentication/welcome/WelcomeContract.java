package com.example.restoreserve.sections.authentication.welcome;

import android.content.Intent;
import android.support.annotation.NonNull;

import com.example.restoreserve.base.BasePresenter;
import com.example.restoreserve.base.BaseView;


/**
 *
 */
public interface WelcomeContract {
    interface View extends BaseView<Presenter> {
        void redirectToSplash(int resultCode);

        void showLoginError();

        void showLoading();

        void hideLoading();

        void showLoginWrongCredentials();

        void openCustomerSignUp();

        void showInvalidEmail();

        void showInvalidPassword();

        void openRestaurantSignUp();
    }

    interface Presenter extends BasePresenter {
        void onLoginClicked(boolean isCustomer, @NonNull String phone, @NonNull String password);

        void onSignUpClicked(boolean isCustomer);

        void onGuestClicked();

        void activityResult(int requestCode, int resultCode, Intent data);
    }
}
