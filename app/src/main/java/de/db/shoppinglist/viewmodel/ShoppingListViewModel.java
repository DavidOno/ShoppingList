package de.db.shoppinglist.viewmodel;

import androidx.lifecycle.ViewModel;

import de.db.shoppinglist.model.ShoppingElement;
import de.db.shoppinglist.model.ShoppingEntry;
import de.db.shoppinglist.model.ShoppingList;
import de.db.shoppinglist.repository.ShoppingRepository;

public class ShoppingListViewModel extends ViewModel {

    private ShoppingRepository repo = ShoppingRepository.getInstance();

    public boolean deleteEntry(ShoppingList list, Object item) {
        if(!(item instanceof ShoppingEntry)){
            throw new IllegalArgumentException("Only items of type ShoppingEntry can be deleted.");
        }
        ShoppingEntry shoppingEntry = (ShoppingEntry) item;
        return repo.deleteEntry(list.getUid(), shoppingEntry.getUid());
    }
}
