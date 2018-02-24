package com.example.restoreserve.sections.authentication.welcome;

import android.content.Intent;
import android.support.annotation.NonNull;


import com.example.restoreserve.base.BaseActivity;
import com.example.restoreserve.data.authentication.AppAuthenticationManager;
import com.example.restoreserve.data.restaurant.model.Restaurant;
import com.example.restoreserve.data.user.User;
import com.example.restoreserve.utils.InputValidationUtils;

import rx.SingleSubscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 *
 */

public class WelcomePresenter implements WelcomeContract.Presenter {

    WelcomeContract.View view;

    public WelcomePresenter(WelcomeContract.View view) {
        this.view = view;
    }

    @Override
    public void start() {

    }

    @Override
    public void onLoginClicked(boolean isCustomer, @NonNull String email, @NonNull String password) {
        if (!emailValid(email)) {
            view.showInvalidEmail();
            return;
        }
        if (!passwordlValid(password)) {
            view.showInvalidPassword();
            return;
        }
        if (isCustomer) {
            loginCustomer(email, password);
        } else {
            loginRestaurant(email, password);
        }
    }

    private void loginCustomer(@NonNull String email, @NonNull String password) {
        view.showLoading();
        AppAuthenticationManager.rxUserLogin(email, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleSubscriber<User>() {
                    @Override
                    public void onSuccess(User user) {
                        view.hideLoading();
                        view.redirectToSplash(BaseActivity.RESULT_CODE_LOGIN_SUCCESSFUL);
                    }

                    @Override
                    public void onError(Throwable error) {
                        view.hideLoading();
                        if (error instanceof AppAuthenticationManager.AccountNotFoundException) {
                            view.showLoginWrongCredentials();
                        } else {
                            view.showLoginError();
                        }
                    }
                });
    }

    private void loginRestaurant(@NonNull String email, @NonNull String password) {
        view.showLoading();
        AppAuthenticationManager.rxRestaurantLogin(email, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleSubscriber<Restaurant>() {
                    @Override
                    public void onSuccess(Restaurant user) {
                        view.hideLoading();
                        view.redirectToSplash(BaseActivity.RESULT_CODE_LOGIN_SUCCESSFUL);
                    }

                    @Override
                    public void onError(Throwable error) {
                        view.hideLoading();
                        if (error instanceof AppAuthenticationManager.AccountNotFoundException) {
                            view.showLoginWrongCredentials();
                        } else {
                            view.showLoginError();
                        }
                    }
                });
    }

    private boolean emailValid(String email) {
        return InputValidationUtils.validateEmail(email);
    }

    private boolean passwordlValid(String password) {
        return InputValidationUtils.validatePassword(password);
    }

    @Override
    public void onSignUpClicked(boolean isCustomer) {
        if (isCustomer) {
            view.openCustomerSignUp();
        } else {
            view.openRestaurantSignUp();
        }
    }

    @Override
    public void onGuestClicked() {
        view.redirectToSplash(BaseActivity.RESULT_CODE_GUEST);
    }

    @Override
    public void activityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case BaseActivity.RC_NEW_REGISTRATION:
                if (resultCode == BaseActivity.RESULT_CODE_REGISTRATION_SUCCESSFUL) {
                    view.redirectToSplash(BaseActivity.RESULT_CODE_REGISTRATION_SUCCESSFUL);
                }
                break;
        }
    }
}
