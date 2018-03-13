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

import java.util.ArrayList;

/**
 *
 */

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.CustomerViewHolder> {
    private Context mContext;
    private SortedList<BannedCustomer> customers;
    private OnCustomerListener listener;

    public SearchAdapter(Context mContext, OnCustomerListener listener) {
        this.mContext = mContext;
        this.listener = listener;
        customers = new SortedList<>(BannedCustomer.class, new SortedList.Callback<BannedCustomer>() {
            @Override
            public int compare(BannedCustomer o1, BannedCustomer o2) {
                return 0;
            }

            @Override
            public void onChanged(int position, int count) {

            }

            @Override
            public boolean areContentsTheSame(BannedCustomer oldItem, BannedCustomer newItem) {
                return oldItem.getUser().getPhoneNumber().equals(newItem.getUser().getPhoneNumber());
            }

            @Override
            public boolean areItemsTheSame(BannedCustomer item1, BannedCustomer item2) {
                return item1.getUser().getPhoneNumber().equals(item2.getUser().getPhoneNumber());
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
        final BannedCustomer reservation = customers.get(position);
        holder.bind(reservation);
    }

    @Override
    public int getItemCount() {
        return (customers != null ? customers.size() : 0);
    }

    public void addCustomers(ArrayList<BannedCustomer> reservations) {
        this.customers.addAll(reservations);
        notifyDataSetChanged();
    }

    public void removeCustomer(BannedCustomer reservation) {
        if (reservation != null) {
            customers.remove(reservation);
        }
    }

    public void replaceCustomers(ArrayList<BannedCustomer> replaced) {
        if (customers != null) {
            customers.clear();
            customers.addAll(replaced);
            notifyDataSetChanged();
        }
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

        public void bind(BannedCustomer bannedCustomer) {
            tvName.setText(bannedCustomer.getUser().getName());
            tvPhoneNumber.setText(bannedCustomer.getUser().getPhoneNumber());
            if (bannedCustomer.isBanned) {
                vContainer.setBackgroundColor(ContextCompat.getColor(mContext, R.color.customer_banned));
            } else {
                vContainer.setBackgroundColor(ContextCompat.getColor(mContext, R.color.reservation_default_bg));
            }
            vContainer.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onCustomerClicked(bannedCustomer);
                }
            });
        }
    }

    public interface OnCustomerListener {
        void onCustomerClicked(BannedCustomer BannedCustomer);
    }
}
