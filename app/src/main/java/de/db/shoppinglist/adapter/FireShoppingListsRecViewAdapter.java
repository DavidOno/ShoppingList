package de.db.shoppinglist.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import de.db.shoppinglist.R;
import de.db.shoppinglist.model.ShoppingList;

public class FireShoppingListsRecViewAdapter extends FirestoreRecyclerAdapter<ShoppingList, FireShoppingListsRecViewAdapter.ViewHolder> {


    private OnListListener onListListener;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public FireShoppingListsRecViewAdapter(@NonNull FirestoreRecyclerOptions<ShoppingList> options, OnListListener onListListener) {
        super(options);
        this.onListListener = onListListener;
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int i, @NonNull ShoppingList shoppingList) {
        holder.nameOfShoppingList.setText(shoppingList.getName());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shoppinglist, parent, false);
        return new FireShoppingListsRecViewAdapter.ViewHolder(view, onListListener);
    }

    public interface OnListListener {
        void onListClick(int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView nameOfShoppingList;
        private OnListListener onListListener;

        public ViewHolder(@NonNull View itemView, OnListListener onListListener) {
            super(itemView);
            nameOfShoppingList = itemView.findViewById(R.id.item_list_name);
            this.onListListener = onListListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onListListener.onListClick(getAdapterPosition());
        }
    }
}
