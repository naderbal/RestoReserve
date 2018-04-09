package com.example.restoreserve.sections.restaurant.settings;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.restoreserve.R;
import com.example.restoreserve.base.BaseActivity;
import com.example.restoreserve.base.BaseFragment;
import com.example.restoreserve.data.authentication.AppAuthenticationManager;
import com.example.restoreserve.data.session.AppSessionManager;
import com.example.restoreserve.sections.authentication.welcome.WelcomeActivity;
import com.example.restoreserve.sections.restaurant.settings.banned.BannedCustomersActivity;

import rx.SingleSubscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class RestaurantSettingsFragment extends BaseFragment {
    final String strLogout = "Log Out";
    final String strSignUp = "Sign Up / Sign In";
    TextView tvProfile;
    TextView tvAboutUs;
    TextView tvLogout;
    TextView tvBannedlist;
    TextView tvDeactivate;

    public RestaurantSettingsFragment() {
    }

    public static RestaurantSettingsFragment newInstance() {
        return new RestaurantSettingsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_restaurant_settings, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        setupLogoutUI();
        tvProfile.setOnClickListener(v -> {
            if (AppSessionManager.getInstance().isRestaurantLoggedIn()) {
                startEditProfile();
            } else {
                startAuthentication();
            }
        });
        tvLogout.setOnClickListener(v -> {
            if (tvLogout.getText().equals(strLogout)) {
                showAlert("Are you sure you want to logout", this::logout);
            } else {
                startAuthentication();
            }
        });
        tvAboutUs.setOnClickListener(v -> {
            openAboutUs();
        });
        tvBannedlist.setOnClickListener(v -> {
            openBannedList();
        });
        tvDeactivate.setOnClickListener(v -> {
            showAlert("Are you sure you want to deactivate your account", this::deactivate);
        });
    }

    private void deactivate() {
        showProgressDialog("Deactivating");
        final String id = AppSessionManager.getInstance().getRestaurant().getId();
        AppAuthenticationManager.deactivateRestaurant(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleSubscriber<Boolean>() {
                    @Override
                    public void onSuccess(Boolean aBoolean) {
                        dismissProgressDialog();
                        logout();
                    }

                    @Override
                    public void onError(Throwable error) {
                        dismissProgressDialog();
                        showToast("Something went wrong");
                    }
                });
    }

    private void openBannedList() {
        Intent intent = new Intent(getActivity(), BannedCustomersActivity.class);
        startActivity(intent);
    }

    private void openAboutUs() {
        Intent intent = new Intent(getActivity(), AboutUsActivity.class);
        startActivity(intent);
    }

    private void startAuthentication() {
        Intent intent = new Intent(getActivity(), WelcomeActivity.class);
        startActivityForResult(intent, BaseActivity.RC_WELCOME);
    }

    private void startEditProfile() {
        Intent intent = new Intent(getActivity(), RestaurantEditProfileActivity.class);
        startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BaseActivity.RC_WELCOME) {
            setupLogoutUI();
        }
    }

    private void logout() {
        AppSessionManager.getInstance().logout();
    }

    private void setupLogoutUI() {
        if (AppSessionManager.getInstance().isRestaurantLoggedIn()) {
            tvLogout.setText(strLogout);
        } else {
            tvLogout.setText(strSignUp);
        }
    }

    private void initViews(View view) {
        tvProfile = view.findViewById(R.id.tvProfile);
        tvAboutUs = view.findViewById(R.id.tvAboutUs);
        tvLogout = view.findViewById(R.id.tvLogout);
        tvBannedlist = view.findViewById(R.id.tvBannedlist);
        tvDeactivate = view.findViewById(R.id.tvDeactivate);
    }
}
