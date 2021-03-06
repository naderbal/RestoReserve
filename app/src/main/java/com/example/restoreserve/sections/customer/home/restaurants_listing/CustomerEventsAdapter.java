package com.example.restoreserve.sections.customer.home.restaurants_listing;

import android.content.Context;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.restoreserve.R;
import com.example.restoreserve.data.event.Event;

import java.util.ArrayList;

/**
 *
 */
public class CustomerEventsAdapter extends RecyclerView.Adapter<CustomerEventsAdapter.EventViewHolder> {
    private Context mContext;
    private SortedList<Event> events;
    private OnEventListener listener;

    public CustomerEventsAdapter(Context mContext, OnEventListener listener) {
        this.mContext = mContext;
        this.listener = listener;
        events = new SortedList<>(Event.class, new SortedList.Callback<Event>() {
            @Override
            public int compare(Event o1, Event o2) {
                return 0;
            }

            @Override
            public void onChanged(int position, int count) {}

            @Override
            public boolean areContentsTheSame(Event oldItem, Event newItem) {
                return oldItem.getId().equals(newItem.getId());
            }

            @Override
            public boolean areItemsTheSame(Event item1, Event item2) {
                return item1.getId().equals(item2.getId());
            }

            @Override
            public void onInserted(int position, int count) {}

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
    public EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_customer_event, parent, false);
        // create and return view holder
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(EventViewHolder holder, int position) {
        final Event event = events.get(position);
        holder.bind(event);
    }

    @Override
    public int getItemCount() {
        return (events != null ? events.size() : 0);
    }

    public void addEvents(ArrayList<Event> events) {
        this.events.addAll(events);
        notifyDataSetChanged();
    }

    public void removeEvent(Event event) {
        if (event != null) {
            events.remove(event);
        }
    }

    public SortedList<Event> getEvents() {
        return events;
    }

    public void replaceEvents(ArrayList<Event> filtered) {
        this.events.clear();
        this.events.addAll(filtered);
        notifyDataSetChanged();
    }

    public class EventViewHolder extends RecyclerView.ViewHolder{
        TextView tvRestaurant, tvEventMessage;

        public EventViewHolder(View itemView) {
            super(itemView);
            tvRestaurant = itemView.findViewById(R.id.tvRestaurant);
            tvEventMessage = itemView.findViewById(R.id.tvEventMessage);
        }

        public void bind(Event event) {
            tvEventMessage.setText(event.getEventMessage());
            tvRestaurant.setText(event.getRestoName());
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEventClicked(event);
                }
            });
        }
    }

    public interface OnEventListener {
        void onEventClicked(Event event);
    }
}
