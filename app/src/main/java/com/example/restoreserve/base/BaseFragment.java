package com.example.restoreserve.base;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.example.restoreserve.R;


/**
 * Created by Nabil on 3/23/2017.
 */
public class BaseFragment extends Fragment {
    protected Handler handler = new Handler();
    // shared
    protected final int POSITION_TAB_FACEBOOK = 0;
    protected final int POSITION_TAB_INSTAGRAM = 1;
    protected final int POSITION_TAB_YOUTUBE = 2;
    protected final int SOCIAL_TABS_COUNT = 3;
    // args
    protected static final String ARG_AUTO_LOAD = "auto_load";
    protected static final String ARG_PLATFORM = "arg_platform";
    // bundle
    protected static final String BUNDLE_PLATFORM = "bundle_platform";
    protected static final String BUNDLE_AUTOLOAD = "bundle_autoload";
    // data
    protected boolean autoLoad = false;
    // log
    protected String TAG = "BaseFragment";

    // BEHAVIOR //

    /**
     * Returns the title resource id set by the fragment,
     * or 0 by default.
     * @return
     */
    public int getFragmentTitle() {
        return 0;
    }

    public void onSelected() {

    }

    public void onUnselected() {

    }

    public void onReselected() {

    }

    public void onRefreshSelected() {

    }

    // DISPATCHES //

    public void onSessionUpdated() {

    }

    // HELPERS //

    protected int getResourceColor(int resId) {
        return ContextCompat.getColor(getContext(), resId);
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
                pdLoading = new ProgressDialog(BaseFragment.this.getActivity());
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

    // DIALOGS //

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
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(response);
        builder.show();
    }

    /**
     * Shows soft keyboard.
     */
    protected static void showKeyboard(Context context) {
        ((InputMethodManager) (context).getSystemService(Context.INPUT_METHOD_SERVICE))
                .toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    protected static void showKeyboard(Context context, View view) {
        ((InputMethodManager) (context).getSystemService(Context.INPUT_METHOD_SERVICE))
                .toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        if (view != null) {
            view.requestFocus();
        }
    }

    /**
     * Hides soft keyboard.
     */
    protected void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (getView() != null) {
            imm.hideSoftInputFromWindow(getView().getApplicationWindowToken(), 0);
        }
    }

    protected void showToast(String text, boolean lengthShort) {
        int length = lengthShort? Toast.LENGTH_SHORT : Toast.LENGTH_LONG;
        Toast.makeText(getContext(), text, length).show();
    }

    protected void showToast(String text) {
        Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
    }

    protected void showAlert(String message, Runnable positive) {
        // create dialog
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
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

    @Override
    public void onStop() {
        super.onStop();
        hideKeyboard();
    }
}
