//package de.db.shoppinglist.adapter;
//
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import de.db.shoppinglist.R;
//import de.db.shoppinglist.model.ShoppingList;
//
//public class ShoppingListsRecViewAdapter extends RecyclerView.Adapter<ShoppingListsRecViewAdapter.ViewHolder>{
//
//    private List<ShoppingList> shoppingLists = new ArrayList<>();
//    private OnListListener onListListener;
//
//    public ShoppingListsRecViewAdapter(OnListListener onListListener) {
//        this.onListListener = onListListener;
//    }
//
//    @NonNull
//    @Override
//    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shoppinglist, parent, false);
//        return new ViewHolder(view, onListListener);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        holder.nameOfShoppingList.setText(shoppingLists.get(position).getName());
//    }
//
//    @Override
//    public int getItemCount() {
//        return shoppingLists.size();
//    }
//
//    public void addShoppingList(String name){
//        shoppingLists.add(new ShoppingList(name));
//        notifyDataSetChanged();
//    }
//
//    public void deleteAll(){
//        shoppingLists.clear();
//        notifyDataSetChanged();
//    }
//
//    public interface OnListListener {
//        void onListClick(int position);
//    }
//
//    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
//
//        private TextView nameOfShoppingList;
//        private OnListListener onListListener;
//
//        public ViewHolder(@NonNull View itemView, OnListListener onListListener) {
//            super(itemView);
//            nameOfShoppingList = itemView.findViewById(R.id.shoppingList_ListItem);
//            this.onListListener = onListListener;
//            itemView.setOnClickListener(this);
//        }
//
//        @Override
//        public void onClick(View v) {
//            onListListener.onListClick(getAdapterPosition());
//        }
//    }
//}
