package de.db.shoppinglist.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import de.db.shoppinglist.model.EntryHistoryElement;
import de.db.shoppinglist.repository.ShoppingRepository;
/**
 * This is the viewmodel for the {@link de.db.shoppinglist.view.SearchEntryFragment}.
 */
public class SearchEntryViewModel extends ViewModel {

    private ShoppingRepository repo = ShoppingRepository.getInstance();

    public LiveData<List<EntryHistoryElement>> getHistory() {
        return repo.getHistory();
    }

    /**
     * Deletes a specific history entry.
     *
     * @param historyEntry The history entry, which should be deleted.
     */
    public void deleteHistoryEntry(EntryHistoryElement historyEntry) {
        repo.deleteHistoryEntry(historyEntry);
    }
}
