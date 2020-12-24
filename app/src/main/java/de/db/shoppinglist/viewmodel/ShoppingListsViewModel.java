package de.db.shoppinglist.viewmodel;

import androidx.lifecycle.ViewModel;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import de.db.shoppinglist.model.ShoppingList;
import de.db.shoppinglist.repository.ShoppingRepository;

public class ShoppingListsViewModel extends ViewModel {

    private ShoppingRepository repo = ShoppingRepository.getInstance();

    public boolean deleteList(ShoppingList list) {
        return repo.deleteList(list.getUid());
    }

    public FirestoreRecyclerOptions<ShoppingList> getRecyclerViewOptions() {
        return repo.getShoppingListsRecyclerViewOptions();
    }
}
