package de.db.shoppinglist.viewmodel;

import androidx.lifecycle.ViewModel;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import de.db.shoppinglist.model.ShoppingList;
import de.db.shoppinglist.repository.ShoppingRepository;
/**
 * This is the viewmodel for the {@link de.db.shoppinglist.view.SelectShoppingListModificationFragment}.
 */
public class SelectListModificationViewModel extends ViewModel {

    private ShoppingRepository repo = ShoppingRepository.getInstance();

    public void deleteList(ShoppingList list) {
        repo.deleteList(list.getUid());
    }

    /**
     * Build the FirestoreRecyclerOptions, used in {@link com.firebase.ui.firestore.FirestoreRecyclerAdapter}.
     * Due to this options the FirestoreRecyclerAdapter knows which shopping-lists to display.
     *
     * @return Returns the options, containing a query, which data should be displayed.
     */
    public FirestoreRecyclerOptions<ShoppingList> getRecyclerViewOptions() {
        return repo.getShoppingListsRecyclerViewOptions();
    }
}
