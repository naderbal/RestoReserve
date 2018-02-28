package com.example.restoreserve.sections.authentication.sign_up.restaurant;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.restoreserve.R;
import com.example.restoreserve.data.reservations.model.Table;
import com.example.restoreserve.data.restaurant.model.Restaurant;
import com.example.restoreserve.data.session.AppSessionManager;

import java.util.ArrayList;

/**
 *
 */
public class TablesDialogFragment extends DialogFragment {
   RecyclerView rvTables;
   EditText etTableName, etSeatsCount;
   Button btnAddTable, btnCancel, btnSubmit;
   OnDialogFragmentListener listener;
   TablesAdapter adapter;
    private ArrayList<Table> tables;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_fragment_tables, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        configureListing();
    }

    private void configureListing() {
        ArrayList<Table> tables = null;
        final AppSessionManager instance = AppSessionManager.getInstance();
        if (this.tables != null) {
            tables = this.tables;
        } else if(instance.isRestaurantLoggedIn()){
            Restaurant restaurant = instance.getRestaurant();
            tables = restaurant.getTables();
        }
        adapter = new TablesAdapter(tables, getContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvTables.setLayoutManager(layoutManager);
        rvTables.setAdapter(adapter);
    }

    public void setListener(OnDialogFragmentListener listener) {
        this.listener = listener;
    }

    private void initViews(View view) {
        rvTables = view.findViewById(R.id.rvTables);
        etTableName = view.findViewById(R.id.etTableName);
        etSeatsCount = view.findViewById(R.id.etSeatsCount);
        btnAddTable = view.findViewById(R.id.btnAddTable);
        btnSubmit = view.findViewById(R.id.btnSubmit);
        btnCancel = view.findViewById(R.id.btnCancel);
        // listeners
        btnAddTable.setOnClickListener(v -> handleAddTableClicked());
        btnSubmit.setOnClickListener(v -> handleSubmitclicked());
        btnCancel.setOnClickListener(v -> {
            if (listener != null) {
                listener.dismiss();
            }
        });
    }

    private void handleSubmitclicked() {
        if (listener != null) {
            final ArrayList<Table> tables = adapter.getTables();
            listener.submit(tables);
        }
    }

    private void handleAddTableClicked() {
        final String tableName = etTableName.getText().toString().trim();
        final String strSeatsCount = etSeatsCount.getText().toString().trim();
        int count = 0;
        try {
        count = Integer.parseInt(strSeatsCount);

        } catch (Exception e) {
            // do nothing
        }

        if (tableName.isEmpty()) {
            Toast.makeText(getContext(), "Please enter table name", Toast.LENGTH_SHORT).show();
            return;
        } else {
            ArrayList<Table> arrayTables = adapter.getTables();
            if (arrayTables != null) {
                for (Table table : arrayTables) {
                    if (table.getId().equals(tableName)) {
                        Toast.makeText(getContext(), "Table with this name already exists", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            }
        }
        if (count == 0) {
            Toast.makeText(getContext(), "Please enter seats count", Toast.LENGTH_SHORT).show();
            return;
        } else if (count > 10) {
            Toast.makeText(getContext(), "Seats count shouldn't exceed 10", Toast.LENGTH_SHORT).show();
            return;
        }
        Table table = new Table(tableName, count);
        adapter.addTable(table);
        etTableName.setText("");
        etSeatsCount.setText("");
    }

    public void setTables(ArrayList<Table> tables) {
        this.tables = tables;
    }

    interface OnDialogFragmentListener {
        void submit(ArrayList<Table> tables);
        void dismiss();
    }

    class TablesAdapter extends RecyclerView.Adapter<TablesAdapter.TableViewHolder> {
        ArrayList<Table> tables;
        Context context;

        public TablesAdapter(ArrayList<Table> tables, Context context) {
            if (tables != null) {
                this.tables = tables;
            } else {
                this.tables = new ArrayList<>();
            }
            this.context = context;
        }

        @Override
        public TableViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_table, parent, false);
            // create and return view holder
            return new TableViewHolder(view);
        }

        @Override
        public void onBindViewHolder(TableViewHolder holder, int position) {
            final Table table = tables.get(position);
            holder.bind(table);
        }

        @Override
        public int getItemCount() {
            return tables != null? tables.size() : 0;
        }

        public ArrayList<Table> getTables() {
            return tables;
        }

        public void addTable(Table table) {
            if (tables != null) {
                tables.add(table);
                notifyItemInserted(tables.size() - 1);
            }
        }

        public class TableViewHolder extends RecyclerView.ViewHolder{

            TextView tvTable, tvSeatsCount;

            public TableViewHolder(View itemView) {
                super(itemView);
                tvTable = itemView.findViewById(R.id.tvTable);
                tvSeatsCount = itemView.findViewById(R.id.tvSeatsCount);

            }

            public void bind(Table ta) {
                tvTable.setText(ta.getId());
                tvSeatsCount.setText(String.valueOf(ta.getSeatsCount()));
            }
        }
    }

}
