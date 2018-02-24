package com.example.restoreserve.sections.splash;

import android.content.Intent;

import com.example.restoreserve.base.BasePresenter;
import com.example.restoreserve.base.BaseView;


/**
 *
 */
public interface SplashContract {

    interface View extends BaseView<Presenter> {
        void openCustomerHome();

        void openWelcome();

        void close();

        void openSignUp();

        void openRestaurantHome();
    }

    interface Presenter extends BasePresenter {

        void activityResult(int requestCode, int resultCode, Intent data);
    }
}
