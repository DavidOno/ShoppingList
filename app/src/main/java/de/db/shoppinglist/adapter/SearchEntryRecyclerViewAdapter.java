package de.db.shoppinglist.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import de.db.shoppinglist.R;
import de.db.shoppinglist.model.ShoppingEntry;

public class SearchEntryRecyclerViewAdapter extends RecyclerView.Adapter<SearchEntryRecyclerViewAdapter.ViewHolder> {

    private List<ShoppingEntry> entries;
    private OnEntryListener onEntryListener;


    public SearchEntryRecyclerViewAdapter(List entries, OnEntryListener onEntryListener) {
        this.entries = new ArrayList<>(entries);
        this.onEntryListener = onEntryListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_entry, parent, false);
        return new ViewHolder(view, onEntryListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.nameOfProduct.setText(entries.get(position).getName());
        holder.unitOfQuantity.setText(entries.get(position).getUnitOfQuantity());
        holder.details.setText(entries.get(position).getDetails());
    }

    @Override
    public int getItemCount() {
        return entries.size();
    }

    public interface OnEntryListener {
        void onEntryClick(int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView nameOfProduct;
        private TextView unitOfQuantity;
        private TextView details;
        private OnEntryListener onEntryListener;

        public ViewHolder(@NonNull View itemView, OnEntryListener onEntryListener) {
            super(itemView);
            nameOfProduct = itemView.findViewById(R.id.item_search_name_of_product);
            unitOfQuantity = itemView.findViewById(R.id.item_search_unit_of_quantity);
            details = itemView.findViewById(R.id.item_search_details);
            this.onEntryListener = onEntryListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onEntryListener.onEntryClick(getAdapterPosition());
        }
    }
}
