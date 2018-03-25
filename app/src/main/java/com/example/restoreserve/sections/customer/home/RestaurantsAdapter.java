package com.example.restoreserve.sections.customer.home;

import android.content.Context;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.restoreserve.R;
import com.example.restoreserve.data.restaurant.model.Restaurant;

import java.util.ArrayList;

/**
 *
 */

public class RestaurantsAdapter extends RecyclerView.Adapter<RestaurantsAdapter.TableViewHolder> {
    private Context mContext;
    private SortedList<Restaurant> restaurants;
    private OnRestaurantsListener listener;

    public RestaurantsAdapter(Context mContext, OnRestaurantsListener listener) {
        this.mContext = mContext;
        this.listener = listener;
        restaurants = new SortedList<Restaurant>(Restaurant.class, new SortedList.Callback<Restaurant>() {
            @Override
            public int compare(Restaurant o1, Restaurant o2) {
                return 0;
            }

            @Override
            public void onChanged(int position, int count) {

            }

            @Override
            public boolean areContentsTheSame(Restaurant oldItem, Restaurant newItem) {
                return oldItem.getId().equals(newItem.getId());
            }

            @Override
            public boolean areItemsTheSame(Restaurant item1, Restaurant item2) {
                return item1.getId().equals(item2.getId());
            }

            @Override
            public void onInserted(int position, int count) {

            }

            @Override
            public void onRemoved(int position, int count) {

            }

            @Override
            public void onMoved(int fromPosition, int toPosition) {

            }
        });
    }

    @Override
    public TableViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_restaurant, parent, false);
        // create and return view holder
        return new TableViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TableViewHolder holder, int position) {
        final Restaurant table = restaurants.get(position);
        holder.bind(table);
    }

    @Override
    public int getItemCount() {
        return (restaurants != null ? restaurants.size() : 0);
    }

    public void addRestaurants(ArrayList<Restaurant> tables) {
        restaurants.addAll(tables);
        notifyDataSetChanged();
    }

    public void replaceRestaurants(ArrayList<Restaurant> restaurants) {
        this.restaurants.clear();
        this.restaurants.addAll(restaurants);
        notifyDataSetChanged();
    }

    public class TableViewHolder extends RecyclerView.ViewHolder{

        TextView tvRestaurant;
        TextView tvAddress;

        public TableViewHolder(View itemView) {
            super(itemView);
            tvRestaurant = itemView.findViewById(R.id.tvRestaurant);
            tvAddress = itemView.findViewById(R.id.tvAddress);
        }

        public void bind(Restaurant restaurant) {
            tvRestaurant.setText(restaurant.getName());
            tvAddress.setText(restaurant.getAddress());
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onRestaurantClicked(restaurant);
                }
            });
        }
    }

    public interface OnRestaurantsListener {
        void onRestaurantClicked(Restaurant restaurant);
    }
}
