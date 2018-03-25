package com.example.restoreserve.sections.restaurant.settings.banned;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.EditText;

import com.example.restoreserve.R;
import com.example.restoreserve.base.BaseActivity;
import com.example.restoreserve.data.restaurant.CustomerProvider;
import com.example.restoreserve.data.session.AppSessionManager;
import com.example.restoreserve.data.user.User;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.jakewharton.rxbinding.widget.TextViewTextChangeEvent;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import rx.Single;
import rx.SingleSubscriber;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 *
 */

public class BannedCustomersActivity extends BaseActivity{
    EditText etSearch;
    RecyclerView rvSearch;
    RecyclerView rvBanned;
    SearchAdapter searchAdapter;
    BannedAdapter bannedAdapter;
    ArrayList<User> users;
    ArrayList<User> bannedUsers;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banned_customers);
        initViews();
        configureSearchListing();
        configureBannedListing();
        getCustomers();
    }

    private void initViews() {
        etSearch = findViewById(R.id.etSearch);
        rvSearch = findViewById(R.id.rvSearch);
        rvBanned = findViewById(R.id.rvBanned);
        RxTextView.textChangeEvents(etSearch)
                .skip(1)
                .debounce(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<TextViewTextChangeEvent>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(TextViewTextChangeEvent textViewTextChangeEvent) {
                        applySearch(etSearch.getText().toString());
                    }
                });
    }

    private void configureSearchListing() {
        searchAdapter = new SearchAdapter(getBaseContext(), bannedCustomer -> handleCustomerClicked(bannedCustomer));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getBaseContext());
        rvSearch.setLayoutManager(linearLayoutManager);
        rvSearch.setAdapter(searchAdapter);
    }

    private void handleCustomerClicked(BannedCustomer customer) {
        if (!customer.isBanned) {
            showAlert("Are you sure you want to ban this customer", () -> banCustomer(customer));
        } else {
            showAlert("Are you sure you want to remove this customer from banned list", () -> unbanCustomer(customer.getUser()));
        }
    }

    private void configureBannedListing() {
        bannedAdapter = new BannedAdapter(getBaseContext(), customer -> {
            showAlert("Are you sure you want to remove this customer from banned list", () -> unbanCustomer(customer));
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getBaseContext());
        rvBanned.setLayoutManager(linearLayoutManager);
        rvBanned.setAdapter(bannedAdapter);
    }

    private void banCustomer(BannedCustomer customer) {
        final String id = AppSessionManager.getInstance().getRestaurant().getId();
        showProgressDialog("Loading");
        CustomerProvider.rxBanCustomer(customer.user, id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleSubscriber<Boolean>() {
                    @Override
                    public void onSuccess(Boolean aBoolean) {
                        dismissProgressDialog();
                        customer.isBanned = true;
                        searchAdapter.notifyDataSetChanged();
                        bannedAdapter.addCustomer(customer.getUser());

                    }

                    @Override
                    public void onError(Throwable error) {
                        dismissProgressDialog();
                    }
                });
    }

    private void unbanCustomer(User customer) {
        final String id = AppSessionManager.getInstance().getRestaurant().getId();
        showProgressDialog("Loading");
        CustomerProvider.rxUnBanCustomer(customer)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleSubscriber<Boolean>() {
                    @Override
                    public void onSuccess(Boolean aBoolean) {
                        dismissProgressDialog();
                        searchAdapter.notifyDataSetChanged();
                        bannedAdapter.removeCustomer(customer);
                    }

                    @Override
                    public void onError(Throwable error) {
                        dismissProgressDialog();
                    }
                });
    }

    public void applySearch(String newString) {
        if (users == null) return;
        // create new array
        ArrayList<User> newUsers = new ArrayList<>();
        for (int i = 0; i < users.size(); i++) {
            // name
            String name = users.get(i).getName();
            // phone number
            String phoneNumber = users.get(i).getPhoneNumber();

            if (name.toLowerCase().startsWith(newString.toLowerCase()) || phoneNumber.startsWith(newString)) {
                newUsers.add(users.get(i));
            }
        }
        searchAdapter.replaceCustomers(convertToBanned(newUsers));
    }

    private ArrayList<BannedCustomer> convertToBanned(ArrayList<User> users) {
        ArrayList<BannedCustomer> bannedCustomers = new ArrayList<>();
        for (User user : users) {
            if (bannedUsers.contains(user)) {
                bannedCustomers.add(new BannedCustomer(user, true));
            } else {
                bannedCustomers.add(new BannedCustomer(user, false));
            }
        }
        return bannedCustomers;
    }

    private Single<ArrayList<User>> getCustomers() {
        showProgressDialog("Loading");
        final Single<ArrayList<User>> arrayListSingle = CustomerProvider.rxGetCustomers()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
                arrayListSingle.subscribe(new SingleSubscriber<ArrayList<User>>() {
            @Override
            public void onSuccess(ArrayList<User> newUsers) {
                users = newUsers;
                getBannnedCustomers();
            }

            @Override
            public void onError(Throwable e) {
            }
        });
        return arrayListSingle;
    }

    private Single<ArrayList<User>> getBannnedCustomers() {
        final String id = AppSessionManager.getInstance().getRestaurant().getId();
        final Single<ArrayList<User>> arrayListSingle = CustomerProvider.rxGetBannedCustomers(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        arrayListSingle
                .subscribe(new SingleSubscriber<ArrayList<User>>() {
                    @Override
                    public void onSuccess(ArrayList<User> newUsers) {
                        bannedUsers = newUsers;
                        bannedAdapter.addCustomers(newUsers);
                        dismissProgressDialog();
                    }

                    @Override
                    public void onError(Throwable e) {
                        dismissProgressDialog();
                    }
                });
        return arrayListSingle;
    }

}
