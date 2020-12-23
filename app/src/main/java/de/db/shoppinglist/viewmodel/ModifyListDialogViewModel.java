package de.db.shoppinglist.viewmodel;

import androidx.lifecycle.ViewModel;

import de.db.shoppinglist.model.ShoppingList;
import de.db.shoppinglist.repository.ShoppingRepository;

public class ModifyListDialogViewModel extends ViewModel {

    private ShoppingRepository repo = ShoppingRepository.getInstance();

    public void updateListName(ShoppingList list) {
        repo.updateListName(list);
    }
}
