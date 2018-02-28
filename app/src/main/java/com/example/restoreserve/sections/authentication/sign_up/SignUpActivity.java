package com.example.restoreserve.sections.authentication.sign_up;

import android.os.Bundle;
import android.widget.Toast;

import com.example.restoreserve.R;
import com.example.restoreserve.base.BaseActivity;
import com.example.restoreserve.data.authentication.AppAuthenticationManager;
import com.example.restoreserve.data.restaurant.model.Restaurant;
import com.example.restoreserve.data.user.User;
import com.example.restoreserve.sections.authentication.sign_up.customer.CustomerProfileFragment;
import com.example.restoreserve.sections.authentication.sign_up.restaurant.RestaurantProfileFragment;

import rx.SingleSubscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class SignUpActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        final boolean isCustomer = getIntent().getBooleanExtra("is_customer", false);
        if (isCustomer) {
            // fragment
            CustomerProfileFragment fragment = CustomerProfileFragment.newInstance(true);
            replaceFragment(R.id.vContainer, fragment, "fragment1");
            fragment.setListener(new CustomerProfileFragment.ProfileFragmentInteractionListener() {
                @Override
                public void onSubmitProfile(User user) {
                    submitCustomerRegistration(user);
                }
            });
        } else {
            // fragment
            RestaurantProfileFragment fragment = RestaurantProfileFragment.newInstance(true);
            replaceFragment(R.id.vContainer, fragment, "fragment2");
            fragment.setListener(new RestaurantProfileFragment.ProfileFragmentInteractionListener() {
                @Override
                public void onSubmitProfile(Restaurant restaurant, String password) {
                    submitRestaurantRegistration(restaurant, password);
                }
            });
        }
    }

    private void submitCustomerRegistration(User user) {
        showProgressDialog(R.string.loading);
        AppAuthenticationManager.rxRegisterUser(user)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleSubscriber<User>() {
            @Override
            public void onSuccess(User value) {
                dismissProgressDialog();
                Toast.makeText(getBaseContext(), "Registration Successful", Toast.LENGTH_SHORT).show();
                setResult(BaseActivity.RESULT_CODE_REGISTRATION_SUCCESSFUL);
                finish();
            }
            @Override
            public void onError(Throwable error) {
                dismissProgressDialog();
                if (error instanceof AppAuthenticationManager.PhoneNumberAlreadyExistsException) {
                    showToast("User with this phone number already exists");
                } else {
                    Toast.makeText(getBaseContext(), "Registration Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void submitRestaurantRegistration(Restaurant restaurant, String password) {
        showProgressDialog(R.string.loading);
        AppAuthenticationManager.rxRegisterRestaurant(restaurant, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleSubscriber<Restaurant>() {
            @Override
            public void onSuccess(Restaurant value) {
                dismissProgressDialog();
                Toast.makeText(getBaseContext(), "Registration Successful", Toast.LENGTH_SHORT).show();
                setResult(BaseActivity.RESULT_CODE_REGISTRATION_SUCCESSFUL);
                finish();
            }

            @Override
            public void onError(Throwable error) {
                dismissProgressDialog();
                if (error instanceof AppAuthenticationManager.PhoneNumberAlreadyExistsException) {
                    showToast("Restaurant with this phone number already exists");
                } else {
                    Toast.makeText(getBaseContext(), "Registration Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
