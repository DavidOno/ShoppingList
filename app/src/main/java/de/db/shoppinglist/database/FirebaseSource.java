package de.db.shoppinglist.database;

import android.util.Log;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

import de.db.shoppinglist.model.ShoppingEntry;
import de.db.shoppinglist.model.ShoppingList;

public class FirebaseSource implements Source {

    public static final String FIREBASE_TAG = "FIREBASE";
    private final String listsRootKey = "Lists";
    private final String entriesKey = "Entries";
    private final String firebaseTag = FIREBASE_TAG;
    private final CollectionReference rootCollectionRef = FirebaseFirestore.getInstance().collection(listsRootKey);


    @Override
    public void addEntry(String listId, ShoppingEntry newEntry) {
        DocumentReference newEntryRef = rootCollectionRef.document(listId).collection(entriesKey).document(newEntry.getUid());
        newEntryRef.set(newEntry).addOnSuccessListener(aVoid -> {
            updateListStatusCounter(listId);
            Log.d(FIREBASE_TAG, "Success: Added Entry");
        })
                .addOnFailureListener(e -> {
                    Log.d(FIREBASE_TAG, e.getMessage());
                });
    }

    @Override
    public void deleteEntry(String listId, String documentUid) {
        DocumentReference entryRef = rootCollectionRef.document(listId).collection("Entries").document(documentUid);
        entryRef.delete().addOnSuccessListener(aVoid -> {
            updateListStatusCounter(listId);
            Log.d(FIREBASE_TAG, "Success: Deleted Entry");
        })
        .addOnFailureListener(e -> {
            Log.d(FIREBASE_TAG, e.getMessage());
        });
    }

    private void updateListStatusCounter(String listId){
        Task<QuerySnapshot> querySnapshotTask = rootCollectionRef.document(listId).collection(entriesKey).get();
        querySnapshotTask.addOnSuccessListener(queryDocumentSnapshots -> {
            long done = queryDocumentSnapshots.getDocuments().stream().filter(doc -> (Boolean) doc.get("done")).count();
            long total = queryDocumentSnapshots.getDocuments().size();
            Map<String, Object> counterVars = new HashMap<>();
            counterVars.put("done", done);
            counterVars.put("total", total);
            rootCollectionRef.document(listId).update(counterVars)
                    .addOnSuccessListener(aVoid -> {
                        Log.d(FIREBASE_TAG, "Success: " + done+"/"+total);
                    })
                    .addOnFailureListener(e -> {
                        Log.d(FIREBASE_TAG, e.getMessage());
                    });
        });
    }

    @Override
    public void addList(ShoppingList shoppingList) {
        rootCollectionRef.document(shoppingList.getUid()).set(shoppingList)
                .addOnSuccessListener(aVoid -> {
                    Log.d(FIREBASE_TAG, "Success: Added List");
                })
                .addOnFailureListener(e -> {
                    Log.d(FIREBASE_TAG, e.getMessage());
                });
    }


    @Override
    public void deleteList(String listId) {
        Task<QuerySnapshot> query = rootCollectionRef.document(listId).collection(entriesKey).get();
        query.addOnSuccessListener(aVoid -> {
            query.getResult().getDocuments().stream()
                    .map(doc -> buildPath(listId, doc))
                    .forEach(DocumentReference::delete);
            Log.d(FIREBASE_TAG, "Success: Deleted all entries");
            deleteListOnly(listId);
        });
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
        Query lists = rootCollectionRef.orderBy("name");
        return new FirestoreRecyclerOptions.Builder<ShoppingList>()
                .setQuery(lists, ShoppingList.class)
                .build();
    }

    @Override
    public void updateEntryPosition(ShoppingList list, ShoppingEntry entry, int position) {
        Map<String, Object> updatePosition = new HashMap<>();
        updatePosition.put("position", position);
        rootCollectionRef.document(list.getUid()).collection(entriesKey).document(entry.getUid()).update(updatePosition);
    }

    @Override
    public void updateStatusDone(String listId, ShoppingEntry entry) {
        Map<String, Object> updateIsDone = new HashMap<>();
        updateIsDone.put("done", entry.isDone());
        rootCollectionRef.document(listId).collection(entriesKey).document(entry.getUid())
                .update(updateIsDone)
                .addOnSuccessListener(aVoid -> {
                    updateListStatusCounter(listId);
                    Log.d(FIREBASE_TAG, "Success: Updated Status");
                })
                .addOnFailureListener(e -> {
                    Log.d(FIREBASE_TAG, e.getMessage());
                });
    }

    @Override
    public void updateListName(ShoppingList list) {
        Map<String, Object> updateName = new HashMap<>();
        updateName.put("name", list.getName());
        rootCollectionRef.document(list.getUid()).update(updateName)
                .addOnSuccessListener(aVoid -> {
                    Log.d(FIREBASE_TAG, "Success: Updated Name");
                })
                .addOnFailureListener(e -> {
                    Log.d(FIREBASE_TAG, e.getMessage());
                });
    }

    @Override
    public void modifyWholeEntry(ShoppingList list, ShoppingEntry entry) {
        Log.d(FIREBASE_TAG, "Starting modifying entry:");
        deleteEntry(list.getUid(), entry.getUid());
        addEntry(list.getUid(), entry);
        Log.d(FIREBASE_TAG, "Ended modifying entry");
    }

    private DocumentReference buildPath(String listId, DocumentSnapshot doc) {
        return rootCollectionRef.document(listId).collection(entriesKey).document(doc.getId());
    }

    private void deleteListOnly(String listId) {
        DocumentReference entryRef = rootCollectionRef.document(listId);
        entryRef.delete().addOnSuccessListener(aVoid -> {
            Log.d(FIREBASE_TAG, "Success: Deleted List");

        }).addOnFailureListener(e -> {
            Log.d(FIREBASE_TAG, e.getMessage());
        });
    }
}
