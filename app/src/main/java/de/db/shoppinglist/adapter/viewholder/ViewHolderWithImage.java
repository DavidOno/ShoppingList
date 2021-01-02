package de.db.shoppinglist.adapter.viewholder;


import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;

import de.db.shoppinglist.R;
import de.db.shoppinglist.adapter.FireShoppingListRecViewAdapter;
import de.db.shoppinglist.adapter.FireShoppingListRecViewAdapter.OnEntryListener;
import de.db.shoppinglist.model.ShoppingEntry;

public class ViewHolderWithImage extends FireShoppingListRecViewAdapter.ViewHolder{
        protected TextView nameOfEntry;
        protected TextView quantity;
        protected TextView unitOfQuantity;
        protected CheckBox isDone;
        protected ImageButton dropDown;
        protected TextView details;
        protected OnEntryListener onEntryListener;
        private FirestoreRecyclerAdapter adapter;

        public ViewHolderWithImage(@NonNull View itemView, OnEntryListener onEntryListener, FirestoreRecyclerAdapter adapter) {
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
        }

        @Override
        public void onClick(View v) {
            onEntryListener.onEntryClick(getAdapterPosition());
        }

    @Override
    public void onBindViewHolder(FireShoppingListRecViewAdapter.ViewHolder holder, int position, ShoppingEntry shoppingEntry) {

    }
}