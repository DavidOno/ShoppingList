package de.db.shoppinglist.viewmodel;

import androidx.lifecycle.ViewModel;

import de.db.shoppinglist.model.ShoppingList;
import de.db.shoppinglist.repository.ShoppingRepository;
/**
 * This is the viewmodel for the {@link de.db.shoppinglist.view.NewListDialog}.
 */
public class NewListDialogViewModel extends ViewModel {

    private ShoppingRepository repo = ShoppingRepository.getInstance();

    /**
     * Adds a new shopping-list to database.
     *
     * @param shoppingList The new shopping-list.
     */
    public void addList(ShoppingList shoppingList) {
        repo.addList(shoppingList);
    }
}
