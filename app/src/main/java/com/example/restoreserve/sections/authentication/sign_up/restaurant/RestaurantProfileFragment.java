package com.example.restoreserve.sections.authentication.sign_up.restaurant;

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
import com.example.restoreserve.data.reservations.model.Table;
import com.example.restoreserve.data.restaurant.model.Restaurant;
import com.example.restoreserve.data.session.AppSessionManager;
import com.example.restoreserve.utils.InputValidationUtils;
import com.example.restoreserve.utils.ui.CustomInputSelectorView;
import com.example.restoreserve.utils.ui.DialogFragmentTimePicker;

import java.util.ArrayList;


/**
 * Created by naderbaltaji on 12/23/17.
 * <p>
 *
 * </p>
 */

public class RestaurantProfileFragment extends BaseFragment {
    ProfileFragmentInteractionListener listener;
    // til
    private TextInputLayout tilName;
    private TextInputLayout tilEmail;
    private TextInputLayout tilPhone;
    private TextInputLayout tilBranch;
    private TextInputLayout tilAddress;
    private TextInputLayout tilWebsite;
    private TextInputLayout tilConfirmationDelay;
    private TextInputLayout tilPassword;
    private TextInputLayout tilConfirmPassword;
    // et
    private TextInputEditText etName;
    private TextInputEditText etEmail;
    private TextInputEditText etPhone;
    private TextInputEditText etBranch;
    private TextInputEditText etAddress;
    private TextInputEditText etWebsite;
    private TextInputEditText etPassword;
    private TextInputEditText etConfirmationDelay;
    private TextInputEditText etConfirmPassword;
    // input selector
    CustomInputSelectorView vOpeningHour;
    CustomInputSelectorView vClosingHour;
    CustomInputSelectorView vTables;
    // scroll view
    private NestedScrollView vScroll;
    // btn
    private Button btnSignUp;
    // data
    boolean withCredentials = false;
    ArrayList<Table> tables;

    public RestaurantProfileFragment() {
    }
    
    public static RestaurantProfileFragment newInstance(boolean withCredentials) {
        Bundle bundle = new Bundle();
        bundle.putBoolean("withCredentials", withCredentials);
        final RestaurantProfileFragment fragment = new RestaurantProfileFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    public void setListener(ProfileFragmentInteractionListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_restaurant_profile, container, false);
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
        tilBranch = view.findViewById(R.id.tilBranch);
        tilAddress = view.findViewById(R.id.tilAddress);
        tilConfirmationDelay = view.findViewById(R.id.tilConfirmationDelay);
        tilWebsite = view.findViewById(R.id.tilWebsite);
        // et
        etName = view.findViewById(R.id.etName);
        etEmail = view.findViewById(R.id.etEmail);
        etPhone = view.findViewById(R.id.etPhone);
        etBranch = view.findViewById(R.id.etBranch);
        etAddress = view.findViewById(R.id.etAddress);
        etWebsite = view.findViewById(R.id.etWebsite);
        etPassword = view.findViewById(R.id.etPassword);
        etConfirmationDelay = view.findViewById(R.id.etConfirmationDelay);
        etConfirmPassword = view.findViewById(R.id.etConfirmPassword);
        // input
        vOpeningHour = view.findViewById(R.id.vOpeningHour);
        vClosingHour = view.findViewById(R.id.vClosingHour);
        vTables = view.findViewById(R.id.vTables);
        // listeners
        vOpeningHour.setOnClickListener(v -> {
            DialogFragmentTimePicker timeFragment = new DialogFragmentTimePicker();
            timeFragment.setListener(new DialogFragmentTimePicker.OnTimePickerListener() {
                @Override
                public void onSubmit(String time) {
                    vOpeningHour.updateValue(time);
                    timeFragment.dismiss();
                }

                @Override
                public void onCancel() {
                    timeFragment.dismiss();
                }
            });
            timeFragment.show(getFragmentManager(), "");
        });

          vClosingHour.setOnClickListener(v -> {
            DialogFragmentTimePicker timeFragment = new DialogFragmentTimePicker();
            timeFragment.setListener(new DialogFragmentTimePicker.OnTimePickerListener() {
                @Override
                public void onSubmit(String time) {
                    vClosingHour.updateValue(time);
                    timeFragment.dismiss();
                }

                @Override
                public void onCancel() {
                    timeFragment.dismiss();
                }
            });
            timeFragment.show(getFragmentManager(), "");
        });

        vTables.setOnClickListener(v -> {
            TablesDialogFragment fragment = new TablesDialogFragment();
            if (tables != null) {
                fragment.setTables(tables);
            }
            fragment.setListener(new TablesDialogFragment.OnDialogFragmentListener() {
                @Override
                public void submit(ArrayList<Table> newTables) {
                    if (newTables != null && newTables.size() > 0) {
                        vTables.updateValue(String.valueOf(newTables.size()));
                        tables = newTables;
                        fragment.dismiss();
                    }
                }

                @Override
                public void dismiss() {
                    fragment.dismiss();
                }
            });
            fragment.show(getFragmentManager(), "");
        });

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
        removeError(etBranch, tilPhone);
        removeError(etAddress, tilPhone);
        removeError(etPassword, tilPassword);
        removeError(etConfirmationDelay, tilConfirmationDelay);
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
        final Restaurant restaurant = AppSessionManager.getInstance().getRestaurant();
        if (restaurant == null) return;
        // name
        String name = restaurant.getName();
        etName.setText(name);
        // phone
        String phone = restaurant.getPhoneNumber();
        etPhone.setText(phone);
        // branch
        String branch = restaurant.getBranch();
        etBranch.setText(branch);
        // address
        String address = restaurant.getAddress();
        etAddress.setText(address);
        // website
        String website = restaurant.getWebsite();
        etWebsite.setText(website);
        // delay
        String delay = String.valueOf(restaurant.getConfirmationDelayMins());
        etConfirmationDelay.setText(delay);
    }

