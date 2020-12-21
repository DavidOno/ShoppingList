package de.db.shoppinglist.viewmodel;

import androidx.lifecycle.ViewModel;

import de.db.shoppinglist.model.ShoppingEntry;
import de.db.shoppinglist.model.ShoppingList;
import de.db.shoppinglist.repository.ShoppingRepository;

public class ModifyEntryViewModel extends ViewModel {

    private ShoppingRepository repo = ShoppingRepository.getInstance();

    public boolean deleteEntry(String listUid, String documentUid){
        return repo.deleteEntry(listUid, documentUid);
    }

    public void addEntry(){

    }

    public boolean modifyEntry(ShoppingList list, ShoppingEntry entry){
        repo.deleteEntry(list.getUid(), entry.getUid());
        return repo.addEntry(list.getUid(), entry);
    }
}
