package com.example.restoreserve.sections.restaurant.settings.banned;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.restoreserve.R;
import com.example.restoreserve.data.user.User;

import java.util.ArrayList;

/**
 *
 */

public class BannedAdapter extends RecyclerView.Adapter<BannedAdapter.CustomerViewHolder> {
    private Context mContext;
    private SortedList<User> customers;
    private OnCustomerListener listener;

    public BannedAdapter(Context mContext, OnCustomerListener listener) {
        this.mContext = mContext;
        this.listener = listener;
        customers = new SortedList<>(User.class, new SortedList.Callback<User>() {
            @Override
            public int compare(User o1, User o2) {
                return 0;
            }

            @Override
            public void onChanged(int position, int count) {

            }

            @Override
            public boolean areContentsTheSame(User oldItem, User newItem) {
                return oldItem.getPhoneNumber().equals(newItem.getPhoneNumber());
            }

            @Override
            public boolean areItemsTheSame(User item1, User item2) {
                return item1.getPhoneNumber().equals(item2.getPhoneNumber());
            }

            @Override
            public void onInserted(int position, int count) {

            }

            @Override
            public void onRemoved(int position, int count) {
                notifyItemRemoved(position);
            }

            @Override
            public void onMoved(int fromPosition, int toPosition) {

            }
        });
    }

    @Override
    public CustomerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_customer, parent, false);
        // create and return view holder
        return new CustomerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomerViewHolder holder, int position) {
        final User reservation = customers.get(position);
        holder.bind(reservation);
    }

    @Override
    public int getItemCount() {
        return (customers != null ? customers.size() : 0);
    }

    public void addCustomers(ArrayList<User> reservations) {
        this.customers.addAll(reservations);
        notifyDataSetChanged();
    }

    public void removeCustomer(User reservation) {
        if (reservation != null) {
            customers.remove(reservation);
        }
    }

    public void replaceCustomers(ArrayList<User> replaced) {
        if (customers != null) {
            customers.clear();
            customers.addAll(replaced);
            notifyDataSetChanged();
        }
    }

    public void addCustomer(User user) {
        customers.add(user);
        notifyDataSetChanged();
    }

    public class CustomerViewHolder extends RecyclerView.ViewHolder{

        TextView tvName, tvPhoneNumber;
        View vContainer;

        public CustomerViewHolder(View itemView) {
            super(itemView);
            vContainer = itemView.findViewById(R.id.vContainer);
            tvName = itemView.findViewById(R.id.tvName);
            tvPhoneNumber = itemView.findViewById(R.id.tvPhoneNumber);
        }

        public void bind(User bannedCustomer) {
            tvName.setText(bannedCustomer.getName());
            tvPhoneNumber.setText(bannedCustomer.getPhoneNumber());
        }
    }

    public interface OnCustomerListener {
        void onCustomerClicked(User customer);
    }
}
