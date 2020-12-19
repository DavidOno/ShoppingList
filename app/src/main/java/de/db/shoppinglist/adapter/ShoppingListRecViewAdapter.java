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


public class ShoppingListRecViewAdapter extends RecyclerView.Adapter<ShoppingListRecViewAdapter.ViewHolder>{

    private List<ShoppingEntry> entries = new ArrayList<>();
    private OnEntryListener onEntryListener;

    public ShoppingListRecViewAdapter(OnEntryListener onEntryListener) {
        this.onEntryListener = onEntryListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_entry, parent, false);
        return new ViewHolder(view, onEntryListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.nameOfEntry.setText(entries.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return entries.size();
    }

    public void addEntry(ShoppingEntry entry){
        entries.add(entry);
        notifyDataSetChanged();
    }

    public void deleteAll(){
        entries.clear();
        notifyDataSetChanged();
    }

    public ShoppingEntry getEntryByPosition(int position){
        return entries.get(position);
    }

    public interface OnEntryListener {
        void onEntryClick(int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView nameOfEntry;
        private OnEntryListener onEntryListener;

        public ViewHolder(@NonNull View itemView, OnEntryListener onEntryListener) {
            super(itemView);
            nameOfEntry = itemView.findViewById(R.id.entryTextView);
            this.onEntryListener = onEntryListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onEntryListener.onEntryClick(getAdapterPosition());
        }
    }
}

