package com.example.restoreserve.sections.authentication.sign_up.customer;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.NestedScrollView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.restoreserve.R;
import com.example.restoreserve.base.BaseFragment;
import com.example.restoreserve.data.session.AppSessionManager;
import com.example.restoreserve.data.user.User;
import com.example.restoreserve.utils.InputValidationUtils;


/**
 * <p>
 *
 * </p>
 */

public class CustomerProfileFragment extends BaseFragment {
    ProfileFragmentInteractionListener listener;
    // til
    private TextInputLayout tilName;
    private TextInputLayout tilEmail;
    private TextInputLayout tilPhone;
    private TextInputLayout tilPassword;
    private TextInputLayout tilConfirmPassword;
    // et
    private TextInputEditText etName;
    private TextInputEditText etEmail;
    private TextInputEditText etPhone;
    private TextInputEditText etPassword;
    private TextInputEditText etConfirmPassword;
    // scroll view
    private NestedScrollView vScroll;
    // btn
    private Button btnSignUp;
    // data
    boolean withCredentials = false;

    public CustomerProfileFragment() {
    }
    
    public static CustomerProfileFragment newInstance(boolean withCredentials) {
        Bundle bundle = new Bundle();
        bundle.putBoolean("withCredentials", withCredentials);
        final CustomerProfileFragment fragment = new CustomerProfileFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    public void setListener(ProfileFragmentInteractionListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_customer_profile, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle args = getArguments();
        withCredentials = args.getBoolean("withCredentials");
        initViews(view, withCredentials);
    }

    private void initViews(View view, boolean withCredentials) {
        // til
        tilEmail = view.findViewById(R.id.tilEmail);
        tilPassword = view.findViewById(R.id.tilPassword);
        tilConfirmPassword = view.findViewById(R.id.tilConfirmPassword);
        tilName = view.findViewById(R.id.tilName);
        tilPhone = view.findViewById(R.id.tilPhone);
        // et
        etName = view.findViewById(R.id.etName);
        etEmail = view.findViewById(R.id.etEmail);
        etPhone = view.findViewById(R.id.etPhone);
        etPassword = view.findViewById(R.id.etPassword);
        etConfirmPassword = view.findViewById(R.id.etConfirmPassword);
        // btn
        btnSignUp = view.findViewById(R.id.btnSignUp);
        vScroll = view.findViewById(R.id.vScroll);
        // click listener
        btnSignUp.setOnClickListener(v -> handleSubmitClicked());
        // credentials
        if (withCredentials) {
            btnSignUp.setText("Sign Up");
        } else {
            btnSignUp.setText("Update");
            view.findViewById(R.id.vCredentials).setVisibility(View.GONE);
            setInputFields();
        }
        removeError(etEmail, tilEmail);
        removeError(etName, tilName);
        removeError(etPhone, tilPhone);
        removeError(etPassword, tilPassword);
        removeError(etConfirmPassword, tilConfirmPassword);
    }

    private void removeError(TextInputEditText et, TextInputLayout til) {
        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                til.setError(null);
            }
        });
    }

    private void setInputFields() {
        final User user = AppSessionManager.getInstance().getUser();
        if (user == null) return;
        // name
        String name = user.getName();
        etName.setText(name);
        // phone
        String phone = user.getPhoneNumber();
        etPhone.setText(phone);
    }

    private void handleSubmitClicked() {
        String name = getInputText(etName);
        String phone = getInputText(etPhone);
        String email = getInputText(etEmail);
        String password = getInputText(etPassword);
        User user = new User(name, email, phone, password);
        // check if inputs are valid
        if (inputFieldsValid(user)) {
            submitRegistration(user);
        }
    }

    private void submitRegistration(User user) {
        if (listener != null) {
            listener.onSubmitProfile(user);
        }
    }

    private boolean inputFieldsValid(@NonNull User user) {
        if (withCredentials) {
            // email
            boolean emailValid = InputValidationUtils.validateEmail(user.getEmail());
            if (!validateField(emailValid, tilEmail, "Invalid Email")) {
                return false;
            }
            // password
            boolean passwordValid = InputValidationUtils.validatePassword(user.getPassword());
            if (!validateField(passwordValid, tilPassword, "Invalid Password")) {
                return false;
            }
            if (!getInputText(etPassword).equals(getInputText(etConfirmPassword))) {
                tilConfirmPassword.setError("Passwords not matching");
                vScroll.scrollTo(0, etConfirmPassword.getTop() - etConfirmPassword.getHeight() / 2);
                return false;
            }
        }
        // name
        boolean nameValid = InputValidationUtils.validateName(user.getName());
        if (!validateField(nameValid, tilName, "Invalid Name")) {
            return false;
        }
        // phone
        boolean phoneValid = InputValidationUtils.validaPhone(user.getPhoneNumber());
        if (!validateField(phoneValid, tilPhone, "Invalid Phone")) {
            return false;
        }
        return true;
    }

    private boolean validateField(boolean fieldValid, TextInputLayout et, String errorMessage) {
        if (!fieldValid) {
            et.setError(errorMessage);
            vScroll.scrollTo(0, et.getTop() - et.getHeight() / 2);
            return false;
        }
        return true;
    }

    private String getInputText(TextInputEditText et) {
        return et.getText().toString().trim();
    }

    public interface ProfileFragmentInteractionListener {
        void onSubmitProfile(User user);
    }
}
