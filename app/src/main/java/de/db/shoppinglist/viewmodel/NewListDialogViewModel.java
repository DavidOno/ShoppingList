package de.db.shoppinglist.viewmodel;

import androidx.lifecycle.ViewModel;

import de.db.shoppinglist.model.ShoppingList;
import de.db.shoppinglist.repository.ShoppingRepository;

public class NewListDialogViewModel extends ViewModel {

    private ShoppingRepository repo = ShoppingRepository.getInstance();

    public void addList(ShoppingList shoppingList) {
        repo.addList(shoppingList);
    }
}
