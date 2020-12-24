package de.db.shoppinglist.repository;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import de.db.shoppinglist.database.FirebaseSource;
import de.db.shoppinglist.database.Source;
import de.db.shoppinglist.model.ShoppingEntry;
import de.db.shoppinglist.model.ShoppingList;

public class ShoppingRepository {

    private static ShoppingRepository instance;
    private Source db = new FirebaseSource();

    /**
     * Ensures that all viewmodels retrieve their information from the same source
     * @return Returns an instance of this singleton.
     */
    public static ShoppingRepository getInstance(){
        if(instance == null){
            instance = new ShoppingRepository();
        }
        return instance;
    }

    public boolean addEntry(String listId, ShoppingEntry newEntry){
        return db.addEntry(listId, newEntry);
    }

    public boolean deleteEntry(String listUid, String documentUid){
        return db.deleteEntry(listUid, documentUid);
    }

    public boolean addList(ShoppingList shoppingList){
        return db.addList(shoppingList);
    }

    public boolean deleteList(String listId){
        return db.deleteList(listId);
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

//    public MutableLiveData<IsDoneRelation> getIsDoneRelationOfList(String listId){
//        final MutableLiveData<IsDoneRelation> relation = new MutableLiveData<>();
//        db.getRelationOfDoneTasks(listId, (done, total) -> {
//            relation.setValue(new IsDoneRelation(total, done));
//        });
//        return relation;
//    }
}
