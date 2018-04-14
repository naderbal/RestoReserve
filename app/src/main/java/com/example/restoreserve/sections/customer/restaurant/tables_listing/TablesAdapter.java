package com.example.restoreserve.sections.customer.restaurant.tables_listing;

import android.content.Context;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.restoreserve.R;
import com.example.restoreserve.data.reservations.model.ReservedTable;

import java.util.ArrayList;

/**
 */

public class TablesAdapter extends RecyclerView.Adapter<TablesAdapter.TableViewHolder> {
    private Context mContext;
    private SortedList<ReservedTable> reservedTables;
    private OnTableListener listener;

    public TablesAdapter(Context mContext, OnTableListener listener) {
        this.mContext = mContext;
        this.listener = listener;
        reservedTables = new SortedList<>(ReservedTable.class, new SortedList.Callback<ReservedTable>() {
            @Override
            public int compare(ReservedTable o1, ReservedTable o2) {
                return 0;
            }

            @Override
            public void onChanged(int position, int count) {

            }

            @Override
            public boolean areContentsTheSame(ReservedTable oldItem, ReservedTable newItem) {
                return oldItem.getTable().getId().equals(newItem.getTable().getId());
            }

            @Override
            public boolean areItemsTheSame(ReservedTable item1, ReservedTable item2) {
                return item1.getTable().getId().equals(item2.getTable().getId());
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_table, parent, false);
        // create and return view holder
        return new TableViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TableViewHolder holder, int position) {
        final ReservedTable table = reservedTables.get(position);
        holder.bind(table);
    }

    @Override
    public int getItemCount() {
        return (reservedTables != null ? reservedTables.size() : 0);
    }

    public void addTables(ArrayList<ReservedTable> tables) {
        this.reservedTables.addAll(tables);
        notifyDataSetChanged();
    }

    public void clearPreviousTables() {
        if (reservedTables != null && reservedTables.size() > 0) {
            reservedTables.clear();
            notifyDataSetChanged();
        }
    }

    class TableViewHolder extends RecyclerView.ViewHolder{

        TextView tvTable;
        TextView tvSeatsCount;

        TableViewHolder(View itemView) {
            super(itemView);
            tvTable = itemView.findViewById(R.id.tvTable);
            tvSeatsCount = itemView.findViewById(R.id.tvSeatsCount);
        }

        public void bind(ReservedTable reservedTable) {
            if (reservedTable.isReserved()) {
                itemView.setAlpha(0.25f);
            } else {
                itemView.setAlpha(1);
                itemView.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onTableClicked(reservedTable);
                    }
                });
            }
            tvTable.setText(String.valueOf(reservedTable.getTable().getId()));
            tvSeatsCount.setText(String.valueOf(reservedTable.getTable().getSeatsCount()));
        }
    }

    public interface OnTableListener {
        void onTableClicked(ReservedTable table);
    }
}
