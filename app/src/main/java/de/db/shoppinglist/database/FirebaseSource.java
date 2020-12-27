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
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

import de.db.shoppinglist.model.EntryHistoryElement;
import de.db.shoppinglist.model.ShoppingEntry;
import de.db.shoppinglist.model.ShoppingList;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

public class FirebaseSource implements Source {

    private static final String FIREBASE_TAG = "FIREBASE";
    private static final String LISTS_ROOT_KEY = "Lists";
    private static final String ENTRIES_KEY = "Entries";
    private static final String HISTORY_KEY = "History";
    private static final String TOTAL_PROPERTY = "total";
    private static final String POSITION_PROPERTY = "position";
    private static final String NAME_PROPERTY = "name";
    private static final String DONE_PROPERTY = "done";
    private static final String DETAILS_PROPERTY = "details";
    private static final String QUANTITY_PROPERTY = "quantity";
    private static final String UNIT_OF_QUANTITY_PROPERTY = "unitOfQuantity";
    public static final String NEXT_FREE_POSITION_PROPERTY = "nextFreePosition";
    private final CollectionReference listsRootCollectionRef = FirebaseFirestore.getInstance().collection(LISTS_ROOT_KEY);
    private final CollectionReference historyRootCollectionRef = FirebaseFirestore.getInstance().collection(HISTORY_KEY);


    @Override
    public void addEntry(String listId, ShoppingEntry newEntry) {
        DocumentReference newEntryRef = listsRootCollectionRef.document(listId).collection(ENTRIES_KEY).document(newEntry.getUid());
        newEntryRef.set(newEntry)
                .addOnSuccessListener(aVoid -> {
                    updateListStatusCounter(listId);
                    addToHistory(newEntry);
                    Log.d(FIREBASE_TAG, "Success: Added Entry");
                })
                .addOnFailureListener(e ->
                    Log.d(FIREBASE_TAG, Objects.requireNonNull(e.getMessage()))
                );
        Map<String, Object> updateNextFreePosition = new HashMap<>();
        updateNextFreePosition.put(NEXT_FREE_POSITION_PROPERTY, newEntry.getPosition());
        listsRootCollectionRef.document(listId).update(updateNextFreePosition)
                .addOnSuccessListener(aVoid -> {
                    updateListStatusCounter(listId);
                    Log.d(FIREBASE_TAG, "Success: Updated nextFreePosition");
                })
                .addOnFailureListener(e ->
                    Log.d(FIREBASE_TAG, Objects.requireNonNull(e.getMessage()))
                );
    }

    private void addToHistory(ShoppingEntry newEntry) {
        getHistory().addOnSuccessListener(snapshots -> {
            Set<EntryHistoryElement> collectedHistory = snapshots.getDocuments()
                    .stream()
                    .map(this::makeHistoryElement)
                    .collect(toSet());
            boolean contains = collectedHistory.contains(newEntry.extractHistoryElement());
            if(!contains){
                EntryHistoryElement historyElement = newEntry.extractHistoryElement();
                historyRootCollectionRef.add(historyElement)
                        .addOnSuccessListener(aVoid ->
                            Log.d(FIREBASE_TAG, "Success: Added to History")
                        )
                        .addOnFailureListener(e ->
                            Log.d(FIREBASE_TAG, Objects.requireNonNull(e.getMessage()))
                        );
            }
        });

    }

    @Override
    public void deleteEntry(String listId, String documentUid) {
        DocumentReference entryRef = listsRootCollectionRef.document(listId).collection(ENTRIES_KEY).document(documentUid);
        entryRef.delete().addOnSuccessListener(aVoid -> {
            updateListStatusCounter(listId);
            Log.d(FIREBASE_TAG, "Success: Deleted Entry");
        })
        .addOnFailureListener(e ->
            Log.d(FIREBASE_TAG, Objects.requireNonNull(e.getMessage()))
        );
    }

