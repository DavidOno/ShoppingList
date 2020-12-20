package de.db.shoppinglist.adapter;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import de.db.shoppinglist.R;
import de.db.shoppinglist.model.ShoppingEntry;

public class FireShoppingListRecViewAdapter extends FirestoreRecyclerAdapter<ShoppingEntry, FireShoppingListRecViewAdapter.ViewHolder> {

    private OnEntryListener onEntryListener;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public FireShoppingListRecViewAdapter(@NonNull FirestoreRecyclerOptions options, OnEntryListener onEntryListener) {
        super(options);
        this.onEntryListener = onEntryListener;
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int i, @NonNull ShoppingEntry shoppingEntry) {
        holder.nameOfEntry.setText(shoppingEntry.getName());
        holder.quantity.setText(getQuantityText(shoppingEntry.getQuantity()));
        holder.unitOfQuantity.setText(shoppingEntry.getUnitOfQuantity());
        holder.isDone.setChecked(shoppingEntry.isDone());
    }

    private String getQuantityText(float quantity) {
        if(quantity - 0 < 0.0001){
            return "";
        }else{
            return String.valueOf(quantity) + " x ";
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_entry, parent, false);
        return new FireShoppingListRecViewAdapter.ViewHolder(view, onEntryListener);
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
