package de.db.shoppinglist.adapter.viewholder;


import android.graphics.Paint;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;

import de.db.shoppinglist.R;
import de.db.shoppinglist.adapter.ShoppingListRecViewAdapter;
import de.db.shoppinglist.adapter.ShoppingListRecViewAdapter.OnEntryListener;
import de.db.shoppinglist.model.ShoppingEntry;

import static de.db.shoppinglist.adapter.viewholder.DefaultViewHolder.*;

/**
 * The ViewHolderWithImage is used to display a shopping-entry within a shopping list({@link ShoppingListRecViewAdapter}.
 * In this case the name, the quantity, the unit of quantity, the details, the image and the status if it was bought are displayed.
 * the main difference to the {@link DefaultViewHolder} lies in the additional display of an image.
 */
public class ViewHolderWithImage extends ShoppingListRecViewAdapter.ViewHolder {
    private TextView nameOfEntry;
    private TextView quantity;
    private TextView unitOfQuantity;
    private CheckBox isDone;
    private ImageButton dropDown;
    private TextView details;
    private ImageView imageView;
    private OnEntryListener onEntryListener;
    private ShoppingListRecViewAdapter adapter;

    /**
     * Instantiates the ViewHolderWithImage.
     *
     * @param itemView        View representing the layout.
     * @param onEntryListener Listener for clicks for each entry.
     * @param adapter         The RecyclerViewAdapter, which uses this view holder.
     */
    public ViewHolderWithImage(@NonNull View itemView, OnEntryListener onEntryListener, ShoppingListRecViewAdapter adapter) {
        super(itemView);
        findViewsById(itemView);
        this.onEntryListener = onEntryListener;
        itemView.setOnClickListener(this);
        this.adapter = adapter;
    }

    private void findViewsById(@NonNull View itemView) {
        nameOfEntry = itemView.findViewById(R.id.entry_name_textview);
        quantity = itemView.findViewById(R.id.entry_quantity_textview);
        unitOfQuantity = itemView.findViewById(R.id.entry_unit_of_quantity_textview);
        isDone = itemView.findViewById(R.id.entry_isDoneCheckbox);
        dropDown = itemView.findViewById(R.id.entry_dropDownButton);
        details = itemView.findViewById(R.id.entry_details);
        imageView = itemView.findViewById(R.id.entry_image);
    }

    @Override
    public void onClick(View v) {
        onEntryListener.onEntryClick(getAdapterPosition());
    }

    /**
     * Should not be called by client.
     * Binds action/properties to the selected item.
     *
     * @param holder        The viewHolder, to which action should be binded. Not used.
     * @param position      The position of the item within the recyclerview.
     * @param shoppingEntry The shopping-entry represented by the current item.
     */
    @Override
    public void onBindViewHolder(ShoppingListRecViewAdapter.ViewHolder holder, int position, ShoppingEntry shoppingEntry) {
        initHolderProperties(holder, shoppingEntry);
        onCheckedChangeListenerForDone(shoppingEntry);
        strikeItemsThroughIfDone();
        final boolean isExpanded = setVisibilityOfDetails(position);
        textChangeListenerForDetails();
        manageDropDownIconVisibility();
        manageDropDownBehaviour(position, isExpanded);
    }

    private void initHolderProperties(ShoppingListRecViewAdapter.ViewHolder holder, ShoppingEntry shoppingEntry) {
        nameOfEntry.setText(shoppingEntry.getName());
        quantity.setText(getQuantityText(shoppingEntry.getQuantity()));
        unitOfQuantity.setText(shoppingEntry.getUnitOfQuantity());
        isDone.setChecked(shoppingEntry.isDone());
        details.setText(shoppingEntry.getDetails());
        Glide.with(holder.itemView.getContext())
                .load(shoppingEntry.getImageURI())
                .skipMemoryCache(false)
                .into(imageView);
    }

    private String getQuantityText(float quantity) {
        if (isZero(quantity)) {
            return NO_TEXT;
        } else {
            if (isInteger(quantity)) {
                int quantityAsInt = (int) quantity;
                return quantityAsInt + MULTIPLIER;
            }
            return quantity + MULTIPLIER;
        }
    }

    private boolean isInteger(float quantity) {
        return (int) quantity == quantity;
    }

    private boolean isZero(float quantity) {
        return quantity - 0 < EPSILON;
    }

    private void onCheckedChangeListenerForDone(ShoppingEntry shoppingEntry) {
        isDone.setOnClickListener(view -> {
            adapter.setEntryContainingCheckedBox(shoppingEntry);
            adapter.setWasChecked(true);
        });
    }


    private boolean setVisibilityOfDetails(int position) {
        final boolean isExpanded = position == adapter.getExpandedPosition();
        details.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        return isExpanded;
    }

    private void strikeItemsThroughIfDone() {
        if (isDone.isChecked()) {
            strikeAllTextPropertiesThrough();
        } else {
            unstrikeAllTextProperties();
        }
    }

    private void unstrikeAllTextProperties() {
        nameOfEntry.setPaintFlags(nameOfEntry.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        quantity.setPaintFlags(quantity.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        unitOfQuantity.setPaintFlags(unitOfQuantity.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        details.setPaintFlags(details.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
    }

    private void strikeAllTextPropertiesThrough() {
        nameOfEntry.setPaintFlags(nameOfEntry.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        quantity.setPaintFlags(quantity.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        unitOfQuantity.setPaintFlags(unitOfQuantity.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        details.setPaintFlags(details.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
    }

    private void textChangeListenerForDetails() {
        details.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //not required
            }

            @Override
            public void onTextChanged(CharSequence sequence, int start, int before, int count) {
                manageDropDownIconVisibility();
            }

            @Override
            public void afterTextChanged(Editable s) {
                //not required
            }
        });
    }

    private void manageDropDownIconVisibility() {
        if (details.getText().toString().isEmpty()) {
            dropDown.setVisibility(View.INVISIBLE);
        } else {
            dropDown.setVisibility(View.VISIBLE);
        }
    }

    private void manageDropDownBehaviour(int position, boolean isExpanded) {
        dropDown.setActivated(isExpanded);
        if (isExpanded) {
            adapter.setPreviousExpandedPosition(position);
        }
        dropDown.setOnClickListener(v -> {
            adapter.setExpandedPosition(isExpanded ? -1 : position);
            adapter.notifyItemChanged(adapter.getPreviousExpandedPosition());
            adapter.notifyItemChanged(position);
        });
    }

}