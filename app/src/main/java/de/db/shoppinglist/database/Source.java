package de.db.shoppinglist.database;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.util.List;

import de.db.shoppinglist.model.ShoppingEntry;
import de.db.shoppinglist.model.ShoppingList;

public interface Source {

    public void addEntry(String listUid, ShoppingEntry entry);

    public void deleteEntry(String listUid, String documentUid);

    public void addList(ShoppingList shoppingList);

    public void deleteList(String listId);

    FirestoreRecyclerOptions<ShoppingEntry> getShoppingListRecyclerViewOptions(String listId);

    FirestoreRecyclerOptions<ShoppingList> getShoppingListsRecyclerViewOptions();

    void updateEntryPosition(ShoppingList list, ShoppingEntry entry, int position);

    void updateStatusDone(String listId, ShoppingEntry entry);

    void updateListName(ShoppingList list);

    void modifyWholeEntry(ShoppingList list, ShoppingEntry entry);

    List<ShoppingEntry> getHistory();
}

