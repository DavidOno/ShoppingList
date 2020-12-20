package de.db.shoppinglist.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import de.db.shoppinglist.database.Database;
import de.db.shoppinglist.model.ShoppingList;

public class ShoppingRepository {

    private static ShoppingRepository instance;
    private Database db;

    public static ShoppingRepository getInstance(){
        if(instance == null){
            instance = new ShoppingRepository();
        }
        return instance;
    }

    public void setDatabase(Database db){
        this.db = db;
    }

    public LiveData<ShoppingList> getShoppingList(String name){
        final MutableLiveData<ShoppingList> shoppingData = new MutableLiveData<>();
        shoppingData.setValue(db.getShoppingList(name));
        return null; //TODO
    }


}
