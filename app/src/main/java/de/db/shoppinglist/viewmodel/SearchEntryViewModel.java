package de.db.shoppinglist.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import de.db.shoppinglist.model.EntryHistoryElement;
import de.db.shoppinglist.model.ShoppingEntry;
import de.db.shoppinglist.repository.ShoppingRepository;

public class SearchEntryViewModel extends ViewModel {

    private ShoppingRepository repo = ShoppingRepository.getInstance();

    public LiveData<List<EntryHistoryElement>> getHistory() {
        return repo.getHistory();
    }

    public void deleteList(EntryHistoryElement historyEntry) {
        repo.deleteHistoryEntry(historyEntry);
    }
}
