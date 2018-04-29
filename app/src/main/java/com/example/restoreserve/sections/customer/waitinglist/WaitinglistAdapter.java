package com.example.restoreserve.sections.customer.waitinglist;

import android.content.Context;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.restoreserve.R;
import com.example.restoreserve.data.waitinglist.Waitinglist;

import java.util.ArrayList;

/**
 *
 */
public class WaitinglistAdapter extends RecyclerView.Adapter<WaitinglistAdapter.TableViewHolder> {
    private Context mContext;
    private SortedList<Waitinglist> waitlists;
    private OnWaitingListener listener;

    public WaitinglistAdapter(Context mContext, OnWaitingListener listener) {
        this.mContext = mContext;
        this.listener = listener;
        waitlists = new SortedList<>(Waitinglist.class, new SortedList.Callback<Waitinglist>() {
            @Override
            public int compare(Waitinglist o1, Waitinglist o2) {
                return 0;
            }

            @Override
            public void onChanged(int position, int count) {

            }

            @Override
            public boolean areContentsTheSame(Waitinglist oldItem, Waitinglist newItem) {
                return oldItem.getId().equals(newItem.getId());
            }

            @Override
            public boolean areItemsTheSame(Waitinglist item1, Waitinglist item2) {
                return item1.getId().equals(item2.getId());
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
    public TableViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_waiting_list, parent, false);
        // create and return view holder
        return new TableViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TableViewHolder holder, int position) {
        final Waitinglist reservation = waitlists.get(position);
        holder.bind(reservation);
    }

    @Override
    public int getItemCount() {
        return (waitlists != null ? waitlists.size() : 0);
    }

    public void replaceWaitinglist(ArrayList<Waitinglist> reservations) {
        this.waitlists.clear();
        this.waitlists.addAll(reservations);
        notifyDataSetChanged();
    }

    public void removeReservation(Waitinglist reservation) {
        if (reservation != null) {
            waitlists.remove(reservation);
        }
    }

    public SortedList<Waitinglist> getWaitlists() {
        return waitlists;
    }

    public void updateWaitingList(Waitinglist waitinglist) {
        for (int i = 0; i < waitlists.size(); i++) {
            if (waitlists.get(i).getId().equals(waitinglist.getId())) {
                waitlists.updateItemAt(i, waitinglist);
                notifyDataSetChanged();
            }
        }
    }

    public class TableViewHolder extends RecyclerView.ViewHolder{

        TextView tvRestaurant, tvDate, tvTime, tvAvailable;
        View vContainer;

        public TableViewHolder(View itemView) {
            super(itemView);
            vContainer = itemView.findViewById(R.id.vContainer);
            tvRestaurant = itemView.findViewById(R.id.tvRestaurant);
            tvAvailable = itemView.findViewById(R.id.tvAvailable);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvTime = itemView.findViewById(R.id.tvTime);
        }

        public void bind(Waitinglist waitinglist) {
            tvRestaurant.setText(waitinglist.getRestoName());
            tvDate.setText(waitinglist.getDate());
            tvTime.setText(waitinglist.getTime());
            boolean clickable = false;
            if (waitinglist.isAvailable()) {
                tvAvailable.setVisibility(View.VISIBLE);
                clickable = true;
            } else {
                tvAvailable.setVisibility(View.INVISIBLE);
            }
            boolean available = clickable;
            vContainer.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onWaitinglistClicked(waitinglist, available);
                }
            });
        }
    }

    public interface OnWaitingListener {
        void onWaitinglistClicked(Waitinglist waitinglist, boolean clickable);
    }
}
