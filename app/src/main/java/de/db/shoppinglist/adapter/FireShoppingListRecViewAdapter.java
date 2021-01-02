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
import de.db.shoppinglist.adapter.viewholder.DefaultViewHolder;
import de.db.shoppinglist.adapter.viewholder.ViewHolderWithImage;
import de.db.shoppinglist.model.ShoppingEntry;


public class FireShoppingListRecViewAdapter extends FirestoreRecyclerAdapter<ShoppingEntry, FireShoppingListRecViewAdapter.ViewHolder> implements Checkable<ShoppingEntry> {


    private static final int DEFAULT_VIEWHOLDER = 0;
    private static final int IMAGE_VIEWHOLDER = 1;
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
    public int getItemViewType(int position) {
        ShoppingEntry item = getItem(position);
        if(hasItemImage(item)){
            return IMAGE_VIEWHOLDER;
        }else{
            return DEFAULT_VIEWHOLDER;
        }
    }

    private boolean hasItemImage(ShoppingEntry item) {
        return item.getImageURI() == null || item.getImageURI().isEmpty();
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull ShoppingEntry shoppingEntry) {
        holder.onBindViewHolder(holder, position, shoppingEntry);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        if(viewType == DEFAULT_VIEWHOLDER) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_entry, parent, false);
            return new DefaultViewHolder(view, onEntryListener, this);
        }
        else if(viewType == IMAGE_VIEWHOLDER) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_entry_with_image, parent, false);
            return new ViewHolderWithImage(view, onEntryListener, this);
        }
        throw new IllegalArgumentException("Found no suitable viewType");
    }

    public void setPreviousExpandedPosition(int previousExpandedPosition){
        this.previousExpandedPosition = previousExpandedPosition;
    }

    public void setExpandedPosition(int expandedPosition){
        this.expandedPosition = expandedPosition;
    }

    public int getPreviousExpandedPosition(){
        return previousExpandedPosition;
    }

    public int getExpandedPosition(){
        return expandedPosition;
    }

    public void setWasChecked(boolean wasCheckedBoolean){
        wasChecked.setValue(wasCheckedBoolean);
    }

    public void setEntryContainingCheckedBox(ShoppingEntry entry){
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

    public static abstract class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public abstract void onBindViewHolder(ViewHolder holder, int position, ShoppingEntry shoppingEntry);
    }









}
