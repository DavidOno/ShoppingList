package de.db.shoppinglist.adapter;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
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
        holder.quantity.setText(getQuantityText(entries.get(position).getQuantity()));
        holder.unitOfQuantity.setText(entries.get(position).getUnitOfQuantity());
        holder.isDone.setChecked(entries.get(position).isDone());
    }

    private String getQuantityText(float quantity) {
        if(quantity - 0 < 0.0001){
            return "";
        }else{
            return String.valueOf(quantity) + " x ";
        }
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
        private TextView quantity;
        private TextView unitOfQuantity;
        private CheckBox isDone;
        private OnEntryListener onEntryListener;

        public ViewHolder(@NonNull View itemView, OnEntryListener onEntryListener) {
            super(itemView);
            nameOfEntry = itemView.findViewById(R.id.entry_name_textview);
            quantity = itemView.findViewById(R.id.entry_quantity_textview);
            unitOfQuantity = itemView.findViewById(R.id.entry_unit_of_quantity_textview);
            isDone = itemView.findViewById(R.id.entry_isDoneCheckbox);
            this.onEntryListener = onEntryListener;
            isDone.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if(isChecked) {
                    nameOfEntry.setPaintFlags(nameOfEntry.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    quantity.setPaintFlags(quantity.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    unitOfQuantity.setPaintFlags(unitOfQuantity.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                }else{
                    nameOfEntry.setPaintFlags(nameOfEntry.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                    quantity.setPaintFlags(quantity.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                    unitOfQuantity.setPaintFlags(unitOfQuantity.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                }
            });
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onEntryListener.onEntryClick(getAdapterPosition());
        }
    }
}

