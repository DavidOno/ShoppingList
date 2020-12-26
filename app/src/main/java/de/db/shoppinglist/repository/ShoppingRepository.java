package de.db.shoppinglist.repository;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.util.List;

import de.db.shoppinglist.database.FirebaseSource;
import de.db.shoppinglist.database.Source;
import de.db.shoppinglist.model.ShoppingEntry;
import de.db.shoppinglist.model.ShoppingList;

public class ShoppingRepository {

    private static ShoppingRepository instance;
    private Source db = new FirebaseSource();

    /**
     * Ensures that all viewmodels retrieve their information from the same source.
     * @return Returns instance of this singleton.
     */
    public static ShoppingRepository getInstance(){
        if(instance == null){
            instance = new ShoppingRepository();
        }
        return instance;
    }

    public void addEntry(String listId, ShoppingEntry newEntry){
        db.addEntry(listId, newEntry);
    }

    public void deleteEntry(String listUid, String documentUid){
        db.deleteEntry(listUid, documentUid);
    }

    public void addList(ShoppingList shoppingList){
        db.addList(shoppingList);
    }

    public void deleteList(String listId){
        db.deleteList(listId);
    }

    public FirestoreRecyclerOptions<ShoppingEntry> getRecyclerViewOptions(String listId) {
        return db.getShoppingListRecyclerViewOptions(listId);
    }

    public FirestoreRecyclerOptions<ShoppingList> getShoppingListsRecyclerViewOptions() {
        return db.getShoppingListsRecyclerViewOptions();
    }

    public void updateEntryPosition(ShoppingList list, ShoppingEntry entry, int position) {
        db.updateEntryPosition(list, entry, position);
    }

    public void updateDoneStatus(String listId, ShoppingEntry entry) {
        db.updateStatusDone(listId, entry);
    }

    public void updateListName(ShoppingList list) {
        db.updateListName(list);
    }

    public void modifyWholeEntry(ShoppingList list, ShoppingEntry entry) {
        db.modifyWholeEntry(list, entry);
    }

    public List<ShoppingEntry> getHistory() {
        return db.getHistory();
    }
}
