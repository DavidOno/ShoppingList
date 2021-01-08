package de.db.shoppinglist.database;

import android.content.Context;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.util.List;
import java.util.function.Consumer;

import de.db.shoppinglist.model.EntryHistoryElement;
import de.db.shoppinglist.model.ShoppingEntry;
import de.db.shoppinglist.model.ShoppingList;

/**
 * This interface is responsible for providing basic CRUD functionalities
 * for shopping-lists and shopping-entries.
 * Every user has his own domain, where he can manage his shopping-lists.
 * All functions automatically write inside the user-specific domain.
 */
public interface Source {


    /**
     * Adds an entry to a specific list.
     *
     * @param listUid The list-id, to which this entry should be added.
     * @param entry   The new entry, which should be added.
     * @param context The application context.
     */
    void addEntry(String listUid, ShoppingEntry entry, Context context);

    /**
     * Deletes an entry from a specific list.
     *
     * @param listUid     Id of the list, containing this entry.
     * @param documentUid Id of the entry, which should be deleted.
     */
    void deleteEntry(String listUid, String documentUid);

    /**
     * Adds a new shopping-list to database.
     *
     * @param shoppingList The new shopping-list.
     */
    void addList(ShoppingList shoppingList);

    /**
     * Deletes a list from database.
     *
     * @param listId Id of the list, which is supposed to be deleted.
     */
    void deleteList(String listId);

    /**
     * Build the FirestoreRecyclerOptions, used in {@link com.firebase.ui.firestore.FirestoreRecyclerAdapter}.
     * Due to this options the FirestoreRecyclerAdapter knows which entries of a list to display.
     *
     * @param listId Id of the list, form which the entries are supposed to be displayed.
     * @return Returns the options, containing a query, which data should be displayed.
     */
    FirestoreRecyclerOptions<ShoppingEntry> getShoppingListRecyclerViewOptions(String listId);

    /**
     * Build the FirestoreRecyclerOptions, used in {@link com.firebase.ui.firestore.FirestoreRecyclerAdapter}.
     * Due to this options the FirestoreRecyclerAdapter knows which shopping-lists to display.
     *
     * @return Returns the options, containing a query, which data should be displayed.
     */
    FirestoreRecyclerOptions<ShoppingList> getShoppingListsRecyclerViewOptions();

    /**
     * Updates the position of an entry within a shopping-list.
     *
     * @param list     The shopping-list, were the element is part of.
     * @param entry    The entry, where the position is supposed to be updated.
     * @param position The new position.-
     */
    void updateEntryPosition(ShoppingList list, ShoppingEntry entry, int position);

    /**
     * Updates if the entry is done or not.
     *
     * @param listId Id of the list containing the entry.
     * @param entry  The entry, with the new done-status.
     */
    void updateStatusDone(String listId, ShoppingEntry entry);

    /**
     * Updates the name of a list.
     *
     * @param list The list, containing the new name.
     */
    void updateListName(ShoppingList list);

    /**
     * Updates the complete entry.
     *
     * @param list    List containing the entry, which is supposed to be updated.
     * @param entry   The modified entry.
     * @param context The application context.
     */
    void modifyWholeEntry(ShoppingList list, ShoppingEntry entry, Context context);

    /**
     * Retrieves the complete history from firebase.
     * Since the implementation runs asynchronous, nothing is returned,
     * but instead the caller has to provide a callback.
     *
     * @param callback A callback to store the retrieved history.
     */
    void getHistory(Consumer<List<EntryHistoryElement>> callback);

    /**
     * Deletes complete history.
     */
    void deleteHistory();

    /**
     * Deletes all lists and the corresponding entries.
     */
    void deleteAllLists();

    /**
     * Uploads Image to Firebase Storage.
     *
     * @param listName Name of the shopping list
     * @param entry    Entry which
     * @param context  Context of the image-uri
     */
    void uploadImage(String listName, ShoppingEntry entry, Context context);

    /**
     * Deletes a specific history entry.
     *
     * @param historyEntry The history entry, which should be deleted.
     */
    void deleteHistoryEntry(EntryHistoryElement historyEntry);
}

