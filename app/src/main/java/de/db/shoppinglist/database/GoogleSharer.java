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
import de.db.shoppinglist.utility.ToastUtility;

import static de.db.shoppinglist.database.FirebaseSource.*;

/**
 * This class allows sharing of data between users, based on their google-mail-address.
 */
public class GoogleSharer implements Sharer {

    /** Contant, representing the email-property of the user-metadata*/
    public static final String EMAIL_PROPERTY = "email";
    private static ToastUtility toastMaker = ToastUtility.getInstance();
    private static final String SHARER_TAG = "GoogleSharer";

    /**
     * Enables sharing between two users.
     * All data is simply copied to the receiving user-directory.
     * The copy is independent of the original source.
     * It's not possible to share with yourself.
     *
     * @param list  List to share.
     * @param email Email to identify the receiving party.
     */
    @Override
    public void share(ShoppingList list, String email) {
        boolean different = checkIfSourceAndDestinationAreDifferent(email);
        if(!different){
            return;
        }
        Task<QuerySnapshot> userIdByEmail = findUserIdByEmail(email);
        userIdByEmail.addOnSuccessListener(documentSnapshots -> {
            String userIdOfReceiver = getUserId(documentSnapshots);
            Task<Void> addList = addListToUser(list, userIdOfReceiver);
            addList.addOnSuccessListener(aVoid -> copyDocuments(list, userIdOfReceiver));
        }).addOnFailureListener(e -> Log.d(SHARER_TAG, Objects.requireNonNull(e.getMessage())));
    }

    private boolean checkIfSourceAndDestinationAreDifferent(String email) {
        String emailCurrentUser = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        if(areSameGmailAddress(emailCurrentUser, email)){
            toastMaker.prepareToast("You cannot share with yourself.");
            return false;
        }
        return true;
    }

    private boolean areSameGmailAddress(String emailCurrentUser, String email) {
        int endNameCurrentUser = emailCurrentUser.indexOf("@");
        int endName = email.indexOf("@");
        String currentUserEmailName = emailCurrentUser.substring(0, endNameCurrentUser);
        String emailName = email.substring(0, endName);
        return currentUserEmailName.equals(emailName);
    }

    private String getUserId(QuerySnapshot documentSnapshots) {
        DocumentSnapshot documentSnapshot = documentSnapshots.getDocuments().get(0);
        return documentSnapshot.getId();
    }

    private String getUserIdOfSender() {
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

    private CollectionReference getListsRootCollectionRef(String userId) {
        return FirebaseFirestore.getInstance().collection(USER_ROOT_KEY).document(userId).collection(LISTS_ROOT_KEY);
    }

    private Task<QuerySnapshot> findUserIdByEmail(String email) {
        CollectionReference usersCollection = FirebaseFirestore.getInstance().collection(USERS_KEY);
        return usersCollection.whereEqualTo(EMAIL_PROPERTY, email).get();
    }
}
