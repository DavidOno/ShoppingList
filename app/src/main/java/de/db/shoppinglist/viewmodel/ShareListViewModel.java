package de.db.shoppinglist.viewmodel;

import androidx.lifecycle.ViewModel;

import de.db.shoppinglist.model.ShoppingList;
import de.db.shoppinglist.repository.ShoppingRepository;

public class ShareListViewModel extends ViewModel {

    private ShoppingRepository repo = ShoppingRepository.getInstance();

    public void share(ShoppingList list, String email) {
        repo.share(list, email);
    }
}
