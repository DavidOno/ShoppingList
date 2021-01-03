package de.db.shoppinglist.viewmodel;

import android.content.Context;

import androidx.lifecycle.ViewModel;

import de.db.shoppinglist.model.ShoppingEntry;
import de.db.shoppinglist.model.ShoppingList;
import de.db.shoppinglist.repository.ShoppingRepository;

public class ModifyEntryViewModel extends ViewModel {

    private ShoppingRepository repo = ShoppingRepository.getInstance();

    public void deleteEntry(String listUid, String documentUid){
        repo.deleteEntry(listUid, documentUid);
    }

    public void toggleDoneStatus(ShoppingList list, ShoppingEntry entry) {
        entry.setDone(!entry.isDone());
        repo.updateDoneStatus(list.getUid(), entry);
    }

    public void modifyEntry(ShoppingList list, ShoppingEntry entry, String imageUri, Context context){
        repo.modifyWholeEntry(list, entry, imageUri, context);
    }

    public void modifyImageOfEntry(ShoppingList list, ShoppingEntry entry, String imageUri, Context context) {
        repo.modifyImageOfEntry(list, entry, imageUri, context);
    }
}
