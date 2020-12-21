package de.db.shoppinglist.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import de.db.shoppinglist.database.Source;
import de.db.shoppinglist.database.FirebaseSource;
import de.db.shoppinglist.model.ShoppingElement;
import de.db.shoppinglist.model.ShoppingList;

public class ShoppingRepository {

    private static ShoppingRepository instance;
    private Source db = new FirebaseSource();

    /**
     * Ensures that all viewmodels retrieve their information from the same source
     * @return Returns an instance of this singleton.
     */
    public static ShoppingRepository getInstance(){
        if(instance == null){
            instance = new ShoppingRepository();
        }
        return instance;
    }

    public void addEntry(){

    }

    public void modifyEntry(){

    }

    public void deleteEntry(ShoppingElement item){
        db.deleteEntry(item);
    }

    public void addList(){

    }

    public void modifyList(){

    }

    public void deleteList(){

    }

    public LiveData<ShoppingList> getShoppingList(String name){
        final MutableLiveData<ShoppingList> shoppingData = new MutableLiveData<>();
        shoppingData.setValue(db.getShoppingList(name));
        return null; //TODO
    }


}
