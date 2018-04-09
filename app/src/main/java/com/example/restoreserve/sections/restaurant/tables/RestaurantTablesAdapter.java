package com.example.restoreserve.sections.restaurant.tables;

import android.content.Context;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.restoreserve.R;
import com.example.restoreserve.data.reservations.model.Reservation;
import com.example.restoreserve.data.reservations.model.Table;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 */
public class RestaurantTablesAdapter extends RecyclerView.Adapter<RestaurantTablesAdapter.TableViewHolder> {
    private Context mContext;
    private SortedList<ReservedTable> tables;
    private OnTableListener listener;

    public RestaurantTablesAdapter(Context mContext, OnTableListener listener) {
        this.mContext = mContext;
        this.listener = listener;
        tables = new SortedList<>(ReservedTable.class, new SortedList.Callback<ReservedTable>() {
            @Override
            public int compare(ReservedTable o1, ReservedTable o2) {
                return 0;
            }

            @Override
            public void onChanged(int position, int count) {

            }

            @Override
            public boolean areContentsTheSame(ReservedTable oldItem, ReservedTable newItem) {
                return oldItem.table.getId().equals(newItem.table.getId());
            }

            @Override
            public boolean areItemsTheSame(ReservedTable item1, ReservedTable item2) {
                return item1.table.getId().equals(item2.table.getId());
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_table_reservation, parent, false);
        // create and return view holder
        return new TableViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TableViewHolder holder, int position) {
        final ReservedTable table = tables.get(position);
        holder.bind(table);
    }

    @Override
    public int getItemCount() {
        return (tables != null ? tables.size() : 0);
    }

    public void addTables(ArrayList<ReservedTable> reservations) {
        this.tables.addAll(reservations);
        notifyDataSetChanged();
    }

    public void removeTable(ReservedTable table) {
        if (table != null) {
            tables.remove(table);
        }
    }

    public SortedList<ReservedTable> getTables() {
        return tables;
    }

    public void replaceTables(ArrayList<ReservedTable> reservedTables) {
        this.tables.clear();
        this.tables.addAll(reservedTables);
        notifyDataSetChanged();
    }

    public void clearTables() {
        tables.clear();
        notifyDataSetChanged();
    }

    public class TableViewHolder extends RecyclerView.ViewHolder{

        TextView tvTable;
        TextView tvSeatsCount;
        TextView tvReserved;

        public TableViewHolder(View itemView) {
            super(itemView);
            tvTable = itemView.findViewById(R.id.tvTable);
            tvSeatsCount = itemView.findViewById(R.id.tvSeatsCount);
            tvReserved  = itemView.findViewById(R.id.tvReserved);
        }

        public void bind(ReservedTable table) {
            tvTable.setText(table.table.getId());
            tvSeatsCount.setText(""+(int) table.table.getSeatsCount());
            tvReserved.setVisibility(table.reservation != null ? View.VISIBLE : View.GONE);
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onTableClicked(table);
                }
            });
        }
    }

    public static class ReservedTable implements Serializable {
        Table table;
        Reservation reservation;

        public ReservedTable(Table table, Reservation reservation) {
            this.table = table;
            this.reservation = reservation;
        }
    }

    public interface OnTableListener {
        void onTableClicked(ReservedTable table);
    }
}
