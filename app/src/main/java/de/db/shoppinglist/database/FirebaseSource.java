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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static java.util.stream.Collectors.toList;

import de.db.shoppinglist.model.EntryHistoryElement;
import de.db.shoppinglist.model.ShoppingEntry;
import de.db.shoppinglist.model.ShoppingList;

public class FirebaseSource implements Source {

    public static final String FIREBASE_TAG = "FIREBASE";
    private final String listsRootKey = "Lists";
    private final String entriesKey = "Entries";
    private final String historyKey = "History";
    private final String firebaseTag = FIREBASE_TAG;
    private final CollectionReference listsRootCollectionRef = FirebaseFirestore.getInstance().collection(listsRootKey);
    private final CollectionReference historyRootCollectionRef = FirebaseFirestore.getInstance().collection(historyKey);


    @Override
    public void addEntry(String listId, ShoppingEntry newEntry) {
        DocumentReference newEntryRef = listsRootCollectionRef.document(listId).collection(entriesKey).document(newEntry.getUid());
        newEntryRef.set(newEntry)
                .addOnSuccessListener(aVoid -> {
                    updateListStatusCounter(listId);
                    addToHistory(newEntry);
                    Log.d(FIREBASE_TAG, "Success: Added Entry");
                })
                .addOnFailureListener(e -> {
                    Log.d(FIREBASE_TAG, e.getMessage());
                });
        Map<String, Object> updateNextFreePosition = new HashMap<>();
        updateNextFreePosition.put("nextFreePosition", newEntry.getPosition());
        listsRootCollectionRef.document(listId).update(updateNextFreePosition)
                .addOnSuccessListener(aVoid -> {
                    updateListStatusCounter(listId);
                    Log.d(FIREBASE_TAG, "Success: Updated nextFreePosition");
                })
                .addOnFailureListener(e -> {
                    Log.d(FIREBASE_TAG, e.getMessage());
                });
    }

    private void addToHistory(ShoppingEntry newEntry) {
        EntryHistoryElement historyElement = newEntry.extractHistoryElement();
        historyRootCollectionRef.add(historyElement)
                .addOnSuccessListener(aVoid -> {
                    Log.d(FIREBASE_TAG, "Success: Added to History");
                })
                .addOnFailureListener(e -> {
                    Log.d(FIREBASE_TAG, e.getMessage());
                });

    }

    @Override
    public void deleteEntry(String listId, String documentUid) {
        DocumentReference entryRef = listsRootCollectionRef.document(listId).collection("Entries").document(documentUid);
        entryRef.delete().addOnSuccessListener(aVoid -> {
            updateListStatusCounter(listId);
            Log.d(FIREBASE_TAG, "Success: Deleted Entry");
        })
        .addOnFailureListener(e -> {
            Log.d(FIREBASE_TAG, e.getMessage());
        });
    }

