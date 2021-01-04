package de.db.shoppinglist.database;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

import de.db.shoppinglist.model.EntryHistoryElement;
import de.db.shoppinglist.model.ShoppingEntry;
import de.db.shoppinglist.model.ShoppingList;
import de.db.shoppinglist.model.UserInfo;
import de.db.shoppinglist.utility.ToastUtility;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

public class FirebaseSource implements Source {


    private final ToastUtility toastMaker = ToastUtility.getInstance();
    private static final String FIREBASE_TAG = "FIREBASE";
    public static final String USER_ROOT_KEY = "Users";
    public static final String LISTS_ROOT_KEY = "Lists";
    public static final String ENTRIES_KEY = "Entries";
    public static final String HISTORY_KEY = "History";
    public static final String TOTAL_PROPERTY = "total";
    public static final String POSITION_PROPERTY = "position";
    public static final String NAME_PROPERTY = "name";
    public static final String DONE_PROPERTY = "done";
    public static final String DETAILS_PROPERTY = "details";
    public static final String QUANTITY_PROPERTY = "quantity";
    public static final String UNIT_OF_QUANTITY_PROPERTY = "unitOfQuantity";
    public static final String NEXT_FREE_POSITION_PROPERTY = "nextFreePosition";
    public static final String IMAGE_STORAGE_KEY = "uploads";
    public static final String IMAGE_URI_PROPERTY = "imageURI";
    public static final String HIST_UID_PROPERTY = "uid";
    public static final String USERS_KEY = "User";



    private CollectionReference getListsRootCollectionRef(){
        String uid = getUserId();
        return FirebaseFirestore.getInstance().collection(USER_ROOT_KEY).document(uid).collection(LISTS_ROOT_KEY);
    }

