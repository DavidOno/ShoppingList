package de.db.shoppinglist.adapter;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import de.db.shoppinglist.R;
import de.db.shoppinglist.model.EntryHistoryElement;

public class SearchEntryRecyclerViewAdapter extends RecyclerView.Adapter<SearchEntryRecyclerViewAdapter.ViewHolder> implements Filterable {

    private List<EntryHistoryElement> entries;
    private List<EntryHistoryElement> allEntries;
    private OnEntryListener onEntryListener;


    public SearchEntryRecyclerViewAdapter(List<EntryHistoryElement> entries, OnEntryListener onEntryListener) {
        this.entries = new ArrayList<>(entries);
        this.allEntries = new ArrayList<>(entries);
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
        if(entries.get(position).getImageURI() != null) {
            Glide.with(holder.itemView.getContext())
                    .load(entries.get(position).getImageURI())
                    .skipMemoryCache(false)
                    .into(holder.imageView);
        }else{
            Glide.with(holder.itemView.getContext()).clear(holder.imageView);
        }
    }

    @Override
    public int getItemCount() {
        return entries.size();
    }

    public EntryHistoryElement getHistoryEntry(int position){
        return entries.get(position);
    }

    @Override
    public Filter getFilter() {
        return historyFilter;
    }

    private Filter historyFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<EntryHistoryElement> filteredList = new ArrayList<>();
            if(constraint == null || constraint.length() == 0){
                filteredList.addAll(allEntries);
            }else{
                String filterPattern = constraint.toString().toLowerCase().trim();
                for(EntryHistoryElement entry: allEntries){
                    if(entry.getName().toLowerCase().trim().contains(filterPattern)){
                        filteredList.add(entry);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            entries.clear();
            entries.addAll((List)results.values);
            notifyDataSetChanged();
        }
    };

    public void setHistory(List<EntryHistoryElement> history) {
        entries.clear();
        entries.addAll(history);
        allEntries.clear();
        allEntries.addAll(history);
    }

    public interface OnEntryListener {
        void onEntryClick(int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView nameOfProduct;
        private TextView unitOfQuantity;
        private TextView details;
        private ImageView imageView;
        private OnEntryListener onEntryListener;

        public ViewHolder(@NonNull View itemView, OnEntryListener onEntryListener) {
            super(itemView);
            nameOfProduct = itemView.findViewById(R.id.item_search_name_of_product);
            unitOfQuantity = itemView.findViewById(R.id.item_search_unit_of_quantity);
            details = itemView.findViewById(R.id.item_search_details);
            imageView = itemView.findViewById(R.id.item_search_image);
            this.onEntryListener = onEntryListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onEntryListener.onEntryClick(getAdapterPosition());
        }
    }
}
