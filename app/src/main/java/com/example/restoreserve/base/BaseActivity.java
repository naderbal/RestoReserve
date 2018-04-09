package com.example.restoreserve.base;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.example.restoreserve.R;


/**
 *
 */
public abstract class BaseActivity extends AppCompatActivity {
    // result codes
    public static final int RESULT_CODE_LOGIN_SUCCESSFUL = 50;
    public static final int RESULT_CODE_LOG_OUT = 51;
    public static final int RESULT_CODE_GUEST = 52;
    public static final int RESULT_CODE_NEW_REGISTRATION = 53;
    public static final int RESULT_CODE_REGISTRATION_SUCCESSFUL = 54;
    public static final int RESULT_CODE_UPDATE_SUCCESSFUL = 55;
    // request codes
    public static final int RC_HOME = 10;
    public static final int RC_WELCOME = 11;
    public static final int RC_NEW_REGISTRATION = 12;
    Handler handler = new Handler();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected int getResourceColor(int resId) {
        return ContextCompat.getColor(getBaseContext(), resId);
    }

    // LOADER //

    private ProgressDialog pdLoading;

    protected void showProgressDialog(int resId) {
        String message = getString(resId);
        showProgressDialog(message);
    }

    protected void showProgressDialog(String message) {
        handler.post((() -> {
            if (pdLoading == null) {
                pdLoading = new ProgressDialog(this);
                pdLoading.setCancelable(false);
                pdLoading.setIndeterminate(true);
            }
            pdLoading.setMessage(message != null? message: getString(R.string.loading));
            if (!pdLoading.isShowing()) {
                pdLoading.show();
            }
        }));
    }

    protected void dismissProgressDialog() {
        handler.post((() -> {
            if (pdLoading != null && pdLoading.isShowing()) {
                pdLoading.dismiss();
            }
        }));
    }

    /**
     * Show simple dialog for showing messages of unhandled received response errors.
     * @param resId The id of the text message in resources.
     */
    protected void showResponseDialog(int resId) {
        String message = getString(resId);
        showResponseDialog(message);
    }

    /**
     * Show simple dialog for showing messages of unhandled received response errors.
     * @param response to be shown in the dialog.
     */
    protected void showResponseDialog(String response) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(response);
        builder.show();
    }

    // TOOLBAR //

    /**
     * Configures this activity's toolbar.
     */
    protected Toolbar configureToolbar(int toolbarId) {
        Toolbar toolbar = findViewById(toolbarId);
        // set toolbar
        setSupportActionBar(toolbar);
        return toolbar;
    }

    /**
     * Configures this activity's toolbar.
     */
    protected void configureToolbar(int toolbarId, String title) {
        configureToolbar(toolbarId);
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setTitle(title);
        }
    }

    /**
     * Configures this activity's toolbar.
     */
    protected void configureToolbarWithUpButton(int toolbarId) {
        Toolbar toolbar = configureToolbar(toolbarId);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        ActionBar supportActionBar = getSupportActionBar();
        // configure back action
        if (supportActionBar != null) {
            supportActionBar.setDisplayShowTitleEnabled(false);
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            supportActionBar.setDisplayShowHomeEnabled(true);
        }
    }

    /**
     * Configures this activity's toolbar.
     */
    protected void configureToolbarWithUpButton(int toolbarId, String title) {
        Toolbar toolbar = configureToolbar(toolbarId);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        ActionBar supportActionBar = getSupportActionBar();
        // configure back action
        if (supportActionBar != null) {
            supportActionBar.setDisplayShowTitleEnabled(true);
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            supportActionBar.setDisplayShowHomeEnabled(true);
            supportActionBar.setTitle(title);
        }
    }

    protected void setToolbarTitle(int resTitle) {
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setTitle(resTitle);
        }
    }

    // KEY BOARD //

    /**
     * Shows soft keyboard.
     */
    protected static void showKeyboard(Context context) {
        ((InputMethodManager) (context).getSystemService(Context.INPUT_METHOD_SERVICE))
                .toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    protected static void showKeyboard(Context context, View view) {
        if (view != null) {
            ((InputMethodManager) (context).getSystemService(Context.INPUT_METHOD_SERVICE))
                    .toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
            view.requestFocus();
        }
    }

    /**
     * Hides soft keyboard.
     */
    protected void hideKeyboard() {
        if(getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    // FRAGMENTS MANAGEMENT //

    /**
     * Replaces current fragment with the given one, without
     * keeping the removed one in the backstack.
     * @param fragment to be replaced with.
     * @param tag the tag of the fragment to be added.
     */
    protected void replaceFragment(int containerId, Fragment fragment, String tag) {
        // add fragment to container
        getSupportFragmentManager()
                .beginTransaction()
                .replace(containerId, fragment, tag)
                .commit();
    }

    /**
     * Replaces current fragment with the given one, without
     * keeping the removed one in the backstack.
     * @param fragment to be replaced with.
     * @param tag the tag of the fragment to be added.
     */
    protected void addFragment(int containerId, Fragment fragment, String tag) {
        // add fragment to container
        getSupportFragmentManager()
                .beginTransaction()
                .addToBackStack(null)
                .replace(containerId, fragment, tag)
                .commit();
    }

    /**
     * Returns fragment in backstack, with given tag.
     */
    protected Fragment getFragmentByTag(String tag) {
        return getSupportFragmentManager().findFragmentByTag(tag);
    }

    /**
     * Pops the activity's fragments backstack once.
     */
    protected void popFragment() {
        getSupportFragmentManager().popBackStack();
    }

    protected void showToast(String text, boolean lengthShort) {
        int length = lengthShort? Toast.LENGTH_SHORT : Toast.LENGTH_LONG;
        Toast.makeText(getBaseContext(), text, length).show();
    }

    protected void showToast(String text) {
        Toast.makeText(getBaseContext(), text, Toast.LENGTH_SHORT).show();
    }

    protected void showAlert(String message, Runnable positive) {
        // create dialog
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        // configure
        alertBuilder.setMessage(message);
        alertBuilder.setCancelable(true);
        alertBuilder.setPositiveButton(
                "Ok",
                (dialog, id) -> {
                    positive.run();
                    dialog.cancel();
                });
        alertBuilder.setNegativeButton("Cancel", (dialog, id) -> dialog.cancel());
        // show dialog
        alertBuilder.show();
    }

}