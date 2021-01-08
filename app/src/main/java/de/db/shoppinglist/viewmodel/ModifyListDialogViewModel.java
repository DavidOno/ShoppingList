package de.db.shoppinglist.viewmodel;

import androidx.lifecycle.ViewModel;

import de.db.shoppinglist.model.ShoppingList;
import de.db.shoppinglist.repository.ShoppingRepository;
/**
 * This is the viewmodel for the {@link de.db.shoppinglist.view.ModifyListDialog}.
 */
public class ModifyListDialogViewModel extends ViewModel {

    private ShoppingRepository repo = ShoppingRepository.getInstance();

    /**
     * Updates the name of a list.
     *
     * @param list The list, containing the new name.
     */
    public void updateListName(ShoppingList list) {
        repo.updateListName(list);
    }
}
