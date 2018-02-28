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

    public class TableViewHolder extends RecyclerView.ViewHolder{

        TextView tvRestaurant, tvDate, tvTime;
        View vContainer;

        public TableViewHolder(View itemView) {
            super(itemView);
            vContainer = itemView.findViewById(R.id.vContainer);
            tvRestaurant = itemView.findViewById(R.id.tvRestaurant);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvTime = itemView.findViewById(R.id.tvTime);
        }

        public void bind(Waitinglist res) {
            tvRestaurant.setText(res.getRestoName());
            tvDate.setText(res.getDate());
            tvTime.setText(res.getTime());
        }
    }

    public interface OnWaitingListener {
        void onReservationClicked(Waitinglist waitinglist);
    }
}
