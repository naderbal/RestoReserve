package com.example.restoreserve.sections.splash;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.restoreserve.R;
import com.example.restoreserve.base.BaseActivity;
import com.example.restoreserve.sections.authentication.sign_up.SignUpActivity;
import com.example.restoreserve.sections.authentication.welcome.WelcomeActivity;
import com.example.restoreserve.sections.customer.home.HomeActivity;
import com.example.restoreserve.sections.restaurant.RestaurantHomeActivity;


public class SplashActivity extends AppCompatActivity implements SplashContract.View {

    SplashContract.Presenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        // set presenter
        setPresenter(new SplashPresenter(this));
        presenter.start();
    }

    @Override
    public void setPresenter(SplashContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void openCustomerHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivityForResult(intent, BaseActivity.RC_HOME);
    }

    @Override
    public void openWelcome() {
        Intent intent = new Intent(this, WelcomeActivity.class);
        startActivityForResult(intent, BaseActivity.RC_WELCOME);
    }

    @Override
    public void close() {
        finish();
    }

    @Override
    public void openSignUp() {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivityForResult(intent, BaseActivity.RC_NEW_REGISTRATION);
    }

    @Override
    public void openRestaurantHome() {
        Intent intent = new Intent(this, RestaurantHomeActivity.class);
        startActivityForResult(intent, BaseActivity.RC_WELCOME);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        presenter.activityResult(requestCode, resultCode, data);
    }
}
