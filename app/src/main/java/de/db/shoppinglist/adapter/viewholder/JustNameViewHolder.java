package de.db.shoppinglist.adapter.viewholder;

import android.graphics.Paint;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;

import de.db.shoppinglist.R;
import de.db.shoppinglist.adapter.FireShoppingListRecViewAdapter;
import de.db.shoppinglist.model.ShoppingEntry;

public class JustNameViewHolder extends FireShoppingListRecViewAdapter.ViewHolder {
    private TextView nameOfEntry;
    private TextView quantity;
    private CheckBox isDone;
    private ImageButton dropDown;
    private TextView details;
    private FireShoppingListRecViewAdapter.OnEntryListener onEntryListener;
    private FireShoppingListRecViewAdapter adapter;

    public JustNameViewHolder(@NonNull View itemView, FireShoppingListRecViewAdapter.OnEntryListener onEntryListener, FireShoppingListRecViewAdapter adapter) {
        super(itemView);
        findViewsById(itemView);
        this.onEntryListener = onEntryListener;
        itemView.setOnClickListener(this);
        this.adapter = adapter;
    }

    private void findViewsById(@NonNull View itemView) {
        nameOfEntry = itemView.findViewById(R.id.entry_name_textview);
        quantity = itemView.findViewById(R.id.entry_quantity_textview);
        isDone = itemView.findViewById(R.id.entry_isDoneCheckbox);
        dropDown = itemView.findViewById(R.id.entry_dropDownButton);
        details = itemView.findViewById(R.id.entry_details);
    }

    @Override
    public void onClick(View v) {
        onEntryListener.onEntryClick(getAdapterPosition());
    }

    @Override
    public void onBindViewHolder(FireShoppingListRecViewAdapter.ViewHolder holder, int position, ShoppingEntry shoppingEntry) {
        initHolderProperties(holder, shoppingEntry);
        onCheckedChangeListenerForDone(holder, shoppingEntry);
        strikeItemsThroughIfDone(holder);
        final boolean isExpanded = setVisibilityOfDetails(holder, position);
        textChangeListenerForDetails(holder);
        manageDropDownIconVisibility(holder);
        manageDropDownBehaviour(holder, position, isExpanded);
    }

    private void initHolderProperties(FireShoppingListRecViewAdapter.ViewHolder holder, ShoppingEntry shoppingEntry) {
        nameOfEntry.setText(shoppingEntry.getName());
        quantity.setText(getQuantityText(shoppingEntry.getQuantity()));
        isDone.setChecked(shoppingEntry.isDone());
        details.setText(shoppingEntry.getDetails());
    }


    private void onCheckedChangeListenerForDone(FireShoppingListRecViewAdapter.ViewHolder holder, ShoppingEntry shoppingEntry){
        isDone.setOnClickListener(view -> {
            adapter.setEntryContainingCheckedBox(shoppingEntry);
            adapter.setWasChecked(true);
        });
    }


    private boolean setVisibilityOfDetails(@NonNull FireShoppingListRecViewAdapter.ViewHolder holder, int position) {
        final boolean isExpanded = position == adapter.getExpandedPosition();
        details.setVisibility(isExpanded? View.VISIBLE:View.GONE);
        return isExpanded;
    }

    private void strikeItemsThroughIfDone(@NonNull FireShoppingListRecViewAdapter.ViewHolder holder) {
        if(isDone.isChecked()){
            strikeAllTextPropertiesThrough(holder);
        }else{
            unstrikeAllTextProperties(holder);
        }
    }

    private void unstrikeAllTextProperties(@NonNull FireShoppingListRecViewAdapter.ViewHolder holder) {
        nameOfEntry.setPaintFlags(nameOfEntry.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        quantity.setPaintFlags(quantity.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        details.setPaintFlags(details.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
    }

    private void strikeAllTextPropertiesThrough(@NonNull FireShoppingListRecViewAdapter.ViewHolder holder) {
        nameOfEntry.setPaintFlags(nameOfEntry.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        quantity.setPaintFlags(quantity.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        details.setPaintFlags(details.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
    }

    private void textChangeListenerForDetails(@NonNull FireShoppingListRecViewAdapter.ViewHolder holder) {
        details.addTextChangedListener(new TextWatcher() {
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

    private void manageDropDownIconVisibility(@NonNull FireShoppingListRecViewAdapter.ViewHolder holder) {
        if (details.getText().toString().isEmpty()) {
            dropDown.setVisibility(View.INVISIBLE);
        } else {
            dropDown.setVisibility(View.VISIBLE);
        }
    }

    private void manageDropDownBehaviour(@NonNull FireShoppingListRecViewAdapter.ViewHolder holder, int position, boolean isExpanded) {
        dropDown.setActivated(isExpanded);
        if(isExpanded){
            adapter.setPreviousExpandedPosition(position);
        }
        dropDown.setOnClickListener(v -> {
            adapter.setExpandedPosition(isExpanded ? -1:position);
            adapter.notifyItemChanged(adapter.getPreviousExpandedPosition());
            adapter.notifyItemChanged(position);
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
}
