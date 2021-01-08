package de.db.shoppinglist.viewmodel;

import androidx.lifecycle.ViewModel;

import de.db.shoppinglist.model.ShoppingList;
import de.db.shoppinglist.repository.ShoppingRepository;
/**
 * This is the viewmodel for the {@link de.db.shoppinglist.view.ShareDialog}.
 */
public class ShareListViewModel extends ViewModel {

    private ShoppingRepository repo = ShoppingRepository.getInstance();

    /**
     * Enables sharing of a single shopping-list.
     *
     * @param list  List to share.
     * @param email Email to identify the receiving party.
     */
    public void share(ShoppingList list, String email) {
        repo.share(list, email);
    }
}
