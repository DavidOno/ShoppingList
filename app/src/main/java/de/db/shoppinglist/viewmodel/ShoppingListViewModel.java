package de.db.shoppinglist.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import de.db.shoppinglist.model.ShoppingList;

public class ShoppingListViewModel extends ViewModel {

    private MutableLiveData<ShoppingList> shoppingList;

    public LiveData<ShoppingList> getShoppingList(){
        return shoppingList;
    }
}
