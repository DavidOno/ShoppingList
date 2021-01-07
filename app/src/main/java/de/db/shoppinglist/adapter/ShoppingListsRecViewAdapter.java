package de.db.shoppinglist.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import de.db.shoppinglist.R;
import de.db.shoppinglist.model.ShoppingList;
import de.db.shoppinglist.view.ShoppingListsFragmentDirections;

public class ShoppingListsRecViewAdapter extends FirestoreRecyclerAdapter<ShoppingList, ShoppingListsRecViewAdapter.ViewHolder> {

    private NavController navController;
    private OnListListener onListListener;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public ShoppingListsRecViewAdapter(@NonNull FirestoreRecyclerOptions<ShoppingList> options, OnListListener onListListener, NavController navController) {
        super(options);
        this.onListListener = onListListener;
        this.navController = navController;
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int i, @NonNull ShoppingList shoppingList) {
        holder.nameOfShoppingList.setText(shoppingList.getName());
        holder.relation.setText((shoppingList.getDone() +"/"+shoppingList.getTotal()));
        holder.shareButton.setOnClickListener(v -> openDialog(shoppingList));
    }

    private void openDialog(ShoppingList list) {
        NavDirections modifyImageDirection = ShoppingListsFragmentDirections.actionShoppingListsFragmentToShareDialog(list);
        navController.navigate(modifyImageDirection);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shoppinglist, parent, false);
        return new ViewHolder(view, onListListener);
    }

    public interface OnListListener {
        void onListClick(int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView nameOfShoppingList;
        private TextView relation;
        private ImageButton shareButton;
        private OnListListener onListListener;

        public ViewHolder(@NonNull View itemView, OnListListener onListListener) {
            super(itemView);
            nameOfShoppingList = itemView.findViewById(R.id.item_list_name);
            relation = itemView.findViewById(R.id.item_list_counter);
            shareButton = itemView.findViewById(R.id.item_list_share);
            this.onListListener = onListListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onListListener.onListClick(getAdapterPosition());
        }
    }
}
