package com.example.restoreserve.sections.customer.home.settings;

import android.os.Bundle;
import android.widget.Toast;


import com.example.restoreserve.R;
import com.example.restoreserve.base.BaseActivity;
import com.example.restoreserve.data.authentication.AppAuthenticationManager;
import com.example.restoreserve.data.session.AppSessionManager;
import com.example.restoreserve.data.user.User;
import com.example.restoreserve.sections.authentication.sign_up.customer.CustomerProfileFragment;

import rx.SingleSubscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class EditProfileActivity extends BaseActivity{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        // fragment
        CustomerProfileFragment fragment = CustomerProfileFragment.newInstance(false);
        replaceFragment(R.id.vContainer, fragment, "fragment");
        fragment.setListener(new CustomerProfileFragment.ProfileFragmentInteractionListener() {
            @Override
            public void onSubmitProfile(User user) {
                submitEditProfile(user);
            }
        });
    }

    private void submitEditProfile(User user) {
        showProgressDialog(R.string.loading);
        final User user1 = AppSessionManager.getInstance().getUser();
        if (user1 == null) {
            return;
        }
        AppAuthenticationManager.rxUpdateUser(user1.getId(), user)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleSubscriber<User>() {
                    @Override
                    public void onSuccess(User value) {
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
