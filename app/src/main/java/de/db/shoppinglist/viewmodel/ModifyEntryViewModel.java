package de.db.shoppinglist.viewmodel;

import androidx.lifecycle.ViewModel;

import de.db.shoppinglist.model.ShoppingElement;
import de.db.shoppinglist.repository.ShoppingRepository;

public class ModifyEntryViewModel extends ViewModel {

    private ShoppingRepository repo;

    public void init(){
        repo = ShoppingRepository.getInstance();
    }

    public void deleteEntry(){
        repo.deleteEntry((ShoppingElement) item);
    }

    public void addEntry(){

    }

    public void modifyEntry(){

    }
}
