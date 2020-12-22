package de.db.shoppinglist.database;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import de.db.shoppinglist.model.ShoppingEntry;
import de.db.shoppinglist.model.ShoppingList;

public interface Source {

    public boolean addEntry(String listUid, ShoppingEntry entry);

    public boolean deleteEntry(String listUid, String documentUid);

    public boolean addList(ShoppingList shoppingList);

    public boolean modifyList();

    public boolean deleteList(String listId);

    FirestoreRecyclerOptions<ShoppingEntry> getShoppingListRecyclerViewOptions(String listId);

    FirestoreRecyclerOptions<ShoppingList> getShoppingListsRecyclerViewOptions();
}
