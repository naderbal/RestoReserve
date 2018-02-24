package com.example.restoreserve.sections.splash;

import android.content.Intent;

import com.example.restoreserve.base.BaseActivity;
import com.example.restoreserve.data.authentication.AppAuthenticationManager;
import com.example.restoreserve.data.restaurant.model.Restaurant;
import com.example.restoreserve.data.session.AppSessionManager;
import com.example.restoreserve.data.user.User;

import rx.Single;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 *
 */
public class SplashPresenter implements SplashContract.Presenter {

    SplashContract.View view;

    public SplashPresenter(SplashContract.View view) {
        this.view = view;
    }

    @Override
    public void start() {
        handleRedirection();
    }

    @Override
    public void activityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == BaseActivity.RESULT_CODE_LOG_OUT) {
            view.openWelcome();
            return;
        }
        switch (requestCode) {
            case BaseActivity.RC_HOME:
                view.close();
                break;
            case BaseActivity.RC_WELCOME:
                switch (resultCode) {
                    case BaseActivity.RESULT_CODE_LOGIN_SUCCESSFUL:
                        final AppSessionManager session = AppSessionManager.getInstance();
                        if (session.isUserLoggedIn()) {
                            view.openCustomerHome();
                        } else if (session.isRestaurantLoggedIn()) {
                            view.openRestaurantHome();
                        }
                        break;
                    case BaseActivity.RESULT_CODE_NEW_REGISTRATION:
                        view.openSignUp();
                        break;
                    case BaseActivity.RESULT_CODE_REGISTRATION_SUCCESSFUL:
                        if(AppSessionManager.getInstance().isUserLoggedIn()) {
                            view.openCustomerHome();
                        } else if(AppSessionManager.getInstance().isRestaurantLoggedIn()) {
                            view.openRestaurantHome();
                        }
                        break;
                    case BaseActivity.RESULT_CODE_GUEST:
                        view.openCustomerHome();
                        break;
                    default:
                        view.close();
                }
                break;
            case BaseActivity.RC_NEW_REGISTRATION:
                view.close();
                break;
        }
    }

    private void handleRedirection() {
        // check if there's a previously logged in user
        if (AppSessionManager.getInstance().isUserLoggedIn()) {
            syncUser();
        } else  if (AppSessionManager.getInstance().isRestaurantLoggedIn()){
            syncRestaurant();
        } else {
            // redirect to login
            view.openWelcome();
        }
    }

    private void syncUser() {
        try {
            // sync user
            Single<User> single = AppAuthenticationManager.rxSyncUser();
            Subscription subscription =
                    single.observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe(appUser -> {
                                // open home
                                view.openCustomerHome();
                            }, throwable -> {
                                if (throwable instanceof AppAuthenticationManager.UserNotFoundException) {
                                    // logout user
                                    AppSessionManager.getInstance().logout();
                                    // redirect to login
                                    view.openWelcome();
                                } else {
                                    // redirect to login
                                    view.openWelcome();
                                }
                            });
            // add to composite
        } catch (Exception e) {
            // shouldn't be any case
            view.openWelcome();
        }
    }

    private void syncRestaurant() {
        try {
            // sync user
            Single<Restaurant> single = AppAuthenticationManager.rxSyncRestaurant();
            Subscription subscription =
                    single.observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe(appUser -> {
                                // open home
                                view.openRestaurantHome();
                            }, throwable -> {
                                if (throwable instanceof AppAuthenticationManager.UserNotFoundException) {
                                    // logout user
                                    AppSessionManager.getInstance().logout();
                                    // redirect to login
                                    view.openWelcome();
                                } else {
                                    // redirect to login
                                    view.openWelcome();
                                }
                            });
            // add to composite
        } catch (Exception e) {
            // shouldn't be any case
            view.openWelcome();
        }
    }


}