    private String getUserId() {
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) {
            Log.d(FIREBASE_TAG, "FirebaseAuth returned null for userId");
        }
        return uid;
    }

    private CollectionReference getHistoryRootCollectionRef(){
        String uid = getUserId();
        return FirebaseFirestore.getInstance().collection(USER_ROOT_KEY).document(uid).collection(HISTORY_KEY);
    }

    @Override
    public void addEntry(String listId, ShoppingEntry newEntry, Uri uploadImageURI, Context context) {
        DocumentReference newEntryRef = getListsRootCollectionRef().document(listId).collection(ENTRIES_KEY).document(newEntry.getUid());
        newEntryRef.set(newEntry)
                .addOnSuccessListener(aVoid -> {
                    updateListStatusCounter(listId);
                    if(uploadImageURI != null){
                        uploadImage(listId, newEntry, uploadImageURI, context);
                    }else{
                        addToHistory(newEntry);
                    }
                    Log.d(FIREBASE_TAG, "Success: Added Entry");
                })
                .addOnFailureListener(e -> {
                            Log.d(FIREBASE_TAG, Objects.requireNonNull(e.getMessage()));
                            toastMaker.prepareToast("Fail: Add new Entry");
                        }
                );
        updateListInformation(listId, newEntry);
    }

    private void updateListInformation(String listId, ShoppingEntry newEntry) {
        Map<String, Object> updateNextFreePosition = new HashMap<>();
        updateNextFreePosition.put(NEXT_FREE_POSITION_PROPERTY, newEntry.getPosition());
        getListsRootCollectionRef().document(listId).update(updateNextFreePosition)
                .addOnSuccessListener(aVoid -> {
                    updateListStatusCounter(listId);
                    Log.d(FIREBASE_TAG, "Success: Updated nextFreePosition");
                })
                .addOnFailureListener(e -> {
                            Log.d(FIREBASE_TAG, Objects.requireNonNull(e.getMessage()));
                            toastMaker.prepareToast("Fail: Updated \"next free position\"");
                        }
                );
    }

    private void addToHistory(ShoppingEntry newEntry) {
        getHistory().addOnSuccessListener(snapshots -> {
            Set<EntryHistoryElement> collectedHistory = collectHistoryAsSet(snapshots);
            boolean alreadyContained = collectedHistory.contains(newEntry.extractHistoryElement());
            if(!alreadyContained){
                addNewElementToHistory(newEntry);
            }
        });
    }

    private void addNewElementToHistory(ShoppingEntry newEntry) {
        EntryHistoryElement historyElement = newEntry.extractHistoryElement();
        getHistoryRootCollectionRef().document(historyElement.getUid()).set(historyElement)
                .addOnSuccessListener(aVoid ->
                    Log.d(FIREBASE_TAG, "Success: Added to History")
                )
                .addOnFailureListener(e -> {
                    Log.d(FIREBASE_TAG, Objects.requireNonNull(e.getMessage()));
                    toastMaker.prepareToast("Fail: Add To History");
                });
    }

    private Set<EntryHistoryElement> collectHistoryAsSet(QuerySnapshot snapshots) {
        return snapshots.getDocuments()
                        .stream()
                        .map(this::makeHistoryElement)
                        .collect(toSet());
    }

    @Override
    public void deleteEntry(String listId, String documentUid) {
        DocumentReference entryRef = getListsRootCollectionRef().document(listId).collection(ENTRIES_KEY).document(documentUid);
        entryRef.delete()
                .addOnSuccessListener(aVoid -> {
                    updateListStatusCounter(listId);
                    Log.d(FIREBASE_TAG, "Success: Deleted Entry");
                })
                .addOnFailureListener(e -> {
                            Log.d(FIREBASE_TAG, Objects.requireNonNull(e.getMessage()));
                            toastMaker.prepareToast("Fail: Delete Entry");
                        }
                );
    }

    private void updateListStatusCounter(String listId){
        Task<QuerySnapshot> querySnapshotTask = getListsRootCollectionRef().document(listId).collection(ENTRIES_KEY).get();
        querySnapshotTask.addOnSuccessListener(queryDocumentSnapshots -> {
            long done = queryDocumentSnapshots.getDocuments().stream().filter(doc -> (Boolean) doc.get(FirebaseSource.DONE_PROPERTY)).count();
            long total = queryDocumentSnapshots.getDocuments().size();
            Map<String, Object> counterVars = new HashMap<>();
            counterVars.put(DONE_PROPERTY, done);
            counterVars.put(TOTAL_PROPERTY, total);
            getListsRootCollectionRef().document(listId).update(counterVars)
                    .addOnSuccessListener(aVoid ->
                        Log.d(FIREBASE_TAG, "Success: " + done+"/"+total)
                    )
                    .addOnFailureListener(e -> {
                            Log.d(FIREBASE_TAG, Objects.requireNonNull(e.getMessage()));
                            toastMaker.prepareToast("Fail: Update List Counter");
                        }
                    );
        });
    }

    @Override
    public void addList(ShoppingList shoppingList) {
        getListsRootCollectionRef().document(shoppingList.getUid()).set(shoppingList)
                .addOnSuccessListener(aVoid ->
                    Log.d(FIREBASE_TAG, "Success: Added List")
                )
                .addOnFailureListener(e -> {
                        Log.d(FIREBASE_TAG, Objects.requireNonNull(e.getMessage()));
                        toastMaker.prepareToast("Fail: Add List");
                    }
                );
    }


    @Override
    public void deleteList(String listId) {
        Task<QuerySnapshot> query = getListsRootCollectionRef().document(listId).collection(ENTRIES_KEY).get();
        query.addOnSuccessListener(aVoid -> {
            Objects.requireNonNull(query.getResult()).getDocuments().stream()
                    .map(doc -> buildPathForEntryDoc(listId, doc))
                    .forEach(DocumentReference::delete);
            Log.d(FIREBASE_TAG, "Success: Deleted all entries");
            deleteListOnly(listId);
        }).addOnFailureListener(e -> {
                    Log.d(FIREBASE_TAG, Objects.requireNonNull(e.getMessage()));
                    toastMaker.prepareToast("Fail: Delete List");
                }
        );
    }

    @Override
    public FirestoreRecyclerOptions<ShoppingEntry> getShoppingListRecyclerViewOptions(String listId) {
        Query query = getListsRootCollectionRef().document(listId).collection(ENTRIES_KEY).orderBy(POSITION_PROPERTY);
        return new FirestoreRecyclerOptions.Builder<ShoppingEntry>()
                .setQuery(query, ShoppingEntry.class)
                .build();
    }

    @Override
    public FirestoreRecyclerOptions<ShoppingList> getShoppingListsRecyclerViewOptions() {
        Query lists = getListsRootCollectionRef().orderBy(NAME_PROPERTY);
        return new FirestoreRecyclerOptions.Builder<ShoppingList>()
                .setQuery(lists, ShoppingList.class)
                .build();
    }

    @Override
    public void updateEntryPosition(ShoppingList list, ShoppingEntry entry, int position) {
        Map<String, Object> updatePosition = new HashMap<>();
        updatePosition.put(POSITION_PROPERTY, position);
        getListsRootCollectionRef().document(list.getUid()).collection(ENTRIES_KEY).document(entry.getUid()).update(updatePosition);
    }

    @Override
    public void updateStatusDone(String listId, ShoppingEntry entry) {
        Map<String, Object> updateIsDone = new HashMap<>();
        updateIsDone.put(DONE_PROPERTY, entry.isDone());
        getListsRootCollectionRef().document(listId).collection(ENTRIES_KEY).document(entry.getUid())
                .update(updateIsDone)
                .addOnSuccessListener(aVoid -> {
                    updateListStatusCounter(listId);
                    Log.d(FIREBASE_TAG, "Success: Updated Status");
                })
                .addOnFailureListener(e -> {
                            Log.d(FIREBASE_TAG, Objects.requireNonNull(e.getMessage()));
                            toastMaker.prepareToast("Fail: Update Status \"Done\"");
                        }
                );
    }

    @Override
    public void updateListName(ShoppingList list) {
        Map<String, Object> updateName = new HashMap<>();
        updateName.put(NAME_PROPERTY, list.getName());
        getListsRootCollectionRef().document(list.getUid()).update(updateName)
                .addOnSuccessListener(aVoid ->
                    Log.d(FIREBASE_TAG, "Success: Updated Name")
                )
                .addOnFailureListener(e -> {
                            Log.d(FIREBASE_TAG, Objects.requireNonNull(e.getMessage()));
                            toastMaker.prepareToast("Fail: Update List Name");
                        }
                );
    }

    @Override
    public void modifyWholeEntry(ShoppingList list, ShoppingEntry entry, String imageUri, Context context) {
        Map<String, Object> updateEntryMap = buildUpdateMap(entry);
        getListsRootCollectionRef().document(list.getUid()).collection(ENTRIES_KEY).document(entry.getUid()).update(updateEntryMap)
                .addOnSuccessListener(aVoid -> {
                    if(imageUri != null){
                        uploadImage(list.getUid(), entry, Uri.parse(imageUri), context);
                    }else {
                        addToHistory(entry);
                    }
                    Log.d(FIREBASE_TAG, "Success: Updated Entry");

                }).addOnFailureListener(e -> {
                    Log.d(FIREBASE_TAG, Objects.requireNonNull(e.getMessage()));
                    toastMaker.prepareToast("Fail: Modify Entry");
                }
        );
        updateListStatusCounter(list.getUid());
    }

    private Map<String, Object> buildUpdateMap(ShoppingEntry entry) {
        Map<String, Object> updateEntryMap = new HashMap<>();
        updateEntryMap.put(NAME_PROPERTY, entry.getName());
        updateEntryMap.put(FirebaseSource.DONE_PROPERTY, entry.isDone());
        updateEntryMap.put(DETAILS_PROPERTY, entry.getDetails());
        updateEntryMap.put(POSITION_PROPERTY, entry.getPosition());
        updateEntryMap.put(QUANTITY_PROPERTY, entry.getQuantity());
        updateEntryMap.put(UNIT_OF_QUANTITY_PROPERTY, entry.getUnitOfQuantity());
        return updateEntryMap;
    }

    @Override
    public void getHistory(Consumer<List<EntryHistoryElement>> callback) {
        Task<QuerySnapshot> querySnapshotTask = getHistoryRootCollectionRef().get();
        querySnapshotTask.addOnSuccessListener(snapshots -> {
            List<EntryHistoryElement> collectedHistory = collectHistoryAsList(snapshots);
            callback.accept(collectedHistory);
            Log.d(FIREBASE_TAG, "Success: Retrieved history");
        }).addOnFailureListener(e -> {
                    Log.d(FIREBASE_TAG, Objects.requireNonNull(e.getMessage()));
                    toastMaker.prepareToast("Fail: Retrieve History");
                }
        );
    }

    private List<EntryHistoryElement> collectHistoryAsList(QuerySnapshot snapshots) {
        return snapshots.getDocuments()
                        .stream()
                        .map(this::makeHistoryElement)
                        .collect(toList());
    }


    private Task<QuerySnapshot> getHistory() {
        return getHistoryRootCollectionRef().get();
    }

    private EntryHistoryElement makeHistoryElement(DocumentSnapshot doc) {
        return new EntryHistoryElement((String) doc.get(NAME_PROPERTY), (String) doc.get(UNIT_OF_QUANTITY_PROPERTY), 
                (String) doc.get(DETAILS_PROPERTY), (String)doc.get(IMAGE_URI_PROPERTY),
                (String)doc.get(HIST_UID_PROPERTY));
    }

    private DocumentReference buildPathForEntryDoc(String listId, DocumentSnapshot doc) {
        return getHistoryRootCollectionRef().document(listId).collection(ENTRIES_KEY).document(doc.getId());
    }


    private DocumentReference buildPathForHistoryDoc(DocumentSnapshot doc) {
        return getHistoryRootCollectionRef().document(doc.getId());
    }



    private void deleteListOnly(String listId) {
        DocumentReference entryRef = getListsRootCollectionRef().document(listId);
        entryRef.delete().addOnSuccessListener(aVoid ->
            Log.d(FIREBASE_TAG, "Success: Deleted List")

        ).addOnFailureListener(e -> {
                    Log.d(FIREBASE_TAG, Objects.requireNonNull(e.getMessage()));
                    toastMaker.prepareToast("Fail: Delete List");
                }
        );
    }

    @Override
    public void deleteHistory(){
        getHistoryRootCollectionRef().get().addOnSuccessListener(documentSnapshots -> {
            documentSnapshots.getDocuments().stream()
                    .map(this::buildPathForHistoryDoc)
                    .forEach(DocumentReference::delete);
            Log.d(FIREBASE_TAG, "Success: Deleted History");
        }).addOnFailureListener(e -> {
                    Log.d(FIREBASE_TAG, Objects.requireNonNull(e.getMessage()));
                    toastMaker.prepareToast("Fail: Delete History");
                }
        );
    }

    @Override
    public void deleteAllLists() {
        Task<QuerySnapshot> querySnapshotTask = getListsRootCollectionRef().get();
        querySnapshotTask.addOnSuccessListener(aVoid -> {
            querySnapshotTask.getResult().getDocuments().stream()
                    .map(DocumentSnapshot::getId)
                    .forEach(this::deleteList);
            Log.d(FIREBASE_TAG, "Success: Deleted All Lists");
        }).addOnFailureListener(e -> {
                    Log.d(FIREBASE_TAG, Objects.requireNonNull(e.getMessage()));
                    toastMaker.prepareToast("Fail: Delete all List");
                }
        );
    }

    private StorageReference buildStorageReference(){
       return FirebaseStorage.getInstance().getReference(IMAGE_STORAGE_KEY+"/"+UUID.randomUUID());
    }

    private void updateImage(String listName, ShoppingEntry entry, String imageURI){
        Map<String, Object> updateImageMap = new HashMap<>();
        updateImageMap.put(IMAGE_URI_PROPERTY, imageURI);
        long start = System.currentTimeMillis();
        getListsRootCollectionRef().document(listName).collection(ENTRIES_KEY).document(entry.getUid()).update(updateImageMap).addOnSuccessListener(aVoid -> {
                Log.d(FIREBASE_TAG, "Success: Updated Image");
        toastMaker.prepareToast(String.valueOf((System.currentTimeMillis() - start)));
        ShoppingEntry entryWithImage = new ShoppingEntry(entry);
        entryWithImage.setImageURI(imageURI);
        addToHistory(entryWithImage);
    }
        ).addOnFailureListener(e -> {
                    Log.d(FIREBASE_TAG, Objects.requireNonNull(e.getMessage()));
                    toastMaker.prepareToast("Fail: Update Image");
                }
        );
    }

    /**
     * Uploads Image to Firebase Storage.
     * Afterwards an update of the specific ShoppingEntry is triggered.
     * Tries to compress images if possible.
     * @param listName Name of the shopping list
     * @param entry Entry which
     * @param imageURI Uri of the image
     * @param context Context of the image-uri
     */
    @Override
    public void uploadImage(String listName, ShoppingEntry entry, Uri imageURI, Context context) {
        final StorageReference image = buildStorageReference();
        byte[] compressedImageBytes = new ImageCompressor(context).compress(imageURI, 30);
        UploadTask uploadTask;
        if(isCompressed(compressedImageBytes)) {
            uploadTask = image.putBytes(compressedImageBytes);
        }else{
            uploadTask = image.putFile(imageURI);
        }
        reactToResultOfUpload(uploadTask, image, listName, entry);
    }

    private boolean isCompressed(byte[] compressedImageBytes) {
        return compressedImageBytes != null;
    }

    private void reactToResultOfUpload(UploadTask uploadTask, StorageReference image, String listName, ShoppingEntry entry){
        uploadTask.addOnSuccessListener(taskSnapshot -> image.getDownloadUrl()
                .addOnSuccessListener(imageURI1 -> {
                    updateImage(listName, entry, imageURI1.toString());
                }))
                .addOnFailureListener(e -> {
                            Log.d(FIREBASE_TAG, Objects.requireNonNull(e.getMessage()));
                            toastMaker.prepareToast("Fail: Upload compressed Image");
                        }
                );
    }


    @Override
    public void signOut(GoogleSignInClient googleSignInClient) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signOut();
        googleSignInClient.signOut().addOnCompleteListener(task -> Log.d(FIREBASE_TAG, "Completely logged out"));
    }

    @Override
    public void signIn(String idToken, Runnable postSignInAction) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(FIREBASE_TAG, "signInWithCredential:success");
                        addToUsers();
                        postSignInAction.run();
                    } else {
                        Log.w(FIREBASE_TAG, "signInWithCredential:failure", task.getException());
                    }
                });
    }

    private void addToUsers() {
        UserInfo userInfo =  new UserInfo(FirebaseAuth.getInstance());
        FirebaseFirestore.getInstance().collection(USERS_KEY).document(getUserId()).set(userInfo);
    }

    @Override
    public void deleteHistoryEntry(EntryHistoryElement historyEntry) {
        String historyId = historyEntry.getUid();
        getHistoryRootCollectionRef().document(historyId).delete()
        .addOnSuccessListener(aVoid -> {
            Log.d(FIREBASE_TAG, "Success: Deleted history-entry");
        }).addOnFailureListener(e -> {
                    Log.d(FIREBASE_TAG, Objects.requireNonNull(e.getMessage()));
                    toastMaker.prepareToast("Fail: Delete List");
                }
        );
    }
}