    private void updateListStatusCounter(String listId){
        Task<QuerySnapshot> querySnapshotTask = listsRootCollectionRef.document(listId).collection(entriesKey).get();
        querySnapshotTask.addOnSuccessListener(queryDocumentSnapshots -> {
            long done = queryDocumentSnapshots.getDocuments().stream().filter(doc -> (Boolean) doc.get("done")).count();
            long total = queryDocumentSnapshots.getDocuments().size();
            Map<String, Object> counterVars = new HashMap<>();
            counterVars.put("done", done);
            counterVars.put("total", total);
            listsRootCollectionRef.document(listId).update(counterVars)
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
        listsRootCollectionRef.document(shoppingList.getUid()).set(shoppingList)
                .addOnSuccessListener(aVoid -> {
                    Log.d(FIREBASE_TAG, "Success: Added List");
                })
                .addOnFailureListener(e -> {
                    Log.d(FIREBASE_TAG, e.getMessage());
                });
    }


    @Override
    public void deleteList(String listId) {
        Task<QuerySnapshot> query = listsRootCollectionRef.document(listId).collection(entriesKey).get();
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
        Query query = listsRootCollectionRef.document(listId).collection(entriesKey).orderBy("position");
        return new FirestoreRecyclerOptions.Builder<ShoppingEntry>()
                .setQuery(query, ShoppingEntry.class)
                .build();
    }

    @Override
    public FirestoreRecyclerOptions<ShoppingList> getShoppingListsRecyclerViewOptions() {
        Query lists = listsRootCollectionRef.orderBy("name");
        return new FirestoreRecyclerOptions.Builder<ShoppingList>()
                .setQuery(lists, ShoppingList.class)
                .build();
    }

    @Override
    public void updateEntryPosition(ShoppingList list, ShoppingEntry entry, int position) {
        Map<String, Object> updatePosition = new HashMap<>();
        updatePosition.put("position", position);
        listsRootCollectionRef.document(list.getUid()).collection(entriesKey).document(entry.getUid()).update(updatePosition);
    }

    @Override
    public void updateStatusDone(String listId, ShoppingEntry entry) {
        Map<String, Object> updateIsDone = new HashMap<>();
        updateIsDone.put("done", entry.isDone());
        listsRootCollectionRef.document(listId).collection(entriesKey).document(entry.getUid())
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
        listsRootCollectionRef.document(list.getUid()).update(updateName)
                .addOnSuccessListener(aVoid -> {
                    Log.d(FIREBASE_TAG, "Success: Updated Name");
                })
                .addOnFailureListener(e -> {
                    Log.d(FIREBASE_TAG, e.getMessage());
                });
    }

    @Override
    public void modifyWholeEntry(ShoppingList list, ShoppingEntry entry) {
        Map<String, Object> updateEntryMap = new HashMap<>();
        updateEntryMap.put("name", entry.getName());
        updateEntryMap.put("done", entry.isDone());
        updateEntryMap.put("details", entry.getDetails());
        updateEntryMap.put("position", entry.getPosition());
        updateEntryMap.put("quantity", entry.getQuantity());
        updateEntryMap.put("unitOfQuantity", entry.getUnitOfQuantity());
        listsRootCollectionRef.document(list.getUid()).collection(entriesKey).document(entry.getUid()).update(updateEntryMap)
                .addOnSuccessListener(aVoid -> {
                    addToHistory(entry);
                    Log.d(FIREBASE_TAG, "Success: Updated Entry");

                }).addOnFailureListener(e -> {
            Log.d(FIREBASE_TAG, e.getMessage());
        });
        updateListStatusCounter(list.getUid());
    }

    @Override
    public void getHistory(Consumer<List<EntryHistoryElement>> callback) {
        Task<QuerySnapshot> querySnapshotTask = historyRootCollectionRef.get();
        querySnapshotTask.addOnSuccessListener(snapshots -> {
            List<EntryHistoryElement> history = new ArrayList<>();
            List<EntryHistoryElement> collectedHistory = snapshots.getDocuments()
                    .stream()
                    .map(this::makeHistoryElement)
                    .collect(toList());
            history.addAll(collectedHistory);
            callback.accept(history);
            Log.d(FIREBASE_TAG, "Success: Retrieved history");
        }).addOnFailureListener(e -> {
            Log.d(FIREBASE_TAG, e.getMessage());
        });
    }

    private EntryHistoryElement makeHistoryElement(DocumentSnapshot doc) {
        return new EntryHistoryElement((String) doc.get("name"), (String) doc.get("unitOfQuantity"), (String) doc.get("details"));
    }

    private DocumentReference buildPath(String listId, DocumentSnapshot doc) {
        return listsRootCollectionRef.document(listId).collection(entriesKey).document(doc.getId());
    }

    private void deleteListOnly(String listId) {
        DocumentReference entryRef = listsRootCollectionRef.document(listId);
        entryRef.delete().addOnSuccessListener(aVoid -> {
            Log.d(FIREBASE_TAG, "Success: Deleted List");

        }).addOnFailureListener(e -> {
            Log.d(FIREBASE_TAG, e.getMessage());
        });
    }
}
