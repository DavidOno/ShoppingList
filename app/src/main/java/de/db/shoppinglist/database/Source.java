package de.db.shoppinglist.database;

import android.content.Context;
import android.net.Uri;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;

import java.util.List;
import java.util.function.Consumer;

import de.db.shoppinglist.model.EntryHistoryElement;
import de.db.shoppinglist.model.ShoppingEntry;
import de.db.shoppinglist.model.ShoppingList;

public interface Source {

    void addEntry(String listUid, ShoppingEntry entry, Uri uploadImageUri, Context context);

    void deleteEntry(String listUid, String documentUid);

    void addList(ShoppingList shoppingList);

    void deleteList(String listId);

    FirestoreRecyclerOptions<ShoppingEntry> getShoppingListRecyclerViewOptions(String listId);

    FirestoreRecyclerOptions<ShoppingList> getShoppingListsRecyclerViewOptions();

    void updateEntryPosition(ShoppingList list, ShoppingEntry entry, int position);

    void updateStatusDone(String listId, ShoppingEntry entry);

    void updateListName(ShoppingList list);

    void modifyWholeEntry(ShoppingList list, ShoppingEntry entry, String imageUri, Context context);

    void getHistory(Consumer<List<EntryHistoryElement>> callback);

    void deleteHistory();

    void deleteAllLists();

    void uploadImage(String listName, ShoppingEntry entry, Uri imageURI, Context context);

    void signOut(GoogleSignInClient googleSignInClient);

    void signIn(String idToken, Runnable navigationToShoppingList);
}

