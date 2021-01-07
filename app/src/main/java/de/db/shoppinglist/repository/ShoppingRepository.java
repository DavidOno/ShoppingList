package de.db.shoppinglist.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import de.db.shoppinglist.database.FirebaseSource;
import de.db.shoppinglist.database.GoogleLogin;
import de.db.shoppinglist.database.GoogleSharer;
import de.db.shoppinglist.database.Login;
import de.db.shoppinglist.database.Sharer;
import de.db.shoppinglist.database.Source;
import de.db.shoppinglist.model.EntryHistoryElement;
import de.db.shoppinglist.model.ShoppingEntry;
import de.db.shoppinglist.model.ShoppingList;

import static java.util.Collections.emptyList;

/**
 * This class represents the repository within the the MVVM-design.
 * Every task, which requires the database is executed in an own thread.
 */
public class ShoppingRepository {

    private static ShoppingRepository instance;
    private Source db = new FirebaseSource();
    private Sharer sharer = new GoogleSharer();
    private Login login = new GoogleLogin();

    /**
     * Ensures that all viewmodels retrieve their information from the same source.
     *
     * @return Returns instance of this singleton.
     */
    public static ShoppingRepository getInstance() {
        if (instance == null) {
            instance = new ShoppingRepository();
        }
        return instance;
    }

    /**
     * Adds an entry to a specific list.
     *
     * @param listId The list-id, to which this entry should be added.
     * @param newEntry   The new entry, which should be added.
     * @param context The application context.
     */
    public void addEntry(String listId, ShoppingEntry newEntry, Context context) {
        Runnable run = () -> db.addEntry(listId, newEntry, context);
        startThread(run);
    }

    /**
     * Deletes an entry from a specific list.
     *
     * @param listUid     Id of the list, containg this entry.
     * @param documentUid Id of the entry, which should be deleted.
     */
    public void deleteEntry(String listUid, String documentUid) {
        Runnable run = () -> db.deleteEntry(listUid, documentUid);
        startThread(run);
    }

    /**
     * Adds a new shopping-list to database.
     *
     * @param shoppingList The new shopping-list.
     */
    public void addList(ShoppingList shoppingList) {
        Runnable run = () -> db.addList(shoppingList);
        startThread(run);
    }

    /**
     * Deletes a list from database.
     *
     * @param listId Id of the list, which is supposed to be deleted.
     */
    public void deleteList(String listId) {
        Runnable run = () -> db.deleteList(listId);
        startThread(run);
    }

    /**
     * Build the FirestoreRecyclerOptions, used in {@link com.firebase.ui.firestore.FirestoreRecyclerAdapter}.
     * Due to this options the FirestoreRecyclerAdapter knows which entries of a list to display.
     *
     * @param listId Id of the list, form which the entries are supposed to be displayed.
     * @return Returns the options, containing a query, which data should be displayed.
     */
    public FirestoreRecyclerOptions<ShoppingEntry> getRecyclerViewOptions(String listId) {
        return db.getShoppingListRecyclerViewOptions(listId);
    }

    /**
     * Build the FirestoreRecyclerOptions, used in {@link com.firebase.ui.firestore.FirestoreRecyclerAdapter}.
     * Due to this options the FirestoreRecyclerAdapter knows which shopping-lists to display.
     *
     * @return Returns the options, containing a query, which data should be displayed.
     */
    public FirestoreRecyclerOptions<ShoppingList> getShoppingListsRecyclerViewOptions() {
        return db.getShoppingListsRecyclerViewOptions();
    }

    /**
     * Updates the position of an entry within a shopping-list.
     *
     * @param list     The shopping-list, were the element is part of.
     * @param entry    The entry, where the position is supposed to be updated.
     * @param position The new position.-
     */
    public void updateEntryPosition(ShoppingList list, ShoppingEntry entry, int position) {
        Runnable run = () -> db.updateEntryPosition(list, entry, position);
        startThread(run);
    }

