package de.db.shoppinglist.database;

import android.util.Log;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import de.db.shoppinglist.model.ShoppingEntry;
import de.db.shoppinglist.model.ShoppingList;

public class FirebaseSource implements Source {

    private final String listsRootKey = "Lists";
    private final String entriesKey = "Entries";
    private final String firebaseTag = "FIREBASE";
    private final CollectionReference rootCollectionRef = FirebaseFirestore.getInstance().collection(listsRootKey);


    @Override
    public boolean addEntry(String listUid, ShoppingEntry newEntry) {
        AtomicBoolean wasSuccess = new AtomicBoolean(false);
        DocumentReference newEntryRef = rootCollectionRef.document(listUid).collection(entriesKey).document(newEntry.getUid());
        newEntryRef.set(newEntry).addOnSuccessListener(aVoid -> {
            Log.d("FIREBASE", "Success: Added Entry");
            wasSuccess.set(true);
        })
                .addOnFailureListener(e -> {
                    Log.d("FIREBASE", e.getMessage());
                });;
        return wasSuccess.get();
    }

    @Override
    public boolean deleteEntry(String listUid, String documentUid) {
        AtomicBoolean wasSuccess = new AtomicBoolean(false);
        DocumentReference entryRef = rootCollectionRef.document(listUid).collection("Entries").document(documentUid);
        entryRef.delete().addOnSuccessListener(aVoid -> {
            Log.d("FIREBASE", "Success: Deleted Entry");
            wasSuccess.set(true);
        })
        .addOnFailureListener(e -> {
            Log.d("FIREBASE", e.getMessage());
        });
        return wasSuccess.get();
    }

    @Override
    public boolean addList(ShoppingList shoppingList) {
        AtomicBoolean wasSuccess = new AtomicBoolean(false);
        rootCollectionRef.document(shoppingList.getUid()).set(shoppingList)
                .addOnSuccessListener(aVoid -> {
                    Log.d("FIREBASE", "Success: Added List");
                    wasSuccess.set(true);
                })
                .addOnFailureListener(e -> {
                    Log.d("FIREBASE", e.getMessage());
                });;
        return wasSuccess.get();
    }

    @Override
    public boolean modifyList() {
        AtomicBoolean wasSuccess = new AtomicBoolean(false);
        return wasSuccess.get();
    }

    @Override
    public boolean deleteList(String listId) {
        boolean wasDeletingEntriesSuccess = deleteEntries(listId);
        boolean wasDeletingListSuccess = deleteListOnly(listId);
        return wasDeletingEntriesSuccess && wasDeletingListSuccess;
    }

    @Override
    public FirestoreRecyclerOptions<ShoppingEntry> getShoppingListRecyclerViewOptions(String listId) {
        Query query = rootCollectionRef.document(listId).collection(entriesKey).orderBy("position");
        return new FirestoreRecyclerOptions.Builder<ShoppingEntry>()
                .setQuery(query, ShoppingEntry.class)
                .build();
    }

    @Override
    public FirestoreRecyclerOptions<ShoppingList> getShoppingListsRecyclerViewOptions() {
        Query query = rootCollectionRef;
        return new FirestoreRecyclerOptions.Builder<ShoppingList>()
                .setQuery(query, ShoppingList.class)
                .build();
    }

    @Override
    public void updateEntryPosition(ShoppingList list, ShoppingEntry entry, int position) {
        Map<String, Object> updatePosition = new HashMap<>();
        updatePosition.put("position", position);
        rootCollectionRef.document(list.getUid()).collection(entriesKey).document(entry.getUid()).update(updatePosition);
    }

    private boolean deleteEntries(String listId) {
        //From https://firebase.google.com/docs/firestore/manage-data/delete-data#collections
        // Deleting collections from an Android client is not recommended.
        return true;
    }

    private boolean deleteListOnly(String listId) {
        AtomicBoolean wasSuccess = new AtomicBoolean(true);
        DocumentReference entryRef = rootCollectionRef.document(listId);
        entryRef.delete().addOnSuccessListener(aVoid -> {
            Log.d("FIREBASE", "Success: Deleted List");

        }).addOnFailureListener(e -> {
            Log.d("FIREBASE", e.getMessage());
            wasSuccess.set(false);
        });
        return wasSuccess.get();
    }
}
