package de.db.shoppinglist.adapter;

import android.graphics.Paint;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.util.Optional;

import de.db.shoppinglist.R;
import de.db.shoppinglist.model.ShoppingEntry;

public class FireShoppingListRecViewAdapter extends FirestoreRecyclerAdapter<ShoppingEntry, FireShoppingListRecViewAdapter.ViewHolder> implements Checkable<ShoppingEntry> {

    private MutableLiveData<Boolean> wasChecked = new MutableLiveData<>();
    private ShoppingEntry entryContainingCheckedBox = null;
    private OnEntryListener onEntryListener;
    private int previousExpandedPosition = -1;
    private int expandedPosition = -1;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options Options on how to query the underlying data.
     */
    public FireShoppingListRecViewAdapter(@NonNull FirestoreRecyclerOptions options, OnEntryListener onEntryListener) {
        super(options);
        this.onEntryListener = onEntryListener;
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull ShoppingEntry shoppingEntry) {
        holder.nameOfEntry.setText(shoppingEntry.getName());
        holder.quantity.setText(getQuantityText(shoppingEntry.getQuantity()));
        holder.unitOfQuantity.setText(shoppingEntry.getUnitOfQuantity());
        holder.isDone.setChecked(shoppingEntry.isDone());
        holder.details.setText(shoppingEntry.getDetails());

        holder.isDone.setOnCheckedChangeListener((buttonView, isChecked) -> {
            entryContainingCheckedBox = shoppingEntry;
            wasChecked.setValue(true);
        });

        if(holder.isDone.isChecked()){
            holder.nameOfEntry.setPaintFlags(holder.nameOfEntry.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.quantity.setPaintFlags(holder.quantity.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.unitOfQuantity.setPaintFlags(holder.unitOfQuantity.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.details.setPaintFlags(holder.details.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }else{
            holder.nameOfEntry.setPaintFlags(holder.nameOfEntry.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            holder.quantity.setPaintFlags(holder.quantity.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            holder.unitOfQuantity.setPaintFlags(holder.unitOfQuantity.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            holder.details.setPaintFlags(holder.details.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }
        final boolean isExpanded = position== expandedPosition;
        holder.details.setVisibility(isExpanded?View.VISIBLE:View.GONE);
        holder.details.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //not required
            }

            @Override
            public void onTextChanged(CharSequence sequence, int start, int before, int count) {
                if(holder.details.getText().toString().isEmpty()){
                    holder.dropDown.setVisibility(View.INVISIBLE);
                }else{
                    holder.dropDown.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //not required
            }
        });
        if(holder.details.getText().toString().isEmpty()){
            holder.dropDown.setVisibility(View.INVISIBLE);
        }else{
            holder.dropDown.setVisibility(View.VISIBLE);
        }
        holder.dropDown.setActivated(isExpanded);
        if(isExpanded){
            previousExpandedPosition = position;
        }
        holder.dropDown.setOnClickListener(v -> {
            expandedPosition = isExpanded ? -1:position;
            notifyItemChanged(previousExpandedPosition);
            notifyItemChanged(position);
        });
    }

    private String getQuantityText(float quantity) {
        if(quantity - 0 < 0.0001){
            return "";
        }else{
            return quantity + " x ";
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_entry, parent, false);
        return new FireShoppingListRecViewAdapter.ViewHolder(view, onEntryListener);
    }

    @Override
    public LiveData<Boolean> getFlag() {
        return wasChecked;
    }

    @Override
    public ShoppingEntry getItem() {
        return entryContainingCheckedBox;
    }

    @Override
    public void resetFlags() {
        wasChecked = new MutableLiveData<>();
        wasChecked.setValue(false);
        entryContainingCheckedBox = null;
    }

    public interface OnEntryListener {
        void onEntryClick(int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView nameOfEntry;
        private TextView quantity;
        private TextView unitOfQuantity;
        private CheckBox isDone;
        private ImageButton dropDown;
        private TextView details;
        private OnEntryListener onEntryListener;

        public ViewHolder(@NonNull View itemView, OnEntryListener onEntryListener) {
            super(itemView);
            nameOfEntry = itemView.findViewById(R.id.entry_name_textview);
            quantity = itemView.findViewById(R.id.entry_quantity_textview);
            unitOfQuantity = itemView.findViewById(R.id.entry_unit_of_quantity_textview);
            isDone = itemView.findViewById(R.id.entry_isDoneCheckbox);
            dropDown = itemView.findViewById(R.id.entry_dropDownButton);
            details = itemView.findViewById(R.id.entry_details);

            this.onEntryListener = onEntryListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onEntryListener.onEntryClick(getAdapterPosition());
        }
    }
}
