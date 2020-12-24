package de.db.shoppinglist.database;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import de.db.shoppinglist.model.ShoppingEntry;
import de.db.shoppinglist.model.ShoppingList;

public interface Source {

    public boolean addEntry(String listUid, ShoppingEntry entry, boolean isPartOfModify);

    public boolean deleteEntry(String listUid, String documentUid, boolean isPartOfModify);

    public boolean addList(ShoppingList shoppingList);

    public boolean modifyList();

    public boolean deleteList(String listId);

    FirestoreRecyclerOptions<ShoppingEntry> getShoppingListRecyclerViewOptions(String listId);

    FirestoreRecyclerOptions<ShoppingList> getShoppingListsRecyclerViewOptions();

    void updateEntryPosition(ShoppingList list, ShoppingEntry entry, int position);

    void updateStatusDone(String listId, ShoppingEntry entry);

    void updateListName(ShoppingList list);

    void modifyWholeEntry(ShoppingList list, ShoppingEntry entry);
}