    private void updateListStatusCounter(String listId){
        Task<QuerySnapshot> querySnapshotTask = listsRootCollectionRef.document(listId).collection(ENTRIES_KEY).get();
        querySnapshotTask.addOnSuccessListener(queryDocumentSnapshots -> {
            long done = queryDocumentSnapshots.getDocuments().stream().filter(doc -> (Boolean) doc.get(FirebaseSource.DONE_PROPERTY)).count();
            long total = queryDocumentSnapshots.getDocuments().size();
            Map<String, Object> counterVars = new HashMap<>();
            counterVars.put(DONE_PROPERTY, done);
            counterVars.put(TOTAL_PROPERTY, total);
            listsRootCollectionRef.document(listId).update(counterVars)
                    .addOnSuccessListener(aVoid ->
                        Log.d(FIREBASE_TAG, "Success: " + done+"/"+total)
                    )
                    .addOnFailureListener(e ->
                        Log.d(FIREBASE_TAG, Objects.requireNonNull(e.getMessage()))
                    );
        });
    }

    @Override
    public void addList(ShoppingList shoppingList) {
        listsRootCollectionRef.document(shoppingList.getUid()).set(shoppingList)
                .addOnSuccessListener(aVoid ->
                    Log.d(FIREBASE_TAG, "Success: Added List")
                )
                .addOnFailureListener(e ->
                    Log.d(FIREBASE_TAG, Objects.requireNonNull(e.getMessage()))
                );
    }


    @Override
    public void deleteList(String listId) {
        Task<QuerySnapshot> query = listsRootCollectionRef.document(listId).collection(ENTRIES_KEY).get();
        query.addOnSuccessListener(aVoid -> {
            Objects.requireNonNull(query.getResult()).getDocuments().stream()
                    .map(doc -> buildPathForShoppingDoc(listId, doc))
                    .forEach(DocumentReference::delete);
            Log.d(FIREBASE_TAG, "Success: Deleted all entries");
            deleteListOnly(listId);
        });
    }

    @Override
    public FirestoreRecyclerOptions<ShoppingEntry> getShoppingListRecyclerViewOptions(String listId) {
        Query query = listsRootCollectionRef.document(listId).collection(ENTRIES_KEY).orderBy(POSITION_PROPERTY);
        return new FirestoreRecyclerOptions.Builder<ShoppingEntry>()
                .setQuery(query, ShoppingEntry.class)
                .build();
    }

    @Override
    public FirestoreRecyclerOptions<ShoppingList> getShoppingListsRecyclerViewOptions() {
        Query lists = listsRootCollectionRef.orderBy(NAME_PROPERTY);
        return new FirestoreRecyclerOptions.Builder<ShoppingList>()
                .setQuery(lists, ShoppingList.class)
                .build();
    }

    @Override
    public void updateEntryPosition(ShoppingList list, ShoppingEntry entry, int position) {
        Map<String, Object> updatePosition = new HashMap<>();
        updatePosition.put(POSITION_PROPERTY, position);
        listsRootCollectionRef.document(list.getUid()).collection(ENTRIES_KEY).document(entry.getUid()).update(updatePosition);
    }

    @Override
    public void updateStatusDone(String listId, ShoppingEntry entry) {
        Map<String, Object> updateIsDone = new HashMap<>();
        updateIsDone.put(DONE_PROPERTY, entry.isDone());
        listsRootCollectionRef.document(listId).collection(ENTRIES_KEY).document(entry.getUid())
                .update(updateIsDone)
                .addOnSuccessListener(aVoid -> {
                    updateListStatusCounter(listId);
                    Log.d(FIREBASE_TAG, "Success: Updated Status");
                })
                .addOnFailureListener(e ->
                    Log.d(FIREBASE_TAG, Objects.requireNonNull(e.getMessage()))
                );
    }

