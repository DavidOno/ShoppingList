package de.db.shoppinglist.adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import de.db.shoppinglist.R;
import de.db.shoppinglist.adapter.viewholder.DefaultViewHolder;
import de.db.shoppinglist.adapter.viewholder.JustNameViewHolder;
import de.db.shoppinglist.adapter.viewholder.ViewHolderWithImage;
import de.db.shoppinglist.model.ShoppingEntry;

/**
 * Recyclerview, for displaying entries, within a shopping-list.
 * Since this recyclerview extends FirestoreRecyclerAdapter, it's updated immediatly after a
 * database change occurred.
 */
public class ShoppingListRecViewAdapter extends FirestoreRecyclerAdapter<ShoppingEntry, ShoppingListRecViewAdapter.ViewHolder> implements Checkable<ShoppingEntry> {


    private static final int DEFAULT_VIEW_HOLDER = 0;
    private static final int IMAGE_VIEW_HOLDER = 1;
    private static final int JUST_NAME_VIEW_HOLDER = 2;
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
    public ShoppingListRecViewAdapter(@NonNull FirestoreRecyclerOptions<ShoppingEntry> options, OnEntryListener onEntryListener) {
        super(options);
        this.onEntryListener = onEntryListener;

    }

    /**
     * This method decides, based on the non-null properties of an entry, which layout would be appropriate.
     *
     * @param position Position of the item.
     * @return Returns a constant, representing a specific viewholder-layout.
     */
    @Override
    public int getItemViewType(int position) {
        ShoppingEntry item = getItem(position);
        if (hasItemImage(item)) {
            return IMAGE_VIEW_HOLDER;
        } else if (hasMissingUnitOfQuantity(item)) {
            return JUST_NAME_VIEW_HOLDER;
        } else {
            return DEFAULT_VIEW_HOLDER;
        }
    }

    private boolean hasMissingUnitOfQuantity(ShoppingEntry item) {
        return item.getUnitOfQuantity().isEmpty();
    }

    private boolean hasItemImage(ShoppingEntry item) {
        return item.getImageURI() != null && !item.getImageURI().isEmpty();
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull ShoppingEntry shoppingEntry) {
        holder.onBindViewHolder(holder, position, shoppingEntry);
    }

    /**
     * Chooses which layout should be inflates, based on the viewtype.
     *
     * @param parent   Parent-viewgroup.
     * @param viewType viewtype.
     * @return Returns the inflated viewholder.
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == DEFAULT_VIEW_HOLDER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_entry, parent, false);
            return new DefaultViewHolder(view, onEntryListener, this);
        } else if (viewType == IMAGE_VIEW_HOLDER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_entry_with_image, parent, false);
            return new ViewHolderWithImage(view, onEntryListener, this);
        } else if (viewType == JUST_NAME_VIEW_HOLDER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_entry_just_name, parent, false);
            return new JustNameViewHolder(view, onEntryListener, this);
        }
        throw new IllegalArgumentException("Found no suitable viewType");
    }

    public void setPreviousExpandedPosition(int previousExpandedPosition) {
        this.previousExpandedPosition = previousExpandedPosition;
    }

    public void setExpandedPosition(int expandedPosition) {
        this.expandedPosition = expandedPosition;
    }

    public int getPreviousExpandedPosition() {
        return previousExpandedPosition;
    }

    public int getExpandedPosition() {
        return expandedPosition;
    }

    public void setWasChecked(boolean wasCheckedBoolean) {
        wasChecked.setValue(wasCheckedBoolean);
    }

    public void setEntryContainingCheckedBox(ShoppingEntry entry) {
        entryContainingCheckedBox = entry;
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

    /**
     * This abstract class is the base class for all potential viewholder.
     * Since all properties, except the entry name, are optional, it's advised to use different
     * layouts.
     */
    public abstract static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public abstract void onBindViewHolder(ViewHolder holder, int position, ShoppingEntry shoppingEntry);
    }


}
