package de.db.shoppinglist.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import de.db.shoppinglist.model.ShoppingElement;
import de.db.shoppinglist.model.ShoppingList;
import de.db.shoppinglist.repository.ShoppingRepository;

public class ShoppingListViewModel extends ViewModel {

    private ShoppingRepository repo;

    public void init(){
        repo = ShoppingRepository.getInstance();
    }

    public void deleteEntry(Object item) {
        if(!(item instanceof ShoppingElement)){
            throw new IllegalArgumentException("Only items of type ShoppingElements can be deleted.");
        }
        repo.deleteEntry((ShoppingElement)item);
    }
}
