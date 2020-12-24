package de.db.shoppinglist.viewmodel;

import androidx.lifecycle.ViewModel;

import de.db.shoppinglist.model.ShoppingEntry;
import de.db.shoppinglist.model.ShoppingList;
import de.db.shoppinglist.repository.ShoppingRepository;

public class NewEntryViewModel extends ViewModel {

    private ShoppingRepository repo = ShoppingRepository.getInstance();

    public void addNewEntry(ShoppingList list, float quantity, String unitOfQuantity, String nameOfProduct, String details) {
        int position = list.getNextFreePosition();
        ShoppingEntry shoppingEntry = new ShoppingEntry(quantity, unitOfQuantity, nameOfProduct, details, position);
        repo.addEntry(list.getUid(), shoppingEntry);
    }
}
