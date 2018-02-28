package com.example.restoreserve.sections.authentication.welcome;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.example.restoreserve.R;
import com.example.restoreserve.base.BaseActivity;
import com.example.restoreserve.data.session.AppSessionManager;
import com.example.restoreserve.sections.authentication.sign_up.SignUpActivity;


public class WelcomeActivity extends BaseActivity implements WelcomeContract.View, View.OnClickListener{
    // views
    private TextInputLayout tilEmail;
    private TextInputLayout tilPassword;
    private TextInputEditText etEmail;
    private TextInputEditText etPassword;
    private Switch switchCustomer;
    private Button btnLogin;
    private TextView tvSignUp;
    // presenter
    WelcomeContract.Presenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        // set presenter
        setPresenter(new WelcomePresenter(this));
        // init views
        initViews();
        AppSessionManager.getInstance().clearCache();
    }

    private void initViews() {
        tilEmail = findViewById(R.id.tilEmail);
        tilPassword = findViewById(R.id.tilPassword);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        switchCustomer = findViewById(R.id.switchCustomer);
        btnLogin = findViewById(R.id.btnLogin);
        tvSignUp = findViewById(R.id.tvSignUp);
        // click listeners
        btnLogin.setOnClickListener(this);
        tvSignUp.setOnClickListener(this);
        etEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                tilEmail.setError(null);
            }
        });
        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                tilPassword.setError(null);
            }
        });
    }

    @Override
    public void setPresenter(WelcomeContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLogin:
                handleLoginClicked();
                break;
            case R.id.tvSignUp:
                handleRegistrationClicked();
                break;
        }
    }

    private void handleRegistrationClicked() {
        boolean isCustomer = !switchCustomer.isChecked();
        presenter.onSignUpClicked(isCustomer);
    }

    /**
     * Gets email and password inputs, and informs presenter
     * of login clicked.
     */
    private void handleLoginClicked() {
        // get input fields
        String email = getInputText(etEmail);
        String password = getInputText(etPassword);
        boolean isCustomer = !switchCustomer.isChecked();
        // inform presenter
        presenter.onLoginClicked(isCustomer, email, password);
    }

    private String getInputText(EditText et) {
        return et.getText().toString().trim();
    }

    @Override
    public void redirectToSplash(int resultCode) {
        setResult(resultCode);
        finish();
    }

    @Override
    public void showLoginError() {
        showToast("Something went wrong", true);
    }

    @Override
    public void showLoading() {
        showProgressDialog(R.string.loading);
    }

    @Override
    public void hideLoading() {
        dismissProgressDialog();
    }

    @Override
    public void showLoginWrongCredentials() {
        showToast("Wrong email/password combination", true);
    }

    @Override
    public void openCustomerSignUp() {
        Intent intent = new Intent(this, SignUpActivity.class);
        intent.putExtra("is_customer", true);
        startActivityForResult(intent, RC_NEW_REGISTRATION);
    }

    @Override
    public void showInvalidEmail() {
        tilEmail.setError("Invalid Email");
    }

    @Override
    public void showInvalidPassword() {
        tilPassword.setError("Invalid Password, should be more than 4 characters");
    }

    @Override
    public void openRestaurantSignUp() {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivityForResult(intent, RC_NEW_REGISTRATION);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        presenter.activityResult(requestCode, resultCode, data);
    }
}
