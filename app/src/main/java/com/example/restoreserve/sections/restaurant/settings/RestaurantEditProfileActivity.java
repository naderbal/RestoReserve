package com.example.restoreserve.sections.restaurant.settings;

import android.os.Bundle;
import android.widget.Toast;

import com.example.restoreserve.R;
import com.example.restoreserve.base.BaseActivity;
import com.example.restoreserve.data.authentication.AppAuthenticationManager;
import com.example.restoreserve.data.restaurant.model.Restaurant;
import com.example.restoreserve.data.session.AppSessionManager;
import com.example.restoreserve.data.user.User;
import com.example.restoreserve.sections.authentication.sign_up.customer.CustomerProfileFragment;
import com.example.restoreserve.sections.authentication.sign_up.restaurant.RestaurantProfileFragment;

import rx.SingleSubscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class RestaurantEditProfileActivity extends BaseActivity{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        // fragment
        RestaurantProfileFragment fragment = RestaurantProfileFragment.newInstance(false);
        replaceFragment(R.id.vContainer, fragment, "fragment");
        fragment.setListener(new RestaurantProfileFragment.ProfileFragmentInteractionListener() {
            @Override
            public void onSubmitProfile(Restaurant restaurant, String password) {
                submitEditProfile(restaurant);
            }
        });
    }

    private void submitEditProfile(Restaurant updatedRestaurant) {
        showProgressDialog(R.string.loading);
        final Restaurant restaurant = AppSessionManager.getInstance().getRestaurant();
        if (restaurant == null) {
            return;
        }
        AppAuthenticationManager.rxUpdateRestaurant(restaurant.getId(), updatedRestaurant)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleSubscriber<Restaurant>() {
                    @Override
                    public void onSuccess(Restaurant value) {
                        dismissProgressDialog();
                        Toast.makeText(getBaseContext(), "Update Successful", Toast.LENGTH_SHORT).show();
                        setResult(BaseActivity.RESULT_CODE_UPDATE_SUCCESSFUL);
                        finish();
                    }
                    @Override
                    public void onError(Throwable error) {
                        dismissProgressDialog();
                        Toast.makeText(getBaseContext(), "Update Failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
