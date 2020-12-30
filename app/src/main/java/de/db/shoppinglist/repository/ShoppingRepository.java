package de.db.shoppinglist.repository;

import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.util.ArrayList;

import java.util.List;
import java.util.function.Consumer;

import de.db.shoppinglist.database.FirebaseSource;
import de.db.shoppinglist.database.Source;
import de.db.shoppinglist.model.EntryHistoryElement;
import de.db.shoppinglist.model.ShoppingEntry;
import de.db.shoppinglist.model.ShoppingList;

import static java.util.Collections.emptyList;

public class ShoppingRepository {

    private static ShoppingRepository instance;
    private Source db = new FirebaseSource();

    /**
     * Ensures that all viewmodels retrieve their information from the same source.
     * @return Returns instance of this singleton.
     */
    public static ShoppingRepository getInstance(){
        if(instance == null){
            instance = new ShoppingRepository();
        }
        return instance;
    }

    public void addEntry(String listId, ShoppingEntry newEntry, Uri uploadImageUri){
        Runnable run = () -> {
            db.addEntry(listId, newEntry, uploadImageUri);
        };
        startThread(run);
    }

    public void deleteEntry(String listUid, String documentUid){
        Runnable run = () -> {
            db.deleteEntry(listUid, documentUid);
        };
        startThread(run);
    }

    public void addList(ShoppingList shoppingList){
        Runnable run = () -> {
            db.addList(shoppingList);
        };
        startThread(run);
    }

    public void deleteList(String listId){
        Runnable run = () -> {
            db.deleteList(listId);
        };
        startThread(run);
    }

    public FirestoreRecyclerOptions<ShoppingEntry> getRecyclerViewOptions(String listId) {
        return db.getShoppingListRecyclerViewOptions(listId);
    }

    public FirestoreRecyclerOptions<ShoppingList> getShoppingListsRecyclerViewOptions() {
        return db.getShoppingListsRecyclerViewOptions();
    }

    public void updateEntryPosition(ShoppingList list, ShoppingEntry entry, int position) {
        Runnable run = () -> {
            db.updateEntryPosition(list, entry, position);
        };
        startThread(run);
    }

    public void updateDoneStatus(String listId, ShoppingEntry entry) {
        Runnable run = () -> {
            db.updateStatusDone(listId, entry);
        };
        startThread(run);
    }

    public void updateListName(ShoppingList list) {
        Runnable run = () -> {
            db.updateListName(list);
        };
        startThread(run);
    }

    public void modifyWholeEntry(ShoppingList list, ShoppingEntry entry) {
        Runnable run = () -> {
            db.modifyWholeEntry(list, entry);
        };
        startThread(run);
    }

    public LiveData<List<EntryHistoryElement>> getHistory() {
        final List<EntryHistoryElement> result = new ArrayList<>();
        MutableLiveData<List<EntryHistoryElement>> liveResult = new MutableLiveData<>(emptyList());
        Consumer<List<EntryHistoryElement>> history = list -> {
            result.addAll(new ArrayList<>(list));
            liveResult.setValue(result);
            Log.d("REPOSITORY", result.toString());
        };
        db.getHistory(history);
        return liveResult;
    }

    public void deleteHistory(){
        Runnable run = () -> db.deleteHistory();
        startThread(run);
    }

    private void startThread(Runnable run){
        Thread thread = new Thread(run);
        thread.start();
    }

    public void deleteAllLists() {
        Runnable run = () -> db.deleteAllLists();
        startThread(run);
    }

    private void uploadImage(String listName, String entryName, Uri imageUri){
        Runnable run = () -> db.uploadImage(listName, entryName, imageUri);
        startThread(run);
    }
}
