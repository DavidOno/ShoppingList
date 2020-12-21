package de.db.shoppinglist.database;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import de.db.shoppinglist.model.ShoppingElement;
import de.db.shoppinglist.model.ShoppingList;

public class FirebaseSource implements Source {

    private static FirebaseSource instance;
    private final String origin = "ORIGIN";
    private final String entries_key = "ENTRIES";


    @Override
    public ShoppingList getShoppingList(String name) {
        return null;
    }

    @Override
    public void addEntry() {

    }

    @Override
    public void modifyEntry() {

    }

    @Override
    public void deleteEntry(ShoppingElement item) {
//        DocumentReference oldEntryRef = FirebaseFirestore.getInstance().collection(origin).document(listName).collection("Entries").document(oldDocumentId);
//        oldEntryRef.delete();
    }

    @Override
    public void addList() {

    }

    @Override
    public void modifyList() {

    }

    @Override
    public void deleteList() {

    }
}
