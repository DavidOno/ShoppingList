package de.db.shoppinglist.viewmodel;

import androidx.lifecycle.ViewModel;

import de.db.shoppinglist.model.ShoppingEntry;
import de.db.shoppinglist.model.ShoppingList;
import de.db.shoppinglist.repository.ShoppingRepository;

public class ShoppingListsViewModel extends ViewModel {

    private ShoppingRepository repo = ShoppingRepository.getInstance();

    public boolean deleteList(ShoppingList list) {
        return repo.deleteList(list.getUid());
    }

}
