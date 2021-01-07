package de.db.shoppinglist.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import de.db.shoppinglist.R;
import de.db.shoppinglist.model.ShoppingList;

public class FireSelectShoppingListModificationViewAdapter extends FirestoreRecyclerAdapter<ShoppingList, FireSelectShoppingListModificationViewAdapter.ViewHolder> {

    private MutableLiveData<Boolean> editClicked = new MutableLiveData<>(false);
    private MutableLiveData<Boolean> deleteClicked = new MutableLiveData<>(false);
    private ShoppingList clickedElement = null;


    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public FireSelectShoppingListModificationViewAdapter(@NonNull FirestoreRecyclerOptions<ShoppingList> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int i, @NonNull ShoppingList shoppingList) {
        holder.nameOfShoppingList.setText(shoppingList.getName());
        holder.edit.setOnClickListener(v -> {
            clickedElement = shoppingList;
            editClicked.setValue(true);
        });
        holder.delete.setOnClickListener(v -> {
            clickedElement = shoppingList;
            deleteClicked.setValue(true);
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_select_shoppinglist, parent, false);
        return new ViewHolder(view);
    }

    public MutableLiveData<Boolean> getEditClicked(){
        return editClicked;
    }

    public MutableLiveData<Boolean> getDeleteClicked(){
        return deleteClicked;
    }

    public ShoppingList getClickedElement() {
        return clickedElement;
    }

    public void resetFlags() {
        clickedElement = null;
        editClicked.setValue(false);
        deleteClicked.setValue(false);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        private TextView nameOfShoppingList;
        private ImageButton edit;
        private ImageButton delete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameOfShoppingList = itemView.findViewById(R.id.item_list_modify_name);
            edit = itemView.findViewById(R.id.item_list_modify_edit_button);
            delete =  itemView.findViewById(R.id.item_list_modify_delete_button);
        }
    }
}
