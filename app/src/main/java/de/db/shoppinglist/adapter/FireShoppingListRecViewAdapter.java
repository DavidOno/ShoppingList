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

import de.db.shoppinglist.R;
import de.db.shoppinglist.model.ShoppingEntry;

public class FireShoppingListRecViewAdapter extends FirestoreRecyclerAdapter<ShoppingEntry, FireShoppingListRecViewAdapter.ViewHolder> implements Checkable<ShoppingEntry> {

    private MutableLiveData<Boolean> wasChecked = new MutableLiveData<>(false);
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
    public FireShoppingListRecViewAdapter(@NonNull FirestoreRecyclerOptions<ShoppingEntry> options, OnEntryListener onEntryListener) {
        super(options);
        this.onEntryListener = onEntryListener;

    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull ShoppingEntry shoppingEntry) {
        initHolderProperties(holder, shoppingEntry);
        onCheckedChangeListenerForDone(holder, shoppingEntry);
        strikeItemsThroughIfDone(holder);
        final boolean isExpanded = setVisibilityOfDetails(holder, position);
        textChangeListenerForDetails(holder);
        manageDropDownIconVisibility(holder);
        manageDropDownBehaviour(holder, position, isExpanded);
    }

    private void onCheckedChangeListenerForDone(@NonNull ViewHolder holder, @NonNull ShoppingEntry shoppingEntry) {
        holder.isDone.setOnClickListener(view -> {
            entryContainingCheckedBox = shoppingEntry;
            wasChecked.setValue(true);
        });
    }

    private boolean setVisibilityOfDetails(@NonNull ViewHolder holder, int position) {
        final boolean isExpanded = position== expandedPosition;
        holder.details.setVisibility(isExpanded? View.VISIBLE:View.GONE);
        return isExpanded;
    }

    private void strikeItemsThroughIfDone(@NonNull ViewHolder holder) {
        if(holder.isDone.isChecked()){
            strikeAllTextPropertiesThrough(holder);
        }else{
            unstrikeAllTextProperties(holder);
        }
    }

    private void unstrikeAllTextProperties(@NonNull ViewHolder holder) {
        holder.nameOfEntry.setPaintFlags(holder.nameOfEntry.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        holder.quantity.setPaintFlags(holder.quantity.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        holder.unitOfQuantity.setPaintFlags(holder.unitOfQuantity.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        holder.details.setPaintFlags(holder.details.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
    }

    private void strikeAllTextPropertiesThrough(@NonNull ViewHolder holder) {
        holder.nameOfEntry.setPaintFlags(holder.nameOfEntry.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        holder.quantity.setPaintFlags(holder.quantity.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        holder.unitOfQuantity.setPaintFlags(holder.unitOfQuantity.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        holder.details.setPaintFlags(holder.details.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
    }

    private void textChangeListenerForDetails(@NonNull ViewHolder holder) {
        holder.details.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //not required
            }

            @Override
            public void onTextChanged(CharSequence sequence, int start, int before, int count) {
                manageDropDownIconVisibility(holder);
            }

            @Override
            public void afterTextChanged(Editable s) {
                //not required
            }
        });
    }

    private void initHolderProperties(@NonNull ViewHolder holder, @NonNull ShoppingEntry shoppingEntry) {
        holder.nameOfEntry.setText(shoppingEntry.getName());
        holder.quantity.setText(getQuantityText(shoppingEntry.getQuantity()));
        holder.unitOfQuantity.setText(shoppingEntry.getUnitOfQuantity());
        holder.isDone.setChecked(shoppingEntry.isDone());
        holder.details.setText(shoppingEntry.getDetails());
    }

    private void manageDropDownIconVisibility(@NonNull ViewHolder holder) {
        if (holder.details.getText().toString().isEmpty()) {
            holder.dropDown.setVisibility(View.INVISIBLE);
        } else {
            holder.dropDown.setVisibility(View.VISIBLE);
        }
    }

    private void manageDropDownBehaviour(@NonNull ViewHolder holder, int position, boolean isExpanded) {
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
        if(isZero(quantity)){
            return "";
        }else{
            if(isInteger(quantity)){
                int quantityAsInt = (int) quantity;
                return quantityAsInt + " x ";
            }
            return quantity + " x ";
        }
    }

    private boolean isInteger(float quantity) {
        return (int)quantity == quantity;
    }

    private boolean isZero(float quantity) {
        return quantity - 0 < 0.0001;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_entry, parent, false);
        return new ViewHolder(view, onEntryListener);
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
        wasChecked.setValue(false);
        entryContainingCheckedBox = null;
    }

    public interface OnEntryListener {
        void onEntryClick(int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView nameOfEntry;
        private TextView quantity;
        private TextView unitOfQuantity;
        private CheckBox isDone;
        private ImageButton dropDown;
        private TextView details;
        private OnEntryListener onEntryListener;

        public ViewHolder(@NonNull View itemView, OnEntryListener onEntryListener) {
            super(itemView);
            findViewsById(itemView);
            this.onEntryListener = onEntryListener;
            itemView.setOnClickListener(this);
        }

        private void findViewsById(@NonNull View itemView) {
            nameOfEntry = itemView.findViewById(R.id.entry_name_textview);
            quantity = itemView.findViewById(R.id.entry_quantity_textview);
            unitOfQuantity = itemView.findViewById(R.id.entry_unit_of_quantity_textview);
            isDone = itemView.findViewById(R.id.entry_isDoneCheckbox);
            dropDown = itemView.findViewById(R.id.entry_dropDownButton);
            details = itemView.findViewById(R.id.entry_details);
        }

        @Override
        public void onClick(View v) {
            onEntryListener.onEntryClick(getAdapterPosition());
        }
    }
}
