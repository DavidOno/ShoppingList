package de.db.shoppinglist.database;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

import de.db.shoppinglist.model.ShoppingEntry;
import de.db.shoppinglist.model.ShoppingList;

import static de.db.shoppinglist.database.FirebaseSource.*;

public class GoogleSharer implements Sharer {

    public static final String EMAIL_PROPERTY = "email";
    private static final String SHARER_TAG = "GoogleSharer";

    @Override
    public void share(ShoppingList list, String email) {
        Task<QuerySnapshot> userIdByEmail = findUserIdByEmail(email);
        userIdByEmail.addOnSuccessListener(documentSnapshots -> {
            String userIdOfReceiver = getUserId(documentSnapshots);
            Task<Void> addList = addListToUser(list, userIdOfReceiver);
            addList.addOnSuccessListener(aVoid -> copyDocuments(list, userIdOfReceiver));
        }).addOnFailureListener(e -> Log.d(SHARER_TAG, Objects.requireNonNull(e.getMessage())));
    }

    private String getUserId(QuerySnapshot documentSnapshots) {
        DocumentSnapshot documentSnapshot = documentSnapshots.getDocuments().get(0);
        return documentSnapshot.getId();
    }

    private String getUserIdOfSender(){
        return FirebaseAuth.getInstance().getUid();
    }

    private void copyDocuments(ShoppingList list, String userIdOfReceiver) {
        getListsRootCollectionRef(getUserIdOfSender()).document(list.getUid()).collection(ENTRIES_KEY).get()
        .addOnSuccessListener(documentSnapshots ->
                documentSnapshots.getDocuments().stream()
                        .map(doc -> doc.toObject(ShoppingEntry.class))
                        .forEach(shoppingEntry -> addEntryToUser(list, shoppingEntry, userIdOfReceiver))
        ).addOnFailureListener(e -> Log.d(SHARER_TAG, Objects.requireNonNull(e.getMessage())));
    }

    private void addEntryToUser(ShoppingList list, ShoppingEntry shoppingEntry, String userId) {
        DocumentReference newEntryRef = getListsRootCollectionRef(userId).document(list.getUid()).collection(ENTRIES_KEY).document(shoppingEntry.getUid());
        newEntryRef.set(shoppingEntry).addOnSuccessListener(aVoid -> {
            Log.d(SHARER_TAG, "Copied doc to user");
        });
    }

    private Task<Void> addListToUser(ShoppingList list, String userId) {
        return getListsRootCollectionRef(userId).document(list.getUid()).set(list);
    }

    private CollectionReference getListsRootCollectionRef(String userId){
        return FirebaseFirestore.getInstance().collection(USER_ROOT_KEY).document(userId).collection(LISTS_ROOT_KEY);
    }

    private Task<QuerySnapshot> findUserIdByEmail(String email) {
        CollectionReference usersCollection = FirebaseFirestore.getInstance().collection(USERS_KEY);
        return usersCollection.whereEqualTo(EMAIL_PROPERTY, email).get();
    }
}
