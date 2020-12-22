package de.db.shoppinglist.viewmodel;

import androidx.lifecycle.ViewModel;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

import de.db.shoppinglist.model.ShoppingElement;
import de.db.shoppinglist.model.ShoppingEntry;
import de.db.shoppinglist.model.ShoppingList;
import de.db.shoppinglist.repository.ShoppingRepository;

public class ShoppingListViewModel extends ViewModel {

    private ShoppingRepository repo = ShoppingRepository.getInstance();

    public boolean deleteEntry(ShoppingList list, Object item) {
        if(!(item instanceof ShoppingEntry)){
            throw new IllegalArgumentException("Only items of type ShoppingEntry can be deleted.");
        }
        ShoppingEntry shoppingEntry = (ShoppingEntry) item;
        return repo.deleteEntry(list.getUid(), shoppingEntry.getUid());
    }

    public FirestoreRecyclerOptions<ShoppingEntry> getRecylerViewOptions(ShoppingList list) {
        return repo.getRecyclerViewOptions(list.getUid());
    }

    public void updatePosition(ShoppingList list, ShoppingEntry entry, int position) {
        repo.updateEntryPosition(list, entry, position);
    }

    public void toggleDoneStatus(ShoppingList list, ShoppingEntry entry) {
        entry.setDone(!entry.isDone());
        repo.updateDoneStatus(list.getUid(), entry);
    }

    public void setStatusToDone(ShoppingList list, ShoppingEntry entry) {
        entry.setDone(true);
        repo.updateDoneStatus(list.getUid(), entry);
    }
}