    private void handleSubmitClicked() {
        String name = getInputText(etName);
        String phone = getInputText(etPhone);
        String email = getInputText(etEmail);
        String password = getInputText(etPassword);
        String branch = getInputText(etBranch);
        String address = getInputText(etAddress);
        String website = getInputText(etWebsite);
        String delay = getInputText(etConfirmationDelay);

        String openingHour = vOpeningHour.getValue();
        String closingHour = vClosingHour.getValue();

        Restaurant restaurant = new Restaurant();

        restaurant.setName(name);
        restaurant.setEmail(email);
        restaurant.setPhoneNumber(phone);
        restaurant.setBranch(branch);
        restaurant.setAddress(address);
        restaurant.setWebsite(website);
        restaurant.setOpeningHour(openingHour);
        restaurant.setClosingHour(closingHour);
        restaurant.setConfirmationDelayMins(Integer.parseInt(delay));
        restaurant.setTables(tables);
        // check if inputs are valid
        if (inputFieldsValid(restaurant, password)) {
            submitRegistration(restaurant, password);
        }
    }

    private void submitRegistration(Restaurant restaurant, String password) {
        if (listener != null) {
            listener.onSubmitProfile(restaurant, password);
        }
    }

    private boolean inputFieldsValid(@NonNull Restaurant restaurant, String password) {
        if (withCredentials) {
            // email
            boolean emailValid = InputValidationUtils.validateEmail(restaurant.getEmail());
            if (!validateField(emailValid, tilEmail, "Invalid Email")) {
                return false;
            }
            // password
            boolean passwordValid = InputValidationUtils.validatePassword(password);
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
        boolean nameValid = InputValidationUtils.validateName(restaurant.getName());
        if (!validateField(nameValid, tilName, "Invalid Name")) {
            return false;
        }
        // phone
        boolean phoneValid = InputValidationUtils.validaPhone(restaurant.getPhoneNumber());
        if (!validateField(phoneValid, tilPhone, "Invalid Phone")) {
            return false;
        }
        if (!InputValidationUtils.validateInputSelector(restaurant.getOpeningHour())) {
            showToast("Please set opening hour");
            return false;
        }
        if (!InputValidationUtils.validateInputSelector(restaurant.getClosingHour())) {
            showToast("Please set closing hour");
            return false;
        }
        if (!InputValidationUtils.validateWebsite(restaurant.getWebsite())) {
            showToast("Please enter a valid website");
            return false;
        }
        if (restaurant.getTables() == null) {
            showToast("Please set tables");
            return false;
        }
        if (restaurant.getConfirmationDelayMins() == 0) {
            showToast("Please set confirmation delay time");
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
        void onSubmitProfile(Restaurant restaurant, String password);
    }
}
