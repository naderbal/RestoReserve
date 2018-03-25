package com.example.restoreserve.sections.customer.reservations;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.restoreserve.R;
import com.example.restoreserve.data.reservations.model.Reservation;

import java.util.ArrayList;

/**
 *
 */
public class ReservationsAdapter extends RecyclerView.Adapter<ReservationsAdapter.TableViewHolder> {
    private Context mContext;
    private SortedList<Reservation> reservations;
    private OnReservationsListener listener;

    public ReservationsAdapter(Context mContext, OnReservationsListener listener) {
        this.mContext = mContext;
        this.listener = listener;
        reservations = new SortedList<>(Reservation.class, new SortedList.Callback<Reservation>() {
            @Override
            public int compare(Reservation o1, Reservation o2) {
                return 0;
            }

            @Override
            public void onChanged(int position, int count) {

            }

            @Override
            public boolean areContentsTheSame(Reservation oldItem, Reservation newItem) {
                return oldItem.getId().equals(newItem.getId());
            }

            @Override
            public boolean areItemsTheSame(Reservation item1, Reservation item2) {
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_user_reservation, parent, false);
        // create and return view holder
        return new TableViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TableViewHolder holder, int position) {
        final Reservation reservation = reservations.get(position);
        holder.bind(reservation);
    }

    @Override
    public int getItemCount() {
        return (reservations != null ? reservations.size() : 0);
    }

    public void replaceReservations(ArrayList<Reservation> reservations) {
        this.reservations.clear();
        this.reservations.addAll(reservations);
        notifyDataSetChanged();
    }

    public void removeReservation(Reservation reservation) {
        if (reservation != null) {
            reservations.remove(reservation);
        }
    }

    public SortedList<Reservation> getReservations() {
        return reservations;
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

        public void bind(Reservation res) {
            tvRestaurant.setText(res.getRestoName());
            tvDate.setText(res.getDate());
            tvTime.setText(res.getTime());

            // check reservation confirmed
            if(res.isConfirmed()) {
                // confirmed show green bg
                vContainer.setBackgroundColor(ContextCompat.getColor(mContext, R.color.reservation_confirmed_bg));
            } else {
                // show default background and set click listener on container
                vContainer.setBackgroundColor(ContextCompat.getColor(mContext, R.color.reservation_default_bg));
                vContainer.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onReservationClicked(res);
                    }
                });
            }
        }
    }

    public interface OnReservationsListener {
        void onReservationClicked(Reservation reservation);
    }
}
