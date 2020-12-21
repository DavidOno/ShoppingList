package de.db.shoppinglist.repository;

import de.db.shoppinglist.database.Source;
import de.db.shoppinglist.database.FirebaseSource;
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

//    public boolean modifyEntry(String uid, ShoppingEntry entry){
//        return db.modifyEntry(uid, entry);
//    }

    public boolean deleteEntry(String listUid, String documentUid){
        return db.deleteEntry(listUid, documentUid);
    }

    public boolean addList(ShoppingList shoppingList){
        return db.addList(shoppingList);
    }

    public void modifyList(){

    }

    public boolean deleteList(String listId){
        return db.deleteList(listId);
    }
}