    @Override
    public void updateListName(ShoppingList list) {
        Map<String, Object> updateName = new HashMap<>();
        updateName.put(NAME_PROPERTY, list.getName());
        listsRootCollectionRef.document(list.getUid()).update(updateName)
                .addOnSuccessListener(aVoid ->
                    Log.d(FIREBASE_TAG, "Success: Updated Name")
                )
                .addOnFailureListener(e ->
                    Log.d(FIREBASE_TAG, Objects.requireNonNull(e.getMessage()))
                );
    }

    @Override
    public void modifyWholeEntry(ShoppingList list, ShoppingEntry entry) {
        Map<String, Object> updateEntryMap = new HashMap<>();
        updateEntryMap.put(NAME_PROPERTY, entry.getName());
        updateEntryMap.put(FirebaseSource.DONE_PROPERTY, entry.isDone());
        updateEntryMap.put(DETAILS_PROPERTY, entry.getDetails());
        updateEntryMap.put(POSITION_PROPERTY, entry.getPosition());
        updateEntryMap.put(QUANTITY_PROPERTY, entry.getQuantity());
        updateEntryMap.put(UNIT_OF_QUANTITY_PROPERTY, entry.getUnitOfQuantity());
        listsRootCollectionRef.document(list.getUid()).collection(ENTRIES_KEY).document(entry.getUid()).update(updateEntryMap)
                .addOnSuccessListener(aVoid -> {
                    addToHistory(entry);
                    Log.d(FIREBASE_TAG, "Success: Updated Entry");

                }).addOnFailureListener(e ->
            Log.d(FIREBASE_TAG, Objects.requireNonNull(e.getMessage()))
        );
        updateListStatusCounter(list.getUid());
    }

    @Override
    public void getHistory(Consumer<List<EntryHistoryElement>> callback) {
        Task<QuerySnapshot> querySnapshotTask = historyRootCollectionRef.get();
        querySnapshotTask.addOnSuccessListener(snapshots -> {
            List<EntryHistoryElement> collectedHistory = snapshots.getDocuments()
                    .stream()
                    .map(this::makeHistoryElement)
                    .collect(toList());
            callback.accept(collectedHistory);
            Log.d(FIREBASE_TAG, "Success: Retrieved history");
        }).addOnFailureListener(e ->
            Log.d(FIREBASE_TAG, Objects.requireNonNull(e.getMessage()))
        );
    }


    private Task<QuerySnapshot> getHistory() {
        return historyRootCollectionRef.get();
    }

    private EntryHistoryElement makeHistoryElement(DocumentSnapshot doc) {
        return new EntryHistoryElement((String) doc.get(NAME_PROPERTY), (String) doc.get(UNIT_OF_QUANTITY_PROPERTY), (String) doc.get(DETAILS_PROPERTY));
    }

    private DocumentReference buildPathForShoppingDoc(String listId, DocumentSnapshot doc) {
        return listsRootCollectionRef.document(listId).collection(ENTRIES_KEY).document(doc.getId());
    }


    private DocumentReference buildPathForHistoryDoc(DocumentSnapshot doc) {
        return historyRootCollectionRef.document(doc.getId());
    }

    private void deleteListOnly(String listId) {
        DocumentReference entryRef = listsRootCollectionRef.document(listId);
        entryRef.delete().addOnSuccessListener(aVoid ->
            Log.d(FIREBASE_TAG, "Success: Deleted List")

        ).addOnFailureListener(e ->
            Log.d(FIREBASE_TAG, Objects.requireNonNull(e.getMessage()))
        );
    }

    public void deleteHistory(){
        historyRootCollectionRef.get().addOnSuccessListener(documentSnapshots -> {
            documentSnapshots.getDocuments().stream()
                    .map(this::buildPathForHistoryDoc)
                    .forEach(DocumentReference::delete);
            Log.d(FIREBASE_TAG, "Success: Deleted History");
        }).addOnFailureListener(e ->
                Log.d(FIREBASE_TAG, Objects.requireNonNull(e.getMessage()))
        );
    }
}
