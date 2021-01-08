package de.db.shoppinglist.viewmodel;

import androidx.lifecycle.ViewModel;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import de.db.shoppinglist.model.ShoppingEntry;
import de.db.shoppinglist.model.ShoppingList;
import de.db.shoppinglist.repository.ShoppingRepository;

/**
 * This is the viewmodel for the {@link de.db.shoppinglist.view.ShoppingListFragment}.
 */
public class ShoppingListViewModel extends ViewModel {

    private ShoppingRepository repo = ShoppingRepository.getInstance();

    /**
     * Deletes an entry from a specific list.
     *
     * @param list List, containing this entry.
     * @param item Item, which should be deleted. Object should be instance of ShoppingEntry.
     */
    public void deleteEntry(ShoppingList list, Object item) {
        if (!(item instanceof ShoppingEntry)) {
            throw new IllegalArgumentException("Only items of type ShoppingEntry can be deleted.");
        }
        ShoppingEntry shoppingEntry = (ShoppingEntry) item;
        repo.deleteEntry(list.getUid(), shoppingEntry.getUid());
    }

    /**
     * Build the FirestoreRecyclerOptions, used in {@link com.firebase.ui.firestore.FirestoreRecyclerAdapter}.
     * Due to this options the FirestoreRecyclerAdapter knows which entries of a list to display.
     *
     * @param list Llist, form which the entries are supposed to be displayed.
     * @return Returns the options, containing a query, which data should be displayed.
     */
    public FirestoreRecyclerOptions<ShoppingEntry> getRecylerViewOptions(ShoppingList list) {
        return repo.getRecyclerViewOptions(list.getUid());
    }

    /**
     * Updates the position of an entry within a shopping-list.
     *
     * @param list     The shopping-list, were the element is part of.
     * @param entry    The entry, where the position is supposed to be updated.
     * @param position The new position.-
     */
    public void updatePosition(ShoppingList list, ShoppingEntry entry, int position) {
        repo.updateEntryPosition(list, entry, position);
    }

    /**
     * Updates if the entry is done or not.
     *
     * @param list  List containing the entry.
     * @param entry The entry, with the new done-status.
     */
    public void toggleDoneStatus(ShoppingList list, ShoppingEntry entry) {
        entry.setDone(!entry.isDone());
        repo.updateDoneStatus(list.getUid(), entry);
    }

    /**
     * Updates the done status to true (= done).
     *
     * @param list List containing the entry.
     * @param entry  The entry, with the new done-status.
     */
    public void setStatusToDone(ShoppingList list, ShoppingEntry entry) {
        entry.setDone(true);
        repo.updateDoneStatus(list.getUid(), entry);
    }
}
