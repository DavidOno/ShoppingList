package de.db.shoppinglist.viewmodel;

import androidx.lifecycle.ViewModel;

import java.util.List;

import de.db.shoppinglist.model.ShoppingEntry;
import de.db.shoppinglist.repository.ShoppingRepository;

public class SearchEntryViewModel extends ViewModel {

    private ShoppingRepository repo = ShoppingRepository.getInstance();

    public List<ShoppingEntry> getHistory() {
        return repo.getHistory();
    }
}
