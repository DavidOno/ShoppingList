package de.db.shoppinglist.database;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.util.List;
import java.util.function.Consumer;

import de.db.shoppinglist.model.EntryHistoryElement;
import de.db.shoppinglist.model.ShoppingEntry;
import de.db.shoppinglist.model.ShoppingList;

public interface Source {

    void addEntry(String listUid, ShoppingEntry entry);

    void deleteEntry(String listUid, String documentUid);

    void addList(ShoppingList shoppingList);

    void deleteList(String listId);

    FirestoreRecyclerOptions<ShoppingEntry> getShoppingListRecyclerViewOptions(String listId);

    FirestoreRecyclerOptions<ShoppingList> getShoppingListsRecyclerViewOptions();

    void updateEntryPosition(ShoppingList list, ShoppingEntry entry, int position);

    void updateStatusDone(String listId, ShoppingEntry entry);

    void updateListName(ShoppingList list);

    void modifyWholeEntry(ShoppingList list, ShoppingEntry entry);

    void getHistory(Consumer<List<EntryHistoryElement>> callback);

    void deleteHistory();

    void deleteAllLists();
}