    /**
     * Updates if the entry is done or not.
     *
     * @param listId Id of the list containing the entry.
     * @param entry  The entry, with the new done-status.
     */
    public void updateDoneStatus(String listId, ShoppingEntry entry) {
        Runnable run = () -> db.updateStatusDone(listId, entry);
        startThread(run);
    }

    /**
     * Updates the name of a list.
     *
     * @param list The list, containing the new name.
     */
    public void updateListName(ShoppingList list) {
        Runnable run = () -> db.updateListName(list);
        startThread(run);
    }

    /**
     * Updates the complete entry.
     *
     * @param list    List containing the entry, which is supposed to be updated.
     * @param entry   The modified entry.
     * @param context The application context.
     */
    public void modifyWholeEntry(ShoppingList list, ShoppingEntry entry, Context context) {
        Runnable run = () -> db.modifyWholeEntry(list, entry, context);
        startThread(run);
    }

    /**
     * Retrieves the complete history from firebase.
     * @return Returns LiveData representing the whole history.
     */
    public LiveData<List<EntryHistoryElement>> getHistory() {
        final List<EntryHistoryElement> result = new ArrayList<>();
        MutableLiveData<List<EntryHistoryElement>> liveResult = new MutableLiveData<>(emptyList());
        Consumer<List<EntryHistoryElement>> history = list -> {
            result.addAll(new ArrayList<>(list));
            liveResult.setValue(result);
        };
        db.getHistory(history);
        return liveResult;
    }

    /**
     * Deletes complete history.
     */
    public void deleteHistory() {
        Runnable run = () -> db.deleteHistory();
        startThread(run);
    }


    private void startThread(Runnable run) {
        Thread thread = new Thread(run);
        thread.start();
    }

    /**
     * Deletes all lists and the corresponding entries.
     */
    public void deleteAllLists() {
        Runnable run = () -> db.deleteAllLists();
        startThread(run);
    }

    /**
     * Allows a complete sign-out, using the {@link GoogleSignInClient}
     * Afterwards the user, if he tries to resign-in, will be able to choose which account,
     * he wants to use.
     *
     * @param googleSignInClient Client, which allows to sign-out.
     */
    public void signOut(GoogleSignInClient googleSignInClient) {
        Runnable run = () -> login.signOut(googleSignInClient);
        startThread(run);
    }

    /**
     * Allows to sign-in.
     *
     * @param idToken          Token for identification.
     * @param navigationToShoppingList Navigation, executed immediately after the sign-in
     */
    public void signInWithCredential(String idToken, Runnable navigationToShoppingList) {
        Runnable run = () -> login.signIn(idToken, navigationToShoppingList);
        startThread(run);
    }

    /**
     * Deletes a specific history entry.
     *
     * @param historyEntry The history entry, which should be deleted.
     */
    public void deleteHistoryEntry(EntryHistoryElement historyEntry) {
        Runnable run = () -> db.deleteHistoryEntry(historyEntry);
        startThread(run);
    }

    /**
     * Enables sharing of a single shopping-list.
     *
     * @param list  List to share.
     * @param email Email to identify the receiving party.
     */
    public void share(ShoppingList list, String email) {
        Runnable run = () -> sharer.share(list, email);
        startThread(run);
    }

    /**
     * Allows to change the underlying implementation of the database during runtime.
     * @param db An alternative implementation for the database.
     */
    public void setDb(Source db) {
        this.db = db;
    }

    /**
     * Allows to change the underlying implementation of the share-logic during runtime.
     * @param sharer An alternative implementation for the share-logic.
     */
    public void setSharer(Sharer sharer) {
        this.sharer = sharer;
    }

    /**
     * Allows to change the underlying implementation of the login-logic during runtime.
     * @param login An alternative implementation for the database.
     */
    public void setLogin(Login login) {
        this.login = login;
    }
}
